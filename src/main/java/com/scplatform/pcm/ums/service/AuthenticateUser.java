/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ums.service;

import java.util.List;

import com.scplatform.pcm.authentication.service.AppContextHelper;
import com.scplatform.pcm.user.entity.Users;
import com.scplatform.pcm.user.service.UserSessionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.scplatform.pcm.authentication.dto.ApplicationContext;
import com.scplatform.pcm.authentication.dto.InvalidUserContext;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticateUser {
	
	static Log logger = LogFactory.getLog(AuthenticateUser.class);

    private UserSessionService userSessionService;
	
	public void authenticate(HttpServletRequest request) throws Exception
	{
		
		HttpSession session = request.getSession();
		if (session.getAttribute("environment") == null) {
			session.setAttribute("environment", System.getProperty("e2.env.subtype"));
		}        
        
		try
		{
			ApplicationContext ac = AppContextHelper.getValidContext(request);
			Users user = userSessionService.findUserByKey(ac.getCurrentUser().getUserKey());
            AppContextHelper.setupSessionContext(user, request);
						
		}
		catch (InvalidUserContext iuc)
		{
			String userid = request.getHeader("iv-user");
			if (userid == null)
			{
				userid = request.getParameter("username");
			}
			if (userid == null)
			{
				logger.info("Login attempted without passing a userid from host "
							+ request.getRemoteHost() + " address:" + request.getRemoteAddr());
				throw new Exception("Login attempted without passing a userid from host "
						+ request.getRemoteHost() + " address:" + request.getRemoteAddr());
			}
			
			logger.info("Login attempted by user " + userid + " from host "
					+ request.getRemoteHost() + " address:" + request.getRemoteAddr());
			
			Users user = null;
			List<Users> users = userSessionService.findAllUsersById(userid);
			if (users.size() == 1)
			{
				user = users.get(0);
			}
			if (user == null)
			{
				logger.error("Login attempted by unknown user: " + userid + " from host "
						+ request.getRemoteHost() + " address:" + request.getRemoteAddr());				
				throw new Exception("Login attempted by unknown user: " + userid + " from host "
						+ request.getRemoteHost() + " address:" + request.getRemoteAddr());
			}
			if (user.getIsEnabled() == false)
			{
				logger.error("Login attempted by disabled user: " + userid + " from host "
						+ request.getRemoteHost() + " address:" + request.getRemoteAddr());				
				throw new Exception("Login attempted by disabled user: " + userid + " from host "
						+ request.getRemoteHost() + " address:" + request.getRemoteAddr());
			}

            userSessionService.setUserLastAccess(user.getUserKey());
			// Load the full users
			user = userSessionService.findUserByKey(user.getUserKey());
            AppContextHelper.setupSessionContext(user, request);
		}
			
		
	}
}
