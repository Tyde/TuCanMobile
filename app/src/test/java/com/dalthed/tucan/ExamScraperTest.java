package com.dalthed.tucan;

import android.widget.ListAdapter;

import com.dalthed.tucan.Connection.AnswerObject;
import com.dalthed.tucan.Connection.CookieManager;
import com.dalthed.tucan.adapters.ThreeLinesAdapter;
import com.dalthed.tucan.exceptions.LostSessionException;
import com.dalthed.tucan.exceptions.TucanDownException;
import com.dalthed.tucan.scraper.ExamsScraper;
import com.dalthed.tucan.testmodels.ExamsModel;
import com.dalthed.tucan.testmodels.exams.TestExamList;

import org.junit.Test;
import org.robolectric.RuntimeEnvironment;

import java.util.Map;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 * Created by yttyd on 30.10.2015.
 */
public class ExamScraperTest extends TestBase {

    public ExamScraperTest() {
        this.resourcesBaseName = "Exams";
        this.testClazzModel = ExamsModel.class;
    }

    @Test
    public void testExamList() {
        for (Map.Entry<String, String> entry : sourcesMap.entrySet()) {
            String id = entry.getKey();
            Object resultObject = resultsMap.get(id);
            if (resultObject instanceof ExamsModel) {

                ExamsModel result = (ExamsModel) resultObject;
                if (result.testExamList.runTest) {
                    System.out.println("Testing " + id + " (testExamList)");
                    AnswerObject answer = new AnswerObject(entry.getValue(), "", new CookieManager(), "");
                    ExamsScraper scraper = new ExamsScraper(RuntimeEnvironment.application, answer);

                    try {
                        TestExamList testExamList = result.testExamList;
                        ListAdapter adapter = scraper.scrapeAdapter(testExamList.mode);
                        if (testExamList.mode == 1) {
                            checkForCourses(testExamList, adapter);
                            checkForMarks(testExamList, adapter);
                        }

                    } catch (LostSessionException e) {
                        fail("Scraper meldet verlorene Session bei korrekter Ausgabe");
                    } catch (TucanDownException e) {
                        fail("Scraper meldet TucanDown bei korrekter Ausgabe");
                    }
                }
            }
        }
    }

    private void checkForMarks(TestExamList testExamList, ListAdapter adapter) {
        if (adapter instanceof ThreeLinesAdapter) {
            ThreeLinesAdapter tlAdapter = (ThreeLinesAdapter) adapter;
            assertThat(testExamList.marks, is(tlAdapter.middleRightThin));
        }
    }

    private void checkForCourses(TestExamList testExamList, ListAdapter adapter) {
        assertEquals(adapter.getCount(), testExamList.courses.size());
        for (int i = 0; i < adapter.getCount(); i++) {
            assertEquals(adapter.getItem(i), testExamList.courses.get(i));
        }
    }


}
