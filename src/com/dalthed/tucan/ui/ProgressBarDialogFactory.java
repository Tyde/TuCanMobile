package com.dalthed.tucan.ui;

import com.dalthed.tucan.TuCanMobileActivity;

import android.app.ProgressDialog;
import android.content.Context;


public class ProgressBarDialogFactory {
	
	public static ProgressDialog createProgressDialog (Context context,String title) {
		ProgressDialog dialog;
		dialog = new ProgressDialog(context);
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dialog.setMax(100);
		dialog.setProgress(0);
		dialog.setTitle(title);
		return dialog;
	}

}
