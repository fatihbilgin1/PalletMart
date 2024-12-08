package com.fatihbilgin.palletmart.saticipanel

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.fatihbilgin.palletmart.R
import com.fatihbilgin.palletmart.databinding.FragmentSettingsSaticiBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class SettingsSatici : Fragment() {
    private lateinit var binding:FragmentSettingsSaticiBinding
    private lateinit var database:DatabaseReference
    private lateinit var auth:FirebaseAuth
    private var userId:String?=null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsSaticiBinding.inflate(inflater,container,false)

        database = FirebaseDatabase.getInstance().getReference("Users")
        auth = FirebaseAuth.getInstance()

        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_settingsSatici_to_saticiAnaEkran)
        }

        getSaticiData()

        binding.editProfileButton.setOnClickListener {
            enableEditing(true)
        }
        binding.saveProfileButton.setOnClickListener {
            updateSaticiData()
        }
        binding.changePasswordButton.setOnClickListener {
            changePassword()
        }
        binding.exitButtonSatici.setOnClickListener {
           auth.signOut()
            findNavController().navigate(R.id.action_settingsSatici_to_loginFragment)
        }

        return binding.root
    }

    private fun getSaticiData(){
        database.orderByChild("role").equalTo("Satıcı")
            .addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (childSnapshot in snapshot.children){
                        userId = childSnapshot.key
                        val saticiName = childSnapshot.child("name").getValue(String::class.java)
                        val saticiEmail = childSnapshot.child("email").getValue(String::class.java)

                        binding.sellerNameText.text = saticiName
                        binding.sellerEmailText.text = saticiEmail
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    context?.let {
                        Toast.makeText(it, "Veri çekilemedi: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            })
    }
    private fun enableEditing(enable : Boolean){
        if (enable){
            binding.sellerNameText.visibility = View.GONE
            binding.sellerEmailText.visibility = View.GONE
            binding.sellerNameEditLayout.visibility = View.VISIBLE
            binding.sellerEmailEditLayout.visibility = View.VISIBLE
            binding.saveProfileButton.visibility = View.VISIBLE
            binding.editProfileButton.visibility = View.GONE

            binding.sellerNameEdit.setText(binding.sellerNameText.text.toString())
            binding.sellerEmailEdit.setText(binding.sellerEmailText.text.toString())
        } else{
            binding.sellerNameText.visibility = View.VISIBLE
            binding.sellerEmailText.visibility = View.VISIBLE
            binding.sellerNameEditLayout.visibility = View.GONE
            binding.sellerEmailEditLayout.visibility = View.GONE
            binding.saveProfileButton.visibility = View.GONE
            binding.editProfileButton.visibility = View.VISIBLE
        }
    }

    private fun updateSaticiData(){
        val newName = binding.sellerNameEdit.text.toString()
        val newEmail = binding.sellerEmailEdit.text.toString()

        if (userId == null || newName.isEmpty() || newEmail.isEmpty()){
            Toast.makeText(context,"Tüm alanları Doldurun.",Toast.LENGTH_SHORT).show()
            return
        }
        val updates = mapOf(
            "name" to newName,
            "email" to newEmail
        )
        database.child(userId!!).updateChildren(updates)
            .addOnSuccessListener {
                Toast.makeText(context,"Bilgiler Başarıyla Güncellendi.",Toast.LENGTH_SHORT).show()
                binding.sellerNameText.text = newName
                binding.sellerEmailText.text = newEmail
                enableEditing(false)
            }
            .addOnFailureListener { error ->
                Toast.makeText(context,"Güncelleme Başarısız: ${error.message}",Toast.LENGTH_SHORT).show()
            }
    }
    private fun changePassword(){
        val newPassword = binding.newPasswordEditText.text.toString()
        val confirmPassword = binding.confirmPasswordEditText.text.toString()

        if (newPassword.isEmpty() || confirmPassword.isEmpty()){
            Toast.makeText(context,"Tüm Alanları Doldurun.",Toast.LENGTH_SHORT).show()
            return
        }

        if (newPassword != confirmPassword){
            Toast.makeText(context,"Yeni Şifreler Eşleşmiyor.",Toast.LENGTH_SHORT).show()
            return
        }
        val user = auth.currentUser
        user?.let {
            it.updatePassword(newPassword)
                .addOnSuccessListener {
                    updatePasswordInDatabase(newPassword)
                }.addOnFailureListener { error ->
                    Toast.makeText(context,"Şifre Güncellenemedi: ${error.message}",Toast.LENGTH_SHORT).show()
                }
        }
    }
    private fun updatePasswordInDatabase(newPassword:String){
        val updates = mapOf("password" to newPassword)
        database.child(userId!!).updateChildren(updates)
            .addOnSuccessListener {
                Toast.makeText(context,"Şifre Başarıyla Değiştirildi.",Toast.LENGTH_SHORT).show()

                auth.signOut()
                Toast.makeText(context,"Çıkış Yapıldı, Yeniden Giriş Yapın.",Toast.LENGTH_SHORT).show()

                findNavController().navigate(R.id.action_settingsSatici_to_loginFragment)
            }
            .addOnFailureListener { error ->
                Toast.makeText(context,"Şifre Güncelleme Başarısız: ${error.message}",Toast.LENGTH_SHORT).show()
            }
    }
}