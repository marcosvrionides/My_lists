package com.example.todolistapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 4;
    private static final String TASKS_TABLE_NAME = "tasks";
    private static final String TASK_NAME = "name";
    private static final String DUE_DATE = "due_date";
    private static final String TASK_TAG = "tag";
    private static final String COMPLETED = "completed";


    public DatabaseHelper(Context context) {
        super(context, TASKS_TABLE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTable = "CREATE TABLE " + TASKS_TABLE_NAME + " (" + TASK_NAME + " TEXT, " + DUE_DATE + " TEXT, " + TASK_TAG + " TEXT, " + COMPLETED + " INTEGER)";
        sqLiteDatabase.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TASKS_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    //add a task to the "tasks" table in the database
    public boolean addData(String item1, String item2, String item3, int item4) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TASK_NAME, item1);
        contentValues.put(DUE_DATE, item2);
        contentValues.put(TASK_TAG, item3);
        contentValues.put(COMPLETED, item4);

        //used to check if the data was added successfully
        long result = sqLiteDatabase.insert(TASKS_TABLE_NAME, null, contentValues);
        return result != -1;

    }

    //get all uncompleted tasks from the database
    public Cursor getUncompletedTasks() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String query = "SELECT * FROM " + TASKS_TABLE_NAME + " WHERE " + COMPLETED + " = 0;";
        return sqLiteDatabase.rawQuery(query, null);
    }

    //get all completed tasks from the database
    public Cursor getCompletedTasks() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String query = "SELECT * FROM " + TASKS_TABLE_NAME + " WHERE " + COMPLETED + " = 1;";
        return sqLiteDatabase.rawQuery(query, null);
    }

    //update the database to set a task as completed
    public void changeCompleted(String taskName) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String query = "UPDATE " + TASKS_TABLE_NAME + " SET " + COMPLETED + " = 1 WHERE " + TASK_NAME + " = " + "'" + taskName + "'";
        sqLiteDatabase.execSQL(query);
    }

    //update the database to set a task as not completed
    public void changeNotCompleted(String taskName) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String query = "UPDATE " + TASKS_TABLE_NAME + " SET " + COMPLETED + " = 0 WHERE " + TASK_NAME + " = " + "'" + taskName + "'";
        sqLiteDatabase.execSQL(query);
    }

    //delete all completed tasks from the database
    public void clearCompletedTasks() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String query = "DELETE FROM " + TASKS_TABLE_NAME + " WHERE " + COMPLETED + " = 1";
        sqLiteDatabase.execSQL(query);
    }

    //get all tags from the database (to display in the tag select dropdown when making a new task)
    public Cursor getAllTags() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String query = "SELECT tag FROM " + TASKS_TABLE_NAME + " WHERE " + COMPLETED + " = 0";
        return sqLiteDatabase.rawQuery(query, null);
    }
}
