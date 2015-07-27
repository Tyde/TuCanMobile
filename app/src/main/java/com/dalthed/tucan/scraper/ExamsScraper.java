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
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.SpinnerAdapter;

import com.dalthed.tucan.R;
import com.dalthed.tucan.Connection.AnswerObject;
import com.dalthed.tucan.adapters.ThreeLinesAdapter;
import com.dalthed.tucan.adapters.ThreeLinesTableAdapter;
import com.dalthed.tucan.exceptions.LostSessionException;
import com.dalthed.tucan.exceptions.TucanDownException;

public class ExamsScraper extends BasicScraper {

	public ArrayList<String> examLinks;
	public ArrayList<String> examNames;
	private ArrayList<String> examNameBuffer;
	private ArrayAdapter<String> ListAdapter;
	private ArrayList<String> SemesterOptionName;
	public ArrayList<String> SemesterOptionValue;
	public int SemesterOptionSelected;
	private ArrayAdapter<String> spinnerAdapter;

	public ExamsScraper(Context context, AnswerObject result) {

		super(context, result);

	}

	@Override
	public ListAdapter scrapeAdapter(int mode) throws LostSessionException, TucanDownException {
		if (checkForLostSeesion()) {
			if (ListAdapter != null) {
				ListAdapter.clear();
			}
			if (mode == 0) {
				return getMenuAdapter();
			} else if (mode == 10) {
				return getExamOverview();
			} else if (mode == 1) {
				return getModuleOverview();
			} else if (mode == 2) {
				return getExamResults();
			} else if (mode == 3) {
				return getAccomplishment();
			} else {
				return null;
			}
		}
		return null;

	}

	/**
	 * @return
	 */
	private ListAdapter getAccomplishment() {
		ArrayList<String> ResultName = new ArrayList<String>();
		ArrayList<String> ResultGrade = new ArrayList<String>();
		ArrayList<String> ResultCredits = new ArrayList<String>();
		ArrayList<String> ResultCountedCredits = new ArrayList<String>();
		ArrayList<Integer> resultColor = new ArrayList<Integer>();
		Element ModuleOverviewTable = doc.select("div.tb").first();
		final Elements examTbodies = ModuleOverviewTable.select("tbody");
		Element examResultTable = examTbodies.first();
		if (examResultTable != null) {
			Iterator<Element> ExamResultRowIterator = examResultTable.select("tr").iterator();
			while (ExamResultRowIterator.hasNext()) {
				Element next = ExamResultRowIterator.next();
				scrapeAccomplishmentRow(ResultName, ResultGrade, ResultCredits,
						ResultCountedCredits, resultColor, next);

			}
			if (examTbodies.size() > 1) {
				Element gpaTable = examTbodies.get(1);
				Iterator<Element> gpaTableIterator = gpaTable.select("tr").iterator();
				while(gpaTableIterator.hasNext()){
					Element next = gpaTableIterator.next();
					final Elements tableHead = next.select("th");
					if(tableHead.size()==2){
						ResultName.add(tableHead.get(0).text());
						ResultGrade.add(tableHead.get(1).text());
						resultColor.add(-1);
						ResultCountedCredits.add("");
						ResultCredits.add("");
						
					}
				}
			}
			ListAdapter = new ThreeLinesTableAdapter(context, ResultName, ResultGrade,
					ResultCredits, ResultCountedCredits, resultColor);
		} else {
			ListAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1,
					new String[] { "Keine Informationen gefunden" });
		}

