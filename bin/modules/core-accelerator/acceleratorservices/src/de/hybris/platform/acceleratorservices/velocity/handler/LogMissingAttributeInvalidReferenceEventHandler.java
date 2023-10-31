/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.velocity.handler;

import org.apache.velocity.app.event.InvalidReferenceEventHandler;
import org.apache.velocity.context.Context;
import org.apache.velocity.util.introspection.Info;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LogMissingAttributeInvalidReferenceEventHandler implements InvalidReferenceEventHandler
{
	private static final Logger LOG = LoggerFactory.getLogger(LogMissingAttributeInvalidReferenceEventHandler.class);

	@Override
	public Object invalidGetMethod(final Context context, final String reference, final Object object, final String property,
			final Info info)
	{
		LOG.info("<<Property '{}' getter for object {} '{}' not found or inaccessible at {},{} in {}>>",
				property, object, reference, info.getLine(), info.getColumn(), info.getTemplateName());
		return "";
	}

	@Override
	public boolean invalidSetMethod(final Context context, final String leftreference, final String rightreference, final Info info)
	{
		return false;
	}

	@Override
	public Object invalidMethod(final Context context, final String reference, final Object object, final String method,
			final Info info)
	{
		LOG.info("<<Method {} for object {} '{}' not found or inaccessible at {},{} in {}>>",
				method, object, reference, info.getLine(), info.getColumn(), info.getTemplateName());
		return "";
	}
}
