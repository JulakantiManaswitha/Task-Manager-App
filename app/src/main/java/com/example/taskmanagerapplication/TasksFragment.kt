package com.example.taskmanagerapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskmanagerapplication.databinding.FragmentTasksBinding
import com.google.android.material.chip.Chip
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import androidx.appcompat.widget.SearchView
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TasksFragment : Fragment() {

    private lateinit var binding: FragmentTasksBinding
    private lateinit var taskAdapter: TaskAdapter
    private var taskList: MutableList<Task> = mutableListOf() // List to hold tasks
    private var originalTasks: MutableList<Task> = mutableListOf()
    private val db: FirebaseFirestore = Firebase.firestore // Initialize Firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for the fragment
        binding = FragmentTasksBinding.inflate(inflater, container, false)

        // Initialize RecyclerView
        binding.recyclerViewTasks.layoutManager = LinearLayoutManager(requireContext())

        fetchTasks() // Fetch tasks from Firestore

        // Floating Action Button to add new tasks
        binding.fabAddTask.setOnClickListener {
            // Navigate to AddEditTaskActivity to create a new task
            val intent = Intent(requireContext(), AddEditTaskActivity::class.java)
            startActivity(intent)
        }

        setupSearchView()
        setupChipGroup()

        return binding.root
    }

    private fun fetchTasks() {
        // Fetch tasks from the Firestore collection "tasks"
        db.collection("tasks")
            .get()
            .addOnSuccessListener { documents ->
                taskList.clear() // Clear the list to avoid duplicates
                originalTasks.clear()
                for (document in documents) {
                    val task = document.toObject(Task::class.java)
                    task.id = document.id // Add document ID to the task
                    taskList.add(task) // Add task to list
                    originalTasks.add(task)
                    Log.d("TaskListFragment", "Loaded task: ${task.title}, Category: ${task.category}")
                }

                // Set up the adapter after fetching tasks
                taskAdapter = TaskAdapter(taskList, originalTasks) { task ->
                    // Navigate to TaskDetailActivity when the task is clicked
                    val intent = Intent(requireContext(), TaskDetailActivity::class.java)
                    intent.putExtra("task_id", task.id) // Pass task ID
                    startActivity(intent)
                }

                binding.recyclerViewTasks.adapter = taskAdapter // Set the adapter to RecyclerView
                taskAdapter.notifyDataSetChanged() // Notify adapter that the data set has changed
            }
            .addOnFailureListener { exception ->
                Log.w("TaskListFragment", "Error getting tasks: ", exception)
            }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // If the search text is empty, show all tasks
                taskAdapter.filter(newText)
                return true
            }
        })

        binding.searchView.setOnCloseListener {
            taskAdapter.filter(null)
            false
        }
    }

    private fun setupChipGroup() {
        val chipAll = binding.chipAll
        val chipWork = binding.chipWork
        val chipPersonal = binding.chipPersonal
        val chipUrgent = binding.chipUrgent

        // Set the "All" chip as selected by default
        chipAll.setChipBackgroundColorResource(R.color.chip_selected_background)

        // OnClickListener for the "All" chip
        chipAll.setOnClickListener {
            resetChipBackgrounds() // Reset all chips
            chipAll.setChipBackgroundColorResource(R.color.chip_selected_background)
            taskAdapter.filter(null) // Show all tasks
            Log.d("TaskListFragment", "Showing all tasks")
        }

        // OnClickListener for each category chip
        chipWork.setOnClickListener { onChipSelected(chipWork, "Work") }
        chipPersonal.setOnClickListener { onChipSelected(chipPersonal, "Personal") }
        chipUrgent.setOnClickListener { onChipSelected(chipUrgent, "Urgent") }
    }

    private fun onChipSelected(chip: Chip, category: String) {
        // Clear all selections
        resetChipBackgrounds()

        // Highlight the selected chip
        chip.setChipBackgroundColorResource(R.color.chip_selected_background)
        taskAdapter.filterByCategory(category) // Filter tasks by selected category

        Log.d("TaskListFragment", "Showing tasks for category: $category")
    }

    private fun resetChipBackgrounds() {
        for (i in 0 until binding.chipGroupCategories.childCount) {
            val chip = binding.chipGroupCategories.getChildAt(i) as Chip
            chip.setChipBackgroundColorResource(R.color.chip_background) // Reset background color
        }
    }
}
