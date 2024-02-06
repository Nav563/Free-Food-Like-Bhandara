package com.example.freefood_likebhandara.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.freefood_likebhandara.adapter.ChefAdapter
import com.example.freefood_likebhandara.adapter.RecentBhandaraAdapter
import com.example.freefood_likebhandara.databinding.ActivityBookChefBinding
import com.example.freefood_likebhandara.model.Chef
import com.example.freefood_likebhandara.viewmodel.ChefViewModel
import com.example.freefood_likebhandara.viewmodel.DailyBhandaraViewModel
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

class BookChefActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookChefBinding
    private lateinit var adapter: ChefAdapter
    private lateinit var chefViewModel: ChefViewModel

    @Inject
    lateinit var chefsCollection: CollectionReference
    private lateinit var recyclerView: RecyclerView
    //private lateinit var chefAdapter: ChefAdapter
    private lateinit var firestore: FirebaseFirestore

    private val chefList = mutableListOf<Chef>()

    private val coroutineScope = CoroutineScope(Job() + Dispatchers.Main)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookChefBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //adapter = ChefAdapter(chefList)
        initializeRecyclerView()
        binding.recyclerview.adapter = adapter

        firestore = FirebaseFirestore.getInstance()

        //chefAdapter = ChefAdapter(this,chefList)
        //binding.recyclerview.adapter = chefAdapter
        binding.recyclerview.layoutManager = LinearLayoutManager(this)
        // Retrieve the search query from the intent
        val searchQuery = intent.getStringExtra("SEARCH_QUERY")
        if (!searchQuery.isNullOrBlank()) {
            searchFirestore(searchQuery)
        }

        chefViewModel = ChefViewModel(Firebase.firestore.collection("Chefs"))
        lifecycleScope.launch { handlePosts() }
    }
    private fun initializeRecyclerView() {
        adapter = ChefAdapter(chefList) { selectedItem ->
            val intent = Intent(this, ChefDetailsActivity::class.java)
            // intent.putExtra("selected_item", selectedItem)

            val gson = Gson()
            val json = gson.toJson(selectedItem)
            intent.putExtra("selected_item", json)

            startActivity(intent)
        }
        binding.recyclerview.adapter = adapter
        binding.recyclerview.layoutManager = LinearLayoutManager(this)
    }
    private suspend fun handlePosts() {
        chefViewModel.fectChefs()
        chefViewModel.chefFlow.collectLatest {
            adapter.chefList = it.toMutableList()
            adapter.notifyDataSetChanged()
        }
    }
    private fun searchFirestore(query: String) {
        coroutineScope.launch {
            getSearchResults(query)
                .flowOn(Dispatchers.IO)
                .collect { chefs ->
                    chefList.clear()
                    chefList.addAll(chefs)
                    adapter.notifyDataSetChanged()
                }
        }
    }
    private fun getSearchResults(query: String): Flow<List<Chef>> = callbackFlow {
        val subscription = firestore.collection("Chefs")
            .whereEqualTo("chefName", query)
            //.whereGreaterThanOrEqualTo("chefName", query)
            //.whereLessThanOrEqualTo("chefName", query + "\uf8ff")
            //.orderBy("chefName")  // Ensure chefName is ordered for the query to work
            //.startAt(query)
            //.endAt(query + "\uf8ff")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                value?.let {
                    val chefs = it.documents.mapNotNull { doc ->
                        doc.toObject(Chef::class.java)
                    }
                    trySend(chefs).isSuccess
                }
            }

        awaitClose { subscription.remove() }
    }
    override fun onDestroy() {
        coroutineScope.cancel()
        super.onDestroy()
    }
}