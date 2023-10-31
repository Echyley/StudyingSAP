/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.datahub.core.facades;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.hybris.platform.dataimportcommons.facades.DataItemImportResult;
import org.apache.commons.io.output.StringBuilderWriter;


/**
 * Encapsulates result of importing the data.
 */
@XmlRootElement(name = "publicationResult")
@XmlAccessorType(XmlAccessType.FIELD)
public class ItemImportResult extends DataItemImportResult<ImportError>
{
	/**
	 * Instantiates successful import result.
	 */
	public ItemImportResult()
	{
		super();
	}

	/**
	 * Instantiates error result for a crashed import process.
	 *
	 * @param e an exception that was intercepted from the import process.
	 */
	public ItemImportResult(final Exception e)
	{
		super(e);
	}

	/**
	 * Instantiates error result for a crashed import process.
	 *
	 * @param msg an error message explaining the problem with the import process.
	 */
	public ItemImportResult(final String msg)
	{
		super(msg);
	}

	/**
	 * Determines whether the item import ran successfully or not.
	 *
	 * @return <code>true</code>, if import was successful; <code>false</code>, if the import crashed or there are
	 * rejected items.
	 */
	public boolean isSuccessful()
	{
		return isSuccess();
	}

	/**
	 * Retrieves all errors reported by the import process.
	 *
	 * @return a collection of errors or an empty collection, if all items were successfully imported.
	 */
	public Collection<ImportError> getErrors()
	{
		return Collections.unmodifiableList(getExportErrorDatas());
	}

	/**
	 * Retrieves message of exception that happened during the import process.
	 *
	 * @return an exception or <code>null</code>, if there were no exception.
	 */
	public String getExceptionMessage()
	{
		return getImportExceptionMessage();
	}

	@Override
	public String toString()
	{
		final StringBuilder buf = new StringBuilder();
		try (final PrintWriter writer = new PrintWriter(new StringBuilderWriter(buf)))
		{
			writer.print(isSuccessful() ? "SUCCESS" : "ERROR");
			if (getImportExceptionMessage() != null)
			{
				writer.print("(");
				writer.print(getImportExceptionMessage());
				writer.print(")");
			}
			return buf.toString();
		}
	}

	/**
	 * Return the status of an {@link ItemImportResult} based on whether the import had
	 * no errors or importExceptionMessage (SUCCESS), had errors but no importException (PARTIAL_SUCCESS), or had
	 * importException (FAILURE)
	 *
	 * @return The status of import
	 */
	public DatahubAdapterEventStatus getStatus()
	{
		if (isSuccessful())
		{
			return DatahubAdapterEventStatus.SUCCESS;
		}
		else if (!getExportErrorDatas().isEmpty() && getImportExceptionMessage() == null)
		{
			return DatahubAdapterEventStatus.PARTIAL_SUCCESS;
		}
		else
		{
			return DatahubAdapterEventStatus.FAILURE;
		}
	}

	@Override
	@XmlElement(name = "exportErrorDatas")
	public List<ImportError> getExportErrorDatas()
	{
		return super.getExportErrorDatas();
	}

	/**
	 * Possible event status.
	 */
	public enum DatahubAdapterEventStatus
	{
		/**
		 * Indicates that all items were imported successfully.
		 */
		SUCCESS,
		/**
		 * Indicates that no items were imported successfully.
		 */
		FAILURE,
		/**
		 * Indicates that some items were imported successfully and some were rejected.
		 */
		PARTIAL_SUCCESS
	}
}
