/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.services.handlers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import de.hybris.platform.jalo.JaloSystemException;
import de.hybris.platform.persistence.hjmp.HJMPException;
import de.hybris.platform.servicelayer.exceptions.ModelRemovalException;

import java.sql.SQLDataException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.DataIntegrityViolationException;

import com.hybris.cockpitng.dataaccess.facades.object.exceptions.ObjectDeletionException;


@RunWith(MockitoJUnitRunner.class)
public class ModelRemovalExceptionTranslationHandlerTest
{

	@Spy
	private ModelRemovalExceptionTranslationHandler handler;

	@Before
	public void setUp()
	{

		doReturn(null).when(handler).getLabelByKey(any());
	}

	@Test
	public void testCanHandleModelRemovalException()
	{
		final ModelRemovalException mre = new ModelRemovalException("test", null);
		assertThat(handler.canHandle(mre)).isTrue();
	}

	@Test
	public void testCanHandleObjectDeletionException()
	{
		final ModelRemovalException mre = new ModelRemovalException("test", null);
		final ObjectDeletionException ode = new ObjectDeletionException("testId", mre);
		assertThat(handler.canHandle(ode)).isTrue();
	}

	@Test
	public void toStringShouldReturnStaticLabel()
	{
		//when
		handler.toString(null);

		//then
		verify(handler).getLabelByKey(ModelRemovalExceptionTranslationHandler.I18N_UNEXPECTED_REMOVE_ERROR);
	}

}
