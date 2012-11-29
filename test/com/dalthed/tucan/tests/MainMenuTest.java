package com.dalthed.tucan.tests;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.junit.Test;

import com.dalthed.tucan.Connection.AnswerObject;
import com.dalthed.tucan.exceptions.LostSessionException;
import com.dalthed.tucan.exceptions.TucanDownException;
import com.dalthed.tucan.scraper.MainMenuScraper;
import com.dalthed.tucan.ui.MainMenu;

public class MainMenuTest extends BasicTest {
	
	@Test
	public void testMainMenu() throws IOException, LostSessionException, TucanDownException {
		String URLStringtoCall = dtef.getErrorURL(16019, "MainMenu");
		MainMenu fakeMenu = new MainMenu();
		String html = Jsoup.connect(URLStringtoCall)
				.cookie("canView", "16ede40c878aee38d0882b3a6b2642c0ae76dafb").get().html();
		AnswerObject result = new AnswerObject(html, "", cm, URLStringtoCall);
		System.out.println(result.getLastCalledURL());
		MainMenuScraper mmScrape = new MainMenuScraper(fakeMenu, result);
		mmScrape.scrapeAdapter(0);
	}
	
	

}
