package com.dalthed.tucan.util;

import java.util.ArrayList;

import android.content.Context;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;

import com.dalthed.tucan.scraper.BasicScraper;

public class ConfigurationChangeStorage {

	private ArrayList<BasicScraper> scrapers;

	public ArrayList<ListAdapter> adapters;


	public ConfigurationChangeStorage() {
		scrapers = new ArrayList<BasicScraper>();
		adapters = new ArrayList<ListAdapter>();

	}

	/**
	 * Gibt den Scraper mit angepassten Context zurück
	 * 
	 * @param index
	 * @param context
	 * @return
	 */
	public BasicScraper getScraper(int index, Context context) {
		if (scrapers.size() > index) {
			BasicScraper returnScraper = scrapers.get(index);
			if (returnScraper != null) {

				returnScraper.renewContext(context);
				return returnScraper;
			}
		}
		return null;

	}
	
	public void addScraper(BasicScraper scrape) {
		scrapers.add(scrape);
	}

}
