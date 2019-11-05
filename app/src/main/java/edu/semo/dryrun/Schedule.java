package edu.semo.dryrun;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;


public class Schedule extends AppCompatActivity {
//for prototype the dates will be hardcoded in
    private ListView myListView;
    private ArrayList<String> runs;
    private Button makeNewRun;
    private static final String TAG = "Schedule";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        myListView = (ListView)findViewById(R.id.scheduleListView);
        myListView.setDivider(null);
        myListView.setDividerHeight(0);

        makeNewRun = (Button)findViewById(R.id.createRunButton);
        runs = new ArrayList<String>();
        fillRunsFromFile();
        makeNewRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent modifyScheduleOne = new Intent(getApplicationContext(), ModifyDateTime.class);
                modifyScheduleOne.putExtra("edu.semo.dryrun.DATE", new Date().toString());
                modifyScheduleOne.putExtra("edu.semo.dryrun.ARRAY", runs);
                startActivityForResult(modifyScheduleOne, 3);
            }
        });


        DateAdapter dateAdapter = new DateAdapter(this, runs);
        myListView.setAdapter(dateAdapter);

        myListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Intent deleteItem = new Intent(getApplicationContext(), PopUp.class);
                deleteItem.putExtra("edu.semo.dryrun.INDEX", position+"");
                deleteItem.putExtra("edu.semo.dryrun.ARRAY", runs);
                startActivityForResult(deleteItem, 1);
                return true;
            }
        });

        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "id: "+id);
                Intent modifySchedule = new Intent(getApplicationContext(), ModifyDateTime.class);
                modifySchedule.putExtra("edu.semo.dryrun.DATE", runs.get(position));
                modifySchedule.putExtra("edu.semo.dryrun.ARRAY", runs);
                startActivityForResult(modifySchedule, 2);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            if(resultCode==RESULT_OK)
            {
                Toast.makeText(getApplicationContext(), "Run Deleted", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(getIntent());
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Error Deleting Run", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(getIntent());
            }
        }
        else if (requestCode==2)
        {
            finish();
            startActivity(getIntent());
        }
        else if (requestCode==3)
        {
            finish();
            startActivity(getIntent());
        }
    }

    private void fillRunsFromFile()
    {
        runs.clear();
        readFromFile();
    }

    private void readFromFile()
    {
        String fileName = getResources().getString(R.string.datafile);
        try{
            FileInputStream fis = openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis);

            BufferedReader bf = new BufferedReader(isr);

            String lines;
            while((lines = bf.readLine()) != null)
            {

                runs.add(lines+"\n");
            }
        }
        catch(FileNotFoundException e)
        {
            Log.d(TAG, e.toString());
        }
        catch(IOException e)
        {
            Log.d(TAG, e.toString());
        }
    }
}
