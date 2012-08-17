package com.dalthed.tucan.scraper;

import java.util.ArrayList;
import java.util.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
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
	private boolean thereAreFiles = false;
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
	public ListAdapter scrapeAdapter(int mode) throws LostSessionException,TucanDownException {
		if (checkForLostSeesion()) {
			if (PREPCall == false) {

				if(checkforModule()){
					return null;
				}
				String Title = doc.select("h1").text();
				if (fsh != null) {
					fsh.setSubtitle(Title);
				}

				scrapeInformations();

				Iterator<Element> captionIt = doc.select("caption").iterator();
				Iterator<Element> dateTable = null;
				Iterator<Element> materialTable = null;
				while (captionIt.hasNext()) {
					Element next = captionIt.next();
					if (next.text().equals("Termine")) {

						dateTable = next.parent().select("tr").iterator();
					} else if (next.text().contains("Material")) {

						materialTable = next.parent().select("tr").iterator();
					}
				}
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
				thereAreFiles = true;

				mPageAdapter.fileList = materialLink;
			}
		} else if (mPageAdapter != null) {
			mPageAdapter.setAdapter(new ArrayAdapter<String>(context,
					android.R.layout.simple_list_item_1, new String[] { "Kein Material" }));
			thereAreFiles = false;
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
	private void scrapeInformations() {
		Elements Deltarows = doc.select("table[courseid]").first().select("tr");
		Element rows;
		if (Deltarows.size() == 1) {
			rows = Deltarows.get(0).select("td").first();
		} else {
			rows = Deltarows.get(1).select("td").first();
		}

		Elements Paragraphs = rows.select("p");
		Iterator<Element> PaIt = Paragraphs.iterator();
		ArrayList<String> titles = new ArrayList<String>();
		ArrayList<String> values = new ArrayList<String>();

		while (PaIt.hasNext()) {

			Element next = PaIt.next();
			String[] information = crop(next.html());
			titles.add(information[0]);
			values.add(information[1]);

		}
		if (mPageAdapter != null) {
			mPageAdapter.setAdapter(new TwoLinesAdapter(context, titles, values));
		}
	}

	private static String[] crop(String startstring) {
		if (startstring.length() > 0) {
			String[] splitted = startstring.split("</b>");
			return new String[] { Jsoup.parse(splitted[0]).text().trim(),
					Jsoup.parse(splitted[1]).text() };
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
