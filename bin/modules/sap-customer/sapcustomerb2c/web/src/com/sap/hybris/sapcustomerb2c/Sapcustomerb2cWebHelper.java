/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcustomerb2c;

/**
 * Simple test class to demonstrate how to include utility classes to your webmodule.
 */
public class Sapcustomerb2cWebHelper
{

	private Sapcustomerb2cWebHelper() {
		throw new IllegalStateException("Utility class");
	}

	public static final String getTestOutput()
	{
		return "testoutput";
	}
}
