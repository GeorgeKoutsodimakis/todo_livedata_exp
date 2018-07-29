package com.example.gkoutsodi.todo_livedata.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

    public DbHelper(Context context){
        super(context, DbContract.DB_NAME, null, DbContract.DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + DbContract.TodoEntry.TABLE + " ( " +
                DbContract.TodoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DbContract.TodoEntry.COL_TODO_NAME + " TEXT NOT NULL, " +
                DbContract.TodoEntry.COL_TODO_DATE + " INTEGER NOT NULL);";
        db.execSQL(createTable);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " +
                DbContract.TodoEntry.TABLE);
        onCreate(db);
    }
}
