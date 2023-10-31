/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.exceptions;

import de.hybris.platform.core.model.c2l.LanguageModel;

import javax.servlet.ServletException;


public class UnsupportedLanguageException extends ServletException
{

	private final LanguageModel language;

	/**
	 * @param msg
	 */
	public UnsupportedLanguageException(final String msg)
	{
		super(msg);
		language = null;
	}

	public UnsupportedLanguageException(final String msg, final Throwable rootCause)
	{
		super(msg, rootCause);
		language = null;
	}

	/**
	 * @return the language
	 */
	public LanguageModel getLanguage()
	{
		return language;
	}
}
