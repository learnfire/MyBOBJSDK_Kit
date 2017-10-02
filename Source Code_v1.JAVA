/*
  Program Name:   Add_EntUser_Alias.java
   Original Author : SAP (note#1804839 -- How to add or remove an Enterprise alias through a script in SAP BI 4.0)
   Modification Author:  Srikar Garisa 
   Modified : Yes (Modified SAP standard code to fill security gap in standard  SAP sample code)
   Requirement Detail: Background Scheduling, Enterprise Alias password should not be visible in source code and unknown
   Version: 1
  
   DATE: 	  09/21/2015 

   FUNCTION:  Adds Enterprise Alias to SAP user aliases which doesn't have corresponding Enterprise type alias 
              
   INPUT:     no external input is required. One of program properties like Program 
   OUTPUT:    Adds enterprise aliases to those accounts which have  SAP aliases and no associated Enterprise aliases     
   			  Program output can be seen from History once it execution completes
   NOTES:   
     Eclipse release info: Version: Mars Release (4.5.0)
     From Eclipse export to .jar and import the jar into BOBJ 
     Coding Main method gets executed only in Eclipse mode
     Coding in Run method gets executed only in BOBJ mode i.e when its run\scheduled in BOBJ 
              
  */


package com.MyBobjKit;	
import com.crystaldecisions.sdk.framework.*;
import com.crystaldecisions.sdk.plugin.desktop.user.*;
import com.crystaldecisions.sdk.occa.infostore.*;
import com.crystaldecisions.sdk.exception.SDKException;
import java.util.*;
import java.util.UUID;


public  class Add_EntUser_Alias implements com.crystaldecisions.sdk.plugin.desktop.program.IProgramBase {
	
	public Add_EntUser_Alias() 
	{
		/* Constructor code here */
	}
	
	
	public static void main(String args[])
	{
		/* main method code here. Skipped in BI mode. Works only in eclipse mode */
	}

	
	@Override
	public void run(IEnterpriseSession sessionMgr, IInfoStore infoStore, String[] arg2) throws SDKException {
		
		int min_id;
		
		try {
		
			
			min_id = 0;

			for(int safety = 0 ; safety < 1000 ; safety++) {
				IInfoObjects users = null;
				boolean need_to_update = false;

				try {
					users = infoStore.query("SELECT TOP 100 STATIC "
							+ " FROM CI_SYSTEMOBJECTS "
							+ " WHERE SI_KIND='User' And SI_ID > " + min_id + " "
							+ " ORDER BY SI_ID ASC ");
				} catch (SDKException e) {
					
					e.printStackTrace();
				}

				if(users.size() == 0) {
					break;
				}

				for(Iterator iuser = users.iterator() ; iuser.hasNext() ; ) {  
					IUser user;
					IUserAliases userAliases;
					boolean isSAP;
					boolean hasEnterprise;

					user = (IUser) iuser.next();
					userAliases = user.getAliases();
					
					isSAP = false;
					hasEnterprise = false;

					for(Iterator ialias = userAliases.iterator() ; ialias.hasNext() ; ) {
						IUserAlias userAlias;
						String authentication;
						
						
						userAlias = (IUserAlias) ialias.next(); 
						authentication = userAlias.getAuthentication();
						
					if(authentication.equals("secSAPR3"))
						{
						isSAP = "secSAPR3".equals(authentication);
						}
						
						if(authentication.equals("secEnterprise")) 
						{
						hasEnterprise = "secEnterprise".equals(authentication);
						}
						
						System.out.println("Checking userid: " + userAlias.getName());
					}
					
					String uuid = null;
					
					
					if(isSAP && !hasEnterprise) { 
						
						
						System.out.println("'" + user.getTitle() + "' - adding Enterprise Alias.<BR>");
						userAliases.addNew("secEnterprise:" + user.getTitle(), false);
												
						// Code to generate random string and set it as new password for Enterprise Alias 
						uuid = UUID.randomUUID().toString();
						//System.out.println("uuid = " + uuid);
						user.setNewPassword(uuid);
						
					    // Set random password ends 
					 						 
						need_to_update = true;
					}

					min_id = user.getID();
				}


				if(need_to_update) {                

					try {
						infoStore.commit(users);
					} catch (SDKException e) {
						
						e.printStackTrace();
					}
				} 
			} 
			

		} finally {
	
		}
		
				
	} 
	
}
