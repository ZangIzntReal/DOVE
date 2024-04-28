package com.example.dove.ui

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.dove.R
import com.example.dove.data.model.User
import com.example.dove.databinding.ActivityMainBinding
import com.example.dove.viewmodel.SharedViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var controller: NavController

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private val sharedViewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ẩn ActionBar
        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // Lấy thông tin người dùng hiện tại
        val currentUser: FirebaseUser? = auth.currentUser
        if (currentUser != null) {
            database.getReference("Users").child(currentUser!!.uid).get().addOnSuccessListener {
                val userInfo = it.getValue(User::class.java)
                sharedViewModel.currentUser = userInfo
            }
        }

        // Điều hướng giữa các fragment
        controller = findNavController(R.id.my_nav_host_fragment)
        binding.bottomNavigationView.setupWithNavController(controller)

        // Thêm listener cho sự kiện thay đổi destination của NavController
        controller.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.personalChatFragment -> {
                    // Khi destination là PersonalFragment, ẩn BottomNavigationView
                    binding.bottomNavigationView.visibility = View.GONE
                }
                else -> {
                    // Khi destination không phải là PersonalFragment, hiển thị BottomNavigationView
                    binding.bottomNavigationView.visibility = View.VISIBLE
                }
            }
        }

        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->

            // Điều hướng đến fragment tương ứng
            when (item.itemId) {
                R.id.chatFragment -> {
                    controller.navigate(R.id.chatFragment)
                    true
                }

                R.id.contactFragment -> {
                    controller.navigate(R.id.contactFragment)
                    true
                }

                R.id.settingFragment -> {
                    controller.navigate(R.id.settingFragment)
                    true
                }

                else -> false
            }
        }
        // Đặt labelVisibilityMode để chỉ hiển thị label khi item được chọn
        binding.bottomNavigationView.labelVisibilityMode = BottomNavigationView.LABEL_VISIBILITY_SELECTED
    }
}