package com.example.dove.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dove.data.model.Chat
import com.example.dove.data.model.Message

class MessageViewModel: ViewModel(){
    private val _messages = MutableLiveData<List<Message>>()

    val messages: LiveData<List<Message>> get() = _messages

    fun setMessages(messages: List<Message>) {
        _messages.value = messages
    }
    fun addMessage(message: Message) {
        val currentMessages = _messages.value?.toMutableList() ?: mutableListOf()
        currentMessages.add(message)
        _messages.value = currentMessages
    }
}