package com.example.dove.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dove.data.model.Contact

class ContactViewModel: ViewModel() {
    private val _contacts = MutableLiveData<List<Contact>>()

    val contacts: LiveData<List<Contact>> get() = _contacts

    fun setContacts(contacts: List<Contact>) {
        _contacts.value = contacts
    }
    fun addContact(contact: Contact) {
        val currentContacts = _contacts.value?.toMutableList() ?: mutableListOf()
        currentContacts.add(contact)
        _contacts.value = currentContacts
    }

}