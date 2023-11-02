package com.example.todo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends Activity {
    private ArrayList<String> taskList;
    private TaskAdapter taskAdapter;
    private static final int REQUEST_TASK_INPUT = 1;
    private static final int REQUEST_TASK_EDIT = 2;
    private static final String EDIT_OPTION = "Edit";
    private static final String DELETE_OPTION = "Delete";
    private static final String MARK_COMPLETED_OPTION = "Mark as Completed";
    private static final String MARK_NOT_COMPLETED_OPTION = "Mark as Not Completed";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton addButton = findViewById(R.id.addButton);
        ListView taskListView = findViewById(R.id.taskListView);

        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(this, taskList);
        taskListView.setAdapter(taskAdapter);

        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TaskInputActivity.class);
            startActivityForResult(intent, REQUEST_TASK_INPUT);
        });

        taskListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showOptionsDialog(position);
                return true;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TASK_INPUT && resultCode == RESULT_OK && data != null) {
            handleTaskInput(data);
        } else if (requestCode == REQUEST_TASK_EDIT && resultCode == RESULT_OK && data != null) {
            handleTaskEdit(data);
        }
    }

    private void handleTaskInput(Intent data) {
        String taskName = data.getStringExtra("taskName");
        String dueDate = data.getStringExtra("dueDate");
        String description = data.getStringExtra("description");

        String taskDetails = "<br/>" + " ";
        taskDetails += "<b>" + taskName + "</b>";
        if (!dueDate.isEmpty() && !dueDate.equals("Due Date (optional)")) {
            taskDetails += "<br/>Due: " + dueDate;
        }
        if (!description.isEmpty()) {
            taskDetails += "<br/>" + description;
        }
        taskDetails += "<br/>" + " ";
        taskList.add(taskDetails);
        taskAdapter.notifyDataSetChanged();
        saveTasksToSharedPreferences(); // Save tasks to SharedPreferences
    }

    private void handleTaskEdit(Intent data) {
        String editedTaskDetails = data.getStringExtra("editedTaskDetails");
        int position = data.getIntExtra("position", -1);

        if (position >= 0) {
            taskList.set(position, editedTaskDetails);
            taskAdapter.notifyDataSetChanged();
            saveTasksToSharedPreferences(); // Save tasks to SharedPreferences
        }
    }
    private void showOptionsDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Task Options");
        builder.setIcon(R.drawable.baseline_task_alt_24);

        String[] options;
        if (isTaskCompleted(taskList.get(position))) {
            options = new String[]{EDIT_OPTION, DELETE_OPTION, MARK_NOT_COMPLETED_OPTION};
        } else {
            options = new String[]{EDIT_OPTION, DELETE_OPTION, MARK_COMPLETED_OPTION};
        }

        builder.setItems(options, (dialog, which) -> {
            String selectedOption = options[which];
            handleOptionClick(selectedOption, position);
        });

        builder.show();
    }

    private void handleOptionClick(String option, int position) {
        switch (option) {
            case EDIT_OPTION:
                openTaskEditActivity(position);
                break;

            case DELETE_OPTION:
                deleteTask(position);
                break;

            case MARK_COMPLETED_OPTION:
                markTaskAsCompleted(position);
                break;

            case MARK_NOT_COMPLETED_OPTION:
                unmarkTaskAsCompleted(position);
                break;
        }
    }

    private class TaskAdapter extends ArrayAdapter<String> {
        public TaskAdapter(Context context, ArrayList<String> taskList) {
            super(context, android.R.layout.simple_list_item_1, taskList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            String taskDetails = getItem(position);

            TextView textView = (TextView) view;
            textView.setText(Html.fromHtml(taskDetails, Html.FROM_HTML_MODE_COMPACT));

            if (isTaskCompleted(taskDetails)) {
                textView.setTextColor(Color.parseColor("#00ff00")); // Green
            } else {
                textView.setTextColor(Color.parseColor("#01cecf")); // Default color
            }
            return view;
        }
    }

    private void openTaskEditActivity(int position) {
        if (position >= 0 && position < taskList.size()) {
            Intent intent = new Intent(MainActivity.this, TaskEditActivity.class);
            intent.putExtra("taskDetails", taskList.get(position));
            intent.putExtra("position", position);
            startActivityForResult(intent, REQUEST_TASK_EDIT);
        }
    }

    private void deleteTask(int position) {
        if (position >= 0 && position < taskList.size()) {
            taskList.remove(position);
            taskAdapter.notifyDataSetChanged();
            saveTasksToSharedPreferences();
        }
    }

    private void markTaskAsCompleted(int position) {
        if (position >= 0 && position < taskList.size()) {
            String taskDetails = taskList.get(position);
            if (!isTaskCompleted(taskDetails)) {
                taskDetails = "<font color='#00ff00'>" + taskDetails + "</font>";
                taskList.set(position, taskDetails);
                taskAdapter.notifyDataSetChanged();
                saveTasksToSharedPreferences();
            }
        }
    }

    private void unmarkTaskAsCompleted(int position) {
        if (position >= 0 && position < taskList.size()) {
            String taskDetails = taskList.get(position);
            if (isTaskCompleted(taskDetails)) {
                taskDetails = taskDetails.replace("<font color='#00ff00'>", "<font color='#01cecf'>");
                taskList.set(position, taskDetails);
                taskAdapter.notifyDataSetChanged();
                saveTasksToSharedPreferences();
            }
        }
    }

    private boolean isTaskCompleted(String taskDetails) {
        return taskDetails != null && taskDetails.contains("<font color='#00ff00'>");
    }

    private void saveTasksToSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("TaskList", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Set<String> taskSet = new HashSet<>(taskList);
        editor.putStringSet("taskList", taskSet);

        editor.apply();
    }

    private void loadTasksFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("TaskList", Context.MODE_PRIVATE);
        Set<String> taskSet = sharedPreferences.getStringSet("taskList", new HashSet<>());

        taskList.clear();
        taskList.addAll(taskSet);
        taskAdapter.notifyDataSetChanged();
    }
}
