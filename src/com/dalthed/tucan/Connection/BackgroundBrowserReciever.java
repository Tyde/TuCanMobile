package com.dalthed.tucan.Connection;

/**
 * Wie {@link BrowserAnswerReciever}, nur f�r {@link SimpleBackgroundBrowser}. Derzeit noch nicht voll funktionsf�hig
 * @author Daniel Thiem
 *
 */
public interface BackgroundBrowserReciever {
	
	/**
	 * Wird aufgerufen, wenn der Request vollst�ndig ausgelesen wurde
	 * @param result Antwort des Servers
	 */
	public void onBackgroundBrowserFinalized(AnswerObject result);
	
	public boolean getwindowFeatureCalled();
}
