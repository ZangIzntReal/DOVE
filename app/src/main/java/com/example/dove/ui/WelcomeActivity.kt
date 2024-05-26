package com.example.dove.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.dove.R
import com.example.dove.databinding.ActivityWelcomeBinding
import com.example.dove.ui.sign.LoginActivity

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val topPadding = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top
            val bottomPadding = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            binding.tvWelcome.setPadding(0, topPadding, 0, bottomPadding)
            insets
        }


        // Get SharedPreferences
        val sharedPreferences = getSharedPreferences("DOVE", Context.MODE_PRIVATE)

        // Check if the user has opened the app before
        val hasOpened = sharedPreferences.getBoolean("hasOpened", false)

        if (hasOpened) {
            // If the user has opened the app before, redirect to LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            // If the user has not opened the app before, update the state
            val editor = sharedPreferences.edit()
            editor.putBoolean("hasOpened", true)
            editor.apply()

            // Continue with WelcomeActivity...
        }

        binding.btnStart.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))

        }
    }
}