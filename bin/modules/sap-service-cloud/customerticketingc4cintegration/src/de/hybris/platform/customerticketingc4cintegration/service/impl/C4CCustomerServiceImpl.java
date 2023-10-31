/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.customerticketingc4cintegration.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import de.hybris.platform.customerticketingc4cintegration.data.Contact;
import de.hybris.platform.customerticketingc4cintegration.data.IndividualCustomer;
import de.hybris.platform.customerticketingc4cintegration.exception.C4CServiceException;
import de.hybris.platform.customerticketingc4cintegration.facade.utils.HttpHeaderUtil;
import de.hybris.platform.customerticketingc4cintegration.service.C4CCustomerService;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import org.apache.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import static de.hybris.platform.customerticketingc4cintegration.constants.Customerticketingc4cintegrationConstants.*;

/**
 * Default Implementation of C4CCustomerService
 */
public class C4CCustomerServiceImpl implements C4CCustomerService {

    private static final Logger LOGGER = Logger.getLogger(C4CCustomerServiceImpl.class);

    private ObjectMapper jacksonObjectMapper;
    private HttpHeaderUtil httpHeaderUtil;
    private RestTemplate restTemplate;

    @Override
    public IndividualCustomer getIndividualCustomerByExternalId(String externalId) throws C4CServiceException {

        //Build URL
        UriComponentsBuilder customerUriBuilder = UriComponentsBuilder.fromHttpUrl(URL)
                .path(INDIVIDUAL_CUSTOMER_SUFFIX)
                .queryParam(FILETR_SUFFIX, String.format("ExternalID eq '%s'", externalId))
                .queryParam(PAGING_TOP_SUFFIX, Integer.valueOf(1));

        String url = customerUriBuilder.build().toString();

        //Get Authorization Headers
        final HttpHeaders headers = httpHeaderUtil.getEnrichedHeaders();
        final HttpEntity<String> entity = new HttpEntity<>(headers);

        //Get Individual Customer
        try {
            LOGGER.info(String.format("Calling URL: %s", url));
            // Call API
            final ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET,
                    entity, String.class);

            // Extract Response
            String response = responseEntity.getBody();
            JsonNode node = jacksonObjectMapper.readTree(response);
            ArrayNode results = (ArrayNode) node.get("d").get("results");
            if (results.isEmpty()) {
                LOGGER.error(String.format("Cannot find c4c customer for external id '%s'", externalId));
                throw new C4CServiceException("Cannot find customer id");
            }

            //Get Customer ID
            return jacksonObjectMapper.convertValue(results.get(0), IndividualCustomer.class);

        }
        catch (JsonProcessingException e) {
            LOGGER.error("Unable to parse Individual Customer JSON: " + e.getMessage());
            throw new SystemException("Unable to parse Individual customer response", e);
        }
        catch (RestClientException e) {
            LOGGER.error("Rest Client Exception: "+e.getMessage(), e);
            throw new C4CServiceException( "Can't get individual Customer" , e);
        }
    }

    @Override
    public Contact getContactByExternalId(String externalId) throws C4CServiceException{

        //Build URL
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(URL)
                .path(CONTACT_SUFFIX)
                .queryParam(FILETR_SUFFIX, String.format("ExternalID eq '%s'", externalId))
                .queryParam(PAGING_TOP_SUFFIX, Integer.valueOf(1));

        String url = uriBuilder.build().toString();

        //Get Authorization Headers
        final HttpHeaders headers = httpHeaderUtil.getEnrichedHeaders();
        final HttpEntity<String> entity = new HttpEntity<>(headers);

        //Get Individual Customer
        try {
            // Call API
            LOGGER.info(String.format("Calling URL: %s", url));
            final ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET,
                    entity, String.class);

            // Extract Response
            String response = responseEntity.getBody();
            JsonNode node = jacksonObjectMapper.readTree(response);
            ArrayNode results = (ArrayNode) node.get("d").get("results");
            if (results.isEmpty()) {
                LOGGER.error(String.format("Cannot find c4c CONTACT for external id '%s'. Empty Response: '%s'", externalId, response));
                throw new C4CServiceException("Cannot find contact id in system");
            }

            //Get Customer ID
            return jacksonObjectMapper.convertValue(results.get(0), Contact.class);

        }
        catch (JsonProcessingException e) {
            LOGGER.error(String.format("Unable to parse Contact JSON. Error: '%s'", e.getMessage()));
            throw new SystemException("Unable to parse Contact response", e);
        }
        catch (RestClientException e) {
            LOGGER.error("Rest Client Exception: "+e.getMessage(), e);
            throw new C4CServiceException( "Can't get contact" , e);
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
