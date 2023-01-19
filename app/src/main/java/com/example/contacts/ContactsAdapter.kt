package com.example.contacts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.contacts.databinding.ItemsRowBinding

class ContactsAdapter(
    private val contacts: ArrayList<Contact>,
    private val updateListener: (id:Int) -> Unit,
    private val deleteListener: (id:Int) -> Unit

): RecyclerView.Adapter<ContactsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsViewHolder {
        return ContactsViewHolder(
            ItemsRowBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {
        val context = holder.itemView.context
        val contact = contacts[position]
        holder.tvName.text = contact.name
        holder.tvEmail.text = contact.email
        holder.tvPhone.text = contact.phone

        holder.ivEdit.setOnClickListener {
            updateListener(contact.id)
        }

        holder.ivDelete.setOnClickListener {
            deleteListener(contact.id)
        }
    }

    override fun getItemCount(): Int {
        return contacts.size
    }
}