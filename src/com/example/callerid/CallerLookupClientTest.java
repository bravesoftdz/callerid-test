package com.example.callerid;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class CallerLookupClientTest {
	private CallerLookupClient client;
	
	@Before
	public void setUp() throws Exception {
		client = new CallerLookupClient();
	}
	
	@Test
	public void testLookupPerson() throws Exception {
		assertEquals("Erik Amundrud", client.fetchName("90956200"));
	}
	
	@Test
	public void testLookupCompany() throws Exception {
		assertEquals("Color Line AS", client.fetchName("81000811"));
	}
	
	/** BONUS */
	@Test
	public void testLookupPhoneSalesman() throws Exception {
		assertEquals("UNICALL", client.fetchName("35705850"));
	}
}
