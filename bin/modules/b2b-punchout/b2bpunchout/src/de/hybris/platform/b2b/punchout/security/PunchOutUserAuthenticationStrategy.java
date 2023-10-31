/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Authentication strategy for Punch Out users.
 */
public interface PunchOutUserAuthenticationStrategy
{

	/**
	 * Authenticates a user into the system.
	 * 
	 * @param userId
	 *           the userId
	 * @param request
	 *           the HTTP request
	 * @param response
	 *           the HTTP response
	 */
	void authenticate(String userId, HttpServletRequest request, HttpServletResponse response);

	/**
	 * Logs out a user from the system.
	 */
	void logout();

}
