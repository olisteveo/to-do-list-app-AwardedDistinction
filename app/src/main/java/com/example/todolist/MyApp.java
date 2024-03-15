package com.example.todolist;

import android.app.Application;
import android.util.Log;
import com.example.todolist.OliDB.DB;

public class MyApp extends Application {
    private static final String DATABASE_NAME = "task_database";
    private static final int DATABASE_VERSION = 1;

    protected DB tasksDB;

    private static final String TAG = "MyApp";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Application onCreate()");
        tasksDB = DB.initFor(this, DATABASE_NAME, DATABASE_VERSION);
        tasksDB.load();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.d(TAG, "Application onTerminate()");
    }
}
