package com.dalthed.tucan.service;

import com.dalthed.tucan.service.parcelables.HTTPSResponse;
import com.dalthed.tucan.service.callbacks.IClassesCallback;

interface HTTPBrowserRemote {
	boolean send_login_credentials(String user,String pwd);
	
	
	oneway void call_course_overview();
	
	
	void register_course_callback(IClassesCallback callback);
	void unregister_course_callback(IClassesCallback callback);
}