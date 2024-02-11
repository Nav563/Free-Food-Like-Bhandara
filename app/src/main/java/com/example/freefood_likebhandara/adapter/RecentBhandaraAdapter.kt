package com.example.freefood_likebhandara.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.freefood_likebhandara.R
import com.example.freefood_likebhandara.model.BhandaraModel

class RecentBhandaraAdapter(
    var list: MutableList<BhandaraModel>,
    private val onItemClick: (BhandaraModel) -> Unit
) :
    RecyclerView.Adapter<RecentBhandaraAdapter.BhandaraViewHolder>() {


    inner class BhandaraViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img: ImageView = itemView.findViewById(R.id.img_bhandara)
        val bhandaraAddress: TextView = itemView.findViewById(R.id.tv_bhandara_address)
        val bhandaraDate: TextView = itemView.findViewById(R.id.tv_bhandara_date)
        val bhandaradetails: CardView = itemView.findViewById(R.id.bhandara_details)
        val bhandaraImg: ImageView = itemView.findViewById(R.id.img_bhandara)
        fun bind(item: BhandaraModel) {
            bhandaraAddress.text = item.bhandaraAddress
            bhandaraDate.text = item.bhandaraDate
            Glide.with(itemView.context).load(item.bhandaraImage).into(img)

            // Set click listener
            bhandaradetails.setOnClickListener { onItemClick.invoke(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BhandaraViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recent_bhandara_item, parent, false)
        return BhandaraViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: BhandaraViewHolder, position: Int) {
        //val data = list[position]
        holder.bind(list[position])


    }

    fun updateItems(items: List<BhandaraModel>) {
        list = items.toMutableList()
        notifyDataSetChanged()
    }
}