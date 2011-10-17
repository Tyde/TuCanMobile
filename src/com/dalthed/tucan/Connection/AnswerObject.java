package com.dalthed.tucan.Connection;

public class AnswerObject {
	private String HTML_text;
	private String redirectURL;
	private CookieManager Cookies;
	public AnswerObject(String HTML,String redirect,CookieManager myCookies){
		this.HTML_text=HTML;
		this.redirectURL=redirect;
		this.Cookies=myCookies;
	}
	public String getHTML(){
		return this.HTML_text;
	}
	public String getRedirectURLString() {
		return this.redirectURL;
	}
	public CookieManager getCookieManager() {
		return this.Cookies;
	}
	
}
