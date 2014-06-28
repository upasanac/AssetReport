package com.adobe.misreport.impl;

import java.util.HashMap;
import java.util.Map;
import javax.jcr.Session;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import com.adobe.misreport.QueryService;
import com.adobe.misreport.constants.MISReportConstant;
import com.adobe.misreport.utils.MISReportUtil;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;

@Component(immediate=true,metatype=false)
@Service(QueryService.class)
public class QueryServiceImpl implements  QueryService{

	@Reference	
	private QueryBuilder queryBuilder;	
	
	@Override
	public SearchResult executeQuery(String lowerBoundDate, String upperBoundDate, String typeOfReport, Session session) {
		
		//Build the predicate Map based on the report type
		Map<String, String> predicateMap = getPredicateMap(lowerBoundDate, upperBoundDate, typeOfReport);
		
		Query query = queryBuilder.createQuery(PredicateGroup.create(predicateMap), session);
		
		return query.getResult();
		
	}	
	
	private static Map<String,String> getPredicateMap(String lowerBoundDate, String upperBoundDate, String typeOfReport)
	{
		Map<String, String> predicateMap = new HashMap<String, String>();
		
		predicateMap.put(MISReportConstant.ORDERBY,MISReportConstant.PATH);
		predicateMap.put(MISReportConstant.LIMIT,MISReportConstant.INFINITY);
		predicateMap.put(MISReportConstant.CHARSET,MISReportConstant.UTF_8);
		predicateMap.put(MISReportConstant.DATE_RANGE_LOWER_OPERATION, ">=");
		predicateMap.put(MISReportConstant.DATE_RANGE_UPPER_OPERATION, "<=");
		predicateMap.put(MISReportConstant.TYPE, MISReportConstant.DAM_ASSET);
		predicateMap.put(MISReportConstant.PATH, MISReportConstant.ASSET_DEFAULT_PATH);
		
		predicateMap.put(MISReportConstant.DATE_RANGE_LOWERBOUND, lowerBoundDate + MISReportConstant.DEFAULT_TIMESTAMP + MISReportUtil.getUTCTimeZoneOffset());
		predicateMap.put(MISReportConstant.DATE_RANGE_UPPERBOUND, upperBoundDate + MISReportConstant.DEFAULT_TIMESTAMP + MISReportUtil.getUTCTimeZoneOffset());
		
		if(typeOfReport.equals(MISReportConstant.ASSET_ADDED)) {
			predicateMap.put(MISReportConstant.PROPERTY_DATE_RANGE, MISReportConstant.JCR_CREATED);
		}
		else if(typeOfReport.equals(MISReportConstant.ASSET_MODIFIED)) {
			predicateMap.put(MISReportConstant.PROPERTY_DATE_RANGE, MISReportConstant.JCR_LAST_MODIFIED);
		}
		
		return predicateMap;	
	}
	

}
