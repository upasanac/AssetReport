package com.adobe.misreport.jobs;

import java.util.Dictionary;
import java.util.Map;
import javax.activation.DataSource;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Modified;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.PropertyOption;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.jcr.api.SlingRepository;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.adobe.misreport.EmailService;
import com.adobe.misreport.GenerateReport;
import com.adobe.misreport.UserManagement;
import com.adobe.misreport.constants.MISReportConstant;
import com.adobe.misreport.utils.MISReportUtil;
import com.day.cq.jcrclustersupport.ClusterAware;

@Component(
		immediate=true, 
		metatype=true,
		label="MIS Daily Asset report Job", 
		description="Scheduled Job to email Asset Daily Report"
)
@Service
@Properties({
    @Property(label = "Enable", name = "enabled", description = "Enable/Disable this JOB", boolValue = false),
    @Property(label = "Scheduler Expression", name = "scheduler.expression", description = "Default is 00 HOURS", value = "0 0 0 1/1 * ? *"),
    @Property(label = "Report Type", name = "reportType", description = "Type of the Report",  options ={
            @PropertyOption(name = MISReportConstant.ASSET_ADDED , value = "Uploaded Asset"),
            @PropertyOption(name = MISReportConstant.ASSET_MODIFIED, value = "Modified Asset")
    })

})
public class MISDailyReportJob implements Runnable, ClusterAware {
	
	private static final Logger log = LoggerFactory.getLogger(MISDailyReportJob.class);

	@Reference
	protected SlingSettingsService slingSettingsService;
	
	@Reference
    private SlingRepository repository;
	
	@Reference
	GenerateReport generateReport;

	@Reference
	EmailService emailService;
	
	@Reference
	UserManagement userManagement;
	
	private Boolean isMasterInstance = Boolean.FALSE;
	private Boolean isAuthorInstance = Boolean.FALSE;
	
	Boolean sendDailyReport = false;	
	String reportType = MISReportConstant.ASSET_ADDED;	
	private final String reportFrequency = MISReportConstant.DAILY;
	
	@Activate
    @Modified
    public void init(final ComponentContext componentContext) {
		Dictionary<String,Object> dictionary = componentContext.getProperties();		
        reportType = (String)dictionary.get(MISReportConstant.REPORT_TYPE);
        sendDailyReport = (Boolean)(dictionary.get("enabled"));
    }	
	
	public void run() {
		
		//run the service only in the master instance in author mode
		log.info("Daily Scheduled Asset Report Job starting >> isMasterInstance:{} and isAuthorInstance:{}.", isMasterInstance, isAuthorInstance);
		Session session = null;
		
        if (isMasterInstance && isAuthorInstance) {
       
        	if(sendDailyReport) {
		        try {
					 session = getSession();
					
					 //TODO- 
					 String lowerBoundDate = "2014-06-26";
					 String upperBoundDate = "2014-06-26";
					 
					 String dateRange = MISReportConstant.TODAY;
						
					 String templatePath = MISReportConstant.REPORT_EMAIL_TEMPLATE;
					 Map<String, String> emailParams = MISReportUtil.buildEmailParams(reportType, reportFrequency, dateRange);					
					 
					 DataSource reportDatasource = generateReport.generateReport(lowerBoundDate, upperBoundDate, reportType, session);
					
					 //Get the UserGroup name for the reporType		
					 String userGroup = userManagement.getUserGroup(reportType);
					
					 String[] authors = userManagement.getAllUsersOfGroup(userGroup, session);
					
					 for(String authorID: authors) {
						emailParams.put(MISReportConstant.FIRST_NAME, userManagement.getUserName(authorID, session));						
						emailService.sendEmail(templatePath, emailParams, session, reportDatasource, userManagement.getEmailAddress(authorID, session));					
					 }
											 
				} catch(Exception e) {
					 log.error("[Exception]",e);
				} finally {
					if(session.isLive())
						session.logout();
				}
		        
		        log.info("Daily Asset Report Job finished sending {} reports", reportType);
        	}
        	else {
        		log.info("Daily Asset Report Job not executed>> Enable it from the AEM Felix console to execute");
        	}
       }
        
	}

	@Override 
	public void bindRepository(String repositoryId, String clusterId, boolean isMaster) {
		isMasterInstance = isMaster;
    	isAuthorInstance = slingSettingsService.getRunModes().contains(MISReportConstant.AUTHOR);
		
	}

	@Override
	public void unbindRepository() {
		isMasterInstance = Boolean.FALSE;
    	isAuthorInstance = Boolean.FALSE;		
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
