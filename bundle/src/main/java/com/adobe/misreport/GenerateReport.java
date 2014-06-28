package com.adobe.misreport;

import javax.activation.DataSource;
import javax.jcr.Session;

public interface GenerateReport {

	public DataSource generateReport(String lowerBoundDate, String upperBoundDate, String reportType, Session session) throws Exception;
	
}
