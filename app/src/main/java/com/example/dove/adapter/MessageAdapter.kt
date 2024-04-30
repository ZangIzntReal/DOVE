package com.example.dove.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dove.R
import com.example.dove.data.model.Message
import com.example.dove.viewmodel.SharedViewModel
import com.google.firebase.database.FirebaseDatabase

class MessageAdapter(private val sharedViewModel: SharedViewModel): RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    private var listOfMessages = listOf<Message>()

    private val TEXT_LEFT = 0
    private val TEXT_RIGHT = 1
    fun setMessages(messages: List<Message>){
        this.listOfMessages = messages
        notifyDataSetChanged()
    }

    class MessageViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val tvMessage = view.findViewById<TextView>(R.id.tvMessage)
        val tvTime = view.findViewById<TextView>(R.id.tvTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val inflate = LayoutInflater.from(parent.context)
        if (viewType == TEXT_RIGHT) {
            val view = inflate.inflate(R.layout.message_sender, parent, false)
            return MessageViewHolder(view)
        } else {
            val view = inflate.inflate(R.layout.message_received, parent, false)
            return MessageViewHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (listOfMessages[position].sender == sharedViewModel.currentUser?.userid) {
            TEXT_RIGHT
        } else {
            TEXT_LEFT
        }
    }

    override fun getItemCount(): Int {
        return listOfMessages.size
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = listOfMessages[position]
        holder.apply {
            tvMessage.text = message.message
            tvTime.text = message.time.toString()
        }
    }
}