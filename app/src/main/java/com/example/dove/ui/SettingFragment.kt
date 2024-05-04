package com.example.dove.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.dove.R
import com.example.dove.databinding.FragmentSettingBinding
import com.example.dove.ui.sign.LoginActivity
import com.example.dove.viewmodel.SharedViewModel

class SettingFragment : Fragment() {

    private lateinit var binding: FragmentSettingBinding
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentUser = sharedViewModel.currentUser!!
        binding.tvName.text = currentUser.username
        binding.tvEmail.text = currentUser.email

        if (currentUser.imageUrl != "") {
            Glide.with(this)
                .load(currentUser.imageUrl?.toUri())
                .into(binding.imgAvatar)
        }

        binding.llLogout.setOnClickListener {
            sharedViewModel.logout()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        binding.llPersonal.setOnClickListener {
            findNavController().navigate(R.id.profileFragment)
        }
    }
}