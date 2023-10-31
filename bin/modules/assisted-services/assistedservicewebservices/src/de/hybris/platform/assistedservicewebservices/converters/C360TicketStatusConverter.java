/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.assistedservicewebservices.converters;

import static de.hybris.platform.assistedservicewebservices.constants.AssistedservicewebservicesConstants.I18N_TICKET_STATUS_PREFIX;

import de.hybris.platform.assistedservicewebservices.dto.C360TicketStatusWsDTO;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.webservicescommons.mapping.WsDTOMapping;

import javax.annotation.Resource;

import java.util.Locale;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;


@WsDTOMapping
public class C360TicketStatusConverter extends CustomConverter<String, C360TicketStatusWsDTO>
{
	@Resource
	private L10NService l10nService;

	@Override
	public C360TicketStatusWsDTO convert(final String ticketStatusCode,
			final Type<? extends C360TicketStatusWsDTO> type, final MappingContext mappingContext)
	{
		final C360TicketStatusWsDTO c360TicketStatusWsDTO = new C360TicketStatusWsDTO();
		c360TicketStatusWsDTO.setCode(ticketStatusCode);
		final String statusNameKey = (I18N_TICKET_STATUS_PREFIX + ticketStatusCode).toLowerCase(Locale.ENGLISH);
		c360TicketStatusWsDTO.setName(getL10nService().getLocalizedString(statusNameKey));
		return mapperFacade.map(c360TicketStatusWsDTO, C360TicketStatusWsDTO.class, mappingContext);
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
