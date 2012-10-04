package com.dalthed.tucan.scraper;

import org.jsoup.select.Elements;

import android.content.Context;
import android.content.Intent;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.dalthed.tucan.R;
import com.dalthed.tucan.TuCanMobileActivity;
import com.dalthed.tucan.Connection.AnswerObject;
import com.dalthed.tucan.exceptions.LostSessionException;
import com.dalthed.tucan.exceptions.TucanDownException;

public class SingleMessageScraper extends BasicScraper {

	public String authorText;
	public String dateText;
	public String titleText;
	public String textText;

	public SingleMessageScraper(Context context, AnswerObject result) {
		super(context, result);
	}

	@Override
	public ListAdapter scrapeAdapter(int mode) throws LostSessionException, TucanDownException {
		if (checkForLostSeesion()) {

			scrapeMessageInformations();

		}
		return null;
	}

	/**
	 * 
	 */
	private void scrapeMessageInformations() {
		Elements TableRows = doc.select("table.tb").select("tr");

		authorText = (TableRows.get(2).select("td").get(1).text());
		dateText = (TableRows.get(3).select("td").get(1).text());
		titleText = (TableRows.get(4).select("td").get(1).text());
		textText = (TableRows.get(5).select("td").get(1).html().replaceAll("<br />", "\n"));
	}

}
