package com.fatihbilgin.palletmart.girispanel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.fatihbilgin.palletmart.R
import com.fatihbilgin.palletmart.adminpanel.kullaniciyonetim.User // Kullanıcı yönetim modelini import ediyoruz
import com.fatihbilgin.palletmart.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val view = binding.root

        // Kayıt ol butonuna tıklama işlemi
        binding.registerButton.setOnClickListener {
            val name = binding.nameEditText.text.toString().trim()
            val phone = binding.phoneEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "Tüm alanları doldurun!", Toast.LENGTH_SHORT).show()
            } else if (password.length < 6) {
                Toast.makeText(context, "Şifre en az 6 karakter olmalı!", Toast.LENGTH_SHORT).show()
            } else {
                // Kullanıcıyı Firebase Authentication ile kaydet
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Kayıt başarılı, kullanıcının UID'sini alıyoruz
                            val userId = firebaseAuth.currentUser?.uid
                            if (userId != null) {
                                // Kullanıcı modeline uygun bir User nesnesi oluşturuyoruz
                                val user = User(
                                    userId = userId,
                                    name = name,
                                    email = email,
                                    phone = phone,
                                    role = "Kullanıcı" // Varsayılan rol
                                )

                                // Firebase Realtime Database'e kullanıcıyı ekliyoruz
                                val database = FirebaseDatabase.getInstance().reference
                                database.child("Users").child(userId).setValue(user)
                                    .addOnCompleteListener { dbTask ->
                                        if (dbTask.isSuccessful) {
                                            Toast.makeText(
                                                context,
                                                "Kayıt başarılı ve veritabanına eklendi!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            // Kayıt başarılıysa giriş ekranına yönlendiriyoruz
                                            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "Veritabanına eklenirken hata: ${dbTask.exception?.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                            }
                        } else {
                            Toast.makeText(
                                context,
                                "Kayıt başarısız: ${task.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }

        // Giriş ekranına geri dönme işlemi
        binding.goToLoginTextView.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}