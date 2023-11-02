package com.example.todo;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TaskEditActivity extends Activity {
    private EditText editName;
    private Button editDueDateButton;
    private EditText editDescription;
    private Button saveButton;

    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private String taskDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_edit);

        editName = findViewById(R.id.editName);
        editDueDateButton = findViewById(R.id.editDueDateButton);
        editDescription = findViewById(R.id.editDescription);
        saveButton = findViewById(R.id.saveButton);

        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("MM/dd/yyyy");

        // Retrieve the task details from the intent
        Intent intent = getIntent();
        taskDetails = intent.getStringExtra("taskDetails");

        // Parse the task details to extract task name, due date, and description
        String[] parts = Html.fromHtml(taskDetails).toString().split("<br/>");
        editName.setText(parts[0].replaceAll("<b>|</b>", ""));
        if (parts.length > 1 && parts[1].startsWith("Due: ")) {
            String dueDate = parts[1].replace("Due: ", "");
            editDueDateButton.setText(dueDate); // Set the due date on the button
        }
        if (parts.length > 2) {
            editDescription.setText(parts[2]);
        }

        editDueDateButton.setOnClickListener(v -> showDatePickerDialog());

        saveButton.setOnClickListener(v -> saveEditedTask());
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    String selectedDate = dateFormat.format(calendar.getTime());
                    editDueDateButton.setText(selectedDate); // Display the picked date on the button
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void saveEditedTask() {
        String name = editName.getText().toString();
        String dueDate = editDueDateButton.getText().toString(); // Get the date from the button
        String description = editDescription.getText().toString();

        // Combine the edited task details
        String editedTaskDetails = "<b>" + name + "</b>";
        if (!dueDate.isEmpty()) {
            editedTaskDetails += "<br/>Due: " + dueDate;
        }
        if (!description.isEmpty()) {
            editedTaskDetails += "<br/>" + description;
        }

        // Return the edited task details to the MainActivity
        Intent resultIntent = new Intent();
        resultIntent.putExtra("editedTaskDetails", editedTaskDetails);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
