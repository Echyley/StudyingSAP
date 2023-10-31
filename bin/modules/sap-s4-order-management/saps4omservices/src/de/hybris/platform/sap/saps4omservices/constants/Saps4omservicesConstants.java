/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omservices.constants;

import java.util.regex.Pattern;

/**
 * Global class for all Saps4omservices constants. You can add global constants for your extension into this class.
 */
public final class Saps4omservicesConstants extends GeneratedSaps4omservicesConstants
{
	public static final String EXTENSIONNAME = "saps4omservices";
	public static final String SAP_PASSPORT_HEADER_NAME = "SAP-PASSPORT";
	public static final String SORT_ORDER = "saps4omservices.s4om.order.history.sort.order";
	public static final String ORDER_TYPE = "saps4omservices.s4om.order.history.filter.order.type";
	public static final String SOLD_TO = "saps4omservices.s4om.order.history.filter.soldto";
	public static final String SALES_ORG = "saps4omservices.s4om.order.history.filter.salesorg";
	public static final String DISTRIBUTION_CHANNEL = "saps4omservices.s4om.order.history.filter.distChannel";
	public static final String DIVISION = "saps4omservices.s4om.order.history.filter.division";
	public static final String SAPS4OMCARTEARLIESTRETRIEVALDATE_PREFIX = "saps4omcartearliestretrievaldate";
	public static final String DATE_FORMAT = "yyyy-MM-dd";
	public static final Pattern DATE_REGEX_PATTERN = Pattern.compile(".*?(\\d+).*");

	private Saps4omservicesConstants()
	{
		//empty to avoid instantiating this constant class
	}

	// implement here constants used by this extension
}
