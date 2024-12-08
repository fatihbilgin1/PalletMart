package com.fatihbilgin.palletmart.adminpanel.urunyonetim

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fatihbilgin.palletmart.R
import com.fatihbilgin.palletmart.databinding.ItemProductBinding

class ProductAdapter(
    private val productList: List<Product>,
    private val onItemClick: (Product) -> Unit // Lambda ile tıklama olayı
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        // LayoutInflater ve binding kullanarak item layout'u bağla
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.binding.productName.text = product.name
        holder.binding.productPrice.text = "₺${product.price}"
        holder.binding.productStock.text = "Stok: ${product.stock}"

        // Glide ile görseli yükleyelim
        if (product.imageUrl.isNotEmpty()) {
            // Eğer imageUrl varsa, Glide ile resmi yükle
            Glide.with(holder.binding.productImage.context)
                .load(product.imageUrl) // imageUrl'yi burada kullanıyoruz
                .placeholder(R.drawable.no_photos) // Varsayılan görsel
                .into(holder.binding.productImage)
        } else {
            // Eğer imageUrl boşsa, varsayılan görseli göster
            holder.binding.productImage.setImageResource(R.drawable.no_photos)
        }

        // Her ürün tıklanabilir olacak ve tıklandığında onItemClick tetiklenecek
        holder.itemView.setOnClickListener {
            onItemClick(product)
        }
    }

    override fun getItemCount(): Int = productList.size

    class ProductViewHolder(val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root)
}