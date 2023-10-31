/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.services.handlers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import de.hybris.platform.servicelayer.exceptions.ModelSavingException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.cockpitng.dataaccess.facades.object.exceptions.ObjectSavingException;

@RunWith(MockitoJUnitRunner.class)
public class ModelSavingExceptionTranslationHandlerTest
{

    @Spy
    private ModelSavingExceptionTranslationHandler handler;

    @Before
    public void setUp()
    {
        doReturn(null).when(handler).getLabelByKey(any());
    }

    @Test
    public void testCanHandleModelRemovalException()
    {
        final ModelSavingException mse = new ModelSavingException("test", null);
        assertThat(handler.canHandle(mse)).isTrue();
    }

    @Test
    public void testCanHandleObjectDeletionException()
    {
        final ModelSavingException mre = new ModelSavingException("test", null);
        final ObjectSavingException ode = new ObjectSavingException("testId", mre);
        assertThat(handler.canHandle(ode)).isTrue();
    }

    @Test
    public void toStringShouldReturnStaticLabel()
    {
        //when
        handler.toString(null);

        //then
        verify(handler).getLabelByKey(ModelSavingExceptionTranslationHandler.I18N_UNEXPECTED_UPDATE_ERROR);
    }
}