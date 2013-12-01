package com.dalthed.tucan.adapters;

import java.util.ArrayList;
import java.util.HashMap;

import com.dalthed.tucan.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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
