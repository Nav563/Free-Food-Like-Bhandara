package com.example.freefood_likebhandara.model

import com.google.firebase.Timestamp
import java.io.Serializable

data class BhandaraModel(
    val bhandaraImage: String = "",
    val bhandaraAddress: String? = "",
    val bhandaraLocation: String = "",
    val bhandaraDate: String = "",
    val postDate: String = "",
    val postedByID: String = "",
    val postedByName: String = "",
    val bhandaraDay: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val zoom: Float = DEFAULT_ZOOM_LEVEL
) : Serializable {
    companion object {
        const val DEFAULT_ZOOM_LEVEL: Float = 15f
    }
}