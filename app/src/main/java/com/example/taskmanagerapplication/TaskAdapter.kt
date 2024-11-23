package com.example.taskmanagerapplication

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanagerapplication.databinding.ItemTaskBinding

class TaskAdapter(
    private var taskList: MutableList<Task>, // Store mutable list for filtering
    private val originalList: List<Task>, // Store the original list for reference
    private val onClick: (Task) -> Unit // Callback for item click
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(private val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(task: Task, onClick: (Task) -> Unit) {
            binding.textViewTitle.text = task.title // Update with your task properties
            binding.textViewDescription.text = task.description
            binding.root.setOnClickListener { onClick(task) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(taskList[position], onClick)
    }

    override fun getItemCount() = taskList.size

    // Method to filter tasks by search query
    fun filter(query: String?) {
        val filteredList =  when {
            query.isNullOrEmpty() -> originalList
            else -> originalList.filter { task ->
                task.title.contains(query,ignoreCase = true) || task.description.contains(query,ignoreCase = true)
             }
        }
        updateList(filteredList)
    }

    // Method to filter tasks by category
    fun filterByCategory(category: String) {
        Log.d("TaskAdapter","Original list size: ${originalList.size}")
        taskList.forEach{task ->
            Log.d("TaskAdapter","Task: ${task.title},Category: ${task.category}")
        }
        val filteredList = if (category.equals("All",ignoreCase = true)) {
            originalList
        } else {
            originalList.filter { task ->
                task.category.equals(category,ignoreCase = true)
            }
        }

        Log.d("TaskAdapter", "Filtering tasks by category: $category, found ${filteredList.size} tasks")
        updateList(filteredList) // Update the list with filtered tasks
    }

    // Helper method to update the displayed list and notify the adapter
    private fun updateList(filteredList: List<Task>) {
        taskList.clear()
        taskList.addAll(filteredList)
        notifyDataSetChanged()
    }
}