package com.adobe.misreport.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.jcr.api.SlingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.adobe.misreport.UserManagement;
import com.adobe.misreport.constants.MISReportConstant;

@Component(immediate=true,metatype=false)
@Service(UserManagement.class)
public class UserManagementImpl implements UserManagement {

	@Reference
	SlingRepository repository;
	
	Logger log = LoggerFactory.getLogger(UserManagementImpl.class);
	
	public String getUserName(String userID, Session session) {
		String name = MISReportConstant.EMPTY_STRING;
		try {			
			UserManager userManager = getUserManager(session);			
			final Authorizable authorizable = userManager.getAuthorizable(userID);		
			if(authorizable != null && authorizable.hasProperty(MISReportConstant.PROFILE_GIVENNAME))
				name =  authorizable.getProperty(MISReportConstant.PROFILE_GIVENNAME)[0].getString();
			else if (authorizable != null && authorizable.hasProperty(MISReportConstant.PROFILE_FAMILYNAME))
				name =  authorizable.getProperty(MISReportConstant.PROFILE_FAMILYNAME)[0].getString();
		}
		catch(Exception e) {
			log.error("[Exception]", e);
		}
		
		return name;
	}

	public String getEmailAddress(String userID, Session session) {
		String email = MISReportConstant.EMPTY_STRING;
		try {			
			UserManager userManager = getUserManager(session);			
			final Authorizable authorizable = userManager.getAuthorizable(userID);		
			if(authorizable != null && authorizable.hasProperty(MISReportConstant.PROFILE_EMAIL))
				email =  authorizable.getProperty(MISReportConstant.PROFILE_EMAIL)[0].getString();
		}
		catch(Exception e) {
			log.error("[Exception]", e);
		}
		
		return email;
		
	}

	public String getUserGroup(String reportType) {
		
		if(reportType.equals(MISReportConstant.ASSET_ADDED)) {
			return MISReportConstant.GROUP_ASSET_ADDED;
		}
		else if(reportType.equals(MISReportConstant.ASSET_MODIFIED)) {
			return MISReportConstant.GROUP_ASSET_MODIFIED;
		}
		else {
			return MISReportConstant.EMPTY_STRING;
		}
	}

	public String[] getAllUsersOfGroup(String groupName, Session session) {
		
		List<String> userList = new ArrayList<String>();
		try {			
			UserManager userManager = getUserManager(session);			
			
			Group authorGroup = (Group) userManager.getAuthorizable(groupName);
			
			if(authorGroup != null) {
				Iterator<Authorizable> authors = authorGroup.getMembers();
				
				while(authors.hasNext()) {
					userList.add(authors.next().getID());
				}
			}
		}
		catch(Exception e) {
			log.error("[Exception]", e);
		}
		
		return userList.toArray(new String[userList.size()]);
	}

	private UserManager getUserManager(Session session) throws UnsupportedRepositoryOperationException, RepositoryException {
		 UserManager userManager = ((JackrabbitSession) session).getUserManager();	
		 return userManager;
	}
}
