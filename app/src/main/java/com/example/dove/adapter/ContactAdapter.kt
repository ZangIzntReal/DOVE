package com.example.dove.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dove.R
import com.example.dove.data.model.Contact
import com.google.firebase.database.FirebaseDatabase



class ContactAdapter: RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    private lateinit var database: FirebaseDatabase

    private var listOfContact = listOf<Contact>()

    interface OnContactClick {
        fun onContactClick(position: Int)
    }

    // biến lưu trữ hàm xử lý sự kiện
    lateinit var onContactClick: OnContactClick

    fun setContacts(contacts: List<Contact>) {
        this.listOfContact = contacts
        notifyDataSetChanged()
    }
    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName = itemView.findViewById<TextView>(R.id.tvName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val inflate = LayoutInflater.from(parent.context)
        val view = inflate.inflate(R.layout.contact_item, parent, false)
        return ContactViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listOfContact.size
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = listOfContact[position]
        holder.apply {
            tvName.text = contact.email
        }
        holder.itemView.setOnClickListener(View.OnClickListener {
            onContactClick.onContactClick(position)
        })
    }
}