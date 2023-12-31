/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.service.impl;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.integrationservices.model.DescriptorFactory;
import de.hybris.platform.integrationservices.model.IntegrationObjectDescriptor;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.integrationservices.populator.ItemToMapConversionContext;
import de.hybris.platform.integrationservices.service.IntegrationObjectAndItemMismatchException;
import de.hybris.platform.integrationservices.service.IntegrationObjectConversionService;
import de.hybris.platform.integrationservices.service.IntegrationObjectNotFoundException;
import de.hybris.platform.integrationservices.service.IntegrationObjectService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Preconditions;

/**
 * The default implementation of IntegrationObjectConversionService.
 */
public class DefaultIntegrationObjectConversionService implements IntegrationObjectConversionService
{
	private IntegrationObjectService integrationObjectService;
	private Converter<ItemToMapConversionContext, Map<String, Object>> itemToIntegrationObjectMapConverter;
	private DescriptorFactory descriptorFactory;

	@Override
	public Map<String, Object> convert(final ItemModel itemModel, final String integrationObjectCode)
	{
		Preconditions.checkArgument(itemModel != null, "item cannot be null");

		final var io = findIntegrationObjectDescriptor(integrationObjectCode);
		final ItemToMapConversionContext conversionContext = getConversionContext(itemModel, io);
		return convert(conversionContext);
	}

	@Override
	public Map<String, Object> convert(final ItemToMapConversionContext context)
	{
		final Map<String, Object> result = context.getConversionResult();
		if (result == null)
		{
			final Map<String, Object> target = new HashMap<>(context.getTypeDescriptor().getAttributes().size());
			context.setConversionResult(target);
			return getItemToIntegrationObjectMapConverter().convert(context, target);
		}
		return result;
	}

	@Override
	public Map<String, Object> convert(final ItemModel itemModel, final IntegrationObjectDescriptor ioDescriptor)
	{
		Preconditions.checkArgument(itemModel != null, "ItemModel cannot be null");
		Preconditions.checkArgument(ioDescriptor != null, "IntegrationObjectDescriptor cannot be null");

		final var conversionContext = getConversionContext(itemModel, ioDescriptor);
		return convert(conversionContext);
	}

	/**
	 * @deprecated use {@link #getConversionContext(Object, IntegrationObjectDescriptor)} instead
	 */
	@Deprecated(since = "2211.FP1", forRemoval = true)
	protected ItemToMapConversionContext getConversionContext(final ItemModel item, final IntegrationObjectDescriptor io)
	{
		return getConversionContext((Object) item, io);
	}

	protected ItemToMapConversionContext getConversionContext(final Object payloadObject, final IntegrationObjectDescriptor io)
	{
		return io.getTypeDescriptor(payloadObject)
		         .map(type -> new ItemToMapConversionContext(payloadObject, type))
		         .orElseThrow(() -> new IntegrationObjectAndItemMismatchException(payloadObject, io));
	}

	protected IntegrationObjectDescriptor findIntegrationObjectDescriptor(final String integrationObjectCode)
	{
		try
		{
			final IntegrationObjectModel model = getIntegrationObjectService().findIntegrationObject(integrationObjectCode);
			return getDescriptorFactory().createIntegrationObjectDescriptor(model);
		}
		catch (final ModelNotFoundException e)
		{
			throw new IntegrationObjectNotFoundException(integrationObjectCode, e);
		}
	}

	protected Converter<ItemToMapConversionContext, Map<String, Object>> getItemToIntegrationObjectMapConverter()
	{
		return itemToIntegrationObjectMapConverter;
	}

	@Required
	public void setItemToIntegrationObjectMapConverter(
			final Converter<ItemToMapConversionContext, Map<String, Object>> itemToIntegrationObjectMapConverter)
	{
		this.itemToIntegrationObjectMapConverter = itemToIntegrationObjectMapConverter;
	}

	protected IntegrationObjectService getIntegrationObjectService()
	{
		return integrationObjectService;
	}

	@Required
	public void setIntegrationObjectService(final IntegrationObjectService integrationObjectService)
	{
		this.integrationObjectService = integrationObjectService;
	}

	protected DescriptorFactory getDescriptorFactory()
	{
		return descriptorFactory;
	}

	@Required
	public void setDescriptorFactory(final DescriptorFactory descriptorFactory)
	{
		this.descriptorFactory = descriptorFactory;
	}
}
