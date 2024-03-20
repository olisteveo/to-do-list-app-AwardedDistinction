package com.example.todolist.OliDB;

import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.todolist.OliDB.Models.Task;

import java.util.ArrayList;

/**
 * Represents a database manager for tasks.
 * Provides various methods for interacting with the tasks table in the database.
 */
public class TasksTable extends DB {

    protected static final String TABLE_NAME = "tasks";
    protected static final String COLUMN_ID = "id";
    protected static final String COLUMN_TASK_NAME = "task_name";
    protected static final String COLUMN_TASK_DESCRIPTION = "task_description";
    protected static final String COLUMN_COMPLETED = "completed";
    private ArrayList<Task> loaded;
    private static TasksTable instance = null;

    /**
     * Constructor for the TasksTable class.
     *
     * @param app       The application instance.
     * @param dbName    The name of the database.
     * @param dbVersion The version of the database schema.
     */
    public TasksTable(Application app, String dbName, int dbVersion) {
        super(app, dbName, dbVersion);
        loaded = new ArrayList<Task>();
        Log.d(LOG_TAG, "Tasks DB init");

    }

    /**
     * Initialises a DB instance for the provided application, DB name, and version.
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
        super.onCreate(db);
    }

    // Method overridden to append custom table column definitions
    @Override
    protected StringBuilder getCreateTableSQL() {
        return super.getCreateTableSQL()
                .append(COLUMN_TASK_NAME + " TEXT,")
                .append(COLUMN_TASK_DESCRIPTION + " TEXT DEFAULT '',")
                .append(COLUMN_COMPLETED + " INTEGER DEFAULT 0)")
                // close the opening parenthesis created from the super call that
                // added the definition of the primary key field
                .append(')');
    }

    @Override
    public String getTableName()
    {
        return TABLE_NAME;
    }

    /**
     * Getter for the ID field name
     * @return the field ID name as a string
     */
    @Override
    public String getIdFieldName()
    {
        return COLUMN_ID;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
    }

    /**
     * Loads the tasks from the database.
     */
    public void load() {
        loadAllRecords();
    }

    /**
     * Returns the loaded tasks.
     *
     * @return The loaded tasks.
     */
    @Override
    public ArrayList<Task> loaded() {
        return loaded;
    }

    /**
     * Retrieves a model object from a cursor.
     *
     * @param cursor                    The cursor to retrieve data from.
     * @return                          Returns the model based task record.
     * @throws IllegalArgumentException Returns the zero-based index for the given column name,
     *                                   or throws IllegalArgumentException if the column doesn't exist.
     */
    protected Task getObjectModelFromCursor(Cursor cursor)
    {
        Task task = Task.newFromInserted(
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASK_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASK_DESCRIPTION)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COMPLETED))
        );
        return task;
    }

    @Override
    protected void addLoadedModelledRecord(Object record)
    {
        this.loaded().add((Task) record);
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
        ContentValues values = new ContentValues();
        values.put(COLUMN_TASK_NAME, taskName);
        values.put(COLUMN_TASK_DESCRIPTION, taskDescription);
        values.put(COLUMN_COMPLETED, completed);
        long id = getWritableDatabase().insert(getTableName(), null, values);
        Task task = Task.newFromInserted((int) id, taskName, taskDescription, completed);
        task.setId((int) id);
        db.close();
        StringBuilder msg = new StringBuilder("Inserted Task - ID ");
        msg.append(task.getId());
        Log.i(LOG_TAG, msg.toString());
        loaded.add(0, task);
        return id;
    }

    /**
     * Deletes a task from the database by its position in the list.
     *
     * @param position The position of the task in the tasks list.
     * @return         True if the task was successfully deleted, false otherwise.
     */
    public boolean deleteByPosition(int position) {
        Task task = loaded().get(position);
        Log.i(LOG_TAG, "Deleting task - " + task.getTaskName());
        String sql = getDeleteRecordSQL(task.getId());
        Log.i(LOG_TAG, "SQL" + sql);
        getWritableDatabase().execSQL(sql);
        StringBuilder msg = new StringBuilder("Deleted Task - " + task.getTaskName() + "- ID ")
                .append(task.getId())
                .append(" - Position ")
                .append(position);
        Log.i(LOG_TAG, msg.toString());
        loaded.remove(task);
        return true;
    }

}
