package com.dalthed.tucan.adapters;

import java.util.ArrayList;

import com.dalthed.tucan.R;
import com.dalthed.tucan.ui.Schedule;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ScheduleAdapter extends ArrayAdapter<String> {
	ArrayList<String> appointmentTime,appointmentName,appointmentDay;
	ArrayList<Boolean> appontmentfirstofDay;
	public ScheduleAdapter(Context context,ArrayList<String> appDate,ArrayList<String> appTime
			,ArrayList<Boolean> appfirstofDay,ArrayList<String> appRoom,ArrayList<String> appName) {
		super(context,R.layout.schedule_event, R.id.schedule_event_room,
				appRoom);
		this.appointmentDay=appDate;
		this.appointmentTime=appTime;
		this.appointmentName=appName;
		this.appontmentfirstofDay=appfirstofDay;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = super.getView(position, convertView, parent);
		
		
		TextView AppTimeView = (TextView) row
				.findViewById(R.id.schedule_event_time);
		TextView AppNameView = (TextView) row
				.findViewById(R.id.schedule_event_name);
		TextView AppDayView = (TextView) row
				.findViewById(R.id.schedule_daytitlebartext);
		LinearLayout AppDayTitle = (LinearLayout) row.findViewById(R.id.schedule_daytitle);
		
		AppTimeView.setText(this.appointmentTime.get(position));
		AppNameView.setText(appointmentName.get(position));
		if(this.appontmentfirstofDay.get(position)==true) {
			AppDayTitle.setVisibility(View.VISIBLE);
			AppDayView.setText(this.appointmentDay.get(position));
			
		}
		else {
			AppDayTitle.setVisibility(View.GONE);
			AppDayView.setText(this.appointmentDay.get(position));
		}

		return row;
	}
}
