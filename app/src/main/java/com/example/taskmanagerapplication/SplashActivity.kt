package com.example.taskmanagerapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.taskmanagerapplication.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up ViewBinding
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set the click listener for the Get Started button
        binding.getStartedButton.setOnClickListener {
            // Navigate to LoginActivity when the button is clicked
            startActivity(Intent(this, LoginActivity::class.java))
            finish() // Close SplashActivity
        }
    }
}
