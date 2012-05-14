package com.dalthed.tucan.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dalthed.tucan.R;
import com.dalthed.tucan.ui.Events;

public class ModuleAdapter extends ArrayAdapter<String> {

	ArrayList<String> moduleCredits, moduleHeads;

	public ModuleAdapter(Context context, ArrayList<String> moduleName,
			ArrayList<String> resultGrade, ArrayList<String> resultDate) {
		super(context, R.layout.row_vv_events, R.id.row_vv_veranst, moduleName);
		this.moduleHeads = resultDate;
		this.moduleCredits = resultGrade;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = super.getView(position, convertView, parent);
		TextView TypeTextView = (TextView) row.findViewById(R.id.row_vv_type);
		TextView DozentTextView = (TextView) row.findViewById(R.id.row_vv_dozent);

		TypeTextView.setText(moduleCredits.get(position));
		DozentTextView.setText(moduleHeads.get(position));

		return row;
	}

}