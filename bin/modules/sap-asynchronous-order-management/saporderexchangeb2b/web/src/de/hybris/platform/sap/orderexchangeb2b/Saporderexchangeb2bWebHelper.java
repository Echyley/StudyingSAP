/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.orderexchangeb2b;

import org.apache.log4j.Logger;


/**
 * Simple test class to demonstrate how to include utility classes to your webmodule.
 */
public class Saporderexchangeb2bWebHelper
{
	private Saporderexchangeb2bWebHelper() {
		throw new IllegalStateException("Utility class");
	}

	/** Edit the local|project.properties to change logging behavior (properties log4j.*). */
	private static final Logger LOG = Logger.getLogger(Saporderexchangeb2bWebHelper.class.getName());

	public static final String getTestOutput()
	{
		return "testoutput";
	}
}
