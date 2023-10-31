/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.dataimportcommons.facades;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Encapsulates result of importing the data.
 */
public class DataItemImportResult<T extends DataImportError> implements Serializable
{
	private final transient List<T> exportErrorDatas;
	private static final long serialVersionUID = 3292372726320737310L;
	@XmlElement(name = "crashReport")
	private String importExceptionMessage;

	/**
	 * Instantiates successful import result.
	 */
	public DataItemImportResult()
	{
		this((String) null);
	}

	/**
	 * Instantiates error result for a crashed import process.
	 *
	 * @param e an exception that was intercepted from the import process.
	 */
	public DataItemImportResult(final Exception e)
	{
		this(extractMessage(e));
	}

	/**
	 * Instantiates error result for a crashed import job.
	 *
	 * @param message an error message explaining the problem with the job execution.
	 */
	public DataItemImportResult(final String message)
	{
		exportErrorDatas = new LinkedList<>();
		importExceptionMessage = message;
	}

	public void setExceptionMessage(final String exceptionMessage)
	{
		this.importExceptionMessage = exceptionMessage;
	}

	private static String extractMessage(final Exception e)
	{
		if (e != null)
		{
			return e.getMessage() == null ? e.getClass().getCanonicalName() : e.getMessage();
		}
		return null;
	}

	/**
	 * Adds errors to this result
	 *
	 * @param errors import errors to add to this result
	 * @return this result
	 * @throws IllegalArgumentException if null passed in.
	 */
	public DataItemImportResult<T> addErrors(final Collection<T> errors)
	{
		if (errors == null)
		{
			throw new IllegalArgumentException("Not expecting a null collection here");
		}

		exportErrorDatas.addAll(errors);
		if (!exportErrorDatas.isEmpty())
		{
			importExceptionMessage = null;
		}
		return this;
	}

	@JsonIgnore
	public boolean isSuccess()
	{
		return getExportErrorDatas().isEmpty() && getImportExceptionMessage() == null;
	}

	@JsonIgnore
	public List<T> getExportErrorDatas()
	{
		return Collections.unmodifiableList(exportErrorDatas);
	}

	@JsonIgnore
	public String getImportExceptionMessage()
	{
		return importExceptionMessage;
	}

	@Override
	public String toString()
	{
		return "DataItemImportResult{" +
				"exportErrorDatas=" + exportErrorDatas +
				", importExceptionMessage='" + importExceptionMessage + '\'' +
				'}';
	}
}
