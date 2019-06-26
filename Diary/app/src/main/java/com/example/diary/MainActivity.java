package com.example.diary;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolBar;
    private EditText editText;
    private Button saveButton, nextButton, prevButton;

    private DataBaseIO dataBaseIO;
    private SQLiteDatabase db;
    private Calendar calendar;
    private int year, month, date;
    private String tableName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolBar = findViewById(R.id.ToolBar);
        editText = findViewById(R.id.EditText);
        saveButton = findViewById(R.id.FunctionButton);
        nextButton = findViewById(R.id.next_button);
        prevButton = findViewById(R.id.prev_button);

        setNotification();

        month = -1;
        calendar = Calendar.getInstance();
        setDate(-1);

        toolBar.inflateMenu(R.menu.toolbar_menu);
        toolBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.toolbar_export:
                        File path = getExternalFilesDir(null);
                        FileIO.exportItems(dataBaseIO.getAllItem(tableName), year, month, path);
                        showToast(path + "に保存しました。");
                        return true;
                }
                return false;
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
                setDate(-1);
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
                setDate(1);
            }
        });
    }


    private void saveData(){
        String saveItem = editText.getText().toString();
        String oldItem = dataBaseIO.readData(tableName, date);
        if (!saveItem.equals("") && !saveItem.equals(oldItem)) {
            dataBaseIO.insertData(tableName, date, saveItem);
            showToast("保存しました");
        }
    }


    private void setDate(int changeValue) {
        calendar.add(Calendar.DATE, changeValue);
        getCurrentDate();
        generateTableName();
        if ((month != calendar.get(Calendar.MONTH)) || year != calendar.get(Calendar.YEAR)) {
            dataBaseIO = new DataBaseIO(getApplicationContext(), tableName);
            Log.d("log", tableName);
        }

        String dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.JAPAN);
        toolBar.setTitle(year + "/" + month + "/" + date + " " + dayOfWeek);
        String body = dataBaseIO.readData(tableName, date);
        editText.setText(body, TextView.BufferType.NORMAL);
    }


    private void showToast (String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    private void getCurrentDate(){
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH)+1;
        date = calendar.get(Calendar.DATE);
    }

    private void generateTableName(){
        tableName = "\""+year+"-"+month+"\"";
    }

    @Override
    public void onBackPressed() {
        saveData();
        super.onBackPressed();
    }

    private void setNotification() {
        Calendar notificationCalendar = Calendar.getInstance();
        notificationCalendar.setTimeInMillis(System.currentTimeMillis());
        notificationCalendar.set(Calendar.HOUR, 6);
        notificationCalendar.set(Calendar.MINUTE, 0);
        notificationCalendar.add(Calendar.DATE, 1);

        Intent notificationIntent = new Intent(this, AlarmReceiver.class);
        notificationIntent.putExtra(AlarmReceiver.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(AlarmReceiver.NOTIFICATION_CONTENT, "通知");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC, notificationCalendar.getTimeInMillis(), pendingIntent);
    }
}
