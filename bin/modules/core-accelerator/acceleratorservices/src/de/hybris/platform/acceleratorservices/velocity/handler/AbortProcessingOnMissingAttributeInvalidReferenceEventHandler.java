/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.velocity.handler;

import org.apache.velocity.app.event.InvalidReferenceEventHandler;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.util.introspection.Info;

public class AbortProcessingOnMissingAttributeInvalidReferenceEventHandler implements InvalidReferenceEventHandler
{
	@Override
	public Object invalidGetMethod(final Context context, final String reference, final Object object, final String property,
			final Info info)
	{
		throw new MethodInvocationException("Property '" + property + "' getter for object " + object + " '" + reference
				+ "' not found or inaccessible ", null, property, info.getTemplateName(), info.getLine(), info.getColumn());
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
		throw new MethodInvocationException("Method " + method + " for object " + object + " '" + reference
				+ "' not found or inaccessible", null, method, info.getTemplateName(), info.getLine(), info.getColumn());
	}
}
