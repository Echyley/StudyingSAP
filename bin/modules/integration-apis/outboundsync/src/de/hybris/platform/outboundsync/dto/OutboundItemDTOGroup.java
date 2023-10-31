/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundsync.dto;

import de.hybris.platform.core.PK;
import de.hybris.platform.outboundsync.job.OutboundItemFactory;
import de.hybris.platform.outboundsync.model.OutboundChannelConfigurationModel;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.common.base.Preconditions;

/**
 * The {@code OutboundItemDTOGroup} class encapsulates the collection of {@link OutboundItemDTO}s.
 * It also provides some convenience accessors.
 */
@Immutable
public class OutboundItemDTOGroup
{
	private final Collection<OutboundItemDTO> outboundItemDTOs;
	private final Long rootItemPk;
	private final OutboundItem outboundItem;
	private final PK cronJobPk;
	private final String changeId;


	private OutboundItemDTOGroup(final Collection<OutboundItemDTO> items, final OutboundItemFactory itemFactory, final String id)
	{
		Preconditions.checkArgument(CollectionUtils.isNotEmpty(items), "OutboundItemDTOs can't be null or empty");
		Preconditions.checkArgument(itemFactory != null, "OutboundItemFactory can't be null");

		outboundItemDTOs = List.copyOf(items);
		rootItemPk = getRootItemPkInternal();
		outboundItem = itemFactory.createItem(getFirstItem());
		cronJobPk = getFirstItem().getCronJobPK();
		changeId = id;
	}

	private OutboundItemDTO getFirstItem()
	{
		final Iterator<OutboundItemDTO> itr = outboundItemDTOs.iterator();
		return itr.next();
	}

	private Long getRootItemPkInternal()
	{
		return outboundItemDTOs.stream()
		                       .filter(dto -> dto.getRootItemPK() != null)
		                       .findFirst()
		                       .map(OutboundItemDTO::getRootItemPK)
		                       .orElse(null);
	}

	/**
	 * Creates an {@code OutboundItemDTOGroup} from the given collection of {@link OutboundItemDTO}
	 *
	 * @param outboundItemDTOs Collection of OutboundItemDTOs
	 * @param itemFactory      a factory for {@link OutboundItem}
	 * @return new instance of this group
	 */
	public static OutboundItemDTOGroup from(final Collection<OutboundItemDTO> outboundItemDTOs,
	                                        final OutboundItemFactory itemFactory)
	{
		return new OutboundItemDTOGroup(outboundItemDTOs, itemFactory, null);
	}

	/**
	 * Creates an {@code OutboundItemDTOGroup} from the given collection of {@link OutboundItemDTO} and assigns a changeId unique
	 * code.
	 *
	 * @param outboundItemDTOs Collection of OutboundItemDTOs
	 * @param itemFactory      a factory for {@link OutboundItem}
	 * @param changeId         a unique String to identify this group
	 * @return new instance of this group
	 */
	public static OutboundItemDTOGroup from(final Collection<OutboundItemDTO> outboundItemDTOs,
	                                        final OutboundItemFactory itemFactory,
	                                        final String changeId)
	{
		return new OutboundItemDTOGroup(outboundItemDTOs, itemFactory, changeId);
	}

	/**
	 * Retrieves a copy of the collection of OutboundItemDTOs
	 *
	 * @return Collection of OutboundItemDTOs
	 */
	public Collection<OutboundItemDTO> getOutboundItemDTOs()
	{
		return outboundItemDTOs;
	}

	/**
	 * Gets the root item PK. If the items in the OutboundItemDTO collection have a root,
	 * the root item PK belongs to the root. Otherwise, the root item PK is the same
	 * as the item's PK.
	 *
	 * @return Root item PK
	 */
	public Long getRootItemPk()
	{
		return rootItemPk;
	}

	/**
	 * Gets the Integration Object's code associated with the changed item
	 *
	 * @return Code
	 */
	public String getIntegrationObjectCode()
	{
		return outboundItem.getIntegrationObject().getCode();
	}

	/**
	 * Gets the ConsumedDestination id associated with the changed item
	 *
	 * @return Destination id
	 */
	public String getDestinationId()
	{
		return outboundItem.getChannelConfiguration().getDestination().getId();
	}

	/**
	 * Gets the {@link OutboundChannelConfigurationModel} associated with the changed item
	 *
	 * @return Channel configuration model
	 */
	public OutboundChannelConfigurationModel getChannelConfiguration()
	{
		return outboundItem.getChannelConfiguration();
	}

	/**
	 * Gets the PK of the CronJob that was triggered
	 *
	 * @return CronJob's primary key
	 */
	public PK getCronJobPk()
	{
		return cronJobPk;
	}

	/**
	 * Retrieves number of changed items in this group
	 *
	 * @return number of changed items related together based on the grouping condition in this group.
	 */
	public int getItemsCount()
	{
		return outboundItemDTOs.size();
	}

	/**
	 * Returns the string with a unique identifier for this group
	 *
	 * @return String with a unique identifier for this group
	 */
	public String getChangeId()
	{
		return changeId;
	}

	@Override
	public String toString()
	{
		return "OutboundItemDTOGroup{" + "outboundItemDTOS=" + outboundItemDTOs + ", rootItemPk=" + rootItemPk + ", cronJobPk=" + cronJobPk + ", changeId=" + changeId + '}';
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

		final OutboundItemDTOGroup that = (OutboundItemDTOGroup) o;

		return new EqualsBuilder().append(outboundItemDTOs, that.outboundItemDTOs).isEquals();
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(17, 37).append(outboundItemDTOs).toHashCode();
	}
}
