package com.dalthed.tucan.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dalthed.tucan.R;


public class TwoLinesAdapter extends ArrayAdapter<String> {
	ArrayList<String> values;

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
