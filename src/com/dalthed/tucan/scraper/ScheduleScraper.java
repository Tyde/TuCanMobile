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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.jsoup.nodes.Element;

import android.content.Context;
import android.widget.ListAdapter;

import com.dalthed.tucan.R;
import com.dalthed.tucan.TucanMobile;
import com.dalthed.tucan.Connection.AnswerObject;
import com.dalthed.tucan.Connection.BrowserAnswerReciever;
import com.dalthed.tucan.Connection.CookieManager;
import com.dalthed.tucan.Connection.RequestObject;
import com.dalthed.tucan.Connection.SimpleSecureBrowser;
import com.dalthed.tucan.adapters.ScheduleAdapter;
import com.dalthed.tucan.exceptions.LostSessionException;
import com.dalthed.tucan.exceptions.TucanDownException;

public class ScheduleScraper extends BasicScraper {

	public ArrayList<String> eventLink, eventDay, eventTime, eventRoom, eventName;
	public ArrayList<Boolean> firstEventofDay;
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
				ScheduleAdapter externAdapter = new ScheduleAdapter(context, eventDay, eventTime,
						firstEventofDay, eventRoom, eventName);
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
		
		SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
		
		
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

						if (i == 0) {
							firstEventofDay.add(true);
						} else {
							firstEventofDay.add(false);
						}
						i++;
						StringBuilder displayDate = new StringBuilder();
						if (Integer.parseInt(monthday.trim()) == day && step == 0) {
							
							displayDate.append(context.getResources().getString(R.string.schedule_today));
							
						} else if (Integer.parseInt(monthday.trim()) == (day + 1) && step == 0) {
							displayDate.append(context.getResources().getString(R.string.schedule_tomorrow));
							
						} else {
							displayDate.append(monthday).append(".").append((month % 12 + 1));
							
						}
						String weekday = sdf.format(new Date(year-1900, month, Integer.parseInt(monthday.trim())));
						displayDate.append(" - ").append(weekday);
						eventDay.add(displayDate.toString());

						eventTime.add(LinktitleArgument[0].trim());
						eventRoom.add(LinktitleArgument[1].trim());
						if (LinktitleArgument.length > 2) {
							eventName.add(LinktitleArgument[2].trim());
						} else {
							eventName.add("");
						}
						eventLink.add(nextEvent.select("a").attr("href"));

					}
				}
			}
		}
	}

	/**
	 * 
	 */
	private void loadNextPage() {
		eventDay = new ArrayList<String>();
		eventTime = new ArrayList<String>();
		eventRoom = new ArrayList<String>();
		eventName = new ArrayList<String>();
		eventLink = new ArrayList<String>();
		firstEventofDay = new ArrayList<Boolean>();
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
