package com.example.todolistapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper mDatabaseHelper;
    float x1, x2; //used for swipe gestures
    private ListView TagsListView;
    private ListView CompletedListView;

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //create notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel("Task Reminders", "Task Reminders", NotificationManager.IMPORTANCE_DEFAULT);
            channel1.setDescription("Task reminders");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
        }

        TagsListView = (ListView) findViewById(R.id.tags_list);
        TagsListView.setOnItemClickListener((adapterView, view, i, l) -> TaskScreen(String.valueOf(adapterView.getItemAtPosition(i))));
        mDatabaseHelper = new DatabaseHelper(this);
        populateListView(0);

        CompletedListView = (ListView) findViewById(R.id.completed_tasks);
        populateListView(1);

        Button new_task_button = findViewById(R.id.new_task_button);
        new_task_button.setOnClickListener(view -> NewTask());

        Button tutorial_button = findViewById(R.id.tutorial_button);
        tutorial_button.setOnClickListener(view -> TutorialScreen());

        Button clear_tasks_button = findViewById(R.id.clear_tasks_button);
        clear_tasks_button.setOnClickListener(view -> {
            mDatabaseHelper.clearCompletedTasks();
            onRestart();
        });

    }

    public boolean onTouchEvent(MotionEvent touchevent) {
        switch (touchevent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = touchevent.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = touchevent.getX();
                if ((x1 - x2) > 300) {
                    TutorialScreen();
                    Log.d("tag", "x1: " + x1 + ", x2: " + x2);
                }
                break;
        }
        return false;
    }

    private void populateListView(int complete) {
        Cursor data = null;
        if (complete == 0) {
            data = mDatabaseHelper.getUncompletedTasks();
        } else if (complete == 1) {
            data = mDatabaseHelper.getCompletedTasks();
        }
        ArrayList<String> listData = new ArrayList<>();
        while (data.moveToNext()) {
            if (complete == 0) {
                if (!listData.contains(data.getString(2))) {
                    listData.add(data.getString(2));
                }
            } else if (complete == 1) {
                listData.add(data.getString(0));
            }
        }
        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
        if (complete == 0) {
            TagsListView.setAdapter(adapter);
        } else if (complete == 1) {
            CompletedListView.setAdapter(adapter);
        }
    }

    private void NewTask() {
        Intent new_task_activity_intent = new Intent(this, NewTaskActivity.class);
        startActivity(new_task_activity_intent);
    }

    private void TutorialScreen() {
        Intent tutorial_activity_intent = new Intent(this, TutorialActivity.class);
        startActivity(tutorial_activity_intent);
    }

    private void TaskScreen(String Tag) {
        Intent task_screen_activity_intent = new Intent(this, TaskScreenActivity.class);
        task_screen_activity_intent.putExtra("Tag", Tag);
        startActivity(task_screen_activity_intent);
    }
}