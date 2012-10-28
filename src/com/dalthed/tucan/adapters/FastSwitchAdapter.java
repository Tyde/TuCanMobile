package com.dalthed.tucan.adapters;

import com.dalthed.tucan.R;
import com.dalthed.tucan.TucanMobile;
import com.dalthed.tucan.ui.SimpleWebListActivity;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Adapter f�r den Dropdown-Spinner in der ActionBar
 * @author Daniel Thiem
 * 
 */
public class FastSwitchAdapter extends ArrayAdapter<String> {
	String mSubtitle = null;
	private Context context;
	/**
	 * Erzeugt einen Adapter f�r den Dropdown-Spinner in der ActionBar
	 * @param context {@link Activity} context
	 * @param ressource Array mit allen Elementen der Dropdown-Liste
	 */
	public FastSwitchAdapter(Context context, String[] ressource) {
		super(context, R.layout.fast_switch_dropdown, R.id.dropdown_maintitle, ressource);
		this.context = context;
		//Setzt genutztes Layout
		setDropDownViewResource(R.layout.fast_switch_dropdown_item);
	}

	/**
	 * Setzt einen Untertitel unter dem derzeit angezeigtem Element
	 * 
	 * @param subtitle Der anzuzeigende Untertitel
	 */
	public void setSubtitle(String subtitle) {
		mSubtitle = subtitle;
		notifyDataSetChanged();
	}

	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View reuseView = super.getView(position, convertView, parent);
		if (TucanMobile.DEBUG) {
			Log.i(TucanMobile.LOG_TAG, "getView called with position" + position);
		}
		TextView subtitleView = (TextView) reuseView.findViewById(R.id.dropdown_subtitle);

		if (subtitleView != null) {
			//Untertitel setzen, falls vorhanden
			if (mSubtitle == null) {
				subtitleView.setVisibility(View.GONE);
				TextView maintitleView = (TextView) reuseView.findViewById(R.id.dropdown_maintitle);
				if (maintitleView != null) {
					// maintitleView.setTextSize(context.getResources().getDimension(R.dimen.dropdown_maintitle_alone));
				}
			} else {
				subtitleView.setText(mSubtitle);
			}

		}
		return reuseView;
	}

	
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return super.getDropDownView(position, convertView, parent);

	}

}
