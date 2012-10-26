package com.dalthed.tucan.scraper;

import java.util.ArrayList;

import org.jsoup.select.Elements;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.Spanned;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.dalthed.tucan.R;
import com.dalthed.tucan.TuCanMobileActivity;
import com.dalthed.tucan.Connection.AnswerObject;
import com.dalthed.tucan.exceptions.LostSessionException;
import com.dalthed.tucan.exceptions.TucanDownException;

public class SingleMessageScraper extends BasicScraper {


	public SingleMessageScraper(Context context, AnswerObject result) {
		super(context, result);
	}

	@Override
	public ListAdapter scrapeAdapter(int mode) throws LostSessionException, TucanDownException {
		if (checkForLostSeesion()) {

			return scrapeMessageInformations();

		}
		return null;
	}

	/**
	 * 
	 */
	private ListAdapter scrapeMessageInformations() {
		Elements tableRows = doc.select("table.tb").select("tr");
		ArrayList<Spanned> informations = new ArrayList<Spanned>();

		StringBuilder authorText = new StringBuilder();
		StringBuilder dateText = new StringBuilder();
		StringBuilder titleText = new StringBuilder();
		StringBuilder textText = new StringBuilder();
		
		if (tableRows != null && tableRows.size()>5) {
			authorText.append(context.getResources().getString(R.string.messages_from)).append(" ");
			dateText.append(context.getResources().getString(R.string.messages_time)).append(" ");
			titleText.append(context.getResources().getString(R.string.messages_title)).append(" ");
			
			authorText.append((tableRows.get(2).select("td").get(1).text()));
			
			dateText.append((tableRows.get(3).select("td").get(1).text()));
			titleText.append((tableRows.get(4).select("td").get(1).text()));
			textText.append((tableRows.get(5).select("td").get(1).html()));
			informations.add(Html.fromHtml(authorText.toString()));
			informations.add(Html.fromHtml(dateText.toString()));
			informations.add(Html.fromHtml(titleText.toString()));
			informations.add(Html.fromHtml(textText.toString()));
		}
		ArrayAdapter<Spanned> adapter = new ArrayAdapter<Spanned>(context, android.R.layout.simple_list_item_1, informations);
		return adapter;
	}

}
