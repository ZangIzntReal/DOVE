package com.example.dove.ui.sign

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.dove.R
import com.example.dove.data.model.User
import com.example.dove.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    // Khởi tạo biến binding
    private lateinit var binding: ActivityRegisterBinding

    // Khởi tạo biến auth và database từ FirebaseAuth và FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Gán giá trị cho biến auth và database
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // Xử lý sự kiện click vào dòng chữ "Already have an account? Login"
        binding.tvRegisterToLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        // Xử lý sự kiện click vào nút "Sign Up"
        binding.btnSignUp.setOnClickListener() {
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            if (password.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            }
            else if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(this, "Registering...", Toast.LENGTH_SHORT).show()
                registerUser(email, password, name)
            }
        }
    }

    // Hàm đăng ký người dùng
    private fun registerUser(email: String, password: String, name: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Nếu đăng ký thành công, tạo một User mới với các thông tin tương ứng
                    val User = User(
                        userid = auth.currentUser?.uid,
                        status = "offline",
                        imageUrl = "",
                        username = name,
                        email = email,
                        contacts = mutableListOf(),
                        chats = mutableListOf()
                    )
                    // Lưu thông tin User vào database
                    database.getReference("Users").child(auth.currentUser?.uid!!).setValue(User)
                    // Tọa node mới trong database để lưu quan hệ giữa email và userId
                    val user = auth.currentUser
                    val userId = user?.uid
                    if (userId != null) {
                        // Create a new node in the database for the relationship between email and userId
                        val emailToUserIdRef = database.getReference("EmailToUserId").child(email.replace(".", ","))
                        emailToUserIdRef.setValue(userId)
                    }
                    // Hiển thị thông báo đăng ký thành công
                    Toast.makeText(this, "Register successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
                else {
                    if (task.exception is FirebaseAuthUserCollisionException) {
                        // Show a message to the user that the email is already in use
                        Toast.makeText(this,"The email address is already in use by another account.", Toast.LENGTH_SHORT).show()
                    } else {
                        // Some other error occurred
                        Toast.makeText(this, "An error occurred: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }
}