package com.fatihbilgin.palletmart.kulanicipanel

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.fatihbilgin.palletmart.R
import com.fatihbilgin.palletmart.adminpanel.siparisyonetim.Order
import com.fatihbilgin.palletmart.adminpanel.urunyonetim.Product
import com.fatihbilgin.palletmart.databinding.FragmentCartBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Cart : Fragment() {

    private lateinit var binding: FragmentCartBinding
    private lateinit var cartAdapter: CartAdapter
    private var cartItems = mutableListOf<Product>()
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater, container, false)

        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid ?: return binding.root
        databaseReference = FirebaseDatabase.getInstance().getReference("Cart").child(userId)

        arguments?.let {
            val orderStatus = it.getString("orderStatus")
            if (orderStatus == "Sipariş Alındı"){
                Toast.makeText(context,"Siparişiniz Alınmıştır.",Toast.LENGTH_SHORT).show()
            }
        }

        // RecyclerView ve adapter'ı kur
        cartAdapter = CartAdapter(cartItems, { position, updatedProduct ->
            cartItems[position] = updatedProduct
            cartAdapter.notifyItemChanged(position)
            updateTotalPrice()
        }, { product ->
            deleteProductFromCart(product) // Ürünü sepetten silme
        },databaseReference)

        binding.cartRecyclerView.adapter = cartAdapter
        binding.cartRecyclerView.layoutManager = LinearLayoutManager(requireContext())


        loadCartItems()

        binding.backButton.setOnClickListener{
            findNavController().navigate(R.id.action_cart_to_kullaniciEkran)
        }

        // Sipariş verme butonuna tıklama
        binding.checkoutButton.setOnClickListener {
            checkStockBeforeCheckout()
        }

        return binding.root
    }

    private fun loadCartItems() {

        // Firebase'den sepet verilerini çekme
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                cartItems.clear()  // Mevcut listeyi temizle
                if (snapshot.exists()) {
                    for (cartSnapshot in snapshot.children) {
                        val product = cartSnapshot.getValue(Product::class.java)
                        product?.let { cartItems.add(it) }
                    }
                    cartAdapter.notifyDataSetChanged()
                    updateTotalPrice()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Veri alınırken bir hata oluştu: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateTotalPrice() {
        var totalPrice = 0.0
        for (item in cartItems) {
            val price = item.price ?: 1.0  // Eğer price null ise varsayılan değer 1.0 olacak
            totalPrice += price * item.quantity
        }
        binding.totalPriceTextView.text = "Toplam: ${totalPrice} TL"
    }

    private fun deleteProductFromCart(product: Product) {

        // Ürünü Firebase'den silme
        product.id?.let { id ->
            databaseReference.child(id).removeValue().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    cartItems.remove(product)
                    cartAdapter.notifyDataSetChanged()
                    updateTotalPrice()
                    Toast.makeText(context, "Ürün sepetten silindi", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Ürün silinemedi", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun checkStockBeforeCheckout() {
        var isStockValid = true
        for (item in cartItems) {
            if (item.quantity > item.stock!!) {
                isStockValid = false
                break
            }
        }

        if (isStockValid) {
            // Ödeme işlemi
            navigateToPaymentFragment()
            updateStockInFirebase()
        } else {
            // Stok hatası
            AlertDialog.Builder(requireContext())
                .setTitle("Stok Hatası")
                .setMessage("Bir veya daha fazla ürünün stoğu tükendi. Lütfen stok miktarına göre düzenleme yapın.")
                .setPositiveButton("Tamam", null)
                .show()
        }
    }


    private fun createOrderInFirebase() {

        val orderId = databaseReference.push().key ?: return
        val orderDate = System.currentTimeMillis().toString() // Sipariş tarihi

        val userId = FirebaseAuth.getInstance().currentUser?.uid?:return
        val usersRef = FirebaseDatabase.getInstance().getReference("Users").child(userId)

        usersRef.child("name").get().addOnSuccessListener { snapshot ->
            val customerName = snapshot.getValue(String::class.java) ?: "Bilinmiyor"
            val status = "Sipariş Alındı"

            // Siparişi Order modeline dönüştür
            val order = Order(orderId, orderDate, customerName, status)

            // Siparişi Firebase'e kaydet
            val ordersRef = FirebaseDatabase.getInstance().getReference("Orders")
            ordersRef.child(orderId).setValue(order).addOnCompleteListener {
                if (it.isSuccessful) {
                    // Sepetteki ürünleri sil
                    clearCartAfterOrder()
                } else {
                    Toast.makeText(context, "Sipariş kaydedilemedi", Toast.LENGTH_SHORT).show()
                }
            }
        }.addOnFailureListener {
            Toast.makeText(context,"Kullanıcı adı alınamadı",Toast.LENGTH_SHORT).show()
        }
    }
    private fun updateStockInFirebase(){
        val productsRef = FirebaseDatabase.getInstance().getReference("Products")
        val updates = mutableMapOf<String, Any?>()

        for (item in cartItems){
            val newStock = (item.stock ?:0) - item.quantity
            if (newStock>0){
                updates["${item.id}/stock"]=newStock
            }
        }
        productsRef.updateChildren(updates).addOnCompleteListener { task ->
            if (task.isSuccessful){
                createOrderInFirebase()
            }else {
                Toast.makeText(context,"Stok Güncellenirken Bir Hata Oluştu",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun clearCartAfterOrder() {
        // Sipariş verildikten sonra sepeti boşalt
        databaseReference.removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                cartItems.clear()
                cartAdapter.notifyDataSetChanged()
                updateTotalPrice()
            }
        }
    }
    private fun navigateToPaymentFragment() {
        // Ödeme ekranına yönlendir
        findNavController().navigate(R.id.action_cart_to_paymentFragment)
    }

}