package com.dalthed.tucan;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dalthed.tucan.service.HTTPBrowserRemote;
import com.dalthed.tucan.service.HTTPBrowserRemoteImpl;
import com.dalthed.tucan.service.callbacks.IClassesCallback;
import com.dalthed.tucan.service.parcelables.HTTPSResponse;


public class TuCanMobileActivity extends Activity {
    /** Called when the activity is first created. */
    //private HTTPSbrowser mBrowserService;
    private HTTPBrowserRemote mBrowserRemoteService;
    private Boolean mbound=false;
    private ServiceConnection mBrowserRemoteServiceConnection = 
    	new ServiceConnection() {			
			@Override
			public void onServiceDisconnected(ComponentName name) {
				try {
					mBrowserRemoteService.unregister_course_callback(courseCallback);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				mBrowserRemoteService=HTTPBrowserRemote.Stub.asInterface(service);
				mbound=true;
				try {
					mBrowserRemoteService.register_course_callback(courseCallback);
					mBrowserRemoteService.call_course_overview();
				}
				catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			
	};
	
	private final IClassesCallback courseCallback =
		new IClassesCallback.Stub() {
			@Override
			public void expressClassesdata(HTTPSResponse ClassesResponse)
					throws RemoteException {
				Toast.makeText(TuCanMobileActivity.this, "Just works??", Toast.LENGTH_SHORT).show();
				
				final TextView txtLoginName = 
					(TextView) findViewById(R.id.textView1);
				txtLoginName.setText(ClassesResponse.HTMLResponse);
			}
		};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
    public void onClickSendLogin (final View sfNormal) {
    	final Intent browserIntent = new Intent(TuCanMobileActivity.this,HTTPBrowserRemoteImpl.class);

    	this.bindService(browserIntent, mBrowserRemoteServiceConnection, Context.BIND_AUTO_CREATE);
    	if(mbound==true){
    		Toast.makeText(TuCanMobileActivity.this, "Service Bound", Toast.LENGTH_SHORT).show();
    	}
    	else {
    		Toast.makeText(TuCanMobileActivity.this, "Service NOT Bound", Toast.LENGTH_SHORT).show();
    	}
    	try {
    			if(mBrowserRemoteService!= null)
    				mBrowserRemoteService.call_course_overview();
    		
			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
    	/*final Intent i = new Intent(this,MainMenu.class);
    	startActivity(i);*/
    	unbindService(mBrowserRemoteServiceConnection);
    }
}