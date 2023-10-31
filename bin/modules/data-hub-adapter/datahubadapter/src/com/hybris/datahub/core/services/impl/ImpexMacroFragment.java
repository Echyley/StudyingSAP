/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.datahub.core.services.impl;

import de.hybris.platform.util.CSVConstants;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

public class ImpexMacroFragment implements ImpExFragment
{
	private final StringBuilder buffer;

	/**
	 * Instantiates a script fragment with constant content.
	 */
	public ImpexMacroFragment()
	{
		buffer = new StringBuilder();
	}

	@Override
	public boolean addLine(final String line)
	{
		return addLine(line, null);
	}

	@Override
	public boolean addLine(final String line, final List<ImpExFragment> fragments)
	{
		if (lineShouldBeAdded(line))
		{
			buffer.append(line).append(CSVConstants.HYBRIS_LINE_SEPARATOR);
			return true;
		}
		return false;
	}

	private static boolean lineShouldBeAdded(final String line)
	{
		return line != null && line.trim().startsWith("$");
	}

	@Override
	public String getContent()
	{
		return buffer.toString();
	}

	@Override
	public InputStream getContentAsInputStream()
	{
		return new ByteArrayInputStream(getContent().getBytes());
	}
}
