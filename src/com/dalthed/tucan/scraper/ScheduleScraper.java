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

package com.dalthed.tucan.scraper;

import java.util.ArrayList;
import java.util.Iterator;

import org.jsoup.nodes.Element;

import android.content.Context;
import android.widget.ListAdapter;

import com.dalthed.tucan.TucanMobile;
import com.dalthed.tucan.Connection.AnswerObject;
import com.dalthed.tucan.Connection.BrowserAnswerReciever;
import com.dalthed.tucan.Connection.CookieManager;
import com.dalthed.tucan.Connection.RequestObject;
import com.dalthed.tucan.Connection.SimpleSecureBrowser;
import com.dalthed.tucan.adapters.ScheduleAdapter;
import com.dalthed.tucan.datamodel.Appointment;
import com.dalthed.tucan.exceptions.LostSessionException;
import com.dalthed.tucan.exceptions.TucanDownException;
import com.dalthed.tucan.util.ScheduleSaver;

public class ScheduleScraper extends BasicScraper {

	
	public ArrayList<Appointment> appointments;
	
	private CookieManager localCookieManager;
	private int step = 0;

	public ScheduleScraper(Context context, AnswerObject result) {
		super(context, result);
		localCookieManager = result.getCookieManager();
	}

	@Override
	public ListAdapter scrapeAdapter(int mode) throws LostSessionException, TucanDownException {
		if (checkForLostSeesion()) {

			Iterator<Element> schedDays = doc.select("div.tbMonthDay").iterator();
			int Month = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH);
			int Day = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH);
			int year = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
			if (step == 0) {
				loadNextPage();

			}
			
			if (step == 1) {
				Month = Month % 12 +1;
			}
			
			scrapeDates(step, schedDays, Month, Day,year);

			if (step == 1) {
				ArrayList<String> rooms = new ArrayList<String>();
				// save appointments for widget
				ScheduleSaver.saveSchedule(appointments);
				for(Appointment appointmnt : appointments)
					rooms.add(appointmnt.getRoom());
				ScheduleAdapter externAdapter = new ScheduleAdapter(context, appointments,rooms);
				return externAdapter;

			} else {
				step = 1;
			}

		}
		return null;
	}

	/**
	 * @param mode
	 * @param schedDays
	 * @param month
	 * @param day
	 */
	private void scrapeDates(int step, Iterator<Element> schedDays, int month, int day,int year) {
		
		while (schedDays.hasNext()) {
			
			Element next = schedDays.next();
			
			String monthday = next.attr("title");
			Iterator<Element> dayEvents = next.select("div.appMonth").iterator();
			
			if (dayEvents != null) {
				int i = 0;
				while (dayEvents.hasNext()) {

					Element nextEvent = dayEvents.next();
					if (Integer.parseInt(monthday.trim()) >= day || step == 1) {
						String[] LinktitleArgument = nextEvent.select("a").attr("title")
								.split(" / ");

						boolean isFirst = i == 0;
						i++;
						
						String[] time = LinktitleArgument[0].trim().split("-");
						String[] fromTime = time[0].trim().split(":");
						String[] toTime   = time[1].trim().split(":");
						String evLink = nextEvent.select("a").attr("href");
						Appointment appointmnt = new Appointment(year, month, Integer.parseInt(monthday.trim()), 
								Integer.parseInt(fromTime[0]), Integer.parseInt(fromTime[1]),
								Integer.parseInt(toTime[0]), Integer.parseInt(toTime[1]),
								(LinktitleArgument.length > 2) ? LinktitleArgument[2].trim() : "", 
								LinktitleArgument[1].trim(),evLink);
						appointmnt.setFirstDay(isFirst);
						appointments.add(appointmnt);

						

					}
				}
			}
		}
	}

	/**
	 * 
	 */
	private void loadNextPage() {
		appointments = new ArrayList<Appointment>();
		
		String nextLink = TucanMobile.TUCAN_PROT + TucanMobile.TUCAN_HOST
				+ doc.select("a[name=skipForward_btn]").attr("href");
		if (context instanceof BrowserAnswerReciever) {
			SimpleSecureBrowser callOverviewBrowser = new SimpleSecureBrowser(
					(BrowserAnswerReciever) context);
			RequestObject thisRequest = new RequestObject(nextLink, localCookieManager,
					RequestObject.METHOD_GET, "");
			callOverviewBrowser.execute(thisRequest);
		}
	}

}
