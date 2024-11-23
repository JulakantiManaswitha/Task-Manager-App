package com.example.taskmanagerapplication

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var profilePic: ImageView
    private lateinit var userEmail: TextView
    private lateinit var logoutButton: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the fragment's layout
        val rootView = inflater.inflate(R.layout.fragment_profile, container, false)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Get references to UI elements
        profilePic = rootView.findViewById(R.id.profilePic)
        userEmail = rootView.findViewById(R.id.userEmail)
        logoutButton = rootView.findViewById(R.id.logoutLayout)

        // Get current logged in user
        val currentUser: FirebaseUser? = auth.currentUser

        // Check if user is logged in
        if (currentUser != null) {
            // Set user details
            userEmail.text = currentUser.email
        }

        // Handle logout button click
        // Inside the logoutButton.setOnClickListener block
        logoutButton.setOnClickListener {
            // Create an AlertDialog to confirm logout
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Logout")
            builder.setMessage("Are you sure you want to log out?")
            builder.setPositiveButton("Yes") { dialog, _ ->
                // Sign out and navigate to the login screen
                auth.signOut()
                val intent = Intent(activity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                activity?.finish() // Close the current activity
                dialog.dismiss() // Close the dialog
            }
            builder.setNegativeButton("No") { dialog, _ ->
                // Dismiss the dialog
                dialog.dismiss()
            }

            // Show the dialog
            builder.create().show()
        }

        return rootView
    }
}
