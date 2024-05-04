package com.example.dove.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dove.R
import com.example.dove.data.model.Contact
import com.example.dove.data.model.User
import com.google.firebase.database.FirebaseDatabase



class ContactAdapter: RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    private var listOfContact = listOf<Contact>()

    private val database = FirebaseDatabase.getInstance()

    interface OnContactClick {
        fun onContactClick(position: Int)
    }

    // biến lưu trữ hàm xử lý sự kiện
    lateinit var onContactClick: OnContactClick

    fun getContacts(): List<Contact> {
        return listOfContact
    }

    fun setContacts(contacts: List<Contact>) {
        this.listOfContact = contacts
        notifyDataSetChanged()
    }
    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName = itemView.findViewById<TextView>(R.id.tvName)
        val imgAvatar = itemView.findViewById<ImageView>(R.id.imgAvatar)
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
        database.getReference("Users").child(contact.id.toString()).get().addOnSuccessListener {
            val user = it.getValue(User::class.java)!!
            holder.apply {
                tvName.text = user.email.toString()
                if (user.imageUrl!!.isNotEmpty()) {
                    Glide.with(holder.itemView.context.applicationContext)
                        .load(user.imageUrl?.toUri())
                        .into(imgAvatar)
                }
            }
        }
        holder.itemView.setOnClickListener(View.OnClickListener {
            onContactClick.onContactClick(position)
        })
    }
}