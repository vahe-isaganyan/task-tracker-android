package com.example.todolist

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

// declaring database, our input fields, and buttons

class MainActivity : AppCompatActivity() {
    private lateinit var db: Database
    private lateinit var inputTask: EditText
    private lateinit var btnAddTask: Button
    private lateinit var btnViewTasks: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //initialize database helper and link objects with xml views

        db = Database(this)
        inputTask = findViewById(R.id.inputTask)
        btnAddTask = findViewById(R.id.btnAddTask)
        btnViewTasks = findViewById(R.id.btnViewTasks)

        //click listener for add task button. Clears text field if task is added.

        btnAddTask.setOnClickListener {
            val taskName = inputTask.text.toString()
            if (taskName.isNotBlank()) {
                val newTask = TaskModification(id = 0, name = taskName, isCompleted = false)
                db.addTask(newTask)
                inputTask.text.clear()
            }
        }

        //view all tasks click listener

        btnViewTasks.setOnClickListener {
            val intent = Intent(this, TaskListActivity::class.java)
            startActivity(intent)
        }
    }
}
