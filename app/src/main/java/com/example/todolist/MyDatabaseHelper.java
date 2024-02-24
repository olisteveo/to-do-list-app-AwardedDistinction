package com.example.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    // Database and table details
    private static final String DATABASE_NAME = "task_database";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "tasks";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TASK_NAME = "task_name";
    private static final String COLUMN_TASK_DESCRIPTION = "task_description";
    private static final String COLUMN_COMPLETED = "completed";

    // SQL query to create the tasks table
    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_TASK_NAME + " TEXT," +
                    COLUMN_TASK_DESCRIPTION + " TEXT DEFAULT ''," + // Allow empty string
                    COLUMN_COMPLETED + " INTEGER DEFAULT 0)";

    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the tasks table
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        // Create tables again
        onCreate(db);
    }

    // Method to insert a new task into the database
    public long insertTask(String taskName, String taskDescription, int completed) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TASK_NAME, taskName);
        values.put(COLUMN_TASK_DESCRIPTION, taskDescription);
        values.put(COLUMN_COMPLETED, completed);
        long id = db.insert(TABLE_NAME, null, values);
        db.close();
        return id;
    }

    // Method to retrieve all tasks from the database
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Task task = new Task();
                task.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                task.setTaskName(cursor.getString(cursor.getColumnIndex(COLUMN_TASK_NAME)));
                task.setTaskDescription(cursor.getString(cursor.getColumnIndex(COLUMN_TASK_DESCRIPTION)));
                task.setCompleted(cursor.getInt(cursor.getColumnIndex(COLUMN_COMPLETED)));
                taskList.add(task);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return taskList;
    }

    // Additional CRUD methods can be added here
}
