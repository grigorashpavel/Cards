package com.pasha.profile.internal.presentation

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pasha.profile.databinding.ItemSessionLayoutBinding
import com.pasha.profile.internal.domain.repositories.models.Device

class SessionRecyclerViewAdapter :
    RecyclerView.Adapter<SessionRecyclerViewAdapter.ItemViewHolder>() {
    private var devices: List<Device> = listOf()

    class ItemViewHolder(val binding: ItemSessionLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemSessionLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ItemViewHolder(binding)
    }



    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = devices[position]
        holder.binding.tvDeviceName.text = item.deviceName
        holder.binding.tvDeviceId.text = item.deviceId
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(devices: List<Device>) {
        this.devices = devices
        notifyDataSetChanged()
    }

    override fun getItemCount() = devices.size

}