/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.controllers;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.assistedservicefacades.AssistedServiceFacade;
import de.hybris.platform.assistedserviceservices.exception.AssistedServiceException;
import de.hybris.platform.assistedservicewebservices.dto.CustomerRegistrationFormWsDTO;
import de.hybris.platform.assistedservicewebservices.helper.CustomerHelper;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.strategies.impl.DefaultCustomerNameStrategy;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.Validator;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

/**
 * Test suite for {@link CustomersController}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CustomersControllerTest {
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String EMAIL_ADDRESS = "test@sap.com";
    private static final String BASE_SITE = "testSite";
    private final CustomerData customerData = new CustomerData();
    private final String customerName = "";
    @Mock
    private DefaultCustomerNameStrategy defaultCustomerNameStrategy;
    @Mock
    private AssistedServiceFacade assistedServiceFacade;
    @Mock
    private Validator customerRegistrationDataValidator;
    @Spy
    private CustomerRegistrationFormWsDTO validCustomerRegistrationForm;
    @Mock
    private CustomerHelper customerHelper;
    @InjectMocks
    private CustomersController customersController;

    @Before
    public void setUp(){
        validCustomerRegistrationForm.setEmailAddress(EMAIL_ADDRESS);
        validCustomerRegistrationForm.setFirstName(FIRST_NAME);
        validCustomerRegistrationForm.setLastName(LAST_NAME);
    }

    @Test
    public void testCreateCustomer() throws AssistedServiceException {
        when(defaultCustomerNameStrategy.getName(anyString(),anyString())).thenReturn(customerName);
        when(assistedServiceFacade.createCustomer(anyString(),anyString())).thenReturn(customerData);

        customersController.createCustomer(validCustomerRegistrationForm, BASE_SITE);

        verify(customerRegistrationDataValidator).validate(any(),any());
        verify(defaultCustomerNameStrategy).getName(FIRST_NAME,LAST_NAME);
        verify(assistedServiceFacade).createCustomer(EMAIL_ADDRESS, customerName);
        verify(customerHelper).getUserWsDTO(customerData);
    }
}
