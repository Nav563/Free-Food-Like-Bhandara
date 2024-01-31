package com.example.freefood_likebhandara.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.freefood_likebhandara.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserViewModel: ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val userCollection = db.collection("Users")
    private val firebaseAuth : FirebaseAuth = FirebaseAuth.getInstance()

    val currentUserLiveData = MutableLiveData<UserModel?>()
    @OptIn(DelicateCoroutinesApi::class)
    fun fetchCurrentUsers() {
        val userId = firebaseAuth.currentUser?.uid
        if (userId != null) {
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val documentSnapshot = userCollection.document(userId).get().await()
                    if (documentSnapshot.exists()) {
                        val currentUser = documentSnapshot.toObject(UserModel::class.java)
                        currentUserLiveData.postValue(currentUser!!)
                    } else {
                        // Handle case where user document does not exist
                        currentUserLiveData.postValue(null)
                    }
                } catch (e: Exception) {
                    // Handle error
                    e.printStackTrace()
                    currentUserLiveData.postValue(null)
                }
            }
        }
    }
}