/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.customerticketingc4cintegration.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.customerticketingc4cintegration.converters.ODataCaseStrategy;
import de.hybris.platform.customerticketingc4cintegration.data.Note;
import de.hybris.platform.customerticketingc4cintegration.data.ServiceRequestData;
import de.hybris.platform.customerticketingc4cintegration.exception.C4CServiceException;
import de.hybris.platform.customerticketingc4cintegration.facade.utils.HttpHeaderUtil;
import de.hybris.platform.customerticketingc4cintegration.service.impl.C4CServiceRequestServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.mockito.Mockito;
import java.util.List;

import static de.hybris.platform.testframework.Assert.assertEquals;
import static io.netty.util.internal.StringUtil.EMPTY_STRING;
import static org.mockito.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.HttpStatus.OK;

@UnitTest
public class C4CServiceRequestServiceImplTest {
    @InjectMocks
    @Spy
    C4CServiceRequestServiceImpl c4CServiceRequestService;

    @Mock
    HttpHeaderUtil httpHeaderUtil;

    @Mock
    RestTemplate restTemplate;

    private static final String C4C_BUYER_PARTY_ID = "c4cBuyerPartyId";
    private static final String C4C_BUYER_MAIN_CONTACT_PARTY_ID = "c4cBuyerMainContactPartyId";
    private static final String TICKET_ID = "ticketId";

    @Before
    public void startUp(){
        ObjectMapper jacksonObjectMapper = new ObjectMapper();
        jacksonObjectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        jacksonObjectMapper.setPropertyNamingStrategy(new ODataCaseStrategy());
        jacksonObjectMapper = jacksonObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        MockitoAnnotations.initMocks(this);
        c4CServiceRequestService.setJacksonObjectMapper(jacksonObjectMapper);
    }

    @Test
    public void shouldUpdateTicket() throws C4CServiceException {
        // Mock Enrich Headers
        final HttpHeaders mockHttpHeaders = new HttpHeaders();
        when(httpHeaderUtil.getEnrichedHeaders()).thenReturn(mockHttpHeaders);

        // Mock API Result
        ResponseEntity<String> responseEntity = mock(ResponseEntity.class);
        when(responseEntity.getStatusCode()).thenReturn(OK);
        when(restTemplate.exchange(matches(".*ServiceRequestCollection\\('1234'\\).*"), eq(PATCH), any(), any(Class.class))).thenReturn(responseEntity);

        // Actual Call
        String objectId = "1234";
        ServiceRequestData serviceRequest = new ServiceRequestData();
        serviceRequest.setObjectID(objectId);
        c4CServiceRequestService.updateTicket(serviceRequest);
        Mockito.verify(c4CServiceRequestService).updateTicket(serviceRequest);
    }

    @Test
    public void shouldCreateNote() throws C4CServiceException {
        // Mock Enrich Headers
        final HttpHeaders mockHttpHeaders = new HttpHeaders();
        when(httpHeaderUtil.getEnrichedHeaders()).thenReturn(mockHttpHeaders);

        // Mock API Result
        ResponseEntity<String> responseEntity = mock(ResponseEntity.class);
        when(responseEntity.getStatusCode()).thenReturn(OK);
        when(restTemplate.exchange(matches(".*ServiceRequestCollection\\('1234'\\)/ServiceRequestTextCollection.*"), eq(POST), any(), any(Class.class))).thenReturn(responseEntity);

        Note note  = new Note();
        note.setParentObjectID("1234");
        c4CServiceRequestService.createNote(note);

        // Verify Behaviour
        verify(responseEntity).getStatusCode();
    }

