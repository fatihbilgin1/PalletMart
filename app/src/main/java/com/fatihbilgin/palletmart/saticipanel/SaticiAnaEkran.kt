package com.fatihbilgin.palletmart.saticipanel

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fatihbilgin.palletmart.R
import com.fatihbilgin.palletmart.adminpanel.urunyonetim.Product
import com.fatihbilgin.palletmart.databinding.FragmentSaticiAnaEkranBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SaticiAnaEkran : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var saticiAdapter:SaticiAdapter
    private lateinit var addProductButton: Button
    private lateinit var settingsButton: Button
    private lateinit var databaseReference: DatabaseReference

    private val productList = mutableListOf<Product>()
   private lateinit var currentUserId:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Firebase database referansını oluştur
        databaseReference = FirebaseDatabase.getInstance().getReference("Products")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       val binding = FragmentSaticiAnaEkranBinding.inflate(inflater,container,false)

        recyclerView = binding.productRecyclerView
        addProductButton = binding.addProductButton
        settingsButton = binding.settingsButton


        currentUserId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()

        recyclerView.layoutManager = LinearLayoutManager(context)

        saticiAdapter = SaticiAdapter(productList){product ->
            val action = SaticiAnaEkranDirections.actionSaticiAnaEkranToProductDetailSatici(product)
            findNavController().navigate(action)
        }


        recyclerView.adapter = saticiAdapter

        loadSellerProducts()

        addProductButton.setOnClickListener{
            findNavController().navigate(R.id.action_saticiAnaEkran_to_addProductSatici)
        }
        settingsButton.setOnClickListener {
            findNavController().navigate(R.id.action_saticiAnaEkran_to_settingsSatici)
        }
        return binding.root

    }

    private fun loadSellerProducts() {
        databaseReference.orderByChild("sellerId").equalTo(currentUserId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    productList.clear()
                    for (productSnapshot in snapshot.children) {
                        val product = productSnapshot.getValue(Product::class.java)
                        product?.let {
                            productList.add(it)
                        }
                    }
                    saticiAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Veri çekme hatası: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

}