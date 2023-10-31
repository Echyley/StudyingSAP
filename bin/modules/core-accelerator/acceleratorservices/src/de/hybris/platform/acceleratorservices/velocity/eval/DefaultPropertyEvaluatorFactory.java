/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.velocity.eval;

import de.hybris.platform.acceleratorservices.velocity.resource.PropertyFileMissingHandler;
import de.hybris.platform.acceleratorservices.velocity.resource.url.ScriptUrlContext;

import javax.annotation.Resource;


public class DefaultPropertyEvaluatorFactory implements PropertyEvaluatorFactory
{
	private PropertyFileMissingHandler propertyFileMissingHandler;

	@Resource
	public void setPropertyFileMissingHandler(final PropertyFileMissingHandler propertyFileMissingHandler)
	{
		this.propertyFileMissingHandler = propertyFileMissingHandler;
	}

	public RowsEvaluator getRowsEvaluator()
	{
		throw new UnsupportedOperationException("Inject a RowsEvaluator implementation");
	}

	@Override
	public PropertyEvaluator create(final ScriptUrlContext urlContext)
	{
		final PropertyEvaluator propertyEvaluator = new DefaultPropertyEvaluator(propertyFileMissingHandler, urlContext);
		propertyEvaluator.setRowEvaluator(getRowsEvaluator());
		return propertyEvaluator;
	}
}
