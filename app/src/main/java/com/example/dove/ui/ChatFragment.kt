package com.example.dove.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dove.R
import com.example.dove.adapter.ChatAdapter
import com.example.dove.adapter.ContactAdapter
import com.example.dove.data.model.Chat
import com.example.dove.data.model.Contact
import com.example.dove.data.model.User
import com.example.dove.databinding.FragmentChatBinding
import com.example.dove.viewmodel.ChatViewModel
import com.example.dove.viewmodel.ContactViewModel
import com.example.dove.viewmodel.SharedViewModel
import com.google.firebase.database.FirebaseDatabase


class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding
    private val chatAdapter = ChatAdapter()
    private val chatViewModel: ChatViewModel by viewModels()
    private var chatList = mutableListOf<Chat>()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var currentUser: User? = null
    private lateinit var database: FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get current user information
        currentUser = sharedViewModel.currentUser

        // Initialize database
        database = FirebaseDatabase.getInstance()

        // Set up rvContacts
        binding.rvChats.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = chatAdapter
        }

        // Get chats from current user
        chatList = currentUser?.chats as MutableList<Chat>
        // Set chats to ChatViewModel
        chatViewModel.setChats(chatList)

        // Observe LiveData chats from ChatViewModel
        chatViewModel.chats.observe(viewLifecycleOwner) {
            // Update chats in adapter when data changes
            chatAdapter.setChats(it)
        }

        // Set up Listener for chats
        chatAdapter.onChatClick = object : ChatAdapter.OnChatClick {
            // Call when a chat is clicked, navigate to chat activity
            override fun onChatClick(position: Int) {
                val chat = chatAdapter.getChats()[position]
                val action = ContactFragmentDirections.actionContactFragmentToPersonalChatFragment(chat.id)
                findNavController().navigate(action)
            }
        }
    }

}