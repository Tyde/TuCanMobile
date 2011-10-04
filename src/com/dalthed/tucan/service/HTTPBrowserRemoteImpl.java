package com.dalthed.tucan.service;

import com.dalthed.tucan.service.HTTPBrowserRemote;
import com.dalthed.tucan.service.callbacks.IClassesCallback;
import com.dalthed.tucan.service.parcelables.HTTPSResponse;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.widget.Toast;

public class HTTPBrowserRemoteImpl extends Service {
	private final RemoteCallbackList<IClassesCallback> callbacks =
		new RemoteCallbackList<IClassesCallback>();
	
	
	
	
	private final HTTPBrowserRemote.Stub mBrowserRemoteBinder =
		new HTTPBrowserRemote.Stub() {
			
			@Override
			public boolean send_login_credentials(String user, String pwd)
					throws RemoteException {
				new Thread() {
					@Override
					public void run() {
						//TODO: send Credentials
					}
				}.start();
				return false;
			}
			
			

			@Override
			public void call_course_overview() throws RemoteException {
				// TODO Auto-generated method stub
				Toast.makeText(HTTPBrowserRemoteImpl.this, "Course Overview called", Toast.LENGTH_SHORT).show();
				final HTTPSResponse myResponse = new HTTPSResponse("START", "Test");
				final int anzCallbacks = callbacks.beginBroadcast();
				for(int i=0; i < anzCallbacks; i++){
					try{
						callbacks.getBroadcastItem(i).
							expressClassesdata(myResponse);
					}
					catch (RemoteException e) {
						// TODO: handle exception
					}
				}
				callbacks.finishBroadcast();
			}

		

			@Override
			public void register_course_callback(IClassesCallback callback)
					throws RemoteException {
				if(callback!=null){
					callbacks.register(callback);
				}
				
			}

			@Override
			public void unregister_course_callback(IClassesCallback callback)
					throws RemoteException {
				if(callback!=null){
					callbacks.unregister(callback);
				}
				
			}
		};
		
	@Override
	public void onCreate() {
		super.onCreate();
		Toast.makeText(this, "Well I'm Stared over here...", Toast.LENGTH_SHORT);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Well I'm Bound over here...", Toast.LENGTH_SHORT).show();
		return mBrowserRemoteBinder;
	}

}
