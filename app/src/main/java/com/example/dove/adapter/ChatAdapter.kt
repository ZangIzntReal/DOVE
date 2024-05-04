package com.example.dove.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dove.R
import com.example.dove.data.model.Chat
import com.example.dove.data.model.User
import com.example.dove.viewmodel.SharedViewModel
import com.google.firebase.database.FirebaseDatabase

class ChatAdapter(private val sharedViewModel: SharedViewModel): RecyclerView.Adapter<ChatAdapter.ChatViewHolder>(){

    private val database = FirebaseDatabase.getInstance()

    private var listOfChats = listOf<Chat>()

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
        val imgAvatar = view.findViewById<ImageView>(R.id.imgAvatar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recent_chat, parent, false)
        return ChatViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listOfChats.size
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val tmpChat = listOfChats[position]
        database.getReference("Chats").child(tmpChat.id.toString()).get().addOnSuccessListener {
            val chat = it.getValue(Chat::class.java)!!
            var lastMessage = ""
            if (chat.chatMessages?.isNotEmpty() == true) {
                lastMessage = chat.chatMessages?.last()?.message.toString()
                // Do something with lastMessage
            }
            var tmpId: String? = ""
            if (chat.user1Id == sharedViewModel.currentUser?.userid) {
                tmpId = chat.user2Id
            } else {
                tmpId = chat.user1Id
            }
            if (tmpId != null) {
                Log.d("currentUser", "currentUser: ${sharedViewModel.currentUser?.userid}")
                Log.d("tmpId", "tmpID: $tmpId")
                database.getReference("Users").child(tmpId).get().addOnSuccessListener {
                    val tmpUser = it.getValue(User::class.java)
                    holder.apply {
                        tvName.text = tmpUser?.username
                        tvLastMessage.text = lastMessage
                        if (tmpUser?.imageUrl != "") {
                            Glide.with(holder.itemView.context.applicationContext)
                                .load(tmpUser?.imageUrl?.toUri())
                                .into(imgAvatar)
                        }
                    }
                    holder.itemView.setOnClickListener(View.OnClickListener {
                        onChatClick.onChatClick(position)
                    })
                }
            }
            else {
                holder.apply {
                    tvName.text = "Unknown"
                    tvLastMessage.text = lastMessage
                }
                holder.itemView.setOnClickListener(View.OnClickListener {
                    onChatClick.onChatClick(position)
                })
            }
        }
    }
}