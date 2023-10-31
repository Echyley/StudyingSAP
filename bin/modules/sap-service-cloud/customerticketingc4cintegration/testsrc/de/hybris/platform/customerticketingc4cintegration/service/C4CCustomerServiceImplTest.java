/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.customerticketingc4cintegration.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.customerticketingc4cintegration.converters.ODataCaseStrategy;
import de.hybris.platform.customerticketingc4cintegration.data.Contact;
import de.hybris.platform.customerticketingc4cintegration.data.IndividualCustomer;
import de.hybris.platform.customerticketingc4cintegration.exception.C4CServiceException;
import de.hybris.platform.customerticketingc4cintegration.facade.utils.HttpHeaderUtil;
import de.hybris.platform.customerticketingc4cintegration.service.impl.C4CCustomerServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpMethod.GET;

/**
 * Test class for {@link de.hybris.platform.customerticketingc4cintegration.service.impl.C4CCustomerServiceImpl}
 */
@UnitTest
public class C4CCustomerServiceImplTest {
    @InjectMocks
    C4CCustomerServiceImpl c4cCustomerService;

    @Mock
    HttpHeaderUtil httpHeaderUtil;

    @Mock
    RestTemplate restTemplate;

    private static final String CUSTOMER_ID = "customerId";

    @Before
    public void startUp(){
        ObjectMapper jacksonObjectMapper = new ObjectMapper();
        jacksonObjectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        jacksonObjectMapper.setPropertyNamingStrategy(new ODataCaseStrategy());
        jacksonObjectMapper = jacksonObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        MockitoAnnotations.initMocks(this);
        c4cCustomerService.setJacksonObjectMapper(jacksonObjectMapper);
    }

    @Test
    public void shouldGetIndividualCustomerByExternalId() throws C4CServiceException {
        // Mock Enrich Headers
        final HttpHeaders mockHttpHeaders = new HttpHeaders();
        when(httpHeaderUtil.getEnrichedHeaders()).thenReturn(mockHttpHeaders);

        // Mock API Result
        ResponseEntity<String> responseEntity = mock(ResponseEntity.class);
        String customerIdVal = "sample_customer_id";
        String individualCustomerResponse = "{ \"d\":{ \"results\":[ {\"CustomerID\": \"sample_customer_id\"} ] } }";
        when(responseEntity.getBody()).thenReturn(individualCustomerResponse);
        when(restTemplate.exchange(matches(".*IndividualCustomerCollection.*ExternalID eq 'customerId'.*"), eq(GET), any(), any(Class.class))).thenReturn(responseEntity);

        // Actual Call
        IndividualCustomer result = c4cCustomerService.getIndividualCustomerByExternalId(CUSTOMER_ID);

        // Verify
        assertNotNull("result cannot be null", result);
        assertEquals("customer id check", customerIdVal, result.getCustomerID());
    }

    @Test(expected = C4CServiceException.class)
    public void shouldThrowC4CServiceExceptionWhenEmptyIndividualCustomer() throws C4CServiceException {
        // Mock Enrich Headers
        final HttpHeaders mockHttpHeaders = new HttpHeaders();
        when(httpHeaderUtil.getEnrichedHeaders()).thenReturn(mockHttpHeaders);

        // Mock API Result
        ResponseEntity<String> responseEntity = mock(ResponseEntity.class);
        String individualCustomerResponse = "{ \"d\":{ \"results\":[] } }";
        when(responseEntity.getBody()).thenReturn(individualCustomerResponse);
        when(restTemplate.exchange(matches(".*IndividualCustomerCollection.*ExternalID eq 'customerId'.*"), eq(GET), any(), any(Class.class))).thenReturn(responseEntity);

        // Actual Call
        c4cCustomerService.getIndividualCustomerByExternalId(CUSTOMER_ID);

        fail("Expected to throw exception before this step");
    }

    @Test(expected = Exception.class)
    public void shouldThrowExceptionWhenRestClientExceptionDuringGetCustomer() throws C4CServiceException {
        // Mock Enrich Headers
        final HttpHeaders mockHttpHeaders = new HttpHeaders();
        when(httpHeaderUtil.getEnrichedHeaders()).thenReturn(mockHttpHeaders);

        // Mock API Result
        doThrow(Exception.class).when(restTemplate).exchange(anyString(), eq(GET), any(), any(Class.class));

        // Actual Call
        c4cCustomerService.getIndividualCustomerByExternalId(CUSTOMER_ID);

        fail("Exception is expected before this step");
    }

    @Test
    public void shouldGetContactByExternalId() throws C4CServiceException {
        // Mock Enrich Headers
        final HttpHeaders mockHttpHeaders = new HttpHeaders();
        when(httpHeaderUtil.getEnrichedHeaders()).thenReturn(mockHttpHeaders);

        // Mock API Result
        ResponseEntity<String> responseEntity = mock(ResponseEntity.class);
        String contactIdVal = "sample_contact_id";
        String contactResponse = "{ \"d\":{ \"results\":[ {\"ContactID\": \"sample_contact_id\", \"ExternalID\": \"customerId\"} ] } }";
        when(responseEntity.getBody()).thenReturn(contactResponse);
        when(restTemplate.exchange(matches(".*ContactCollection.*ExternalID eq 'customerId'.*"), eq(GET), any(), any(Class.class))).thenReturn(responseEntity);

        // Actual Call
        Contact result = c4cCustomerService.getContactByExternalId(CUSTOMER_ID);

        // Verify
        assertNotNull("contact response not null", result);
        assertEquals("check contact id", contactIdVal, result.getContactID());
    }

    @Test(expected = C4CServiceException.class)
    public void shouldThrowC4CServiceExceptionWhenEmptyContact() throws C4CServiceException {
        // Mock Enrich Headers
        final HttpHeaders mockHttpHeaders = new HttpHeaders();
        when(httpHeaderUtil.getEnrichedHeaders()).thenReturn(mockHttpHeaders);

        // Mock API Result
        ResponseEntity<String> responseEntity = mock(ResponseEntity.class);
        String contactResponse = "{ \"d\":{ \"results\":[ ] } }";
        when(responseEntity.getBody()).thenReturn(contactResponse);
        when(restTemplate.exchange(matches(".*ContactCollection.*ExternalID eq 'customerId'.*"), eq(GET), any(), any(Class.class))).thenReturn(responseEntity);

        // Actual Call
        c4cCustomerService.getContactByExternalId(CUSTOMER_ID);

        fail("Exception is expected before this step");
    }

    @Test(expected = Exception.class)
    public void shouldThrowExceptionWhenRestClientExceptionDuringGetContact() throws C4CServiceException {
        // Mock Enrich Headers
        final HttpHeaders mockHttpHeaders = new HttpHeaders();
        when(httpHeaderUtil.getEnrichedHeaders()).thenReturn(mockHttpHeaders);

        // Mock API Result
        doThrow(Exception.class).when(restTemplate).exchange(anyString(), eq(GET), any(), any(Class.class));

        // Actual Call
        c4cCustomerService.getContactByExternalId(CUSTOMER_ID);

        fail("Exception is expected before this step");
    }

}
