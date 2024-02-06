package com.example.freefood_likebhandara.viewmodel

import com.example.freefood_likebhandara.model.Chef
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ChefViewModel(private val chefCollection: CollectionReference) {
    val chefFlow = MutableSharedFlow<List<Chef>>()

    @OptIn(DelicateCoroutinesApi::class)
    fun fectChefs() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val querySnapshot: QuerySnapshot = chefCollection.get().await()
                val chefs = querySnapshot.toObjects<Chef>()
                chefFlow.emit(chefs)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}