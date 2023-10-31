/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.c4c.customer.interceptor;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.testframework.Assert;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.sap.hybris.c4c.customer.constants.Sapc4ccustomerb2cConstants;
import com.sap.hybris.c4c.customer.dto.C4CCustomerData;
import com.sap.hybris.c4c.customer.event.SapC4cCustomerUpdateEvent;
import com.sap.hybris.c4c.customer.util.SapC4cCustomerUtils;


/**
 *
 */
@UnitTest
public class DefaultSapC4cCustomerInterceptorTest
{
	@InjectMocks
	private final DefaultSapC4cCustomerInterceptor sapC4cCustomerInterceptor = new DefaultSapC4cCustomerInterceptor();

	@Mock
	private EventService eventService;

	@Mock
	private SapC4cCustomerUtils customerUtil;
	
	@Mock
	private ConfigurationService configurationService;
	
	@Mock
	private Configuration configuration;



	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testOnValidate() throws InterceptorException
	{
		final CustomerModel customerModel = new CustomerModel();
		setCustomerDetails(customerModel);

		final InterceptorContext ctx = Mockito.mock(InterceptorContext.class);
		when(ctx.isNew(Mockito.any(CustomerModel.class))).thenReturn(Boolean.FALSE);
		when(ctx.isModified(Mockito.any(CustomerModel.class), Mockito.anyString())).thenReturn(Boolean.TRUE);
		
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getString(Mockito.eq(Sapc4ccustomerb2cConstants.C4C_CUSTOMER_CPI_REPLICATE))).thenReturn("true");

		final C4CCustomerData customerData = new C4CCustomerData();
		when(customerUtil.getCustomerDataForCustomer(Mockito.any(CustomerModel.class), Mockito.anyList())).thenReturn(customerData);
		doNothing().when(eventService).publishEvent(Mockito.any(SapC4cCustomerUpdateEvent.class));

		sapC4cCustomerInterceptor.onValidate(customerModel, ctx);

		verify(eventService, times(1)).publishEvent(Mockito.any(SapC4cCustomerUpdateEvent.class));

	}

	@Test
	public void testShouldReplicate()
	{
		final CustomerModel customerModel = new CustomerModel();
		setCustomerDetails(customerModel);

		final InterceptorContext ctx = Mockito.mock(InterceptorContext.class);
		when(ctx.isNew(Mockito.any(CustomerModel.class))).thenReturn(Boolean.FALSE);
		when(ctx.isModified(Mockito.any(CustomerModel.class), Mockito.anyString())).thenReturn(Boolean.TRUE);

		final boolean result = sapC4cCustomerInterceptor.shouldReplicate(customerModel, ctx);

		Assert.assertEquals(true, result);

	}

	@Test
	public void testShouldReplicateFalse()
	{
		final CustomerModel customerModel = new CustomerModel();
		setCustomerDetails(customerModel);

		final InterceptorContext ctx = Mockito.mock(InterceptorContext.class);
		when(ctx.isModified(Mockito.any(CustomerModel.class), Mockito.anyString())).thenReturn(Boolean.FALSE);

		final boolean result = sapC4cCustomerInterceptor.shouldReplicate(customerModel, ctx);

		Assert.assertEquals(false, result);

	}

	private void setCustomerDetails(final CustomerModel customer)
	{
		customer.setCustomerID("12345");
		customer.setName("electronics customer");

		final AddressModel addressModel = new AddressModel();
		customer.setDefaultShipmentAddress(addressModel);
		customer.setDefaultPaymentAddress(addressModel);

		addressModel.setEmail("address@mail.com");
		addressModel.setStreetnumber("street number");
		addressModel.setStreetname("street name");
		addressModel.setTown("town");
		final CountryModel country = Mockito.mock(CountryModel.class);
		when(country.getIsocode()).thenReturn("DE");
		addressModel.setCountry(country);
		addressModel.setPostalcode("12345");
		addressModel.setPobox("12345");
		addressModel.setPhone1("9123123123");
		addressModel.setCellphone("9121212121");
		addressModel.setOwner(customer);

		final List<AddressModel> addresses = new ArrayList<>();
		addresses.add(addressModel);
		customer.setAddresses(addresses);
	}

}
