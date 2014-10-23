package com.sd3993.amd;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;

public class Schedule extends Activity {

    static public ScheduleAdapter scheduleAdapter;
    static ArrayList<Timers> timersArrayList = new ArrayList<Timers>();
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule);
        scheduleAdapter = new ScheduleAdapter(this, getFragmentManager());
        listView = (ListView) this.findViewById(R.id.listView);
        listView.setAdapter(scheduleAdapter);
        SwipeToDismissListener touchListener = new SwipeToDismissListener(listView,
                new SwipeToDismissListener.DismissCallbacks() {
                    @Override
                    public boolean canDismiss(int position) {
                        return true;
                    }

                    @Override
                    public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                        for (int position : reverseSortedPositions) {
                            timersArrayList.remove(timersArrayList.get(position));
                        }
                        scheduleAdapter.notifyDataSetChanged();
                    }
                }
        );
        listView.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        listView.setOnScrollListener(touchListener.makeScrollListener());
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
}