package com.fatihbilgin.palletmart.kulanicipanel

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fatihbilgin.palletmart.R
import com.fatihbilgin.palletmart.adminpanel.urunyonetim.Product
import com.fatihbilgin.palletmart.databinding.ItemCartBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener


class CartAdapter(
    private val cartItems: MutableList<Product>,
    private val onQuantityChanged: (Int, Product) -> Unit,  // Quantity değiştiğinde çağrılacak fonksiyon
    private val onDeleteProduct: (Product) -> Unit , // Ürün silme işlemi için fonksiyon
    private val databaseReference: DatabaseReference
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartItems[position]

        val productRef = databaseReference.child(item.id!!)
        productRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val updatedProduct = snapshot.getValue(Product::class.java)
                updatedProduct?.let {
                    holder.binding.productName.text = item.name
                    holder.binding.productPrice.text ="Fiyat: ${it.price} TL"
                    holder.binding.stockTextView.text = "Stok: ${it.stock}"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(holder.itemView.context,"Veri alınamadı: ${error.message}",Toast.LENGTH_SHORT).show()
            }
        })



        // Glide ile ürün resmini yükleme
        Glide.with(holder.itemView.context)
            .load(item.imageUrl)  // Firebase'den gelen resim URL'si
            .placeholder(R.drawable.no_photos)
            .into(holder.binding.productImage)

        // Mevcut miktarı güncelleme
        holder.binding.quantityEditText.setText(item.quantity.toString())

        // Azaltma butonunun işlevi
        holder.binding.decreaseTextView.setOnClickListener {
            if (item.quantity > 1) {
                item.quantity--
                holder.binding.quantityEditText.setText(item.quantity.toString())
                onQuantityChanged(position, item)
            }
        }
        // Artırma butonunun işlevi
        holder.binding.increaseTextView.setOnClickListener {
            if (item.quantity < item.stock!!) {  // Eğer miktar stok miktarından küçükse artırılabilir
                item.quantity++
                holder.binding.quantityEditText.setText(item.quantity.toString())
                onQuantityChanged(position, item)
            } else {
                Toast.makeText(holder.itemView.context, "Stok sınırına ulaşıldı", Toast.LENGTH_SHORT).show()
            }
        }

        // Kullanıcı, almak istediği miktarı doğrudan EditText'e yazacak
        holder.binding.quantityEditText.setText(item.quantity.toString())

        // Kullanıcı, miktarı girdiğinde bu değeri güncelle
        holder.binding.quantityEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val quantityText = holder.binding.quantityEditText.text.toString()
                val newQuantity = quantityText.toIntOrNull()
                if (newQuantity != null && newQuantity in 1..item.stock!!) {
                    item.quantity = newQuantity
                    holder.binding.quantityEditText.setText(item.quantity.toString())
                    onQuantityChanged(position, item)
                } else {
                    Toast.makeText(holder.itemView.context, "Geçersiz miktar", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Çöp kutusuna tıklama işlemi
        holder.binding.deleteProductImage.setOnClickListener {
            onDeleteProduct(item) // Ürünü silme işlemi
        }
    }

    override fun getItemCount(): Int = cartItems.size
}