package com.dalthed.tucan.ui;

import com.dalthed.tucan.Connection.AnswerObject;

import android.app.ListActivity;

public abstract class SimpleWebListActivity extends ListActivity {
	public abstract void onPostExecute(AnswerObject result) ;
}
