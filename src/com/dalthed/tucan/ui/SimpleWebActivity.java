package com.dalthed.tucan.ui;

import com.dalthed.tucan.Connection.AnswerObject;

import android.app.Activity;

public abstract class SimpleWebActivity extends Activity {
	public abstract void onPostExecute(AnswerObject result) ;
}
