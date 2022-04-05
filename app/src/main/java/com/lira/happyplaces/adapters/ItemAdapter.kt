package com.lira.happyplaces.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.lira.happyplaces.database.HappyPlaceEntity
import com.lira.happyplaces.databinding.ItemHappyPlaceBinding

class ItemAdapter(private val items: ArrayList<HappyPlaceEntity>,
                  private val detailListener:(id:Int) -> Unit,
                  private val updateListener:(id:Int) -> Unit,
                  private val deleteListener:(id:Int) -> Unit): RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    private val viewBinderHelper: ViewBinderHelper = ViewBinderHelper()

    class ViewHolder(binding: ItemHappyPlaceBinding): RecyclerView.ViewHolder(binding.root){
        val ivPlaceImage = binding.ivPlaceImage
        val tvTitle = binding.tvTitle
        val tvDescription = binding.tvDescription
        val swipeRevealLayout = binding.swipeRevealLayout
        val ibEdit = binding.ibEdit
        val ibDelete = binding.ibDelete
        val cvMain = binding.cvMain
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemHappyPlaceBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.ivPlaceImage.setImageURI(Uri.parse(item.image))
        holder.tvTitle.text = item.title
        holder.tvDescription.text = item.description

        viewBinderHelper.setOpenOnlyOne(true)
        viewBinderHelper.bind(holder.swipeRevealLayout, item.id.toString())
        viewBinderHelper.closeLayout(item.id.toString())

        holder.cvMain.setOnClickListener{
            detailListener.invoke(item.id)
        }

        holder.ibEdit.setOnClickListener {
            updateListener.invoke(item.id)
        }

        holder.ibDelete.setOnClickListener {
            deleteListener.invoke(item.id)
        }

    }

    override fun getItemCount(): Int {
        return items.size
    }

}