package com.dalthed.tucan.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dalthed.tucan.R;


public class ThreeLinesTableAdapter extends ArrayAdapter<String> {
	ArrayList<String> resultName, resultGrade, resultCredits,
			resultCountedCredits;

	public ThreeLinesTableAdapter(Context context,ArrayList<String> resName,
			ArrayList<String> resGrade, ArrayList<String> resCredits,
			ArrayList<String> resCtCredits) {
		super(context, R.layout.singleevent_row_date,
				R.id.singleevent_row_date_date, resCredits);
		this.resultName = resName;
		this.resultGrade = resGrade;
		this.resultCredits = resCredits;
		this.resultCountedCredits = resCtCredits;
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

		AppTimeView.setText(this.resultCountedCredits.get(position));
		AppNumberView.setText("");
		AppRoomView.setText(this.resultName.get(position));
		AppInstructorView.setText(this.resultGrade.get(position));

		return row;
	}

}
