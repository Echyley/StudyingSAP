/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapinvoiceaddon.utils;

import org.apache.commons.lang3.StringUtils;

public class SapInvoiceAddonUtils {
    
        private SapInvoiceAddonUtils(){
            //private constructor to hide public constructor
        }

	public static String filter(final String value) throws NumberFormatException {

		final String encodedInvoiceCode = StringUtils.isNotBlank(value) ? filterString(value) : value;
		boolean numberFormatCheck = false;
		if(null!=encodedInvoiceCode)
		{
		 numberFormatCheck = StringUtils.isNotBlank(encodedInvoiceCode) ? checkNumberFormat(encodedInvoiceCode): false;
		}
		if (numberFormatCheck) {
			return encodedInvoiceCode;
		} else {
			throw new NumberFormatException("Invoice code not valid");
		}
	}

	public static boolean checkNumberFormat(String encodedInvoiceCode) {
		String numbetRegex = "[0-9]+";
		return encodedInvoiceCode.matches(numbetRegex);
	}
	
	
	private static String filterString(final String value)
	{
		if (value == null)
		{
			return null;
		}
		String sanitized = value;
		sanitized = sanitized.replaceAll("eval\\((.*)\\)", "");
		sanitized = sanitized.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
		return sanitized;
	}
}
