/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.facades.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.cpq.productconfig.facades.data.CommerceParameters;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Unit test for {@link DefaultCommerceParametersFacade}
 */
@UnitTest
public class DefaultCommerceParametersFacadeTest
{
	private static final String CURRENCY = "EUR";
	private static final String LOCALE = "de-DE";

	private DefaultCommerceParametersFacade classUnderTest;

	@Mock
	private I18NService i18NService;

	@Mock
	private CommonI18NService commonI18NService;

	@Mock
	private CurrencyModel currencyModel;


	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new DefaultCommerceParametersFacade(i18NService, commonI18NService);
	}

	@Test
	public void testAddConfigurationToCart() throws Exception
	{
		given(i18NService.getCurrentLocale()).willReturn(Locale.GERMANY);
		given(commonI18NService.getCurrentCurrency()).willReturn(currencyModel);
		given(currencyModel.getIsocode()).willReturn(CURRENCY);
		final CommerceParameters commerceParameters = classUnderTest.prepareCommerceParameters();
		assertEquals(CURRENCY, commerceParameters.getCurrency());
		assertEquals(LOCALE, commerceParameters.getLocale());
	}

}
