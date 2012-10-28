package com.dalthed.tucan.Connection;

import android.app.Activity;
import android.widget.ListAdapter;

import com.dalthed.tucan.scraper.BasicScraper;
import com.dalthed.tucan.util.ConfigurationChangeStorage;
/**
 * Interface, das es ermöglicht, die empfangenen Antworten vom Server innerhalb der {@link Activity} weiterzuverarbeiten
 * @author Daniel Thiem
 *
 */
public interface BrowserAnswerReciever {
	/**
	 * Wird aufgerufen, wenn SimpleSecureBrowser fertig ist.
	 * 
	 * @param result
	 */
	public abstract void onPostExecute(AnswerObject result);
	
	/**
	 * Gibt ein {@link ConfigurationChangeStorage} zurück welche alle wichtigen {@link BasicScraper}, {@link ListAdapter} und {@link SimpleSecureBrowser} der Activity enthält
	 * @return gefüllter {@link ConfigurationChangeStorage}
	 * 
	 */
	public abstract ConfigurationChangeStorage saveConfiguration();
	
	/**
	 * Pflegt den empfangenen {@link ConfigurationChangeStorage} wieder in die {@link Activity} ein 
	 * @param conf {@link ConfigurationChangeStorage}, welcher relevante Daten für die Activity enthält
	 */
	public abstract void retainConfiguration(ConfigurationChangeStorage conf);
}
