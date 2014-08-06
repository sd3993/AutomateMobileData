package com.sd3993.amd;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;

public class ScheduleAdapter extends BaseAdapter {

    static ArrayList<Timers> timersArrayList;
    static String type;
    static int pos;
    Activity context;
    FragmentManager fragmentManager;

    public ScheduleAdapter(Activity context, ArrayList<Timers> timersArrayList) {
        super();
        this.context = context;
        ScheduleAdapter.timersArrayList = timersArrayList;
    }

    @Override
    public int getCount() {
        return (timersArrayList.size());
    }

    @Override
    public Object getItem(int position) {
        return timersArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        Holder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_row, parent, false);
            holder = new Holder();

            holder.s = (TextView) convertView.findViewById(R.id.startTime);
            holder.e = (TextView) convertView.findViewById(R.id.endTime);
            holder.c = (CheckBox) convertView.findViewById(R.id.alarm_enabled);

            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        (holder.s).setText((timersArrayList.get(position)).startTime);
        (holder.e).setText((timersArrayList.get(position)).endTime);

        (holder.s).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog("start", position);
            }
        });
        (holder.e).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog("end", position);
            }
        });

        return convertView;
    }

    public void showTimePickerDialog(String type, int pos) {
        ScheduleAdapter.type = type;
        ScheduleAdapter.pos = pos;
        DialogFragment timePickerFragment = new TimePickerFragment();
        timePickerFragment.show(fragmentManager, "timePicker");
    }

    static class Holder {
        TextView s;
        TextView e;
        CheckBox c;
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker

            String initTime = "";

            if (type.equalsIgnoreCase("start")) {
                initTime = (timersArrayList.get(pos)).startTime + ' ';
                if (initTime.equals("Start Time "))
                    initTime = "00:00 ";
            } else if (type.equalsIgnoreCase("end")) {
                initTime = (timersArrayList.get(pos)).endTime + ' ';
                if (initTime.equals("End Time "))
                    initTime = "00:00 ";
            }

            int hour = Integer.parseInt(initTime.substring(0, initTime.indexOf(':')));
            int minute = Integer.parseInt(initTime.substring(initTime.indexOf(':') + 1, initTime.indexOf(' ')));

            if (initTime.substring(initTime.indexOf(' ')).trim().equals("PM")) {
                if (hour != 12)
                    hour += 12;
            } else if (initTime.substring(initTime.indexOf(' ')).trim().equals("AM")) {
                if (hour == 12)
                    hour = 0;
            }
            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        @Override
        public void onTimeSet(TimePicker view, int hour, int minute) {
            // Do something with the time chosen by the user

            String am_pm = "";
            String m = Integer.toString(minute);

            if (hour >= 12) {
                am_pm = "PM";
                if (hour != 12)
                    hour -= 12;
            } else if (hour < 12) {
                am_pm = "AM";
                if (hour == 0)
                    hour = 12;
            }

            if (m.length() == 1)
                m = "0" + m;

            Schedule.updateList(type, pos, (hour + ":" + m + " " + am_pm));
        }
    }
}