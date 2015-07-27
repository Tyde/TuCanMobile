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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import com.dalthed.tucan.TucanMobile;
import com.dalthed.tucan.Connection.AnswerObject;
import com.dalthed.tucan.Connection.BrowserAnswerReciever;
import com.dalthed.tucan.Connection.CookieManager;
import com.dalthed.tucan.Connection.RequestObject;
import com.dalthed.tucan.Connection.SimpleSecureBrowser;
import com.dalthed.tucan.adapters.AppointmentAdapter;
import com.dalthed.tucan.adapters.TwoLinesAdapter;
import com.dalthed.tucan.exceptions.LostSessionException;
import com.dalthed.tucan.exceptions.TucanDownException;
import com.dalthed.tucan.helpers.FastSwitchHelper;
import com.dalthed.tucan.ui.FragmentSingleEvent.PagerAdapter;
import com.dalthed.tucan.ui.Module;

public class SingleEventScraper extends BasicScraper {
	/**
	 * Prepcall wird bei einem Intent aus dem Schedule mitgesendet, und
	 * bedeutet, dass eine Seite geöffnet wurde, welche nur informationen zum
	 * einzelnen Termin gibt.
	 */
	public boolean PREPCall;
	private CookieManager localCookieManager;
	private FastSwitchHelper fsh;
	private PagerAdapter mPageAdapter;
	public ArrayList<String> materialLink;

	private ViewPager mPager;

	public SingleEventScraper(Context context, AnswerObject result, Boolean PREPCall,
			FastSwitchHelper fsh, PagerAdapter mPageAdapter, ViewPager mPager) {
		super(context, result);
		this.PREPCall = PREPCall;
		this.localCookieManager = result.getCookieManager();
		this.fsh = fsh;
		this.mPageAdapter = mPageAdapter;
		this.mPager = mPager;
	}

	@Override
	public ListAdapter scrapeAdapter(int mode) throws LostSessionException, TucanDownException {
		if (checkForLostSeesion()) {
			if (PREPCall == false) {

				if (checkforModule()) {
					return null;
				}
				String Title = doc.select("h1").text().replace("\n"," ");
				if (fsh != null) {
					fsh.setSubtitle(Title);
				}



				Iterator<Element> captionIt = doc.select("caption").iterator();
				Iterator<Element> dateTable = null;
				Iterator<Element> materialTable = null;
                Iterator<Element> informationTable = null;
				while (captionIt.hasNext()) {
					Element next = captionIt.next();
					if (next.text().equals("Termine")) {

						dateTable = next.parent().select("tr").iterator();
					} else if (next.text().contains("Material")) {

						materialTable = next.parent().select("tr").iterator();
					} else if (next.text().contains("Veranstaltungsdetails")) {
                        informationTable = next.parent().select("tr").iterator();
                    }
				}

                scrapeInformations(informationTable);
				scrapeAppointments(dateTable);

				scrapeMaterials(materialTable);
				if (mPageAdapter != null) {
					mPageAdapter.initializeData(mPager);
				}

			} else {
				// Es handelt sich um eine Übersichtsseite zu einem einzelnen
				// Termin
				callRealEventPage();

			}
		}
		return null;
	}

	public void configurationChange(FastSwitchHelper fsh, PagerAdapter mPageAdapter,
			ViewPager mPager) throws LostSessionException, TucanDownException {
		this.fsh = fsh;
		this.mPageAdapter = mPageAdapter;
		this.mPager = mPager;
		scrapeAdapter(0);
	}

	/**
	 * 
	 */
	private void callRealEventPage() {
		String nextlink = TucanMobile.TUCAN_PROT + TucanMobile.TUCAN_HOST
				+ doc.select("div.detailout").select("a").attr("href");
		if (context instanceof BrowserAnswerReciever) {
			SimpleSecureBrowser callOverviewBrowser = new SimpleSecureBrowser(
					(BrowserAnswerReciever) context);
			RequestObject thisRequest = new RequestObject(nextlink, localCookieManager,
					RequestObject.METHOD_GET, "");
			PREPCall = false;
			callOverviewBrowser.execute(thisRequest);
		}
	}

