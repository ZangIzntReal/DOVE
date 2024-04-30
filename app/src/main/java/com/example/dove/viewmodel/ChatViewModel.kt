package com.example.dove.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dove.data.model.Chat
import com.example.dove.data.model.Contact

class ChatViewModel: ViewModel() {
    private val _chats = MutableLiveData<List<Chat>>()

    val chats: LiveData<List<Chat>> get() = _chats

    fun setChats(chats: List<Chat>) {
        _chats.value = chats
    }
    fun addContact(chat: Chat) {
        val currentContacts = _chats.value?.toMutableList() ?: mutableListOf()
        currentContacts.add(chat)
        _chats.value = currentContacts
    }
}