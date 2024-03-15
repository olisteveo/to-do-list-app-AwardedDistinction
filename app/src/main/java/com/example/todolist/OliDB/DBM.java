package com.example.todolist.OliDB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Abstract class representing a database manager.
 * Provides basic functionality for interacting with SQLite databases.
 */
abstract public class DBM extends SQLiteOpenHelper {

    /**
     * Constructor for the database manager.
     *
     * @param context   The context in which the database manager is instantiated.
     * @param dbName    The name of the database.
     * @param dbVersion The version of the database schema.
     */
    public DBM(Context context, String dbName, int dbVersion) {
        super(context, dbName, null, dbVersion);
    }
}
