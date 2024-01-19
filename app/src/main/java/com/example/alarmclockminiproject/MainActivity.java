package com.example.alarmclockminiproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.alarmclockminiproject.databinding.ActivityMainBinding;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MaterialTimePicker timePicker;
    private Calendar alarmTime;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        alarmTime = Calendar.getInstance();

        createNotificationChannel();

        binding.selectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePicker = new MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_12H)
                        .setHour(12)
                        .setMinute(0)
                        .setTitleText("Select Alarm Time")
                        .build();
                timePicker.show(getSupportFragmentManager(), "androidknowledge");
                timePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int hour = timePicker.getHour();
                        int minute = timePicker.getMinute();
                        String time = String.format("%02d:%02d %s", hour, minute, (hour >= 12) ? "PM" : "AM");
                        binding.selectTime.setText(time);
                        alarmTime.set(Calendar.HOUR_OF_DAY, hour);
                        alarmTime.set(Calendar.MINUTE, minute);
                        alarmTime.set(Calendar.SECOND, 0);
                        alarmTime.set(Calendar.MILLISECOND, 0);
                    }
                });
            }
        });

        binding.setAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (timePicker == null) {
                    Toast.makeText(MainActivity.this, "Please select a time for the alarm", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Set the alarm
                setAlarm(alarmTime);

                Toast.makeText(MainActivity.this, "Alarm Set", Toast.LENGTH_SHORT).show();
            }
        });

        binding.cancelAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (alarmManager != null) {
                    alarmManager.cancel(pendingIntent);
                    Toast.makeText(MainActivity.this, "Alarm Canceled", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setAlarm(Calendar alarmTime) {
        Intent intent = new Intent(this, AlarmReciever.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        long alarmTimeMillis = alarmTime.getTimeInMillis();

        // Set the alarm to trigger at the specified time
        alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTimeMillis, pendingIntent);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "akchannel";
            String desc = "Channel for Alarm Manager";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("androidknowledge", name, importance);
            channel.setDescription(desc);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
