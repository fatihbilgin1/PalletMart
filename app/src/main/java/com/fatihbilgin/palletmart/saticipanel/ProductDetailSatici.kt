package com.fatihbilgin.palletmart.saticipanel

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fatihbilgin.palletmart.R
import com.fatihbilgin.palletmart.adminpanel.urunyonetim.Product
import com.fatihbilgin.palletmart.databinding.FragmentProductDetailSaticiBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso

class ProductDetailSatici : Fragment() {

    private lateinit var binding: FragmentProductDetailSaticiBinding
    private lateinit var databaseReference: DatabaseReference
    private var productId:String? = null
    private var sellerId:String? = null
    private var imageUrl:String? = null

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            // Seçilen fotoğrafı ImageView'a yükle
            binding.productImageUrl.setImageURI(it)
            imageUrl = it.toString()  // Seçilen fotoğrafın URI'sini güncelle
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductDetailSaticiBinding.inflate(inflater,container,false)

        val args: ProductDetailSaticiArgs by navArgs()
        val product = args.product
        productId = product.id
        sellerId = product.sellerId
        imageUrl = product.imageUrl

        binding.productNameEditText.setText(product.name)
        binding.productDescriptionEditText.setText(product.description)
        binding.productPriceEditText.setText(product.price.toString())
        binding.productStockEditText.setText(product.stock.toString())

        databaseReference = FirebaseDatabase.getInstance().getReference("Products")

        product.imageUrl?.let { imageUrl ->
            loadImage(Uri.parse(imageUrl))
        }
        binding.productImageUrl.setOnClickListener{
            pickImageLauncher.launch("image/*")
        }

        binding.deleteProductButton.setOnClickListener {
            deleteProduct()
        }
        binding.updateProductButton.setOnClickListener {
                updateProduct()
        }

        return binding.root
    }
    private fun updateProduct(){
        val name = binding.productNameEditText.text.toString().trim()
        val description = binding.productDescriptionEditText.text.toString().trim()
        val priceText = binding.productPriceEditText.text.toString().trim()
        val stockText = binding.productStockEditText.text.toString().trim()


        if (name.isEmpty() || description.isEmpty() || priceText.isEmpty() || stockText.isEmpty()){
            Toast.makeText(requireContext(),"Lütfen Tüm alanları Doldurun",Toast.LENGTH_SHORT).show()
            return
        }
        val price = priceText.toDoubleOrNull()
        val stock = stockText.toIntOrNull()

        if (price == null || stock == null){
            Toast.makeText(requireContext(),"Lütfen Geçerli bir fiyat ve stok adeti girin.",Toast.LENGTH_SHORT).show()
            return
        }


        val updatedProduct = Product(
            id = productId,
            name= name,
            description = description,
            price = price,
            stock = stock,
            imageUrl = imageUrl!!,
            sellerId = sellerId!!
        )

        productId?.let { id ->
            databaseReference.child(id).setValue(updatedProduct)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(),"Ürün Başarıyla Güncellendi",Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_productDetailSatici_to_saticiAnaEkran)
                }.addOnFailureListener {
                    Toast.makeText(requireContext(),"Güncelleme başarısız. Lütfen daha sonra tekrar deneyin.",Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun deleteProduct(){
        productId?.let{id ->
            databaseReference.child(id).removeValue()
                .addOnSuccessListener {
                    Toast.makeText(requireContext(),"Ürün Silindi",Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_productDetailSatici_to_saticiAnaEkran)
                }.addOnFailureListener {
                    Toast.makeText(requireContext(),"Ürün silinemedi. Lütfen Daha Sonra Tekrar Deneyin.",Toast.LENGTH_SHORT).show()
                }
        }
    }
    private fun loadImage(imageUri: Uri){
        try {
            Picasso.get().load(imageUri).into(binding.productImageUrl)
        }catch (e: Exception){
            Toast.makeText(requireContext(),"Görsel Yüklenemedi.",Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

}