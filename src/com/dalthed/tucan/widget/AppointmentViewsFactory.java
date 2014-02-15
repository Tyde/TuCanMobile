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

package com.dalthed.tucan.widget;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import com.dalthed.tucan.R;
import com.dalthed.tucan.datamodel.Appointment;
import com.dalthed.tucan.util.ScheduleSaver;

/**
 * Aktualisiert die Homescreen-Widget Eintraege aus der Stundenplanliste
 * @author Tim Kranz
 *
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class AppointmentViewsFactory implements RemoteViewsFactory {
	private ArrayList<Appointment> items = ScheduleSaver.loadSchedule();
	private Context ctxt = null;
	private int appWidgetID;
	
	public static final String INTENT_CLICK_POSITION = "INTENT_CLICK_POSITION";
	public static final String INTENT_TYPE = "INTENT_TYPE";
	public static final String INTENT_TYPE_OPEN_APP = "OPEN TUCAB APP";

	public AppointmentViewsFactory() {
		removeOldEntries();
	}

	private void removeOldEntries() {
		Calendar now = GregorianCalendar.getInstance(); //now contains date and time
		int i = 0;
		boolean changed = false;
		while (i < items.size()) {
			if (items.get(i).getEndTime().before(now)) { //remove only if appointment is ended
				items.remove(0);
				changed = true;
			} else {
				break;
			}
		}
		if(changed)
			ScheduleSaver.saveSchedule(items);
	}

	public AppointmentViewsFactory(Context ctxt, Intent intent) {
		this.ctxt = ctxt;
		appWidgetID = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
	}

	@Override
	public void onCreate() {
	}

	@Override
	public void onDestroy() {
	}

	@Override
	public int getCount() {
		removeOldEntries();
		if(items == null || items.isEmpty())
			return 1;
		//limit to (value from settings) entries
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctxt.getApplicationContext());
		int maxEntries = Integer.parseInt(prefs.getString("widget_max_entries", "25")); //a bit dirty...
		return Math.min(items.size(), maxEntries);
	}

	@Override
	public RemoteViews getViewAt(int position) {
		RemoteViews row = new RemoteViews(ctxt.getPackageName(),
				R.layout.widget_schedule_row);

		Intent i = new Intent();
		i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetID);
		
		if(items == null || items.isEmpty()){
			// empty list -> user has to login and open schedule first
			row.setTextViewText(R.id.widget_event_room, ctxt.getResources().getString(R.string.widget_no_data_msg_title));
			row.setTextViewText(R.id.widget_event_name, ctxt.getResources().getString(R.string.widget_no_data_msg));
			
			i.putExtra(INTENT_TYPE, INTENT_TYPE_OPEN_APP);
		}else{
			
			Appointment item = items.get(position);
			if (position == 0 || !item.getDateDescr().equals(items.get(position - 1).getDateDescr())) {
				row.setTextViewText(R.id.widget_daytitlebartext,
						item.getDateDescr());
				row.setViewVisibility(R.id.widget_daytitle, View.VISIBLE);
			} else {
				row.setViewVisibility(R.id.widget_daytitle, View.GONE);
			}
	
			row.setTextViewText(R.id.widget_event_name, item.getName());
			row.setTextViewText(R.id.widget_event_time, item.getTimeInterval());
			row.setTextViewText(R.id.widget_event_room, item.getRoom());
			// set onclick-Acton
			i.putExtra(INTENT_CLICK_POSITION, position);
		}
		row.setOnClickFillInIntent(R.id.widget_row, i);

		return row;
	}

	@Override
	public RemoteViews getLoadingView() {
		return (null);
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public void onDataSetChanged() {
		//force update data
		items = ScheduleSaver.loadSchedule();
		removeOldEntries();
	}
}