/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.pci.impl;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.spy;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.apiregistryservices.exceptions.CredentialException;
import de.hybris.platform.apiregistryservices.services.ApiRegistryClientService;
import de.hybris.platform.sap.productconfig.runtime.interf.analytics.model.AnalyticsDocument;
import de.hybris.platform.sap.productconfig.runtime.pci.client.PCIClient;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.hybris.charon.exp.HttpException;
import com.hybris.charon.exp.ServiceUnavailableException;

import rx.Observable;


/**
 * Unit test for {@link PCICharonFacadeImpl}
 */
@UnitTest
public class PCICharonFacadeImplTest
{
	private PCICharonFacadeImpl classUnderTest;
	@Mock
	private PCIClient client;

	@Mock
	private ApiRegistryClientService apiRegistryClientService;

	@Before
	public void setup() throws CredentialException
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new PCICharonFacadeImpl();
		classUnderTest.setClient(client);
		classUnderTest.setApiRegistryClientService(apiRegistryClientService);
		final PCIRequestErrorHandlerImpl errorHandler = spy(new PCIRequestErrorHandlerImpl());
		classUnderTest.setPciRequestErrorHandler(errorHandler);
		willReturn("server error msg").given(errorHandler).getServerMessage(any(HttpException.class));
		Mockito.when(apiRegistryClientService.lookupClient(PCIClient.class)).thenReturn(client);
	}

	@Test
	public void testGetClient()
	{
		classUnderTest.setClient(null);
		final PCIClient result = classUnderTest.getClient();
		assertNotNull(result);
	}

	@Test(expected = IllegalStateException.class)
	public void testGetClientNotFound() throws CredentialException
	{
		classUnderTest.setClient(null);
		Mockito.when(apiRegistryClientService.lookupClient(PCIClient.class)).thenThrow(CredentialException.class);
		classUnderTest.getClient();
	}

	@Test
	public void testCreateAnalyticsDocumentHttpException()
	{
		final HttpException ex = new ServiceUnavailableException(Integer.valueOf(123), "message");
		Mockito.when(client.createAnalyticsDocument(Mockito.any())).thenThrow(ex);
		final AnalyticsDocument output = classUnderTest.createAnalyticsDocument(new AnalyticsDocument());
		assertNotNull(output);
	}

	@Test
	public void testCreateAnalyticsDocument()
	{
		final Observable<AnalyticsDocument> observable = Observable.from(Arrays.asList(new AnalyticsDocument()));
		Mockito.when(client.createAnalyticsDocument(Mockito.any())).thenReturn(observable);
		final AnalyticsDocument input = new AnalyticsDocument();
		final AnalyticsDocument result = classUnderTest.createAnalyticsDocument(input);
		assertNotNull(result);
		Mockito.verify(client).createAnalyticsDocument(input);
	}

}
