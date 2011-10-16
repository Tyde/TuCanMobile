package com.dalthed.tucan;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import android.os.AsyncTask;
import android.widget.Toast;

public class HTTPSBrowser extends AsyncTask<URL, Integer, String>  {
	private HttpsURLConnection HTTPConnection;
	@Override
	protected String doInBackground(URL... realURL) {
		try {
			HTTPConnection = (HttpsURLConnection) realURL[0].openConnection();
			InputStream ins = HTTPConnection.getInputStream();
			InputStreamReader isr = new InputStreamReader(ins);
			BufferedReader in = new BufferedReader(isr);
			String inputLine;
			String alllines="";
			while ((inputLine = in.readLine())!= null ){
				alllines+=inputLine;
			}
			in.close();
			//Toast notifyall = Toast.makeText(TucanMobile.getAppContext(), "Doing in Background", Toast.LENGTH_SHORT);
			//notifyall.show();
			return alllines;
		} catch (Exception e) {
			//e.printStackTrace();
			//Toast notifyall = Toast.makeText(TucanMobile.getAppContext(), e.getMessage(), Toast.LENGTH_SHORT);
			//notifyall.show();
			return "something wrong here"+e.getMessage();
		}
	}
	@Override
	protected void onPostExecute(String result) {
		Toast notifyall = Toast.makeText(TucanMobile.getAppContext(), "DONE", Toast.LENGTH_SHORT);
		notifyall.show();
	}
}
