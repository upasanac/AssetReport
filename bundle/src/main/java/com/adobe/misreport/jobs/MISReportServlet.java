package com.adobe.misreport.jobs;

import java.io.IOException;
import java.util.Map;

import javax.activation.DataSource;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.ServletException;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.jcr.api.SlingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.adobe.misreport.EmailService;
import com.adobe.misreport.GenerateReport;
import com.adobe.misreport.UserManagement;
import com.adobe.misreport.constants.MISReportConstant;
import com.adobe.misreport.utils.MISReportUtil;
 
@SlingServlet(paths="/bin/report/assetreport",extensions="json",methods="GET")
public class MISReportServlet extends SlingSafeMethodsServlet {

	private static final long serialVersionUID = 1L;
	
	private static final Logger log = LoggerFactory.getLogger(MISReportServlet.class);
	
	@Reference
	GenerateReport generateReport;

	@Reference
	EmailService emailService;
	
	@Reference
	UserManagement userManagement;
	
	@Reference
    private SlingRepository repository;
	
	
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)  throws ServletException, IOException {
		
		Session session = getSession();
		
		String reportType = MISReportConstant.ASSET_ADDED;
		String reportFrequency = MISReportConstant.DAILY;
		
		String upperBoundDate = "2014-06-26";
		String lowerBoundDate = "2014-06-01";
		String dateRange = "2014-06-01 to 2014-06-26";
		
		String templatePath = MISReportConstant.REPORT_EMAIL_TEMPLATE;
		Map<String, String> emailParams = MISReportUtil.buildEmailParams(reportType, reportFrequency, dateRange);
		
		try {
			DataSource reportDatasource = generateReport.generateReport( lowerBoundDate, upperBoundDate,reportType, session);
			
			
			
			//Get the UserGroup name for the report	type		
			String userGroup = userManagement.getUserGroup(reportType);
			
			String[] authors = userManagement.getAllUsersOfGroup(userGroup, session);
			
			for(String authorID: authors) {
				
				emailParams.put(MISReportConstant.FIRST_NAME, userManagement.getUserName(authorID, session));
				
				emailService.sendEmail(templatePath, emailParams, session, reportDatasource, userManagement.getEmailAddress(authorID, session));
			
			}
		
		}
		catch(Exception e) {
			log.error("[Exception]",e);
		}
		
		
	}
	
	
	/**
	 * 
	 * @return
	 * @throws LoginException
	 */
	private Session getSession()  {
		Session session=null;
		try {
			session = repository.loginAdministrative(null);
		} catch (RepositoryException e) {
			log.error("[RepositoryException]",e);
		}
	
		return session;
	}
}
