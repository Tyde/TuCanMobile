package com.dalthed.tucan.adapters;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.dalthed.tucan.R;
/**
 * {@link ListAdapter}, welcher 3 Zeilen pro Element dargibt.
 * Der Adapter nutzt row_vv_events.xml und folgt folgendem Schema:
 *<table>
 *<tr><td colspan=4>***************************************************</td></tr>
 *<tr><td>*</td><td>ERSTE ZEILE</td><td align=right></td><td>*</td></tr>
 *<tr><td>*</td><td></td><td align=right>zweite Zeile</td><td>*</td></tr>
 *<tr><td>*</td><td>dritte Zeile</td><td align=right></td><td>*</td></tr>
 *<tr><td colspan=4>***************************************************</td></tr>
 *</table> 
 * @author Daniel Thiem
 * @since 2012-05-14
 */
public class ThreeLinesAdapter extends ArrayAdapter<String> {

	List<String> middleRightThin, bottomLeftThin;
	/**
	 *  * {@link ListAdapter}, welcher 3 Zeilen pro Element dargibt.
	 * Der Adapter nutzt row_vv_events.xml und folgt folgendem Schema:
	 *<table>
	 *<tr><td colspan=4>***************************************************</td></tr>
	 *<tr><td>*</td><td>topLeftFat</td><td align=right></td><td>*</td></tr>
	 *<tr><td>*</td><td></td><td align=right>mittleRightThin</td><td>*</td></tr>
	 *<tr><td>*</td><td>bottemLeftThin</td><td align=right></td><td>*</td></tr>
	 *<tr><td colspan=4>***************************************************</td></tr>
	 *</table> 
	 * @param context {@link Activity} context
	 * @param topLeftFat siehe Tabelle
	 * @param middleRightThin siehe Tabelle
	 * @param bottomLeftThin siehe Tabelle
	 */
	public ThreeLinesAdapter(Context context, List<String> topLeftFat,
			List<String> middleRightThin, List<String> bottomLeftThin) {
		super(context, R.layout.row_vv_events, R.id.row_vv_veranst, topLeftFat);
		this.bottomLeftThin = bottomLeftThin;
		this.middleRightThin = middleRightThin;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = super.getView(position, convertView, parent);
		TextView TypeTextView = (TextView) row.findViewById(R.id.row_vv_type);
		TextView DozentTextView = (TextView) row.findViewById(R.id.row_vv_dozent);

		TypeTextView.setText(middleRightThin.get(position));
		DozentTextView.setText(bottomLeftThin.get(position));

		return row;
	}
	
	

}