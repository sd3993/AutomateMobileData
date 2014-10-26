package com.sd3993.amd;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {

    static String type;
    static int changePos;
    Context context;
    FragmentManager fragmentManager;

    public ScheduleAdapter(Context context, FragmentManager fragmentManager) {
        super();
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    public static int getChangePosition() {
        return ScheduleAdapter.changePos;
    }

    public static void setChangePosition(int position) {
        ScheduleAdapter.changePos = position;
    }

    public static ArrayList<Timers> getList() {
        return Schedule.timersArrayList;
    }

    public static void updateList(String type, String store) {
        if (type.equalsIgnoreCase("start"))
            getList().get(getChangePosition()).startTime = store;
        else if (type.equalsIgnoreCase("end"))
            getList().get(getChangePosition()).endTime = store;
        else if (type.equalsIgnoreCase("state"))
            getList().get(getChangePosition()).isEnabled = Boolean.valueOf(store);
        Schedule.mScheduleAdapter.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return getList() == null ? 0 : getList().size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.list_row, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        changeStateColor(viewHolder.item, position);

        viewHolder.s.setText((getList().get(position)).startTime);
        viewHolder.e.setText((getList().get(position)).endTime);
        viewHolder.c.setText((getList().get(position)).isEnabled ? "Enabled" : "Disabled");

        viewHolder.s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setChangePosition(position);
                showTimePickerDialog("start");
            }
        });
        viewHolder.e.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setChangePosition(position);
                showTimePickerDialog("end");
            }
        });

        viewHolder.c.setOnCheckedChangeListener(null);
        viewHolder.c.setChecked((getList().get(position)).isEnabled);
        viewHolder.c.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isEnabled) {
                setChangePosition(position);
                updateList("state", String.valueOf(isEnabled));
            }
        });
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView s;
        TextView e;
        CheckBox c;
        LinearLayout item;

        public ViewHolder(View itemView) {
            super(itemView);
            s = (TextView) itemView.findViewById(R.id.startTime);
            e = (TextView) itemView.findViewById(R.id.endTime);
            c = (CheckBox) itemView.findViewById(R.id.alarm_enabled);
            item = (LinearLayout) itemView.findViewById(R.id.item_card);
        }

    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            String initTime = "";

            if (type.equalsIgnoreCase("start")) {
                initTime = (getList().get(getChangePosition())).startTime + ' ';
                if (initTime.equals("Start Time "))
                    initTime = "00:00 ";
            } else if (type.equalsIgnoreCase("end")) {
                initTime = (getList().get(getChangePosition())).endTime + ' ';
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