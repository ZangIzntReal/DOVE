package com.example.dove.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dove.R
import com.example.dove.data.model.Chat
import com.example.dove.data.model.User
import com.example.dove.viewmodel.SharedViewModel
import com.google.firebase.database.FirebaseDatabase

class ChatAdapter: RecyclerView.Adapter<ChatAdapter.ChatViewHolder>(){

    private val database = FirebaseDatabase.getInstance()

    private var listOfChats = listOf<Chat>()
    private val sharedViewModel = SharedViewModel()

    interface OnChatClick {
        fun onChatClick(position: Int)
    }

    lateinit var onChatClick: OnChatClick

    fun getChats(): List<Chat> {
        return listOfChats
    }

    fun setChats(chats: List<Chat>) {
        this.listOfChats = chats
        notifyDataSetChanged()
    }
    class ChatViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val tvName = view.findViewById<TextView>(R.id.tvName)
        val tvLastMessage = view.findViewById<TextView>(R.id.tvLastMessage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recent_chat, parent, false)
        return ChatViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listOfChats.size
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = listOfChats[position]
        val lastMessage = chat.chatMessages?.last()
        var tmpId: String? = ""
        if (chat.user1Id == sharedViewModel.currentUser?.userid) {
            tmpId = chat.user2Id
        } else {
            tmpId = chat.user1Id
        }
        if (tmpId != null) {
            database.getReference("Users").child(tmpId).get().addOnSuccessListener {
                val tmpUser = it.getValue(User::class.java)
                holder.apply {
                    tvName.text = tmpUser?.username
                    tvLastMessage.text = lastMessage?.message
                }
                holder.itemView.setOnClickListener(View.OnClickListener {
                    onChatClick.onChatClick(position)
                })
            }
        }
        else {
            holder.apply {
                tvName.text = "Unknown"
                tvLastMessage.text = lastMessage?.message
            }
            holder.itemView.setOnClickListener(View.OnClickListener {
                onChatClick.onChatClick(position)
            })
        }
    }
}