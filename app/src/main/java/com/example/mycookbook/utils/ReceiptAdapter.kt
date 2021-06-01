package com.example.mycookbook.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mycookbook.database.Receipt
import com.example.mycookbook.databinding.ListItemBinding

class ReceiptAdapter(val clickListener: ReceiptClickListener): ListAdapter<Receipt, ReceiptAdapter.ReceiptViewHolder>(DiffCallback){

    class ReceiptViewHolder(private var binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        //val img: ImageView =binding.imageReceiptTitleId
        //val title: TextView = binding.textViewReceiptTitleId
        fun bind(receipt: Receipt, clickListener: ReceiptClickListener) {
            binding.receipt = receipt
            if(receipt.receiptImageUri1 != null) binding.imgReceipt.setImageURI(receipt.receiptImageUri1!!.toUri())
            binding.clickListener = clickListener
            binding.executePendingBindings()

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding  = ListItemBinding.inflate(layoutInflater, parent, false)
        return ReceiptViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReceiptViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, clickListener)
    }

    class ReceiptClickListener(val clickListener: (receipt: Receipt) -> Unit) {
        fun onClick(receipt: Receipt) = clickListener(receipt)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Receipt>() {
        override fun areItemsTheSame(oldItem: Receipt, newItem: Receipt): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Receipt, newItem: Receipt): Boolean {
            return oldItem.receiptId == newItem.receiptId
        }
    }
}