/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundsync.job.impl;

import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.outboundsync.dto.OutboundItemDTO;
import de.hybris.platform.outboundsync.job.SyncFilter;

import java.util.Optional;

import org.apache.commons.lang.StringUtils;


/**
 * A filter for non-delete changes collected by {@link DefaultFilteringService}. If the changed item type
 * has a root item, then the change is passed through. Otherwise the change is filtered out.
 */
public class ChangeSyncFilter implements SyncFilter
{
	@Override
	public boolean isApplicable(final OutboundItemDTO dto)
	{
		return dto != null && !dto.isDeleted();
	}

	@Override
	public Optional<OutboundItemDTO> evaluate(final OutboundItemDTO dto, final IntegrationObjectModel io)
	{
		if (dto != null)
		{
			return StringUtils.isBlank(dto.getItemType()) ? Optional.of(dto) : internalEvaluate(dto, io);
		}
		return Optional.empty();
	}

	private Optional<OutboundItemDTO> internalEvaluate(final OutboundItemDTO dto,
	                                                   final IntegrationObjectModel io)
	{
		return Optional.of(dto).filter(outboundItemDTO -> matchesIntegrationObjectItem(dto, io));
	}

	private boolean matchesIntegrationObjectItem(final OutboundItemDTO dto, final IntegrationObjectModel io)
	{
		final String dtoType = dto.getItemType();
		return StringUtils.isEmpty(dtoType) || io != null && io.getItems()
		                                                       .stream()
		                                                       .map(IntegrationObjectItemModel::getType)
		                                                       .anyMatch(type -> matchesItemType(dtoType, type));
	}

	private boolean matchesItemType(final String changedType, final ComposedTypeModel ioType)
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