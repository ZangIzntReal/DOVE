package com.example.dove.viewmodel

import androidx.lifecycle.ViewModel
import com.example.dove.data.model.Chat
import com.example.dove.data.model.User

class SharedViewModel: ViewModel() {
    var currentUser: User? = null
    var currentChat: Chat? = null

    fun logout() {
        currentUser = null
        currentChat = null
    }
}