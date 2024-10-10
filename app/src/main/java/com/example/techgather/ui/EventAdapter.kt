package com.example.techgather.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.techgather.data.event.ListEventsItem
import com.example.techgather.databinding.ItemEventBinding

class EventAdapter(
    private val onClick: (ListEventsItem) -> Unit
) : ListAdapter<ListEventsItem, EventAdapter.MyViewHolder>(DIFF_CALLBACK) {

    // ViewHolder untuk menampilkan item event
    class MyViewHolder(private val binding: ItemEventBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: ListEventsItem, onClick: (ListEventsItem) -> Unit) {
            binding.tvEventName.text = event.name
            binding.tvEventDescription.text = event.summary ?: "No description available"

            val imageUrl = event.imageLogo ?: event.mediaCover
            Glide.with(binding.root.context)
                .load(imageUrl)
                .into(binding.ivEventImage)

            binding.root.setOnClickListener {
                onClick(event)
            }
        }
    }

    // Fungsi untuk menginflate layout item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    // Mengikat data dengan ViewHolder
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event, onClick)
    }

    companion object {
        // DiffUtil untuk membandingkan item lama dan baru
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListEventsItem>() {
            override fun areItemsTheSame(oldItem: ListEventsItem, newItem: ListEventsItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ListEventsItem, newItem: ListEventsItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}