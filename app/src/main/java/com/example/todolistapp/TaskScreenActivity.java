package com.example.todolistapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class TaskScreenActivity extends AppCompatActivity {

    private static final String TAG = "TaskScreenActivity";

    DatabaseHelper mDatabaseHelper = new DatabaseHelper(this);
    String Tag;
    private ListView mListView;

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
        setContentView(R.layout.activity_task_screen);
        mListView = (ListView) findViewById(R.id.tasks_list);

        Intent intent = getIntent();
        Tag = intent.getStringExtra("Tag");
        TextView pageTitle = (TextView) findViewById(R.id.tagTitleTextView);
        pageTitle.setText(String.format("%s:", Tag));

        Button new_task_button = findViewById(R.id.new_task_button);
        new_task_button.setOnClickListener(view -> NewTask());

        populateListView();
    }

    private void populateListView() {
        Cursor data = mDatabaseHelper.getUncompletedTasks();
        ArrayList<String> listData = new ArrayList<>();
        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, listData);
        mListView.setAdapter(adapter);
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mListView.setOnItemClickListener((adapterView, view, i, l) -> {
            if (mListView.isItemChecked(i)) {
                mDatabaseHelper.changeCompleted(listData.get(i));
                toastMessage(listData.get(i) + " marked as complete");
            } else {
                mDatabaseHelper.changeNotCompleted(listData.get(i));
                toastMessage(listData.get(i) + " task marked as not complete");
            }
        });
        while (data.moveToNext()) {
            if (Tag.equals(data.getString(2))) {
                listData.add(data.getString(0));
            }
        }
    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void NewTask() {
        Intent new_task_activity_intent = new Intent(this, NewTaskActivity.class);
        new_task_activity_intent.putExtra("Tag", Tag);
        startActivity(new_task_activity_intent);
    }
}