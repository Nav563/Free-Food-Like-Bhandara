package com.example.freefood_likebhandara.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.freefood_likebhandara.R
import com.example.freefood_likebhandara.model.Chef

class ChefAdapter(var chefList: MutableList<Chef>, private val onItemClick: (Chef) -> Unit) :
    RecyclerView.Adapter<ChefAdapter.ChefViewHolder>() {


    inner class ChefViewHolder(itemView : View) :
        RecyclerView.ViewHolder(itemView){
        val img: ImageView = itemView.findViewById(R.id.chef_img)
        val chefName: TextView = itemView.findViewById(R.id.tv_name)
        val chefMobile: TextView = itemView.findViewById(R.id.tv_mobile)
        val chefAddress: TextView = itemView.findViewById(R.id.tv_address)
        val bookChef: Button = itemView.findViewById(R.id.book_chef)
        fun bind(item: Chef) {
            chefName.text = item.chefName
            chefMobile.text = item.chefMobile
            chefAddress.text = item.chefAddress
            Glide.with(itemView.context).load(item.chefImage).into(img)

            // Set click listener
            bookChef.setOnClickListener { onItemClick.invoke(item) }
        }
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChefViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.chef_item, parent, false)

        return ChefViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return chefList.size
    }

    override fun onBindViewHolder(holder: ChefViewHolder, position: Int) {
        holder.bind(chefList[position])
    }
    fun updateItems(items: List<Chef>) {
        chefList = items.toMutableList()
        notifyDataSetChanged()
    }
}