package com.fatihbilgin.palletmart.adminpanel.siparisyonetim

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fatihbilgin.palletmart.databinding.ItemOrderBinding

class OrderAdapter(
    private var orders: List<Order>,
    private val onItemClick: (Order) -> Unit
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(private val binding: ItemOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order) {
            binding.orderId.text = "Sipariş ID: ${order.id}"
            binding.orderDate.text = "Sipariş Tarihi: ${order.date}"
            binding.orderStatus.text = "Durum: ${order.status}"
            binding.customerName.text = "Kullanıcı: ${order.customerName}"
            // Detay için tıklama
            binding.root.setOnClickListener { onItemClick(order) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding =
            ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orders[position])
    }

    override fun getItemCount(): Int = orders.size

    fun updateList(newOrders: List<Order>) {
        orders = newOrders
        notifyDataSetChanged()
    }
}