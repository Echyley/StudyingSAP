/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundsync.dto;

import de.hybris.platform.core.PK;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.outboundsync.model.OutboundChannelConfigurationModel;

import javax.validation.constraints.NotNull;

import com.google.common.base.Preconditions;

public final class OutboundItemDTOBuilder
{
	private OutboundItemChange item;
	private Long integrationObjectPK;
	private Long channelConfigurationPK;
	private Long rootItemPK;
	private PK cronJobPK;
	private ChangeInfo changeInfo;
	private boolean synchronizeDelete;
	private boolean batch;

	private OutboundItemDTOBuilder()
	{
	}

	public static OutboundItemDTOBuilder outboundItemDTO()
	{
		return new OutboundItemDTOBuilder();
	}

	public static OutboundItemDTOBuilder from(@NotNull final OutboundItemDTO dto)
	{
		Preconditions.checkArgument(dto != null, "OutboundItemDTO cannot be null");

		return new OutboundItemDTOBuilder()
				.withItem(dto.getItem())
				.withIntegrationObjectPK(dto.getIntegrationObjectPK())
				.withChannelConfigurationPK(dto.getChannelConfigurationPK())
				.withRootItemPK(dto.getRootItemPK())
				.withInfo(dto.getChangeInfo())
				.withCronJobPK(dto.getCronJobPK())
				.withSynchronizeDelete(dto.isSynchronizeDelete())
				.withBatch(dto.isBatch());
	}

	public OutboundItemDTOBuilder withBatch(final boolean batch)
	{
		this.batch = batch;
		return this;
	}

	public OutboundItemDTOBuilder withItem(final OutboundItemChange itm)
	{
		item = itm;
		return this;
	}

	public OutboundItemDTOBuilder withIntegrationObject(final IntegrationObjectModel model)
	{
		return withIntegrationObjectPK(model.getPk().getLong());
	}

	public OutboundItemDTOBuilder withRootItem(@NotNull final OutboundItemChange item)
	{
		Preconditions.checkArgument(item != null, "OutboundItemChange cannot be null");
		return withRootItemPK(item.getPK());
	}

	public OutboundItemDTOBuilder withIntegrationObjectPK(final Long pk)
	{
		integrationObjectPK = pk;
		return this;
	}

	/**
	 * Specifies the outbound channel configuration and all items that can be derived from the channel configuration, e.g.
	 * integration object, etc.
	 *
	 * @param model model of the outbound channel configuration to include in the DTO
	 * @return a builder with the channel configuration specified
	 */
	public OutboundItemDTOBuilder withChannelConfiguration(final OutboundChannelConfigurationModel model)
	{
		return withChannelConfigurationPK(model.getPk().getLong())
				.withIntegrationObject(model.getIntegrationObject())
				.withSynchronizeDelete(model.getSynchronizeDelete())
				.withBatch(model.getBatch());
	}

	public OutboundItemDTOBuilder withChannelConfigurationPK(final Long pk)
	{
		channelConfigurationPK = pk;
		return this;
	}

	public OutboundItemDTOBuilder withRootItemPK(final Long pk)
	{
		rootItemPK = pk;
		return this;
	}

	public OutboundItemDTOBuilder withCronJobPK(@NotNull final PK pk)
	{
		cronJobPK = pk;
		return this;
	}

	public OutboundItemDTOBuilder withSynchronizeDelete(final boolean syncDelete)
	{
		synchronizeDelete = syncDelete;
		return this;
	}

	public OutboundItemDTOBuilder withInfo(final ChangeInfo info)
	{
		changeInfo = info;
		return this;
	}

	public OutboundItemDTO build()
	{
		return new OutboundItemDTO(
				item,
				channelConfigurationPK,
				cronJobPK,
				integrationObjectPK,
				rootItemPK,
				changeInfo,
				synchronizeDelete,
				batch);
	}
}
