package com.example.taskmanagerapplication

data class Task(
    var id: String = "", // To store the document ID
    var title: String = "",
    var description: String = "",
    var dueDate: String = "",
    var priority: String = "",
    var completed: Boolean = false,
    var category: String = "" // New field for task category
)