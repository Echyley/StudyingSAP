/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorfacades.urlencoder.attributes;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorfacades.urlencoder.data.UrlEncoderData;
import de.hybris.platform.acceleratorfacades.urlencoder.impl.DefaultUrlEncoderFacade;
import de.hybris.platform.acceleratorservices.urlencoder.attributes.UrlEncodingAttributeManager;
import de.hybris.platform.acceleratorservices.urlencoder.attributes.impl.DefaultCurrencyAttributeManager;
import de.hybris.platform.acceleratorservices.urlencoder.attributes.impl.DefaultLanguageAttributeManager;
import de.hybris.platform.acceleratorservices.urlencoder.impl.DefaultUrlEncoderService;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.storesession.StoreSessionFacade;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultUrlEncoderFacadeTest
{
	private DefaultUrlEncoderFacade urlEncoderFacade;

	@Mock
	private SessionService sessionService;

	@Mock
	private StoreSessionFacade storeSessionFacade;

	@Mock
	private CMSSiteService cmsSiteService;

	@Mock
	private DefaultCurrencyAttributeManager currencyAttributeManager;

	@Mock
	private DefaultLanguageAttributeManager languageAttributeManager;

	@Mock
	private DefaultUrlEncoderService urlEncoderService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		urlEncoderFacade = new DefaultUrlEncoderFacade();

		final CMSSiteModel cmsSiteModel = new CMSSiteModel();
		cmsSiteModel.setUid("electronics");

		final List<String> urlEncodingAttrList = new ArrayList<java.lang.String>();
		urlEncodingAttrList.add("language");
		urlEncodingAttrList.add("currency");
		cmsSiteModel.setUrlEncodingAttributes(urlEncodingAttrList);

		final LanguageData languageDataData = new LanguageData();
		languageDataData.setIsocode("en");

		final CurrencyData currencyData = new CurrencyData();
		currencyData.setIsocode("USD");

		given(storeSessionFacade.getDefaultCurrency()).willReturn(currencyData);
		given(storeSessionFacade.getDefaultLanguage()).willReturn(languageDataData);


		final UrlEncoderData currencyUrlEncoderData = new UrlEncoderData();
		currencyUrlEncoderData.setAttributeName("currency");
		currencyUrlEncoderData.setDefaultValue("USD");
		currencyUrlEncoderData.setCurrentValue("USD");

		final UrlEncoderData languageUrlEncoderData = new UrlEncoderData();
		languageUrlEncoderData.setAttributeName("language");
		languageUrlEncoderData.setDefaultValue("en");
		languageUrlEncoderData.setCurrentValue("en");

		final List<UrlEncoderData> urlEncoderDataList = new ArrayList<UrlEncoderData>();
		urlEncoderDataList.add(currencyUrlEncoderData);
		urlEncoderDataList.add(languageUrlEncoderData);

		given(sessionService.getAttribute("urlEncodingData")).willReturn(urlEncoderDataList);

		urlEncoderFacade.setSessionService(sessionService);
		urlEncoderFacade.setUrlEncoderService(urlEncoderService);

		final List<String> availableCurrencyList = new ArrayList<String>();
		availableCurrencyList.add("USD");
		availableCurrencyList.add("JPY");
		given(currencyAttributeManager.getAllAvailableValues()).willReturn(availableCurrencyList);
		given(Boolean.valueOf(currencyAttributeManager.isValid("USD"))).willReturn(Boolean.TRUE);
		given(Boolean.valueOf(currencyAttributeManager.isValid("JPY"))).willReturn(Boolean.TRUE);

		final List<String> availableLanguageList = new ArrayList<String>();
		availableLanguageList.add("en");
		availableLanguageList.add("ja");
		given(languageAttributeManager.getAllAvailableValues()).willReturn(availableLanguageList);
		given(Boolean.valueOf(languageAttributeManager.isValid("en"))).willReturn(Boolean.TRUE);
		given(Boolean.valueOf(languageAttributeManager.isValid("ja"))).willReturn(Boolean.TRUE);

		final Map<String, UrlEncodingAttributeManager> attributeManagerMap = new HashMap<String, UrlEncodingAttributeManager>();
		attributeManagerMap.put("language", languageAttributeManager);
		attributeManagerMap.put("currency", currencyAttributeManager);

		given(urlEncoderService.getUrlEncodingAttrManagerMap()).willReturn(attributeManagerMap);

	}

	@Test
	public void testCalculateAndUpdateUrlEncodingDataForDefault()
	{
		Assert.assertEquals("USD/en", urlEncoderFacade.calculateAndUpdateUrlEncodingData("/teststorefront/", "teststorefront"));
	}

	@Test
	public void testCalculateAndUpdateUrlEncodingDataForNullUri()
	{
		Assert.assertEquals("", urlEncoderFacade.calculateAndUpdateUrlEncodingData(null, "teststorefront"));
	}


	@Test
	public void testCalculateAndUpdateUrlEncodingDataForAValidUrl()
	{
		given(currencyAttributeManager.getCurrentValue()).willReturn("JPY");
		given(languageAttributeManager.getCurrentValue()).willReturn("ja");
		Assert.assertEquals("JPY/ja",
				urlEncoderFacade.calculateAndUpdateUrlEncodingData("/teststorefront/JPY/ja/", "teststorefront"));
	}


	@Test
	public void testCalculateAndUpdateUrlEncodingDataForInvalidAttr()
	{
		given(currencyAttributeManager.getCurrentValue()).willReturn("USD");
		given(languageAttributeManager.getCurrentValue()).willReturn("en");
		Assert.assertEquals("USD/en", urlEncoderFacade.calculateAndUpdateUrlEncodingData(
				"/teststorefront/invalidLanguage/invalidCurrency/", "teststorefront"));
	}


	@Test
	public void testCalculateAndUpdateUrlEncodingDataForMissingAttr()
	{
		given(currencyAttributeManager.getCurrentValue()).willReturn("JPY");
		given(languageAttributeManager.getCurrentValue()).willReturn("en");
		Assert.assertEquals("JPY/en", urlEncoderFacade.calculateAndUpdateUrlEncodingData("/teststorefront/JPY/", "teststorefront"));
	}
}
