package com.example.dove.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import com.google.android.gms.tasks.Tasks
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding
    private val chatViewModel: ChatViewModel by viewModels()
    private var chatList = mutableListOf<Chat>()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var currentUser: User? = null
    private lateinit var database: FirebaseDatabase
    private val chatAdapter by lazy { ChatAdapter(sharedViewModel) }
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

        binding.progressBar.visibility = View.VISIBLE
        binding.rvChats.visibility = View.GONE

        // Get current user information
        currentUser = sharedViewModel.currentUser

        // Initialize database
        database = FirebaseDatabase.getInstance()

        // Set up rvContacts
        binding.rvChats.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = chatAdapter
        }

        /// Get chats from current user
        database.getReference("Users").child(currentUser?.userid ?: "").get().addOnSuccessListener {
                if (it.exists()) {
                    val chats = it.getValue(User::class.java)?.chats ?: mutableListOf()
                    chatList = chats.toMutableList()
                    chatViewModel.setChats(chatList)

                    // Observe LiveData chats from ChatViewModel
                    chatViewModel.chats.observe(viewLifecycleOwner) {
                        // Update chats in adapter when data changes
                        chatAdapter.setChats(it)
                    }
                    binding.progressBar.visibility = View.GONE
                    binding.rvChats.visibility = View.VISIBLE
                }
            }.addOnFailureListener {
                binding.progressBar.visibility = View.GONE
                binding.rvChats.visibility = View.VISIBLE
                Toast.makeText(requireContext(), "Failed to get chats", Toast.LENGTH_SHORT).show()
            }

        // Set up Listener for chats
        chatAdapter.onChatClick = object : ChatAdapter.OnChatClick {
            // Call when a chat is clicked, navigate to chat activity
            override fun onChatClick(position: Int) {
                val chat = chatAdapter.getChats()[position]
                sharedViewModel.currentChat = chat
                findNavController().navigate(R.id.personalChatFragment)
            }
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    binding.rvChats.visibility = View.GONE
                    binding.progressBar.visibility = View.VISIBLE
                    searchChat(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    private fun searchChat(query: String) {
        val tasks = chatList.map { chat ->
            database.getReference("Users").child(chat.getChatName(currentUser?.userid ?: "")).get().continueWith { task ->
                if (task.isSuccessful) {
                    val user = task.result.getValue(User::class.java)
                    if (user?.username?.contains(query, ignoreCase = true) == true) {
                        chat
                    } else {
                        null
                    }
                } else {
                    null
                }
            }
        }
        Tasks.whenAllSuccess<Chat>(tasks).addOnSuccessListener { results ->
            val searchResult = results.filterNotNull()
            chatAdapter.setChats(searchResult)
            binding.progressBar.visibility = View.GONE
            binding.rvChats.visibility = View.VISIBLE
        }
    }


}