package com.example.todolist.OliDB;

import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.todolist.Tasks.Task;

import java.util.ArrayList;

/**
 * Represents a database manager for tasks.
 * Provides methods for interacting with the tasks table in the database.
 */
public class TasksTable extends DBM {

    protected static Application myApp;
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
    private ArrayList<Task> loaded;
    private static TasksTable instance = null;


    private static String LOG_TAG = "Database";

    /**
     * Constructor for the DB class.
     *
     * @param app       The application instance.
     * @param dbName    The name of the database.
     * @param dbVersion The version of the database schema.
     */
    public TasksTable(Application app, String dbName, int dbVersion) {
        super(app.getApplicationContext(), dbName, dbVersion);
        myApp = app;
        Log.d(LOG_TAG, "Application DB init");

    }

    /**
     * Initializes a DB instance for the provided application, database name, and version.
     *
     * @param app       The application instance.
     * @param dbName    The name of the database.
     * @param dbVersion The version of the database schema.
     * @return          The initialised DB instance.
     */
    public static TasksTable initFor(Application app, String dbName, int dbVersion) {
        if (instance == null) {
            instance = new TasksTable(app, dbName, dbVersion);
        }
        return instance;
    }

    /**
     * Retrieves the DB instance.
     *
     * @return The DB instance.
     */
    public static TasksTable getInstance() {
        return instance;
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
        // trigger Create tables event again from upgraded table definition
        onCreate(db);
    }

    /**
     * Loads tasks from the database.
     */
    public void load() {
        loaded = getAllTasks();
    }

    /**
     * Returns the loaded tasks.
     *
     * @return The loaded tasks.
     */
    public ArrayList<Task> loaded() {
        return loaded;
    }

    /**
     * Inserts a new task into the database.
     *
     * @param taskName        The name of the task.
     * @param taskDescription The description of the task.
     * @param completed       The completion status of the task.
     * @return                The ID of the inserted task.
     */
    public long insertTask(String taskName, String taskDescription, int completed) {
        SQLiteDatabase db = getWritableDatabase();
        Task task = new Task();
        task.setTaskName(taskName);
        task.setTaskDescription(taskDescription);
        task.setCompleted(completed);
        ContentValues values = new ContentValues();
        values.put(COLUMN_TASK_NAME, taskName);
        values.put(COLUMN_TASK_DESCRIPTION, taskDescription);
        values.put(COLUMN_COMPLETED, completed);
        long id = getWritableDatabase().insert(TABLE_NAME, null, values);
        task.setId((int) id);
        db.close();
        StringBuilder msg = new StringBuilder("Inserted Task - ID ");
        msg.append(task.getId());
        Log.i(LOG_TAG, msg.toString());
        loaded.add(task);
        return id;
    }

    /**
     * Deletes a task from the database by its position in the list.
     *
     * @param position The position of the task in the tasks list.
     * @return         True if the task was successfully deleted, false otherwise.
     */
    public boolean deleteByPosition(int position) {
        // return flag assumes nothing deleted
        boolean deleted = false;
        for (Task task : loaded()) {
            if (task.getPosition() == position) {
                // run the SQL to delete this task from the database
                Log.i(LOG_TAG, "Deleting task - " + task.getTaskName());
                StringBuilder sql = new StringBuilder("DELETE FROM `")
                        .append(TABLE_NAME)
                        .append("` WHERE `")
                        .append(COLUMN_ID)
                        .append("` = ")
                        .append(task.getId());
                Log.i(LOG_TAG, "SQL" + sql.toString());
                getWritableDatabase().execSQL(sql.toString());
                StringBuilder msg = new StringBuilder("Deleted Task - " + task.getTaskName() + "- ID ")
                        .append(task.getId())
                        .append(" - Position ")
                        .append(task.getPosition());
                Log.i(LOG_TAG, msg.toString());
                loaded.remove(task);
                // update the deleted return flag
                deleted = true;
                // break out of the loop
                break;
            }
        }
        // trigger a reordering of the positions in loaded()
        setPositions(loaded);
        // return if a task was deleted or not
        return deleted;
    }

    /**
     * Retrieves all tasks from the database.
     *
     * @return An ArrayList containing all tasks retrieved from the database.
     */
    public ArrayList<Task> getAllTasks() {
        // Initialize an ArrayList to store the retrieved tasks
        ArrayList<Task> taskList = new ArrayList<>();

        // Get a writable database instance
        SQLiteDatabase db = getWritableDatabase();

        // Execute a raw query to select all rows from the tasks table, ordered by ID in descending order
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_ID + " DESC", null);

        // Log a message indicating that all tasks are being loaded from the database
        Log.i(LOG_TAG, "Loading all tasks");

        // Initialize a counter to keep track of the position of each task in the list
        int counter = 0;

        // Check if the cursor is not null and move it to the first row
        if (cursor != null && cursor.moveToFirst()) {
            // Iterate over the rows in the cursor
            do {
                // Create a new Task object for each row
                Task task = new Task();

                // Set the position of the task in the list
                task.setPosition(counter++);

                // Set the ID of the task from the cursor
                task.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));

                // Set the task name from the cursor
                task.setTaskName(cursor.getString(cursor.getColumnIndex(COLUMN_TASK_NAME)));

                // Set the task description from the cursor
                task.setTaskDescription(cursor.getString(cursor.getColumnIndex(COLUMN_TASK_DESCRIPTION)));

                // Set the completion status of the task from the cursor
                task.setCompleted(cursor.getInt(cursor.getColumnIndex(COLUMN_COMPLETED)));

                // Add the task to the task list
                taskList.add(task);
            } while (cursor.moveToNext()); // Move the cursor to the next row
            cursor.close(); // Close the cursor
        }

        db.close(); // Close the database connection

        // Log a message indicating the number of tasks loaded from the database
        StringBuilder msg = new StringBuilder();
        msg.append(taskList.size());
        msg.append(" tasks loaded");
        Log.i(LOG_TAG, msg.toString());

        // Return the ArrayList containing all tasks retrieved from the database
        return taskList;
    }


    /**
     * Sets positions for tasks in the loaded list.
     *
     * @param taskList The list of tasks.
     */
    protected void setPositions(ArrayList<Task> taskList) {
        int counter = 0;
        for (Task task : taskList) {
            task.setPosition(counter++);
        }
    }

}
