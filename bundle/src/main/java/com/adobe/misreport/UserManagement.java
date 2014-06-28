package com.adobe.misreport;

import javax.jcr.Session;

public interface UserManagement {

	public String getUserName(String userID, Session session);
	public String getEmailAddress(String userID, Session session);
	public String getUserGroup(String reportType);
	public String[] getAllUsersOfGroup(String groupName, Session session);
	
}
