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

package com.hybris.datahub.core.facades;

import de.hybris.platform.servicelayer.impex.ImpExError;
import de.hybris.platform.servicelayer.impex.ImpExHeaderError;
import de.hybris.platform.servicelayer.impex.ImpExValueLineError;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import de.hybris.platform.dataimportcommons.facades.DataImportError;
import de.hybris.platform.dataimportcommons.facades.ErrorCode;

/**
 * Describes error that has happened during the import process.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ImportError extends DataImportError
{
	private static final String UNDEFINED_ERROR_MESSAGE = "There was an ImpExError but it did not provide an error message.";
	private static final String VALUE_LINE_DELIMITER = ";";

	/**
	 * A regular expression pattern for extracting an item ID from the impex source line. Item ID is a number between two
	 * ';' characters at the beginning of the impex line.
	 */
	private static final Pattern ID_PATTERN = Pattern.compile("\\s*;\\s*(\\d+)\\s*;.*");
	/**
	 * A regular expression pattern for extracting item type from an impex header line. The item type follows the impex
	 * command and is separated from it by at least one space. ';' separates the type from the rest of the header line,
	 * e.g. Product is captured from 'INSERT_UPDATE Product;...'
	 */
	private static final Pattern TYPE_PATTERN = Pattern.compile("(?:INSERT|INSERT_UPDATE|UPDATE|REMOVE)\\s+(\\w+);.*");

	@XmlElement(name = "canonicalItemId")
	private final Long canonicalItemId;
	private final String itemType;
	@XmlTransient
	private final String scriptLine;

	public ImportError()
	{
		this(null, null, null, null, null);
	}

	public ImportError(final String type, final String line, final String msg)
	{
		this(null, type, line, msg, null);
	}

	private ImportError(final Long id, final String type, final String line, final String msg, final String errorCode)
	{
		super(messageOrDefault(msg), errorCode);
		canonicalItemId = id;
		itemType = type;
		scriptLine = line;
	}

	public static ImportError create(final ImpExError error)
	{
		if (error instanceof ImpExValueLineError)
		{
			final ImpExValueLineError valueLineError = (ImpExValueLineError) error;
			final Long canonicalItemId = extractItemIdFromValueLine(valueLineError.getLine().getSource().get(1));
			final String scriptLine = valueLineError.getSource().values().stream().collect(Collectors.joining(VALUE_LINE_DELIMITER));
			final String errorMessage = messageOrDefault(valueLineError.getErrorMessage());
			return new ImportError(canonicalItemId, valueLineError.getItemType(), scriptLine, errorMessage, valueLineError.getErrorType().toString());
		}

		final ImpExHeaderError headerError = (ImpExHeaderError) error;
		final String header = headerError.getSource().values().stream().collect(Collectors.joining(VALUE_LINE_DELIMITER));
		final String errorMessage = messageOrDefault(headerError.getErrorMessage());
		return new ImportError(null, headerError.getItemType(), header, errorMessage, null);
	}

	/**
	 * Creates an error.
	 *
	 * @param line rejected impex script line
	 * @param msg an error message explaining the problem
	 * @return new instance of the error
	 */
	public static ImportError create(final String line, final String msg)
	{
		if (msg == null)
		{
			return null;
		}
		final String nullSafeLn = line != null ? line : "";
		final Long dataHubId = extractItemIdFromSourceLine(nullSafeLn);
		if( dataHubId != null )
		{
			return new ImportError(dataHubId, null, nullSafeLn, msg, ErrorCode.classify(msg).toString());
		}
		return new ImportError(itemTypeFromHeader(nullSafeLn), nullSafeLn, msg);
	}

	private static String messageOrDefault(final String msg)
	{
		return (msg != null && msg.length() > 0) ? msg : UNDEFINED_ERROR_MESSAGE;
	}

	private static Long extractItemIdFromSourceLine(final String line)
	{
		final Matcher matcher = ID_PATTERN.matcher(line);
		return matcher.matches() ? Long.valueOf(matcher.group(1)) : null;
	}

	private static String itemTypeFromHeader(final String line)
	{
		final Matcher matcher = TYPE_PATTERN.matcher(line);
		return matcher.matches() ? matcher.group(1) : null;
	}

	private static Long extractItemIdFromValueLine(final String line)
	{
		try
		{
			return Long.valueOf(removeSymbolsAddedByImportService(line));
		}
		catch (final NumberFormatException e)
		{
			return null;
		}
	}

	private static String removeSymbolsAddedByImportService(final String in)
	{
		return in.replace("<ignore>", "");
	}

	/**
	 * Retrieves ID of the item that failed to import.
	 *
	 * @return item ID or <code>null</code>, if the error code indicates an impex header error.
	 */
	public Long getCanonicalItemId()
	{
		return canonicalItemId;
	}

	/**
	 * Determines type of the item that failed to import.
	 *
	 * @return item type
	 */
	public String getItemType()
	{
		return itemType;
	}

	/**
	 * Retrieves the line of the impex script, which resulted in this error
	 *
	 * @return line of the impex script that was rejected by the import.
	 */
	public String getScriptLine()
	{
		return scriptLine;
	}

	@Override
	public String toString()
	{
		return "ImportError{" +
				"canonicalItemId=" + canonicalItemId +
				", itemType='" + itemType + '\'' +
				", scriptLine='" + scriptLine + '\'' +
				", message='" + getMessage() + '\'' +
				", code='" + getCode() + '\'' +
				'}';
	}
}
