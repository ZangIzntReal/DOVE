package com.example.dove.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dove.R
import com.example.dove.adapter.ContactAdapter
import com.example.dove.data.model.Chat
import com.example.dove.data.model.Contact
import com.example.dove.data.model.User
import com.example.dove.databinding.FragmentContactBinding
import com.example.dove.viewmodel.ContactViewModel
import com.example.dove.viewmodel.SharedViewModel
import com.google.firebase.database.FirebaseDatabase

class ContactFragment : Fragment() {

    private lateinit var binding: FragmentContactBinding
    private val contactAdapter = ContactAdapter()
    private val contactViewModel: ContactViewModel by viewModels()
    private var contactList = mutableListOf<Contact>()
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
        binding = FragmentContactBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Lấy thông tin người dùng hiện tại
        currentUser = sharedViewModel.currentUser
        Log.d("ContactFragment", "Current user: $currentUser")

        // Khởi tạo database
        database = FirebaseDatabase.getInstance()

        // Thiết lập rvContacts
        binding.rvContacts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = contactAdapter
        }
        // Lấy danh sách contact từ database
        contactList = currentUser?.contacts as MutableList<Contact>
        Log.d("ContactFragment", "contactList: $contactList")
        // Set danh sách contact vào ContactViewModel
        contactViewModel.setContacts(contactList)
        // Quan sát LiveData contacts từ ContactViewModel
        contactViewModel.contacts.observe(viewLifecycleOwner) {
            Log.d("ContactFragment", "Contacts: ${it.size}")
            contactAdapter.setContacts(it)
        }
        // Thiết lập listener cho sự kiện thêm contact
        binding.btnAddContact.setOnClickListener {
            val dialog = AddContactDialogFragment()
            dialog.listener = object : AddContactDialogFragment.AddContactDialogListener {
                override fun onDialogPositiveClick(email: String) {
                    Toast.makeText(context, "Email: $email", Toast.LENGTH_SHORT).show()
                    Log.d("ContactFragment", "Email: $email")
                    addNewContact(email)
                }
            }
            dialog.show(childFragmentManager, "AddContactDialogFragment")
        }

        // Thiết lập listener cho sự kiện click vào contact
        contactAdapter.onContactClick = object : ContactAdapter.OnContactClick {
            override fun onContactClick(position: Int) {
                val contact = contactAdapter.getContacts()[position]
                database.getReference("Users").child(contact.id.toString()).get().addOnSuccessListener { dataSnapshot ->
                    if (dataSnapshot.exists()) {
                        val user1 = currentUser
                        val user2 = dataSnapshot.getValue(User::class.java)!!
                        Log.d("ContactFragment", "User A: $user1")
                        Log.d("ContactFragment", "User B: $user2")
                        if (user1 != null && checkNotExistingChat(user1, user2)) {
                            val id = database.getReference("Chats").push().key.toString()
                            val chat = Chat(
                                id,
                                user1.userid,
                                user2.userid,
                                mutableListOf()
                            )
                            database.getReference("Chats").child(id.toString()).setValue(chat)
                            user1.addNewChat(chat)
                            user2.addNewChat(chat)
                            user1.addExitChat(user2.userid)
                            user2.addExitChat(user1.userid)
                            sharedViewModel.currentUser = user1
                            database.getReference("Users").child(user1.userid.toString()).setValue(user1)
                            database.getReference("Users").child(user2.userid.toString()).setValue(user2)
                            sharedViewModel.currentChat = chat
                            findNavController().navigate(R.id.personalChatFragment)
                        }
                        else {
                            Toast.makeText(context, "Chat already exists", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun checkNotExistingChat(user1: User, user2: User): Boolean {
        return user1.exitChat?.find { it == user2.userid } == null
    }

    private fun addNewContact(tmpEmail: String) {
        val email = tmpEmail.replace(".", ",")
        if (tmpEmail == currentUser?.email) {
            Toast.makeText(context, "Cannot add yourself", Toast.LENGTH_SHORT).show()
            return
        }
        else if (currentUser?.contacts?.find { it.email == email } != null) {
            Toast.makeText(context, "Contact already exists", Toast.LENGTH_SHORT).show()
        }
        else {
            database.getReference("EmailToUserId").child(email).get().addOnSuccessListener { dataSnapshot ->
                if (dataSnapshot.exists()) {
                    val id = dataSnapshot.value.toString()
                    Log.d("ContactFragment", "User id: $id")
                    database.getReference("Users").child(id).get()
                        .addOnSuccessListener { userSnapshot ->
                            if (userSnapshot.exists()) {
                                Toast.makeText(context, "User found", Toast.LENGTH_SHORT).show()
                                val newUser = userSnapshot.getValue(User::class.java)
                                Log.d("ContactFragment", "New user: $newUser")
                                val newContact = Contact(
                                    id = newUser?.userid,
                                    email = newUser?.email,
                                )
                                Log.d("ContactFragment", "New contact: $newContact")
                                updateContactList(newContact)
                                contactViewModel.addContact(newContact)
                                Log.d(
                                    "ContactFragment",
                                    "Contacts size: ${contactViewModel.contacts.value?.size}"
                                )
                            } else {
                                // Hiển thị thông báo lỗi
                                Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    // Hiển thị thông báo lỗi
                    Toast.makeText(context, "Id not found", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateContactList(newContact: Contact) {
        val tmpUser = currentUser
        tmpUser?.addNewContact(newContact)
        sharedViewModel.currentUser = tmpUser
        currentUser = tmpUser // Cập nhật currentUser trong ContactFragment
        database.getReference("Users").child(currentUser?.userid.toString()).setValue(tmpUser)
    }
}