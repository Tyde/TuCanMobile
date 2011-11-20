package com.dalthed.tucan.ui;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.dalthed.tucan.R;
import com.dalthed.tucan.TuCanMobileActivity;
import com.dalthed.tucan.TucanMobile;
import com.dalthed.tucan.Connection.AnswerObject;
import com.dalthed.tucan.Connection.CookieManager;
import com.dalthed.tucan.Connection.RequestObject;
import com.dalthed.tucan.Connection.SimpleSecureBrowser;
import com.viewpagerindicator.TitlePageIndicator;
import com.viewpagerindicator.TitleProvider;

public class FragmentSingleEvent extends FragmentWebActivity {
	static final int NUM_ITEMS = 3;
	PagerAdapter mPageAdapter;
	ViewPager mPager;
	private CookieManager localCookieManager;
	private static final String LOG_TAG = "TuCanMobile";
	private String URLStringtoCall;
	private Boolean PREPCall;
	protected String[] mTitles;
	ArrayList<String> materialLink;

	int mode = 0;
	boolean thereAreFiles = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_singleevent);

		// Wichtige Infos aus dem Intent holen
		String CookieHTTPString = getIntent().getExtras().getString("Cookie");
		URLStringtoCall = getIntent().getExtras().getString("URL");
		PREPCall = getIntent().getExtras().getBoolean("PREPLink");
		URL URLtoCall;

		// FragmentTitel laden
		mTitles = getResources().getStringArray(R.array.singleevent_options);

		// PagerAdapter an ViewPager anbinden
		mPageAdapter = new PagerAdapter(getSupportFragmentManager(), mTitles);
		mPager = (ViewPager) findViewById(R.id.multipager);
		mPager.setAdapter(mPageAdapter);

		// TitlePageIndicator befüllen
		TitlePageIndicator titleIndicator = (TitlePageIndicator) findViewById(R.id.titles);
		titleIndicator.setViewPager(mPager);
		titleIndicator.setTextSize(20);
		titleIndicator.setTextColor(0x33333333);
		titleIndicator.setSelectedColor(0xFF000000);
		titleIndicator.setSelectedBold(false);

		try {
			// Seite aufrufen..
			URLtoCall = new URL(URLStringtoCall);
			localCookieManager = new CookieManager();
			localCookieManager.generateManagerfromHTTPString(
					URLtoCall.getHost(), CookieHTTPString);
			callResultBrowser = new SimpleSecureBrowser(this);
			RequestObject thisRequest = new RequestObject(URLStringtoCall,
					localCookieManager, RequestObject.METHOD_GET, "");

			callResultBrowser.execute(thisRequest);
		} catch (MalformedURLException e) {
			Log.e(LOG_TAG, e.getMessage());
		}
	}

	/**
	 * PagerAdapter für PageViewer
	 * 
	 * @author Tyde
	 * 
	 */
	public static class PagerAdapter extends FragmentPagerAdapter implements
			TitleProvider {
		private String[] mtitles;
		private ArrayList<ArrayAdapter<String>> adapterList;
		FragmentManager fm;
		public OnItemClickListener clicklistener;
		public ArrayList<String> fileList;

		public PagerAdapter(FragmentManager fm, String[] titles) {
			super(fm);
			this.fm = fm;
			this.mtitles = titles;
			this.adapterList = new ArrayList<ArrayAdapter<String>>();
		}

		@Override
		public ListFragment getItem(int position) {
			ArrayListFragment newFragment = ArrayListFragment
					.newInstance(position);

			return (ListFragment) newFragment;
		}
		/**
		 * Initialisiertes ListFragment abrufen
		 * @param position x-Position des Fragments
		 * @return ListFragment der position
		 */
		public ArrayListFragment getInitializedItem(int position) {
			ArrayListFragment fragment = (ArrayListFragment) fm
					.findFragmentByTag("android:switcher:" + R.id.multipager
							+ ":" + position);
			return (ArrayListFragment) fragment;

		}
		
		/**
		 * Speichert den entsprechenden ArrayAdapter in einer Liste des PagerAdapters
		 * @param adapter der zu speichernde Adapter
		 */
		public void setAdapter(ArrayAdapter<String> adapter) {
			adapterList.add(adapter);
		}

		@Override
		public void finishUpdate(View container) {
			super.finishUpdate(container);
			/*
			 * Liste wird eingebaut, wenn geupdated wird und die Liste noch leer
			 * ist
			 */
			for (int ii = 0; ii <= 2; ii++) {
				if (getInitializedItem(ii) != null && adapterList.size() > ii) {
					if (getInitializedItem(ii).getListAdapter() == null)
						getInitializedItem(ii).setListAdapter(
								adapterList.get(ii));
					if (ii == 2 && fileList != null) {
						getInitializedItem(ii).setFilelinks(fileList);
					}
				}

			}

		}

		@Override
		public int getCount() {
			return NUM_ITEMS;
		}

		public String getTitle(int position) {

			return mtitles[position];
		}
	}

	public static class ArrayListFragment extends ListFragment {
		int mNum;
		private boolean thereAreFiles;
		private ArrayList<String> materialLink;

		static ArrayListFragment newInstance(int num) {
			ArrayListFragment f = new ArrayListFragment();

			Bundle args = new Bundle();
			args.putInt("num", num);

			f.setArguments(args);
			return f;
		}

		public int myid;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View v = inflater.inflate(R.layout.fragment_pager_list, container,
					false);
			myid = this.getId();
			return v;

		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			mNum = getArguments() != null ? getArguments().getInt("num") : 1;
		}

		public void setFilelinks(ArrayList<String> fileList) {
			thereAreFiles = true;
			materialLink = fileList;
		}

		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			if (thereAreFiles) {
				if (!materialLink.get(position).equals("")) {
					String url = TucanMobile.TUCAN_PROT
							+ TucanMobile.TUCAN_HOST
							+ materialLink.get(position);

					Uri mUri = Uri.parse(url);
					Intent DownloadFile = new Intent(Intent.ACTION_VIEW, mUri);

					startActivity(DownloadFile);
				}

			}
		}

	}

	@Override
	public void onPostExecute(AnswerObject result) {

		Document doc = Jsoup.parse(result.getHTML());
		sendHTMLatBug(doc.html());
		if (doc.select("span.notLoggedText").text().length() > 0) {
			Intent BackToLoginIntent = new Intent(this,
					TuCanMobileActivity.class);
			BackToLoginIntent.putExtra("lostSession", true);
			startActivity(BackToLoginIntent);
		} else {
			if (PREPCall == false) {
				String Title = doc.select("h1").text();

				TextView SingleEventTitle = (TextView) findViewById(R.id.singleevent_title);
				SingleEventTitle.setText(Title);
				Elements Deltarows = doc.select("table[courseid]").first()
						.select("tr");
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

				mPageAdapter.setAdapter(new SingleEventAdapter(titles, values));

				// Termin-Selektor:
				// Terminselektor

				Iterator<Element> captionIt = doc.select("caption").iterator();
				Iterator<Element> DateTable = null;
				Iterator<Element> materialTable = null;
				while (captionIt.hasNext()) {
					Element next = captionIt.next();
					if (next.text().equals("Termine")) {

						DateTable = next.parent().select("tr").iterator();
					} else if (next.text().contains("Material")) {

						materialTable = next.parent().select("tr").iterator();
					}
				}
				ArrayList<String> eventNumber = new ArrayList<String>();
				ArrayList<String> eventDate = new ArrayList<String>();
				ArrayList<String> eventTime = new ArrayList<String>();

				ArrayList<String> eventRoom = new ArrayList<String>();
				ArrayList<String> eventInstructor = new ArrayList<String>();
				if(DateTable!=null){
					while (DateTable.hasNext()) {
						Element next = DateTable.next();
						Elements cols = next.select("td");
						if (cols.size() > 5) {
							eventNumber.add(cols.get(0).text());
							eventDate.add(cols.get(1).text());
							eventTime.add(cols.get(2).text() + "-"
									+ cols.get(3).text());
							eventRoom.add(cols.get(4).text());
							eventInstructor.add(cols.get(5).text());
						}
	
					}
					

				}
				else {
					eventDate.add("");
					eventTime.add("");
					eventNumber.add("");
					eventRoom.add("Keine Daten vorhanden");
					eventInstructor.add("");
				}
				mPageAdapter.setAdapter(new AppointmentAdapter(eventDate,
						eventTime, eventNumber, eventRoom, eventInstructor));
				
				int ct = 0;
				ArrayList<String> materialNumber = new ArrayList<String>();
				ArrayList<String> materialName = new ArrayList<String>();
				ArrayList<String> materialDesc = new ArrayList<String>();
				materialLink = new ArrayList<String>();
				ArrayList<String> materialFile = new ArrayList<String>();
				if (materialTable != null) {
					while (materialTable.hasNext()) {
						Element next = materialTable.next();

						if (next.select("td").size() > 1) {
							ct++;

							int mod = (ct % 3);
							switch (mod) {
							case 1:
								materialNumber.add(next.select("td").get(0)
										.text());
								materialName.add(next.select("td").get(1)
										.text());

								break;
							case 2:
								materialDesc.add(next.select("td").get(1)
										.text());
								break;
							case 0:
								if (next.attr("class").equals("tbdata_stretch")) {
									materialLink.add(next.select("td").get(1)
											.select("a").attr("href"));
									materialFile.add(next.select("td").get(1)
											.select("a").text());
								} else {
									materialLink.add("");
									materialFile.add("");
									materialNumber.add(next.select("td").get(0)
											.text());
									materialName.add(next.select("td").get(1)
											.text());
									ct++;
								}

								break;
							}
						}
					}
				}

				if (ct > 2) {
					mPageAdapter.setAdapter(new AppointmentAdapter(
							materialNumber, materialFile, null, materialName,
							materialDesc));
					thereAreFiles = true;

					mPageAdapter.fileList = materialLink;
				} else
					mPageAdapter.setAdapter(new ArrayAdapter<String>(this,
							android.R.layout.simple_list_item_1,
							new String[] { "Kein Material" }));

				mPageAdapter.finishUpdate(mPager);

			} else {
				String nextlink = TucanMobile.TUCAN_PROT
						+ TucanMobile.TUCAN_HOST
						+ doc.select("div.detailout").select("a").attr("href");
				SimpleSecureBrowser callOverviewBrowser = new SimpleSecureBrowser(
						this);
				RequestObject thisRequest = new RequestObject(nextlink,
						localCookieManager, RequestObject.METHOD_GET, "");
				PREPCall = false;
				callOverviewBrowser.execute(thisRequest);
			}
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

	class SingleEventAdapter extends ArrayAdapter<String> {
		ArrayList<String> values;

		public SingleEventAdapter(ArrayList<String> properties,
				ArrayList<String> values) {
			super(FragmentSingleEvent.this, R.layout.singleevent_row,
					R.id.singleevent_row_property, properties);
			this.values = values;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = super.getView(position, convertView, parent);
			TextView ValueTextView = (TextView) row
					.findViewById(R.id.singleevent_row_value);

			ValueTextView.setText(" " + this.values.get(position));

			return row;
		}

	}

	class AppointmentAdapter extends ArrayAdapter<String> {
		ArrayList<String> appointmentTime, appointmentNumber, appointmentRoom,
				appointmentInstructor;

		public AppointmentAdapter(ArrayList<String> appDate,
				ArrayList<String> appTime, ArrayList<String> appNumber,
				ArrayList<String> appRoom, ArrayList<String> appInstructor) {
			super(FragmentSingleEvent.this, R.layout.singleevent_row_date,
					R.id.singleevent_row_date_date, appDate);
			this.appointmentTime = appTime;
			this.appointmentInstructor = appInstructor;
			this.appointmentNumber = appNumber;
			this.appointmentRoom = appRoom;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = super.getView(position, convertView, parent);
			TextView AppTimeView = (TextView) row
					.findViewById(R.id.singleevent_row_date_time);
			TextView AppNumberView = (TextView) row
					.findViewById(R.id.singleevent_row_date_number);
			TextView AppRoomView = (TextView) row
					.findViewById(R.id.singleevent_row_date_room);
			TextView AppInstructorView = (TextView) row
					.findViewById(R.id.singleevent_row_date_instructor);

			AppTimeView.setText(this.appointmentTime.get(position));
			if (this.appointmentNumber != null)
				AppNumberView.setText(this.appointmentNumber.get(position));
			else
				AppNumberView.setText("");
			AppRoomView.setText(this.appointmentRoom.get(position));
			AppInstructorView.setText(this.appointmentInstructor.get(position));

			return row;
		}

	}

}
