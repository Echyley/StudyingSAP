/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.swaggerintegration.config;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import org.springdoc.core.customizers.PropertyCustomizer;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.oas.models.media.DateTimeSchema;
import io.swagger.v3.oas.models.media.Schema;


public class CommerceDateTimeFormatCustomizer implements PropertyCustomizer
{

	@Override
	public Schema customize(final Schema property, final AnnotatedType type)
	{
		if (property == null)
		{
			return null;
		}
		if (property instanceof DateTimeSchema && property.getExample() == null)
		{
			property.setExample(getCurrentDateTime());
		}
		return property;
	}

	private String getCurrentDateTime()
	{
		return ZonedDateTime.now().toOffsetDateTime().truncatedTo(ChronoUnit.SECONDS).toString();
	}
}
