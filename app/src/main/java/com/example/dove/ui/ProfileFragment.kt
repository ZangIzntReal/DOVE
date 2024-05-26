package com.example.dove.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.dove.R
import com.example.dove.data.model.User
import com.example.dove.databinding.FragmentProfileBinding
import com.example.dove.viewmodel.SharedViewModel
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var currentUser: User
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private val PICK_IMAGE_REQUEST_CODE = 1000

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        currentUser = sharedViewModel.currentUser!!

        binding.tvEmail.text = currentUser.email
        binding.etUsername.setText(currentUser.username)


        if (currentUser.imageUrl != "") {
            Glide.with(this)
                .load(currentUser.imageUrl?.toUri())
                .into(binding.imgAvatar)
        }

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.btnSave.setOnClickListener {
            val newUsername = binding.etUsername.text.toString()
            currentUser.username = newUsername
            sharedViewModel.currentUser = currentUser
            database.getReference("Users").child(currentUser.userid.toString()).child("username").setValue(newUsername)
            binding.etUsername.clearFocus()
            binding.etUsername.setText(newUsername)
        }

        binding.cvAvatar.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri = data.data

            Log.d("UploadImage", "Image URI: $selectedImageUri")
            binding.imgAvatar.setImageURI(selectedImageUri)

            val storageRef = storage.reference.child("avatars/${currentUser.userid}")

            // Upload the image to Firebase Storage
            storageRef.putFile(selectedImageUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    // Handle successful upload
                    taskSnapshot.storage.downloadUrl.addOnSuccessListener { downloadUri ->
                        // Update avatarUrl in currentUser and Firebase Database
                        currentUser.imageUrl = downloadUri.toString()
                        sharedViewModel.currentUser = currentUser
                        database.getReference("Users").child(currentUser.userid.toString()).child("imageUrl").setValue(downloadUri.toString())
                    }
                    Toast.makeText(context, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    // Handle failed upload
                    Toast.makeText(context, "Failed to upload image: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}