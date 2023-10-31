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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An input stream for reading the fragmented ImpEx file content received from DataHub.
 */
public class FragmentedImpExInputStream extends InputStream
{
	private static final Logger LOGGER = LoggerFactory.getLogger(FragmentedImpExInputStream.class);

	private ImpExFragment[] fragments;
	private int currentFragmentNum;
	private InputStream currentFragmentInputStream;

	/**
	 * Instantiates this input stream.
	 *
	 * @param f a list of ImpEx fragments to read content from. This stream will read the fragments in the same order
	 * they received in the list.
	 */
	public FragmentedImpExInputStream(final List<ImpExFragment> f)
	{
		fragments = f != null ? f.toArray(new ImpExFragment[f.size()]) : noFragments();
		currentFragmentNum = 0;
	}

	@Override
	public int read()
	{
		int byteRead = -1;
		while (byteRead == -1 && currentFragmentNum < fragments.length)
		{
			try
			{
				byteRead = readFrom(currentInputStream());
			}
			catch(final IOException e)
			{
				throw new IOExceptionWrapper(e);
			}
		}
		return byteRead;
	}

	/**
	 * Override in order to catch custom exception and rethrow IOException.
	 *
	 * This is necessary because the default implementation swallows IOException silently
	 * which causes MediaUtil.copy() to continue to read as it expects more data to arrive.
	 *
	 * @param b
	 * @param off
	 * @param len
	 * @return The amount of bytes that were read.
	 * @throws IOException
	 */
	@Override
	public int read(final byte[] b, final int off, final int len) throws IOException
	{
		try
		{
			return super.read(b, off, len);
		}
		catch(final IOExceptionWrapper wrapper)
		{
			LOGGER.trace(wrapper.getMessage(), wrapper);
			throw (IOException)wrapper.getCause();
		}
	}

	private InputStream currentInputStream() throws IOException
	{
		if (currentFragmentInputStream == null)
		{
			currentFragmentInputStream = fragments[currentFragmentNum].getContentAsInputStream();
		}
		return currentFragmentInputStream;
	}

	private int readFrom(final InputStream in) throws IOException
	{
		final int val = in.read();
		if (val == -1)
		{
			closeCurrentFragmentStream();
			currentFragmentNum++;
		}
		return val;
	}

	@Override
	public int available() throws IOException
	{
		return currentFragmentInputStream != null ? currentFragmentInputStream.available() : 0;
	}

	@Override
	public void close() throws IOException
	{
		closeCurrentFragmentStream();
		fragments = noFragments();
	}

	private void closeCurrentFragmentStream() throws IOException
	{
		if (currentFragmentInputStream != null)
		{
			currentFragmentInputStream.close();
			currentFragmentInputStream = null;
		}
	}

	private static ImpExFragment[] noFragments()
	{
		return new ImpExFragment[0];
	}

	@Override
	public boolean markSupported()
	{
		return false;
	}

	/**
	 * IOExceptionWrapper wraps the IOException in a RuntimeException
	 */
	private static class IOExceptionWrapper extends RuntimeException
	{
		IOExceptionWrapper(final IOException e)
		{
			super(e);
		}
	}
}
