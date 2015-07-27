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

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.dalthed.tucan.R;

/**
 * {@link ListAdapter}, welcher zum darstellen von zwei Informationen in einer
 * Zeilesinnvoll ist. Der Adapter nutzt row.xml und folgt folgendem Schema:
 * <table>
 * <tr>
 * <td colspan=4>***************************************************</td>
 * </tr>
 * <tr>
 * <td>*</td>
 * <td>Linke Spalte</td>
 * <td align=right>Rechte Spalte</td>
 * <td>*</td>
 * </tr>
 * 
 * <tr>
 * <td colspan=4>***************************************************</td>
 * </tr>
 * </table>
 * 
 * @author Daniel Thiem
 * 
 */
public class OneLineTwoColsAdapter extends ArrayAdapter<String> {

	String[] startClock;

	/**
	 * {@link ListAdapter}, welcher zum darstellen von zwei Informationen in
	 * einer Zeilesinnvoll ist. Der Adapter nutzt row.xml und folgt folgendem
	 * Schema:
	 * <table>
	 * <tr>
	 * <td colspan=4>***************************************************</td>
	 * </tr>
	 * <tr>*<td>*</td><td>leftCol</td><td align=right>rightCol</td><td>*</td>
	 * </tr>
	 * 
	 * <tr>
	 * <td colspan=4>***************************************************</td>
	 * </tr>
	 * </table>
	 * 
	 * @param context {@link Activity} context
	 * @param leftCol siehe Tabelle
	 * @param rightCol siehe Tabelle
	 */
	public OneLineTwoColsAdapter(Context context, String[] leftCol, String[] rightCol) {
		super(context, R.layout.row, R.id.label, leftCol);
		this.startClock = rightCol;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = super.getView(position, convertView, parent);
		TextView clockText = (TextView) row.findViewById(R.id.row_time);
		clockText.setText(startClock[position]);
		return row;
	}

}
