package com.dalthed.tucan.Connection;
/**
 * AnswerObject ist das Object, welches von BrowseMethods wieder zurückgegeben wird
 * Es enthält den HTML text, die redirect URL, die zuletzt aufgerufene URL und den CookieManager
 * @author Tyde
 *
 */
public class AnswerObject {
	private String HTML_text;
	private String redirectURL;
	private String lastcalledURL;
	private CookieManager Cookies;
	/**
	 * RückgabeObjekt der BrowseMethods
	 * @param HTML HTML-Code
	 * @param redirect Redirect-URL aus dem HTTP-Header
	 * @param myCookies CookieManager mit relevanten Cookies
	 * @param lastcalledURL Zuletzt aufgerufene URL
	 */
	public AnswerObject(String HTML,String redirect,CookieManager myCookies,String lastcalledURL){
		this.HTML_text=HTML;
		this.redirectURL=redirect;
		this.Cookies=myCookies;
		this.lastcalledURL=lastcalledURL;
	}
	public String getHTML(){
		return this.HTML_text;
	}
	public String getRedirectURLString() {
		return this.redirectURL;
	}
	public String getLastCalledURL() {
		return this.lastcalledURL;
	}
	public CookieManager getCookieManager() {
		return this.Cookies;
	}
	
}
