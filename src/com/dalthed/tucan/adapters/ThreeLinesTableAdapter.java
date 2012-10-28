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
 * {@link ListAdapter}, welcher eine Tabelle mit 3 Zeilen dargibt (Ähnlich {@link AppointmentAdapter}, nur ohne Nummer)
 * Der Adapter nutzt singleevent_row_date.xml und folgt folgendem Schema:
 *<table>
 *<tr><td colspan=4>***************************************************</td></tr>
 *<tr><td>*</td><td>Links Oben</td><td align=right>Rechts Oben</td><td>*</td></tr>
 *<tr><td>*</td><td>Links Mitte</td><td align=right></td><td>*</td></tr>
 *<tr><td>*</td><td>Links Unten</td><td align=right></td><td>*</td></tr>
 *<tr><td colspan=4>***************************************************</td></tr>
 *</table> 
 * @author Tyde
 *
 */
public class ThreeLinesTableAdapter extends ArrayAdapter<String> {
	protected ArrayList<String> middleRight, bottomLeft, topLeft,
			topRight;
	/**
	 * {@link ListAdapter}, welcher eine Tabelle mit 3 Zeilen dargibt
	 * Der Adapter nutzt singleevent_row_date.xml und folgt folgendem Schema:
	 *<table>
	 *<tr><td colspan=4>***************************************************</td></tr>
	 *<tr><td>*</td><td>topLeft</td><td align=right>topRight</td><td>*</td></tr>
	 *<tr><td>*</td><td>middleRight</td><td align=right></td><td>*</td></tr>
	 *<tr><td>*</td><td>bottomLeft</td><td align=right></td><td>*</td></tr>
	 *<tr><td colspan=4>***************************************************</td></tr>
	 *</table> 
	 *
	 * @param context {@link Activity} context
	 * @param mittleRight siehe Tabelle
	 * @param bottomLeft siehe Tabelle
	 * @param topLeft siehe Tabelle
	 * @param topRight siehe Tabelle
	 *
	 */
	public ThreeLinesTableAdapter(Context context,ArrayList<String> mittleRight,
			ArrayList<String> bottomLeft, ArrayList<String> topLeft,
			ArrayList<String> topRight) {
		super(context, R.layout.singleevent_row_date,
				R.id.singleevent_row_date_date, topLeft);
		this.middleRight = mittleRight;
		this.bottomLeft = bottomLeft;
		this.topLeft = topLeft;
		this.topRight = topRight;
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

		AppTimeView.setText(this.topRight.get(position));
		AppNumberView.setText("");
		AppRoomView.setText(this.middleRight.get(position));
		AppInstructorView.setText(this.bottomLeft.get(position));

		return row;
	}

}
