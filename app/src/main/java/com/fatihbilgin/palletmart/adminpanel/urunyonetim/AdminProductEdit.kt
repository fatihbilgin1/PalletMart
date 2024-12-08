package com.fatihbilgin.palletmart.adminpanel.urunyonetim

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.fatihbilgin.palletmart.R
import com.fatihbilgin.palletmart.databinding.FragmentAdminProductEditBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AdminProductEdit : Fragment() {

    private lateinit var binding: FragmentAdminProductEditBinding
    private lateinit var databaseReference: DatabaseReference
    private var productId: String? = null
    private var imageUri: Uri? = null

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                imageUri = it
                binding.productImageView.setImageURI(uri)  // Seçilen görseli ImageView'da göster
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // View Binding ile layout'u bağla
        binding = FragmentAdminProductEditBinding.inflate(inflater, container, false)

        // Firebase Refs
        databaseReference = FirebaseDatabase.getInstance().getReference("Products")

        // Safe Args ile gelen argümanları al
        val args: AdminProductEditArgs by navArgs()  // Eğer Safe Args kullanıyorsanız
        val product = args.product  // Argümanı al

        // Ürün ID'sini al
        productId = product.id

        // Glide ile mevcut görseli yükle
        Glide.with(requireContext())
            .load(product.imageUrl)  // Firebase'den alınan cihazdaki URL
            .into(binding.productImageView)

        // EditText'lere mevcut verileri ata
        binding.editProductName.setText(product.name)
        binding.editProductPrice.setText(product.price.toString())
        binding.editProductStock.setText(product.stock.toString())
        binding.editProductDescription.setText(product.description)

        // Fotoğraf seçme işlemi
        binding.productImageView.setOnClickListener {
            pickImageLauncher.launch("image/*")  // Resim seçme için galeriyi aç
        }

        // Kaydet butonuna tıklanırsa
        binding.saveProductButton.setOnClickListener {
            val name = binding.editProductName.text.toString().trim()
            val description = binding.editProductDescription.text.toString().trim()
            val priceString = binding.editProductPrice.text.toString().trim()
            val stockString = binding.editProductStock.text.toString().trim()

            // Boş alan kontrolü
            if (name.isEmpty() || description.isEmpty() || priceString.isEmpty() || stockString.isEmpty()) {
                Toast.makeText(requireContext(), "Lütfen tüm alanları doldurun.", Toast.LENGTH_SHORT).show()
            } else {
                // price'ı Double? türüne dönüştürme (null olmasına izin veriyoruz)
                val price = priceString.toDoubleOrNull()  // Eğer geçerli bir sayı değilse null döner

                // stock'u Int? türüne dönüştürme (null olmasına izin veriyoruz)
                val stock = stockString.toIntOrNull()  // Eğer geçerli bir sayı değilse null döner

                // Eğer price ya da stock null olduysa, varsayılan değer atayabiliriz
                val finalPrice = price ?: 0.0  // price null ise 0.0 olarak ayarlanır
                val finalStock = stock ?: 0  // stock null ise 0 olarak ayarlanır

                // Görsel URL'si
                val imageUrl = imageUri?.toString() ?: product.imageUrl // Eğer yeni bir görsel seçilmemişse, mevcut URL'yi kullan

                // Yeni bir Product nesnesi oluştur
                val updatedProduct = Product(
                    name = name,
                    description = description,
                    price = finalPrice,  // Price artık Double?
                    stock = finalStock,  // Stock artık Int?
                    imageUrl = imageUrl
                )

                saveProduct(updatedProduct)  // Ürünü kaydet
            }
        }

        return binding.root
    }

    private fun saveProduct(product: Product) {
        val productRef = databaseReference.child(productId ?: return)

        val updatedProduct = mutableMapOf<String, Any>()

        // Değişen alanları güncelle
        if (product.name.isNotEmpty()) updatedProduct["name"] = product.name
        if (product.description.isNotEmpty()) updatedProduct["description"] = product.description
        if (product.price != null) updatedProduct["price"] = product.price
        if (product.stock != null) updatedProduct["stock"] = product.stock

        // Eğer yeni bir görsel seçilmişse, URL'yi kaydet
        if (product.imageUrl.isNotEmpty()) {
            updatedProduct["imageUrl"] = product.imageUrl // URL kaydedilir
        }

        // Firebase'e kaydet
        productRef.updateChildren(updatedProduct)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Ürün başarıyla güncellendi.", Toast.LENGTH_SHORT).show()
                requireActivity().onBackPressed()  // Geri dön
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Ürün güncellenemedi.", Toast.LENGTH_SHORT).show()
            }
    }
}