package com.example.dove.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dove.R
import com.example.dove.adapter.MessageAdapter
import com.example.dove.data.model.Chat
import com.example.dove.data.model.Message
import com.example.dove.data.model.User
import com.example.dove.databinding.FragmentPersonalChatBinding
import com.example.dove.ui.sign.LoginActivity
import com.example.dove.viewmodel.MessageViewModel
import com.example.dove.viewmodel.SharedViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale


class PersonalChatFragment : Fragment() {

    private lateinit var binding: FragmentPersonalChatBinding
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var database: FirebaseDatabase
    private var messageList = mutableListOf<Message>()
    private var currentUser: User? = null
    private var currentChat: Chat? = null
    private val messageViewModel: MessageViewModel by viewModels()
    private val messageAdapter by lazy { MessageAdapter(sharedViewModel) }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPersonalChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize database
        database = FirebaseDatabase.getInstance()
        // Get current user information
        currentUser = sharedViewModel.currentUser

        // Set up rvMessages
        binding.rvMessages.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = messageAdapter
        }

        // Get chat id
        val tmpChat = sharedViewModel.currentChat
        database.getReference("Chats").child(tmpChat?.id.toString()).get().addOnSuccessListener {
            sharedViewModel.currentChat = it.getValue(Chat::class.java)
            currentChat = sharedViewModel.currentChat
            // Get chat messages
            var tmpId = ""
            if (currentUser?.userid == currentChat?.user1Id) {
                tmpId = currentChat?.user2Id.toString()
            } else {
                tmpId = currentChat?.user1Id.toString()
            }
            messageList = currentChat?.chatMessages as MutableList<Message>
            messageViewModel.setMessages(messageList)
            messageViewModel.messages.observe(viewLifecycleOwner) {
                messageAdapter.setMessages(it)
            }
            database.getReference("Users").child(tmpId).get().addOnSuccessListener {
                val tmpUser = it.getValue(User::class.java)
                binding.tvName.text = tmpUser?.username
            }
        }

        val chatRef = database.getReference("Chats").child(tmpChat?.id.toString())

        // Attach a listener to read the data at our posts reference
        chatRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get the updated chat object
                val updatedChat = dataSnapshot.getValue(Chat::class.java)

                // Update the current chat in the shared view model
                sharedViewModel.currentChat = updatedChat

                // Update the current chat in the local variable
                currentChat = updatedChat

                // Get chat messages
                var tmpId = ""
                if (currentUser?.userid == currentChat?.user1Id) {
                    tmpId = currentChat?.user2Id.toString()
                } else {
                    tmpId = currentChat?.user1Id.toString()
                }
                messageList = currentChat?.chatMessages as MutableList<Message>
                messageViewModel.setMessages(messageList)
                messageViewModel.messages.observe(viewLifecycleOwner) {
                    messageAdapter.setMessages(it)
                }
                database.getReference("Users").child(tmpId).get().addOnSuccessListener {
                    val tmpUser = it.getValue(User::class.java)
                    binding.tvName.text = tmpUser?.username
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle possible errors.
                Log.d("loadChat:onCancelled", databaseError.toException().toString())
            }
        })


        // Set up send button
        binding.btnSend.setOnClickListener {
            val message = binding.etMessage.text.toString()
            val currentTime = Calendar.getInstance().time
            val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
            val time = formatter.format(currentTime)
            binding.etMessage.text.clear()
            val newMessage = Message(
                message,
                currentUser?.userid,
                time,
            )
            messageList.add(newMessage)
            messageViewModel.setMessages(messageList)
            messageViewModel.messages.observe(viewLifecycleOwner) {
                messageAdapter.setMessages(it)
            }
            database.getReference("Chats").child(currentChat?.id.toString()).child("chatMessages").setValue(messageList)
        }

        // Set up back button
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        // Set up more button to logout
        binding.btnMore.setOnClickListener {
            sharedViewModel.logout()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }
    }
}