package com.example.contentproviderdemo

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.contentproviderdemo.databinding.ContactItemBinding

class ContactAdapter : Adapter<ContactViewHolder>() {
    private var data = ArrayList<ContactModel>()
    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        context = parent.context
        val binding =
            ContactItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContactViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.binding.contactName.text = data[position].name
        holder.binding.contactNumber.text = data[position].number
    }

    fun setData(newData: ArrayList<ContactModel>) {
        val contactDiffUtil = ContactDiffUtil(data, newData)
        val contactDiff = DiffUtil.calculateDiff(contactDiffUtil)
        data = newData
        contactDiff.dispatchUpdatesTo(this)

    }

    class ContactDiffUtil(
        private val oldData: ArrayList<ContactModel>,
        private val newData: ArrayList<ContactModel>
    ) :
        DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return oldData.size
        }

        override fun getNewListSize(): Int {
            return newData.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldData[oldItemPosition] == newData[newItemPosition]
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldData[oldItemPosition] == newData[newItemPosition]
        }

    }
}