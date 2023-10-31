/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyab2bwebservices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Simple test class to demonstrate how to include utility classes to your webmodule.
 */
public class Gigyab2bwebservicesWebHelper
{
	/** Edit the local|project.properties to change logging behavior (properties log4j.*). */
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(Gigyab2bwebservicesWebHelper.class.getName());

	private Gigyab2bwebservicesWebHelper()
	{
	}

	public static final String getTestOutput()
	{
		return "testoutput";
	}
}
