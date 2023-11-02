package com.example.todo;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

public class TaskInputActivity extends Activity {

    private EditText taskNameEditText;
    private Button dueDateButton; // Use a Button for due date display
    private EditText descriptionEditText;
    private Calendar calendar;
    private int year, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_input);

        taskNameEditText = findViewById(R.id.taskNameEditText);
        dueDateButton = findViewById(R.id.dueDateButton); // Use the Button
        descriptionEditText = findViewById(R.id.descriptionEditText);
        Button submitButton = findViewById(R.id.submitButton);
        calendar = Calendar.getInstance();

        dueDateButton.setOnClickListener(v -> {
            // Show a date picker dialog
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    TaskInputActivity.this,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        // Update the dueDateButton text with the selected date
                        monthOfYear++; // Month starts from 0
                        String formattedDate = String.format("%02d/%02d/%04d", monthOfYear, dayOfMonth, year);
                        dueDateButton.setText("Due Date: " + formattedDate);
                    },
                    year, month, day);
            datePickerDialog.show();
        });

        submitButton.setOnClickListener(v -> {
            // Get the entered task details
            String taskName = taskNameEditText.getText().toString();
            String dueDate = dueDateButton.getText().toString().replace("Due Date: ", ""); // Extract the date
            String description = descriptionEditText.getText().toString();

            // Check if the taskName is empty
            if (taskName.isEmpty()) {
                // Display a Toast message
                Toast.makeText(TaskInputActivity.this, "Task Name cannot be empty", Toast.LENGTH_SHORT).show();
            } else {
                // Create an Intent to pass data back to MainActivity
                Intent intent = new Intent();
                intent.putExtra("taskName", taskName);
                intent.putExtra("dueDate", dueDate);
                intent.putExtra("description", description);

                // Set the result and finish the activity
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}