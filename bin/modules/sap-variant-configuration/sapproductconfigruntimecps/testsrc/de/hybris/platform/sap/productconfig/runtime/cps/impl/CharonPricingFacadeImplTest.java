/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.apiregistryservices.exceptions.CredentialException;
import de.hybris.platform.apiregistryservices.services.ApiRegistryClientService;
import de.hybris.platform.sap.productconfig.runtime.cps.ProductConfigurationPassportService;
import de.hybris.platform.sap.productconfig.runtime.cps.RequestErrorHandler;
import de.hybris.platform.sap.productconfig.runtime.cps.client.PricingClient;
import de.hybris.platform.sap.productconfig.runtime.cps.client.PricingClientBase;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingDocumentInput;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingDocumentResult;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingEngineException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.hybris.charon.exp.HttpException;

import rx.Observable;


/**
 * Test class for {@link CharonPricingFacadeImpl}
 */
@UnitTest
public class CharonPricingFacadeImplTest
{
	private static final String SAP_PASSPORT = "passport";
	private CharonPricingFacadeImpl classUnderTest;
	@Mock
	private RequestErrorHandler errorHandler;

	@Mock
	private PricingClient client;

	@Mock
	private ApiRegistryClientService apiRegistryClientService;

	@Mock
	private ProductConfigurationPassportService productConfigurationPassportService;

	@Before
	public void setup() throws CredentialException
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new CharonPricingFacadeImpl();
		classUnderTest.setRequestErrorHandler(errorHandler);
		classUnderTest.setApiRegistryClientService(apiRegistryClientService);
		when(apiRegistryClientService.lookupClient(PricingClient.class)).thenReturn(client);
		classUnderTest.setProductConfigurationPassportService(productConfigurationPassportService);

		Mockito.when(productConfigurationPassportService.generate(CharonPricingFacadeImpl.PASSPORT_PRICING))
				.thenReturn(SAP_PASSPORT);
	}

	@Test
	public void testProductConfigurationPassportService()
	{
		assertEquals(productConfigurationPassportService, classUnderTest.getProductConfigurationPassportService());
	}

	@Test
	public void testApiRegistryClientService()
	{
		classUnderTest.setApiRegistryClientService(apiRegistryClientService);
		assertEquals(apiRegistryClientService, classUnderTest.getApiRegistryClientService());
	}

	@Test
	public void testGetClient()
	{
		classUnderTest.setClient(null);
		final PricingClientBase result = classUnderTest.getClient();
		assertNotNull(result);
	}

	@Test(expected = IllegalStateException.class)
	public void testGetClientNotFound() throws CredentialException
	{
		when(apiRegistryClientService.lookupClient(PricingClient.class)).thenThrow(CredentialException.class);
		final PricingClientBase result = classUnderTest.getClient();

	}

	@Test
	public void testCreatePricingDocumentErrorHandlerCalled() throws PricingEngineException
	{
		classUnderTest.setClient(client);
		final HttpException ex = new HttpException(Integer.valueOf(666), "something went horribly wrong");
		Mockito.doThrow(ex).when(client).createPricingDocument(any(), any());
		classUnderTest.createPricingDocument(new PricingDocumentInput());
		Mockito.verify(errorHandler).processCreatePricingDocumentError(ex);
	}

	@Test
	public void testCreatePricingDocumentRuntimeExceptionHandlerCalled() throws PricingEngineException
	{
		classUnderTest.setClient(client);
		final RuntimeException ex = new RuntimeException("something went horribly wrong");
		Mockito.doThrow(ex).when(client).createPricingDocument(any(), any());
		classUnderTest.createPricingDocument(new PricingDocumentInput());
		Mockito.verify(errorHandler).processCreatePricingDocumentRuntimeException(ex);
	}

	@Test
	public void testCreatePricingDocument() throws PricingEngineException
	{
		classUnderTest.setClient(client);
		final PricingDocumentResult pricingDocumentResult = new PricingDocumentResult();

		when(client.createPricingDocument(any(), any())).thenReturn(Observable.just(pricingDocumentResult));

		final PricingDocumentResult result = classUnderTest.createPricingDocument(new PricingDocumentInput());

		assertEquals(pricingDocumentResult, result);
	}
}
