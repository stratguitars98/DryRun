package edu.semo.dryrun;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class PopUp extends AppCompatActivity {
    private Button confirmButton;
    private ArrayList<String> newRuns;
    private int origDateIndex;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int w = dm.widthPixels;
        int h = dm.heightPixels;
        getWindow().setLayout((int)(w*.8), (int)(h*.6));
        Intent in = getIntent();
        final Intent returnIntent = new Intent();
        origDateIndex = Integer.parseInt(in.getStringExtra("edu.semo.dryrun.INDEX"));
        newRuns = in.getStringArrayListExtra("edu.semo.dryrun.ARRAY");
        confirmButton = (Button) findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newRuns.remove(origDateIndex);
                String fileName = getResources().getString(R.string.datafile);
                try {
                    File directory = getFilesDir();
                    File f = new File(directory, fileName);
                    f.createNewFile();
                    FileOutputStream fos = new FileOutputStream(f, false);
                    for (int i = 0; i < newRuns.size(); i++)
                        fos.write(newRuns.get(i).getBytes());
                    setResult(RESULT_OK, returnIntent);
                    finish();
                }
                catch(FileNotFoundException e)
                {
                    setResult(RESULT_CANCELED, returnIntent);
                    finish();
                }
                catch(IOException e)
                {
                    setResult(RESULT_CANCELED, returnIntent);
                    finish();
                }
            }
        });

    }
}
