/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.customerticketingocc.converters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.customerticketingocc.dto.ticket.TicketAssociatedObjectWsDTO;
import de.hybris.platform.customerticketingocc.errors.exceptions.ConvertException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.TypeFactory;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TicketAssociatedObjectConverterTest
{
	private static final String OBJECT_TYPE = "Order";
	private static final String OBJECT_CODE = "00002000";
	private static final String OBJECT_MODIFIED_AT = "13-01-21";
	private static final String ASSOCIATED_OBJECT_STR = OBJECT_TYPE + ": " + OBJECT_CODE + "; Updated: " + OBJECT_MODIFIED_AT;

	@InjectMocks
	private TicketAssociatedObjectConverter ticketAssociatedObjectConverter;
	@Mock
	private MappingContext mappingContext;
	@Mock
	private MapperFacade mapperFacade;

	@Test
	public void testConvertToSuccessful() throws ParseException
	{
		when(mapperFacade.map(any(), eq(TicketAssociatedObjectWsDTO.class), eq(mappingContext))).thenAnswer(answer -> answer.getArguments()[0]);

		final TicketAssociatedObjectWsDTO result = ticketAssociatedObjectConverter
				.convertTo(ASSOCIATED_OBJECT_STR, TypeFactory.valueOf(TicketAssociatedObjectWsDTO.class), mappingContext);

		final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yy");
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

		verifyNoMoreInteractions(mappingContext);
		assertThat(result).isNotNull()
				.hasFieldOrPropertyWithValue("code", OBJECT_CODE)
				.hasFieldOrPropertyWithValue("type", OBJECT_TYPE)
				.hasFieldOrPropertyWithValue("modifiedAt", simpleDateFormat.parse(OBJECT_MODIFIED_AT));
	}

	@Test
	public void testConvertToSpecialCharactersInCode() throws ParseException
	{
		when(mapperFacade.map(any(), eq(TicketAssociatedObjectWsDTO.class), eq(mappingContext))).thenAnswer(answer -> answer.getArguments()[0]);

		final String associatedObjectStrSpecialChars = OBJECT_TYPE + ": 0000;00:00; Updated: " + OBJECT_MODIFIED_AT ;
		final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yy");
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

		TicketAssociatedObjectWsDTO result = ticketAssociatedObjectConverter
				.convertTo(associatedObjectStrSpecialChars, TypeFactory.valueOf(TicketAssociatedObjectWsDTO.class), mappingContext);

		verifyNoMoreInteractions(mappingContext);
		assertThat(result).isNotNull()
				.hasFieldOrPropertyWithValue("code", "0000;00:00")
				.hasFieldOrPropertyWithValue("type", OBJECT_TYPE)
				.hasFieldOrPropertyWithValue("modifiedAt", simpleDateFormat.parse(OBJECT_MODIFIED_AT));
	}

	@Test
	public void testConvertToInvalidSourceString()
	{
		final String invalidAssociatedObjectStr = OBJECT_TYPE + " " + OBJECT_CODE;

		verifyNoMoreInteractions(mappingContext);
		assertThatThrownBy(() -> ticketAssociatedObjectConverter
				.convertTo(invalidAssociatedObjectStr, TypeFactory.valueOf(TicketAssociatedObjectWsDTO.class), mappingContext))
				.isInstanceOf(ConvertException.class);
	}

	@Test
	public void testConvertToInvalidDate() throws ParseException
	{
		final String invalidAssociatedObjectStr = OBJECT_TYPE + ": " + OBJECT_CODE + "; Updated: " + "01:01:21";

		verifyNoMoreInteractions(mappingContext);
		assertThatThrownBy(() -> ticketAssociatedObjectConverter
				.convertTo(invalidAssociatedObjectStr, TypeFactory.valueOf(TicketAssociatedObjectWsDTO.class), mappingContext))
				.isInstanceOf(ConvertException.class);
	}

	@Test
	public void testConvertFrom()
	{
		final TicketAssociatedObjectWsDTO ticketAssociatedObjectWsDTO = new TicketAssociatedObjectWsDTO();
		ticketAssociatedObjectWsDTO.setCode(OBJECT_CODE);
		ticketAssociatedObjectWsDTO.setType(OBJECT_TYPE);
		String result = ticketAssociatedObjectConverter.convertFrom(ticketAssociatedObjectWsDTO, TypeFactory.valueOf(String.class), mappingContext);

		verifyNoMoreInteractions(mappingContext);
		assertThat(result).isEqualTo(OBJECT_TYPE + "=" + OBJECT_CODE);
	}
}
