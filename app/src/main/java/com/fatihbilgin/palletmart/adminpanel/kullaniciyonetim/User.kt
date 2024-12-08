package com.fatihbilgin.palletmart.adminpanel.kullaniciyonetim

data class User(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "",
    val phone: String = "",
    val photoUrl: String = ""
)