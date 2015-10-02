package com.dalthed.tucan.tests;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.junit.Test;

import com.dalthed.tucan.connection.AnswerObject;
import com.dalthed.tucan.exceptions.LostSessionException;
import com.dalthed.tucan.exceptions.TucanDownException;
import com.dalthed.tucan.scraper.ExamsScraper;
import com.dalthed.tucan.ui.Exams;

public class ExamsTest extends BasicTest {
	//@Test
	public void testExamsScraper() throws IOException, LostSessionException, TucanDownException {
		String URLStringtoCall = dtef.getErrorURL(1132, "ExamsScraper");
		Exams fakeExams = new Exams();
		String html = Jsoup.connect(URLStringtoCall)
				.cookie("canView", "16ede40c878aee38d0882b3a6b2642c0ae76dafb").get().html();
		AnswerObject result = new AnswerObject(html, "", cm, URLStringtoCall);
		ExamsScraper examScraper = new ExamsScraper(fakeExams, result);
		int methodid = 3;

		examScraper.scrapeAdapter(methodid);
		
		
	}
}
