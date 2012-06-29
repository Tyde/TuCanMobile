package com.dalthed.tucan.scraper;

import java.util.ArrayList;
import java.util.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.dalthed.tucan.R;
import com.dalthed.tucan.Connection.AnswerObject;
import com.dalthed.tucan.exceptions.LostSessionException;
import com.dalthed.tucan.ui.Events.OnItemSelectedListener;
import com.dalthed.tucan.ui.SimpleWebListActivity;
import com.dalthed.tucan.adapters.ThreeLinesAdapter;

public class EventsScraper extends BasicScraper {
	private int mode;
	public ArrayList<String> eventLinks;
	public ArrayList<String> eventNames;
	private ArrayAdapter<String> ListAdapter;
	public ArrayList<String> applyLink;
	public ArrayList<String> eventLink;
	public ArrayList<String> SemesterOptionName;
	public ArrayList<String> SemesterOptionValue;
	public int SemesterOptionSelected;

	public EventsScraper(Context context, AnswerObject result) {
		super(context, result);
		
	}
	
	

	public ListAdapter scrapeAdapter(int modus) throws LostSessionException {
		this.mode = modus;
		if (doc.select("span.notLoggedText").text().length() > 0) {
			// Check for logged out
			throw new LostSessionException();
		} else {
			// When bug exists: send HTML to resolve Bug
			SimpleWebListActivity.sendHTMLatBug(doc.html());
			if (mode == 0) {
				// LinkGruppe Veranstaltungen finden und durchlaufen
				return getMenuAdapter();
			} else if (mode == 2) {
				// Modus Anmeldung
				return getApplicationAdapter();
			} else {
				eventLink = new ArrayList<String>();
				if (mode == 10) {
					// Modus Module
					return getModules();
				} else if (mode == 1) {
					// Modus Veranstaltungen
					return getEvents();
				}
			}
		}
		return null;

	}

	public SpinnerAdapter spinnerAdapter() {
		if (doc == null) {
			return null;
		}
		SemesterOptionName = new ArrayList<String>();
		SemesterOptionValue = new ArrayList<String>();

		Iterator<Element> SemesterOptionIterator = doc.select("option").iterator();
		int i = 0;
		while (SemesterOptionIterator.hasNext()) {
			Element next = SemesterOptionIterator.next();
			SemesterOptionName.add(next.text());
			SemesterOptionValue.add(next.attr("value"));
			if (next.hasAttr("selected")) {
				Log.i(LOG_TAG, next.text() + " is selected, has val " + i);
				SemesterOptionSelected = i;
			}
			i++;
		}
		ArrayAdapter<String> SpinnerAdapter = new ArrayAdapter<String>(context,
				android.R.layout.simple_spinner_item, SemesterOptionName);
		return SpinnerAdapter;
	}

	/**
	 * @return
	 */
	private ListAdapter getEvents() {
		ArrayList<String> eventName = new ArrayList<String>();
		ArrayList<String> eventHead = new ArrayList<String>();
		ArrayList<String> eventTime = new ArrayList<String>();
		Element ModuleOverviewTable = doc.select("div.tb").first();
		Iterator<Element> ExamRowIterator = ModuleOverviewTable.select("tbody").first()
				.select("tr").iterator();
		while (ExamRowIterator.hasNext()) {
			Element next = ExamRowIterator.next();
			Elements ExamCols = next.select("td");
			if (ExamCols.size() > 0) {
				eventName.add(ExamCols.get(2).text());
				eventLink.add(ExamCols.get(2).select("a").attr("href"));
				Log.i(LOG_TAG, "Link" + ExamCols.get(2).select("a").attr("href"));
				eventHead.add(ExamCols.get(3).text());
				eventTime.add(ExamCols.get(4).text());
			}
		}
		if(ListAdapter!=null){
			ListAdapter.clear();
		}
		ListAdapter = new ThreeLinesAdapter(context, eventName, eventTime, eventHead);
		return ListAdapter;
	}

	/**
	 * @return
	 */
	private ListAdapter getModules() {
		ArrayList<String> eventName = new ArrayList<String>();
		ArrayList<String> eventHead = new ArrayList<String>();
		ArrayList<String> eventCredits = new ArrayList<String>();
		Element ModuleOverviewTable = doc.select("div.tb").first();
		Iterator<Element> ExamRowIterator = ModuleOverviewTable.select("tbody").first()
				.select("tr").iterator();
		while (ExamRowIterator.hasNext()) {
			Element next = ExamRowIterator.next();
			Elements ExamCols = next.select("td");
			if (ExamCols.size() > 0) {
				eventName.add(ExamCols.get(2).text());
				eventHead.add(ExamCols.get(3).text());
				eventCredits.add(ExamCols.get(4).text());
				eventLink.add(ExamCols.get(2).select("a").attr("href"));
				Log.i(LOG_TAG, "Link" + ExamCols.get(2).select("a").attr("href"));
			}
		}
		if(ListAdapter!=null){
			ListAdapter.clear();
		}
		ListAdapter = new ThreeLinesAdapter(context, eventName, eventCredits, eventHead);
		return ListAdapter;
	}

	/**
	 * @return
	 */
	private ListAdapter getApplicationAdapter() {
		if (doc.select("table.tbcoursestatus") == null) {
			Elements ListElements = doc.select("div#contentSpacer_IE").first().select("ul").first()
					.select("li");
			Iterator<Element> ListIterator = ListElements.iterator();
			applyLink = new ArrayList<String>();
			ArrayList<String> applyName = new ArrayList<String>();
			while (ListIterator.hasNext()) {
				Element next = ListIterator.next();
				// Log.i(LOG_TAG,next.select("a").attr("href"));
				applyLink.add(next.select("a").attr("href"));
				applyName.add(next.text());
			}
			ListAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1,
					applyName);
			return ListAdapter;
		} else {
			Log.i(LOG_TAG, doc.select("table.tbcoursestatus").html());
			return null;
			// TODO : Call importand Intent
		}
	}

	private ListAdapter getMenuAdapter() {
		Elements links = doc.select("li#link000273").select("li");

		Iterator<Element> linkIt = links.iterator();
		eventLinks = new ArrayList<String>();
		eventNames = new ArrayList<String>();
		while (linkIt.hasNext()) {
			Element next = linkIt.next();
			String id = next.id();
			// Links Veranstaltugen und Module finden und aufnehmen
			if (id.equals("link000275") || id.equals("link000274")) {
				// /|| id.equals("link000311")
				eventLinks.add(next.select("a").attr("href"));
				eventNames.add(next.select("a").text());
			}
		}
		// ArrayList kopieren um Links nicht neu laden zu müssen
		@SuppressWarnings("unchecked")
		ArrayList<String> eventNameBuffer = (ArrayList<String>) eventNames.clone();
		// Adapter erstellen und einsetzen
		ListAdapter = new ArrayAdapter<String>(context, R.layout.menu_row,
				R.id.main_menu_row_textField,
				eventNameBuffer);
		return ListAdapter;
	}
}
