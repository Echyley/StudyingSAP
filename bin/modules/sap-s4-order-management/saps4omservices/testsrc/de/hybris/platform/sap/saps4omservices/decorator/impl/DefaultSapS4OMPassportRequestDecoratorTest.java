/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omservices.decorator.impl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.saps4omservices.services.SapS4OMPassportService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSapS4OMPassportRequestDecoratorTest {

    @InjectMocks
    private DefaultSapS4OMPassportRequestDecorator passport = new DefaultSapS4OMPassportRequestDecorator();
	
    @Mock
    private SapS4OMPassportService sapS4OMPassportService;
    
    @Mock
	HttpHeaders headers;
    
    @Test
	public void testOnDecorate() {
    	String action = "send";
    	when(sapS4OMPassportService.generate(action)).thenReturn("abcd");
    	passport.decorate(headers,action);
    	assertNotNull(headers);
		
	}

}
