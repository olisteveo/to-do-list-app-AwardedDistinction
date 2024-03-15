// AddTaskActivity.java
package com.example.todolist.Tasks;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.annotation.SuppressLint;

import com.example.todolist.R;


public class AddTaskActivity extends AppCompatActivity {

    private EditText editTextTaskName;
    private EditText editTextTaskDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        // Initialize views
        editTextTaskName = findViewById(R.id.editTextTaskName);
        editTextTaskDescription = findViewById(R.id.editTextTaskDescription);

        // Set click listener for "Add Task" button
        Button buttonAddTask = findViewById(R.id.buttonAddTask);
        buttonAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTask();
            }
        });
    }

    // Method to add task
    private void addTask() {
        String taskName = editTextTaskName.getText().toString().trim();
        String taskDescription = editTextTaskDescription.getText().toString().trim();

        if (!taskName.isEmpty()) {
            // Pass data back to MainActivity
            Intent intent = new Intent();
            intent.putExtra("taskName", taskName);
            intent.putExtra("taskDescription", taskDescription);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            Toast.makeText(this, "Please enter a task name", Toast.LENGTH_SHORT).show();
        }
    }


    // Method to handle back button press
    @Override
    @SuppressLint("MissingSuperCall")
    public void onBackPressed() {
        // Navigate back to MainActivity
        finish();
    }
}
