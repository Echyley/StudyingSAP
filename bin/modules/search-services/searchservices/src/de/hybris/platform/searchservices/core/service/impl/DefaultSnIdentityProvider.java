/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.core.service.impl;

import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.searchservices.admin.data.SnIndexType;
import de.hybris.platform.searchservices.core.SnException;
import de.hybris.platform.searchservices.core.service.SnContext;
import de.hybris.platform.searchservices.core.service.SnExpressionEvaluator;
import de.hybris.platform.searchservices.core.service.SnIdentityProvider;
import de.hybris.platform.searchservices.util.ParameterUtils;
import de.hybris.platform.servicelayer.model.ModelService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link SnIdentityProvider}.
 */
public class DefaultSnIdentityProvider implements SnIdentityProvider<ItemModel>
{
	public static final String EXPRESSION_PARAM = "expression";
	public static final String EXPRESSION_PARAM_DEFAULT_VALUE = null;

	private ModelService modelService;
	private SnExpressionEvaluator snExpressionEvaluator;

	@Override
	public String getIdentifier(final SnContext context, final ItemModel item) throws SnException
	{
		final SnIndexType indexType = context.getIndexType();
		final String expression = ParameterUtils.getString(indexType.getIdentityProviderParameters(), EXPRESSION_PARAM,
				EXPRESSION_PARAM_DEFAULT_VALUE);

		if (StringUtils.isNotBlank(expression))
		{
			return evaluateExpression(item, expression);
		}
		else
		{
			return item.getPk().getLongValueAsString();
		}
	}

	@Override
	public String getIdentifier(final SnContext context, final PK pk) throws SnException
	{
		final SnIndexType indexType = context.getIndexType();
		final String expression = ParameterUtils.getString(indexType.getIdentityProviderParameters(), EXPRESSION_PARAM,
				EXPRESSION_PARAM_DEFAULT_VALUE);

		if (StringUtils.isNotBlank(expression))
		{
			final ItemModel item = modelService.get(pk);
			return evaluateExpression(item, expression);
		}
		else
		{
			return pk.getLongValueAsString();
		}
	}

	protected String evaluateExpression(final ItemModel item, final String expression) throws SnException
	{
		final Object value = snExpressionEvaluator.evaluate(item, expression);
		if (value == null)
		{
			return null;
		}

		return String.valueOf(value);
	}

	public ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	public SnExpressionEvaluator getSnExpressionEvaluator()
	{
		return snExpressionEvaluator;
	}

	@Required
	public void setSnExpressionEvaluator(final SnExpressionEvaluator snExpressionEvaluator)
	{
		this.snExpressionEvaluator = snExpressionEvaluator;
	}
}
