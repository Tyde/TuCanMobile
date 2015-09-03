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
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.SpinnerAdapter;

import com.dalthed.tucan.R;
import com.dalthed.tucan.TucanMobile;
import com.dalthed.tucan.Connection.AnswerObject;
import com.dalthed.tucan.adapters.HighlightedThreeLinesAdapter;
import com.dalthed.tucan.adapters.MergedAdapter;
import com.dalthed.tucan.adapters.ThreeLinesAdapter;
import com.dalthed.tucan.exceptions.LostSessionException;
import com.dalthed.tucan.exceptions.TucanDownException;
import com.dalthed.tucan.ui.SimpleWebListActivity;

public class EventsScraper extends BasicScraper {
	private int mode;
	/**
	 * Links zu den einzelnen Untermodulen
	 */
	public ArrayList<String> eventLinks;
	/**
	 * Namen deer einzelnen Events
	 */
	public ArrayList<String> eventNames;
	private ArrayAdapter<String> ListAdapter;
	/**
	 * Links zur Anmeldung zu Events
	 */
	public ArrayList<String> applyLink;
	/**
	 * Links zu den einzelnen Events
	 */
	public ArrayList<String> eventLink;
	/**
	 * Option zum Einstellen des Semesters - Name
	 */
	public ArrayList<String> SemesterOptionName;
	/**
	 * Option zum einstellen des Semesters - Link
	 */
	public ArrayList<String> SemesterOptionValue;
	/**
	 * id des gewählten Semesters
	 */
	public int SemesterOptionSelected;

	public EventsScraper(Context context, AnswerObject result) {
		super(context, result);

	}

	public ListAdapter scrapeAdapter(int modus) throws LostSessionException,
			TucanDownException {
		this.mode = modus;
		if (checkForLostSeesion()) {
			// When bug exists: send HTML to resolve Bug
			SimpleWebListActivity.sendHTMLatBug(doc.html());
			
			switch (mode) {
			case 0:
				// LinkGruppe Veranstaltungen finden und durchlaufen
				return getMenuAdapter();
			case 2:
				// Modus Anmeldung
				return getApplicationAdapter();
			case 10:
				eventLink = new ArrayList<String>();
				// Modus Module
				return getModules();
			case 1:
				eventLink = new ArrayList<String>();
				// Modus Veranstaltungen
				return getEvents();
			}
		}
		return null;

	}

