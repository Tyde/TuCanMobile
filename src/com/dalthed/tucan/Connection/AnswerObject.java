package com.dalthed.tucan.Connection;

public class AnswerObject {
	private String HTML_text;
	private String redirectURL;
	private String lastcalledURL;
	private CookieManager Cookies;
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
