/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundsync.dto;

import de.hybris.platform.core.PK;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.outboundsync.model.OutboundChannelConfigurationModel;

import javax.annotation.concurrent.Immutable;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.common.base.Preconditions;

@Immutable
public class OutboundItemDTO
{
	private final OutboundItemChange item;
	private final Long integrationObjectPK;
	private final Long channelConfigurationPK;
	private final Long rootItemPK;
	private final PK cronJobPK;
	private final ChangeInfo changeInfo;
	private final boolean synchronizeDelete;
	private final boolean batch;

	/**
	 * Instantiates an uninitialized DTO. Internal state is not set after calling this constructor and the fields, which are not
	 * expected to be {@code null}, are {@code null}
	 * @deprecated use the {@link OutboundItemDTOBuilder} instead.
	 */
	@Deprecated(since = "2205", forRemoval = true)
	public OutboundItemDTO()
	{
		item = null;
		integrationObjectPK = null;
		channelConfigurationPK = null;
		rootItemPK = null;
		cronJobPK = null;
		changeInfo = null;
		synchronizeDelete = false;
		batch = false;
	}

	/**
	 * @deprecated use the {@link OutboundItemDTOBuilder} builder.
	 */
	@Deprecated(since = "2211", forRemoval = true)
	OutboundItemDTO(@NotNull final OutboundItemChange item,
	                @NotNull final Long channelConfigurationPK,
	                @NotNull final PK cronJobPK,
	                final Long integrationObjectPK,
	                final Long rootItemPK,
	                final ChangeInfo changeInfo,
	                final boolean synchronizeDelete)
	{
		this(item, channelConfigurationPK, cronJobPK, integrationObjectPK, rootItemPK, changeInfo, synchronizeDelete, false);
	}

	OutboundItemDTO(@NotNull final OutboundItemChange item,
	                @NotNull final Long channelConfigurationPK,
	                @NotNull final PK cronJobPK,
	                final Long integrationObjectPK,
	                final Long rootItemPK,
	                final ChangeInfo changeInfo,
	                final boolean synchronizeDelete,
	                final boolean isBatchSupported)
	{
		Preconditions.checkArgument(item != null, "item cannot be null");
		Preconditions.checkArgument(channelConfigurationPK != null, "channelConfigurationPk cannot be null");
		Preconditions.checkArgument(cronJobPK != null, "cronJobPk cannot be null");

		this.item = item;
		this.integrationObjectPK = integrationObjectPK;
		this.channelConfigurationPK = channelConfigurationPK;
		this.rootItemPK = rootItemPK;
		this.cronJobPK = cronJobPK;
		this.changeInfo = changeInfo;
		this.synchronizeDelete = synchronizeDelete;
		this.batch = isBatchSupported;
	}

	public ChangeInfo getChangeInfo()
	{
		return changeInfo;
	}

	@NotNull
	public OutboundItemChange getItem()
	{
		return item;
	}

	public @NotNull Long getIntegrationObjectPK()
	{
		return integrationObjectPK;
	}

	@NotNull
	public Long getChannelConfigurationPK()
	{
		return channelConfigurationPK;
	}

	public Long getRootItemPK()
	{
		return rootItemPK;
	}

	@NotNull
	public PK getCronJobPK()
	{
		return cronJobPK;
	}

	public String getIntegrationKey()
	{
		return changeInfo != null ? changeInfo.getIntegrationKey() : null;
	}

	public boolean isSynchronizeDelete()
	{
		return synchronizeDelete;
	}

	public String getItemType()
	{
		return changeInfo != null ? changeInfo.getItemType() : null;
	}

	public String getRootItemType()
	{
		return changeInfo != null ? changeInfo.getRootItemType() : null;
	}

	/**
	 * Determines whether this DTO represents a deleted item.
	 *
	 * @return {@code true}, if the item represented by this DTO has been deleted in the platform; {@code false}, if the item was
	 * created or changed.
	 */
	public boolean isDeleted()
	{
		return item.getChangeType() == OutboundChangeType.DELETED;
	}

	/**
	 * Determines whether the job is synchronizing a batch.
	 *
	 * @return {@code true}, if the channel has batch enabled, otherwise {@code false}
	 */
	public boolean isBatch()
	{
		return batch;
	}

