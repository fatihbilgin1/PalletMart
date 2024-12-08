package com.fatihbilgin.palletmart.kulanicipanel

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fatihbilgin.palletmart.adminpanel.urunyonetim.Product
import com.fatihbilgin.palletmart.databinding.ItemUserkullaniciBinding

class ProductListAdapter(
    private val products: List<Product>,
    private val onClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductListAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(private val binding: ItemUserkullaniciBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.productName.text = product.name
            binding.productPrice.text = "₺${product.price ?: 0.0}"
            Glide.with(binding.productImage.context)
                .load(product.imageUrl)
                .into(binding.productImage)

            binding.root.setOnClickListener { onClick(product) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemUserkullaniciBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size
}