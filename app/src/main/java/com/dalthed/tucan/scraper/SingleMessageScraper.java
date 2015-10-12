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

import org.jsoup.select.Elements;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import com.dalthed.tucan.R;
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
//		ArrayAdapter<Spanned> adapter = new ArrayAdapter<Spanned>(context, android.R.layout.simple_list_item_1, informations);
		ArrayAdapter<Spanned> adapter = new ArrayAdapter<Spanned>(context, R.layout.customlistview, informations);
		return adapter;
	}

}
