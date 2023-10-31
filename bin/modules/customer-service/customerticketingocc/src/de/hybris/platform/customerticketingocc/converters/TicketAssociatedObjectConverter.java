/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.customerticketingocc.converters;

import de.hybris.platform.customerticketingocc.dto.ticket.TicketAssociatedObjectWsDTO;
import de.hybris.platform.customerticketingocc.errors.exceptions.ConvertException;
import de.hybris.platform.webservicescommons.mapping.WsDTOMapping;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.util.TimeZone;
import java.util.regex.Pattern;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

@WsDTOMapping
public class TicketAssociatedObjectConverter extends BidirectionalConverter<String, TicketAssociatedObjectWsDTO>
{
	private static final int ASSOCIATED_TO_SLICES_LENGTH = 4;
	private static final int ASSOCIATED_TO_SLICES_TYPE_INDEX = 0;
	private static final int ASSOCIATED_TO_SLICES_CODE_INDEX = 1;
	private static final int ASSOCIATED_TO_SLICES_DATE_INDEX = 3;
	private static final Pattern ASSOCIATED_TO_SPLIT_REGEX = Pattern.compile("[:;]\\s+");

	@Override
	public TicketAssociatedObjectWsDTO convertTo(final String associatedTo, final Type<TicketAssociatedObjectWsDTO> type,
			final MappingContext mappingContext)
	{
		// the associatedTo is in format: '<type>: <code>; UPDATED: <date>', eg: Cart: 0000000; UPDATED: 13-1-12
		final String[] parts = ASSOCIATED_TO_SPLIT_REGEX.split(associatedTo);

		if (parts.length != ASSOCIATED_TO_SLICES_LENGTH)
		{
			throw new ConvertException("invalid input, cannot convert to TicketAssociatedObjectWsDTO");
		}

		final TicketAssociatedObjectWsDTO ticketAssociatedObjectWsDTO = new TicketAssociatedObjectWsDTO();
		ticketAssociatedObjectWsDTO.setType(parts[ASSOCIATED_TO_SLICES_TYPE_INDEX]);
		ticketAssociatedObjectWsDTO.setCode(parts[ASSOCIATED_TO_SLICES_CODE_INDEX]);
		try
		{
			final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yy");
			simpleDateFormat.setTimeZone(TimeZone.getTimeZone(ZoneOffset.UTC));
			ticketAssociatedObjectWsDTO.setModifiedAt(simpleDateFormat.parse(parts[ASSOCIATED_TO_SLICES_DATE_INDEX]));
		}
		catch (final ParseException e)
		{
			throw new ConvertException(e.getMessage());
		}
		return mapperFacade.map(ticketAssociatedObjectWsDTO, TicketAssociatedObjectWsDTO.class, mappingContext);
	}

	@Override
	public String convertFrom(final TicketAssociatedObjectWsDTO ticketAssociatedObjectWsDTO, final Type<String> type,
			final MappingContext mappingContext)
	{
		return ticketAssociatedObjectWsDTO.getType() + "=" + ticketAssociatedObjectWsDTO.getCode();
	}
}