	/**
	 * @param materialTable
	 */
	private void scrapeMaterials(Iterator<Element> materialTable) {
		int ct = 0;
		ArrayList<String> materialNumber = new ArrayList<String>();
		ArrayList<String> materialName = new ArrayList<String>();
		ArrayList<String> materialDesc = new ArrayList<String>();
		materialLink = new ArrayList<String>();
		ArrayList<String> materialFile = new ArrayList<String>();
		int mod = 0;
		if (materialTable != null) {
			while (materialTable.hasNext()) {
				Element next = materialTable.next();

				if (next.select("td").size() > 1) {
					ct++;

					if (next.select("td").get(0).text().matches("[0-9]+")) {
						// First line

						materialNumber.add(next.select("td").get(0).text());
						materialName.add(next.select("td").get(1).text());
						if (mod == 1) {
							materialDesc.add("");
							mod = 2;
						}
						if (mod == 2) {
							materialLink.add("");
							materialFile.add("");
						}

						mod = 1;
					} else if (mod == 1) {

						materialDesc.add(next.select("td").get(1).text());
						mod = 2;
					} else if (mod == 2) {

						materialLink.add(next.select("td").get(1).select("a").attr("href"));
						materialFile.add(next.select("td").get(1).select("a").text());
						mod = 0;
					}
				}
			}
		}
		if (mod == 1) {
			materialDesc.add("");
			mod = 2;
		}
		if (mod == 2) {
			materialLink.add("");
			materialFile.add("");
		}
		if (ct > 2) {
			if (mPageAdapter != null) {
				mPageAdapter.setAdapter(new AppointmentAdapter(context, materialNumber,
						materialFile, null, materialName, materialDesc));

				mPageAdapter.fileList = materialLink;
			}
		} else if (mPageAdapter != null) {
			mPageAdapter.setAdapter(new ArrayAdapter<String>(context,
					android.R.layout.simple_list_item_1, new String[] { "Kein Material" }));

		}
	}

	/**
	 * @param DateTable
	 */
	private void scrapeAppointments(Iterator<Element> DateTable) {
		ArrayList<String> eventNumber = new ArrayList<String>();
		ArrayList<String> eventDate = new ArrayList<String>();
		ArrayList<String> eventTime = new ArrayList<String>();

		ArrayList<String> eventRoom = new ArrayList<String>();
		ArrayList<String> eventInstructor = new ArrayList<String>();
		if (DateTable != null) {
			while (DateTable.hasNext()) {
				Element next = DateTable.next();
				Elements cols = next.select("td");
				if (cols.size() > 5) {
					eventNumber.add(cols.get(0).text());
					eventDate.add(cols.get(1).text());
					eventTime.add(cols.get(2).text() + "-" + cols.get(3).text());
					eventRoom.add(cols.get(4).text());
					eventInstructor.add(cols.get(5).text());
				}

			}

		} else {
			eventDate.add("");
			eventTime.add("");
			eventNumber.add("");
			eventRoom.add("Keine Daten vorhanden");
			eventInstructor.add("");
		}
		if (mPageAdapter != null) {
			mPageAdapter.setAdapter(new AppointmentAdapter(context, eventDate, eventTime,
					eventNumber, eventRoom, eventInstructor));
		}
	}

	/**
	 * 
	 */
	private void scrapeInformations(Iterator<Element> informationIterator) {

        while (informationIterator.hasNext()) {

            Element nextElement = informationIterator.next();

            Elements td = nextElement.select("td");
            if(td!=null && td.hasClass("tbdata")){
                Elements Paragraphs = nextElement.select("p");
                Iterator<Element> PaIt = Paragraphs.iterator();
                ArrayList<String> titles = new ArrayList<String>();
                ArrayList<String> values = new ArrayList<String>();

                while (PaIt.hasNext()) {

                    Element next = PaIt.next();
                    String[] information = crop(next.html());
                    if(information[1].length() > 0){
                        titles.add(information[0]);
                        values.add(information[1]);
                    }

                }
                Log.i(LOG_TAG, "Informationscraper working");
                if (mPageAdapter != null) {
                    Log.i(LOG_TAG, "InformationAdapter set");
                    mPageAdapter.setAdapter(new TwoLinesAdapter(context, titles, values));
                }
            }
        }
	}

	private static String[] crop(String startstring) {
		int pos = startstring.indexOf("</b>"); //wir suchen nur den ersten close-Tag. Bei Split wuerden wir bei jedem </b> splitten!
		if (startstring.length() > 0 && pos != -1 && startstring.length() > pos+4) {
			String first = startstring.substring(0, pos+4);
			String second = startstring.substring(pos+4).trim();
			if(!first.endsWith(":</b>")) // Doppelpunkte immer in der ersten Zeile
				first.replaceAll("</b>", ":</b>");
			if(second.startsWith(":<br />")) // und nicht manchmal in der zweiten
				second = second.substring(7).trim();
			while(second.startsWith("<br />")) // Schwachsinn entfernen, z.B. " <br /> <br />SomeText"
				second = second.replaceFirst("<br />", "").trim();
			while(second.endsWith("<br />")) // Schwachsinn entfernen, z.B. "SomeText <br /> <br />"
				second = second.substring(0, second.length()-6).trim();
			
			return new String[] { Jsoup.parse(first).text().trim(),
					second.trim() };
		} else {
			return new String[] { "", "" };

		}
	}

	/**
	 * 
	 */
	private Boolean checkforModule() {
		if (!doc.select("form[name=moduleform]").isEmpty()) {
			// Möglicherweise ist ein Modul angewählt
			Intent goToModule = new Intent(context, Module.class);

			goToModule.putExtra("Cookie",
					localCookieManager.getCookieHTTPString(TucanMobile.TUCAN_HOST));
			goToModule.putExtra("URL", "HTML");
			goToModule.putExtra("HTML", doc.html());
			context.startActivity(goToModule);
			return true;
		}
		return false;
	}

}
