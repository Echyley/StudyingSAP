/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.velocity.resource.name;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;


/**
 *
 * Basic property name builder
 *
 */
public class DefaultPropertyNameBuilder implements FileNameBuilder
{

	private static final String PROPERTIES_EXTENSION = ".properties";

	@Override
	public String buildFileName(final Locale locale, final String resourceIdentifier)
	{
		Preconditions.checkArgument(StringUtils.isNotBlank(resourceIdentifier), "Given resource should be not blank");
		Preconditions.checkNotNull(locale, "Given locale should be not null");

		return (resourceIdentifier + "_" + locale.getLanguage() + PROPERTIES_EXTENSION);

	}
}
