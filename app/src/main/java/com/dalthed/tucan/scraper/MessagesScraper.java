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
import java.util.Iterator;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.util.Log;
import android.widget.ListAdapter;

import com.dalthed.tucan.TucanMobile;
import com.dalthed.tucan.connection.AnswerObject;
import com.dalthed.tucan.connection.BrowserAnswerReciever;
import com.dalthed.tucan.connection.CookieManager;
import com.dalthed.tucan.connection.RequestObject;
import com.dalthed.tucan.connection.SimpleSecureBrowser;
import com.dalthed.tucan.adapters.ThreeLinesAdapter;
import com.dalthed.tucan.exceptions.LostSessionException;
import com.dalthed.tucan.exceptions.TucanDownException;

public class MessagesScraper extends BasicScraper {

	private CookieManager localCookieManager;
	private int step = 0;
	public ArrayList<String> messageLink;

	public MessagesScraper(Context context, AnswerObject result) {
		super(context, result);
		localCookieManager = result.getCookieManager();
	}

	@Override
	public ListAdapter scrapeAdapter(int mode) throws LostSessionException,
			TucanDownException {
		Log.i(LOG_TAG, "Daten ausgewertet in Step: "+ step);
		if (checkForLostSeesion()) {
			if (step == 0) {
				loadArchive();
				step++;
			} else if (step == 1) {
				loadInbox();
				step++;
			} else {
				return extractMessageList();
			}
		}
		return null;
	}

	/**
	 * Finds the Inbox link and opens it
	 */
	private void loadInbox() {
		try {
			String link = TucanMobile.TUCAN_PROT
					+ TucanMobile.TUCAN_HOST
					+ doc.select("div.tbcontrol").first().select("a").get(1)
							.attr("href");

			if (context instanceof BrowserAnswerReciever) {
				SimpleSecureBrowser callInboxBrowser = new SimpleSecureBrowser(
						(BrowserAnswerReciever) context);
				RequestObject thisRequest = new RequestObject(link,
						localCookieManager, RequestObject.METHOD_GET, "");
				callInboxBrowser.execute(thisRequest);
			}
		} catch (NullPointerException e) {
			reportUnexpectedBehaviour(e);
		} catch (ArrayIndexOutOfBoundsException e) {
			reportUnexpectedBehaviour(e);
		}
	}

	/**
	 * 
	 */
	private ListAdapter extractMessageList() {
		Iterator<Element> messageRows = doc.select("tr.tbdata").iterator();
		ArrayList<String> messageDate = new ArrayList<String>();
		ArrayList<String> messageAuthor = new ArrayList<String>();
		ArrayList<String> messageTitle = new ArrayList<String>();
		messageLink = new ArrayList<String>();
		try {
			while (messageRows.hasNext()) {
				Element next = messageRows.next();
				Elements MessageCols = next.select("td");
				if (MessageCols.size() > 0) {
					messageDate.add(MessageCols.get(1).text() + " - "
							+ MessageCols.get(2).text());
					messageAuthor.add("Von: " + MessageCols.get(3).text());
					messageTitle.add(MessageCols.get(4).text());
					messageLink
							.add(MessageCols.get(4).select("a").attr("href"));
				}
			}
		} catch (NullPointerException e) {
			reportUnexpectedBehaviour(e);
		} catch (ArrayIndexOutOfBoundsException e) {
			reportUnexpectedBehaviour(e);
		}
		ThreeLinesAdapter listAdapter = new ThreeLinesAdapter(context,
				messageTitle, messageDate, messageAuthor);
		return listAdapter;
	}

	/**
	 * 
	 */
	private void loadArchive() {
		try {
			String AllMailLink = TucanMobile.TUCAN_PROT
					+ TucanMobile.TUCAN_HOST
					+ doc.select("div.tbcontrol").select("a").last()
							.attr("href");

			if (context instanceof BrowserAnswerReciever) {
				SimpleSecureBrowser callOverviewBrowser = new SimpleSecureBrowser(
						(BrowserAnswerReciever) context);
				RequestObject thisRequest = new RequestObject(AllMailLink,
						localCookieManager, RequestObject.METHOD_GET, "");
				callOverviewBrowser.execute(thisRequest);
			}
		} catch (NullPointerException e) {
			reportUnexpectedBehaviour(e);
		} catch (ArrayIndexOutOfBoundsException e) {
			reportUnexpectedBehaviour(e);
		}
	}

}
