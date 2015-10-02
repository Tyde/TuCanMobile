package com.dalthed.tucan.tests;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.junit.Test;

import com.dalthed.tucan.connection.AnswerObject;
import com.dalthed.tucan.exceptions.LostSessionException;
import com.dalthed.tucan.exceptions.TucanDownException;
import com.dalthed.tucan.scraper.MainMenuScraper;
import com.dalthed.tucan.scraper.UniApplicationScraper;
import com.dalthed.tucan.ui.MainMenu;

public class MainMenuTest extends BasicTest {
	
	@Test
	public void testMainMenu() throws IOException, LostSessionException, TucanDownException {
		//String URLStringtoCall = dtef.getErrorURL(16019, "MainMenu");
		String URLStringtoCall = lhef.getErrorURL("fdf3bea7-d967-4dab-ba44-c73bbee46762");
		MainMenu fakeMenu = new MainMenu();
		String html = Jsoup.connect(URLStringtoCall)
				.cookie("canView", "16ede40c878aee38d0882b3a6b2642c0ae76dafb").get().html();
		AnswerObject result = new AnswerObject(html, "", cm, URLStringtoCall);
		System.out.println(result.getLastCalledURL());
		MainMenuScraper mmScrape = new MainMenuScraper(fakeMenu, result);
		mmScrape.scrapeAdapter(0);
	}
	
	
	@Test
	public void testUniApplication() throws IOException,LostSessionException,TucanDownException {
		String URLStringtoCall = lhef.getErrorURL("fdf3bea7-d967-4dab-ba44-c73bbee46762");
		MainMenu fakeMenu = new MainMenu();
		String html = Jsoup.connect(URLStringtoCall)
				.cookie("canView", "16ede40c878aee38d0882b3a6b2642c0ae76dafb").get().html();
		AnswerObject result = new AnswerObject(html, "", cm, URLStringtoCall);
		System.out.println(result.getLastCalledURL());
		UniApplicationScraper mmScrape = new UniApplicationScraper(fakeMenu, result);
		mmScrape.scrapeAdapter(0);
	}
	

}
