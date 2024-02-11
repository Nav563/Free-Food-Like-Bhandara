package com.example.freefood_likebhandara.model

import com.google.firebase.Timestamp

data class PostModel(
    val postImage: String = "",
    val postDescription: String? = "",
    val timestamp: Timestamp = Timestamp.now(),
    val postDate: String = "",
    val postedByID: String = "",
    val postedByName: String = "",
    val phone: String = "",
    val canPublicContact: Boolean = false
)