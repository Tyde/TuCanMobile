package com.dalthed.tucan.adapters;

import com.dalthed.tucan.R;
import com.dalthed.tucan.TucanMobile;
import com.dalthed.tucan.ui.SimpleWebListActivity;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class FastSwitchAdapter extends ArrayAdapter<String> {
	String mSubtitle = null;
	private Context context;
	public FastSwitchAdapter(Context context,String[] ressource) {
		super(context, R.layout.fast_switch_dropdown,R.id.dropdown_maintitle,ressource);
		this.context = context;
		setDropDownViewResource(R.layout.fast_switch_dropdown_item);
	}
	/**
	 * sets the Subtitle
	 * @param subtitle
	 */
	public void setSubtitle(String subtitle){
		mSubtitle=subtitle;
		notifyDataSetChanged();
	}
	
	
	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View reuseView=super.getView(position, convertView, parent);
		Log.i(TucanMobile.LOG_TAG, "getView called with position"+position);
		TextView subtitleView = (TextView) reuseView.findViewById(R.id.dropdown_subtitle);
		
		if(subtitleView != null){
			if(mSubtitle==null){
				subtitleView.setVisibility(View.GONE);
				TextView maintitleView = (TextView) reuseView.findViewById(R.id.dropdown_maintitle);
				if(maintitleView != null) {
					//maintitleView.setTextSize(context.getResources().getDimension(R.dimen.dropdown_maintitle_alone));
				}
			}
			else {
				subtitleView.setText(mSubtitle);
			}
			
		}
		return reuseView;
	}

	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getDropDownView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return super.getDropDownView(position, convertView, parent);
		
	}
	
	

}
