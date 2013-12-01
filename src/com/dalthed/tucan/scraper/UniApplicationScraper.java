package com.dalthed.tucan.scraper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;

import android.widget.ListAdapter;

import com.dalthed.tucan.R;
import com.dalthed.tucan.Connection.AnswerObject;
import com.dalthed.tucan.adapters.UniApplicationAdapter;
import com.dalthed.tucan.exceptions.LostSessionException;
import com.dalthed.tucan.exceptions.TucanDownException;

public class UniApplicationScraper extends BasicScraper {

	private static final ArrayList<Integer> orderList = new ArrayList<Integer>() {
		{
			add(R.id.application_type);
			add(R.id.application_field);
			add(R.id.application_semester);
			add(R.id.application_status);
			add(R.id.application_begin);
			add(R.id.application_sent);
		}
	};

	public UniApplicationScraper(Context context, AnswerObject result) {
		super(context, result);
	}

	@Override
	public ListAdapter scrapeAdapter(int mode) throws LostSessionException,
			TucanDownException {
		if (checkForLostSeesion()) {
			try {
				Elements tbs = doc.select("div#contentSpacer_IE")
						.select("table.tb").get(1).select("tr.tbdata");
				Iterator<Element> tbIt = tbs.iterator();
				HashMap<Integer, ArrayList<String>> contentMap = UniApplicationAdapter
						.getEmptyContentMap();
				while (tbIt.hasNext()) {
					Element next = tbIt.next();
					Elements tds = next.select("td");
					if (tds.size() == 7) {
						for (int i = 0; i <= 5; i++) {
							contentMap.get(orderList.get(i)).add(
									tds.get(i).text());
						}
					}

				}
				UniApplicationAdapter adapter = new UniApplicationAdapter(
						context, contentMap);
				return adapter;
			} catch (ArrayIndexOutOfBoundsException e) {
				reportUnexpectedBehaviour(e);
			}
		}
		return null;
	}

}
