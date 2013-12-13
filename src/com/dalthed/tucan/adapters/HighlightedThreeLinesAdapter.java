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

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.dalthed.tucan.R;
/**
 * {@link ListAdapter}, welcher 3 Zeilen pro Element dargibt und einzelne Rows hervorheben kann.
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
public class HighlightedThreeLinesAdapter extends ArrayAdapter<String> {

	List<String> middleRightThin, bottomLeftThin;
	private List<Boolean> isHighlighted;
	private Context context;
	/**
	 *  * {@link ListAdapter}, welcher 3 Zeilen pro Element dargibt und einzelne Rows hervorheben kann.
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
	public HighlightedThreeLinesAdapter(Context context, List<String> topLeftFat,
			List<String> middleRightThin, List<String> bottomLeftThin,List<Boolean> isHighlighted) {
		super(context, R.layout.row_vv_events, R.id.row_vv_veranst, topLeftFat);
		this.context = context;
		this.bottomLeftThin = bottomLeftThin;
		this.middleRightThin = middleRightThin;
		this.isHighlighted = isHighlighted;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = super.getView(position, convertView, parent);
		TextView TypeTextView = (TextView) row.findViewById(R.id.row_vv_type);
		TextView DozentTextView = (TextView) row.findViewById(R.id.row_vv_dozent);
		if(isHighlighted.get(position)){
			row.setBackgroundColor(context.getResources().getColor(R.color.level00));
		}
		else {
			row.setBackgroundColor(Color.WHITE);
		}
		TypeTextView.setText(middleRightThin.get(position));
		DozentTextView.setText(bottomLeftThin.get(position));

		return row;
	}
	
	

}