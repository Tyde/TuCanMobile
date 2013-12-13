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

package com.dalthed.tucan.ui;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

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
import android.widget.ListAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.dalthed.tucan.R;
import com.dalthed.tucan.TuCanMobileActivity;
import com.dalthed.tucan.TucanMobile;
import com.dalthed.tucan.Connection.AnswerObject;
import com.dalthed.tucan.Connection.CookieManager;
import com.dalthed.tucan.Connection.RequestObject;
import com.dalthed.tucan.Connection.SimpleSecureBrowser;
import com.dalthed.tucan.exceptions.LostSessionException;
import com.dalthed.tucan.exceptions.TucanDownException;
import com.dalthed.tucan.scraper.SingleEventScraper;
import com.dalthed.tucan.util.ConfigurationChangeStorage;
import com.viewpagerindicator.TitlePageIndicator;

public class FragmentSingleEvent extends FragmentWebActivity {
	static final int NUM_ITEMS = 3;
	PagerAdapter mPageAdapter;
	ViewPager mPager;
	private CookieManager localCookieManager;
	private static final String LOG_TAG = "TuCanMobile";
	private String URLStringtoCall;
	private Boolean PREPCall;
	protected String[] mTitles;
	private SingleEventScraper scrape;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, true, 2);
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
		titleIndicator.setFooterColor(getResources().getColor(R.color.tucan_green));
		titleIndicator.setBackgroundColor(getResources().getColor(R.color.tucan_grey));
		titleIndicator.setSelectedBold(false);
		
			try {
				// Seite aufrufen..
				callResultBrowser = new SimpleSecureBrowser(this);
				if (TucanMobile.DEBUG) {
					callResultBrowser.HTTPS = this.HTTPS;
				}
				URLtoCall = new URL(URLStringtoCall);

				localCookieManager = new CookieManager();
				localCookieManager.generateManagerfromHTTPString(URLtoCall.getHost(),
						CookieHTTPString);

				RequestObject thisRequest = new RequestObject(URLStringtoCall, localCookieManager,
						RequestObject.METHOD_GET, "");

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
	public static class PagerAdapter extends FragmentPagerAdapter {
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
			ArrayListFragment newFragment = ArrayListFragment.newInstance(position);

			return (ListFragment) newFragment;
		}

		/**
		 * Initialisiertes ListFragment abrufen
		 * 
		 * @param position
		 *            x-Position des Fragments
		 * @return ListFragment der position
		 */
		public ArrayListFragment getInitializedItem(int position) {
			ArrayListFragment fragment = (ArrayListFragment) fm
					.findFragmentByTag("android:switcher:" + R.id.multipager + ":" + position);
			return (ArrayListFragment) fragment;

		}

		/**
		 * Speichert den entsprechenden ArrayAdapter in einer Liste des
		 * PagerAdapters
		 * 
		 * @param adapter
		 *            der zu speichernde Adapter
		 */
		public void setAdapter(ArrayAdapter<String> adapter) {
			adapterList.add(adapter);
		}

		public void setPrimaryItem(ViewGroup container, int position, Object object) {
			super.setPrimaryItem(container, position, object);
			Log.i(LOG_TAG, "setPrimaryItem called");
			if (object instanceof ArrayListFragment && position <= (adapterList.size() - 1)) {
				Log.i(LOG_TAG, "is the right instance -> Position is " + position
						+ " and Object is: " + object.toString());
				ArrayListFragment curFrag = ((ArrayListFragment) object);
				if (position == 2 && fileList != null) {
					curFrag.setFilelinks(fileList);
				}
				if (!curFrag.hasListAdapter) {
					curFrag.setListAdapter(adapterList.get(position));
				}

			}
			/*
			 * Liste wird eingebaut, wenn geupdated wird und die Liste noch leer
			 * ist
			 * 
			 * for (int ii = 0; ii <= 2; ii++) { if (getInitializedItem(ii) !=
			 * null && adapterList.size() > ii) { if
			 * (getInitializedItem(ii).getListAdapter() == null)
			 * getInitializedItem(ii).setListAdapter( adapterList.get(ii)); if
			 * (ii == 2 && fileList != null) {
			 * getInitializedItem(ii).setFilelinks(fileList); } } else {
			 * Log.i(LOG_TAG, "hier ist schon was drin"); }
			 * 
			 * }
			 */
		}

		public void initializeData(ViewGroup container) {
			setPrimaryItem(container, 0, getInitializedItem(0));
		}

		@Override
		public void startUpdate(ViewGroup container) {
			super.startUpdate(container);
			Log.i(LOG_TAG, "startUpdate called");
		}

		@Override
		public int getCount() {
			return NUM_ITEMS;
		}

		public CharSequence getTitle(int position) {

			return mtitles[position];
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return getTitle(position);
		}
	}

	public static class ArrayListFragment extends SherlockListFragment {
		int mNum;
		private boolean thereAreFiles;
		private ArrayList<String> materialLink;
		public boolean hasListAdapter = false;

		@Override
		public void setListAdapter(ListAdapter adapter) {
			super.setListAdapter(adapter);
			hasListAdapter = true;
		}

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
			View v = inflater.inflate(R.layout.fragment_pager_list, container, false);
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
					String url = TucanMobile.TUCAN_PROT + TucanMobile.TUCAN_HOST
							+ materialLink.get(position);

					Uri mUri = Uri.parse(url);
					Intent DownloadFile = new Intent(Intent.ACTION_VIEW, mUri);

					startActivity(DownloadFile);
				}

			}
		}

	}

	public void onPostExecute(AnswerObject result) {

		scrape = new SingleEventScraper(this, result, PREPCall, fsh, mPageAdapter, mPager);
		try {

			scrape.scrapeAdapter(0);
			this.PREPCall = scrape.PREPCall;
		} catch (LostSessionException e) {
			Intent BackToLoginIntent = new Intent(this, TuCanMobileActivity.class);
			BackToLoginIntent.putExtra("lostSession", true);
			startActivity(BackToLoginIntent);
		} catch (TucanDownException e) {
			TucanMobile.alertOnTucanDown(this, e.getMessage());
		}

	}

	@Override
	public ConfigurationChangeStorage saveConfiguration() {
		return null;
	}

	@Override
	public void retainConfiguration(ConfigurationChangeStorage conf) {
	}

	

}
