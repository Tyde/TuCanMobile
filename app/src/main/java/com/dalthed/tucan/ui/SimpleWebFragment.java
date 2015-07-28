package com.dalthed.tucan.ui;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dalthed.tucan.TucanMobile;

import org.acra.ACRA;

/**
 * Created by yttyd_000 on 27.07.2015.
 */
public class SimpleWebFragment extends Fragment  {
    public static final String ARGUMENT_HTTPS = "HTTPS";
    protected Boolean https = true;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        if (TucanMobile.DEBUG &&  getArguments().containsKey(ARGUMENT_HTTPS)) {
            https = getArguments().getBoolean("HTTPS");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
