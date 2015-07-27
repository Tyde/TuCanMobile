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
import android.text.Html;
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

		ValueTextView.setText(" " + Html.fromHtml(this.values.get(position)));

		return row;
	}

}
