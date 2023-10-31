/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.impl;

import de.hybris.platform.sap.productconfig.runtime.cps.CPSConflictTextParser;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;


/**
 * Default implementation of {@link CPSConflictTextParser}
 */
public class CPSConflictTextParserImpl implements CPSConflictTextParser
{
	private static final Pattern replacePattern = Pattern.compile("\\x26[a-zA-Z]*\\x26");

	@Override
	public String parseConflictText(final String rawText)
	{
		return rawText != null ? replacePattern.matcher(rawText).replaceAll(StringUtils.EMPTY) : StringUtils.EMPTY;
	}

}
