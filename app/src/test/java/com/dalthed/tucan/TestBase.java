package com.dalthed.tucan;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;


import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class TestBase {

    protected String resourcesBaseName = "";
    protected HashMap<String, String> sourcesMap = new HashMap<>();

    @Before
    public void getResources() {
        boolean hasFoundResource = false;
        int i = 0;

        do {
            String resourceName = "../../../" + resourcesBaseName + "_" + i + ".html";
            System.out.println(resourceName);
            InputStream stream = TestBase.class.getResourceAsStream(resourceName);
            hasFoundResource = stream != null;
            if (stream != null) {
                System.out.println("found "+resourcesBaseName + "_" + i + ".html");
                try {
                    String file = IOUtils.toString(stream);
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
        assert true;
    }
}
