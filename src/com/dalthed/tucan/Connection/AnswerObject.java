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
/**
 * AnswerObject ist das Object, welches von BrowseMethods wieder zur�ckgegeben wird
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
