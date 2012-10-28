package com.dalthed.tucan.Connection;

/**
 * Wie {@link BrowserAnswerReciever}, nur für {@link SimpleBackgroundBrowser}. Derzeit noch nicht voll funktionsfähig
 * @author Daniel Thiem
 *
 */
public interface BackgroundBrowserReciever {
	
	/**
	 * Wird aufgerufen, wenn der Request vollständig ausgelesen wurde
	 * @param result Antwort des Servers
	 */
	public void onBackgroundBrowserFinalized(AnswerObject result);
	
	public boolean getwindowFeatureCalled();
}
