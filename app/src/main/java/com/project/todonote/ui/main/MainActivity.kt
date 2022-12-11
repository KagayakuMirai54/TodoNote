package com.project.todonote.ui.main

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Constraints
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.todonote.ui.main.viewmodel.ProvideFactory
import com.project.todonote.TaskDialog
import com.project.todonote.adapter.TaskDataAdapter
import com.project.todonote.adapter.TaskDataModel
import com.project.todonote.databinding.ActivityMainBinding
import com.project.todonote.db.TodoNoteDb
import com.project.todonote.model.Priority
import com.project.todonote.model.State
import com.project.todonote.model.Task
import com.project.todonote.ui.main.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private var _taskDialog: TaskDialog? = null
    private val taskDialog get() = _taskDialog!!

    override fun onCreate(savedInstanceState: Bundle?) {

        mainViewModel = ViewModelProvider(
            this,
            ProvideFactory(TodoNoteDb(this))
        )[MainViewModel::class.java]

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()

        mainViewModel.getTasks().observe(this) { tasks ->
            val tasksList = tasks
                .sortedBy { it.taskType }
                .groupBy { it.taskType }
                .flatMap {
                    listOf(
                        TaskDataModel.Header(it.key.name),
                        *(it.value
                            .sortedByDescending { t -> t.id!! }
                            .map { task ->
                                task.toDataModel()
                            }).toTypedArray()
                    )
                }

            taskDataAdapter.differ.submitList(tasksList)
        }
    }

    private fun setupToolbar() {
        mainViewModel.getToday()

        mainViewModel.today.observe(this) {
            binding.apply {
                tvDate.text = it.date.toString()
                tvDay.text = it.day
                tvMonthYear.text = it.monthYear
            }
        }

        binding.btnNewTask.setOnClickListener {
            showTaskDialog(null)
        }
    }

    private val taskDataAdapter by lazy {
        TaskDataAdapter(
            applicationContext,
            checkTodoListener = {
                val priority = if (it.priority != Priority.DONE) {
                    Priority.DONE
                } else Priority.NORMAL
                mainViewModel.setPriority(priority, it.id)
            },
            itemTodoListener = {
                showTaskDialog(it.toTask())
            }
        )
    }

    private fun setupRecyclerView() {
        binding.rvTasks.apply {
            adapter = taskDataAdapter
            layoutManager = LinearLayoutManager(applicationContext)
        }
    }

    private fun showTaskDialog(task: Task?) {
        if (_taskDialog == null) {
            _taskDialog = TaskDialog(
                this, task,
                onSubmitClickListener = { s, t ->
                    when (s) {
                        State.ADD -> mainViewModel.addTask(t)
                        State.UPDATE -> mainViewModel.updateTask(t)
                    }
                    taskDialog.dismiss()
                    _taskDialog = null
                },
                onDeleteClickListener = {
                    mainViewModel.deleteTask(it)
                    taskDialog.dismiss()
                    _taskDialog = null
                }
            )
        }

        taskDialog.show()

        taskDialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(
                Constraints.LayoutParams.MATCH_PARENT,
                Constraints.LayoutParams.WRAP_CONTENT
            )
        }

        taskDialog.setOnDismissListener {
            _taskDialog = null
        }
    }
}