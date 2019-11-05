package edu.semo.dryrun;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;


public class ModifyDateTime extends AppCompatActivity {
    private static final String TAG = "ModifyDateTime";
    private Button mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private Button mDisplayTime;
    private TimePickerDialog.OnTimeSetListener mTimeSetListener;
    private int[] dateArray;
    private int[] timeArray;
    private Button saveRunButton;
    private ArrayList<String> newRuns;
    private String origDate;
    private ArrayList<Date> sortedRuns;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_date_time);
        mDisplayDate = (Button) findViewById(R.id.dateEntry);
        mDisplayTime = (Button) findViewById(R.id.timeEntry);
        saveRunButton = (Button) findViewById(R.id.saveChangesButton);
        Intent in = getIntent();
        origDate = in.getStringExtra("edu.semo.dryrun.DATE");
        newRuns = in.getStringArrayListExtra("edu.semo.dryrun.ARRAY");
        this.dateArray=mapDate(origDate);
        this.timeArray=mapTime(origDate);

        String tempDate = Integer.toString(this.dateArray[1] + 1) + "/" + this.dateArray[0] + "/" + this.dateArray[2];
        mDisplayDate.setText(tempDate);
        String tempTime = String.format("%02d:%02d", this.timeArray[0], this.timeArray[1]);
        mDisplayTime.setText(tempTime);

        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(
                        ModifyDateTime.this,
                        android.R.style.Theme_DeviceDefault_Dialog_MinWidth,
                        mDateSetListener,
                        dateArray[2], dateArray[1], dateArray[0]);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                dateArray[0]=dayOfMonth;
                dateArray[1]=month;
                dateArray[2]=year;
                month++;
                String date = month + "/" + dayOfMonth + "/" + year;
                mDisplayDate.setText(date);
            }
        };

        mDisplayTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog dialog = new TimePickerDialog(
                        ModifyDateTime.this,
                        android.R.style.Theme_DeviceDefault_Dialog_MinWidth,
                        mTimeSetListener,
                        timeArray[0], timeArray[1], true
                );
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                timeArray[0]=hourOfDay;
                timeArray[1]=minute;
                String tempTime = String.format("%02d:%02d", hourOfDay, minute);
                mDisplayTime.setText(tempTime);
            }
        };

        saveRunButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeToFile();
            }
        });
    }

    //Really naive way of parsing from date string
    private int[] mapDate(String origDate)
    {
        String[] splitDate = origDate.split(" ");
        int[] dateArray = new int[3];
        //Put this into assets?
        //day of month
        dateArray[0]=Integer.parseInt(splitDate[2]);
        HashMap<String, Integer> monthMap = new HashMap<>();
        monthMap.put("Jan", 0);
        monthMap.put("Feb", 1);
        monthMap.put("Mar", 2);
        monthMap.put("Apr", 3);
        monthMap.put("May", 4);
        monthMap.put("Jun", 5);
        monthMap.put("Jul", 6);
        monthMap.put("Aug", 7);
        monthMap.put("Sep", 8);
        monthMap.put("Oct", 9);
        monthMap.put("Nov", 10);
        monthMap.put("Dec", 11);
        //month of year
        dateArray[1]=monthMap.get(splitDate[1]);
        //year
        dateArray[2]=Integer.parseInt(splitDate[5].trim());
        return dateArray;
    }
    private int[] mapTime(String origTime)
    {
        String[] splitTime= origTime.split(" ")[3].split(":");
        int[] timeArray = new int[3];
        timeArray[0] = Integer.parseInt(splitTime[0]);
        timeArray[1] = Integer.parseInt(splitTime[1]);
        timeArray[2] = Integer.parseInt(splitTime[2]);
        return timeArray;
    }

    private void writeToFile()
    {
        final Intent returnIntent = new Intent();
        String fileName = getResources().getString(R.string.datafile);

        for(String s: this.newRuns)
        {
            System.out.println(s);
        }
        try{
            File directory = getFilesDir();
            File f = new File(directory, fileName);
            f.createNewFile();
            FileOutputStream fos = new FileOutputStream(f, false);
            Date possibleDate = new Date(dateArray[2]-1900,dateArray[1],dateArray[0], timeArray[0], timeArray[1]);
            Date compareDate = new Date();
            if(compareDate.compareTo(possibleDate)>0)
            {
                Toast.makeText(getApplicationContext(), "You can not make a run in the past.", Toast.LENGTH_SHORT).show();
                return;
            }
            String entryText = possibleDate.toString()+"\n";
            if(newRuns.contains(entryText))
            {
                Toast.makeText(getApplicationContext(), "Run with this time already exists.", Toast.LENGTH_SHORT).show();
                return;
            }
            newRuns.add(entryText);
            this.horribleSort();
            int index = 0;
            while (index<newRuns.size()&&!newRuns.get(index).equals(origDate)) {
                fos.write(newRuns.get(index).getBytes());
                index++;
            }
            for (int i = ++index; i < newRuns.size(); i++)
                fos.write(newRuns.get(i).getBytes());
            fos.close();

            Toast.makeText(getApplicationContext(), "Run Saved", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK, returnIntent);
            finish();
        }
        catch(FileNotFoundException e)
        {
            Toast.makeText(getApplicationContext(), "Could not find file", Toast.LENGTH_SHORT).show();
            Log.d(TAG, e.toString());
            setResult(RESULT_CANCELED, returnIntent);
            finish();
        }
        catch(IOException e)
        {
            Log.d(TAG, e.toString());
            setResult(RESULT_CANCELED, returnIntent);
            finish();
        }
    }

    private void horribleSort()
    {
        this.sortedRuns = new ArrayList<Date>();
        for(String s: this.newRuns)
        {
            int[] tempDate;
            int[] tempTime;
            tempDate = mapDate(s);
            tempTime = mapTime(s);
            this.sortedRuns.add(new Date(tempDate[2]-1900, tempDate[1], tempDate[0], tempTime[0], tempTime[1]));
        }
        Collections.sort(this.sortedRuns);
        newRuns.clear();

        for(Date d: this.sortedRuns)
        {
            newRuns.add(d.toString()+"\n");
        }


    }

}