		return ListAdapter;

	}

	/**
	 * @param ResultName
	 * @param ResultGrade
	 * @param ResultCredits
	 * @param ResultCountedCredits
	 * @param resultColor
	 * @param next
	 */
	private void scrapeAccomplishmentRow(ArrayList<String> ResultName,
			ArrayList<String> ResultGrade, ArrayList<String> ResultCredits,
			ArrayList<String> ResultCountedCredits, ArrayList<Integer> resultColor, Element next) {
		Elements examResultCols = next.select("td");
		if (next.hasClass("level00")) {
			// MainHead
			if (examResultCols.size() > 0) {
				resultColor.add(context.getResources().getColor(R.color.level00));
				ResultName.add(examResultCols.get(0).text());
				ResultCountedCredits.add("");
				ResultCredits.add("");
				ResultGrade.add("");
			}

		} else if (next.hasClass("level01")) {
			// SubHead
			if (examResultCols.size() > 0) {
				resultColor.add(context.getResources().getColor(R.color.level01));
				ResultName.add(examResultCols.get(0).text());
				ResultCountedCredits.add("");
				ResultCredits.add("");
				ResultGrade.add("");
			}

		} else if (next.hasClass("level02")) {

			// SubSubHead
			if (examResultCols.size() > 0) {
				resultColor.add(context.getResources().getColor(R.color.level02));
				ResultName.add(examResultCols.get(0).text());
				ResultCountedCredits.add("");
				ResultCredits.add("");
				ResultGrade.add("");
			}
		} else if (next.hasClass("level03")) {
			// SubSubSubHead
			if (examResultCols.size() > 0) {

				resultColor.add(context.getResources().getColor(R.color.level03));
				ResultName.add(examResultCols.get(0).text());
				ResultCountedCredits.add("");
				ResultCredits.add("");
				ResultGrade.add("");
			}

		} else if (next.hasClass("level04")) {
			// SubSubSubSubHead
			if (examResultCols.size() > 0) {
				resultColor.add(context.getResources().getColor(R.color.level04));
				ResultName.add(examResultCols.get(0).text());
				ResultCountedCredits.add("");
				ResultCredits.add("");
				ResultGrade.add("");
			}
		} else if (examResultCols.size() > 4) {
			if (examResultCols.get(0).hasClass("level04")
					|| examResultCols.get(0).hasClass("level03")
					|| examResultCols.get(0).hasClass("level02")
					|| examResultCols.get(0).hasClass("level01")) {
				Element firstColumn = examResultCols.get(0);
				int rowColor = getRowColor(firstColumn);
				resultColor.add(rowColor);
				ResultName.add(examResultCols.get(0).text());
				ResultCredits.add(examResultCols.get(2).text());
				ResultCountedCredits.add(examResultCols.get(3).text());
				ResultGrade.add("");

			} else {
				resultColor.add(-1);
				ResultName.add(examResultCols.get(1).text());
				ResultCountedCredits.add(examResultCols.get(3).text());
				ResultCredits.add(examResultCols.get(4).text());
				ResultGrade.add(examResultCols.get(5).text());
			}
		} else if (examResultCols.size() == 1) {
			Element firstColumn = examResultCols.get(0);
			int rowColor = getRowColor(firstColumn);
			resultColor.add(rowColor);
			ResultName.add(firstColumn.text());
			ResultCountedCredits.add("");
			ResultGrade.add("");
			ResultCredits.add("");

		}
	}

	/**
	 * @param firstColumn
	 * @return
	 */
	private int getRowColor(Element firstColumn) {
		int rowColor = -1;
		if (firstColumn.hasClass("level04")) {
			rowColor = (context.getResources().getColor(R.color.level04));
		} else if (firstColumn.hasClass("level03")) {
			rowColor = (context.getResources().getColor(R.color.level03));
		} else if (firstColumn.hasClass("level02")) {
			rowColor = (context.getResources().getColor(R.color.level02));
		} else if (firstColumn.hasClass("level01")) {
			rowColor = (context.getResources().getColor(R.color.level01));
		}
		return rowColor;
	}

	public SpinnerAdapter spinnerAdapter() {
		if (spinnerAdapter == null) {
			SemesterOptionName = new ArrayList<String>();
			SemesterOptionValue = new ArrayList<String>();
			Iterator<Element> SemesterOptionIterator = doc.select("option").iterator();
			int i = 0;
			SemesterOptionSelected = 0;
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
			spinnerAdapter = new ArrayAdapter<String>(context,
					android.R.layout.simple_spinner_item, SemesterOptionName);

		}
		return spinnerAdapter;

	}

	/**
	 * @return
	 */
	private ListAdapter getExamResults() {
		ArrayList<String> ResultName = new ArrayList<String>();
		ArrayList<String> ResultGrade = new ArrayList<String>();
		ArrayList<String> ResultDate = new ArrayList<String>();
		Iterator<Element> ExamResultRowIterator = doc.select("tr.tbdata").iterator();
		while (ExamResultRowIterator.hasNext()) {
			Element next = ExamResultRowIterator.next();
			Elements ExamResultCols = next.select("td");
			Log.i(LOG_TAG, "Größe Cols:" + ExamResultCols.size());
			ResultName.add(Jsoup.parse(ExamResultCols.get(0).html().split("<br />")[0]).text());
			ResultDate.add(ExamResultCols.get(1).text());
			ResultGrade.add(ExamResultCols.get(2).text() + "  " + ExamResultCols.get(3).text());
		}

		ListAdapter = new ThreeLinesAdapter(context, ResultName, ResultGrade, ResultDate);
		return ListAdapter;
	}

	/**
	 * @return
	 */
	private ListAdapter getModuleOverview() {
		ArrayList<String> ResultName = new ArrayList<String>();
		ArrayList<String> ResultGrade = new ArrayList<String>();
		ArrayList<String> ResultCredits = new ArrayList<String>();
		Element ModuleOverviewTable = doc.select("div.tb").first();
		Iterator<Element> ExamResultRowIterator = ModuleOverviewTable.select("tbody").first()
				.select("tr").iterator();
		while (ExamResultRowIterator.hasNext()) {
			Element next = ExamResultRowIterator.next();
			Elements ExamResultCols = next.select("td");
			Log.i(LOG_TAG, "Größe Cols:" + ExamResultCols.size());
			if (ExamResultCols.size() > 1) {
				ResultName.add(ExamResultCols.get(1).text());
				ResultCredits.add(ExamResultCols.get(4).text());
				ResultGrade.add(ExamResultCols.get(2).text());
			}

		}

		ListAdapter = new ThreeLinesAdapter(context, ResultName, ResultGrade, ResultCredits);
		return ListAdapter;
	}

	/**
	 * @return
	 */
	private ListAdapter getExamOverview() {
		ArrayList<String> ExamName = new ArrayList<String>();
		ArrayList<String> ExamDate = new ArrayList<String>();
		ArrayList<String> ExamState = new ArrayList<String>();
		Element ModuleOverviewTable = doc.select("div.tb").first();
		Iterator<Element> ExamRowIterator = ModuleOverviewTable.select("tbody").first()
				.select("tr").iterator();
		while (ExamRowIterator.hasNext()) {
			Element next = ExamRowIterator.next();
			Elements ExamCols = next.select("td");
			if (ExamCols.size() > 1) {
				ExamName.add(ExamCols.get(1).text());
				ExamDate.add(ExamCols.get(3).text());
				ExamState.add(ExamCols.get(4).text());
			}
		}

		ListAdapter = new ThreeLinesAdapter(context, ExamName, ExamDate, ExamState);
		return ListAdapter;
	}

	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private ListAdapter getMenuAdapter() {
		Elements links = doc.select("li#link000280").select("li");

		Iterator<Element> linkIt = links.iterator();
		examLinks = new ArrayList<String>();
		examNames = new ArrayList<String>();
		while (linkIt.hasNext()) {
			Element next = linkIt.next();
			String id = next.id();

			if (id.equals("link000318") || id.equals("link000316")) {
				String link = next.select("a").attr("href");
				String name = next.select("a").text();
				examLinks.add(link);
				examNames.add(name);
			} else if (id.equals("link000323")) {
				Iterator<Element> subLinks = next.select("li.depth_3").iterator();
				while (subLinks.hasNext()) {
					Element subnext = subLinks.next();
					String link = subnext.select("a").attr("href");
					String name = subnext.select("a").text();
					examLinks.add(link);
					examNames.add(name);
				}
			}

			// Log.i(LOG_TAG,next.toString()+"Hakki");
		}
		String SessionArgument = lastCalledUrl.split("ARGUMENTS=")[1].split(",")[0];
		examLinks
				.add("https://www.tucan.tu-darmstadt.de/scripts/mgrqcgi?APPNAME=CampusNet&PRGNAME=EXAMREGISTRATION&ARGUMENTS="
						+ SessionArgument + ",-N000318,");
		examNames.add("Anmeldung zu Prüfungen");
		examNameBuffer = (ArrayList<String>) examNames.clone();
		ListAdapter = new ArrayAdapter<String>(context, R.layout.menu_row,
				R.id.main_menu_row_textField, examNameBuffer);
		return ListAdapter;
	}

}
