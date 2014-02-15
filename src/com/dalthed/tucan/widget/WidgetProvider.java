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

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.widget.RemoteViews;

import com.dalthed.tucan.R;

/**
 * Provider fuer das Stundenplan-Widget
 * 
 * @author Tim Kranz
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class WidgetProvider extends AppWidgetProvider {
	
	public static final String SCHEDULE_CLICK_ACTION = "com.dalthed.tucan.widget.SCHEDULE_CLICK_ACTION";

	@Override
	public void onUpdate(Context ctxt, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
			return;

		updateWidgetData(ctxt); //fixes widget update problem?
		System.out.println("Run..");
		for (int widgetID : appWidgetIds) {
			
			RemoteViews widget = new RemoteViews(ctxt.getPackageName(),
					R.layout.widget);
			
			//transparency begin, inspired by https://github.com/azapps/mirakel-android
			GradientDrawable drawable = (GradientDrawable) ctxt
                    .getResources().getDrawable(R.drawable.widget_background);
			drawable.setAlpha(getWidgetTransparency(ctxt));
			Bitmap bitmap = Bitmap.createBitmap(500, 400, Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            widget.setImageViewBitmap(R.id.widget_background, bitmap);
            // transparency end
			
			
			Intent svcIntent = new Intent(ctxt, WidgetService.class);

			svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
					widgetID);
			svcIntent.setData(Uri.parse(svcIntent
					.toUri(Intent.URI_INTENT_SCHEME)));

			widget.setRemoteAdapter(widgetID, R.id.widget_main, svcIntent);
			
			Intent clickIntent = new Intent(ctxt, WidgetProvider.class);
			clickIntent.setAction(SCHEDULE_CLICK_ACTION);
			
			PendingIntent clickPI = PendingIntent.getBroadcast(ctxt, 0,
					clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);

			widget.setPendingIntentTemplate(R.id.widget_main, clickPI);

			appWidgetManager.updateAppWidget(widgetID, widget);
		}

		super.onUpdate(ctxt, appWidgetManager, appWidgetIds);
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
//		// for later usage
//		System.out.println("in \"onReceive\"");
//		// action of 'click on an item in schedule list (widget)'
//		if(SCHEDULE_CLICK_ACTION.equals(intent.getAction())){
//			System.out.println("schedule click action");
//			int widgetID = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
//			System.out.println("WidgetID: "+widgetID);
//			int position = intent.getIntExtra(AppointmentViewsFactory.INTENT_CLICK_POSITION, -1);
//			System.out.println("Click position: "+position);
//		}
		
		super.onReceive(context, intent);
	}
	
	private int getWidgetTransparency(Context ctxt){
		final SharedPreferences altPrefs = ctxt.getSharedPreferences("WIDGET", Context.MODE_PRIVATE);
		return 255-altPrefs.getInt("transparency", 64);
	}

	/**
	 * Updates the entries of all schedule widgets (for instance after removing old entries)
	 * @param context
	 */
	public static void updateWidgetData(Context context) {
		AppWidgetManager appWidgetManager = AppWidgetManager
				.getInstance(context);

		ComponentName thisWidget = new ComponentName(context,
				WidgetProvider.class);
		
		int[] ids = appWidgetManager.getAppWidgetIds(thisWidget);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			appWidgetManager.notifyAppWidgetViewDataChanged(ids, R.id.widget_main);
	}
	
}
