package com.example.contacts

import androidx.recyclerview.widget.RecyclerView
import com.example.contacts.databinding.ItemsRowBinding

class ContactsViewHolder(binding: ItemsRowBinding): RecyclerView.ViewHolder(binding.root) {
    val llMain = binding.llMain
    val tvName = binding.tvName
    val tvEmail = binding.tvEmail
    val tvPhone = binding.tvPhone
    val ivEdit = binding.ivEdit
    val ivDelete = binding.ivDelete
}
