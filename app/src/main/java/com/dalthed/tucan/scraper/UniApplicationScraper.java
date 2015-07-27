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
