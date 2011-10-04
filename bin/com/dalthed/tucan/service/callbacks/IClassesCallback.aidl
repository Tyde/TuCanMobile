package com.dalthed.tucan.service.callbacks;
import  com.dalthed.tucan.service.parcelables.HTTPSResponse;

interface IClassesCallback {
	void expressClassesdata(in HTTPSResponse ClassesResponse);
	
}