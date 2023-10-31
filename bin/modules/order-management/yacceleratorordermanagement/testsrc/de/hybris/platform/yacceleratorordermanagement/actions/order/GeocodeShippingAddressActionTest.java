/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.yacceleratorordermanagement.actions.order;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.storelocator.GPS;
import de.hybris.platform.storelocator.GeoWebServiceWrapper;
import de.hybris.platform.storelocator.data.AddressData;
import de.hybris.platform.storelocator.exception.GeoServiceWrapperException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoSession;


@UnitTest
public class GeocodeShippingAddressActionTest
{
	@Mock
	private GeoWebServiceWrapper geoWebServiceWrapper;

	@Mock
	private Converter addressConverter;

	@Mock
	private ModelService modelService;

	@Mock
	private GPS gps;

	@Mock
	private OrderModel order;

	private AddressModel address;

	@InjectMocks
	private GeocodeShippingAddressAction action;

	MockitoSession mockito;

	@Before
	public void setUp()
	{
		mockito = Mockito.mockitoSession().initMocks(this).startMocking();
		address = new AddressModel();
	}

	@After
	public void cleanUp() throws Exception
	{
		mockito.finishMocking();
	}

	@Test
	public void shouldObtainLatitudeAndLongitudeFromDeliveryAddress()
	{
		Mockito.lenient().when(geoWebServiceWrapper.geocodeAddress(nullable(AddressData.class))).thenReturn(gps);
		Mockito.lenient().when(gps.getDecimalLatitude()).thenReturn(47.00);
		Mockito.lenient().when(gps.getDecimalLongitude()).thenReturn(35.50);
		Mockito.lenient().when(order.getDeliveryAddress()).thenReturn(address);

		final OrderProcessModel orderProcess = setupBasicOrderProcessModel();
		action.executeAction(orderProcess);

		verify(geoWebServiceWrapper).geocodeAddress(nullable(AddressData.class));
		assertEquals(47.00, orderProcess.getOrder().getDeliveryAddress().getLatitude(),0.0000000001);
		assertEquals(35.50, orderProcess.getOrder().getDeliveryAddress().getLongitude(), 0.0000000001);
	}

	@Test
	public void shouldNotFailWhenGeoWebServiceWrapperIsUnavailable()
	{
		Mockito.lenient().when(geoWebServiceWrapper.geocodeAddress(nullable(AddressData.class))).thenThrow(new GeoServiceWrapperException());

		final OrderProcessModel orderProcess = setupBasicOrderProcessModel();
		action.executeAction(orderProcess);

		verify(geoWebServiceWrapper).geocodeAddress(nullable(AddressData.class));

		assertNull(orderProcess.getOrder().getDeliveryAddress().getLatitude());
		assertNull(orderProcess.getOrder().getDeliveryAddress().getLongitude());
	}

	private OrderProcessModel setupBasicOrderProcessModel()
	{
		final CountryModel unitedStates = new CountryModel();
		unitedStates.setIsocode("US");

		final AddressModel broadwayNewYork = new AddressModel();
		broadwayNewYork.setCountry(unitedStates);
		broadwayNewYork.setDistrict("New York");
		broadwayNewYork.setStreetname("Broadway avenue");
		broadwayNewYork.setPostalcode("100001");

		final OrderModel order = new OrderModel();
		order.setDeliveryAddress(broadwayNewYork);

		final OrderProcessModel orderProcess = new OrderProcessModel();
		orderProcess.setOrder(order);

		return orderProcess;
	}

}
