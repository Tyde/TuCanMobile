package com.dalthed.tucan.adapters;

import java.util.ArrayList;


import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dalthed.tucan.R;
import com.dalthed.tucan.ui.RegisterExams;
import com.dalthed.tucan.ui.SimpleWebListActivity;

public class RegisterExamAdapter extends ArrayAdapter<String> {
	ArrayList<Boolean> eventisModule;
	ArrayList<String> eventName;
	ArrayList<Integer> examSelection;
	private Context context;

	public RegisterExamAdapter(Context context,ArrayList<Boolean> eventisModule,
			ArrayList<String> eventName, ArrayList<String> examDate,
			ArrayList<Integer> examSelection) {
		
		
		super(context, R.layout.registerexam_row,
				R.id.registerexam_row_date, examDate);
		this.context = context;
		this.eventisModule = eventisModule;
		this.eventName = eventName;
		this.examSelection = examSelection;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = super.getView(position, convertView, parent);
		LinearLayout isModuleLayout = (LinearLayout) row
				.findViewById(R.id.registerexam_row_ismodule);
		LinearLayout isEventLayout = (LinearLayout) row
				.findViewById(R.id.registerexam_row_isevent);

		Log.i(SimpleWebListActivity.LOG_TAG, "Calling " + position);

		if (eventisModule.get(position)) {
			isModuleLayout.setVisibility(View.VISIBLE);
			isEventLayout.setVisibility(View.GONE);
			TextView ModuleNameView = (TextView) row
					.findViewById(R.id.registerexam_row_module_name);

			ModuleNameView.setText(eventName.get(position));
		} else {
			isModuleLayout.setVisibility(View.GONE);
			isEventLayout.setVisibility(View.VISIBLE);
			TextView eventNameView = (TextView) row
					.findViewById(R.id.registerexam_row_examName);
			TextView eventSelectionView = (TextView) row
					.findViewById(R.id.registerexam_row_selection);
			eventNameView.setText(eventName.get(position));
			String selectionstring = "";
			switch (examSelection.get(position)) {
			case 0:
				eventSelectionView.setTextColor(Color.BLACK);
				selectionstring = "Abgelaufen";
				break;
			case 1:
				eventSelectionView.setTextColor(Color.BLACK);
				selectionstring = "Ausgewählt";
				break;
			case 2:
				eventSelectionView.setTextColor(context.getResources().getColor(R.color.tucan_green));

				selectionstring = "Anmelden";
				break;
			case 3:
				eventSelectionView.setTextColor(context.getResources().getColor(R.color.register_deregister_red));
				selectionstring = "Abmelden";
				break;
			default:
				break;

			}

			eventSelectionView.setText(selectionstring);
		}

		return row;
	}

}
