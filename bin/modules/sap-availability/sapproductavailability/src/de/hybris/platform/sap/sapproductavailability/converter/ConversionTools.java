/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapproductavailability.converter;


public class ConversionTools
{

	private static final int FRACTIONAL_PART = 4;


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
		final StringBuilder builder = new StringBuilder(size);
		while (size++ < desiredLength)
		{
			builder.append("0");
		}
		builder.append(inputString);
		return builder.toString();
	}

	/**
	 * Since the format of the quantity is xxx,yyy we need to reparse the string.
	 * 
	 * Format of quantity: [+|-][0123456789]{1,13}\{DecimalPoint}[0123456789]{3} DecimalPoint: {.|,} depending on Locale.
	 * 
	 */
	public static Long convertQuantityToNumber(final String quantity)
	{
		return new Long(quantity.substring(0, quantity.length() - FRACTIONAL_PART).replace("\\.", ""));
	}


	/**
	 * Converted the product code to the internal format. For numeric product ID, add leading zeros so that the length of
	 * the product ID becomes 18 decimal digits. For non-numeric product ID, do not do any change.
	 * 
	 * @param ProductCode
	 * @return Formatted product code
	 */
	protected String convertProductCode(final String ProductCode)
	{
		if (ProductCode.matches("\\d+"))
		{
			return String.format("%018d", Integer.valueOf(ProductCode));
		}
		return ProductCode;
	}
}
