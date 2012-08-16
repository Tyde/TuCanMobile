package com.dalthed.tucan.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dalthed.tucan.R;

public class OneLineTwoColsAdapter extends ArrayAdapter<String> {

	String[] startClock;

	public OneLineTwoColsAdapter(Context context, String[] Events, String[] Times) {
		super(context, R.layout.row, R.id.label, Events);
		this.startClock = Times;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = super.getView(position, convertView, parent);
		TextView clockText = (TextView) row.findViewById(R.id.row_time);
		clockText.setText(startClock[position]);
		return row;
	}

}
