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
 * 
 * {@link ListAdapter}, welcher eine Tabelle mit 2 Zeilen dargibt
 * Der Adapter nutzt singleevent_row.xml und folgt folgendem Schema:
 *<table>
 *<tr><td colspan=4>***************************************************</td></tr>
 *<tr><td>*</td><td>Links Oben</td><td align=right></td><td>*</td></tr>
 *<tr><td>*</td><td></td><td align=right>RECHTS UNTEN</td><td>*</td></tr>
 *<tr><td colspan=4>***************************************************</td></tr>
 *</table> 
 * 
 * @author Tyde
 *
 */

public class TwoLinesAdapter extends ArrayAdapter<String> {
	ArrayList<String> values;
	/**
	 * {@link ListAdapter}, welcher eine Tabelle mit 2 Zeilen dargibt
	 * Der Adapter nutzt singleevent_row.xml und folgt folgendem Schema:
	 *<table>
	 *<tr><td colspan=4>***************************************************</td></tr>
	 *<tr><td>*</td><td>Links Oben</td><td align=right></td><td>*</td></tr>
	 *<tr><td>*</td><td></td><td align=right>RECHTS UNTEN</td><td>*</td></tr>
	 *<tr><td colspan=4>***************************************************</td></tr>
	 *</table> 
	 * @param context {@link Activity} context
	 * @param leftTopFat siehe Tabelle
	 * @param rightBottomThin siehe Tabelle
	 */
	public TwoLinesAdapter(Context context,ArrayList<String> leftTopFat,
			ArrayList<String> rightBottomThin) {
		super(context, R.layout.singleevent_row,
				R.id.singleevent_row_property, leftTopFat);
		this.values = rightBottomThin;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = super.getView(position, convertView, parent);
		TextView ValueTextView = (TextView) row
				.findViewById(R.id.singleevent_row_value);

		ValueTextView.setText(" " + this.values.get(position));

		return row;
	}

}
