package com.dalthed.tucan.scraper;

import java.util.ArrayList;

import org.jsoup.select.Elements;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import com.dalthed.tucan.Connection.AnswerObject;
import com.dalthed.tucan.exceptions.LostSessionException;
import com.dalthed.tucan.exceptions.TucanDownException;

public class ModuleScraper extends BasicScraper {
	public ArrayList<String> eventLinks;
	public String title;

	public ModuleScraper(Context context, AnswerObject result) {
		super(context, result);
	}

	@Override
	public ListAdapter scrapeAdapter(int mode) throws LostSessionException, TucanDownException {
		if (checkForLostSeesion()) {
			return scrapeModule();
		} else {
			return null;
		}
	}

	/**
	 * Extracts the Module Information from the HTML code
	 * @return ListAdapter for the Activity
	 */
	private ListAdapter scrapeModule() {
		title = doc.select("h1").text();
		Elements events = doc.select("a[name=eventLink]");
		// System.out.println(events);
		ArrayList<String> eventNames = new ArrayList<String>();
		eventLinks = new ArrayList<String>();
		if (events.size() % 3 == 0) {
			for (int i = 0; i < events.size(); i += 3) {
				eventNames.add(events.get(i).text() + " " + events.get(i + 1).text() + " "
						+ events.get(i + 2).text());
				eventLinks.add(events.get(i).attr("href"));
				System.out.println(events.get(i).attr("href"));
			}
		}

		ArrayAdapter<String> simpleAdapter = new ArrayAdapter<String>(context,
				android.R.layout.simple_list_item_1, eventNames);
		return simpleAdapter;
	}
}
