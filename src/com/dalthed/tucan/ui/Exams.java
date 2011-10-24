package com.dalthed.tucan.ui;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.dalthed.tucan.R;
import com.dalthed.tucan.TucanMobile;

import com.dalthed.tucan.Connection.AnswerObject;
import com.dalthed.tucan.Connection.CookieManager;
import com.dalthed.tucan.Connection.RequestObject;
import com.dalthed.tucan.Connection.SimpleSecureBrowser;

public class Exams extends SimpleWebListActivity {
	private String UserName;
	private CookieManager localCookieManager;
	private static final String LOG_TAG = "TuCanMobile";
	private int mode = 0;
	private ArrayList<String> examLinks;
	private ArrayList<String> examNames;
	private ArrayAdapter<String> ListAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.exams);

		// Webhandling Start
		String CookieHTTPString = getIntent().getExtras().getString("Cookie");
		String URLStringtoCall = getIntent().getExtras().getString("URL");
		UserName = getIntent().getExtras().getString("UserName");
		URL URLtoCall;

		try {
			URLtoCall = new URL(URLStringtoCall);
			localCookieManager = new CookieManager();
			localCookieManager.generateManagerfromHTTPString(
					URLtoCall.getHost(), CookieHTTPString);
			SimpleSecureBrowser callOverviewBrowser = new SimpleSecureBrowser(
					this);
			RequestObject thisRequest = new RequestObject(URLStringtoCall,
					localCookieManager, RequestObject.METHOD_GET, "");

			callOverviewBrowser.execute(thisRequest);
		} catch (MalformedURLException e) {
			Log.e(LOG_TAG, e.getMessage());
		}
		// Webhandling End
	}
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		switch (position) {
		case 2:
			mode=2;
			SimpleSecureBrowser callOverviewBrowser = new SimpleSecureBrowser(this);
			Log.i(LOG_TAG,"URL: "+TucanMobile.TUCAN_PROT+TucanMobile.TUCAN_HOST+examLinks.get(2));
			RequestObject thisRequest = new RequestObject(TucanMobile.TUCAN_PROT+TucanMobile.TUCAN_HOST+examLinks.get(2),
					localCookieManager, RequestObject.METHOD_GET, "");
			callOverviewBrowser.execute(thisRequest);
			break;
		default:
			break;
		}
		
	}
	
	
	class ModuleAdapter extends ArrayAdapter<String>{

		ArrayList<String> resultGrade,resultDate;
		public ModuleAdapter(ArrayList<String> resultName,ArrayList<String> resultGrade,ArrayList<String> resultDate) {
			super(Exams.this,R.layout.row_vv_events,R.id.row_vv_veranst,resultName);
			this.resultDate=resultDate;
			this.resultGrade=resultGrade;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = super.getView(position, convertView, parent);
			TextView TypeTextView = (TextView) row.findViewById(R.id.row_vv_type);
			TextView DozentTextView = (TextView) row.findViewById(R.id.row_vv_dozent);

			TypeTextView.setText(resultGrade.get(position));
			DozentTextView.setText(resultDate.get(position));
			
			return row;
		}
		
	}

	public void onPostExecute(AnswerObject result) {
		Document doc = Jsoup.parse(result.getHTML());
		if(mode==0){
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
					Iterator<Element> subLinks = next.select("li.depth_3")
							.iterator();
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
			ListAdapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, examNames);
			setListAdapter(ListAdapter);
		}
		else if(mode==2){
			ArrayList<String> ResultName = new ArrayList<String>();
			ArrayList<String> ResultGrade = new ArrayList<String>();
			ArrayList<String> ResultDate = new ArrayList<String>();
			
			Iterator<Element> ExamResultRowIterator = doc.select("tr.tbdata").iterator();
			while(ExamResultRowIterator.hasNext()){
				Element next = ExamResultRowIterator.next();
				Elements ExamResultCols = next.select("td");
				Log.i(LOG_TAG,"Größe Cols:"+ExamResultCols.size());
				ResultName.add(Jsoup.parse(ExamResultCols.get(0).html().split("<br />")[0]).text());
				ResultDate.add(ExamResultCols.get(1).text());
				ResultGrade.add(ExamResultCols.get(2).text() + "  "+ ExamResultCols.get(3).text());
			}
			ArrayList<String> SemesterOptionName = new ArrayList<String>();
			ArrayList<String> SemesterOptionValue = new ArrayList<String>();
			Iterator<Element> SemesterOptionIterator = doc.select("option").iterator();
			while(SemesterOptionIterator.hasNext()){
				Element next = SemesterOptionIterator.next();
				SemesterOptionName.add(next.text());
				SemesterOptionValue.add(next.attr("value"));
			}
			ArrayAdapter<String> SpinnerAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, SemesterOptionName);
			
			
			Spinner semesterSpinner = (Spinner) findViewById(R.id.exam_semester_spinner);
			semesterSpinner.setVisibility(View.VISIBLE);
			semesterSpinner.setAdapter(SpinnerAdapter);
			semesterSpinner.setSelection(SpinnerAdapter.getCount()-1);
			ListAdapter.clear();
			ListAdapter = new ModuleAdapter(ResultName, ResultGrade, ResultDate);
			setListAdapter(ListAdapter);
			
		}
		
	}

}
