/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.smarteditwebservices;


/**
 * Simple test class to demonstrate how to include utility classes to your webmodule.
 */
public final class SmarteditwebservicesWebHelper
{
	// Prevent initializing instance since only static methods exist
	private SmarteditwebservicesWebHelper(){

	}

	public static final String getTestOutput()
	{
		return "testoutput";
	}
}
