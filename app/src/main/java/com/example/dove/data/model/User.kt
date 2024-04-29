package com.example.dove.data.model

data class User(
    val userid: String? = "",
    var status : String? = "",
    val imageUrl : String? = "",
    var username: String? = "",
    val email : String? ="",
    var contacts: List<Contact>? = mutableListOf(),
    var chats: List<Chat>? = mutableListOf()
) {
   public fun getChatWithUser(user: User): Chat? {
        for (chat in chats!!) {
            if (chat.userA.userid == user.userid || chat.userB.userid == user.userid) {
                return chat
            }
        }
        return null
    }

    public fun addNewContact(contact: Contact) {
        val newContacts = contacts!!.toMutableList()
        newContacts.add(contact)
        contacts = newContacts
    }
}
