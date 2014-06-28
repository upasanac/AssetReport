package com.adobe.misreport.constants;

public class MISReportConstant {

	public static final String EMPTY_STRING = "";
	public static final String ASSET_ADDED = "assetAdded";
	public static final String ASSET_MODIFIED = "assetModified";
	public static final String DAILY = "Daily";
	public static final String WEEKLY = "Weekly";
	public static final String MONTHLY = "Monthly";
	public static final String TODAY = "today";
	public static final String AUTHOR = "author";
	
	public static final String JCR_CREATED = "jcr:created";
	public static final String JCR_LAST_MODIFIED = "jcr:content/jcr:lastModified";
	
	public static final String ORDERBY = "orderby";
	public static final String PATH = "path";
	public static final String LIMIT = "p.limit";
	public static final String INFINITY = "-1";
	public static final String CHARSET = "_charset_";
	public static final String UTF_8 = "utf-8";
	public static final String DATE_RANGE_LOWERBOUND = "0_daterange.lowerBound";
	public static final String DATE_RANGE_UPPERBOUND = "0_daterange.upperBound";
	public static final String DATE_RANGE_LOWER_OPERATION = "0_daterange.lowerOperation";
	public static final String DATE_RANGE_UPPER_OPERATION = "0_daterange.upperOperation";
	public static final String PROPERTY_DATE_RANGE = "0_daterange.property";
	public static final String TYPE = "type";
	public static final String DAM_ASSET = "dam:Asset";
	public static final String ASSET_DEFAULT_PATH = "/content/dam";
	public static final String DEFAULT_TIMESTAMP = "T00:00:00.000";
	
	//Asset Report email constants
	public static final String SENDER_EMAIL_ADDRESS = "senderEmailAddress";
	public static final String SENDER_NAME = "senderName";
	public static final String REPORT_EMAIL_TEMPLATE = "/etc/notification/email/reportEmailTemplate/emailtemplate.txt";
	public static final String REPORT_FREQUENCY = "reportFrequency";
	public static final String REPORT_TYPE = "reportType";
	public static final String DATE_RANGE = "dateRange";
	public static final String FIRST_NAME = "firstName";
	public static final String UPLOADED = "uploaded";
	public static final String MODIFIED = "modified";
	
	//Asset Report Constants
	public static final String FIELD_TITLE = "TITLE";
	public static final String FIELD_TYPE = "TYPE";
	public static final String FIELD_SIZE = "SIZE";
	public static final String FIELD_ADDED = "ADDED";
	public static final String FIELD_ADDED_BY = "ADDED BY";
	public static final String FIELD_MODIFIED = "MODIFIED";
	public static final String FIELD_MODIFIED_BY = "MODIFIED BY";
	public static final String FIELD_PATH = "PATH";
	public static final String DEFAULT_SHEET_NAME = "assetReport";
	public static final String ASSET_ADDED_SHEET_NAME = "assetadditionreport";
	public static final String ASSET_MODIFIED_SHEET_NAME = "assetmodificationreport";
	public static final String REPORT_FILE_NAME = "assetreport.xlsx";
	
	//user manager constants
	public static final String PROFILE_GIVENNAME = "profile/givenName";
	public static final String PROFILE_FAMILYNAME = "profile/familyName";
	public static final String PROFILE_EMAIL = "profile/email";
	public static final String GROUP_ASSET_ADDED = "report-asset-added";
	public static final String GROUP_ASSET_MODIFIED = "report-asset-modified";
}
