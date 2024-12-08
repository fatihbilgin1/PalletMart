package com.fatihbilgin.palletmart.kulanicipanel

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.fatihbilgin.palletmart.R
import com.fatihbilgin.palletmart.adminpanel.urunyonetim.Product
import com.fatihbilgin.palletmart.databinding.FragmentKullaniciEkranBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class KullaniciEkran : Fragment() {

    private lateinit var binding:FragmentKullaniciEkranBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var productListAdapter: ProductListAdapter
    private val productList = mutableListOf<Product>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentKullaniciEkranBinding.inflate(inflater,container,false)
        databaseReference = FirebaseDatabase.getInstance().getReference("Products")

        setupRecyclerView()
        loadProducts()

        binding.sellerPromptTextView.setOnClickListener{
          val userId = FirebaseAuth.getInstance().currentUser?.uid
            userId?.let {
                updateUserRoleToSeller(it)
            }
        }

        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            when(menuItem.itemId){
                R.id.nav_home -> findNavController().navigate(R.id.kullaniciEkran)
                R.id.nav_cart -> findNavController().navigate(R.id.cart)
                R.id.nav_user_info -> findNavController().navigate(R.id.userInfo)
            }
            true
        }


        return binding.root
    }

    private fun setupRecyclerView(){
        productListAdapter = ProductListAdapter(productList){product ->
            val action = KullaniciEkranDirections.actionKullaniciEkranToProductDetail(product)
            findNavController().navigate(action)
        }
        binding.productRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.productRecyclerView.adapter = productListAdapter
    }
    private fun loadProducts(){
            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener{
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    productList.clear()
                    for (productSnapshot in snapshot.children){
                        val product = productSnapshot.getValue(Product::class.java)
                        product?.let { productList.add(it) }
                    }
                    productListAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context,"Veri Çekilemedi: ${error.message}",Toast.LENGTH_SHORT).show()
                }
            })
    }
    private fun updateUserRoleToSeller(userId:String){
        val database = FirebaseDatabase.getInstance().getReference("Users").child(userId)
        database.child("role").setValue("Satıcı").addOnSuccessListener {
            Toast.makeText(context,"Satıcı oldunuz! Artık Ürün Ekleyebilirsiniz.",Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_kullaniciEkran_to_saticiAnaEkran)
        }.addOnFailureListener {
            Toast.makeText(requireContext(),"Satıcı olmak için işlem başarısız oldu",Toast.LENGTH_SHORT).show()
        }
    }
}