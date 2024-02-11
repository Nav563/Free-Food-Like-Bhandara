package com.example.freefood_likebhandara.fragment

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.freefood_likebhandara.activities.CreatePostActivity
import com.example.freefood_likebhandara.adapter.PostAdapter
import com.example.freefood_likebhandara.databinding.FragmentCommunityBinding
import com.example.freefood_likebhandara.viewmodel.PostViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


class CommunityFragment : Fragment() {

    private lateinit var binding: FragmentCommunityBinding
    private lateinit var adapter: PostAdapter
    private lateinit var postViewModel: PostViewModel

    @Inject
    lateinit var postsCollection: CollectionReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCommunityBinding.inflate(layoutInflater)
        adapter = PostAdapter(requireContext(), mutableListOf())
        binding.postRecycler.adapter = adapter


        postViewModel = PostViewModel(Firebase.firestore.collection("Posts"))
        lifecycleScope.launch { handlePosts() }
        binding.btnAddPost.setOnClickListener {
            startActivity(Intent(requireContext(), CreatePostActivity::class.java))
        }

        return binding.root
    }

    private suspend fun handlePosts() {
        postViewModel.fetchPosts()
        postViewModel.postsFlow.collectLatest {
            adapter.list = it.toMutableList()
            adapter.notifyDataSetChanged()
        }
    }
}

