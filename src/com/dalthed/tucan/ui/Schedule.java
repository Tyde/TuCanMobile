package com.dalthed.tucan.ui;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.PropertyResourceBundle;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.dalthed.tucan.R;
import com.dalthed.tucan.TucanMobile;
import com.dalthed.tucan.Connection.AnswerObject;
import com.dalthed.tucan.Connection.BrowseMethods;
import com.dalthed.tucan.Connection.CookieManager;
import com.dalthed.tucan.Connection.RequestObject;
import com.dalthed.tucan.Connection.SimpleSecureBrowser;
import com.dalthed.tucan.ui.SingleEvent.OnItemSelectedListener;

public class Schedule extends SimpleWebListActivity {
	private CookieManager localCookieManager;
	private static final String LOG_TAG = "TuCanMobile";
	private String URLStringtoCall;
	int mode = 0;
	ArrayList<String> eventLink,eventDay,eventTime,eventRoom,eventName;
	ArrayList<Boolean> firstEventofDay;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.schedule);
		

		
		String CookieHTTPString = getIntent().getExtras().getString("Cookie");
		URLStringtoCall = getIntent().getExtras().getString("URL");
		URL URLtoCall;
		
		try {
			URLtoCall = new URL(URLStringtoCall);
			localCookieManager = new CookieManager();
			localCookieManager.generateManagerfromHTTPString(
					URLtoCall.getHost(), CookieHTTPString);
			SimpleSecureBrowser callOverviewBrowser = new SimpleSecureBrowser(
					this);
			RequestObject thisRequest = new RequestObject(URLStringtoCall,
					localCookieManager, RequestObject.METHOD_GET, "");

			callOverviewBrowser.execute(thisRequest);
		} catch (MalformedURLException e) {
			Log.e(LOG_TAG, e.getMessage());
		}
		
		
	}

	
	@Override
	public void onPostExecute(AnswerObject result) {
		Document doc = Jsoup.parse(result.getHTML());
		Iterator<Element> schedDays=doc.select("div.tbMonthDay").iterator();
		int Month = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH); 
		int Day = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH); 
		if(mode==0) {
			eventDay = new ArrayList<String>();
			eventTime = new ArrayList<String>();
			eventRoom = new ArrayList<String>();
			eventName = new ArrayList<String>();
			eventLink = new ArrayList<String>();
			firstEventofDay = new ArrayList<Boolean>();
			String nextLink = TucanMobile.TUCAN_PROT + TucanMobile.TUCAN_HOST + doc.select("a[name=skipForward_btn]").attr("href");
			SimpleSecureBrowser callOverviewBrowser = new SimpleSecureBrowser(
					this);
			RequestObject thisRequest = new RequestObject(nextLink,
					localCookieManager, RequestObject.METHOD_GET, "");
			callOverviewBrowser.execute(thisRequest);
			
		}	
		if(mode==1)
			Month++;
		while(schedDays.hasNext()){
			Element next = schedDays.next();
			String monthday=next.attr("title");
			Iterator<Element> dayEvents = next.select("div.appMonth").iterator();
			if(dayEvents!= null){
				int i=0;
				while(dayEvents.hasNext()){
					
					
					
					Element nextEvent = dayEvents.next();
					if(Integer.parseInt(monthday.trim()) > Day || mode==1){
						if(i==0){
							firstEventofDay.add(true);
						}
						else {
							firstEventofDay.add(false);
						}
						i++;
						if(Integer.parseInt(monthday.trim()) == Day && mode==0) {
							eventDay.add("Heute");
						}
						else if(Integer.parseInt(monthday.trim()) == (Day+1) && mode==0) {
							eventDay.add("Morgen");
						}
						else {
							eventDay.add(monthday + "." + (Month+1));
						}
						
						
						String[] LinktitleArgument = nextEvent.select("a").attr("title").split("/");
						eventTime.add(LinktitleArgument[0].trim());
						eventRoom.add(LinktitleArgument[1].trim() + "/" + LinktitleArgument[2].trim());
						eventName.add(LinktitleArgument[3].trim());
						eventLink.add(nextEvent.select("a").attr("href"));
					
					}
				}
			}
		}
		
		
		if(mode==1)
		{
			AppointmentAdapter ExternAdapter = new AppointmentAdapter(eventDay,eventTime,firstEventofDay,eventRoom,eventName);
			setListAdapter(ExternAdapter);
		}
		else {
			mode=1;
		}
		
	

	}
	
	class AppointmentAdapter extends ArrayAdapter<String> {
		ArrayList<String> appointmentTime,appointmentName,appointmentDay;
		ArrayList<Boolean> appontmentfirstofDay;
		public AppointmentAdapter(ArrayList<String> appDate,ArrayList<String> appTime
				,ArrayList<Boolean> appfirstofDay,ArrayList<String> appRoom,ArrayList<String> appName) {
			super(Schedule.this,R.layout.schedule_event, R.id.schedule_event_room,
					appRoom);
			this.appointmentDay=appDate;
			this.appointmentTime=appTime;
			this.appointmentName=appName;
			this.appontmentfirstofDay=appfirstofDay;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = super.getView(position, convertView, parent);
			
			
			TextView AppTimeView = (TextView) row
					.findViewById(R.id.schedule_event_time);
			TextView AppNameView = (TextView) row
					.findViewById(R.id.schedule_event_name);
			TextView AppDayView = (TextView) row
					.findViewById(R.id.schedule_daytitlebartext);
			LinearLayout AppDayTitle = (LinearLayout) row.findViewById(R.id.schedule_daytitle);
			
			AppTimeView.setText(this.appointmentTime.get(position));
			AppNameView.setText(appointmentName.get(position));
			if(this.appontmentfirstofDay.get(position)==true) {
				AppDayTitle.setVisibility(View.VISIBLE);
				AppDayView.setText(this.appointmentDay.get(position));
				
			}
			else {
				AppDayTitle.setVisibility(View.GONE);
				AppDayView.setText(this.appointmentDay.get(position));
			}

			return row;
		}
		
	}
	

	

}
