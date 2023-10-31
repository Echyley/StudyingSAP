/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.dataimport.batch.converter.impl;

import de.hybris.platform.acceleratorservices.dataimport.batch.converter.ImpexConverter;
import de.hybris.platform.acceleratorservices.dataimport.batch.converter.ImpexRowFilter;
import de.hybris.platform.servicelayer.exceptions.SystemException;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Default implementation of {@link ImpexConverter}.
 */
public class DefaultImpexConverter implements ImpexConverter
{
	private static final char SEMICOLON_CHAR = ';';
	private static final char PLUS_CHAR = '+';
	private static final char SEQUENCE_CHAR = 'S';
	private static final String EMPTY_STRING = "";
	private static final char BRACKET_END = '}';
	private static final char BRACKET_START = '{';
	private static final char DOUBLE_QUOTE = '"';
	private static final char COMMA = ',';

	private static final char[] IMPEX_ESCAPE_CHARS = new char[] {
			SEMICOLON_CHAR, COMMA, DOUBLE_QUOTE, CharUtils.CR, CharUtils.LF };

	private String header;
	private String impexRow;
	private String type;
	private ImpexRowFilter rowFilter = new NullImpexRowFilter();
	private boolean enableEscaping;

	@Override
	public String convert(final Map<Integer, String> row, final Long sequenceId)
	{
		String result = EMPTY_STRING;
		if (!MapUtils.isEmpty(row))
		{
			final StringBuilder builder = new StringBuilder();
			int copyIdx = 0;
			int idx = impexRow.indexOf(BRACKET_START);
			while (idx > -1)
			{
				final int endIdx = impexRow.indexOf(BRACKET_END, idx);
				processRow(row, sequenceId, builder, copyIdx, idx, endIdx);
				copyIdx = endIdx + 1;
				idx = impexRow.indexOf(BRACKET_START, endIdx);
			}
			if (copyIdx < impexRow.length())
			{
				builder.append(impexRow.substring(copyIdx));
			}
			result = builder.toString();
		}
		// if escaping is enabled the result is already escaped at this point
		return enableEscaping ? result : escapeQuotes(result);
	}

	protected void processRow(final Map<Integer, String> row, final Long sequenceId, final StringBuilder builder,
			final int copyIdx, final int idx, final int endIdx)
	{
		if (endIdx < 0)
		{
			throw new SystemException("Invalid row syntax [brackets not closed]: " + impexRow);
		}
		builder.append(impexRow.substring(copyIdx, idx));
		if (impexRow.charAt(idx + 1) == SEQUENCE_CHAR)
		{
			builder.append(sequenceId);
		}
		else
		{
			processValues(row, builder, idx, endIdx);
		}
	}

	protected void processValues(final Map<Integer, String> row, final StringBuilder builder, final int idx, final int endIdx)
	{
		final boolean mandatory = impexRow.charAt(idx + 1) == PLUS_CHAR;
		Integer mapIdx = null;
		try
		{
			mapIdx = Integer.valueOf(impexRow.substring(mandatory ? idx + 2 : idx + 1, endIdx));
		}
		catch (final NumberFormatException e)
		{
			throw new SystemException("Invalid row syntax [invalid column number]: " + impexRow, e);
		}
		final String colValue = row.get(mapIdx);
		if (mandatory && StringUtils.isBlank(colValue))
		{
			throw new IllegalArgumentException("Missing value for " + mapIdx);
		}
		if (colValue == null)
		{
			return;
		}
		if ( enableEscaping )
		{
			final String escapedColValue = escape(colValue);
			builder.append(escapedColValue);
		}
		else
		{
			builder.append(colValue);
		}
	}

	protected String escape(final String value)
	{
		if (!StringUtils.containsAny(value, IMPEX_ESCAPE_CHARS))
		{
			return value;
		}
		StringWriter writer = new StringWriter();
		writer.write(DOUBLE_QUOTE);
		for (int i = 0; i < value.length(); i++)
		{
			final char c = value.charAt(i);
			if (c == DOUBLE_QUOTE) {
				writer.write(DOUBLE_QUOTE); // escape double quote
			}
			writer.write(c);
		}
		writer.write(DOUBLE_QUOTE);
		return writer.toString();
	}

	protected String escapeQuotes(final String input)
	{
		final String[] splitedInput = StringUtils.splitPreserveAllTokens(input, SEMICOLON_CHAR);
		final List<String> tmp = new ArrayList<String>();
		for (final String string : splitedInput)
		{
			if (doesNotContainNewLine(string))
			{
				tmp.add(StringEscapeUtils.escapeCsv(string));
			}
			else
			{
				tmp.add(string);
			}
		}
		return StringUtils.join(tmp, SEMICOLON_CHAR);
	}

	protected boolean doesNotContainNewLine(final String string)
	{
		return !StringUtils.contains(string, CharUtils.LF);
	}

	/**
	 * @see de.hybris.platform.acceleratorservices.dataimport.batch.converter.ImpexConverter#filter(java.util.Map)
	 */
	@Override
	public boolean filter(final Map<Integer, String> row)
	{
		return rowFilter.filter(row);
	}

	/**
	 * @see de.hybris.platform.acceleratorservices.dataimport.batch.converter.ImpexConverter#getHeader()
	 */
	@Override
	public String getHeader()
	{
		return header;
	}

	/**
	 * @param header
	 *           the header to set
	 */
	@Required
	public void setHeader(final String header)
	{
		Assert.hasText(header);
		this.header = header;
	}

	/**
	 * @param impexRow
	 *           the impexRow to set
	 */
	@Required
	public void setImpexRow(final String impexRow)
	{
		Assert.hasText(impexRow);
		this.impexRow = impexRow;
	}

	/**
	 * @param type
	 *           the type to set
	 */
	public void setType(final String type)
	{
		this.type = type;
	}

	/**
	 * @return the type
	 */
	@Override
	public String getType()
	{
		return type;
	}

	/**
	 * @param rowFilter
	 *           the rowFilter to set
	 */
	public void setRowFilter(final ImpexRowFilter rowFilter)
	{
		Assert.notNull(rowFilter);
		this.rowFilter = rowFilter;
	}

	public boolean getEnableEscaping()
	{
		return enableEscaping;
	}

	public void setEnableEscaping(final boolean enableEscaping)
	{
		this.enableEscaping = enableEscaping;
	}
}
