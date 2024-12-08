package com.fatihbilgin.palletmart.kulanicipanel

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.fatihbilgin.palletmart.R
import com.fatihbilgin.palletmart.adminpanel.kullaniciyonetim.User
import com.fatihbilgin.palletmart.databinding.FragmentUserInfoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

@Suppress("DEPRECATION")
class UserInfo : Fragment() {

    private lateinit var binding: FragmentUserInfoBinding
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserInfoBinding.inflate(inflater, container, false)

        // Kullanıcı bilgilerini Firebase Realtime Database'den çekme
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userRef = database.child(currentUser.uid)
            userRef.get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val user = snapshot.getValue(User::class.java)
                    if (user != null) {
                        binding.nameEditText.setText(user.name)
                        binding.emailEditText.setText(user.email)
                        binding.phoneEditText.setText(user.phone)
                    }
                }
            }
        }

        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_userInfo_to_kullaniciEkran)
        }

        binding.exituserButton.setOnClickListener {
            signOutUser()
        }

        binding.holdAccountButton.setOnClickListener {
            holdUserAccount()
        }

        binding.updateButton.setOnClickListener {
            updateUserInfo()
        }

        return binding.root
    }

    private fun updateUserInfo() {
        val firstName = binding.nameEditText.text.toString()
        val email = binding.emailEditText.text.toString()
        val phone = binding.phoneEditText.text.toString()
        val password = binding.passwordEditText.text.toString() // Yeni şifre alanı

        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(context, "Kullanıcı oturumu geçerli değil.", Toast.LENGTH_SHORT).show()
            return
        }

        val userRef = database.child(currentUser.uid)

        // Kullanıcı verilerini al ve sadece değişen alanları güncelle
        userRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val user = snapshot.getValue(User::class.java)
                if (user != null) {
                    val updates = mutableMapOf<String, Any>()
                    var requiresReLogin = false // Kullanıcının yeniden giriş yapmasını gerektiren durum kontrolü

                    if (user.name != firstName && firstName.isNotEmpty()) {
                        updates["name"] = firstName
                    }

                    if (user.email != email && email.isNotEmpty()) {
                        requiresReLogin = true
                        updateEmail(email)
                    }

                    if (user.phone != phone && phone.isNotEmpty()) {
                        updates["phone"] = phone
                    }

                    if (password.isNotEmpty()) {
                        requiresReLogin = true
                        updatePassword(password)
                    }

                    if (updates.isNotEmpty()) {
                        userRef.updateChildren(updates).addOnSuccessListener {
                            Toast.makeText(context, "Bilgiler başarıyla güncellendi.", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener { e ->
                            Toast.makeText(context, "Hata: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    // Kullanıcı yeniden giriş yapması gerekiyorsa
                    if (requiresReLogin) {
                        Toast.makeText(context, "Bilgiler güncellendi. Lütfen tekrar giriş yapın.", Toast.LENGTH_LONG).show()
                        signOutUser()
                    }
                }
            }
        }.addOnFailureListener { e ->
            Toast.makeText(context, "Veri alınırken hata oluştu: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updatePassword(newPassword: String) {
        val currentUser = auth.currentUser

        if (currentUser == null) {
            Toast.makeText(context, "Kullanıcı oturumu geçerli değil.", Toast.LENGTH_SHORT).show()
            return
        }

        currentUser.updatePassword(newPassword).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Şifre başarıyla güncellendi.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Şifre güncellenirken hata oluştu: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signOutUser() {
        auth.signOut()
        findNavController().navigate(R.id.action_userInfo_to_loginFragment)
    }

    private fun holdUserAccount() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userRef = database.child(currentUser.uid)
            val updatedRole = hashMapOf(
                "role" to "Dondurulmuş"
            ) as Map<String, Any>

            userRef.updateChildren(updatedRole).addOnSuccessListener {
                signOutUser()
            }.addOnFailureListener { e ->
                Toast.makeText(context, "Hata: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateEmail(newEmail: String) {
        val currentUser = auth.currentUser

        if (currentUser == null) {
            Toast.makeText(context, "Kullanıcı oturumu geçerli değil.", Toast.LENGTH_SHORT).show()
            return
        }

        currentUser.updateEmail(newEmail).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userRef = database.child(currentUser.uid)
                userRef.child("email").setValue(newEmail)
            } else {
                Toast.makeText(context, "E-posta güncellenirken hata oluştu: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}