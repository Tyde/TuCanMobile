package com.dalthed.tucan.tests;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertTrue;

import android.widget.ListAdapter;

import com.dalthed.tucan.TucanMobile;
import com.dalthed.tucan.Connection.AnswerObject;
import com.dalthed.tucan.Connection.CookieManager;
import com.dalthed.tucan.exceptions.LostSessionException;
import com.dalthed.tucan.exceptions.TucanDownException;
import com.dalthed.tucan.scraper.EventsScraper;
import com.dalthed.tucan.scraper.RegisterExamsScraper;
import com.dalthed.tucan.scraper.ScheduleScraper;
import com.dalthed.tucan.ui.Events;
import com.dalthed.tucan.ui.RegisterExams;
import com.dalthed.tucan.ui.Schedule;
import com.xtremelabs.robolectric.RobolectricTestRunner;

public class MainTest extends BasicTest {
	/*
	@Test
	public void runMyEvaluationFaster() throws IOException, LostSessionException, TucanDownException {
		ArrayList<File> htmlFiles = new ArrayList<File>();
		String basePathTucanSet= "C:\\Users\\Tyde\\Desktop\\tucan_set\\stage\\";
		File directory = new File( basePathTucanSet);
		if(directory.isDirectory()){
			FileFilter fastF = new FileFilter() {
				
				@Override
				public boolean accept(File arg0) {
					
					if(arg0.getName().endsWith("htm")){
						return true;
					}
					return false;
				}
			};
			htmlFiles.addAll( Arrays.asList(directory.listFiles(fastF)));
			
		}
		Events fakeEv = new Events();
		for(File singleFile : htmlFiles){
			String html =Jsoup.parse(singleFile,"ISO-8859-1").html();
			AnswerObject result = new AnswerObject(html, "", cm, "local");
			EventsScraper test = new EventsScraper(fakeEv, result);
			ListAdapter lAdap = test.scrapeAdapter(2);
			System.out.println(lAdap);
		}
	}
	*/
	@Test
	public void testEventsScraper() throws IOException, LostSessionException, TucanDownException {
		String URLStringtoCall = dtef.getErrorURL(68968, "EventsScraper");
		Events fakeEvents = new Events();
		String html = Jsoup.connect(URLStringtoCall)
				.cookie("canView", "16ede40c878aee38d0882b3a6b2642c0ae76dafb").get().html();
		AnswerObject result = new AnswerObject(html, "", cm, URLStringtoCall);
		EventsScraper fakescrape = new EventsScraper(fakeEvents	, result);
		fakescrape.scrapeAdapter(1);
	}

	 @Test
	public void testRegisterExamsScraper() throws IOException, LostSessionException,
			TucanDownException {

		String URLStringtoCall = dtef.getErrorURL(20254, "RegisterExams");
		RegisterExams fakeExams = new RegisterExams();
		String html = Jsoup.connect(URLStringtoCall)
				.cookie("canView", "16ede40c878aee38d0882b3a6b2642c0ae76dafb").get().html();
		AnswerObject result = new AnswerObject(html, "", cm, URLStringtoCall);
		RegisterExamsScraper examScraper = new RegisterExamsScraper(fakeExams, result,
				URLStringtoCall, cm);
		// "Welche methode soll ausgeführt werden?"
		// "0: scrapeAnswer(0)"
		// "1: startRegistration()"
		// "2: getRegisterDialog()"
		int methodid = 0;

		switch (methodid) {
		case 0:
			examScraper.scrapeAdapter(0);
			break;
		case 1:
			examScraper.startRegistration();
			break;
		case 2:
			examScraper.getRegisterdialog();
			break;
		default:
			break;
		}

	}

	//@Test
	public void testScheduleScraper() throws IOException, LostSessionException, TucanDownException {
		String URLStringtoCall = dtef.getErrorURL(13923, "Schedule");
		Schedule fakeSched = new Schedule();
		String html = Jsoup.connect(URLStringtoCall)
				.cookie("canView", "16ede40c878aee38d0882b3a6b2642c0ae76dafb").get().html();
		AnswerObject result = new AnswerObject(html, "", cm, URLStringtoCall);
		ScheduleScraper schedscraper = new ScheduleScraper(fakeSched, result);

		schedscraper.scrapeAdapter(0);
	}

}
