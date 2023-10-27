package com.example.todolist

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// database class for the SQLite database
class Database(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    //object to hold values for database and declaring our constants
    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "TaskDatabase"
        private const val TABLE_TASKS = "Tasks"
        private const val KEY_ID = "id"
        private const val KEY_TASK_NAME = "taskName"
        private const val KEY_IS_COMPLETED = "isCompleted"
    }

    //method during startup to create the task table
    override fun onCreate(db: SQLiteDatabase?) {
        val create_tasks_table =
            ("CREATE TABLE $TABLE_TASKS($KEY_ID INTEGER PRIMARY KEY,$KEY_TASK_NAME TEXT,$KEY_IS_COMPLETED INTEGER)")
        db?.execSQL(create_tasks_table)
    }

    //this method is for when the app is updated or the database schema is changed. I left it to default since we don't need it here.
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_TASKS")
        onCreate(db)
    }

    // insert task to database

    //closing database after operations to save on memory. Not necessary since operations are thread safe but figured it's good practice to remember
    //for more memory heavy apps

    fun addTask(task: TaskModification) {
        val values = ContentValues().apply {
            put(KEY_TASK_NAME, task.name)
            put(KEY_IS_COMPLETED, if (task.isCompleted) 1 else 0)
        }
        val db = this.writableDatabase
        db.insert(TABLE_TASKS, null, values)
        db.close()
    }

    //retrieve tasks
    @SuppressLint("Range")
    fun getAllTasks(): List<TaskModification> {
        val tasks = ArrayList<TaskModification>()
        val selectQuery = "SELECT * FROM $TABLE_TASKS"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        //loop through rows and add task to the list
        if (cursor.moveToFirst()) {
            do {
                val task = TaskModification(
                    id = cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                    name = cursor.getString(cursor.getColumnIndex(KEY_TASK_NAME)),
                    isCompleted = cursor.getInt(cursor.getColumnIndex(KEY_IS_COMPLETED)) == 1
                )
                tasks.add(task)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return tasks
    }

    //update task to completed
    fun markTaskAsComplete(taskId: Int) {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(KEY_IS_COMPLETED, 1)
        }
        db.update(TABLE_TASKS, contentValues, "$KEY_ID=?", arrayOf(taskId.toString()))
        db.close()
    }

    // revert task back to not completed
    fun markTaskAsNotCompleted(taskId: Int) {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(KEY_IS_COMPLETED, 0)
        }
        db.update(TABLE_TASKS, contentValues, "$KEY_ID=?", arrayOf(taskId.toString()))
        db.close()
    }

    //deletes specific task based on ID
    fun deleteTask(taskId: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_TASKS, "$KEY_ID=?", arrayOf(taskId.toString()))
        db.close()
    }
}