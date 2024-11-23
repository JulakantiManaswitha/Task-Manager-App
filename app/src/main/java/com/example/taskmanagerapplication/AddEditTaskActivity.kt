package com.example.taskmanagerapplication

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taskmanagerapplication.databinding.ActivityAddEditTaskBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class AddEditTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditTaskBinding
    private var taskId: String? = null // For editing a task
    private val db = FirebaseFirestore.getInstance() // Initialize Firestore
    private var dueDate: String? = null // To store the selected due date

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up ViewBinding
        binding = ActivityAddEditTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the back arrow functionality
        binding.backArrow.setOnClickListener {
            onBackPressed() // This will navigate the user back to the previous activity
        }

        // Set up priority spinner
        val priorities = arrayOf("High", "Medium", "Low")
        binding.spinnerTaskPriority.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, priorities)

        // Set up category spinner
        val categories = arrayOf("Personal", "Work", "Urgent")
        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTaskCategory.adapter = categoryAdapter

        // Check if editing a task
        taskId = intent.getStringExtra("task_id")
        if (taskId != null) {
            loadTask(taskId!!) // Load task details for editing
            binding.buttonDelete.visibility = View.VISIBLE // Show delete button
        }

        // Set date picker dialog for due date
        binding.duedateInput.setOnClickListener {
            showDatePickerDialog()
        }

        // Save task button click listener
        binding.buttonSave.setOnClickListener {
            if (taskId != null) {
                updateTask(taskId!!) // Update existing task
            } else {
                addTask() // Add new task
            }
        }

        // Delete task button click listener
        binding.buttonDelete.setOnClickListener {
            taskId?.let { id -> deleteTask(id) }
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            dueDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            binding.duedateInput.setText(dueDate) // Update the EditText to show selected date
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun loadTask(taskId: String) {
        // Load task details from Firestore
        db.collection("tasks").document(taskId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    binding.tasktitleInput.setText(document.getString("title"))
                    binding.taskdescInput.setText(document.getString("description"))
                    binding.spinnerTaskPriority.setSelection(getPriorityIndex(document.getString("priority")))
                    dueDate = document.getString("dueDate") // Load due date
                    binding.duedateInput.setText(dueDate) // Update the due date EditText

                    // Set the category spinner selection
                    val category = document.getString("category")
                    if(category != null) {
                        val categoryPosition = (binding.spinnerTaskCategory.adapter as ArrayAdapter<String>).getPosition(category)
                        binding.spinnerTaskCategory.setSelection(categoryPosition)
                        Log.d("AddEditTaskActivity","Category loaded: $category at position $categoryPosition")
                    }
                } else {
                    Log.w("AddEditTaskActivity","No such task found!")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("AddEditTaskActivity", "Error getting task", exception)
            }
    }

    private fun addTask() {
        // Get data from fields
        val title = binding.tasktitleInput.text.toString()
        val description = binding.taskdescInput.text.toString()
        val priority = binding.spinnerTaskPriority.selectedItem.toString()
        val category = binding.spinnerTaskCategory.selectedItem.toString()

        if (title.isNotBlank() && dueDate != null) { // Ensure due date is set
            // Create a new task in Firestore
            val taskData = hashMapOf(
                "title" to title,
                "description" to description,
                "priority" to priority,
                "dueDate" to dueDate, // Include due date in task data
                "category" to category
            )

            db.collection("tasks")
                .add(taskData)
                .addOnSuccessListener { documentReference ->
                    Toast.makeText(this, "Task added successfully: ${documentReference.id}", Toast.LENGTH_SHORT).show()
                    Log.d("AddEditTaskActivity", "Task added: ${documentReference.id}")
                    finish() // Close activity
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error adding task", Toast.LENGTH_SHORT).show()
                    Log.w("AddEditTaskActivity", "Error adding task", e)
                }
        } else {
            Toast.makeText(this, "Please enter a title and set a due date", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateTask(taskId: String) {
        // Get data from fields
        val title = binding.tasktitleInput.text.toString()
        val description = binding.taskdescInput.text.toString()
        val priority = binding.spinnerTaskPriority.selectedItem.toString()
        val category = binding.spinnerTaskCategory.selectedItem.toString()

        if (title.isNotBlank() && dueDate != null) { // Ensure due date is set
            // Update task in Firestore
            val taskData = hashMapOf(
                "title" to title,
                "description" to description,
                "priority" to priority,
                "dueDate" to dueDate,
                "category" to category
            )

            db.collection("tasks").document(taskId)
                .set(taskData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Task updated successfully", Toast.LENGTH_SHORT).show()
                    Log.d("AddEditTaskActivity", "Task updated: $taskId")
                    finish() // Close activity
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error updating task", Toast.LENGTH_SHORT).show()
                    Log.w("AddEditTaskActivity", "Error updating task", e)
                }
        } else {
            Toast.makeText(this, "Please enter a title and set a due date", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteTask(taskId: String) {
        // Delete task from Firestore
        db.collection("tasks").document(taskId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Task deleted successfully", Toast.LENGTH_SHORT).show()
                Log.d("AddEditTaskActivity", "Task deleted: $taskId")
                finish() // Close activity
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error deleting task", Toast.LENGTH_SHORT).show()
                Log.w("AddEditTaskActivity", "Error deleting task", e)
            }
    }

    private fun getPriorityIndex(priority: String?): Int {
        return when (priority) {
            "High" -> 0
            "Medium" -> 1
            "Low" -> 2
            else -> 1 // Default to Medium
        }
    }
}