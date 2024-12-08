package com.fatihbilgin.palletmart.adminpanel.kullaniciyonetim

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.fatihbilgin.palletmart.R
import com.google.firebase.database.FirebaseDatabase

class addUseradmin : Fragment(R.layout.fragment_add_useradmin) {

    private var selectedImageUri: Uri? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // XML'deki görünümlerle bağlantı
        val backButton: ImageView = view.findViewById(R.id.backButton)
        val addUserPhotoButton: Button = view.findViewById(R.id.addUserPhotoButton)
        val saveUserButton: Button = view.findViewById(R.id.saveUserButton)
        val userNameEditText: EditText = view.findViewById(R.id.editUserName)
        val userEmailEditText: EditText = view.findViewById(R.id.editUserEmail)
        val userPhoneEditText: EditText = view.findViewById(R.id.editUserPhone)
        val userProfileImageView:ImageView = view.findViewById(R.id.userProfileImageView)

        // Fotoğraf Seçimi İçin Launcher
        val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                selectedImageUri = result.data?.data
                // Fotoğraf seçildiyse, "no photo" yerine seçilen fotoğrafı göster
                selectedImageUri?.let {
                    Glide.with(requireContext()).load(it).into(userProfileImageView)
                }
                Toast.makeText(requireContext(), "Fotoğraf seçildi.", Toast.LENGTH_SHORT).show()
            }
        }

        // Geri Dönüş Butonu
        backButton.setOnClickListener {
            findNavController().navigate(R.id.action_addUseradmin_to_userManagement)
        }

        // Fotoğraf Ekle Butonu
        addUserPhotoButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            pickImageLauncher.launch(intent)
        }

        // Kaydet Butonu
        saveUserButton.setOnClickListener {
            val userName = userNameEditText.text.toString().trim()
            val userEmail = userEmailEditText.text.toString().trim()
            val userPhone = userPhoneEditText.text.toString().trim()

            if (userName.isEmpty() || userEmail.isEmpty() || userPhone.isEmpty()) {
                Toast.makeText(requireContext(), "Lütfen tüm alanları doldurun.", Toast.LENGTH_SHORT).show()
            } else if (userPhone.length != 10) { // Örnek: Türkiye için 10 haneli telefon numarası
                Toast.makeText(requireContext(), "Telefon numarası 10 haneli olmalıdır.", Toast.LENGTH_SHORT).show()
            } else if (selectedImageUri == null) {
                Toast.makeText(requireContext(), "Lütfen bir fotoğraf seçin.", Toast.LENGTH_SHORT).show()
            } else {
                // Firebase'e Veri Kaydetme
                val databaseRef = FirebaseDatabase.getInstance().getReference("Users")
                val userId = databaseRef.push().key ?: ""

                val user = User(
                    userId = userId,
                    name = userName,
                    email = userEmail,
                    role = "Kullanıcı", // Varsayılan rol
                    phone = userPhone,
                    photoUrl = selectedImageUri.toString()
                )

                databaseRef.child(userId).setValue(user).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(requireContext(), "Kullanıcı başarıyla kaydedildi.", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_addUseradmin_to_userManagement)
                    } else {
                        Toast.makeText(requireContext(), "Kayıt başarısız oldu: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}