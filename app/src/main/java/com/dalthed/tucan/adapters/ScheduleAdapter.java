/**
 *	This file is part of TuCan Mobile.
 *
 *	TuCan Mobile is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	TuCan Mobile is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with TuCan Mobile.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dalthed.tucan.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.dalthed.tucan.R;
import com.dalthed.tucan.datamodel.Appointment;
/**
 * {@link ListAdapter}, welcher zum darstellen von Veranstaltungen im Stundenplan sinnvoll ist.
 * Der Adapter nutzt schedule_event.xml und folgt folgendem Schema:
 *<table>
 *<tr><td colspan=4>***************************************************</td></tr>
 *<tr bgcolor=red><td>*</td><td colspan=2>Überschrift, falls erforderlich</td><td>*</td></tr>
 *<tr><td>*</td><td>Raum</td><td align=right>Zeit</td><td>*</td></tr>
 *<tr><td>*</td><td></td><td align=right>Veranstaltung</td><td>*</td></tr>
 *<tr><td colspan=4>***************************************************</td></tr>
 *</table> 
 * @author Daniel Thiem
 *
 */
public class ScheduleAdapter extends ArrayAdapter<String> {
	ArrayList<Appointment> appointments;
	/**
	 *  * {@link ListAdapter}, welcher zum darstellen von Veranstaltungen im Stundenplan sinnvoll ist.
	 * Der Adapter nutzt schedule_event.xml und folgt folgendem Schema:
	 *<table>
	 *<tr><td colspan=4>***************************************************</td></tr>
	 *<tr bgcolor=red><td>*</td><td colspan=2>appDate, falls appfirstofDay == true</td><td>*</td></tr>
	 *<tr><td>*</td><td>appRoom</td><td align=right>appTime</td><td>*</td></tr>
	 *<tr><td>*</td><td></td><td align=right>appName</td><td>*</td></tr>
	 *<tr><td colspan=4>***************************************************</td></tr>
	 *</table> 
	 * @param context {@link Activity} context
	 * @param appointments a list of all appointments
	 */
	public ScheduleAdapter(Context context,ArrayList<Appointment> appointments, ArrayList<String> rooms) {
		
		super(context,R.layout.schedule_event, R.id.schedule_event_room, rooms); // TODO: Check this
		this.appointments = appointments;
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
		
		AppTimeView.setText(appointments.get(position).getTimeInterval());
		AppNameView.setText(appointments.get(position).getName());

		//Titelleiste zeigen, falls erste Veranstaltung am tag
		if(appointments.get(position).isFirstDay()) {
			AppDayTitle.setVisibility(View.VISIBLE);			
		}else {
			AppDayTitle.setVisibility(View.GONE);
		}
		AppDayView.setText(appointments.get(position).getDateDescr());

		return row;
	}
}
