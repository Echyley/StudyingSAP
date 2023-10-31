/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;


import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.odata2services.odata.EdmProviderFactory;
import de.hybris.platform.odata2services.odata.processor.ODataProcessorFactory;

import org.apache.olingo.odata2.api.edm.provider.EdmProvider;
import org.apache.olingo.odata2.api.ep.callback.OnWriteFeedContent;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.odata2.api.processor.ODataErrorCallback;
import org.apache.olingo.odata2.api.processor.ODataSingleProcessor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultServiceFactoryUnitTest
{
	private static final ODataContext CONTEXT = mock(ODataContext.class);

	@Mock(lenient = true)
	private EdmProviderFactory edmProviderFactory;
	@Mock(lenient = true)
	private ODataProcessorFactory processorFactory;
	@Mock(lenient = true)
	private ODataErrorCallback errorCallback;
	@InjectMocks
	private DefaultServiceFactory serviceFactory;

	@Test
	public void testCreateService()
	{
		lenient().when(edmProviderFactory.createInstance(any(ODataContext.class))).thenReturn(mock(EdmProvider.class));
		lenient().when(processorFactory.createProcessor(any(ODataContext.class))).thenReturn(mock(ODataSingleProcessor.class));

		assertThat(serviceFactory.createService(CONTEXT)).isNotNull();
	}

	@Test
	public void testErrorCallbackIsRegistered()
	{
		assertThat(serviceFactory.getCallback(ODataErrorCallback.class))
				.isNotNull()
				.isSameAs(errorCallback);
	}

	@Test
	public void testErrorCallbackIsRegisteredButNotAssignable()
	{
		assertThat(serviceFactory.getCallback(OnWriteFeedContent.class)).isNull();
	}
}
