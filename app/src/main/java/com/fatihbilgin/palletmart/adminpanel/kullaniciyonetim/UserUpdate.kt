package com.fatihbilgin.palletmart.adminpanel.kullaniciyonetim

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.fatihbilgin.palletmart.R
import com.fatihbilgin.palletmart.databinding.FragmentUserUpdateBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class UserUpdate : Fragment() {

    private lateinit var binding: FragmentUserUpdateBinding
    private lateinit var databaseReference: DatabaseReference
    private var userId:String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserUpdateBinding.inflate(inflater, container, false)
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        userId = arguments?.getString("userId")

        userId?.let { loadUserDetails(it) }


        binding.updateButton.setOnClickListener{
            userId?.let { updateButton(it) }
        }
        binding.cancelButton.setOnClickListener{
            findNavController().navigate(R.id.action_userUpdate_to_userDetail)
        }

        return binding.root
    }
    private fun loadUserDetails(userId:String){
        databaseReference.child(userId).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()){
                val name = snapshot.child("name").value.toString()
                val email = snapshot.child("email").value.toString()

                binding.userNameEditText.setText(name)
                binding.userEmailEditText.setText(email)
            }
            else{
                Toast.makeText(context,"Kullanıcı Bilgileri Alınamadı!",Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_userUpdate_to_userDetail)
            }
        }.addOnFailureListener {
            Toast.makeText(context,"Hata: ${it.message}",Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateButton(userId: String){

        val updatedName = binding.userNameEditText.text.toString().trim()
        val updatedEmail =binding.userEmailEditText.text.toString().trim()

        if(updatedName.isEmpty() || updatedEmail.isEmpty()){
            Toast.makeText(context,"Tüm Alanları Doldurun!",Toast.LENGTH_SHORT).show()
            return
        }
        val updates = mapOf(
            "name" to updatedName,
            "email" to updatedEmail
        )

        databaseReference.child(userId).updateChildren(updates).addOnCompleteListener{task ->
            if (task.isSuccessful){
                Toast.makeText(context,"Kullanıcı Bilgileri Başarıyla Güncellendi.",Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_userUpdate_to_userDetail)
            }
            else{
                Toast.makeText(context,"Güncelleme Başarısız: ${task.exception?.message}",Toast.LENGTH_SHORT).show()
            }
        }
    }

}