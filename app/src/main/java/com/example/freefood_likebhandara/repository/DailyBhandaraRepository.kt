package com.example.freefood_likebhandara.repository

import com.example.freefood_likebhandara.model.BhandaraModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.util.Date

class DailyBhandaraRepository {

    suspend fun getBhandaraData(startDate: Date, endDate: Date): List<BhandaraModel> {
        return Firebase.firestore.collection("Bhandara")
            .whereGreaterThanOrEqualTo("timestamp", Timestamp(startDate))
            .whereLessThanOrEqualTo("timestamp", Timestamp(endDate))
            .get()
            .await()
            .toObjects(BhandaraModel::class.java)
    }
}