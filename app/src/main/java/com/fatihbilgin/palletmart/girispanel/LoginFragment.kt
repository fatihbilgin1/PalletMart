package com.fatihbilgin.palletmart.girispanel

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.fatihbilgin.palletmart.R
import com.fatihbilgin.palletmart.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root

        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        setupListeners()

        return view
    }

    private fun setupListeners() {
        // Giriş butonuna tıklama
        binding.loginbutton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(requireContext(), "Lütfen e-posta adresinizi girin.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(requireContext(), "Lütfen şifrenizi girin.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            handleLogin(email, password)
        }

        // Kayıt ol butonuna tıklama
        binding.registerTextView.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun handleLogin(email: String, password: String) {
        // Firebase Authentication ile giriş
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val currentUser = firebaseAuth.currentUser
                    if (currentUser != null) {
                        // Kullanıcı girişi başarılı, rolü almak için veritabanına sorgu yapıyoruz
                        val userId = currentUser.uid
                        database.child("Users").child(userId).get().addOnSuccessListener { snapshot ->
                            if (snapshot.exists()) {
                                val role = snapshot.child("role").value.toString() // role'nin doğru şekilde kaydedildiğinden emin olun
                                if (role == "Admin") {
                                    Toast.makeText(requireContext(), "Admin olarak giriş yapıldı.", Toast.LENGTH_SHORT).show()
                                    findNavController().navigate(R.id.action_loginFragment_to_adminAnaEkran)
                                } else if (role == "Kullanıcı") {
                                    Toast.makeText(requireContext(), "Kullanıcı olarak giriş yapıldı.", Toast.LENGTH_SHORT).show()
                                    findNavController().navigate(R.id.action_loginFragment_to_kullaniciEkran)
                                }else if (role == "Satıcı"){
                                    Toast.makeText(requireContext(),"Satıcı Olarak Giriş Yapıldı.",Toast.LENGTH_SHORT).show()
                                    findNavController().navigate(R.id.action_loginFragment_to_saticiAnaEkran)
                                } else {
                                    Toast.makeText(requireContext(), "Rol hatalı.", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(requireContext(), "Kullanıcı verisi bulunamadı.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "E-posta, şifre hatalı.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}