	/**
	 * Gibt den Adapter für den Spinner zurück
	 * 
	 * @return Spinneradapter mit den Semestern
	 * @author Daniel Thiem
	 */
	public SpinnerAdapter spinnerAdapter() {
		if (doc == null) {
			return null;
		}
		SemesterOptionName = new ArrayList<String>();
		SemesterOptionValue = new ArrayList<String>();
		doc.select("select#semester");
		Iterator<Element> SemesterOptionIterator = doc.select("option")
				.iterator();
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
	 * Gibt einen ListAdapter mit allen Events zurück
	 * 
	 * @return ListAdapter mit Events zu Semester
	 * @author Daniel Thiem
	 */
	private ListAdapter getEvents() {

		ArrayList<String> eventName = new ArrayList<String>();
		ArrayList<String> eventHead = new ArrayList<String>();
		ArrayList<String> eventTime = new ArrayList<String>();
		Element ModuleOverviewTable = doc.select("table.tb").first();
		if (ModuleOverviewTable != null) {
			final Element tableBody = ModuleOverviewTable.select("tbody")
					.first();
			if (tableBody != null) {
				final Elements tableRow = tableBody.select("tr");
				if (tableRow != null) {
					Iterator<Element> ExamRowIterator = tableRow.iterator();
					while (ExamRowIterator.hasNext()) {
						Element next = ExamRowIterator.next();
						Elements ExamCols = next.select("td");
						if (ExamCols.size() > 0) {
							eventName.add(ExamCols.get(2).text());
							eventLink.add(ExamCols.get(2).select("a")
									.attr("href"));
							Log.i(LOG_TAG, "Link"
									+ ExamCols.get(2).select("a").attr("href"));
							eventHead.add(ExamCols.get(3).text());
							eventTime.add(ExamCols.get(4).text());
						}
					}
					if (ListAdapter != null) {
						ListAdapter.clear();
					}
					ListAdapter = new ThreeLinesAdapter(context, eventName,
							eventTime, eventHead);
					return ListAdapter;
				}
			}
		}
		return new ArrayAdapter<String>(context,
				android.R.layout.simple_list_item_1, android.R.id.text1,
				new String[] { context.getResources().getString(
						R.string.events_none_found) });

	}

	/**
	 * Gibt einen ListAdapter mit den Modulen zurück
	 * 
	 * @return ListAdapter mit dem zum Semester passenden Modulen
	 * @author Daniel Thiem
	 */
	private ListAdapter getModules() {
		ArrayList<String> eventName = new ArrayList<String>();
		ArrayList<String> eventHead = new ArrayList<String>();
		ArrayList<String> eventCredits = new ArrayList<String>();
		Element ModuleOverviewTable = doc.select("div.tb").first();
		Iterator<Element> ExamRowIterator = ModuleOverviewTable.select("tbody")
				.first().select("tr").iterator();
		while (ExamRowIterator.hasNext()) {
			Element next = ExamRowIterator.next();
			Elements ExamCols = next.select("td");
			if (ExamCols.size() > 4) {
				eventName.add(ExamCols.get(2).text());
				eventHead.add(ExamCols.get(3).text());
				eventCredits.add(ExamCols.get(4).text());
				eventLink.add(ExamCols.get(2).select("a").attr("href"));
				Log.i(LOG_TAG, "Link"
						+ ExamCols.get(2).select("a").attr("href"));
			}
		}
		if (ListAdapter != null) {
			ListAdapter.clear();
		}
		ListAdapter = new ThreeLinesAdapter(context, eventName, eventCredits,
				eventHead);
		return ListAdapter;
	}

	/**
	 * Gibt einen ListAdapter zurück der durch die Anmeldungsdaten anzeigt
	 * 
	 * @return ListAdapter mit anmeldungsdaten
	 * @author Daniel Thiem
	 */
	private ListAdapter getApplicationAdapter() {
		Element content = doc.select("div#contentSpacer_IE").first();
		if (content != null) {
			// Informationen über tiefere Kategorien
			ListAdapter deepLinkAdapter = getApplicationDeepLinkAdapter(content);
			// Informationen über einzelne Events
			ListAdapter singleItemAdapter = getApplicationSingleItems(content);
			// Adapter ggf. zusammenfügen
			if (deepLinkAdapter != null && singleItemAdapter != null) {
				return new MergedAdapter(deepLinkAdapter, singleItemAdapter);
			} else if (deepLinkAdapter != null && singleItemAdapter == null) {
				return deepLinkAdapter;
			} else if (deepLinkAdapter == null && singleItemAdapter != null) {
				return singleItemAdapter;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * Gibt einzelne Events in einem ListAdapter zurück.
	 * 
	 * @param content
	 *            Content div Element
	 * @return ListAdapter
	 * @author Daniel Thiem
	 */
	private ListAdapter getApplicationSingleItems(Element content) {
		final Element coursestatusTable = content
				.select("table.tbcoursestatus").first();
		if (coursestatusTable != null) {
			Elements moduleTable = coursestatusTable.select("tr");
			ListAdapter singleEventAdapter = null;
			if (moduleTable.size() > 0) {
				// Einzelne Veranstaltungen werden angeboten
				ArrayList<String> itemName = new ArrayList<String>();
				ArrayList<String> itemInstructor = new ArrayList<String>();
				ArrayList<String> itemDate = new ArrayList<String>();
				ArrayList<Boolean> isModule = new ArrayList<Boolean>();
				for (Element next : moduleTable) {
					final Elements cols = next.select("td");
					Element firstCol = cols.first();
					if (firstCol != null && cols.size() == 4) {
						final Element secondCol = cols.get(1);
						List<Node> innerChilds = secondCol.childNodes();

						if (firstCol.hasClass("tbsubhead")) {
							// Es handelt sich um ein Modul

							if (innerChilds.size() == 4) {

								final Node instructorNode = innerChilds.get(3);
								if (instructorNode instanceof TextNode) {

									String moduleInstructor = ((TextNode) instructorNode)
											.text();
									String moduleName = secondCol.select(
											"span.eventTitle").text();
									String moduleDeadline = cols.get(2).text();
									itemName.add(moduleName);
									itemInstructor.add(moduleInstructor);
									itemDate.add(moduleDeadline);
									isModule.add(true);
								}

							}

						} else if (firstCol.hasClass("tbdata")) {
							// Es handelt sich um ein Event
							String eventName = null, eventInstructor = null, eventDates = null;
							if (innerChilds.size() == 1) {
								// Event nur mit Namen
								final String evNmHtml = secondCol.html();
								eventName = TucanMobile
										.getEventNameByString(evNmHtml);
								eventInstructor = "";
								eventDates = "";

							} else if (innerChilds.size() == 7) {
								// Event mit Vollinformationen
								final Node instructorNode = innerChilds.get(4);
								final Node dateNode = innerChilds.get(6);
								if (instructorNode instanceof TextNode
										&& dateNode instanceof TextNode) {
									eventName = secondCol.select(
											"span.eventTitle").text();
									eventInstructor = ((TextNode) instructorNode)
											.text().trim();
									eventDates = ((TextNode) dateNode).text()
											.trim();
								}
							} else if (innerChilds.size() == 5) {
								// Event ohne Datum
								final Node instructorNode = innerChilds.get(4);
								if (instructorNode instanceof TextNode) {
									eventName = secondCol.select(
											"span.eventTitle").text();
									eventInstructor = ((TextNode) instructorNode)
											.text().trim();
									eventDates = "";
								}
							}
							itemName.add(eventName);
							itemInstructor.add(eventInstructor);
							itemDate.add(eventDates);
							isModule.add(false);

						}
					}
				}
				// Adapter zum zurückgeben erstellen
				singleEventAdapter = new HighlightedThreeLinesAdapter(context,
						itemName, itemInstructor, itemDate, isModule);
			}

			return singleEventAdapter;
		}
		return null;
	}

	/**
	 * Gibt einen Adapter zurück welcher die Kategorien enthalten um tiefer zu
	 * gehen. Speichert ausserdem den Link zu diesen Kategorien in applyLink ab
	 * 
	 * @param content
	 *            Das Seitenelement, welches diese informationen enthält
	 * @return Adapter mit Kategorien
	 * @author Daniel Thiem
	 * @since 2012-11-26
	 */
	private ListAdapter getApplicationDeepLinkAdapter(Element content) {
		final Elements deepLinkListElement = content
				.select("div#contentSpacer_IE > ul");
		if (deepLinkListElement.size() > 0) {
			Elements deepLinkList = deepLinkListElement.first().select("li");
			if (deepLinkList.size() > 0) {
				// Tiefergehende Links verfügbar
				applyLink = new ArrayList<String>();
				ArrayList<String> applyName = new ArrayList<String>();
				for (Element next : deepLinkList) {
					// Listenelemente durchgehen
					final Elements linkElement = next.select("a");
					if (linkElement.size() == 1) {
						applyLink.add(linkElement.attr("href"));
						applyName.add(next.text());
					}
				}
				if (TucanMobile.TESTING) {
					System.out.println(applyName.toString());
				}
				ListAdapter deepListAdapter = new ArrayAdapter<String>(context,
						android.R.layout.simple_list_item_1, applyName);
				return deepListAdapter;
			}
		}
		return null;
	}

	/**
	 * Gibt einen Adapter zurück, welcher auf der Hauptseite angezeigt wird.
	 * Ausserdem speichert er die Links für die Einzelnen Seiten in den
	 * eventLinks ab
	 * 
	 * @return ListAdapter mit der MenüNavigation
	 * @author Daniel Thiem
	 * 
	 */
	private ListAdapter getMenuAdapter() {
		Elements links = doc.select("li#link000273").select("li");
		ArrayList<String> linkids = new ArrayList<String>();
		linkids.add("link000275");
		linkids.add("link000274");
		if (TucanMobile.DEBUG) {
			linkids.add("link000311");
		}
		Iterator<Element> linkIt = links.iterator();
		eventLinks = new ArrayList<String>();
		eventNames = new ArrayList<String>();
		while (linkIt.hasNext()) {
			Element next = linkIt.next();
			String id = next.id();
			// Links Veranstaltugen und Module finden und aufnehmen
			if (linkids.contains(id)) {
				eventLinks.add(next.select("a").attr("href"));
				eventNames.add(next.select("a").text());
			}

		}
		// ArrayList kopieren um Links nicht neu laden zu müssen
		@SuppressWarnings("unchecked")
		ArrayList<String> eventNameBuffer = (ArrayList<String>) eventNames
				.clone();
		// Adapter erstellen und einsetzen
		ListAdapter = new ArrayAdapter<String>(context, R.layout.menu_row,
				R.id.main_menu_row_textField, eventNameBuffer);
		return ListAdapter;
	}
}
