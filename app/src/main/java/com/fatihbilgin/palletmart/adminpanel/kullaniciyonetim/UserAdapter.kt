package com.fatihbilgin.palletmart.adminpanel.kullaniciyonetim

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fatihbilgin.palletmart.R
import androidx.navigation.findNavController

class UserAdapter(private val userList: List<User>) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.userName.text = user.name
        holder.userEmail.text = user.email
        holder.userRole.text = user.role

        // Profil fotoğrafını Glide ile yükle
        Glide.with(holder.itemView.context)
            .load(user.photoUrl)
            .placeholder(R.drawable.no_photos) // Resim yüklenirken geçici olarak gösterilecek resim
            .into(holder.userProfileImageView)


        // Aksiyon butonuna tıklama işlemi
        holder.userActionButton.setOnClickListener {
            // UserDetail ekranına geçiş
            val bundle = Bundle().apply{
                putString("userId",user.userId)
            }
           it.findNavController().navigate(R.id.action_userManagement_to_userDetail, bundle)
        }
    }

    override fun getItemCount(): Int = userList.size

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.userNameTextView)
        val userEmail: TextView = itemView.findViewById(R.id.userEmailTextView)
        val userRole: TextView = itemView.findViewById(R.id.userRoleTextView)
        val userProfileImageView: ImageView = itemView.findViewById(R.id.userProfileImageView)
        val userActionButton: ImageView = itemView.findViewById(R.id.userActionButton)
    }
}