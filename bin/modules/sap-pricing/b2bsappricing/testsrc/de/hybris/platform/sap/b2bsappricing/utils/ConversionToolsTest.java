/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.b2bsappricing.utils;


import java.text.SimpleDateFormat;
import java.util.Locale;

import junit.framework.TestCase;

import org.junit.Assert;


@SuppressWarnings("javadoc")
public class ConversionToolsTest extends TestCase
{

	public void testCutOfZeros()
	{
		Assert.assertEquals("00001", ConversionTools.addLeadingZerosToNumericID("0001",5));
		Assert.assertEquals("0012345678", ConversionTools.addLeadingZerosToNumericID("12345678",10));
 	}

}
