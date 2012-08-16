package com.dalthed.tucan.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dalthed.tucan.R;

public class AppointmentAdapter extends ArrayAdapter<String> {
	ArrayList<String> appointmentTime, appointmentNumber, appointmentRoom,
			appointmentInstructor;

	public AppointmentAdapter(Context context,ArrayList<String> appDate,
			ArrayList<String> appTime, ArrayList<String> appNumber,
			ArrayList<String> appRoom, ArrayList<String> appInstructor) {
		super(context, R.layout.singleevent_row_date,
				R.id.singleevent_row_date_date, appDate);
		this.appointmentTime = appTime;
		this.appointmentInstructor = appInstructor;
		this.appointmentNumber = appNumber;
		this.appointmentRoom = appRoom;
	}

	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = super.getView(position, convertView, parent);
		TextView AppTimeView = (TextView) row
				.findViewById(R.id.singleevent_row_date_time);
		TextView AppNumberView = (TextView) row
				.findViewById(R.id.singleevent_row_date_number);
		TextView AppRoomView = (TextView) row
				.findViewById(R.id.singleevent_row_date_room);
		TextView AppInstructorView = (TextView) row
				.findViewById(R.id.singleevent_row_date_instructor);

		AppTimeView.setText(this.appointmentTime.get(position));
		if (this.appointmentNumber != null)
			AppNumberView.setText(this.appointmentNumber.get(position));
		else
			AppNumberView.setText("");
		AppRoomView.setText(this.appointmentRoom.get(position));
		AppInstructorView.setText(this.appointmentInstructor.get(position));

		return row;
	}

}
