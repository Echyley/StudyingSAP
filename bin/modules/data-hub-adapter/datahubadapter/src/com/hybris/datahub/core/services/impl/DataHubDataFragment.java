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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;
import com.hybris.datahub.core.facades.ImportError;

/**
 * A fragment containing data being loaded from the Data Hub
 */
public class DataHubDataFragment implements ImpExFragment
{
	private static final String HEADER = "HEADER";

	private final DataHubFacade dataHub;
	private final Map<String, String> headers;
	private String url;
	private final StringBuilder impexHeader;
	private final StringBuilder impexBody;

	/**
	 * Instantiates this fragment.
	 *
	 * @param daHub implementation of the facade for communicating to the Data Hub.
	 */
	public DataHubDataFragment(final DataHubFacade daHub)
	{
		Preconditions.checkArgument(daHub != null, "DataHubFacade cannot be null");
		dataHub = daHub;
		url = "";
		headers = new HashMap<>();
		impexHeader = new StringBuilder();
		impexBody = new StringBuilder();
	}

	private static boolean lineIsAUrlComment(final String line)
	{
		return isSpecialComment(line, "URL");
	}

	private static String commentPayload(final String line, final String type)
	{
		final String signature = "#$" + type + ":";
		return line.trim().substring(signature.length()).trim();
	}

	private static boolean isSpecialComment(final String line, final String type)
	{
		return line.trim().startsWith("#$" + type + ":");
	}

	private static boolean lineIsAHeaderComment(final String line)
	{
		return isSpecialComment(line, HEADER) && commentPayload(line, HEADER).contains("=");
	}

	private static boolean lineIsImpexBody(final String line)
	{
		return line.trim().startsWith(";");
	}

	@Override
	public boolean addLine(final String line)
	{
		return addLine(line, new ArrayList<>());
	}

	@Override
	public boolean addLine(String line, List<ImpExFragment> fragments)
	{
		if (line == null)
		{
			return false;
		}

		if (impexHeader.length() > 0)
		{
			if (lineIsAUrlComment(line))
			{
				url = commentPayload(line, "URL");
				return true;
			}
			if (lineIsAHeaderComment(line))
			{
				addHeader(line);
				return true;
			}
			if (lineIsImpexBody(line))
			{
				impexBody.append(line).append(CSVConstants.HYBRIS_LINE_SEPARATOR);
				return true;
			}
			validateImpexHeader(fragments);
		}
		else if (line.startsWith("INSERT") || line.startsWith("REMOVE"))
		{
			impexHeader.append(line).append(CSVConstants.HYBRIS_LINE_SEPARATOR);
			return true;
		}
		return false;
	}

	private void validateImpexHeader(final List<ImpExFragment> fragments)
	{
		try
		{
			final StringBuilder macros = new StringBuilder();
			if (CollectionUtils.isNotEmpty(fragments) && fragments.get(0) instanceof ImpexMacroFragment)
			{
				macros.append(fragments.get(0).getContent());
			}
			validateImpexHeader(impexHeader.toString(), macros.toString());
		}
		catch (final ImpExException | IOException ex)
		{
			final List<ImportError> errors = getErrorsForFragment(ex);
			throw new ImpexValidationException(errors, ex);
		}
	}

	protected void validateImpexHeader(final String header, final String macros) throws ImpExException
	{
		final String newHeader = macros + CSVConstants.HYBRIS_LINE_SEPARATOR + header;

		ImpExReader.parseHeader(newHeader);
	}

	private List<ImportError> getErrorsForFragment(final Exception ex)
	{
		final List<ImportError> errors = new ArrayList<>();
		try
		{
			final String body = IOUtils.toString(getImpexBody(), Charset.defaultCharset());
			final String[] bodyLines = StringUtils.split(body, CSVConstants.HYBRIS_LINE_SEPARATOR);
			for (final String bodyLine : bodyLines)
			{
				if (lineIsImpexBody(bodyLine))
				{
					final ImportError importError = ImportError.create(bodyLine, ex.getMessage());
					errors.add(importError);
				}
			}
		}
		catch (final IOException ioEx)
		{
			throw new ImpexValidationException(Collections.singletonList(ImportError.create(null, ioEx.getMessage())), ioEx);
		}
		return errors;
	}

	private void addHeader(final String line)
	{
		final String prop = commentPayload(line, HEADER);
		final int idx = prop.indexOf('=');
		headers.put(prop.substring(0, idx), prop.substring(idx + 1));
	}

	protected InputStream getImpexBody() throws IOException
	{
		try
		{
			return url.trim().isEmpty()
					? new ByteArrayInputStream(impexBody.toString().getBytes())
					: dataHub.readData(url, headers);
		}
		catch (final Exception ex)
		{
			throw new IOException(ex);
		}
	}

	@Override
	public String getContent() throws IOException
	{
		return IOUtils.toString(getContentAsInputStream(), Charset.defaultCharset());
	}

	@Override
	public InputStream getContentAsInputStream() throws IOException
	{
		return new SequenceInputStream(new ByteArrayInputStream(impexHeader.toString().getBytes()), getImpexBody());
	}

	/**
	 * Retrieves facade being used for communication with the Data Hub.
	 *
	 * @return facade implementation.
	 */
	protected DataHubFacade getDataHubFacade()
	{
		return dataHub;
	}

	/**
	 * Reads URL specified in this fragment.
	 *
	 * @return the URL to read data from.
	 */
	public String getUrl()
	{
		return url;
	}

	/**
	 * Reads headers present in this fragment.
	 *
	 * @return a map of the HTTP headers to pass to the Integration Layer; an empty map, if no headers defined in this
	 * fragment.
	 */
	public Map<String, String> getHeaders()
	{
		return headers;
	}


	/**
	 * Reads value of a specific header defined in this fragment.
	 *
	 * @param header name of the header to read.
	 * @return value of the specified header or <code>null</code>, if the specified header is not defined in this
	 * fragment.
	 */
	public String getHeader(final String header)
	{
		return headers.get(header);
	}
}
