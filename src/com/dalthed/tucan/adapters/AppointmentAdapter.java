package com.dalthed.tucan.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.dalthed.tucan.R;
/**
 * Use ThreeLinesTableAdapter
 * {@link ListAdapter}, welcher zum darstellen von Veranstaltungen sinnvoll ist.
 * Der Adapter nutzt singleevent_row_date.xml und folgt folgendem Schema:
 *<table>
 *<tr><td colspan=4>***************************************************</td></tr>
 *<tr><td>*</td><td>Datum</td><td align=right>Zeit</td><td>*</td></tr>
 *<tr><td>*</td><td>Nummer</td><td align=right>RAUM</td><td>*</td></tr>
 *<tr><td>*</td><td>Lehrender</td><td align=right></td><td>*</td></tr>
 *<tr><td colspan=4>***************************************************</td></tr>
 *</table> 
 * @author Daniel Thiem
 *
 */

public class AppointmentAdapter extends ArrayAdapter<String> {
	ArrayList<String> appointmentTime, appointmentNumber, appointmentRoom,
			appointmentInstructor;
	/**
	 * {@link ArrayAdapter}, welcher folgendem Schema folgt
	 * <table>
	 *<tr><td colspan=4>***************************************************</td></tr>
	 *<tr><td>*</td><td>appDate</td><td align=right>appTime</td><td>*</td></tr>
	 *<tr><td>*</td><td>appNumber</td><td align=right>appRoom</td><td>*</td></tr>
	 *<tr><td>*</td><td>appInstructor</td><td align=right></td><td>*</td></tr>
	 *<tr><td colspan=4>***************************************************</td></tr>
	 *</table> 
	 * @param context {@link Activity} context
	 * @param appDate siehe Tabelle
	 * @param appTime siehe Tabelle
	 * @param appNumber siehe Tabelle
	 * @param appRoom siehe Tabelle
	 * @param appInstructor siehe Tabelle
	 */
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
