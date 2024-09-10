package com.example.navigationtest

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.navigationtest.databinding.ActivityLogInBinding
import com.google.firebase.auth.FirebaseAuth

class LogInActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLogInBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLogInBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val username = intent.getStringExtra("USERNAME")


        binding.buttonLogIn.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Inside your LogInActivity
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val currentUser = FirebaseAuth.getInstance().currentUser
                            val username = currentUser?.displayName // Retrieve the username

                            val intent = Intent(this, MainActivity::class.java)
                            intent.putExtra("USERNAME", username) // Pass the username to MainActivity
                            startActivity(intent)
                            finish()
                        } else {
                            // Handle login failure
                        }


            }

            }
            else{
                Toast.makeText(this,"Invalid input, Try Again!",Toast.LENGTH_SHORT).show()
            }
        }

        binding.textViewSignUp.setOnClickListener({
            val intent =Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        })
        binding.textViewForgotPassword.setOnClickListener {
            resetPassword()
        }
    }

    private fun resetPassword() {
        // Get the email entered by the user
        val email = binding.editTextEmail.text.toString().trim()

        if (email.isNotEmpty()) {
            // Initiate password reset process using Firebase Authentication
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Password reset email sent successfully
                        Toast.makeText(this, "Password reset email sent to $email", Toast.LENGTH_SHORT).show()
                    } else {
                        // Password reset email sending failed
                        Toast.makeText(this, "Failed to send password reset email", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
        }
    }
}
