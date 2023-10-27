package com.example.todolist

import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat

//class to display and manage tasks
class TaskListActivity : AppCompatActivity() {

    //declaring variables for the database helper and list view
    private lateinit var db: Database
    private lateinit var listViewTasks: ListView
    private lateinit var gestureDetector: GestureDetectorCompat

    //initializes activity when created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        //initialize database helper and list view
        db = Database(this)
        listViewTasks = findViewById(R.id.listViewTasks)

        // set task list click listener
        listViewTasks.setOnItemClickListener { _, _, position, _ ->
            val selectedTask = listViewTasks.adapter.getItem(position) as TaskModification
            showOptionsDialog(selectedTask)
        }

        //populate list view with tasks from database
        updateListView()

        //swiping functionality gesture detection. originally had a back button but thought it would be fun to add swipe feature
        gestureDetector = GestureDetectorCompat(this, SwipeGestureDetector())
    }

    //refresh list view with tasks
    private fun updateListView() {
        val tasks = db.getAllTasks()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, tasks)
        listViewTasks.adapter = adapter
    }

    //display dialogue list with options on each task when tapped
    private fun showOptionsDialog(task: TaskModification) {
        val options = if (task.isCompleted) {
            arrayOf("Mark as not complete", "Delete")
        } else {
            arrayOf("Mark as complete", "Delete")
        }
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose action:")
        builder.setItems(options) { _, which ->
            when (options[which]) {
                "Mark as not complete" -> {
                    db.markTaskAsNotCompleted(task.id)
                    updateListView()
                }

                "Mark as complete" -> {
                    db.markTaskAsComplete(task.id)
                    updateListView()
                }

                "Delete" -> {
                    db.deleteTask(task.id)
                    updateListView()
                }
            }
        }
        builder.show()
    }

    //gesture detection class for swipe events
    inner class SwipeGestureDetector : GestureDetector.SimpleOnGestureListener() {
        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            if (e1.x - e2.x > 100 && Math.abs(velocityX) > 100) {
                finish()
                return true
            }
            return super.onFling(e1, e2, velocityX, velocityY)
        }
    }

    //initiating gesture detection
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (gestureDetector.onTouchEvent(event)) {
            true
        } else {
            super.onTouchEvent(event)
        }
    }
}
