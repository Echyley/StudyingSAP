/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.customerticketingc4cintegration.service.impl;

import static de.hybris.platform.customerticketingc4cintegration.constants.Customerticketingc4cintegrationConstants.CREATE_MEMO_ACTIVITY_SUFFIX;
import static de.hybris.platform.customerticketingc4cintegration.constants.Customerticketingc4cintegrationConstants.CREATE_MEMO_ACTIVITY_URL;
import static de.hybris.platform.customerticketingc4cintegration.constants.Customerticketingc4cintegrationConstants.EXPAND_SUFFIX;
import static de.hybris.platform.customerticketingc4cintegration.constants.Customerticketingc4cintegrationConstants.FILETR_SUFFIX;
import static de.hybris.platform.customerticketingc4cintegration.constants.Customerticketingc4cintegrationConstants.GET_MEMO_ACTIVITY_URL;
import static de.hybris.platform.customerticketingc4cintegration.constants.Customerticketingc4cintegrationConstants.NOTE_SUFFIX;
import static de.hybris.platform.customerticketingc4cintegration.constants.Customerticketingc4cintegrationConstants.OBJECT_ID;
import static de.hybris.platform.customerticketingc4cintegration.constants.Customerticketingc4cintegrationConstants.ORDER_BY_SUFFIX;
import static de.hybris.platform.customerticketingc4cintegration.constants.Customerticketingc4cintegrationConstants.PAGING_COUNT_SUFFIX;
import static de.hybris.platform.customerticketingc4cintegration.constants.Customerticketingc4cintegrationConstants.PAGING_SKIP_SUFFIX;
import static de.hybris.platform.customerticketingc4cintegration.constants.Customerticketingc4cintegrationConstants.PAGING_TOP_SUFFIX;
import static de.hybris.platform.customerticketingc4cintegration.constants.Customerticketingc4cintegrationConstants.SELECT_SUFFIX;
import static de.hybris.platform.customerticketingc4cintegration.constants.Customerticketingc4cintegrationConstants.TICKETING_SUFFIX;
import static de.hybris.platform.customerticketingc4cintegration.constants.Customerticketingc4cintegrationConstants.URL;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpMethod.POST;

import de.hybris.platform.core.servicelayer.data.PaginationData;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.customerticketingc4cintegration.constants.Customerticketingc4cintegrationConstants;
import de.hybris.platform.customerticketingc4cintegration.data.MemoActivity;
import de.hybris.platform.customerticketingc4cintegration.data.Note;
import de.hybris.platform.customerticketingc4cintegration.data.RelatedTransaction;
import de.hybris.platform.customerticketingc4cintegration.data.ServiceRequestData;
import de.hybris.platform.customerticketingc4cintegration.exception.C4CServiceException;
import de.hybris.platform.customerticketingc4cintegration.facade.utils.HttpHeaderUtil;
import de.hybris.platform.customerticketingc4cintegration.service.C4CServiceRequestService;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.servicelayer.search.paginated.util.PaginatedSearchUtils;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * C4C ServiceRequest Service Implementation
 */
public class C4CServiceRequestServiceImpl implements C4CServiceRequestService {

    private static final Logger LOGGER = Logger.getLogger(C4CServiceRequestServiceImpl.class);

    private static final String TICKETING_ID_SUFFIX = TICKETING_SUFFIX+"('%s')";
    private static final String RESULTS = "results";
    private static final String D = "d";
    private static final String COUNT = "__count";

    private ObjectMapper jacksonObjectMapper;
    private HttpHeaderUtil httpHeaderUtil;
    private RestTemplate restTemplate;

