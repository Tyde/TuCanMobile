package com.dalthed.tucan.scraper;

import java.util.ArrayList;
import java.util.Iterator;

import org.acra.ACRA;
import org.acra.ErrorReporter;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.dalthed.tucan.TucanMobile;
import com.dalthed.tucan.Connection.AnswerObject;
import com.dalthed.tucan.Connection.BrowserAnswerReciever;
import com.dalthed.tucan.Connection.CookieManager;
import com.dalthed.tucan.Connection.RequestObject;
import com.dalthed.tucan.Connection.SimpleSecureBrowser;
import com.dalthed.tucan.adapters.RegisterExamAdapter;
import com.dalthed.tucan.exceptions.LostSessionException;
import com.dalthed.tucan.exceptions.TucanDownException;

public class RegisterExamsScraper extends BasicScraper implements OnClickListener {

	private ArrayList<Boolean> eventisModule;
	private ArrayList<String> eventName;
	private ArrayList<String> examDate;
	public ArrayList<String> registerLink;
	public ArrayList<Integer> examSelection;
	private boolean justGetimportant;
	private String postString;
	private SimpleSecureBrowser callResultBrowser;
	private String URLStringtoCall;
	private CookieManager localCookieManager;

	/**
	 * Creates a RegisterExamsScraper
	 * 
	 * @param context
	 *            Activity Context
	 * @param result
	 *            ResultObject from the call
	 * @param URLStringtoCall
	 * @param lcm
	 *            Cookiemanager
	 */
	public RegisterExamsScraper(Context context, AnswerObject result, String URLStringtoCall,
			CookieManager lcm) {
		super(context, result);
		this.URLStringtoCall = URLStringtoCall;
		this.localCookieManager = lcm;
	}

	@Override
	public ListAdapter scrapeAdapter(int mode) throws LostSessionException, TucanDownException {

		if (checkForLostSeesion() && mode == 0) {

			return getExamsOverview();

		}
		return null;
	}

	/**
	 * Erstellt einen RegisterDialog und zeigt ihn an, gibt den nachfolgenden
	 * Modus an
	 * 
	 * @return nachfolgender Modus
	 * @throws LostSessionException
	 * @since 2012-06-01
	 */
	public int getRegisterdialog() throws LostSessionException, TucanDownException {
		if (checkForLostSeesion()) {

			Element form = doc.select("form[name=registrationdetailsform]").first();
			if (form != null) {
				final Element contenttable = form.select("table.tb750").first();
				final Element contentrow = contenttable.select("tr").last();
				Elements cols = contentrow.select("td");
				Iterator<Element> iterateForms = form.select("input").iterator();
				ArrayList<String> formName = new ArrayList<String>();
				ArrayList<String> formValue = new ArrayList<String>();
				postString = "";
				int ct = 0;
				while (iterateForms.hasNext()) {
					Element next = iterateForms.next();
					formName.add(next.attr("name"));
					formValue.add(next.attr("value"));
					if (ct > 0) {
						postString += "&";
					}
					ct++;
					postString += next.attr("name") + "=" + next.attr("value");
				}
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setMessage(
						"An/Abmelden zu:" + cols.get(1).text() + "\n" + cols.get(6).text())
						.setCancelable(true).setPositiveButton("Ja", this)
						.setNegativeButton("Nein", null);
				AlertDialog alert = builder.create();
				alert.show();
				return 3; // Return Mode
			} else {
				Element errorSpan = doc.select("span.error").first();
				if(errorSpan!=null){
					AlertDialog.Builder builder = new AlertDialog.Builder(context);
					builder.setMessage("Fehler: "+errorSpan.text());
					AlertDialog alert = builder.create();
					alert.show();
					return 0;
				}
			}
		}
		return -1;

	}

	/**
	 * Führt die Anmeldung aus und gibt den nächsten modus zurück
	 * 
	 * @return der nächste modus
	 * @throws LostSessionException
	 * @since 2012-06-01
	 */
	public int startRegistration() throws LostSessionException, TucanDownException {
		if (checkForLostSeesion()) {

			Element form = doc.select("form[name=registrationdetailsform]").first();
			String resultText = "";
			if (form != null) {
				resultText = form.select("span.note").first().text();
			} else {
				try {
					resultText = doc.select("p.remarks").first().select("span.error").text();
				} catch (NullPointerException e) {
					ACRA.getErrorReporter().handleSilentException(e);
				}
			}
			Toast.makeText(context, resultText, Toast.LENGTH_LONG).show();
			if (context instanceof BrowserAnswerReciever) {
				callResultBrowser = new SimpleSecureBrowser((BrowserAnswerReciever) context);
				RequestObject thisRequest = new RequestObject(URLStringtoCall, localCookieManager,
						RequestObject.METHOD_GET, "");

				callResultBrowser.execute(thisRequest);
			}
			return 0;
		}
		return -1;
	}

	public ListAdapter getExamsOverview() {
		Element significantTable = doc.select("table.nb").select("tbody").first();

		Iterator<Element> rows = significantTable.select("tr").iterator();
		eventisModule = new ArrayList<Boolean>();
		eventName = new ArrayList<String>();
		examDate = new ArrayList<String>();
		registerLink = new ArrayList<String>();
		examSelection = new ArrayList<Integer>();
		while (rows.hasNext()) {
			try {

				Element next = rows.next();

				if (next.hasClass("level02")) {
					eventisModule.add(true);
					eventName.add(next.select("td").get(1).text());
					examDate.add("");
					examSelection.add(-1);
					registerLink.add("");
				} else if (next.hasClass("level03")) {
					// NEU
					eventisModule.add(true);
					eventName.add(next.select("td").get(0).text());
					examDate.add("");
					examSelection.add(-1);
					registerLink.add("");
				}

				else {
					eventisModule.add(false);
					Elements cols = next.select("td");
					eventName.add(cols.get(2).text());
					examDate.add(cols.get(3).text());
					// Wenn keine Anmeldung/Abmeldung möglich ist
					if (cols.get(4).select("a").isEmpty()) {
						if (cols.get(4).text().equals("Ausgewählt"))
							examSelection.add(1);
						else
							examSelection.add(0);

						registerLink.add("");
					} else {
						// Anmeldung/Abmeldung möglich
						if (cols.get(4).select("a").text().equals("Anmelden"))
							examSelection.add(2);
						else
							examSelection.add(3);
						registerLink.add(cols.get(4).select("a").attr("href"));
					}

				}

			}

			catch (IndexOutOfBoundsException e) {
				Log.e(LOG_TAG, "Index out of Bounds");
				ACRA.getErrorReporter().handleSilentException(e);
			}
			// System.out.println();
		}
		if (justGetimportant) {
			// TODO: Only get a selection

		}
		RegisterExamAdapter nextAdapter = new RegisterExamAdapter(context, eventisModule,
				eventName, examDate, examSelection);
		return nextAdapter;
	}

	public void onClick(DialogInterface dialog, int which) {
		if (context instanceof BrowserAnswerReciever) {
			SimpleSecureBrowser finalizeRegister = new SimpleSecureBrowser(
					(BrowserAnswerReciever) context);
			RequestObject callstatuschange = new RequestObject(TucanMobile.TUCAN_PROT
					+ TucanMobile.TUCAN_HOST + "/scripts/mgrqcgi", localCookieManager,
					RequestObject.METHOD_POST, postString);
			finalizeRegister.execute(callstatuschange);
		}
	}

}
