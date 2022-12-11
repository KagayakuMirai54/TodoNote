package com.project.todonote.ui.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.project.todonote.db.TodoNoteDb


@Suppress("UNCHECKED_CAST")
class ProvideFactory (
    private val todoNoteDb : TodoNoteDb
    ) : ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(todoNoteDb) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
        }