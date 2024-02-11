package com.example.freefood_likebhandara.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.freefood_likebhandara.databinding.PostLayoutItemBinding
import com.example.freefood_likebhandara.model.PostModel
import com.google.firebase.auth.FirebaseAuth

private lateinit var firebaseAuth: FirebaseAuth

class PostAdapter(val context: Context, var list: MutableList<PostModel>) :
    RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    inner class PostViewHolder(val binding: PostLayoutItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = PostLayoutItemBinding.inflate(LayoutInflater.from(context), parent, false)

        firebaseAuth = FirebaseAuth.getInstance()
        return PostViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val data = list[position]

        Glide.with(context).load(data.postImage).into(holder.binding.postImg)
        holder.binding.txtPostDescription.text = data.postDescription.toString()
        holder.binding.tvUserName.text = data.postedByID
        holder.binding.tvName.text = data.postedByName
        holder.binding.tvPostTime.text = data.postDate

    }
}