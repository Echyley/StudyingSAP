/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.customerticketingocc.converters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.customerticketingfacades.data.StatusData;
import de.hybris.platform.customerticketingocc.dto.ticket.TicketStatusWsDTO;
import de.hybris.platform.servicelayer.i18n.L10NService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import ma.glasnost.orika.metadata.TypeFactory;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TicketStatusConverterTest
{
	private static final String STATUS_ID_CLOSED = "CLOSED";
	private static final String STATUS_NAME_CLOSED = "Closed";

	@InjectMocks
	private TicketStatusConverter ticketStatusConverter;
	@Mock
	private L10NService l10NService;
	@Mock
	private MappingContext mappingContext;
	@Mock
	private MapperFacade mapperFacade;

	@Test
	public void testConvertToSuccessful()
	{
		when(l10NService.getLocalizedString("ticketstatus.closed")).thenReturn(STATUS_NAME_CLOSED);
		when(mapperFacade.map(any(), eq(TicketStatusWsDTO.class), eq(mappingContext))).thenAnswer(answer -> answer.getArguments()[0]);

		final StatusData statusData = new StatusData();
		statusData.setId(STATUS_ID_CLOSED);

		TicketStatusWsDTO result = ticketStatusConverter.convertTo(statusData, TypeFactory.valueOf(TicketStatusWsDTO.class), mappingContext);

		verifyNoMoreInteractions(mappingContext);
		assertThat(result).isNotNull()
				.hasFieldOrPropertyWithValue("id", STATUS_ID_CLOSED)
				.hasFieldOrPropertyWithValue("name", STATUS_NAME_CLOSED);
	}

	@Test
	public void testConvertFromSuccessful()
	{
		final TicketStatusWsDTO ticketStatusWsDTO = new TicketStatusWsDTO();
		ticketStatusWsDTO.setId(STATUS_ID_CLOSED);
		ticketStatusWsDTO.setName(STATUS_NAME_CLOSED);
		StatusData result = ticketStatusConverter.convertFrom(ticketStatusWsDTO, TypeFactory.valueOf(StatusData.class), mappingContext);

		verifyNoMoreInteractions(mappingContext);
		assertThat(result).isNotNull().hasFieldOrPropertyWithValue("id", STATUS_ID_CLOSED);
	}

	@Test
	public void testEquals()
	{
		final TicketCategoryConverter converter1 = new TicketCategoryConverter();
		final TicketCategoryConverter converter2 = new TicketCategoryConverter();

		assertThat(converter1).isEqualTo(converter2);
	}

	@Test
	public void testEqualsWithSameConverter()
	{
		final TicketCategoryConverter converter1 = new TicketCategoryConverter();

		assertThat(converter1).isEqualTo(converter1);
	}

	@Test
	public void testEqualsWithDifferentConverter()
	{
		final TicketCategoryConverter converter1 = new TicketCategoryConverter();
		final BidirectionalConverter<StatusData, TicketStatusWsDTO> converter2 = new BidirectionalConverter<>()
		{
			@Override
			public TicketStatusWsDTO convertTo(final StatusData source, final Type<TicketStatusWsDTO> destinationType, final MappingContext mappingContext)
			{
				return null;
			}

			@Override
			public StatusData convertFrom(final TicketStatusWsDTO source, final Type<StatusData> destinationType, final MappingContext mappingContext)
			{
				return null;
			}
		};

		assertThat(converter1).isNotEqualTo(converter2);
	}

	@Test
	public void testEqualsWithNullConverter()
	{
		final TicketCategoryConverter converter1 = new TicketCategoryConverter();

		assertThat(converter1).isNotEqualTo(null);
	}

	@Test
	public void testHashCode()
	{
		final TicketCategoryConverter converter1 = new TicketCategoryConverter();
		final TicketCategoryConverter converter2 = new TicketCategoryConverter();

		assertThat(converter1.hashCode()).isEqualTo(converter2.hashCode());
	}

	@Test
	public void testHashCodesWithSameConverter()
	{
		final TicketCategoryConverter converter1 = new TicketCategoryConverter();

		assertThat(converter1.hashCode()).isEqualTo(converter1.hashCode());
	}

	@Test
	public void testHashCodeWithDifferentConverter()
	{
		final TicketCategoryConverter converter1 = new TicketCategoryConverter();
		final BidirectionalConverter<StatusData, TicketStatusWsDTO> converter2 = new BidirectionalConverter<>()
		{
			@Override
			public TicketStatusWsDTO convertTo(final StatusData source, final Type<TicketStatusWsDTO> destinationType, final MappingContext mappingContext)
			{
				return null;
			}

			@Override
			public StatusData convertFrom(final TicketStatusWsDTO source, final Type<StatusData> destinationType, final MappingContext mappingContext)
			{
				return null;
			}
		};

		assertThat(converter1.hashCode()).isNotEqualTo(converter2.hashCode());
	}
}
