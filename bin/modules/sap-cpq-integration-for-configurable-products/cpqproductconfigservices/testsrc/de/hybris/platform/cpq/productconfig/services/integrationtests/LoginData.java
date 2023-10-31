/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services.integrationtests;

/**
 * Simple POJO for passing on login data in tests
 */
public class LoginData
{
	/**
	 * default constructor, can be used if no login is required for the test
	 */
	public LoginData()
	{
		this.executeLogin = false;
		this.userName = null;
		this.password = null;
	}

	/**
	 * constructor to be used if a login is required for the test
	 *
	 * @param userName
	 * @param password
	 */
	public LoginData(final String userName, final String password)
	{
		this.executeLogin = true;
		this.userName = userName;
		this.password = password;
	}

	/**
	 * only if <code>true</code> a login is executed
	 */
	public final boolean executeLogin;
	/**
	 * user name to be used for tests
	 */
	public final String userName;
	/**
	 * password to be used for tests
	 */
	public final String password;
}


