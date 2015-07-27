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
import java.util.HashMap;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dalthed.tucan.R;

public class UniApplicationAdapter extends ArrayAdapter<String> {

	public final HashMap<Integer, ArrayList<String>> contentMap;

	public static HashMap<Integer, ArrayList<String>> getEmptyContentMap() {
		return new HashMap<Integer, ArrayList<String>>() {
			{
				put(R.id.application_type, new ArrayList<String>());
				put(R.id.application_field, new ArrayList<String>());
				put(R.id.application_semester, new ArrayList<String>());
				put(R.id.application_status, new ArrayList<String>());
				put(R.id.application_begin, new ArrayList<String>());
				put(R.id.application_sent, new ArrayList<String>());
			}
		};
	}

	public UniApplicationAdapter(Context context,
			HashMap<Integer, ArrayList<String>> contentMap) {

		super(context, R.layout.application_row, R.id.application_field,
				contentMap.get(R.id.application_field));
		this.contentMap = contentMap;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View tempView = super.getView(position, convertView, parent);
		updateTextViewById(position, R.id.application_begin, tempView);
		updateTextViewById(position, R.id.application_semester, tempView);
		updateTextViewById(position, R.id.application_type, tempView);
		updateTextViewById(position, R.id.application_sent, tempView);
		updateTextViewById(position, R.id.application_status, tempView);
		return tempView;
	}

	private void updateTextViewById(int position, int id, View parentView) {
		TextView tViewToUpdate = (TextView) parentView.findViewById(id);
		tViewToUpdate.setText(contentMap.get(id).get(position));
	}

}
