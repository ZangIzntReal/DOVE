package com.example.dove.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.dove.R
import com.example.dove.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var controller: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ẩn ActionBar
        supportActionBar?.hide()

        controller = findNavController(R.id.my_nav_host_fragment)
        binding.bottomNavigationView.setupWithNavController(controller)

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

                R.id.profileFragment -> {
                    controller.navigate(R.id.profileFragment)
                    true
                }

                else -> false
            }
        }
        // Đặt labelVisibilityMode để chỉ hiển thị label khi item được chọn
        binding.bottomNavigationView.labelVisibilityMode = BottomNavigationView.LABEL_VISIBILITY_SELECTED
    }
}