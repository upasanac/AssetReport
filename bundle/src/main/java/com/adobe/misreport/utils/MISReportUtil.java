package com.adobe.misreport.utils;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import org.apache.commons.lang.time.DurationFormatUtils;
import com.adobe.misreport.constants.MISReportConstant;

public class MISReportUtil {

	public static String getUTCTimeZoneOffset() {
		
    	Calendar calendar = Calendar.getInstance();
    	
    	TimeZone timeZone = TimeZone.getTimeZone(calendar.getTimeZone().getID());
    	int offsetInMS = timeZone.getRawOffset();
    	
    	return offsetInMS >= 0 ? "+" + DurationFormatUtils.formatDuration(offsetInMS, "HH:mm") 
    								 : DurationFormatUtils.formatDuration(offsetInMS, "HH:mm");
	}
	
	
    public static  Map<String, String> buildEmailParams(String reportType, String reportFrequency, String dateRange) {
		
    	Map<String, String> emailParamMap = new HashMap<String, String>();
    	
    	if(reportType.equals(MISReportConstant.ASSET_ADDED)) {
    		emailParamMap.put(MISReportConstant.REPORT_TYPE, MISReportConstant.UPLOADED);
    	}
    	else if(reportType.equals(MISReportConstant.ASSET_MODIFIED)) {
    		emailParamMap.put(MISReportConstant.REPORT_TYPE, MISReportConstant.MODIFIED);
    	}
    	
    	
    	if(reportFrequency.equals(MISReportConstant.DAILY)) {
    		emailParamMap.put(MISReportConstant.REPORT_FREQUENCY, MISReportConstant.DAILY);
    	}
    	else if(reportFrequency.equals(MISReportConstant.WEEKLY)) {
    		emailParamMap.put(MISReportConstant.REPORT_FREQUENCY, MISReportConstant.WEEKLY);
    	}
    	else if(reportFrequency.equals(MISReportConstant.MONTHLY)) {
    		emailParamMap.put(MISReportConstant.REPORT_FREQUENCY, MISReportConstant.MONTHLY);
    	}
    	
    	emailParamMap.put(MISReportConstant.DATE_RANGE, dateRange);
    	
		return emailParamMap;
	}
}
