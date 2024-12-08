package com.fatihbilgin.palletmart.kulanicipanel

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.fatihbilgin.palletmart.R
import com.fatihbilgin.palletmart.adminpanel.urunyonetim.Product
import com.fatihbilgin.palletmart.databinding.FragmentProductDetailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ProductDetail : Fragment() {
        private lateinit var binding:FragmentProductDetailBinding
        private var selectedProduct: Product? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductDetailBinding.inflate(inflater, container, false)

        arguments?.let {
            selectedProduct = ProductDetailArgs.fromBundle(it).product
        }

        displayProductDetails()

        binding.addToCartButton.setOnClickListener{
            addToCart()
        }
        binding.backButton.setOnClickListener{
            findNavController().navigate(R.id.action_productDetail_to_kullaniciEkran)
        }
        return binding.root
    }

    private fun displayProductDetails(){
        selectedProduct?.let{ product ->
            binding.productName.text = product.name
            binding.productDescription.text = product.description
            binding.productPrice.text = "Fiyat: ${product.price} TL"
            binding.productStock.text = if (product.stock ?: 0>0){
                "Stok: ${product.stock}"
            }else{
                "Tükendi"
            }

            Glide.with(this)
                .load(product.imageUrl)
                .placeholder(R.drawable.no_photos)
                .into(binding.productImage)
        }
    }
    private fun addToCart(){
       val currentUser = FirebaseAuth.getInstance().currentUser
       val userId = currentUser?.uid

        if (userId == null){
            Toast.makeText(context,"Kullanıcı Oturumu Açık Değil",Toast.LENGTH_SHORT).show()
            return
        }

        val cartReference : DatabaseReference = FirebaseDatabase.getInstance().getReference("Cart").child(userId)

        selectedProduct?.let{ product ->
            cartReference.child(product.id ?: "").setValue(product)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Ürün sepete eklendi.", Toast.LENGTH_SHORT).show()
                }
            .addOnFailureListener { error ->
                Toast.makeText(requireContext(), "Hata: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(context,"Ürün Bilgisi Alınamadı.",Toast.LENGTH_SHORT).show()
        }
    }
}