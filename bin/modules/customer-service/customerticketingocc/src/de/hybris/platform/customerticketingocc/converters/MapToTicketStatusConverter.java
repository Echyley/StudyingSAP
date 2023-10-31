/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.customerticketingocc.converters;

import de.hybris.platform.customerticketingfacades.data.StatusData;
import de.hybris.platform.customerticketingocc.dto.ticket.TicketStatusWsDTO;
import de.hybris.platform.webservicescommons.mapping.WsDTOMapping;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;

import java.util.List;
import java.util.Map;

@WsDTOMapping
public class MapToTicketStatusConverter extends CustomConverter<Map<String, List<StatusData>>, TicketStatusWsDTO>
{
	// should only have 1 modified field -> state.
	private static final int MODIFIED_FIELDS_SIZE = 1;
	private static final int STATUS_MODIFICATION_RECORDS_COUNT = 2;

	@Override
	public TicketStatusWsDTO convert(final Map<String, List<StatusData>> modifiedFields,
			final Type<? extends TicketStatusWsDTO> type, final MappingContext mappingContext)
	{
		final List<StatusData> statusDataList = MODIFIED_FIELDS_SIZE == modifiedFields.size() ? modifiedFields.values().stream().findFirst().orElse(null) : null;
		final boolean invalidTicketStatus = (null == statusDataList || STATUS_MODIFICATION_RECORDS_COUNT != statusDataList.size());
		if (invalidTicketStatus)
		{
			return null;
		}

		final StatusData statusData = statusDataList.get(1);
		return mapperFacade.map(statusData, TicketStatusWsDTO.class, mappingContext);
	}

	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}

		return o != null && getClass() == o.getClass();
	}

	@Override
	public int hashCode()
	{
		return getClass().hashCode();
	}
}
