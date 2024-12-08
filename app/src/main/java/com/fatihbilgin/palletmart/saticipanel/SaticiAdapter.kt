package com.fatihbilgin.palletmart.saticipanel

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fatihbilgin.palletmart.R
import com.fatihbilgin.palletmart.adminpanel.urunyonetim.Product
import com.bumptech.glide.Glide
import com.fatihbilgin.palletmart.databinding.ItemProductsaticiBinding

class SaticiAdapter(
    private val productList: List<Product>,
    private val onItemClickSatici: (Product) ->Unit
) : RecyclerView.Adapter<SaticiAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductsaticiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.binding.productNameTextView.text = product.name
        holder.binding.productPriceTextView.text = "Fiyat: ${product.price} TL"
        holder.binding.productStockTextView.text = "Stok: ${product.stock}"

        // Görseli yüklemek için Glide veya Picasso gibi kütüphaneleri kullanabilirsiniz
       if(product.imageUrl.isNotEmpty()){
           Glide.with(holder.binding.productImageView.context)
               .load(product.imageUrl)
               .placeholder(R.drawable.no_photos)
               .into(holder.binding.productImageView)
       } else{
           holder.binding.productImageView.setImageResource(R.drawable.no_photos)
       }

        holder.itemView.setOnClickListener{
            onItemClickSatici(product)
        }


    }

    override fun getItemCount(): Int = productList.size

    class ProductViewHolder(val binding: ItemProductsaticiBinding) : RecyclerView.ViewHolder(binding.root)
}