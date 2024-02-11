package com.example.freefood_likebhandara.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.freefood_likebhandara.databinding.ItemSliderBinding

class ImagePagerAdapter(private val imageUrls: List<String>) : RecyclerView.Adapter<ImagePagerAdapter.ViewHolder>() {



     // This method returns our layout
     override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
         //val view = LayoutInflater.from(context).inflate(R.layout.item_slider, parent, false)
         val binding = ItemSliderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
         return ViewHolder(binding)
     }

     // This method binds the screen with the view
     override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageUrls = imageUrls[position]
         holder.binding.pagerImage.setImageResource(imageUrls[position].code)
         //Glide.with(context).load(data.postImage).into(holder.binding.pagerImage)
     }

     // This Method returns the size of the Array
     override fun getItemCount(): Int {
         return imageUrls.size
     }

     // The ViewHolder class holds the view
     class ViewHolder(val binding: ItemSliderBinding) : RecyclerView.ViewHolder(binding.root)
}