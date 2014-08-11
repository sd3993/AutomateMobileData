package com.sd3993.amd;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;

public class ScheduleAdapter extends BaseAdapter {

    static String type;
    static int position;
    boolean isSetByUser;
    Context context;
    FragmentManager fragmentManager;

    public ScheduleAdapter(Context context) {
        super();
        this.context = context;
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
            holder.item = convertView.findViewById(R.id.list_item_bg);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        if (getList().get(position).isEnabled)
            changeBgColor(holder.item, R.drawable.bg_alarm_enabled, position);
        else
            changeBgColor(holder.item, R.drawable.bg_alarm_disabled, position);

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
                isSetByUser = true;
                setPosition(position);
                updateList("state", String.valueOf(isEnabled));
            }
        });

        return convertView;
    }

    public void changeBgColor(final View view, final int bgColor, int position) {

        // get the center for the clipping circle
        int cx = (view.getLeft() + view.getRight()) / 2;
        int cy = (view.getTop() + view.getBottom()) / 2;

        // get the initial radius for the clipping circle
        int initialRadius = view.getWidth();
        // create the animation (the final radius is zero)
        ValueAnimator hideAnim =
                ViewAnimationUtils.createCircularReveal(view, cx, cy, initialRadius, 0);
        hideAnim.setDuration(300);

        // get the final radius for the clipping circle
        int finalRadius = view.getWidth();
        // create and start the animator for this view
        // (the start radius is zero)
        final ValueAnimator showAnim =
                ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);
        showAnim.setDuration(500);

        hideAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setBackgroundResource(bgColor);
                showAnim.start();
            }
        });

        if (isSetByUser && (getPosition() == position))
            hideAnim.start();
        else
            view.setBackgroundResource(bgColor);
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
        View item;
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker

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

            updateList(type, (hour + ":" + m + " " + am_pm));
        }
    }
}