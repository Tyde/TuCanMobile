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

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import com.dalthed.tucan.TucanMobile;
import com.dalthed.tucan.Connection.AnswerObject;
import com.dalthed.tucan.Connection.CookieManager;
import com.dalthed.tucan.adapters.ThreeLinesAdapter;
import com.dalthed.tucan.exceptions.LostSessionException;
import com.dalthed.tucan.exceptions.TucanDownException;
import com.dalthed.tucan.ui.FragmentSingleEvent;


public class VVEventsScraper extends BasicScraper implements OnItemClickListener {

	public String[] Eventlink;
	private CookieManager localCookieManager;

	public VVEventsScraper(Context context, AnswerObject result) {
		super(context, result);
		this.localCookieManager = result.getCookieManager();
	}

	@Override
	public ListAdapter scrapeAdapter(int mode) throws LostSessionException,TucanDownException {
		if(checkForLostSeesion()){
			return getListOfEvents();
		}
		return null;
	}

	/**
	 * @return
	 */
	private ListAdapter getListOfEvents() {
		Elements tbdata = doc.select("tr.tbdata");
		String[] Eventnames = new String[tbdata.size()];
		String[] Eventdozent = new String[tbdata.size()];
		String[] Eventtype = new String[tbdata.size()];
		Eventlink = new String[tbdata.size()];
		int i = 0;
		if (tbdata.size() > 0) {
			Iterator<Element> EventListIterator = tbdata.iterator();
			while (EventListIterator.hasNext()) {
				Element nextElement = EventListIterator.next();
				Elements rows = nextElement.select("td");
				Element leftcolumn = rows.get(1);
				Element rightcolumn = rows.get(3);
				Eventlink[i] = leftcolumn.select("a").attr("href");
				Eventnames[i] = leftcolumn.select("a").text();
				List<Node> importantnotes = leftcolumn.childNodes();
				Iterator<Node> imnit = importantnotes.iterator();
				while (imnit.hasNext()) {
					Log.i(LOG_TAG, imnit.next().outerHtml());
				}
				Eventdozent[i] = importantnotes.get(3).toString();
				Eventtype[i] = rightcolumn.text();
				Log.i(LOG_TAG, Eventtype[i]);
				i++;
			}
		}
		ArrayAdapter<String> listAdapter = new ThreeLinesAdapter(context,Arrays.asList(Eventnames)
				,Arrays.asList(Eventtype) ,Arrays.asList(Eventdozent));
		return listAdapter;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent StartSingleEventIntent = new Intent(context, FragmentSingleEvent.class);
		StartSingleEventIntent.putExtra("URL", TucanMobile.TUCAN_PROT + TucanMobile.TUCAN_HOST
				+ this.Eventlink[position]);
		StartSingleEventIntent.putExtra("Cookie",
				localCookieManager.getCookieHTTPString(TucanMobile.TUCAN_HOST));
		// StartSingleEventIntent.putExtra("UserName", UserName);
		context.startActivity(StartSingleEventIntent);
	}

}
