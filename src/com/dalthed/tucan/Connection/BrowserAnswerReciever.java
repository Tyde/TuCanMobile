package com.dalthed.tucan.Connection;

public interface BrowserAnswerReciever {
	/**
	 * Wird aufgerufen, wenn SimpleSecureBrowser fertig ist.
	 * 
	 * @param result
	 */
	public abstract void onPostExecute(AnswerObject result);
}
