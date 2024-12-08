package com.fatihbilgin.palletmart.adminpanel

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.fatihbilgin.palletmart.R
import com.fatihbilgin.palletmart.databinding.FragmentSettingsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Settings : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private var userId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        // Firebase Database ve FirebaseAuth instance'larını alıyoruz
        database = FirebaseDatabase.getInstance().getReference("Users")
        auth = FirebaseAuth.getInstance()

        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_settings_to_adminAnaEkran)
        }

        getAdminData()

        binding.editProfileButton.setOnClickListener {
            enableEditing(true)
        }

        binding.saveProfileButton.setOnClickListener {
            updateAdminData()
        }

        // Şifre değiştirme butonuna tıklama işlemi
        binding.changePasswordButton.setOnClickListener {
            changePassword()
        }

        return binding.root
    }

    private fun getAdminData() {
        database.orderByChild("role").equalTo("Admin")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (childSnapshot in snapshot.children) {
                        userId = childSnapshot.key
                        val adminName = childSnapshot.child("name").getValue(String::class.java)
                        val adminEmail = childSnapshot.child("email").getValue(String::class.java)

                        binding.adminNameText.text = adminName
                        binding.adminEmailText.text = adminEmail
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Veri Çekilemedi: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun enableEditing(enable: Boolean) {
        if (enable) {
            binding.adminNameText.visibility = View.GONE
            binding.adminEmailText.visibility = View.GONE
            binding.adminNameEditLayout.visibility = View.VISIBLE
            binding.adminEmailEditLayout.visibility = View.VISIBLE
            binding.saveProfileButton.visibility = View.VISIBLE
            binding.editProfileButton.visibility = View.GONE

            binding.adminNameEdit.setText(binding.adminNameText.text.toString())
            binding.adminEmailEdit.setText(binding.adminEmailText.text.toString())
        } else {
            binding.adminNameText.visibility = View.VISIBLE
            binding.adminEmailText.visibility = View.VISIBLE
            binding.adminNameEditLayout.visibility = View.GONE
            binding.adminEmailEditLayout.visibility = View.GONE
            binding.saveProfileButton.visibility = View.GONE
            binding.editProfileButton.visibility = View.VISIBLE
        }
    }

    private fun updateAdminData() {
        val newName = binding.adminNameEdit.text.toString()
        val newEmail = binding.adminEmailEdit.text.toString()

        if (userId == null || newName.isEmpty() || newEmail.isEmpty()) {
            Toast.makeText(context, "Tüm Alanları Doldurun.", Toast.LENGTH_SHORT).show()
            return
        }

        val updates = mapOf(
            "name" to newName,
            "email" to newEmail
        )

        database.child(userId!!).updateChildren(updates)
            .addOnSuccessListener {
                Toast.makeText(context, "Bilgiler Başarıyla Güncellendi.", Toast.LENGTH_SHORT).show()
                binding.adminNameText.text = newName
                binding.adminEmailText.text = newEmail
                enableEditing(false)
            }
            .addOnFailureListener { error ->
                Toast.makeText(context, "Güncelleme Başarısız: ${error.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun changePassword() {
        val newPassword = binding.newPasswordEditText.text.toString()
        val confirmPassword = binding.confirmPasswordEditText.text.toString()

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(context, "Tüm Alanları Doldurun.", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPassword != confirmPassword) {
            Toast.makeText(context, "Yeni şifreler eşleşmiyor.", Toast.LENGTH_SHORT).show()
            return
        }

        val user = auth.currentUser

        user?.let {
            // Şifreyi değiştir
            it.updatePassword(newPassword)
                .addOnSuccessListener {
                    // Şifre güncellendikten sonra veritabanı güncellemesi yapılabilir
                    updatePasswordInDatabase(newPassword)
                }
                .addOnFailureListener { error ->
                    Toast.makeText(context, "Şifre Güncellenemedi: ${error.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun updatePasswordInDatabase(newPassword: String) {
        // Veritabanındaki şifreyi güncelleme işlemi
        val updates = mapOf("password" to newPassword)
        database.child(userId!!).updateChildren(updates)
            .addOnSuccessListener {
                Toast.makeText(context, "Şifre Başarıyla Değiştirildi.", Toast.LENGTH_SHORT).show()

                // Çıkış yapma işlemi
                auth.signOut()
                Toast.makeText(context, "Çıkış yapıldı, yeniden giriş yapın.", Toast.LENGTH_SHORT).show()

                // Login ekranına yönlendirme
                findNavController().navigate(R.id.action_settings_to_loginFragment)
            }
            .addOnFailureListener { error ->
                Toast.makeText(context, "Şifre Güncelleme Başarısız: ${error.message}", Toast.LENGTH_SHORT).show()
            }
    }
}