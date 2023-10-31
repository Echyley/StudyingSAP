/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.servicelayer.i18n.interceptors;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.session.SessionService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import junit.framework.Assert;


/**
 * Test covering the {@link RemoveLastLanguageInterceptor} logic.
 */
@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class RemoveSessionLanguageInterceptorTest
{

	@Mock
	private SessionService sessionService;

	@Mock
	private InterceptorContext interceptorContext;

	private RemoveSessionLanguageInterceptor interceptor;

	@Before
	public void setUp()
	{
		interceptor = new RemoveSessionLanguageInterceptor();
		interceptor.setSessionService(sessionService);
	}

	@Test
	public void testRemoveInterceptorNoLanguageModel() throws InterceptorException
	{
		Mockito.lenient().when(sessionService.getAttribute(Mockito.anyString())).thenReturn(null);

		interceptor.onRemove(new ProductModel(), interceptorContext);

		verify(sessionService, Mockito.never()).getAttribute(Mockito.anyString());
	}


	@Test
	public void testRemoveInterceptorLanguageModelPossible() throws InterceptorException
	{
		final LanguageModel model = new LanguageModel();

		//replay so getAttribute will return the model
		when(sessionService.getAttribute("language")).thenReturn(model);
		//call it with different model
		interceptor.onRemove(new LanguageModel(), interceptorContext);
		verify(sessionService, Mockito.times(2)).getAttribute("language");
	}


	@Test
	public void testRemoveInterceptorLanguageModelImPossible() throws InterceptorException
	{
		final LanguageModel model = new LanguageModel();

		//replay so getAttribute will return the model
		when(sessionService.getAttribute("language")).thenReturn(model);
		try
		{
			//call with the same instance
			interceptor.onRemove(model, interceptorContext);
			Assert.fail("Should not able to remove session language ");
		}
		catch (final InterceptorException ie)
		{
			//
		}
		verify(sessionService, Mockito.times(2)).getAttribute("language");
	}


}
