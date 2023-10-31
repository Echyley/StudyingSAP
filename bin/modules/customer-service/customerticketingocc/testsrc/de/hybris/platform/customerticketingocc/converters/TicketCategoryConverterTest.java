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
import de.hybris.platform.customerticketingfacades.data.TicketCategory;
import de.hybris.platform.customerticketingocc.dto.ticket.TicketCategoryWsDTO;
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
public class TicketCategoryConverterTest
{
	private static final String TICKET_CATEGORY_ID_ENQUIRY = "ENQUIRY";
	private static final String TICKET_CATEGORY_NAME_ENQUIRY = "Enquiry";

	@InjectMocks
	private TicketCategoryConverter ticketCategoryConverter;
	@Mock
	private L10NService l10NService;
	@Mock
	private MappingContext mappingContext;
	@Mock
	private MapperFacade mapperFacade;

	@Test
	public void testConvertToSuccessful()
	{
		when(l10NService.getLocalizedString("text.account.supporttickets.createticket.ticketcategory.enquiry")).thenReturn(TICKET_CATEGORY_NAME_ENQUIRY);
		when(mapperFacade.map(any(), eq(TicketCategoryWsDTO.class), eq(mappingContext))).thenAnswer(answer -> answer.getArguments()[0]);
		TicketCategoryWsDTO result = ticketCategoryConverter.convertTo(TicketCategory.ENQUIRY, TypeFactory.valueOf(TicketCategoryWsDTO.class), mappingContext);

		verifyNoMoreInteractions(mappingContext);
		assertThat(result).isNotNull()
				.hasFieldOrPropertyWithValue("id", TICKET_CATEGORY_ID_ENQUIRY)
				.hasFieldOrPropertyWithValue("name", TICKET_CATEGORY_NAME_ENQUIRY);
	}

	@Test
	public void testConvertFromSuccessful()
	{
		final TicketCategoryWsDTO ticketCategoryWsDTO = new TicketCategoryWsDTO();
		ticketCategoryWsDTO.setId(TICKET_CATEGORY_ID_ENQUIRY);
		ticketCategoryWsDTO.setName(TICKET_CATEGORY_NAME_ENQUIRY);
		TicketCategory result = ticketCategoryConverter.convertFrom(ticketCategoryWsDTO, TypeFactory.valueOf(TicketCategory.class), mappingContext);

		verifyNoMoreInteractions(mappingContext);
		assertThat(result).isEqualTo(TicketCategory.ENQUIRY);
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
		final BidirectionalConverter<TicketCategory, TicketCategoryWsDTO> converter2 = new BidirectionalConverter<>()
		{
			@Override
			public TicketCategoryWsDTO convertTo(final TicketCategory source, final Type<TicketCategoryWsDTO> destinationType, final MappingContext mappingContext)
			{
				return null;
			}

			@Override
			public TicketCategory convertFrom(final TicketCategoryWsDTO source, final Type<TicketCategory> destinationType, final MappingContext mappingContext)
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
		final BidirectionalConverter<TicketCategory, TicketCategoryWsDTO> converter2 = new BidirectionalConverter<>()
		{
			@Override
			public TicketCategoryWsDTO convertTo(final TicketCategory source, final Type<TicketCategoryWsDTO> destinationType, final MappingContext mappingContext)
			{
				return null;
			}

			@Override
			public TicketCategory convertFrom(final TicketCategoryWsDTO source, final Type<TicketCategory> destinationType, final MappingContext mappingContext)
			{
				return null;
			}
		};

		assertThat(converter1.hashCode()).isNotEqualTo(converter2.hashCode());
	}
}
