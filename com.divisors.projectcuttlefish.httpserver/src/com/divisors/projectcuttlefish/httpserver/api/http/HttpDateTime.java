package com.divisors.projectcuttlefish.httpserver.api.http;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class HttpDateTime {
	protected final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
	
	public static HttpDateTime now() {
		return new HttpDateTime(Calendar.getInstance().getTime());
	}
	
	protected final Instant time;
	
	public HttpDateTime(Date date) {
		time = null;
	}
	public HttpDateTime(long timestamp) {
		time = null;
	}
	@Override
	public String toString() {
		Calendar calendar = Calendar.getInstance();
	    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
	    return dateFormat.format(calendar.getTime());
	}
}
