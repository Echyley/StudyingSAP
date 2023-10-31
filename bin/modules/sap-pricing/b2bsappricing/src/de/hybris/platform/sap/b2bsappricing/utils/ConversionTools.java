/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.b2bsappricing.utils;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;



/**
 * Common conversion tools
 *
 */
public  class ConversionTools
{

	/**
	 * Method adds leading zeros to a numeric string based on a specified total length
	 *
	 * @param inputString
	 *           contains the numeric String
	 * @param desiredLength
	 *           specifies the total desired length of the string
	 * @return String with leading zeroes appended
	 *
	 */
	public static String addLeadingZerosToNumericID(final String inputString, final int desiredLength)
	{
		int size = inputString.length();

		//check if inputString is numeric
		for (int i = 0; i < size; i++)
		{
			final char ch = inputString.charAt(i);
			if (!Character.isDigit(ch))
			{
				return inputString;
			}
		}
		//if inputString is already the desired length, keep as is
		if (size >= desiredLength)
		{
			return inputString;
		}

		//pad with missing zeroes on the left
		final StringBuilder buffer = new StringBuilder(size);
		while (size++ < desiredLength)
		{
			buffer.append("0");
		}
		buffer.append(inputString);
		return buffer.toString();
	}
}
