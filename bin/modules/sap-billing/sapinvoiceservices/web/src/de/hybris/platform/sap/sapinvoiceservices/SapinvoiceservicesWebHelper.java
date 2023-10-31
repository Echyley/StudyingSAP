/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapinvoiceservices;

import org.apache.log4j.Logger;


/**
 * Simple test class to demonstrate how to include utility classes to your webmodule.
 */
public class SapinvoiceservicesWebHelper
{
        private SapinvoiceservicesWebHelper(){
            //private constructor to hide public constructor
        }
	/** Edit the local|project.properties to change logging behavior (properties log4j.*). */
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(SapinvoiceservicesWebHelper.class.getName());

	public static final String getTestOutput()
	{
		return "testoutput";
	}
}
