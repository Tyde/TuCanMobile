package com.dalthed.tucan.service.parcelables;

import android.os.Parcel;
import android.os.Parcelable;

public class HTTPSResponse implements Parcelable {
	public String HTMLResponse;
	public String TucanURI;
	
	public HTTPSResponse(final String HTMLResponse, final String TucanURI) {
		this.HTMLResponse=HTMLResponse;
		this.TucanURI=TucanURI;
	}
	
	private HTTPSResponse(final Parcel in) {
		readFromParcel(in);
	}

	
	public static final Parcelable.Creator<HTTPSResponse> CREATOR =
		new Parcelable.Creator<HTTPSResponse>() {

			@Override
			public HTTPSResponse createFromParcel(Parcel source) {
				// TODO Auto-generated method stub
				return new HTTPSResponse(source);
			}

			@Override
			public HTTPSResponse[] newArray(int size) {
				// TODO Auto-generated method stub
				return new HTTPSResponse[size];
			}
			
		};
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void readFromParcel(final Parcel in) {
		this.HTMLResponse=in.readString();
		this.TucanURI=in.readString();
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(HTMLResponse);
		out.writeString(TucanURI);
	}

}
