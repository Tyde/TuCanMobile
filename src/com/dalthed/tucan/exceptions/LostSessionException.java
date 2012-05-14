package com.dalthed.tucan.exceptions;

public class LostSessionException extends Exception {

	public LostSessionException() {
		super();
	}

	public LostSessionException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public LostSessionException(String detailMessage) {
		super(detailMessage);
	}

	public LostSessionException(Throwable throwable) {
		super(throwable);
	}

}
