/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.core.service.impl;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.searchservices.core.SnException;
import de.hybris.platform.searchservices.core.service.SnExpressionEvaluator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link SnExpressionEvaluator}.
 */
public class DefaultSnExpressionEvaluator implements SnExpressionEvaluator
{
	protected static final char SEPARATOR = '.';

	private TypeService typeService;
	private ModelService modelService;

	@Override
	public Object evaluate(final Object root, final String expression) throws SnException
	{
		ServicesUtil.validateParameterNotNullStandardMessage("expression", expression);

		final String[] attributePath = buildAttributePath(expression);

		Object value = root;

		for (final String attribute : attributePath)
		{
			value = doEvaluate(value, attribute, null);
		}

		return value;
	}

	@Override
	public Object evaluate(final Object root, final String expression, final List<Locale> locales) throws SnException
	{
		ServicesUtil.validateParameterNotNullStandardMessage("expression", expression);

		final String[] attributePath = buildAttributePath(expression);

		final Map<Locale, Object> localizedValue = new HashMap<>();

		for (final Locale locale : locales)
		{
			Object value = root;

			for (final String attribute : attributePath)
			{
				value = doEvaluate(value, attribute, locale);
			}

			if (value != null)
			{
				localizedValue.put(locale, value);
			}
		}

		if (MapUtils.isEmpty(localizedValue))
		{
			return null;
		}

		return localizedValue;
	}

	protected String[] buildAttributePath(final String expression)
	{
		return StringUtils.split(expression.trim(), SEPARATOR);
	}

	protected Object doEvaluate(final Object root, final String attribute, final Locale locale) throws SnException
	{
		if (root == null)
		{
			return null;
		}
		else if (root instanceof Map)
		{
			return ((Map) root).get(attribute);
		}
		else if (root instanceof Collection)
		{
			if (CollectionUtils.isEmpty((Collection) root))
			{
				return Collections.emptyList();
			}

			final List<Object> values = new ArrayList<>();

			for (final Object rootItem : (Collection) root)
			{
				final Object value = resolveAttributeValue(rootItem, attribute, locale);

				if (value instanceof Collection)
				{
					values.addAll((Collection) value);
				}
				else if (value != null)
				{
					values.add(value);
				}
			}

			return values;
		}
		else
		{
			return resolveAttributeValue(root, attribute, locale);
		}
	}

	protected Object resolveAttributeValue(final Object root, final String attribute, final Locale locale) throws SnException
	{
		if (root instanceof ItemModel)
		{
			return resolveItemAttributeValue((ItemModel) root, attribute, locale);
		}
		else
		{
			return resolveObjectAttributeValue(root, attribute);
		}
	}

	protected Object resolveItemAttributeValue(final ItemModel root, final String attribute, final Locale locale)
	{
		final Object value;

		if (StringUtils.equals(ItemModel.PK, attribute))
		{
			value = root.getPk();
		}
		else if (StringUtils.equals(ItemModel.ITEMTYPE, attribute))
		{
			value = root.getItemtype();
		}
		else
		{
			final ComposedTypeModel composedType = typeService.getComposedTypeForClass(root.getClass());
			if (typeService.hasAttribute(composedType, attribute))
			{
				final AttributeDescriptorModel attributeDescriptor = typeService.getAttributeDescriptor(composedType, attribute);
				if (locale == null || BooleanUtils.isFalse(attributeDescriptor.getLocalized()))
				{
					value = modelService.getAttributeValue(root, attribute);
				}
				else
				{
					value = modelService.getAttributeValue(root, attribute, locale);
				}
			}
			else
			{
				value = null;
			}
		}

		return value;
	}

	protected Object resolveObjectAttributeValue(final Object root, final String attribute) throws SnException
	{
		final Object value;

		if (PropertyUtils.isReadable(root, attribute))
		{
			try
			{
				value = PropertyUtils.getProperty(root, attribute);
			}
			catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
			{
				throw new SnException(e);
			}
		}
		else
		{
			value = null;
		}

		return value;
	}

	public TypeService getTypeService()
	{
		return typeService;
	}

	@Required
	public void setTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
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
}
