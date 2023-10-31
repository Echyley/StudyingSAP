/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.customerticketingocc.converters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.customerticketingfacades.data.StatusData;
import de.hybris.platform.customerticketingocc.dto.ticket.TicketStatusWsDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import ma.glasnost.orika.metadata.TypeFactory;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MapToTicketStatusConverterTest
{
	private static final String STATUS_ID_CLOSED = "CLOSED";
	private static final String STATUS_NAME_CLOSED = "Closed";

	@InjectMocks
	private MapToTicketStatusConverter mapToTicketStatusConverter;
	@Mock
	private MappingContext mappingContext;
	@Mock
	private MapperFacade mapperFacade;

	@Test
	public void testConvertSuccessful()
	{
		final StatusData oldStatusData = new StatusData();
		final StatusData newStatusData = new StatusData();
		final Map<String, List<StatusData>> map = Map.of("Status", List.of(oldStatusData, newStatusData));
		final TicketStatusWsDTO ticketStatusWsDTO = new TicketStatusWsDTO();
		newStatusData.setId(STATUS_ID_CLOSED);
		ticketStatusWsDTO.setId(STATUS_ID_CLOSED);
		ticketStatusWsDTO.setName(STATUS_NAME_CLOSED);

		when(mapperFacade.map(newStatusData, TicketStatusWsDTO.class, mappingContext)).thenReturn(ticketStatusWsDTO);

		TicketStatusWsDTO result = mapToTicketStatusConverter.convert(map, TypeFactory.valueOf(TicketStatusWsDTO.class), mappingContext);

		verifyNoMoreInteractions(mappingContext);
		assertThat(result).isEqualTo(ticketStatusWsDTO);
	}

	@Test
	public void testConvertStatusListIsEmpty()
	{
		final Map<String, List<StatusData>> map = Map.of("Status", new ArrayList<>());
		TicketStatusWsDTO result = mapToTicketStatusConverter.convert(map, TypeFactory.valueOf(TicketStatusWsDTO.class), mappingContext);

		verifyNoMoreInteractions(mappingContext);
		assertThat(result).isNull();
	}

	@Test
	public void testConvertStatusListOnlyHasOneItem()
	{
		final StatusData statusData = new StatusData();
		final Map<String, List<StatusData>> map = Map.of("Status", List.of(statusData));
		TicketStatusWsDTO result = mapToTicketStatusConverter.convert(map, TypeFactory.valueOf(TicketStatusWsDTO.class), mappingContext);

		verifyNoMoreInteractions(mappingContext);
		assertThat(result).isNull();
	}

	@Test
	public void testConvertStatusListHasMultipleItems()
	{
		final StatusData statusData = new StatusData();
		final Map<String, List<StatusData>> map = Map.of("Status", List.of(statusData, statusData, statusData));
		TicketStatusWsDTO result = mapToTicketStatusConverter.convert(map, TypeFactory.valueOf(TicketStatusWsDTO.class), mappingContext);

		verifyNoMoreInteractions(mappingContext);
		assertThat(result).isNull();
	}

	@Test
	public void testConvertStatusListNotInMap()
	{
		final Map<String, List<StatusData>> map = new HashMap<>();
		TicketStatusWsDTO result = mapToTicketStatusConverter.convert(map, TypeFactory.valueOf(TicketStatusWsDTO.class), mappingContext);

		verifyNoMoreInteractions(mappingContext);
		assertThat(result).isNull();
	}

	@Test
	public void testConvertStatusMoreThanOneItemsInMap()
	{
		final StatusData statusData = new StatusData();
		final Map<String, List<StatusData>> map = Map.of("key1", List.of(statusData), "key2", List.of(statusData));
		TicketStatusWsDTO result = mapToTicketStatusConverter.convert(map, TypeFactory.valueOf(TicketStatusWsDTO.class), mappingContext);

		verifyNoMoreInteractions(mappingContext);
		assertThat(result).isNull();
	}

	@Test
	public void testEquals()
	{
		final MapToTicketStatusConverter converter1 = new MapToTicketStatusConverter();
		final MapToTicketStatusConverter converter2 = new MapToTicketStatusConverter();

		assertThat(converter1).isEqualTo(converter2);
	}

	@Test
	public void testEqualsWithSameConverter()
	{
		final MapToTicketStatusConverter converter1 = new MapToTicketStatusConverter();

		assertThat(converter1).isEqualTo(converter1);
	}

	@Test
	public void testEqualsWithDifferentConverter()
	{
		final MapToTicketStatusConverter converter1 = new MapToTicketStatusConverter();
		final CustomConverter<Map<String, List<StatusData>>, TicketStatusWsDTO> converter2 = new CustomConverter<>()
		{
			@Override
			public TicketStatusWsDTO convert(final Map<String, List<StatusData>> source, final Type<? extends TicketStatusWsDTO> destinationType, final MappingContext mappingContext)
			{
				return null;
			}
		};

		assertThat(converter1).isNotEqualTo(converter2);
	}

	@Test
	public void testEqualsWithNullConverter()
	{
		final MapToTicketStatusConverter converter1 = new MapToTicketStatusConverter();

		assertThat(converter1).isNotEqualTo(null);
	}

	@Test
	public void testHashCode()
	{
		final MapToTicketStatusConverter converter1 = new MapToTicketStatusConverter();
		final MapToTicketStatusConverter converter2 = new MapToTicketStatusConverter();

		assertThat(converter1.hashCode()).isEqualTo(converter2.hashCode());
	}

	@Test
	public void testHashCodesWithSameConverter()
	{
		final MapToTicketStatusConverter converter1 = new MapToTicketStatusConverter();

		assertThat(converter1.hashCode()).isEqualTo(converter1.hashCode());
	}

	@Test
	public void testHashCodeWithDifferentConverter()
	{
		final MapToTicketStatusConverter converter1 = new MapToTicketStatusConverter();
		final CustomConverter<Map<String, List<StatusData>>, TicketStatusWsDTO> converter2 = new CustomConverter<>()
		{
			@Override
			public TicketStatusWsDTO convert(final Map<String, List<StatusData>> source, final Type<? extends TicketStatusWsDTO> destinationType, final MappingContext mappingContext)
			{
				return null;
			}
		};

		assertThat(converter1.hashCode()).isNotEqualTo(converter2.hashCode());
	}
}
