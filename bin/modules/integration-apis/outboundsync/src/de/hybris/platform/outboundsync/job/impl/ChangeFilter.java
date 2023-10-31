/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundsync.job.impl;

import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.integrationservices.util.Log;
import de.hybris.platform.outboundsync.OutboundSyncFeature;
import de.hybris.platform.outboundsync.dto.OutboundItemDTO;

import java.util.Optional;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

/**
 * A filter for changes collected by {@link StreamingChangesCollector}, that passes the change through, if the changed item type
 * and type of the root item, it relates to, belong to the integration object. Otherwise the change is filtered out.
 */
class ChangeFilter
{
	private static final Logger LOG = Log.getLogger(ChangeFilter.class);


	private final IntegrationObjectModel integrationObject;

	public ChangeFilter(final IntegrationObjectModel io)
	{
		integrationObject = io;
	}

	/**
	 * Evaluates the filtering conditions against the specified item DTO.
	 *
	 * @param dto an item DTO to check against the filtering conditions.
	 * @return an {@code Optional} containing the {@code dto}, if it satisfies the filtering conditions; or an
	 * {@code Optional.empty()}, if it does not.
	 */
	public Optional<OutboundItemDTO> evaluate(@NotNull final OutboundItemDTO dto)
	{
		return Optional.of(dto)
		               .filter(this::isDeleteFeatureEnabled)
		               .filter(this::matchesIntegrationObject)
		               .filter(this::deleteItemIsRootType);
	}

	// Remove when feature is fully implemented
	private boolean isDeleteFeatureEnabled(final OutboundItemDTO dto)
	{
		return !dto.isDeleted() || OutboundSyncFeature.DELETE.isEnabled();
	}

	private boolean deleteItemIsRootType(final OutboundItemDTO dto)
	{
		return !dto.isDeleted() || isItemChangedRootType(dto);
	}

	private boolean isItemChangedRootType(final OutboundItemDTO dto)
	{
		if (StringUtils.isBlank(dto.getItemType()) || StringUtils.isBlank(dto.getRootItemType()))
		{
			return false;
		}
		else if (!dto.getItemType().equals(dto.getRootItemType()))
		{
			LOG.warn("Deleted item {} cannot be synchronized because the item type is not the root type.", dto);
			return false;
		}
		return true;
	}

	private boolean matchesIntegrationObject(final OutboundItemDTO dto)
	{
		return matchesIntegrationObjectItem(dto.getItemType()) && matchesIntegrationObjectItem(dto.getRootItemType());
	}

	private boolean matchesIntegrationObjectItem(final String dtoType)
	{
		return StringUtils.isEmpty(dtoType) || integrationObject.getItems()
		                                                        .stream()
		                                                        .map(IntegrationObjectItemModel::getType)
		                                                        .anyMatch(type -> matchesItemType(type, dtoType));
	}

	private boolean matchesItemType(final ComposedTypeModel ioType, final String changedType)
	{
		return dtoTypeMatchesIntegrationObjectItemType(changedType, ioType)
				|| dtoTypeMatchesIntegrationObjectItemSubType(changedType, ioType);
	}

	private boolean dtoTypeMatchesIntegrationObjectItemType(final String dtoType, final ComposedTypeModel ioType)
	{
		return ioType.getCode().equals(dtoType);
	}

	private boolean dtoTypeMatchesIntegrationObjectItemSubType(final String dtoType, final ComposedTypeModel ioType)
	{
		return ioType.getAllSubTypes()
		             .stream()
		             .anyMatch(type -> dtoTypeMatchesIntegrationObjectItemType(dtoType, type));
	}
}
