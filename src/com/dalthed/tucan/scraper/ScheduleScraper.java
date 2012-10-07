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
			if (step == 0) {
				loadNextPage();

			}
			if (step == 1) {
				Month++;
			}
			scrapeDates(step, schedDays, Month, Day);

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
	 * @param Month
	 * @param Day
	 */
	private void scrapeDates(int step, Iterator<Element> schedDays, int Month, int Day) {
		while (schedDays.hasNext()) {
			Element next = schedDays.next();
			String monthday = next.attr("title");
			Iterator<Element> dayEvents = next.select("div.appMonth").iterator();
			if (dayEvents != null) {
				int i = 0;
				while (dayEvents.hasNext()) {

					Element nextEvent = dayEvents.next();
					if (Integer.parseInt(monthday.trim()) >= Day || step == 1) {
						String[] LinktitleArgument = nextEvent.select("a").attr("title")
								.split(" / ");

						if (i == 0) {
							firstEventofDay.add(true);
						} else {
							firstEventofDay.add(false);
						}
						i++;
						if (Integer.parseInt(monthday.trim()) == Day && step == 0) {
							eventDay.add("Heute");
						} else if (Integer.parseInt(monthday.trim()) == (Day + 1) && step == 0) {
							eventDay.add("Morgen");
						} else {
							eventDay.add(monthday + "." + (Month + 1));
						}

						eventTime.add(LinktitleArgument[0].trim());
						eventRoom.add(LinktitleArgument[1].trim());
						eventName.add(LinktitleArgument[2].trim());
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
