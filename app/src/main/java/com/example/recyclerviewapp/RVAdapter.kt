package com.example.recyclerviewapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.recyclerviewapp.databinding.RvlayoutBinding

class RVAdapter(
    private val itemList: ArrayList<Trip>,
    private val listener: OnTripItemClickListener
) : RecyclerView.Adapter<RVAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(val binding: RvlayoutBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener, View.OnLongClickListener {

        init {
            binding.root.setOnClickListener(this)
            binding.root.setOnLongClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(itemList[position], position)
            }
        }

        override fun onLongClick(v: View?): Boolean {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemLongClick(itemList[position], position)
                return true
            }
            return false
        }
    }

    fun showDeleteConfirmationDialog(context: Context, position: Int) {
        AlertDialog.Builder(context)
            .setTitle("Delete Item")
            .setMessage("Are you sure you want to delete this item?")
            .setPositiveButton("Yes") { dialog, _ ->
                itemList.removeAt(position)
                notifyItemRemoved(position)
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = RvlayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = itemList[position]
        holder.binding.tvfromplace.text = item.from
        holder.binding.tvtoplace.text = item.to
        holder.binding.statustv.text = item.status.toString()
        holder.binding.tvdistance.text = item.distance
        holder.binding.tvtime.text = item.duration
        holder.binding.tvdate.text = item.date
        holder.binding.tvstation.text = item.stations

        when (item.status) {
            TripStatus.SUCCESSFUL -> holder.binding.statustv.setTextColor(
                ContextCompat.getColor(holder.binding.root.context, R.color.status_successful)
            )
            TripStatus.DELAYED -> holder.binding.statustv.setTextColor(
                ContextCompat.getColor(holder.binding.root.context, R.color.status_delayed)
            )
            TripStatus.CANCELLED -> holder.binding.statustv.setTextColor(
                ContextCompat.getColor(holder.binding.root.context, R.color.status_cancelled)
            )
        }
    }

    override fun getItemCount() = itemList.size

    interface OnTripItemClickListener {
        fun onItemClick(trip: Trip, position: Int)
        fun onItemLongClick(trip: Trip, position: Int)
    }
}
