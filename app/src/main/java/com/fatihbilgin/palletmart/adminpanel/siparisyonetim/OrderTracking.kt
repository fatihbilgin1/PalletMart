package com.fatihbilgin.palletmart.adminpanel.siparisyonetim

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.fatihbilgin.palletmart.R
import com.fatihbilgin.palletmart.databinding.FragmentOrderTrackingBinding
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderTracking : Fragment(R.layout.fragment_order_tracking) {

    private var _binding: FragmentOrderTrackingBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: DatabaseReference
    private lateinit var adapter: OrderAdapter
    private val orders = mutableListOf<Order>()

    private fun formatTimestamp(timestamp: String): String {
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = Date(timestamp.toLong())
            sdf.format(date)
        } catch (e: Exception) {
            "Geçersiz Tarih"
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentOrderTrackingBinding.bind(view)

        database = FirebaseDatabase.getInstance().getReference("Orders")

        adapter = OrderAdapter(orders) { order ->

            val action = OrderTrackingDirections.actionOrderTrackingToOrderDetail(order.id)
            findNavController().navigate(action)
        }

        binding.orderRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.orderRecyclerView.adapter = adapter

        fetchOrders()

        binding.backButton.setOnClickListener{
            findNavController().navigate(R.id.action_orderTracking_to_adminAnaEkran)
        }

        // **Arama Çubuğu** işlevselliğini burada ekliyoruz
        binding.orderSearchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Arama gönderildiğinde bir işlem yapmamıza gerek yok
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Arama metni değiştiğinde buradaki veriyi filtrele
                val filteredOrders = orders.filter { order ->
                    order.customerName.contains(newText ?: "", ignoreCase = true)
                }
                adapter.updateList(filteredOrders) // Filtrelenmiş listeyi adapter'a gönder
                return true
            }
        })
    }

    private fun fetchOrders() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                orders.clear()
               for (snapshotChild in snapshot.children){
                   val id = snapshotChild.child("id").value.toString()
                   val date = snapshotChild.child("date").value.toString()
                   val customerName = snapshotChild.child("customerName").value?.toString() ?: "Bilinmiyor"
                   val status = snapshotChild.child("status").value.toString()

                   Log.d("OrderTracking","CustomerName: $customerName")

                   val formattedDate = formatTimestamp(date)

                   val order = Order(id,formattedDate,customerName,status)
                   orders.add(order)
               }
                adapter.updateList(orders)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Veri yüklenemedi: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}