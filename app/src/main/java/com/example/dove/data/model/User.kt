package com.example.dove.data.model

data class User(
    val userid: String? = "",
    var status : String? = "",
    val imageUrl : String? = "",
    var username: String? = "",
    val email : String? ="",
    var contacts: List<Contact>? = mutableListOf(),
    var chats: List<String>? = mutableListOf(),
    var exitChat: List<String>? = mutableListOf()
) {

    public fun addChat(chatId: String?) {
        val newChats = chats!!.toMutableList()
        newChats.add(chatId!!)
        chats = newChats
    }

    public fun addExitChat(userId: String?) {
        val newExitChats = exitChat!!.toMutableList()
        newExitChats.add(userId!!)
        exitChat = newExitChats
    }

    public fun addNewContact(contact: Contact) {
        val newContacts = contacts!!.toMutableList()
        newContacts.add(contact)
        contacts = newContacts
    }
}
