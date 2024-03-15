package com.example.todolist.OliDB;

import android.app.Application;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

/**
 * Represents a database manager for tasks.
 * Provides methods for interacting with the tasks table in the database.
 */
public class DB extends DBM {

    protected static String TABLE_NAME;
    protected static String COLUMN_ID = "id";

    protected static Application myApp;
    protected static DB instance = null;
    protected ArrayList<?> loaded;


    protected static String LOG_TAG = "Database";

    /**
     * Constructor for the DB class.
     *
     * @param app       The application instance.
     * @param dbName    The name of the database.
     * @param dbVersion The version of the database schema.
     */
    public DB(Application app, String dbName, int dbVersion) {
        super(app.getApplicationContext(), dbName, dbVersion);
        myApp = app;
        Log.d(LOG_TAG, "Application DB init");

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the tasks table
        db.execSQL(getCreateTableSQL().toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + getTableName());
        // trigger Create tables event again from upgraded table definition
        onCreate(db);
    }

    /**
     * Get the partially built SQL create table including table name and ID field
     * This allows for extending classes to reuse for other create table sql
     * @return Partially built SQL string
     */
    protected StringBuilder getCreateTableSQL()
    {
        return new StringBuilder("CREATE TABLE ")
                // get the table name to create that has set
                .append(getTableName())
                .append("(")
                .append(getIdFieldName())
                .append(" INTEGER PRIMARY KEY AUTOINCREMENT,");
    }

    protected String getDeleteRecordSQL(int id)
    {
        StringBuilder sql = new StringBuilder("DELETE FROM `")
                .append(getTableName())
                .append("` WHERE `")
                .append(getIdFieldName())
                .append("` = ")
                .append(id);
        return sql.toString();
    }

    protected String getLoadRecordsSQL()
    {
        return new StringBuilder("SELECT * FROM ")
                .append(getTableName())
                .append(" ORDER BY ")
                .append(getIdFieldName())
                .append(" DESC")
                .toString();
    }

    public String getTableName()
    {
        return TABLE_NAME;
    }

    /**
     * Getter for the ID field name
     * @return field ID name as a string
     */
    public String getIdFieldName()
    {
        return COLUMN_ID;
    }


    /**
     * Loads tasks from the database.
     */
    public void load() {
        loadAllRecords();
    }

    /**
     * Returns the loaded tasks.
     *
     * @return The loaded tasks.
     */
    public ArrayList loaded() {
        return loaded;
    }

    protected void addLoadedModelledRecord(Object record)
    {
        this.loaded().add(record);
    }

    protected Object getObjectModelFromCursor(Cursor cursor)
    {
        return new Object();
    }

    /**
     * Retrieves all tasks from the database.
     *
     * @return An ArrayList containing all tasks retrieved from the database.
     */
    public void loadAllRecords() {
        // Get a writable database instance
        SQLiteDatabase db = getReadableDatabase();

        // Execute a raw query to select all rows from the tasks table, ordered by ID in descending order
        Cursor cursor = db.rawQuery(getLoadRecordsSQL(), null);

        // Log a message indicating that all tasks are being loaded from the database
        Log.i(LOG_TAG, "Loading all records");

        // Initialize a counter to keep track of the position of each task in the list
        int counter = 0;

        // Check if the cursor is not null and move it to the first row
        if (cursor != null && cursor.moveToFirst()) {
            // Iterate over the rows in the cursor
            do {
                // Add the task to the task list
                addLoadedModelledRecord(getObjectModelFromCursor(cursor));
            } while (cursor.moveToNext()); // Move the cursor to the next row
            cursor.close(); // Close the cursor
        }

        db.close(); // Close the database connection

        // Log a message indicating the number of tasks loaded from the database
        StringBuilder msg = new StringBuilder();
        msg.append(this.loaded().size());
        msg.append(" records loaded");
        Log.i(LOG_TAG, msg.toString());
    }

}
