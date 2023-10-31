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

import com.google.common.base.Preconditions;
import com.hybris.datahub.core.dto.ItemImportTaskData;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

/**
 * Default implementation of the FragmentReader interface.
 */
public class DefaultFragmentReader implements FragmentReader
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultFragmentReader.class);
	private static final int ERROR_CODE = 200001;

	private DataHubFacade facade;

	@Override
	public List<ImpExFragment> readScriptFragments(final ItemImportTaskData ctx) throws ImpExException
	{
		Preconditions.checkArgument(ctx != null, "ItemImportTaskData cannot be null");
		Preconditions.checkArgument(ctx.getImpexMetaData() != null, "ImpexMetaData cannot be null");
		final List<ImpExFragment> fragments = new ArrayList<>();

		try (final LineNumberReader reader = new LineNumberReader(
				new InputStreamReader(new ByteArrayInputStream(ctx.getImpexMetaData()))))
		{
			for (String line = reader.readLine(); line != null; line = reader.readLine())
			{
				if (!line.isEmpty())
				{
					processLine(line, fragments, ctx);
				}
			}
		}
		catch (final IOException e)
		{
			throw new ImpExException(e, "Failed to read ImpEx script", ERROR_CODE);
		}
		return fragments;
	}

	private void processLine(final String line, final List<ImpExFragment> fragments, final ItemImportTaskData taskData)
	{
		if (!addLineToCurrentFragment(line, fragments, taskData))
		{
			addLineToNewFragment(line, fragments);
		}
	}

	private static boolean addLineToCurrentFragment(final String line, final List<ImpExFragment> fragments,
	                                                final ItemImportTaskData ctx)
	{
		final ImpExFragment currentFragment = fragments.isEmpty() ? null : fragments.get(fragments.size() - 1);
		try
		{
			return currentFragment != null && currentFragment.addLine(line, fragments);
		}
		catch (final ImpexValidationException ex)
		{
			LOGGER.trace(ex.getMessage(), ex);
			ctx.getHeaderErrors().addAll(ex.getErrors());
			if (!fragments.isEmpty())
			{
				fragments.remove(fragments.size() - 1);
			}
			return false;
		}
	}

	private void addLineToNewFragment(String line, final List<ImpExFragment> fragments)
	{
		final ImpExFragment[] fragmentsToTry = getFragmentsToTry();

		for (final ImpExFragment frag : fragmentsToTry)
		{
			if (frag.addLine(line, fragments))
			{
				fragments.add(frag);
				break;
			}
		}
	}

	protected ImpExFragment[] getFragmentsToTry()
	{
		return new ImpExFragment[]{ new DataHubDataFragment(facade), new ImpexMacroFragment(), new ConstantTextFragment() };
	}

	/**
	 * Injects facade implementation into this reader.
	 *
	 * @param dataHubFacade facade implementation to use.
	 */
	@Required
	public void setDataHubFacade(final DataHubFacade dataHubFacade)
	{
		facade = dataHubFacade;
	}

	public DataHubFacade getFacade()
	{
		return facade;
	}
}
