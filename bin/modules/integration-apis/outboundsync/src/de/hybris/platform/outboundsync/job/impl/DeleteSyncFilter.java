/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundsync.job.impl;

import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.outboundsync.OutboundSyncFeature;
import de.hybris.platform.outboundsync.dto.OutboundItemDTO;
import de.hybris.platform.outboundsync.job.SyncFilter;

import java.util.Optional;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;

/**
 * A filter for changes collected by {@link StreamingChangesCollector}, that passes the change through, if the changed item type
 * and type of the root item, it relates to, belong to the integration object. Otherwise the change is filtered out.
 */
public class DeleteSyncFilter implements SyncFilter
{

	@Override
	public boolean isApplicable(final OutboundItemDTO dto)
	{
		return dto != null && dto.isDeleted();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<OutboundItemDTO> evaluate(final OutboundItemDTO dto, final IntegrationObjectModel io)
	{
		return dto != null ? Optional.of(dto)
		               .filter(itemDTO -> io != null)
		               .filter(itemDTO -> isDeleteFeatureEnabled())
		               .filter(this::typeAndRootTypeArePresent)
		               .filter(itemDTO -> itemTypeIsRootType(itemDTO, io)) : Optional.empty();
	}

	private boolean typeAndRootTypeArePresent(final @NotNull OutboundItemDTO itemDTO)
	{
		return StringUtils.isNotBlank(itemDTO.getItemType()) && StringUtils.isNotBlank(itemDTO.getRootItemType());
	}

	// Remove when feature is fully implemented
	private boolean isDeleteFeatureEnabled()
	{
		return OutboundSyncFeature.DELETE.isEnabled();
	}

	private boolean itemTypeIsRootType(final OutboundItemDTO dto, final IntegrationObjectModel io)
	{
		return matchesItemType(io.getRootItem().getType(), dto.getItemType());
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