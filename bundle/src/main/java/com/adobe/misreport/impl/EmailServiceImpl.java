
package com.adobe.misreport.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.activation.DataSource;
import javax.jcr.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrLookup;
import org.apache.commons.mail.HtmlEmail;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.adobe.misreport.EmailService;
import com.adobe.misreport.constants.MISReportConstant;
import com.day.cq.commons.mail.MailTemplate;
import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;

@Component(label = "MIS E-mail Service",
    description = "A Generic Email service that sends an email to a given list of recipients.")
@Service(EmailService.class)
public final class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);
 
    @Reference
    private MessageGatewayService messageGatewayService;

    
    public boolean sendEmail(final String templatePath, final Map<String, String> emailParams,
    		Session session,  DataSource reportDatasource, final String... recipients) {
    	
        if (recipients == null || recipients.length <= 0) {
            throw new IllegalArgumentException("Invalid Recipients");
        }

        List<InternetAddress> addresses = new ArrayList<InternetAddress>(recipients.length);
        for (String recipient : recipients) {
            try {
                addresses.add(new InternetAddress(recipient));
            } catch (AddressException e) {
                log.warn("Invalid email address {} passed to sendEmail(). Skipping.", recipient);
            }
        }
        InternetAddress[] iAddressRecipients = addresses.toArray(new InternetAddress[addresses.size()]);
        
        return  sendEmail(templatePath, emailParams, session, reportDatasource, iAddressRecipients);

    }


    public boolean sendEmail(final String templatePath, final Map<String, String> emailParams,
    		Session session, DataSource reportDatasource, final InternetAddress... recipients) {

        if (recipients == null || recipients.length <= 0) {
            throw new IllegalArgumentException("Invalid Recipients");
        }

        if (StringUtils.isBlank(templatePath)) {
            throw new IllegalArgumentException("Template path is null or empty");
        }
        HtmlEmail email = getEmail(templatePath, emailParams, session);
        
        if (email == null) {
            throw new IllegalArgumentException("Error while creating template");
        }

        MessageGateway<HtmlEmail> messageGateway = messageGatewayService.getGateway(HtmlEmail.class);
        
        try {
			email.setTo(Arrays.asList(recipients));
			email.attach(reportDatasource, MISReportConstant.REPORT_FILE_NAME, MISReportConstant.EMPTY_STRING);
			messageGateway.send(email);
		} catch (Exception e) {
			log.error("[Exception] Email sent failed", e);
			 return false;
		}

        return true;
    }


    private HtmlEmail getEmail(String templatePath, Map<String, String> emailParams, Session session) {
        
        try {

           final MailTemplate mailTemplate = MailTemplate.create(templatePath, session);

           if (mailTemplate == null) {
               log.warn("Email template at {} could not be created.", templatePath);
               return null;
           }

           final HtmlEmail email = mailTemplate.getEmail(StrLookup.mapLookup(emailParams), HtmlEmail.class);

           if (emailParams.containsKey(MISReportConstant.SENDER_EMAIL_ADDRESS)
                    && emailParams.containsKey(MISReportConstant.SENDER_NAME)) {
                email.setFrom(emailParams.get(MISReportConstant.SENDER_EMAIL_ADDRESS),
                        emailParams.get(MISReportConstant.SENDER_NAME));
           } else if (emailParams.containsKey(MISReportConstant.SENDER_EMAIL_ADDRESS)) {
                email.setFrom(emailParams.get(MISReportConstant.SENDER_EMAIL_ADDRESS));
           }

           return email;

        } catch (Exception e) {
            log.error("Unable to construct email from template " + templatePath, e);
        } 

        return null;
    }

}
