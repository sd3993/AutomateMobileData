package com.sd3993.amd;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;


public class Schedule extends Activity {

    public ListView listView;
    static public ScheduleAdapter scheduleAdapter;
    static ArrayList<Timers> timersArrayList = new ArrayList<Timers>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule);
        scheduleAdapter = new ScheduleAdapter(this,timersArrayList);
        scheduleAdapter.fragmentManager=getFragmentManager();
        listView = (ListView)this.findViewById(R.id.listView_items);
        listView.setAdapter(scheduleAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.schedule, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.add_alarm:
                addAlarm();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addAlarm() {
        timersArrayList.add(new Timers());
        scheduleAdapter.notifyDataSetChanged();
    }

    public static void updateList(String type, int pos, String time) {
        if(type.equalsIgnoreCase("start"))
            timersArrayList.get(pos).startTime = time;
        else if(type.equalsIgnoreCase("end"))
            timersArrayList.get(pos).endTime = time;
        scheduleAdapter.notifyDataSetChanged();
    }
}