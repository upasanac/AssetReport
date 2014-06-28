package com.adobe.misreport;

import javax.jcr.Session;
import com.day.cq.search.result.SearchResult;

public interface QueryService {

	public SearchResult executeQuery(String lowerBoundDate, String upperBoundDate, String typeOfReport, Session session);
}
