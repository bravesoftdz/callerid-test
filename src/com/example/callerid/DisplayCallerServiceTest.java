package com.example.callerid;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import android.widget.TextView;

@RunWith(RobolectricTestRunner.class)
public class DisplayCallerServiceTest {
	private final static String CALLER_NUMBER = "02486";
	private final static String CALLER_NAME = "Knowit";
	
	private DisplayCallerService service;
	
	@Before
	public void setUp() throws Exception {
		service = mock(DisplayCallerService.class);
		
//		when(service.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).thenReturn(Robolectric.application.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
//		when(service.getSystemService(Service.WINDOW_SERVICE)).thenReturn(Robolectric.application.getSystemService(Service.WINDOW_SERVICE));
//		when(service.getNumber(new Intent())).thenReturn(CALLER_NUMBER);
//		doCallRealMethod().when(service).onCreate();
//		doCallRealMethod().when(service).onStartCommand(new Intent(), 0, 0);
//		
//		service.onCreate();
//		service.onStartCommand(new Intent(), 0, 0);
	}
	
	/**
	 * Service should display incoming number on TextView
	 * @See caller_id.xml
	 */
	@Test
	public void viewShouldDisplayNumber() throws Exception {
		doCallRealMethod().when(service).updateViewWithCallerNumber(anyString());
		service.updateViewWithCallerNumber(CALLER_NUMBER);
		assertEquals(CALLER_NUMBER, ((TextView)service.view.findViewById(R.id.number)).getText());
	}
	
	/**
	 * Service should display callers id on TextView
	 * @See caller_id.xml
	 */
	@Test
	public void viewShouldDisplayName() throws Exception {
		doCallRealMethod().when(service).updateViewWithCallerName(anyString());
		service.updateViewWithCallerName(CALLER_NAME);
		assertEquals(CALLER_NAME, ((TextView)service.view.findViewById(R.id.name)).getText());
	}
}
