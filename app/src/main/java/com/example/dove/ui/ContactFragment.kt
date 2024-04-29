package com.example.dove.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dove.R
import com.example.dove.adapter.ContactAdapter
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

        // Khởi tạo database
        database = FirebaseDatabase.getInstance()

        // Thiết lập rvContacts
        binding.rvContacts.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = contactAdapter
        }
        // Lấy danh sách contact từ database
        contactList = currentUser?.contacts as MutableList<Contact>
        // Set danh sách contact vào ContactViewModel
        contactViewModel.setContacts(contactList)
        // Quan sát LiveData contacts từ ContactViewModel
        contactViewModel.contacts.observe(viewLifecycleOwner) {
            contactAdapter.setContacts(it)
        }
        // Thiết lập listener cho sự kiện thêm contact
        binding.btnAddContact.setOnClickListener {
            val dialog = AddContactDialogFragment()
            dialog.listener = object : AddContactDialogFragment.AddContactDialogListener {
                override fun onDialogPositiveClick(email: String) {
                    addNewContact(email)
                }
            }
            dialog.show(childFragmentManager, "AddContactDialogFragment")
        }

    }

    private fun addNewContact(email: String) {
        val myRef = database.getReference("users").orderByChild("email").equalTo(email)
        myRef.get().addOnSuccessListener {
            if (it.exists()) {
                val newUser = it.children.first().getValue(User::class.java)
                val newContact = Contact(
                    id = newUser?.userid,
                    email = newUser?.email,
                )
                updateContactList(newContact)
                contactList.add(newContact)
                contactViewModel.setContacts(contactList)
            }
            else {
                // Hiển thị thông báo lỗi
                Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun updateContactList(newContact: Contact) {
        val tmpUser = currentUser
        tmpUser?.addNewContact(newContact)
        sharedViewModel.currentUser = tmpUser
        database.getReference("users").child(currentUser?.userid.toString()).setValue(tmpUser)
    }
}