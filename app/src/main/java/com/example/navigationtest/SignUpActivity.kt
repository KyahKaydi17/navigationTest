package com.example.navigationtest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.navigationtest.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import javax.security.auth.login.LoginException


class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.buttonSignUp.setOnClickListener {
            val email = binding.editTextEmail.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()
            val username = binding.editTextUsername.text.toString().trim() // Retrieve username

            if (email.isNotEmpty() && password.length >= 6 && username.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val firebaseUser = auth.currentUser
                            val profileUpdates = UserProfileChangeRequest.Builder()
                                .setDisplayName(username) // Set the username
                                .build()

                            firebaseUser?.updateProfile(profileUpdates)
                                ?.addOnCompleteListener { profileUpdateTask ->
                                    if (profileUpdateTask.isSuccessful) {
                                        // Username saved successfully
                                        val intent = Intent(this, LogInActivity::class.java)
                                        startActivity(intent)
                                    } else {
                                        // Handle username update failure
                                    }
                                }
                        } else {
                            // Handle sign-up failure
                        }
                    }
            }


        }
    }
}
