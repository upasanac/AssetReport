package com.adobe.misreport.jobs;

import com.adobe.misreport.EmailService;
import com.adobe.misreport.GenerateReport;
import com.adobe.misreport.UserManagement;
import com.adobe.misreport.constants.MISReportConstant;
import com.adobe.misreport.utils.MISReportUtil;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.jcr.api.SlingRepository;
import org.apache.sling.settings.SlingSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataSource;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.Map;

/**
 * Created by prajesh on 6/29/2014.
 */

@Component(immediate = true)
@Service(Runnable.class)
public class ReportJob implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(ReportJob.class);

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


    private String reportType = MISReportConstant.ASSET_ADDED;
    private String frequency;

    public ReportJob()
    {

    }

    public ReportJob(String frequency, String reportType)
    {
        this.frequency = frequency;
        this.reportType = reportType;
    }


    public void run() {

        //run the service only in the master instance in author mode
        Session session = null;

        log.info("Job running for "+frequency+" report for reportType="+reportType);

                try {
                    //session = getSession();

                    String reportFrequency = MISReportConstant.DAILY;

                    //TODO-
                    String lowerBoundDate = "2014-06-26";
                    String upperBoundDate = "2014-06-26";

                    String dateRange = MISReportConstant.TODAY;

                    log.info("Sending EMAIL report...");

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
                    if(session!=null && session.isLive())
                        session.logout();
                }

                log.info(frequency+" Asset Report Job finished sending {} reports", reportType);
            }




    /**
     *
     * @return
     * @throws org.apache.sling.api.resource.LoginException
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