    @Test
    public void shouldGetServiceRequestsByBuyerPartyID() throws C4CServiceException {
        // Mock Enrich Headers
        final HttpHeaders mockHttpHeaders = new HttpHeaders();
        when(httpHeaderUtil.getEnrichedHeaders()).thenReturn(mockHttpHeaders);

        // Mock API Result
        ResponseEntity<String> responseEntity = mock(ResponseEntity.class);
        String contactResponse = "{ \"d\":{ \"__count\":\"1\" ,\"results\":[ {} ] }}";
        when(responseEntity.getBody()).thenReturn(contactResponse);
        when(restTemplate.exchange(matches(".*ServiceRequestCollection.*BuyerPartyID eq 'c4cBuyerPartyId'.*"), eq(GET), any(), any(Class.class))).thenReturn(responseEntity);

        // Actual Call
        SearchPageData<ServiceRequestData> searchPage = c4CServiceRequestService.getServiceRequestsByBuyerPartyID(C4C_BUYER_PARTY_ID, 0, 0, EMPTY_STRING);
        List<ServiceRequestData> results = searchPage.getResults();

        // Verify
        assertEquals(Integer.valueOf(1), Integer.valueOf(results.size()));
    }

    @Test
    public void shouldGetServiceRequestsByBuyerMainContactPartyID() throws C4CServiceException {
        // Mock Enrich Headers
        final HttpHeaders mockHttpHeaders = new HttpHeaders();
        when(httpHeaderUtil.getEnrichedHeaders()).thenReturn(mockHttpHeaders);

        // Mock API Result
        ResponseEntity<String> responseEntity = mock(ResponseEntity.class);
        String contactResponse = "{ \"d\":{ \"__count\":\"1\" ,\"results\":[ {} ] }}";
        when(responseEntity.getBody()).thenReturn(contactResponse);
        when(restTemplate.exchange(matches(".*ServiceRequestCollection.*BuyerMainContactPartyID eq 'c4cBuyerMainContactPartyId'.*"), eq(GET), any(), any(Class.class))).thenReturn(responseEntity);

        // Actual Call
        SearchPageData<ServiceRequestData> searchPage = c4CServiceRequestService.getServiceRequestsByBuyerMainContactPartyID(C4C_BUYER_MAIN_CONTACT_PARTY_ID, 0, 0, EMPTY_STRING);
        List<ServiceRequestData> results = searchPage.getResults();

        // Verify
        assertEquals(Integer.valueOf(1), Integer.valueOf(results.size()));
    }

    @Test
    public void shouldGetServiceRequest() throws C4CServiceException {
        // Mock Enrich Headers
        final HttpHeaders mockHttpHeaders = new HttpHeaders();
        when(httpHeaderUtil.getEnrichedHeaders()).thenReturn(mockHttpHeaders);

        // Mock API Result
        ResponseEntity<String> responseEntity = mock(ResponseEntity.class);
        String contactResponse = "{ \"d\":{ \"__count\":\"1\" ,\"results\":{\"ObjectID\": \"ticketId\"} }}";
        when(responseEntity.getBody()).thenReturn(contactResponse);
        when(restTemplate.exchange(matches(".*ServiceRequestCollection\\('ticketId'\\).*"), eq(GET), any(), any(Class.class))).thenReturn(responseEntity);

        // Actual Call
        ServiceRequestData serviceRequest = c4CServiceRequestService.getServiceRequest(TICKET_ID);

        // Verify
        assertEquals(TICKET_ID, serviceRequest.getObjectID());
    }

    @Test
    public void shouldCreateServiceRequest() throws C4CServiceException{
        // Mock Enrich Headers
        final HttpHeaders mockHttpHeaders = new HttpHeaders();
        when(httpHeaderUtil.getEnrichedHeaders()).thenReturn(mockHttpHeaders);

        // Mock API Result
        ResponseEntity<String> responseEntity = mock(ResponseEntity.class);
        String contactResponse = "{ \"d\": { \"results\":{ \"ObjectID\": \"ticketId\"} } }";
        when(responseEntity.getBody()).thenReturn(contactResponse);
        when(restTemplate.exchange(matches(".*ServiceRequestCollection.*"), eq(POST), any(), any(Class.class))).thenReturn(responseEntity);

        // Actual Call
        ServiceRequestData serviceRequestRequest = new ServiceRequestData();
        ServiceRequestData serviceRequestResponse = c4CServiceRequestService.createServiceRequest(serviceRequestRequest);

        // Verify
        assertEquals(TICKET_ID, serviceRequestResponse.getObjectID());
    }
}
