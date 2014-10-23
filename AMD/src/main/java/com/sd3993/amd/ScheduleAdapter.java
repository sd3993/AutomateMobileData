package com.sd3993.amd;

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
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;

public class ScheduleAdapter extends BaseAdapter {

    static String type;
    static int position;
    Context context;
    FragmentManager fragmentManager;

    public ScheduleAdapter(Context context, FragmentManager fragmentManager) {
        super();
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    public static int getPosition() {
        return ScheduleAdapter.position;
    }

    public static void setPosition(int position) {
        ScheduleAdapter.position = position;
    }

    public static ArrayList<Timers> getList() {
        return Schedule.timersArrayList;
    }

    public static void updateList(String type, String store) {
        if (type.equalsIgnoreCase("start"))
            getList().get(getPosition()).startTime = store;
        else if (type.equalsIgnoreCase("end"))
            getList().get(getPosition()).endTime = store;
        else if (type.equalsIgnoreCase("state"))
            getList().get(getPosition()).isEnabled = Boolean.valueOf(store);
        Schedule.scheduleAdapter.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return (getList().size());
    }

    @Override
    public Object getItem(int position) {
        return getList().get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final Holder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_row, parent, false);
            holder = new Holder();

            holder.s = (TextView) convertView.findViewById(R.id.startTime);
            holder.e = (TextView) convertView.findViewById(R.id.endTime);
            holder.c = (CheckBox) convertView.findViewById(R.id.alarm_enabled);
            holder.item = (LinearLayout) convertView.findViewById(R.id.item_card);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        changeStateColor(holder.item, position);

        holder.s.setText((getList().get(position)).startTime);
        holder.e.setText((getList().get(position)).endTime);
        holder.c.setText((getList().get(position)).isEnabled ? "Enabled" : "Disabled");

        holder.s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPosition(position);
                showTimePickerDialog("start");
            }
        });
        holder.e.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPosition(position);
                showTimePickerDialog("end");
            }
        });

        holder.c.setOnCheckedChangeListener(null);
        holder.c.setChecked((getList().get(position)).isEnabled);
        holder.c.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isEnabled) {
                setPosition(position);
                updateList("state", String.valueOf(isEnabled));
            }
        });

        return convertView;
    }

    public void changeStateColor(final LinearLayout view, int position) {
        if (getList().get(position).isEnabled)
            view.setBackgroundResource(R.drawable.bg_enabled);
        else
            view.setBackgroundResource(R.drawable.bg_disabled);
    }

    public void showTimePickerDialog(String type) {
        ScheduleAdapter.type = type;
        DialogFragment timePickerFragment = new TimePickerFragment();
        timePickerFragment.show(fragmentManager, "timePicker");
    }

    static class Holder {
        TextView s;
        TextView e;
        CheckBox c;
        LinearLayout item;
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            String initTime = "";

            if (type.equalsIgnoreCase("start")) {
                initTime = (getList().get(getPosition())).startTime + ' ';
                if (initTime.equals("Start Time "))
                    initTime = "00:00 ";
            } else if (type.equalsIgnoreCase("end")) {
                initTime = (getList().get(getPosition())).endTime + ' ';
                if (initTime.equals("End Time "))
                    initTime = "00:00 ";
            }

            int hour = Integer.parseInt(initTime.substring(0, initTime.indexOf(':')));
            int minute = Integer.parseInt(initTime.substring(initTime.indexOf(':') + 1,
                    initTime.indexOf(' ')));

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

            updateList(type, (hour + ":" + m + " " + am_pm));
        }
    }
}