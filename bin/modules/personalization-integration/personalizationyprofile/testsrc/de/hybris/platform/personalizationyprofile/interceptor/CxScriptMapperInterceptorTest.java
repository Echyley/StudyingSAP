/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.personalizationyprofile.interceptor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.personalizationintegration.model.CxMapperScriptModel;
import de.hybris.platform.personalizationyprofile.event.InvalidateConsumptionLayerUserSegmentsProviderCacheEvent;
import de.hybris.platform.personalizationyprofile.segment.ConsumptionLayerUserSegmentsProvider;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CxScriptMapperInterceptorTest
{
	private CxScriptMapperInterceptor cxScriptMapperInterceptor;

	@Mock
	private ConsumptionLayerUserSegmentsProvider consumptionLayerUserSegmentsProvider;

	@Mock
	private EventService eventService;

	@Mock
	private CxMapperScriptModel scriptModel;

	@Mock
	private InterceptorContext interceptorContext;


	@Before
	public void setup()
	{
		cxScriptMapperInterceptor = new CxScriptMapperInterceptor();
		cxScriptMapperInterceptor.setConsumptionLayerUserSegmentsProvider(consumptionLayerUserSegmentsProvider);
		cxScriptMapperInterceptor.setEventService(eventService);
	}

	@Test
	public void testOnRemove() throws InterceptorException
	{
		//when

		cxScriptMapperInterceptor.onRemove(scriptModel, interceptorContext);

		//then

		verify(eventService, times(1)).publishEvent(isA(InvalidateConsumptionLayerUserSegmentsProviderCacheEvent.class));
	}

	@Test
	public void testOnValidate() throws InterceptorException
	{
		//given

		when(interceptorContext.isNew(any())).thenReturn(true);

		//when

		cxScriptMapperInterceptor.onValidate(scriptModel, interceptorContext);

		//then

		verify(eventService, times(1)).publishEvent(isA(InvalidateConsumptionLayerUserSegmentsProviderCacheEvent.class));
	}
}
