package com.adobe.misreport.schedulers;

import com.adobe.misreport.constants.MISReportConstant;
import com.adobe.misreport.jobs.ReportJob;
import com.adobe.misreport.jobs.WeeklyReportJob;
import org.apache.felix.scr.annotations.*;
import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Dictionary;

/**
 * Created by prajesh on 6/29/2014.
 */
@Component(immediate = true, policy = ConfigurationPolicy.REQUIRE, metatype = true)
@Properties({
        @Property(name = "enabled", boolValue = false),
        @Property(name = "scheduler.expression"),
        @Property(name = "reportType", options ={
                @PropertyOption(name = "Uploaded Assets" , value = MISReportConstant.ASSET_ADDED),
                @PropertyOption(name="Modified Assets", value = MISReportConstant.ASSET_MODIFIED)
        })

})
public class WeeklyAssetReportScheduler {
    @Reference
    private Scheduler scheduler;





    private Logger logger = LoggerFactory.getLogger(DailyAssetReportScheduler.class);



    @Activate
    public void init(final ComponentContext componentContext)
    {
        BundleContext bundleContext = componentContext.getBundleContext();
        logger.info("Weekly Scheduler configured...");

        Dictionary<String,String> dictionary = componentContext.getProperties();

        logger.info("Scheduler expression = " + dictionary.get("scheduler.expression"));

        String reportType = dictionary.get("reportType");
       // Boolean enabled = Boolean.getBoolean(dictionary.get("enabled"));
        try {

            ReportJob reportJob = new ReportJob("Weekly", reportType);

            logger.info("Creating report job");

            if(true) {
                this.scheduler.addJob("weeklyReport", reportJob , null, dictionary.get("scheduler.expression"), true);
            }
        }catch(Exception e)
        {
            logger.error("Scheduler job error : "+e);
        }
    }


}
