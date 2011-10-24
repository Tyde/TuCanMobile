package com.dalthed.tucan.ui;

import com.dalthed.tucan.Connection.AnswerObject;

import android.app.ListActivity;
/**
 * SimpleWebListActivity notwendig für SimpleSecureBrowser
 * @author Tyde
 *
 */
public abstract class SimpleWebListActivity extends ListActivity {
	/**
	 * Wird aufgerufen, wenn SimpleSecureBrowser fertig ist.
	 * @param result
	 */
	public abstract void onPostExecute(AnswerObject result) ;
}
