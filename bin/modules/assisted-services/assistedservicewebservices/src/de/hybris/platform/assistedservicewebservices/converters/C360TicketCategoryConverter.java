/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.assistedservicewebservices.converters;

import static de.hybris.platform.assistedservicewebservices.constants.AssistedservicewebservicesConstants.I18N_TICKET_CATEGORY_PREFIX;

import de.hybris.platform.assistedservicewebservices.dto.C360TicketCategoryWsDTO;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.webservicescommons.mapping.WsDTOMapping;

import javax.annotation.Resource;

import java.util.Locale;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;


@WsDTOMapping
public class C360TicketCategoryConverter extends CustomConverter<String, C360TicketCategoryWsDTO>
{
	@Resource
	private L10NService l10nService;

	@Override
	public C360TicketCategoryWsDTO convert(final String ticketCategoryCode,
			final Type<? extends C360TicketCategoryWsDTO> type, final MappingContext mappingContext)
	{
		final C360TicketCategoryWsDTO c360TicketCategoryWsDTO = new C360TicketCategoryWsDTO();
		c360TicketCategoryWsDTO.setCode(ticketCategoryCode);
		final String statusNameKey = (I18N_TICKET_CATEGORY_PREFIX + ticketCategoryCode).toLowerCase(Locale.ENGLISH);
		c360TicketCategoryWsDTO.setName(getL10nService().getLocalizedString(statusNameKey));
		return mapperFacade.map(c360TicketCategoryWsDTO, C360TicketCategoryWsDTO.class, mappingContext);
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
