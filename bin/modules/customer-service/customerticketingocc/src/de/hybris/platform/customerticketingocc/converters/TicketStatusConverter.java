/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.customerticketingocc.converters;

import static de.hybris.platform.customerticketingocc.constants.CustomerticketingoccConstants.I18N_TICKET_STATUS_PREFIX;

import de.hybris.platform.customerticketingfacades.data.StatusData;
import de.hybris.platform.customerticketingocc.dto.ticket.TicketStatusWsDTO;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.webservicescommons.mapping.WsDTOMapping;

import javax.annotation.Resource;

import java.util.Locale;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

@WsDTOMapping
public class TicketStatusConverter extends BidirectionalConverter<StatusData, TicketStatusWsDTO>
{
	@Resource
	private L10NService l10nService;

	@Override
	public TicketStatusWsDTO convertTo(final StatusData statusData, final Type<TicketStatusWsDTO> type,
			final MappingContext mappingContext)
	{
		final TicketStatusWsDTO ticketStatusWsDTO = new TicketStatusWsDTO();
		ticketStatusWsDTO.setId(statusData.getId());

		final String statusNameKey = (I18N_TICKET_STATUS_PREFIX + statusData.getId()).toLowerCase(Locale.ENGLISH);
		ticketStatusWsDTO.setName(getL10nService().getLocalizedString(statusNameKey));
		return mapperFacade.map(ticketStatusWsDTO, TicketStatusWsDTO.class, mappingContext);
	}

	@Override
	public StatusData convertFrom(final TicketStatusWsDTO ticketStatusWsDTO, final Type<StatusData> type,
			final MappingContext mappingContext)
	{
		final StatusData statusData = new StatusData();
		statusData.setId(ticketStatusWsDTO.getId());
		return statusData;
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

	public L10NService getL10nService()
	{
		return l10nService;
	}

	public void setL10nService(final L10NService l10nService)
	{
		this.l10nService = l10nService;
	}
}
