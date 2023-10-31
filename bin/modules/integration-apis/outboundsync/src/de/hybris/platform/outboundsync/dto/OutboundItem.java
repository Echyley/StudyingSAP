/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundsync.dto;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectDescriptor;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.integrationservices.model.TypeDescriptor;
import de.hybris.platform.integrationservices.model.impl.DefaultDescriptorFactory;
import de.hybris.platform.outboundsync.model.OutboundChannelConfigurationModel;

import java.util.Optional;

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Preconditions;

@Immutable
public class OutboundItem
{
	private OutboundItemChange item;
	private IntegrationObjectDescriptor integrationObject;
	private OutboundChannelConfigurationModel channelConfiguration;
	private ItemModel changedItemModel;
	
	public static Builder outboundItem(){ return new Builder();}

	public OutboundItemChange getItem()
	{
		return item;
	}

	public IntegrationObjectDescriptor getIntegrationObject()
	{
		return integrationObject;
	}

	public OutboundChannelConfigurationModel getChannelConfiguration()
	{
		return channelConfiguration;
	}

	/**
	 * Retrieves changed item model.
	 * @return an {@code Optional} containing the item model, if the item was changed or created; and {@code Optional.empty()}, if
	 * the item was deleted.
	 */
	public Optional<ItemModel> getChangedItemModel()
	{
		return Optional.ofNullable(changedItemModel);
	}

	/**
	 * Retrieves item type descriptor corresponding to this item in the {@link IntegrationObjectModel}.
	 * @return type descriptor or empty value, if this item has no corresponding type descriptor in the {@link IntegrationObjectModel}
	 * model.
	 */
	public Optional<TypeDescriptor> getTypeDescriptor()
	{
		return getChangedItemModel()
				.flatMap(it -> getIntegrationObject().getTypeDescriptor(it));
	}

	@Override
	public String toString()
	{
		return "OutboundItemChange{" +
				"item=" + item +
				", integrationObject=" + integrationObject +
				", channelConfiguration=" + channelConfiguration +
				'}';
	}

	public static final class Builder
	{
		private OutboundItemChange itemChange;
		private ItemModel changedItemModel;
		private IntegrationObjectDescriptor integrationObject;
		private OutboundChannelConfigurationModel channelConfiguration;

		private Builder()
		{
		}

		public Builder withItemChange(final OutboundItemChange change)
		{
			Preconditions.checkArgument(change != null, "item change cannot be null");

			itemChange = change;
			return this;
		}

		public Builder withChangedItemModel(final ItemModel itemModel)
		{
			changedItemModel = itemModel;
			return this;
		}

		/**
		 * @deprecated use {@link #withIntegrationObject(IntegrationObjectDescriptor)} to avoid conversion of the model to
		 * descriptor
		 */
		@Deprecated(since = "2205", forRemoval = true)
		public Builder withIntegrationObject(final IntegrationObjectModel model)
		{
			final IntegrationObjectDescriptor descriptor = DefaultDescriptorFactory.getContextFactory()
			                                                                       .createIntegrationObjectDescriptor(model);
			return withIntegrationObject(descriptor);
		}

		public Builder withIntegrationObject(final IntegrationObjectDescriptor desc)
		{
			Preconditions.checkArgument(desc != null, "IntegrationObjectDescriptor cannot be null");

			integrationObject = desc;
			return this;
		}

		public Builder withChannelConfiguration(final OutboundChannelConfigurationModel configuration)
		{
			Preconditions.checkArgument(configuration != null, "channel configuration cannot be null");

			channelConfiguration = configuration;
			return this;
		}

		public OutboundItem build()
		{
			final OutboundItem outboundItem = new OutboundItem();
			outboundItem.item = itemChange;
			outboundItem.changedItemModel = changedItemModel;
			outboundItem.integrationObject = integrationObject;
			outboundItem.channelConfiguration = channelConfiguration;
			return outboundItem;
		}
	}
}
