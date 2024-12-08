package com.fatihbilgin.palletmart.adminpanel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.fatihbilgin.palletmart.R
import com.fatihbilgin.palletmart.girispanel.LoginFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AdminAnaEkran : Fragment() {

    // Firebase Database reference
    private lateinit var database: DatabaseReference

    // UI elements
    private lateinit var totalProductsText: TextView
    private lateinit var totalOrdersText: TextView
    private lateinit var totalUsersText: TextView

    // FirebaseAuth instance
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_ana_ekran, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize UI elements
        totalProductsText = view.findViewById(R.id.totalProductText)
        totalOrdersText = view.findViewById(R.id.totalOrdersText)
        totalUsersText = view.findViewById(R.id.totalUsersText)

        // Initialize Firebase Database and Authentication
        database = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()  // FirebaseAuth instance'ını alıyoruz

        // Retrieve data from Firebase
        fetchDataFromDatabase()

        // Set up card click listeners
        val productCard = view.findViewById<CardView>(R.id.productCard)
        val userCard = view.findViewById<CardView>(R.id.userCard)
        val orderCard = view.findViewById<CardView>(R.id.orderCard)
        val settingCard = view.findViewById<CardView>(R.id.settingCard)
        val exitButton = view.findViewById<TextView>(R.id.exitButton)

        productCard.setOnClickListener {
            findNavController().navigate(R.id.action_adminAnaEkran_to_productManagement)
        }

        userCard.setOnClickListener {
            findNavController().navigate(R.id.action_adminAnaEkran_to_userManagement)
        }

        orderCard.setOnClickListener {
            findNavController().navigate(R.id.action_adminAnaEkran_to_orderTracking)
        }

        settingCard.setOnClickListener {
            findNavController().navigate(R.id.action_adminAnaEkran_to_settings)
        }

        // Çıkış butonuna tıklama
        exitButton.setOnClickListener {
            signOut() // Çıkış işlemi
        }
    }

    // Method to fetch data from Firebase
    private fun fetchDataFromDatabase() {
        database.child("Products").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val totalProducts = snapshot.childrenCount
                totalProductsText.text = "📦 Toplam Ürün: ${totalProducts}"
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,"Ürün Bilgileri Alınamadı.",Toast.LENGTH_SHORT).show()
            }
        })

        database.child("Orders").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val totalOrders = snapshot.childrenCount
                totalOrdersText.text = "🛒 Toplam Sipariş: $totalOrders"
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Sipariş bilgileri alınamadı", Toast.LENGTH_SHORT).show()
            }
        })

        database.child("Users").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val totalUsers = snapshot.childrenCount
                totalUsersText.text = "👤 Toplam Kullanıcı: $totalUsers"
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Kullanıcı bilgileri alınamadı", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Firebase Authentication ile çıkış yapma
    private fun signOut() {
        auth.signOut() // Kullanıcıyı Firebase Authentication'dan çıkartıyoruz
        Toast.makeText(context, "Çıkış yapıldı", Toast.LENGTH_SHORT).show()

        // Çıkış sonrası LoginFragment'a yönlendirme
        val fragment = LoginFragment()
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.loginFragment, fragment)
        transaction.commit()
    }
}