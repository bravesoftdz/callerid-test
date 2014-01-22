package com.example.callerid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.ContactsContract.PhoneLookup;
import android.telephony.TelephonyManager;

@RunWith(RobolectricTestRunner.class)
public class PhoneStateReceiverTest {
	private final static String KNOWN_CALLER = "02486";
	private final static String UNKNOWN_CALLER = "22654321";
	private final static String ABROAD_CALLER = "+46854613312";
	
	private PhoneStateReceiver receiver;
	private Context context;
	
	@Before
	public void setUp() throws Exception {
		receiver = new PhoneStateReceiver();
		context = mock(Context.class);
	}

	/**
	 * 	Incoming call should start service.
	 * 	Phone is online and number is unknown.
	 */
	@Test
	public void incomingCallShouldStartService() throws Exception {
		mockupConnectivityManager(true);
		mockPhoneLookupContentResolver(false, UNKNOWN_CALLER);
		
		receiver.onReceive(context, createIncomingCallIntent(UNKNOWN_CALLER));
		assertNull(receiver.getResultData());
		
		ArgumentCaptor<Intent> argument = ArgumentCaptor.forClass(Intent.class);
		verify(context, times(1)).startService(argument.capture());
		verify(context, never()).stopService(argument.capture());
		assertEquals(argument.getValue().getComponent().getClassName(), DisplayCallerService.class.getName());
	}

	/**
	 * Answering the phone should stop service,
	 * phone is offline (unimportant) and number is unknown (unimportant).
	 */
	@Test
	public void answeringPhoneShouldStopService() throws Exception {
		mockupConnectivityManager(false);
		mockPhoneLookupContentResolver(false, UNKNOWN_CALLER);
		
		receiver.onReceive(context, createOffHookIntent());
		assertNull(receiver.getResultData());
		
		ArgumentCaptor<Intent> argument = ArgumentCaptor.forClass(Intent.class);
		verify(context, never()).startService(argument.capture());
		verify(context, times(1)).stopService(argument.capture());
		assertEquals(argument.getValue().getComponent().getClassName(), DisplayCallerService.class.getName());
	}
	
	/** BONUS */
	@Test
	public void testIsHiddenNumber() throws Exception {
		assertTrue(receiver.isHidden(null));
		assertFalse(receiver.isHidden(UNKNOWN_CALLER));
		assertFalse(receiver.isHidden(KNOWN_CALLER));
	}
	
	/** BONUS */
	@Test
	public void testIsNorwegian() throws Exception {
		assertTrue(receiver.isNorwegian(UNKNOWN_CALLER));
		assertTrue(receiver.isNorwegian(KNOWN_CALLER));
		assertFalse(receiver.isNorwegian(ABROAD_CALLER));
	}
	
	/** BONUS */
	@Test
	public void testIsOnline() throws Exception {
		mockupConnectivityManager(false);
		assertFalse(receiver.isOnline(context));
		
		mockupConnectivityManager(true);
		assertTrue(receiver.isOnline(context));
	}
	
	/** BONUS */
	@Test
	public void testIsKnownContact() throws Exception {
		mockPhoneLookupContentResolver(false, UNKNOWN_CALLER);
		assertFalse(receiver.isKnownContact(context, UNKNOWN_CALLER));
		
		mockPhoneLookupContentResolver(true, KNOWN_CALLER);
		assertTrue(receiver.isKnownContact(context, KNOWN_CALLER));
	}

	private ConnectivityManager mockupConnectivityManager(boolean online) {
		final ConnectivityManager connectivityManager = mock(ConnectivityManager.class);
		final NetworkInfo networkInfo = Mockito.mock(NetworkInfo.class);
		when(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(connectivityManager);
		
		when(connectivityManager.getNetworkInfo(anyInt())).thenReturn(networkInfo);
		when(connectivityManager.getActiveNetworkInfo()).thenReturn(networkInfo);
		
		when(networkInfo.isAvailable()).thenReturn(online);
		when(networkInfo.isConnected()).thenReturn(online);
		
		return connectivityManager;
	}
	
	private void mockPhoneLookupContentResolver(boolean inDb, String number) {
		ContentResolver contentResolver = mock(ContentResolver.class);
		when(context.getContentResolver()).thenReturn(contentResolver);
		Uri lookupUnknown = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
		
		Cursor cursor = mock(Cursor.class);
		when(cursor.moveToFirst()).thenReturn(inDb);
		when(cursor.move(inDb ? 0 : anyInt())).thenReturn(inDb);
		when(cursor.getCount()).thenReturn(inDb ? 1 : 0);
		when(contentResolver.query(eq(lookupUnknown), any(String[].class), anyString(), any(String[].class), anyString())).thenReturn(cursor);
	}
	
	private Intent createIncomingCallIntent(String number) {
		Intent intent = new Intent(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
		intent.putExtra(TelephonyManager.EXTRA_STATE, TelephonyManager.EXTRA_STATE_RINGING);
		if (number!=null){
			intent.putExtra(TelephonyManager.EXTRA_INCOMING_NUMBER, number);
		}
		return intent;
	}
	
	private Intent createOffHookIntent() {
		Intent intent = new Intent(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
		intent.putExtra(TelephonyManager.EXTRA_STATE, TelephonyManager.EXTRA_STATE_OFFHOOK);
		return intent;
	}
}
