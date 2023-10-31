/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.customerticketingocc.converters;

import static de.hybris.platform.customerticketingocc.constants.CustomerticketingoccConstants.I18N_TICKET_CATEGORY_PREFIX;

import de.hybris.platform.customerticketingfacades.data.TicketCategory;
import de.hybris.platform.customerticketingocc.dto.ticket.TicketCategoryWsDTO;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.webservicescommons.mapping.WsDTOMapping;

import javax.annotation.Resource;

import java.util.Locale;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;


@WsDTOMapping
public class TicketCategoryConverter extends BidirectionalConverter<TicketCategory, TicketCategoryWsDTO>
{
	@Resource
	private L10NService l10nService;

	@Override
	public TicketCategoryWsDTO convertTo(final TicketCategory ticketCategory, final Type<TicketCategoryWsDTO> type,
			final MappingContext mappingContext)
	{
		final TicketCategoryWsDTO ticketCategoryWsDTO = new TicketCategoryWsDTO();
		ticketCategoryWsDTO.setId(ticketCategory.name());

		final String categoryNameKey = (I18N_TICKET_CATEGORY_PREFIX + ticketCategory.name()).toLowerCase(Locale.ENGLISH);
		ticketCategoryWsDTO.setName(getL10nService().getLocalizedString(categoryNameKey));

		return mapperFacade.map(ticketCategoryWsDTO, TicketCategoryWsDTO.class, mappingContext);
	}

	@Override
	public TicketCategory convertFrom(final TicketCategoryWsDTO ticketCategoryWsDTO, final Type<TicketCategory> type,
			final MappingContext mappingContext)
	{
		return TicketCategory.valueOf(ticketCategoryWsDTO.getId());
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