	@Override
	public String toString()
	{
		return "OutboundItemChange{" +
				"item=" + item +
				", rootItemPK=" + rootItemPK +
				", changeInfo=" + changeInfo +
				", integrationObjectPK=" + integrationObjectPK +
				", channelConfigurationPK=" + channelConfigurationPK +
				", cronJobPK=" + cronJobPK +
				", synchronizeDelete=" + synchronizeDelete +
				", isBatch=" + batch +
				'}';
	}

	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}

		if (o == null || getClass() != o.getClass())
		{
			return false;
		}

		final OutboundItemDTO that = (OutboundItemDTO) o;

		return new EqualsBuilder()
				.append(item, that.item)
				.append(integrationObjectPK, that.integrationObjectPK)
				.append(channelConfigurationPK, that.channelConfigurationPK)
				.append(rootItemPK, that.rootItemPK)
				.append(changeInfo, that.changeInfo)
				.append(cronJobPK, that.cronJobPK)
				.append(synchronizeDelete, that.synchronizeDelete)
				.append(batch, that.batch)
				.isEquals();
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(17, 37)
				.append(item)
				.append(integrationObjectPK)
				.append(channelConfigurationPK)
				.append(rootItemPK)
				.append(changeInfo)
				.append(cronJobPK)
				.append(synchronizeDelete)
				.append(batch)
				.toHashCode();
	}

	/**
	 * {@link OutboundItemDTO.Builder} inner builder class is deprecated. All the reference to this class have been replaced with
	 * {@link OutboundItemDTOBuilder} references.
	 *
	 * @deprecated use the {@link OutboundItemDTOBuilder} builder.
	 */
	@Deprecated(since = "2205", forRemoval = true)
	public static final class Builder
	{
		private OutboundItemChange outboundItem;
		private Long integrationObjectPK;
		private Long channelConfigurationPK;
		private Long rootItemPK;
		private PK cronJobPK;
		private ChangeInfo changeInfo;
		private boolean synchronizeDelete;


		private Builder()
		{
		}

		public static Builder item()
		{
			return new Builder();
		}

		public static Builder from(final OutboundItemDTO dto)
		{
			return new Builder()
					.withItem(dto.getItem())
					.withIntegrationObjectPK(dto.getIntegrationObjectPK())
					.withChannelConfigurationPK(dto.getChannelConfigurationPK())
					.withRootItemPK(dto.getRootItemPK())
					.withInfo(dto.changeInfo)
					.withCronJobPK(dto.getCronJobPK())
					.withSynchronizeDelete(dto.isSynchronizeDelete());
		}

		public Builder withItem(final OutboundItemChange itm)
		{
			outboundItem = itm;
			return this;
		}

		public Builder withIntegrationObject(final IntegrationObjectModel model)
		{
			return withIntegrationObjectPK(model.getPk().getLong());
		}

		public Builder withIntegrationObjectPK(final Long pk)
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
		public Builder withChannelConfiguration(final OutboundChannelConfigurationModel model)
		{
			return withChannelConfigurationPK(model.getPk().getLong())
					.withIntegrationObject(model.getIntegrationObject())
					.withSynchronizeDelete(model.getSynchronizeDelete());
		}

		public Builder withChannelConfigurationPK(final Long pk)
		{
			channelConfigurationPK = pk;
			return this;
		}

		public Builder withRootItemPK(final Long pk)
		{
			rootItemPK = pk;
			return this;
		}

		public Builder withCronJobPK(final PK pk)
		{
			cronJobPK = pk;
			return this;
		}

		public Builder withSynchronizeDelete(final boolean syncDelete)
		{
			synchronizeDelete = syncDelete;
			return this;
		}

		public Builder withInfo(final ChangeInfo info)
		{
			changeInfo = info;
			return this;
		}

		public OutboundItemDTO build()
		{
			return new OutboundItemDTO(
					outboundItem,
					channelConfigurationPK,
					cronJobPK,
					integrationObjectPK,
					rootItemPK,
					changeInfo,
					synchronizeDelete);
		}
	}
}
