package com.project.todonote.ui.main.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.todonote.model.Task
import com.project.todonote.db.TodoNoteDb
import com.project.todonote.model.Priority
import com.project.todonote.model.Today
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel (private val todoNoteDatabase: TodoNoteDb) : ViewModel(){

    private val dateFormat = SimpleDateFormat("dd", Locale.getDefault())
    private val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
    private val monthYearFormat = SimpleDateFormat("MMM, yyyy", Locale.getDefault())

    val today : MutableLiveData<Today> by lazy { MutableLiveData<Today>() }

    fun getToday(){
        val time = System.currentTimeMillis()
        today.value = Today(
            date = dateFormat.format(time).toInt(),
            day = dayFormat.format(time),
            monthYear = monthYearFormat.format(time)
            )
    }

    fun addTask(task: Task) = viewModelScope.launch {
        todoNoteDatabase.getTaskDao().upsertTask(task)
    }

    fun deleteTask(task: Task) = viewModelScope.launch {
        todoNoteDatabase.getTaskDao().deleteTask(task)
    }

    fun getTasks() = todoNoteDatabase.getTaskDao().getTasks()

    fun setPriority(priority: Priority, id: Int) = viewModelScope.launch {
        todoNoteDatabase.getTaskDao().setPriority(priority, id)
    }

    fun updateTask(task: Task): Job {
        if (task.id == null) throw IllegalStateException("Task id can not be null")
        return viewModelScope.launch {
            todoNoteDatabase
                .getTaskDao()
                .updateTask(task.id, task.title, task.description, task.priority)
        }
    }

}