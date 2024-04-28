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
import com.example.dove.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
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
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            }
            else {
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
                        useremail = email
                    )
                    // Lưu thông tin User vào database
                    database.getReference("Users").child(auth.currentUser?.uid!!).setValue(User)
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            }
    }
}