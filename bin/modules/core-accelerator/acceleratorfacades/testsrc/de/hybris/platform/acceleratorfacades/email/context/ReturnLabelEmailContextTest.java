/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorfacades.email.context;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ReturnLabelEmailContextTest
{
	private final ReturnLabelEmailContext returnLabelEmailContext = new ReturnLabelEmailContext();
	@Mock
	private Converter<OrderModel, OrderData> orderConverter;
	@Mock
	private ReturnProcessModel returnProcessModel;
	@Mock
	private EmailPageModel emailPageModel;
	@Mock
	private ReturnRequestModel returnRequestModel;
	@Mock
	private OrderModel orderModel;

	@Before
	public void setUp()
	{
		returnLabelEmailContext.setOrderConverter(orderConverter);
	}

	@Test
	public void testForInit()
	{
		OrderData orderData = new OrderData();
		mockGetOrder();
		when(orderConverter.convert(orderModel)).thenReturn(orderData);

		returnLabelEmailContext.init(returnProcessModel, emailPageModel);

		assertEquals(orderData, returnLabelEmailContext.getOrder());
		verify(orderConverter).convert(orderModel);
	}

	@Test
	public void testGetSite()
	{
		BaseSiteModel site = new BaseSiteModel();
		mockGetOrder();
		when(orderModel.getSite()).thenReturn(site);

		assertEquals(site, returnLabelEmailContext.getSite(returnProcessModel));
	}

	@Test
	public void testGetCustomer()
	{
		CustomerModel customer = new CustomerModel();
		mockGetOrder();
		when(orderModel.getUser()).thenReturn(customer);

		assertEquals(customer, returnLabelEmailContext.getCustomer(returnProcessModel));
	}

	@Test
	public void testGetEmailLanguage()
	{
		LanguageModel language = new LanguageModel();
		mockGetOrder();
		when(orderModel.getLanguage()).thenReturn(language);

		assertEquals(language, returnLabelEmailContext.getEmailLanguage(returnProcessModel));
	}

	private void mockGetOrder()
	{
		when(returnProcessModel.getReturnRequest()).thenReturn(returnRequestModel);
		when(returnRequestModel.getOrder()).thenReturn(orderModel);
	}
}
