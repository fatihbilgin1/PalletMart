package com.fatihbilgin.palletmart.adminpanel.siparisyonetim

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.fatihbilgin.palletmart.R
import com.fatihbilgin.palletmart.databinding.FragmentOrderDetailBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderDetail : Fragment(R.layout.fragment_order_detail) {

    private var _binding: FragmentOrderDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: DatabaseReference
    private var orderId: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentOrderDetailBinding.bind(view)

        // Bundle'dan orderId'yi alıyoruz
        arguments?.let {
            orderId = it.getString("orderId")
        }

        orderId?.let {
            database = FirebaseDatabase.getInstance().getReference("Orders").child(it)
            fetchOrderDetails()
        }

        // Onayla butonuna tıklama
        binding.approveOrderButton.setOnClickListener {
            updateOrderStatus("Kargoya Verildi")
        }

        // Reddet butonuna tıklama
        binding.rejectOrderButton.setOnClickListener {
            updateOrderStatus("Reddedildi")
        }

        // Geri dön butonu
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun fetchOrderDetails() {
        database.get().addOnSuccessListener { snapshot ->
            val order = snapshot.getValue(Order::class.java)
            order?.let {
                binding.orderId.text = "Sipariş ID: ${it.id}"

                val formattedDate = formatTimestamp(it.date)  // Formatlanmış tarih
                binding.orderDate.text = "Sipariş Tarihi: $formattedDate"

                binding.customerName.text = "Müşteri: ${it.customerName}"
                binding.orderStatus.text = "Durum: ${it.status}"

                // Sipariş durumu kontrolü yap
                if (it.status == "Kargoya Verildi" || it.status == "Reddedildi") {
                    disableButtons(it.status)
                }
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Sipariş bilgileri alınamadı.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun formatTimestamp(timestamp: String): String {
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = Date(timestamp.toLong())  // Timestamp'ı uzun sayı olarak alıyoruz
            sdf.format(date)  // Formatlı tarih
        } catch (e: Exception) {
            "Geçersiz Tarih"  // Hata durumunda
        }
    }

    private fun updateOrderStatus(newStatus: String) {
        database.child("status").setValue(newStatus)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Sipariş durumu $newStatus olarak güncellendi.", Toast.LENGTH_SHORT).show()
                disableButtons(newStatus) // Butonları devre dışı bırak
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Durum güncellenemedi.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun disableButtons(status: String) {
        // Onayla butonunu devre dışı bırak
        binding.approveOrderButton.isEnabled = false
        binding.approveOrderButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.gri))
        binding.approveOrderButton.text = status

        // Reddet butonunu devre dışı bırak
        binding.rejectOrderButton.isEnabled = false
        binding.rejectOrderButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.gri))
        binding.rejectOrderButton.text = status
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}