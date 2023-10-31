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
package de.hybris.platform.dataimportcommons.facades;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Describes error that has happened during the import process.
 */
@JsonPropertyOrder(value = { "code", "message" })
@XmlAccessorType(XmlAccessType.FIELD)
public class DataImportError implements Serializable
{
	private static final long serialVersionUID = 6516579441358431053L;
	private final String message;
	private final String code;

	public DataImportError(final String message, final String code)
	{
		this.message = message;
		this.code = code;
	}

	/**
	 * Reads the message describing the problem.
	 *
	 * @return an error message.
	 */
	public String getMessage()
	{
		return message;
	}

	/**
	 * Retrieves code of the error, which uniquely defines what problem has happened.
	 *
	 * @return the error code.
	 */
	public String getCode()
	{
		return code;
	}

	@Override
	public String toString()
	{
		return "DataImportError{" +
				", message='" + message + '\'' +
				", code='" + code + '\'' +
				'}';
	}
}
