package com.example.dove.data.model

data class User(
    val userid: String? = "",
    var status : String? = "",
    val imageUrl : String? = "",
    var username: String? = "",
    val email : String? ="",
    var contacts: List<Contact>? = listOf(),
    var chats: List<Chat>? = listOf()
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
