package com.example.taskmanagerapplication

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taskmanagerapplication.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth
    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up ViewBinding
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Set up eye icon toggle for password visibility
        binding.passwordInput.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEndIndex = 2 // Right drawable index
                if (event.rawX >= (binding.passwordInput.right - binding.passwordInput.compoundDrawables[drawableEndIndex].bounds.width())) {
                    togglePasswordVisibility()
                    return@setOnTouchListener true // Consume the touch event
                }
            }
            false // Allow other touch events to proceed
        }

        // Set up eye icon toggle for password visibility
        binding.confirmPasswordInput.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEndIndex = 2 // Right drawable index
                if (event.rawX >= (binding.confirmPasswordInput.right - binding.confirmPasswordInput.compoundDrawables[drawableEndIndex].bounds.width())) {
                    toggleConfirmPasswordVisibility()
                    return@setOnTouchListener true // Consume the touch event
                }
            }
            false // Allow other touch events to proceed
        }

        binding.signupButton.setOnClickListener {
            registerUser()
        }

        binding.loginText.setOnClickListener{
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Hide password
            binding.passwordInput.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.passwordInput.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_password_24, 0, R.drawable.baseline_remove_red_eye_24, 0)
        } else {
            // Show password
            binding.passwordInput.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.passwordInput.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_password_24, 0, R.drawable.baseline_remove_red_eye_24, 0)
        }
        isPasswordVisible = !isPasswordVisible // Toggle the flag
        binding.passwordInput.setSelection(binding.passwordInput.text.length) // Set cursor to end
    }

    private fun toggleConfirmPasswordVisibility() {
        if (isConfirmPasswordVisible) {
            // Hide confirm password
            binding.confirmPasswordInput.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.confirmPasswordInput.setCompoundDrawablesRelativeWithIntrinsicBounds(
                R.drawable.baseline_password_24, // Left drawable
                0, // Top drawable
                R.drawable.baseline_remove_red_eye_24, // Right drawable
                0  // Bottom drawable
            )
        } else {
            // Show confirm password
            binding.confirmPasswordInput.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.confirmPasswordInput.setCompoundDrawablesRelativeWithIntrinsicBounds(
                R.drawable.baseline_password_24, // Left drawable
                0, // Top drawable
                R.drawable.baseline_remove_red_eye_24, // Right drawable
                0  // Bottom drawable
            )
        }
        isConfirmPasswordVisible = !isConfirmPasswordVisible
        binding.confirmPasswordInput.setSelection(binding.confirmPasswordInput.text.length)
    }

    private fun registerUser() {
        val email = binding.emailInput.text.toString().trim()
        val password = binding.passwordInput.text.toString().trim()
        val confirmPassword = binding.confirmPasswordInput.text.toString().trim()

        // Validate inputs
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
            return
        }

        // Check if email is in a valid format
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address.", Toast.LENGTH_SHORT).show()
            return
        }

        // Check if passwords match
        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show()
            return
        }

        // Check for password strength (e.g., minimum length)
        if (password.length < 6) {
            Toast.makeText(this, "Password must be at least 6 characters long.", Toast.LENGTH_SHORT).show()
            return
        }

        // Create the user
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("SignupActivity", "Registration successful for user: $email")
                    Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()
                    // Navigate to the login activity
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish() // Call finish to prevent returning to the signup screen
                } else {
                    Log.w("SignupActivity", "Registration failed", task.exception)
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}