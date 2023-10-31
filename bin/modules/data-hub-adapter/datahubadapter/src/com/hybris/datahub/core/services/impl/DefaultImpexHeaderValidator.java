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

import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.impex.jalo.ImpExReader;
import de.hybris.platform.util.CSVConstants;

import com.hybris.datahub.core.services.ImpexHeaderValidator;

public class DefaultImpexHeaderValidator implements ImpexHeaderValidator
{
	@Override
	public void validateImpexHeader(final String header, final String macros) throws ImpExException
	{
		final String headerLine = (macros + CSVConstants.DEFAULT_LINE_SEPARATOR + header).trim();
		ImpExReader.parseHeader(headerLine);
	}
}
