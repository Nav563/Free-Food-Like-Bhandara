package com.example.freefood_likebhandara.viewmodel

import com.example.freefood_likebhandara.model.PostModel
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PostViewModel(private val postCollection: CollectionReference) {
    val postsFlow = MutableSharedFlow<List<PostModel>>()

    @OptIn(DelicateCoroutinesApi::class)
    fun fetchPosts() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val querySnapshot : QuerySnapshot = postCollection.get().await()
                val posts = querySnapshot.toObjects<PostModel>()
                postsFlow.emit(posts)
            }
            catch (e: Exception){
                e.printStackTrace()
            }
        }
    }
}