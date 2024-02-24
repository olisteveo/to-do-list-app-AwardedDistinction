package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> taskList;
    private ArrayAdapter<String> adapter;
    private static final int ADD_TASK_REQUEST = 1; // Request code for AddTaskActivity
    private static final int EDIT_TASK_REQUEST = 2; // Request code for EditTaskActivity
    private EditText editText;
    private MyDatabaseHelper dbHelper; // Database helper instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialise task list and adapter with custom layout
        taskList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, R.layout.custom_task_list_item, R.id.textViewTaskName, taskList);
        // Set up the ListView
        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);

        // Find the EditText
        editText = findViewById(R.id.editText);

        // Add bottom border to EditText
        addBottomBorder();

        // Initialize database helper
        dbHelper = new MyDatabaseHelper(this);

        ArrayList tasks = dbHelper.getAllTasks();
        Log.d("database", tasks.toString());
        for (Object task: tasks ) {
            Log.d("database", task.toString());
            taskList.add(0, task.toString()); // Add task to the beginning of the list
        }

        // Set click listener for list items
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editOrDeleteTask(position);
            }
        });
    }

    // Method to add task
    public void addTask(View view) {
        EditText editText = findViewById(R.id.editText);
        String task = editText.getText().toString().trim();

        if (!task.isEmpty()) {
            taskList.add(0, task); // Add task to the beginning of the list
            adapter.notifyDataSetChanged();
            editText.getText().clear();
            dbHelper.insertTask(task, "",0);
        } else {
            Toast.makeText(this, "Please enter a task", Toast.LENGTH_SHORT).show();
        }
    }

    private void editOrDeleteTask(final int position) {
        // Display options to edit or delete the selected task
        final String task = taskList.get(position);

        // Implement editing logic here
        // For simplicity, show a toast indicating the selected task and its position in the list
        Toast.makeText(MainActivity.this, "Selected task: " + task + "\nPosition: " + position, Toast.LENGTH_SHORT).show();
    }

    // Method to delete a task
    public void deleteTask(View view) {
        // Find the parent view of the delete button, which is the list item
        View listItem = (View) view.getParent();

        // Find the index of the list item
        final int position = ((ListView) findViewById(R.id.listView)).getPositionForView(listItem);

        // Show the delete confirmation dialog
        showDeleteConfirmationDialog(position);
    }

    // Method to show delete confirmation dialog
    private void showDeleteConfirmationDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this task?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // If user confirms deletion, delete the task
                        taskList.remove(position);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(MainActivity.this, "Task deleted", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // If user cancels deletion, do nothing
                    }
                })
                .create()
                .show();
    }

    // Method to navigate to AddTaskActivity
    public void navigateToAddTask(View view) {
        Intent intent = new Intent(this, AddTaskActivity.class);
        startActivityForResult(intent, ADD_TASK_REQUEST); // Start AddTaskActivity with request code
    }

    // Method to navigate to EditTaskActivity
    public void navigateToEditTask(View view) {
        Intent intent = new Intent(this, EditTaskActivity.class);
        startActivity(intent);
    }

    // Handle the result from AddTaskActivity and EditTaskActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == ADD_TASK_REQUEST) {
                // Extract the task details from the intent
                String taskName = data.getStringExtra("taskName");
                String taskDescription = data.getStringExtra("taskDescription");

                // Construct the task string
                String task = taskName + ": " + taskDescription;

                // Add the task to the top of the list
                taskList.add(0, task);
                dbHelper.insertTask(taskName, taskDescription,0);
                adapter.notifyDataSetChanged();
            } else if (requestCode == EDIT_TASK_REQUEST) {
                // Handle the result from EditTaskActivity if needed
            }
        }
    }

    // Method to add bottom border to EditText
    private void addBottomBorder() {
        // Set bottom border color and height
        Drawable drawable = getResources().getDrawable(R.drawable.edittext_bottom_border);
        drawable.setColorFilter(Color.parseColor("#FF4081"), PorterDuff.Mode.SRC_ATOP);
        editText.setBackground(drawable);
    }
}
