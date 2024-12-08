package com.fatihbilgin.palletmart.adminpanel.kullaniciyonetim

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.fatihbilgin.palletmart.R
import com.fatihbilgin.palletmart.databinding.FragmentUserManagementBinding
import com.google.firebase.database.*

class UserManagement : Fragment() {

    private lateinit var binding: FragmentUserManagementBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var userAdapter: UserAdapter
    private val userList = mutableListOf<User>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserManagementBinding.inflate(inflater, container, false)

        databaseReference = FirebaseDatabase.getInstance().getReference("Users")

        setupRecyclerView()
        fetchUsers()

        binding.addUserButton.setOnClickListener {
            findNavController().navigate(R.id.action_userManagement_to_addUseradmin)
        }

        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_userManagement_to_adminAnaEkran)
        }

        return binding.root
    }

    private fun setupRecyclerView() {
        binding.userRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        userAdapter = UserAdapter(userList)
        binding.userRecyclerView.adapter = userAdapter
    }

    private fun fetchUsers() {
        // Firebase'den kullanıcı verilerini dinlemek için ValueEventListener kullanıyoruz
        databaseReference.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear() // Eski listeyi temizle

                // Firebase'den her kullanıcıyı ekle
                for (data in snapshot.children) {
                    val user = data.getValue(User::class.java)
                    user?.let { userList.add(it) }
                }

                // RecyclerView'u güncelle
                userAdapter.notifyDataSetChanged()

                // Kullanıcı yoksa "Kullanıcı bulunamadı" mesajını göster
                binding.noUsersTextView.visibility =
                    if (userList.isEmpty()) View.VISIBLE else View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                // Hata durumunda kullanıcıyı bilgilendir
                Toast.makeText(requireContext(), "Hata: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Firebase'den bir kullanıcı silindiğinde veya engellendiğinde RecyclerView'un güncellenmesini sağlamak için:
    private fun refreshUserList() {
        // UserManagement ekranında yeniden kullanıcıları al
        fetchUsers()
    }
}