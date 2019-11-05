package edu.semo.dryrun;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Date;

public class DateAdapter extends BaseAdapter {

    LayoutInflater mInflater;
    ArrayList<String> runs;

    public DateAdapter(Context c, ArrayList<String> d)
    {
        mInflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        runs = d;
    }

    @Override
    public int getCount() {
        if(runs != null)
            return runs.size();
        return 0;
    }


    @Override
    public Object getItem(int position) {
        return runs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = mInflater.inflate(R.layout.date_layout, null);
        TextView dateTextView = (TextView)v.findViewById(R.id.dateTextView);
        dateTextView.setText(runs.get(position).trim());
        dateTextView.setTextSize(18);
        return v;
    }
}
