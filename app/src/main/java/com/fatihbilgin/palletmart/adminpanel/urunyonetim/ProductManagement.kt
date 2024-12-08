package com.fatihbilgin.palletmart.adminpanel.urunyonetim

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fatihbilgin.palletmart.R
import com.fatihbilgin.palletmart.databinding.FragmentProductManagementBinding
import com.google.firebase.database.*

class ProductManagement : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var addProductButton: Button
    private lateinit var backButton: ImageView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var productList: MutableList<Product>
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Firebase database referansını oluştur
        databaseReference = FirebaseDatabase.getInstance().getReference("Products")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentProductManagementBinding.inflate(inflater, container, false)

        recyclerView = binding.adminProductRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)

        productList = mutableListOf()
        productAdapter = ProductAdapter(productList) { product: Product ->
            // Tıklanan ürünü göndermek
            val action = ProductManagementDirections
                .actionProductManagementToProductDetailadmin(product)
            findNavController().navigate(action)
        }
        recyclerView.adapter = productAdapter

        addProductButton = binding.addProductButton
        backButton = binding.backButton

        addProductButton.setOnClickListener {
            findNavController().navigate(R.id.action_ProductManagement_to_addProductadmin)
        }

        backButton.setOnClickListener {
            findNavController().navigate(R.id.action_ProductManagement_to_adminAnaEkran)
        }

        // Firebase'den ürünleri çekmek için listener ekleyin
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productList.clear() // Önce listeyi temizle

                for (dataSnapshot in snapshot.children) {
                    val product = dataSnapshot.getValue(Product::class.java)
                    product?.let {
                        productList.add(it) // Ürünü listeye ekle
                    }
                }

                // RecyclerView adaptörünü güncelle
                productAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Veriler alınamadı: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })

        return binding.root
    }
}