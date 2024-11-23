package com.example.taskmanagerapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class SettingsFragment : Fragment() {

    private lateinit var notificationPreference: TextView
    private lateinit var accountManagement: TextView
    private lateinit var aboutApp: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the fragment's layout
        val rootView = inflater.inflate(R.layout.fragment_settings, container, false)

        // Initialize views
        notificationPreference = rootView.findViewById(R.id.notificationsPreference)
        accountManagement = rootView.findViewById(R.id.accountManagement)
        aboutApp = rootView.findViewById(R.id.aboutApp)

        // Handle notifications settings click
        notificationPreference.setOnClickListener {
            // Open Notification settings
            Toast.makeText(activity, "Open Notifications Settings", Toast.LENGTH_SHORT).show()
        }

        // Handle account management click
        accountManagement.setOnClickListener {
            // Open account management
            Toast.makeText(activity, "Open Account Management", Toast.LENGTH_SHORT).show()
        }

        // Handle about app click
        aboutApp.setOnClickListener {
            // Open an About dialog or activity
            Toast.makeText(activity, "About this App", Toast.LENGTH_SHORT).show()
        }

        return rootView
    }
}
