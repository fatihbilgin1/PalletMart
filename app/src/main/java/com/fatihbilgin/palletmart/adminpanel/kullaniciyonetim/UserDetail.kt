package com.fatihbilgin.palletmart.adminpanel.kullaniciyonetim

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.fatihbilgin.palletmart.R
import com.fatihbilgin.palletmart.databinding.FragmentUserDetailBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class UserDetail : Fragment() {

    private lateinit var binding: FragmentUserDetailBinding
    private lateinit var databaseReference: DatabaseReference
    private var userId:String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserDetailBinding.inflate(inflater, container, false)
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        userId = arguments?.getString("userId")

        userId?.let { loadUserDetails(it) }

        binding.backButton.setOnClickListener{
            findNavController().navigate(R.id.action_userDetail_to_userManagement)
        }
        binding.updateButton.setOnClickListener{
            val bundle = Bundle().apply { putString("userId",userId) }
            findNavController().navigate(R.id.action_userDetail_to_userUpdate, bundle)
        }
        binding.deleteButton.setOnClickListener{
            userId?.let { deleteButton(it) }
        }
        binding.accountFreezeButton.setOnClickListener{
            userId?.let { accountFreezeButton(it) }
        }

        return binding.root
    }
    private fun loadUserDetails(userId:String){
        databaseReference.child(userId).addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val user = snapshot.getValue(User::class.java)
                    user?.let {
                        binding.UserName.text = it.name
                        binding.userPhoneTextView.text = it.phone
                    }
                }
                else{
                    Toast.makeText(context,"Kullanıcı Bulunamadı!",Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_userDetail_to_userManagement)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,"Veri Alınamadı:${error.message}",Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteButton(userId: String){
        databaseReference.child(userId).removeValue().addOnCompleteListener{task ->
            if (task.isSuccessful){
                Toast.makeText(context,"Kullanıcı Başarıyla Silindi.",Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_userDetail_to_userManagement)
            }
            else{
                Toast.makeText(context,"Silme İşlemi Başarısız:${task.exception?.message}",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun accountFreezeButton(userId: String){
        val updates = mapOf("role" to "Dondurulmuş")
        databaseReference.child(userId).updateChildren(updates).addOnCompleteListener{task ->
            if (task.isSuccessful){
                Toast.makeText(context,"Hesap Başarıyla Donduruldu.",Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(context,"Hesap Dondurulamadı!:${task.exception?.message}",Toast.LENGTH_SHORT).show()
            }
        }
    }

}