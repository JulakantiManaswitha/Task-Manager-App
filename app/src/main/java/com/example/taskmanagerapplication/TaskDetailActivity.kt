package com.example.taskmanagerapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import com.example.taskmanagerapplication.databinding.ActivityTaskDetailBinding
import com.google.firebase.firestore.FirebaseFirestore

class TaskDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTaskDetailBinding
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var taskId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up ViewBinding
        binding = ActivityTaskDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the back arrow functionality
        binding.backArrow.setOnClickListener {
            onBackPressed() // This will navigate the user back to the previous activity
        }

        // Get task ID from Intent
        taskId = intent.getStringExtra("task_id")
        taskId?.let { loadTask(it) }

        // Floating Action Button for editing
        binding.btnEdit.setOnClickListener {
            // Navigate to AddEditTaskActivity for editing
            val intent = Intent(this, AddEditTaskActivity::class.java)
            intent.putExtra("task_id", taskId) // Pass task ID for editing
            startActivity(intent)
        }

        // Delete task button with confirmation dialog
        binding.fabDelete.setOnClickListener {
            taskId?.let { id -> showDeleteConfirmationDialog(id) }
        }
    }

    private fun loadTask(taskId: String) {
        db.collection("tasks").document(taskId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    binding.textViewTaskTitle.text = document.getString("title")
                    binding.textViewTaskDesc.text = document.getString("description")
                    binding.textViewTaskPriority.text = document.getString("priority")
                    binding.textViewTaskDueDate.text = document.getString("dueDate")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("TaskDetailActivity", "Error getting task", exception)
                Toast.makeText(this, "Error loading task details", Toast.LENGTH_SHORT).show()
            }
    }

    // Show a confirmation dialog before deleting
    private fun showDeleteConfirmationDialog(taskId: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Task")
        builder.setMessage("Are you sure you want to delete this task?")

        builder.setPositiveButton("Yes") { _, _ ->
            deleteTask(taskId) // Proceed with deleting the task
        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss() // Dismiss the dialog if the user selects "No"
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun deleteTask(taskId: String) {
        db.collection("tasks").document(taskId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Task deleted successfully", Toast.LENGTH_SHORT).show()
                finish() // Close activity after deleting the task
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error deleting task", Toast.LENGTH_SHORT).show()
                Log.w("TaskDetailActivity", "Error deleting task", e)
            }
    }
}
