package com.example.gkoutsodi.todo_livedata;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.gkoutsodi.todo_livedata.DataBase.DbContract;
import com.example.gkoutsodi.todo_livedata.DataBase.DbHelper;
import com.example.gkoutsodi.todo_livedata.Todo;

import java.util.ArrayList;
import java.util.List;

public class TodoViewModel extends AndroidViewModel{

    private MutableLiveData<List<Todo>> mTodos;
    private DbHelper mTodoDbHelper;

    TodoViewModel(Application application) {
        super(application);
        mTodoDbHelper = new DbHelper(application);
    }

    public LiveData<List<Todo>> getTodos() {
        if (mTodos == null) {
            mTodos = new MutableLiveData<List<Todo>>();
            loadTodos();
        }
        return mTodos;
    }

    private void loadTodos() {
        List<Todo> newTodos = new ArrayList<Todo>();
        SQLiteDatabase db = mTodoDbHelper.getReadableDatabase();
        Cursor cursor = db.query(DbContract.TodoEntry.TABLE,
                new String[]{
                        DbContract.TodoEntry._ID,
                        DbContract.TodoEntry.COL_TODO_NAME,
                        DbContract.TodoEntry.COL_TODO_DATE
                },
                null, null, null, null, null);
        while (cursor.moveToNext()) {
            int idxId = cursor.getColumnIndex(DbContract.TodoEntry._ID);
            int idxName = cursor.getColumnIndex(DbContract.TodoEntry.COL_TODO_NAME);
            int idxDate = cursor.getColumnIndex(DbContract.TodoEntry.COL_TODO_DATE);
            newTodos.add(new Todo(cursor.getLong(idxId), cursor.getString(idxName), cursor.getLong(idxDate)));
        }
        cursor.close();
        db.close();
        mTodos.setValue(newTodos);
    }

    public void addTodo(String name, long date) {
        SQLiteDatabase db = mTodoDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DbContract.TodoEntry.COL_TODO_NAME, name);
        values.put(DbContract.TodoEntry.COL_TODO_DATE, date);
        long id = db.insertWithOnConflict(DbContract.TodoEntry.TABLE,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
        List<Todo> todos = mTodos.getValue();
        ArrayList<Todo> clonedTodos = new ArrayList<Todo>(todos.size());
        for(int i = 0; i < todos.size(); i++){
            clonedTodos.add(new Todo(todos.get(i)));
        }
        Todo todo = new Todo(id , name, date);
        clonedTodos.add(todo);
        mTodos.setValue(clonedTodos);
    }

    public void removeTodo(long id) {
        SQLiteDatabase db = mTodoDbHelper.getWritableDatabase();
        db.delete(
                DbContract.TodoEntry.TABLE,
                DbContract.TodoEntry._ID + " = ?",
                new String[]{Long.toString(id)}
        );
        db.close();
        List<Todo> todos = mTodos.getValue();
        ArrayList<Todo> clonedTodos = new ArrayList<Todo>(todos.size());
        for(int i = 0; i < todos.size(); i++){
            clonedTodos.add(new Todo(todos.get(i)));
        }
        int index = -1;
        for(int i = 0; i < clonedTodos.size(); i++){
            Todo todo = clonedTodos.get(i);
            if (todo.getId() == id) {
                index = i;
            }
        }
        if (index != -1) {
            clonedTodos.remove(index);
        }
        mTodos.setValue(clonedTodos);
    }

}