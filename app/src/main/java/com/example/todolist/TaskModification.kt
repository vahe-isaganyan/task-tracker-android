package com.example.todolist


//created data class so we can have a unique identifier of each class, name, and also a flag if it was completed or not.

//we also want to showcase a visual representation of a task being completed so we have an override method with a customized string method.

data class TaskModification(val id: Int, val name: String, val isCompleted: Boolean) {
    override fun toString(): String {
        return if (isCompleted) "$name (Completed)" else name
    }
}



