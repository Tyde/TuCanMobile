package com.dalthed.tucan.exceptions;

public class TucanDownException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public TucanDownException() {
		super();
	}
	
	public TucanDownException(String ErrorMessage) {
		super(ErrorMessage);
	}

}
