/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundsync.job.impl;

import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.integrationservices.model.DescriptorFactory;
import de.hybris.platform.integrationservices.model.IntegrationObjectDescriptor;
import de.hybris.platform.integrationservices.util.ApplicationBeans;
import de.hybris.platform.outboundsync.dto.OutboundItem;
import de.hybris.platform.outboundsync.dto.OutboundItemDTO;
import de.hybris.platform.outboundsync.job.OutboundItemFactory;
import de.hybris.platform.outboundsync.model.OutboundChannelConfigurationModel;
import de.hybris.platform.servicelayer.exceptions.ModelLoadingException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

/**
 * Default implementation of the {@link de.hybris.platform.outboundsync.job.OutboundItemFactory}
 */
public class DefaultOutboundItemFactory implements OutboundItemFactory
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultOutboundItemFactory.class);

	private ModelService modelService;
	private DescriptorFactory descriptorFactory;

	@Override
	public OutboundItem createItem(final OutboundItemDTO itemDto)
	{
		final ItemModel changedItemModel = findItemByPk(itemDto.getItem().getPK());
		final OutboundChannelConfigurationModel channelConfiguration = findItemByPk(itemDto.getChannelConfigurationPK());

		return OutboundItem.outboundItem()
		                   .withItemChange(itemDto.getItem())
		                   .withChangedItemModel(changedItemModel)
		                   .withChannelConfiguration(channelConfiguration)
		                   .withIntegrationObject(deriveIntegrationObject(channelConfiguration))
		                   .build();
	}

	private IntegrationObjectDescriptor deriveIntegrationObject(final OutboundChannelConfigurationModel channelConfiguration)
	{
		return Optional.ofNullable(channelConfiguration)
		               .map(OutboundChannelConfigurationModel::getIntegrationObject)
		               .map(getDescriptorFactory()::createIntegrationObjectDescriptor)
		               .orElse(null);
	}

	private <T extends ItemModel> T findItemByPk(final Long pk)
	{
		try
		{
			if (pk != null)
			{
				return modelService.get(PK.fromLong(pk));
			}
		}
		catch (final ModelLoadingException e)
		{
			LOG.trace("The item with PK={} was not found. {}", pk, e.getMessage());
		}
		return null;
	}

	/**
	 * Retrieves model service dependency of this factory
	 *
	 * @return model service implementation used by this factory
	 * @deprecated do not rely on the current dependencies of this factory implementation - it may change any time.
	 * Override {@link #setModelService(ModelService)} method if you need to use it in your subclass. Or better yet, prefer
	 * composition to inheritance and reuse code by wrapping it instead of subclassing.
	 */
	@Deprecated(since = "2205", forRemoval = true)
	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService service)
	{
		modelService = service;
	}

	public void setDescriptorFactory(final DescriptorFactory factory)
	{
		descriptorFactory = factory;
	}

	private DescriptorFactory getDescriptorFactory()
	{
		if (descriptorFactory == null)
		{
			descriptorFactory = ApplicationBeans.getBean("integrationServicesDescriptorFactory", DescriptorFactory.class);
		}
		return descriptorFactory;
	}
}
