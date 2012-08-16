package com.dalthed.tucan.scraper;

import java.util.ArrayList;
import java.util.Iterator;

import org.jsoup.Jsoup;
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
import com.dalthed.tucan.adapters.ThreeLinesAdapter;
import com.dalthed.tucan.adapters.ThreeLinesTableAdapter;
import com.dalthed.tucan.exceptions.LostSessionException;

import com.dalthed.tucan.ui.SimpleWebListActivity;
import com.dalthed.tucan.ui.Exams.OnItemSelectedListener;

public class ExamsScraper extends BasicScraper {

	public ArrayList<String> examLinks;
	public ArrayList<String> examNames;
	private ArrayList<String> examNameBuffer;
	private ArrayAdapter<String> ListAdapter;
	private ArrayList<String> SemesterOptionName;
	public ArrayList<String> SemesterOptionValue;
	public int SemesterOptionSelected;

	public ExamsScraper(Context context, AnswerObject result) {
		super(context, result);
	}

	@Override
	public ListAdapter scrapeAdapter(int mode) throws LostSessionException {
		if (doc.select("span.notLoggedText") == null
				|| doc.select("span.notLoggedText").text().length() > 0) {
			// Logged out..
			throw new LostSessionException();
		}
		SimpleWebListActivity.sendHTMLatBug(doc.html());
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

	/**
	 * @return
	 */
	private ListAdapter getAccomplishment() {
		ArrayList<String> ResultName = new ArrayList<String>();
		ArrayList<String> ResultGrade = new ArrayList<String>();
		ArrayList<String> ResultCredits = new ArrayList<String>();
		ArrayList<String> ResultCountedCredits = new ArrayList<String>();
		Element ModuleOverviewTable = doc.select("div.tb").first();
		Element examResultTable = ModuleOverviewTable.select("tbody").first();
		if (examResultTable != null) {
			Iterator<Element> ExamResultRowIterator = examResultTable.select("tr").iterator();
			while (ExamResultRowIterator.hasNext()) {
				Element next = ExamResultRowIterator.next();
				Elements ExamResultCols = next.select("td");
				if (ExamResultCols.size() > 4) {
					ResultName.add(ExamResultCols.get(1).text());
					ResultCountedCredits.add(ExamResultCols.get(3).text());
					ResultCredits.add(ExamResultCols.get(4).text());
					ResultGrade.add(ExamResultCols.get(5).text());
				}

			}
			ListAdapter = new ThreeLinesTableAdapter(context, ResultName, ResultGrade,
					ResultCredits, ResultCountedCredits);
		} else {
			ListAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1,
					new String[] { "Keine Informationen gefunden" });
		}

		return ListAdapter;

	}

	public SpinnerAdapter spinnerAdapter() {
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
		ArrayAdapter<String> SpinnerAdapter = new ArrayAdapter<String>(context,
				android.R.layout.simple_spinner_item, SemesterOptionName);
		return SpinnerAdapter;
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
