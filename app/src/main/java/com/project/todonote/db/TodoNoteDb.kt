package com.project.todonote.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.project.todonote.model.Task


@Database(
    entities = [Task::class],
    version = 1,
    exportSchema = false
)
abstract class TodoNoteDb : RoomDatabase(){
    abstract  fun getTaskDao() : TaskDao

    companion object{
        @Volatile
        private var instance: TodoNoteDb? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDatabase(context).also { instance = it }
        }

        private fun createDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            TodoNoteDb::class.java,
            "tasks.db"
        ).build()

    }
}