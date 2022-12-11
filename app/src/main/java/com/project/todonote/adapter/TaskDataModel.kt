package com.project.todonote.adapter

import androidx.annotation.Keep
import com.project.todonote.model.Priority
import com.project.todonote.model.TypeTask

sealed class TaskDataModel {

    @Keep
    data class Header(
        val title: String
    ): TaskDataModel()

    @Keep
    data class Task(
        val id: Int,
        var title: String,
        var description: String,
        var priority: Priority
    ) : TaskDataModel(){
        fun toTask() = com.project.todonote.model.Task(title, description, priority, id)
        val taskType = if (priority == Priority.DONE ) TypeTask.DONE else TypeTask.TODO
    }
}