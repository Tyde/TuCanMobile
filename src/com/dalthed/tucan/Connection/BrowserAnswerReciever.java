/**
 *	This file is part of TuCan Mobile.
 *
 *	TuCan Mobile is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	TuCan Mobile is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with TuCan Mobile.  If not, see <http://www.gnu.org/licenses/>.
 */

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
