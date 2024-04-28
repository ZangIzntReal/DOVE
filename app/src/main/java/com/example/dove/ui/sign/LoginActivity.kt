package com.example.dove.ui.sign

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.dove.R
import com.example.dove.data.model.User
import com.example.dove.databinding.ActivityLoginBinding
import com.example.dove.ui.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    // Khởi tạo biến binding
    private lateinit var binding: ActivityLoginBinding
    // Khởi tạo biến auth và database từ FirebaseAuth và FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Gán giá trị cho biến auth và database
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // Xử lý sự kiện click vào dòng chữ "Don't have an account? Register"
        binding.tvLoginToRegister.setOnClickListener() {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.btnLogin.setOnClickListener() {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            } else {
                loginUser(email, password)
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Toast.makeText(this, "Login success", Toast.LENGTH_SHORT).show()
                    database.getReference("Users").child(user?.uid.toString()).child("status").setValue("online")
                    database.getReference("Users").child(user!!.uid).get().addOnSuccessListener {
                        val userInfo = it.getValue(User::class.java)
                        // userInfo now contains the user's information
                    }
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
                }
            }
    }
}