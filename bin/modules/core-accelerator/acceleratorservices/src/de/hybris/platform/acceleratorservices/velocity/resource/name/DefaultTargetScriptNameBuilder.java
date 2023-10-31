/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.velocity.resource.name;

import java.util.Locale;


/**
 *
 * Basic property name builder
 *
 */
public class DefaultTargetScriptNameBuilder implements FileNameBuilder
{

	private static final String PROPERTIES_EXTENSION = ".impex";

	@Override
	public String buildFileName(final Locale locale, final String resourceIdentifier)
	{
		return (resourceIdentifier + "_" + locale.getLanguage() + PROPERTIES_EXTENSION);
	}

}
