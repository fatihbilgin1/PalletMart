package com.fatihbilgin.palletmart.adminpanel.urunyonetim

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.fatihbilgin.palletmart.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID

class AddProductadmin : Fragment() {

    private lateinit var productImageView: ImageView
    private lateinit var productAddPhoto: Button
    private lateinit var productNameEditText: EditText
    private lateinit var productPriceEditText: EditText
    private lateinit var productStockEditText: EditText
    private lateinit var productDescriptionText: EditText
    private lateinit var addProductButton: Button
    private lateinit var backButton: ImageView

    private var selectedImageUri: Uri? = null
    private lateinit var databaseReference: DatabaseReference

    // Yeni ActivityResultLauncher
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                selectedImageUri = it
                productImageView.setImageURI(it)  // Görseli ImageView'da göster
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databaseReference = FirebaseDatabase.getInstance().getReference("Products")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_productadmin, container, false)

        productImageView = view.findViewById(R.id.productImageView)
        productAddPhoto = view.findViewById(R.id.addPhotoButton)
        productNameEditText = view.findViewById(R.id.editProductName)
        productPriceEditText = view.findViewById(R.id.editProductPrice)
        productStockEditText = view.findViewById(R.id.editProductStock)
        productDescriptionText = view.findViewById(R.id.editProductDescription)
        addProductButton = view.findViewById(R.id.saveProductButton)
        backButton = view.findViewById(R.id.backButton)

        // Görsel eklemek için butona tıklama
        productAddPhoto.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // Ürünü kaydetmek için butona tıklama
        addProductButton.setOnClickListener {
            saveProduct()
        }

        // Geri gitmek için butona tıklama
        backButton.setOnClickListener {
            findNavController().navigate(R.id.action_addProductadmin_to_productManagement)
        }

        return view
    }

    private fun saveProduct() {
        val productName = productNameEditText.text.toString()
        val productPrice = productPriceEditText.text.toString().toDoubleOrNull()
        val productStock = productStockEditText.text.toString().toIntOrNull()
        val productDescription = productDescriptionText.text.toString()

        // Alanların boş olup olmadığını kontrol et
        if (productName.isBlank() || productDescription.isBlank() || productPrice == null || productStock == null || selectedImageUri == null) {
            Toast.makeText(requireContext(), "Tüm alanları doldurup fotoğraf seçiniz.", Toast.LENGTH_SHORT).show()
            return
        }

        // Görseli cihazda kaydet
        val savedImageUri = saveImageToDevice(requireContext(), selectedImageUri!!)

        savedImageUri?.let {
            val imageUrl = it.toString()  // Görselin URI'sini string olarak al

            val productId = databaseReference.push().key
            val product = Product(productId, productName, productDescription, productPrice, productStock, imageUrl)

            productId?.let {
                // Ürünü Firebase Realtime Database'e kaydet
                databaseReference.child(it).setValue(product)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(requireContext(), "Ürün başarıyla kaydedildi!", Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.action_addProductadmin_to_productManagement)
                        } else {
                            Toast.makeText(requireContext(), "Ürün kaydedilemedi: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } ?: run {
                Toast.makeText(requireContext(), "Ürün ID'si oluşturulamadı. Lütfen tekrar deneyin.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveImageToDevice(context: Context, imageUri: Uri): Uri? {
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
            val file = File(context.filesDir, UUID.randomUUID().toString() + ".jpg")
            val outputStream = FileOutputStream(file)

            inputStream?.copyTo(outputStream)
            outputStream.close()
            inputStream?.close()

            return Uri.fromFile(file)  // Görselin kaydedildiği URI'yi döndür
        } catch (e: Exception) {
            Log.e("AddProductAdmin", "Error saving image: ${e.message}")
            Toast.makeText(context, "Resim kaydedilemedi: ${e.message}", Toast.LENGTH_SHORT).show()
            return null
        }
    }
}