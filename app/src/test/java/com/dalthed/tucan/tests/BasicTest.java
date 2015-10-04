package com.dalthed.tucan.tests;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.dalthed.tucan.TucanMobile;
import com.dalthed.tucan.connection.CookieManager;
import com.xtremelabs.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class BasicTest {
	
	protected DanielThiemdeErrorFinder dtef;
	protected CookieManager cm;
	protected LocalhostErrorFinder lhef;

	@Before
	public void setUp() throws Exception {

		if (!TucanMobile.TESTING) {
			System.err.println("App is NOT in Testing mode");
			assertTrue(false);
			System.exit(1);
		}
		dtef = new DanielThiemdeErrorFinder();
		lhef = new LocalhostErrorFinder();
		cm = new CookieManager();
		cm.inputCookie("www.daniel-thiem.de", "canView", "16ede40c878aee38d0882b3a6b2642c0ae76dafb");
	}
	
	@Test
	public void fakeTest() {
		assertTrue(true);
	}
	

}