    @Override
    public void updateTicket(ServiceRequestData serviceRequest) throws C4CServiceException{
        //Build Uri
        String uri = UriComponentsBuilder.fromHttpUrl(URL)
                .path( String.format(TICKETING_ID_SUFFIX, serviceRequest.getObjectID()) )
                .build()
                .toUriString();
        LOGGER.info(String.format("Calling URI: %s", uri));

        try {
            // Prepare request
            final HttpHeaders headers = httpHeaderUtil.getEnrichedHeaders();
            final String jsonServiceRequest = jacksonObjectMapper.writeValueAsString(serviceRequest);
            final HttpEntity<String> entity = new HttpEntity<>(jsonServiceRequest, headers);

            // Call API
            final ResponseEntity<String> responseEntity = restTemplate.exchange(uri, PATCH, entity, String.class);

            LOGGER.info(String.format("Response status for update: %s", responseEntity.getStatusCode()));
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(String.format("Response headers for update: %s", responseEntity.getHeaders()));
                LOGGER.debug(String.format("Response body for update: %s", responseEntity.getBody()));
            }
        } catch (JsonProcessingException e) {
            LOGGER.error("Unable to write service request value as string", e);
            throw new SystemException("Unable to update the ticket due to internal errors", e);
        }
        catch (RestClientException e){
            LOGGER.error("Unable to update the ticket", e);
            throw new C4CServiceException("Unable to update ticket status", e);
        }

    }

    @Override
    public void createNote(Note note) throws C4CServiceException{
        //Build Uri
        String uri = UriComponentsBuilder.fromHttpUrl(URL)
                .path( String.format(TICKETING_ID_SUFFIX, note.getParentObjectID()) )
                .path(NOTE_SUFFIX)
                .build()
                .toUriString();
        LOGGER.info(String.format("Calling note URI: %s", uri));

        try {
            // Prepare request
            final HttpHeaders headers = httpHeaderUtil.getEnrichedHeaders();
            final String jsonNote = jacksonObjectMapper.writeValueAsString(note);
            final HttpEntity<String> entity = new HttpEntity<>(jsonNote, headers);

            // Call API
            final ResponseEntity<String> responseEntity = restTemplate.exchange(uri, POST, entity, String.class);

            LOGGER.info(String.format("Response status for post message: %s", responseEntity.getStatusCode()));
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(String.format("Response headers for post message: %s", responseEntity.getHeaders()));
                LOGGER.debug(String.format("Response body for post message: %s", responseEntity.getBody()));
            }
        } catch (JsonProcessingException e) {
            LOGGER.error("Unable to parse service request note value as string");
            throw new SystemException("Unable to add service request note due to internal errors");
        }
        catch (RestClientException e){
            LOGGER.error("Unable to add note in the ticket", e);
            throw new C4CServiceException("Unable add note to ticket", e);
        }
    }

    @Override
    public SearchPageData<ServiceRequestData> getServiceRequestsByBuyerPartyID(String buyerPartyID, int pageSize, int currentPage, String sorting) throws C4CServiceException{
        // Construct url
        final int skip = pageSize * currentPage;
        LOGGER.debug("Sorting: " + sorting);

        String filterValue = String.format("BuyerPartyID eq '%s'", buyerPartyID);

        final String uri = UriComponentsBuilder.fromHttpUrl(URL)
                .path(TICKETING_SUFFIX)
                .queryParam(FILETR_SUFFIX, filterValue)
                .queryParam(PAGING_TOP_SUFFIX, Integer.valueOf(pageSize))
                .queryParam(PAGING_SKIP_SUFFIX, Integer.valueOf(skip))
                .query(ORDER_BY_SUFFIX + sorting + " desc")
                .query(PAGING_COUNT_SUFFIX)
                .query(EXPAND_SUFFIX)
                .build()
                .toUriString();
        LOGGER.info("Calling uri: " + uri);

        try {

            // Call API
            final HttpHeaders headers = httpHeaderUtil.getEnrichedHeaders();
            final HttpEntity<String> entity = new HttpEntity<>(headers);
            final ResponseEntity<String> responseEntity = restTemplate.exchange(uri, GET, entity, String.class);

            // Extract Response
            String response = responseEntity.getBody();
            JsonNode node = jacksonObjectMapper.readTree(response);
            JsonNode jsonResultsArr = node.get(D).get(RESULTS);
            JsonNode jsonCount = node.get(D).get(COUNT);
            ObjectReader srListReader = jacksonObjectMapper.readerFor(new TypeReference<List<ServiceRequestData>>() {
                //unused block
            });
            List<ServiceRequestData> results = srListReader.readValue(jsonResultsArr);

            // Create Pagination
            SearchPageData<ServiceRequestData> searchPage = PaginatedSearchUtils.createSearchPageDataWithPagination(pageSize, currentPage, true);
            searchPage.setResults(results);
            PaginationData pagination = searchPage.getPagination();
            pagination.setTotalNumberOfResults(jsonCount.asInt());
            LOGGER.debug("Total Count: " + jsonCount.asInt());

            return searchPage;
        } catch (final IOException e) {
            LOGGER.error("Unable convert ticketData" + e);
            throw new SystemException("Unable to parse results: "+e.getMessage());
        } catch (final RestClientException e) {
            LOGGER.error("Can't send request" + e);
            throw new C4CServiceException("Unable to get results"+e.getMessage());
        }
    }

    @Override
    public SearchPageData<ServiceRequestData> getServiceRequestsByBuyerMainContactPartyID(String buyerMainContactPartyID, int pageSize, int currentPage, String sorting) throws C4CServiceException{
        // Construct url
        final int skip = pageSize * currentPage;
        LOGGER.debug("Sorting: " + sorting);

        String filterValue = String.format("BuyerMainContactPartyID eq '%s'", buyerMainContactPartyID);
        final String uri = UriComponentsBuilder.fromHttpUrl(URL)
                .path(TICKETING_SUFFIX)
                .queryParam(FILETR_SUFFIX, filterValue)
                .queryParam(PAGING_TOP_SUFFIX, Integer.valueOf(pageSize))
                .queryParam(PAGING_SKIP_SUFFIX, Integer.valueOf(skip))
                .query(ORDER_BY_SUFFIX + sorting + " desc")
                .query(PAGING_COUNT_SUFFIX)
                .query(EXPAND_SUFFIX)
                .build()
                .toUriString();
        LOGGER.info("Result uri: " + uri);

        try {

            // Call API
            final HttpHeaders headers = httpHeaderUtil.getEnrichedHeaders();
            final HttpEntity<String> entity = new HttpEntity<>(headers);
            final ResponseEntity<String> responseEntity = restTemplate.exchange(uri, GET, entity, String.class);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(String.format("Response status: %s", responseEntity.getStatusCode()));
                LOGGER.debug(String.format("Response headers: %s", responseEntity.getHeaders()));
                LOGGER.debug(String.format("Response body: %s", responseEntity.getBody()));
            }

            // Extract Response
            String response = responseEntity.getBody();
            JsonNode node = jacksonObjectMapper.readTree(response);
            JsonNode jsonResultsArr = node.get(D).get(RESULTS);
            JsonNode jsonCount = node.get(D).get(COUNT);
            ObjectReader srListReader = jacksonObjectMapper.readerFor(new TypeReference<List<ServiceRequestData>>() {
                //unused block
            });
            List<ServiceRequestData> results = srListReader.readValue(jsonResultsArr);

            // Create Pagination
            SearchPageData<ServiceRequestData> searchPage = PaginatedSearchUtils.createSearchPageDataWithPagination(pageSize, currentPage, true);
            searchPage.setResults(results);
            PaginationData pagination = searchPage.getPagination();
            pagination.setTotalNumberOfResults(jsonCount.asInt());
            LOGGER.debug("Total Count: " + jsonCount.asInt());

            return searchPage;
        } catch (final IOException e) {
            LOGGER.error("Can't convert ticketData" + e);
            throw new SystemException("Unable to parse results: "+e.getMessage());
        } catch (final RestClientException e) {
            LOGGER.error("Can't send request" + e);
            throw new C4CServiceException("Unable to get results"+e.getMessage());
        }
    }

    @Override
    public ServiceRequestData getServiceRequest(String ticketId) throws C4CServiceException {
        // Construct url
        final String uri = UriComponentsBuilder.fromHttpUrl(URL)
                .path( String.format(TICKETING_ID_SUFFIX, ticketId) )
                .query(EXPAND_SUFFIX)
                .build()
                .toUriString();
        LOGGER.info("Result uri: " + uri);

        try {
            // Call api
            final HttpHeaders headers = httpHeaderUtil.getEnrichedHeaders();
            final HttpEntity<String> entity = new HttpEntity<>(headers);
            final ResponseEntity<String> responseEntity = restTemplate.exchange(uri, GET, entity, String.class);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(String.format("Response status: %s", responseEntity.getStatusCode()));
                LOGGER.debug(String.format("Response headers: %s", responseEntity.getHeaders()));
                LOGGER.debug(String.format("Response body: %s", responseEntity.getBody()));
            }

            // Extract service requests
            String response = responseEntity.getBody();
            JsonNode node = jacksonObjectMapper.readTree(response);
            JsonNode srNode = node.get(D).get(RESULTS);
            return jacksonObjectMapper.convertValue(srNode, ServiceRequestData.class);
        } catch (final IOException e) {
            LOGGER.error("Got IO Exception: "+e.getMessage(), e);
            throw new SystemException("Can't convert ticketData", e);
        } catch (final RestClientException e) {
            LOGGER.error("Rest Client Exception: "+e.getMessage(), e);
            throw new C4CServiceException( String.format("Unable to fetch ticket id: '%s'", ticketId) , e);
        }
    }

    @Override
    public ServiceRequestData createServiceRequest(ServiceRequestData serviceRequest) throws C4CServiceException {
        // Construct URL
        final String uri = UriComponentsBuilder.fromHttpUrl(URL)
                .path(TICKETING_SUFFIX)
                .build()
                .toUriString();
        LOGGER.info("Calling url: " + uri);

        try {
            // Call API
            final HttpHeaders headers = httpHeaderUtil.getEnrichedHeaders();
            final HttpEntity<String> entity = new HttpEntity<>(jacksonObjectMapper.writeValueAsString(serviceRequest), headers);
            final ResponseEntity<String> responseEntity = restTemplate.exchange(uri, POST, entity, String.class);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(String.format("create Response status: %s", responseEntity.getStatusCode()));
                LOGGER.debug(String.format("create Response headers: %s", responseEntity.getHeaders()));
                LOGGER.debug(String.format("create Response body: %s", responseEntity.getBody()));
            }

            // Extract service request
            String response = responseEntity.getBody();
            JsonNode node = jacksonObjectMapper.readTree(response);
            JsonNode jsonServiceRequest = node.get(D).get(RESULTS);
            return jacksonObjectMapper.convertValue(jsonServiceRequest, ServiceRequestData.class);
        } catch (IOException e) {
            LOGGER.error("Unable to parse json: "+e.getMessage(), e);
            throw new SystemException( "Can't create ticket" , e);
        } catch (RestClientException e) {
            LOGGER.error("Rest Client Exception: "+e.getMessage(), e);
            throw new C4CServiceException( "Can't create ticket" , e);
        }

    }

    @Override
    public MemoActivity createMemoActivity(MemoActivity memoActivityRequest) throws C4CServiceException {

	// Construct URL
	final String uri = UriComponentsBuilder.fromHttpUrl(CREATE_MEMO_ACTIVITY_URL)
		.path(CREATE_MEMO_ACTIVITY_SUFFIX)
		.build()
		.toUriString();

	LOGGER.info("Calling url: " + uri);

	try {
	    // Call API
	    final HttpHeaders headers = httpHeaderUtil.getEnrichedHeaders();
	    final HttpEntity<String> entity = new HttpEntity<>(jacksonObjectMapper.writeValueAsString(memoActivityRequest), headers);
	    final ResponseEntity<String> responseEntity = restTemplate.exchange(uri, POST, entity, String.class);

	    if (LOGGER.isDebugEnabled()) {
		LOGGER.debug(String.format("create Response status: %s", responseEntity.getStatusCode()));
		LOGGER.debug(String.format("create Response headers: %s", responseEntity.getHeaders()));
		LOGGER.debug(String.format("create Response body: %s", responseEntity.getBody()));
	    }

	    // Extract service request
	    String response = responseEntity.getBody();
	    JsonNode node = jacksonObjectMapper.readTree(response);
	    JsonNode jsonServiceRequest = node.get(D).get(RESULTS);
	    return jacksonObjectMapper.convertValue(jsonServiceRequest, MemoActivity.class);

	} catch (IOException e) {
	    LOGGER.error("Unable to parse json: " + e.getMessage(), e);
	    throw new SystemException("Can't create memo activity", e);
	} catch (RestClientException e) {
	    LOGGER.error("Rest Client Exception: " + e.getMessage(), e);
	    throw new C4CServiceException("Can't create memo activity", e);
	}
    }

    @Override
    public List<MemoActivity> getMemoActivities(String ticketId) throws C4CServiceException {

	String suffix = String.format(Customerticketingc4cintegrationConstants.MEMO_ACTIVITY_TICKET_SUFFIX + "('%s')",
		ticketId);
	String uri = UriComponentsBuilder.fromHttpUrl(GET_MEMO_ACTIVITY_URL)
		.path(suffix)
		.path(Customerticketingc4cintegrationConstants.GET_MEMO_ACTIVITY_SUFFIX)
		.query(Customerticketingc4cintegrationConstants.ORDER_BY_SUFFIX
			+ Customerticketingc4cintegrationConstants.MEMO_ACTIVITY_ORDERBY_DEFAULT_VALUE)
		.build()
		.toUriString();
	LOGGER.info(String.format("Calling note URI: %s", uri));

	try {

	    // Call API
	    final HttpHeaders headers = httpHeaderUtil.getEnrichedHeaders();
	    final HttpEntity<String> entity = new HttpEntity<>(headers);
	    final ResponseEntity<String> responseEntity = restTemplate.exchange(uri, GET, entity, String.class);

	    // Extract Response
	    String response = responseEntity.getBody();
	    JsonNode node = jacksonObjectMapper.readTree(response);
	    JsonNode jsonResultsArr = node.get(D).get(RESULTS);
	    ObjectReader srListReader = jacksonObjectMapper.readerFor(new TypeReference<List<MemoActivity>>() {
		// unused block
	    });
	    List<MemoActivity> results = srListReader.readValue(jsonResultsArr);

	    return results;
	} catch (final IOException e) {
	    LOGGER.error("Unable convert ticketData" + e);
	    throw new SystemException("Unable to parse results: " + e.getMessage());
	} catch (final RestClientException e) {
	    LOGGER.error("Can't send request" + e);
	    throw new C4CServiceException("Unable to get results" + e.getMessage());
	}
    }

    @Override
    public void updateTicketWithMemoActivity(RelatedTransaction relatedTransactionRequest) throws C4CServiceException {
	// Build Uri
	String uri = UriComponentsBuilder.fromHttpUrl(URL)
		.path(String.format(TICKETING_ID_SUFFIX, relatedTransactionRequest.getParentObjectID()))
		.path(Customerticketingc4cintegrationConstants.RELATED_BUSINESS_TRANSACTION_SUFFIX)
		.build()
		.toUriString();
	LOGGER.info(String.format("Calling URI: %s", uri));

	try {
	    // Prepare request
	    final HttpHeaders headers = httpHeaderUtil.getEnrichedHeaders();
	    final String jsonServiceRequest = jacksonObjectMapper.writeValueAsString(relatedTransactionRequest);
	    final HttpEntity<String> entity = new HttpEntity<>(jsonServiceRequest, headers);

	    // Call API
	    final ResponseEntity<String> responseEntity = restTemplate.exchange(uri, POST, entity, String.class);

	    LOGGER.info(String.format("Response status for update: %s", responseEntity.getStatusCode()));
	    if (LOGGER.isDebugEnabled()) {
		LOGGER.debug(String.format("Response headers for update: %s", responseEntity.getHeaders()));
		LOGGER.debug(String.format("Response body for update: %s", responseEntity.getBody()));
	    }
	} catch (JsonProcessingException e) {
	    LOGGER.error("Unable to write request value as string", e);
	    throw new SystemException("Unable to update the ticket with memo activity due to internal errors", e);
	} catch (RestClientException e) {
	    LOGGER.error("Unable to update the ticket with memo activity", e);
	    throw new C4CServiceException("Unable to update ticket with memo activity", e);
	}

    }

    @Override
    public String getTicketObjectId(String ticketId) throws C4CServiceException {

	// Construct url
	String filterValue = String.format("ID eq '%s'", ticketId);

	final String uri = UriComponentsBuilder.fromHttpUrl(URL)
		.path(TICKETING_SUFFIX)
		.queryParam(FILETR_SUFFIX, filterValue)
		.queryParam(SELECT_SUFFIX, OBJECT_ID)
		.build().toUriString();
	LOGGER.info("Result uri: " + uri);

	try {
	    // Call api
	    final HttpHeaders headers = httpHeaderUtil.getEnrichedHeaders();
	    final HttpEntity<String> entity = new HttpEntity<>(headers);
	    final ResponseEntity<String> responseEntity = restTemplate.exchange(uri, GET, entity, String.class);

	    if (LOGGER.isDebugEnabled()) {
		LOGGER.debug(String.format("Response status: %s", responseEntity.getStatusCode()));
		LOGGER.debug(String.format("Response headers: %s", responseEntity.getHeaders()));
		LOGGER.debug(String.format("Response body: %s", responseEntity.getBody()));
	    }

	    // Extract service requests
	    String response = responseEntity.getBody();
	    JsonNode node = jacksonObjectMapper.readTree(response);
	    ArrayNode srNode = (com.fasterxml.jackson.databind.node.ArrayNode) node.get(D).get(RESULTS);
	    return srNode.get(0).get(OBJECT_ID).textValue();
	} catch (final IOException e) {
	    LOGGER.error("Got IO Exception: " + e.getMessage(), e);
	    throw new SystemException("Can't convert serviceRequestData", e);
	} catch (final RestClientException e) {
	    LOGGER.error("Rest Client Exception: " + e.getMessage(), e);
	    throw new C4CServiceException(String.format("Unable to fetch objectId for ticket id: '%s'", ticketId), e);
	}
    }

    // <editor-fold desc="Setters and Getters">

    public void setJacksonObjectMapper(ObjectMapper jacksonObjectMapper) {
        this.jacksonObjectMapper = jacksonObjectMapper;
    }

    public void setHttpHeaderUtil(HttpHeaderUtil httpHeaderUtil) {
        this.httpHeaderUtil = httpHeaderUtil;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // </editor-fold>
}
