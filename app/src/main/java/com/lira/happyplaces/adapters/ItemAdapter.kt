package com.lira.happyplaces.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lira.happyplaces.database.HappyPlaceEntity
import com.lira.happyplaces.databinding.ItemHappyPlaceBinding

class ItemAdapter(private val items: ArrayList<HappyPlaceEntity>): RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    class ViewHolder(binding: ItemHappyPlaceBinding): RecyclerView.ViewHolder(binding.root){
        val ivPlaceImage = binding.ivPlaceImage
        val tvTitle = binding.tvTitle
        val tvDescription = binding.tvDescription
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemHappyPlaceBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.ivPlaceImage.setImageURI(Uri.parse(item.image))
        holder.tvTitle.text = item.title
        holder.tvDescription.text = item.description
    }

    override fun getItemCount(): Int {
        return items.size
    }

}