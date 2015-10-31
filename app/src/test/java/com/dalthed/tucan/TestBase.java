package com.dalthed.tucan;

import com.google.gson.Gson;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import static org.junit.Assert.*;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class TestBase {

    protected String resourcesBaseName = "";
    protected Class testClazzModel;
    protected HashMap<String, String> sourcesMap = new HashMap<>();
    protected HashMap<String, Object> resultsMap = new HashMap<>();

    @Before
    public void getResources() {
        boolean hasFoundResource = false;
        int i = 0;
        Gson gson = new Gson();
        do {
            String resourceName = "../../../" + resourcesBaseName + "_" + i + ".html";
            String resultName = "../../../" + resourcesBaseName + "_" + i + ".results.json";

            InputStream stream = TestBase.class.getResourceAsStream(resourceName);
            InputStream resultStream = TestBase.class.getResourceAsStream(resultName);
            hasFoundResource = stream != null;
            boolean hasFoundResult = resultStream != null;
            if (hasFoundResource && hasFoundResult) {

                try {
                    String file = IOUtils.toString(stream);
                    Object results = gson.fromJson(IOUtils.toString(resultStream), testClazzModel);
                    resultsMap.put(resourcesBaseName + "_" + i + ".html", results);
                    sourcesMap.put(resourcesBaseName + "_" + i + ".html", file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            i++;
        } while (hasFoundResource);

    }

    @Test
    public void doMockTest() {
        assertTrue(true);
    }
}
