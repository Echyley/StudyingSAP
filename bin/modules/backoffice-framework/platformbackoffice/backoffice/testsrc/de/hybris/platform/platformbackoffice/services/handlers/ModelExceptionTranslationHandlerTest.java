/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.services.handlers;

import static org.mockito.Mockito.verify;

import de.hybris.platform.jalo.JaloSystemException;
import de.hybris.platform.persistence.hjmp.HJMPException;

import java.sql.SQLDataException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.DataIntegrityViolationException;

@RunWith(MockitoJUnitRunner.class)
public class ModelExceptionTranslationHandlerTest {


    @Spy
    private ModelExceptionTranslationHandler handler;


    @Test
    public void translateByCauseShouldResolveRecursively()
    {
        //given
        final SQLDataException sqlDataException = new SQLDataException("");
        final JaloSystemException jaloSystemException = new JaloSystemException(sqlDataException);
        final DataIntegrityViolationException dataIntegrityViolationException = new DataIntegrityViolationException("", jaloSystemException);
        final HJMPException hjmp = new HJMPException("", dataIntegrityViolationException);

        //when
        handler.translateByCause(hjmp);

        //then
        verify(handler).translateByCause(hjmp);
        verify(handler).translateByCause(dataIntegrityViolationException);
        verify(handler).translateByCause(jaloSystemException);
        verify(handler).translateByCause(sqlDataException);
        verify(handler).getLabelByKey(ModelExceptionTranslationHandler.I18N_UNEXPECTED_DB_ERROR);
    }


}