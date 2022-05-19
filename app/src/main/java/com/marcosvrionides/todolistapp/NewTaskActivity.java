package com.marcosvrionides.todolistapp;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class NewTaskActivity extends AppCompatActivity {

    DatabaseHelper mDatabaseHelper;
    String passedTag;
    private EditText editTaskName;
    private EditText editTag;
    private Spinner tagSelect;
    private DatePickerDialog datePickerDialog;
    private Button dateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        createNotificationChannel();
        initDatePicker();
        editTaskName = (EditText) findViewById(R.id.task_name);
        editTag = (EditText) findViewById(R.id.tag);

        Intent intent = getIntent();
        passedTag = intent.getStringExtra("Tag"); //if a user is on the task screen and taps on add item the tag is passed here to auto-fill the tag selection part.

        dateButton = findViewById(R.id.due_date);
        dateButton.setText("");

        tagSelect = (Spinner) findViewById(R.id.tag_spinner);
        mDatabaseHelper = new DatabaseHelper(this);
        populateTagsDropdown(); //get the existing tags from the database and populate the drop down with them
        tagSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selection = tagSelect.getSelectedItem().toString();
                if (selection != "Select tag") {
                    editTag.setText(selection);
                } else {
                    editTag.setText(passedTag);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        Button createButton = findViewById(R.id.create_task_button);
        createButton.setOnClickListener(view -> {
            String TaskName = editTaskName.getText().toString();
            String DueDate = dateButton.getText().toString();
            String Tag = editTag.getText().toString();
            if (TaskName.length() == 0 || Tag.length() == 0) {
                toastMessage("Task and tag fields must not be empty");
            } else {
                AddData(TaskName, DueDate, Tag, 0); //add the task to the database
                //reset the input fields
                editTaskName.setText("");
                editTag.setText("");

                if (dateButton.getText() != "") {
                    //get the input date
                    String myDate = (String) dateButton.getText();
                    //parse it so that it is given a time of 9am
                    LocalDateTime localDateTime = LocalDate.parse(myDate, DateTimeFormatter.ofPattern("dd/MM/yyyy")).atTime(9, 0);
                    //convert it to milliseconds
                    long inputDateInMillis = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                    //calculate the milliseconds from now till the selected time
                    long timeAtButtonClick = System.currentTimeMillis();
                    long timeTillNotificationSent = inputDateInMillis - timeAtButtonClick;
                    //setup a notification for the chosen date at time 9am
                    Intent intent1 = new Intent(this, ReminderBroadcast.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE), intent1, 0);
                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + timeTillNotificationSent, pendingIntent);
                    toastMessage("Reminder set for the " + myDate + " at 9:00 am");
                }
                //refresh the tags dropdown to show the new tag if the user made one
                populateTagsDropdown();
                passedTag = "";
                finish();
            }
        });
    }

    //create the notification channel for the task reminders
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("task_reminders", "Task Reminders", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Channel for reminders");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    //date picker for the user to select a due date
    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, month, day) -> {
            month = month + 1;
            String date = makeDateString(day, month, year);
            dateButton.setText(date);
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.BUTTON_POSITIVE;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()); //user cannot set a due date that is in the past
    }

    //take user's input date and convert to string
    private String makeDateString(int day, int month, int year) {
        return doubleDigits(day) + "/" + doubleDigits(month) + "/" + year;
    }

    //convert month and day into 2 digits
    private String doubleDigits(int i) {
        if (String.valueOf(i).length() == 1) {
            return "0" + String.valueOf(i);
        } else {
            return String.valueOf(i);
        }
    }

    public void openDatePicker(View view) {
        datePickerDialog.show();
    }

    public void AddData(String task, String due_date, String tag, int complete) {
        boolean insertData = mDatabaseHelper.addData(task, due_date, tag, complete);
        if (insertData) {
            toastMessage("Task created");
        } else {
            toastMessage("Error creating task");
        }
    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void populateTagsDropdown() {
        Cursor data = mDatabaseHelper.getAllTags();
        ArrayList<String> listData = new ArrayList<>();
        listData.add("Select tag");
        while (data.moveToNext()) {
            if (!listData.contains(data.getString(0))) { //make sure the tag is not already in the list
                listData.add(data.getString(0));
            }
        }
        SpinnerAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, listData);
        tagSelect.setAdapter((SpinnerAdapter) adapter);
    }
}