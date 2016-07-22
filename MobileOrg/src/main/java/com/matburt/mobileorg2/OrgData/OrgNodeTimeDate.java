package com.matburt.mobileorg2.OrgData;

import android.text.TextUtils;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OrgNodeTimeDate {
	public TYPE type = TYPE.Scheduled;

	public int year = -1;
	public int monthOfYear = -1;
	public int dayOfMonth = -1;
	public int startTimeOfDay = -1;
	public int startMinute = -1;
	public int endTimeOfDay = -1;
	public int endMinute = -1;
	public int matchStart = -1, matchEnd = -1;
	
	public enum TYPE {
		Scheduled,
		Deadline,
		Timestamp,
		InactiveTimestamp
	}

	public OrgNodeTimeDate(TYPE type) {
		this.type = type;
	}

	public OrgNodeTimeDate(TYPE type, String line){
		this.type = type;

//		Matcher matcher =OrgNodeTimeDate.getTimestampMatcher(type).matcher(line);
//
//		if(matcher.find(0)) {
//			result = matcher.group(2);
//
//			if(matcher.group(3) != null){
//				Log.v("time","group3 : "+matcher.group(3));
//				result += matcher.group(3);
//			}
//
//		}
		parseDate(line);
	}

	public OrgNodeTimeDate(TYPE type, int day, int month, int year) {
		this(type);
		setDate(day, month, year);
	}

	public OrgNodeTimeDate(TYPE type, int day, int month, int year, int startTimeOfDay, int startMinute) {
		this(type);
		setDate(day, month, year);
		setTime(startTimeOfDay, startMinute);
	}


	public void setDate(int day, int month, int year) {
		this.dayOfMonth = day;
		this.monthOfYear = month;
		this.year = year;
	}

	public void setTime(int startTimeOfDay, int startMinute) {
		this.startTimeOfDay = startTimeOfDay;
		this.startMinute = startMinute;
	}

	public void setToCurrentDate() {
		final Calendar c = Calendar.getInstance();
		this.year = c.get(Calendar.YEAR);
		this.monthOfYear = c.get(Calendar.MONTH) + 1;
		this.dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
	}
	
	private static final Pattern schedulePattern = Pattern
			.compile("((\\d{4})-(\\d{1,2})-(\\d{1,2}))(?:[^\\d]*)"
					+ "((\\d{1,2})\\:(\\d{2}))?(-((\\d{1,2})\\:(\\d{2})))?");

	public void parseDate(String date) {
		if(date == null)
			return;

		Matcher propm = schedulePattern.matcher(date);

		if (propm.find()) {
			matchStart = propm.start();
			matchEnd = propm.end();
			try {
				year = Integer.parseInt(propm.group(2));
				monthOfYear = Integer.parseInt(propm.group(3));
				dayOfMonth = Integer.parseInt(propm.group(4));
				
				startTimeOfDay = Integer.parseInt(propm.group(6));
				startMinute = Integer.parseInt(propm.group(7));

				endTimeOfDay = Integer.parseInt(propm.group(10));
				endMinute = Integer.parseInt(propm.group(11));
			} catch (NumberFormatException e) {}
		}
	}

	
	public String getDate() {
		return String.format("%d-%02d-%02d", year, monthOfYear, dayOfMonth);
	}
	
	public String getStartTime() {
		return String.format("%02d:%02d", startTimeOfDay, startMinute);
	}
	
	public String getEndTime() {
		return String.format("%02d:%02d", endTimeOfDay, endMinute);
	}

	public long getEpochTime(){
		int hour = startTimeOfDay > -1 ? startTimeOfDay : 0;
		int minute = startMinute > -1 ? startMinute : 0;

		return new GregorianCalendar(year, monthOfYear, dayOfMonth, hour, minute).getTimeInMillis()/1000L;
	}
	
	
	public String toString() {
		return getDate().toString() + getStartTimeFormated() + getEndTimeFormated();
	}
	
	public String toFormatedString() {
		return formatDate(type, getDate());
	}

	
	private String getStartTimeFormated() {
		String time = getStartTime().toString();

		if (startTimeOfDay == -1
				|| startMinute == -1 || TextUtils.isEmpty(time))
			return "";
		else
			return " " + time;
	}
	
	private String getEndTimeFormated() {
		String time = getEndTime().toString();

		if (endTimeOfDay == -1
				|| endMinute == -1 || TextUtils.isEmpty(time))
			return "";
		else
			return "-" + time;
	}
	
	
	public static String typeToFormated(TYPE type) {
		switch (type) {
		case Scheduled:
			return "SCHEDULED: ";
		case Deadline:
			return "DEADLINE: ";
		case Timestamp:
			return "";
		default:
			return "";
		}
	}
	
	public static String formatDate(TYPE type, String timestamp) {
		if (TextUtils.isEmpty(timestamp))
			return "";
		else {
			return OrgNodeTimeDate.typeToFormated(type) + "<" + timestamp + ">";
		}
	}

	static public Pattern getTimestampMatcher(OrgNodeTimeDate.TYPE type) {
		final String timestampPattern =  "<([^>]+)(\\d\\d:\\d\\d)>"; // + "(?:\\s*--\\s*<([^>]+)>)?"; for ranged date
//		final String timestampLookbehind = "\\s*(?<!(?:SCHEDULED:|DEADLINE:)\\s?)";

//		String pattern;
//		if(type == OrgNodeTimeDate.TYPE.Timestamp)
//			pattern = timestampLookbehind + "(" + timestampPattern + ")";
//		else

		String pattern = "\\s*(" + OrgNodeTimeDate.typeToFormated(type) + "\\s*" + timestampPattern + ")";

		return Pattern.compile(pattern);
	}

}
