package com.project.todonote.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.project.todonote.model.Priority
import com.project.todonote.model.Task


@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTask(media: Task): Long

    @Delete
    suspend fun deleteTask(task : Task)

    @Query("SELECT * FROM tasks")
    fun getTasks(): LiveData<List<Task>>

    @Query("UPDATE tasks SET priority = :priority WHERE id = :id")
    suspend fun setPriority(priority: Priority, id: Int)

    @Query(
        "UPDATE tasks " +
                "SET  title = :title, description = :description, priority = :priority" +
                " WHERE id = :id"
    )
    suspend fun updateTask(id: Int, title: String, description: String, priority: Priority)

}