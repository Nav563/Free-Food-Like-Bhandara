package com.example.freefood_likebhandara.model

data class UserModel(
    val userImage: String? = "",
    val name: String = "",
    val mobile: String = "",
    val email: String = "",
    val password: String = "",
    val punyaCoins: Int? = 0,
)
