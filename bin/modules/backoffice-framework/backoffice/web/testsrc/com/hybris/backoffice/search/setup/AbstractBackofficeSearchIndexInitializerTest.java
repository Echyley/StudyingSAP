/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
 package com.hybris.backoffice.search.setup;

 import static org.mockito.Mockito.verify;
 import static org.mockito.Matchers.any;

 import org.junit.Before;
 import org.junit.Test;
 import org.junit.runner.RunWith;
 import org.mockito.Spy;
 import org.mockito.junit.MockitoJUnitRunner;
 import javax.annotation.Resource;

 import com.hybris.backoffice.search.events.AfterInitializationEndBackofficeSearchListener;

 @RunWith(MockitoJUnitRunner.class)
 public class AbstractBackofficeSearchIndexInitializerTest
 {
    static class AbstractBackofficeSearchIndexInitializerImpl extends AbstractBackofficeSearchIndexInitializer {
        protected void initializeIndexesIfNecessary() {}

        protected boolean shouldInitializeIndexes() {
            return true;
        }
    }

    @Resource
    @Spy
    private AfterInitializationEndBackofficeSearchListener afterInitializationEndBackofficeListener;


    private final AbstractBackofficeSearchIndexInitializerImpl abstractBackofficeSearchIndexInitializerImpl = new AbstractBackofficeSearchIndexInitializerImpl();

    @Before
    public void setUp() throws Exception
    {
       abstractBackofficeSearchIndexInitializerImpl.setAfterInitializationEndBackofficeListener(afterInitializationEndBackofficeListener);
    }
    @Test
    public void shouldRegisterCallbackWhenCallbackIsNotRegistered()
    {
        abstractBackofficeSearchIndexInitializerImpl.registerSystemInitializationEndCallback();
        verify(abstractBackofficeSearchIndexInitializerImpl.afterInitializationEndBackofficeListener).registerCallback(any());
    }

     @Test
     public void shouldRegisterCallbackWhenInitialize()
     {
         abstractBackofficeSearchIndexInitializerImpl.initialize();
         verify(abstractBackofficeSearchIndexInitializerImpl.afterInitializationEndBackofficeListener).registerCallback(any());
     }

 }
