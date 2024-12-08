package com.fatihbilgin.palletmart.adminpanel.urunyonetim

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fatihbilgin.palletmart.R
import com.fatihbilgin.palletmart.databinding.FragmentProductDetailadminBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class ProductDetailadmin : Fragment() {

    private lateinit var binding: FragmentProductDetailadminBinding
    private lateinit var databaseReference: DatabaseReference
    private var isApproved = false // Onay durumu kontrolü

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductDetailadminBinding.inflate(inflater, container, false)

        // Safe Args ile gelen argümanları al
        val args: ProductDetailadminArgs by navArgs()
        val product = args.product

        // TextView'lere verileri ata
        binding.productNameTextView.text = product.name
        binding.productDescriptionTextView.text = product.description
        binding.productPriceTextView.text = "₺${product.price}"
        binding.productStockTextView.text = "Stok: ${product.stock}"

        databaseReference = FirebaseDatabase.getInstance().getReference("Products")

        // Ürün ID'sine göre veriyi çek
        product.id?.let {
            // Onay durumunu kontrol et
            databaseReference.child(it).child("approved").get().addOnSuccessListener { snapshot ->
                val approved = snapshot.getValue(Boolean::class.java) ?: false
                isApproved = approved
                updateApproveButtonState(approved)
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Onay durumu alınamadı.", Toast.LENGTH_SHORT).show()
            }

            // Görsel URI'sini çek ve yükle
            databaseReference.child(it).child("imageUrl").get()
                .addOnSuccessListener { snapshot ->
                    val imageUriString = snapshot.getValue(String::class.java)
                    if (!imageUriString.isNullOrEmpty()) {
                        val imageUri = Uri.parse(imageUriString)
                        loadImage(imageUri) // URI ile görseli yükle
                    } else {
                        Toast.makeText(requireContext(), "Görsel URI'si geçersiz.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Görsel yüklenemedi.", Toast.LENGTH_SHORT).show()
                }
        }

        // Onayla butonuna tıklanırsa
        binding.approveButton.setOnClickListener {
            if (!isApproved) {
                approveProduct(product.id)
                isApproved = true
                updateApproveButtonState(true)
            } else {
                Toast.makeText(requireContext(), "Ürün zaten onaylanmış.", Toast.LENGTH_SHORT).show()
            }
        }

        // Geri butonuna tıklanırsa
        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_ProductDetailadmin_to_ProductManagement)
        }

        // Güncelle butonuna tıklanırsa
        binding.updateProductButton.setOnClickListener {
            val action = ProductDetailadminDirections.actionProductDetailadminToAdminProductEdit(product)
            findNavController().navigate(action)
        }

        // Silme butonu işlevi
        binding.deleteProductButton.setOnClickListener {
            deleteProduct(product.id)
        }

        return binding.root
    }

    // Ürünü onayla
    private fun approveProduct(productId: String?) {
        productId?.let {
            databaseReference.child(it).child("approved").setValue(true)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Ürün onaylandı.", Toast.LENGTH_SHORT).show()
                    isApproved = true // Onay durumu güncelleniyor
                    updateApproveButtonState(true)
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Onaylama işlemi başarısız. Lütfen tekrar deneyin.", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // Ürünü sil
    private fun deleteProduct(productId: String?) {
        productId?.let { productId ->
            databaseReference.child(productId).removeValue()
                .addOnSuccessListener {
                    val cartRef = FirebaseDatabase.getInstance().getReference("Cart")
                    cartRef.addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (userSnapshot in snapshot.children){
                                val userCartRef = userSnapshot.ref
                                userCartRef.child(productId).removeValue()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(requireContext(),"Sepet Güncellenirken Hata oluştu.",Toast.LENGTH_SHORT).show()
                        }
                    })

                    Toast.makeText(requireContext(), "Ürün silindi.", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp() // Silme işleminden sonra geri dön
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Silme işlemi başarısız. Lütfen tekrar deneyin.", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // Onayla butonunun durumunu güncelle
    private fun updateApproveButtonState(approved: Boolean) {
        if (approved) {
            binding.approveButton.isEnabled = false
            binding.approveButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.text_color))
            binding.approveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        } else {
            binding.approveButton.isEnabled = true
            binding.approveButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.yesil))
            binding.approveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        }
    }

    // URI ile görseli yükle
    private fun loadImage(imageUri: Uri) {
        try {
            Picasso.get().load(imageUri).into(binding.productImageView)
        } catch (e: Exception) {
            // Hata durumunda anlamlı bir mesaj göster
            Toast.makeText(requireContext(), "Görsel yüklenemedi: Lütfen tekrar deneyin.", Toast.LENGTH_SHORT).show()
            e.printStackTrace()  // Hata loglama
        }
    }
}