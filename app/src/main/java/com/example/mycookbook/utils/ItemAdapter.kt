package com.example.mycookbook.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.mycookbook.database.Receipt
import com.example.mycookbook.databinding.ListItemBinding

class ItemAdapter(
        private val receipts: List<Receipt>,
        private val receiptClickListener: ItemAdapter.ReceiptClickListener
) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    interface ReceiptClickListener {
        fun onClick()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding  = ListItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(receipts[position])
    }

    override fun getItemCount() = receipts.size

    inner class ViewHolder(private var binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(receipt: Receipt) {
            binding.receipt = receipt
            if(receipt.receiptImageUri1 != null) binding.imgReceipt.setImageURI(receipt.receiptImageUri1!!.toUri())
            binding.executePendingBindings()
            binding.cardView.setOnClickListener {
                //Launch intent for user to select photos
                receiptClickListener.onClick()
            }
        }
    }
}