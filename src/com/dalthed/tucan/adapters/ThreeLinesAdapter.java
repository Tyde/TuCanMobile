package com.dalthed.tucan.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dalthed.tucan.R;
import com.dalthed.tucan.ui.Events;
/**
 * Ehemalig ModuleAdapter
 * @author Daniel Thiem
 * @since 2012-05-14
 */
public class ThreeLinesAdapter extends ArrayAdapter<String> {

	ArrayList<String> middleRightThin, bottomLeftThin;

	public ThreeLinesAdapter(Context context, ArrayList<String> topLeftFat,
			ArrayList<String> middleRightThin, ArrayList<String> bottomLeftThin) {
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