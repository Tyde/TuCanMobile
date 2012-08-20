package com.dalthed.tucan.Connection;

import com.dalthed.tucan.util.ConfigurationChangeStorage;

public interface BrowserAnswerReciever {
	/**
	 * Wird aufgerufen, wenn SimpleSecureBrowser fertig ist.
	 * 
	 * @param result
	 */
	public abstract void onPostExecute(AnswerObject result);
	
	/**
	 * 
	 * @return
	 */
	public abstract ConfigurationChangeStorage saveConfiguration();
	
	/**
	 * 
	 * @param conf
	 */
	public abstract void retainConfiguration(ConfigurationChangeStorage conf);
}
