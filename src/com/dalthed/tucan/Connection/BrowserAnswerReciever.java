package com.dalthed.tucan.Connection;

import android.app.Activity;
import android.widget.ListAdapter;

import com.dalthed.tucan.scraper.BasicScraper;
import com.dalthed.tucan.util.ConfigurationChangeStorage;
/**
 * Interface, das es erm�glicht, die empfangenen Antworten vom Server innerhalb der {@link Activity} weiterzuverarbeiten
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
	 * Gibt ein {@link ConfigurationChangeStorage} zur�ck welche alle wichtigen {@link BasicScraper}, {@link ListAdapter} und {@link SimpleSecureBrowser} der Activity enth�lt
	 * @return gef�llter {@link ConfigurationChangeStorage}
	 * 
	 */
	public abstract ConfigurationChangeStorage saveConfiguration();
	
	/**
	 * Pflegt den empfangenen {@link ConfigurationChangeStorage} wieder in die {@link Activity} ein 
	 * @param conf {@link ConfigurationChangeStorage}, welcher relevante Daten f�r die Activity enth�lt
	 */
	public abstract void retainConfiguration(ConfigurationChangeStorage conf);
}
