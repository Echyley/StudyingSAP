/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercewebservicestests.test.groovy.webservicetests.v2.spock.invoices

import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.commercewebservices.core.constants.YcommercewebservicesConstants
import de.hybris.platform.commercewebservicestests.setup.TestSetupUtils
import de.hybris.platform.commercewebservicestests.test.groovy.webservicetests.v2.spock.orders.AbstractOrderTest

import groovyx.net.http.HttpResponseDecorator
import spock.lang.Unroll

import static groovyx.net.http.ContentType.*
import static org.apache.http.HttpStatus.*
import static org.junit.Assume.assumeFalse;

@ManualTest
@Unroll
class UserOrderInvoicesTest extends AbstractOrderTest {

	static final ALL_IVOICES = 1
	static final String USERNAME_WITH_INVOICES = "orderhistoryuser@test.com"
	static final String PASSWORD = "1234"
	static final String ORDER_CODE = "testOrder13"
	static final String INVOICE_ID_1 = "testInovice1"
	static final String INVOICE_ID_2 = "testInovice2"
	static final String DISABLED_ENDPOINTS_KEY = YcommercewebservicesConstants.MODULE_NAME.concat(".api.restrictions.disabled.endpoints")
	static final String DISABLED_ENDPOINT_INVOICES = "getUserOrderInvoices"
	static final String DISABLED_ENDPOINT_INVOICE_BINARY = "getUserOrderInvoiceBinary"
	
	static final CUSTOMER_WITH_INVOICES = ["id": USERNAME_WITH_INVOICES, "password": PASSWORD]
	
	def "Trusted client requests invoices by order code: #format"() {
	    
	    assumeFalse(TestSetupUtils.isEndpointDisabled(DISABLED_ENDPOINTS_KEY,DISABLED_ENDPOINT_INVOICES));
	    
		given: "trusted client"
		authorizeTrustedClient(restClient)
		TestSetupUtils.updateSapInvoiceEnabled(true);

		when: "trusted client retrieves invoices by order code"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + "/users/" + CUSTOMER_WITH_INVOICES.id + "/orders/" + ORDER_CODE + "/invoices",
				contentType: format,
				query: ['fields': FIELD_SET_LEVEL_FULL],
				requestContentType: URLENC)

		then: "he receives list of invoices of that order"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
			status == SC_OK
			data.pagination.count == 20
			data.pagination.totalPages == 1
			data.pagination.page == 0
			data.pagination.totalCount == ALL_IVOICES
			data.invoices.size() > 0
			for( invoice in data.invoices ) {
  				isNotEmpty(invoice.invoiceId)
                isNotEmpty(invoice.createdAt)
                isNotEmpty(invoice.netAmount)
                isNotEmpty(invoice.totalAmount)
            }
			
		}

		where:
		format << [XML, JSON]
	}
	
	
	def "Trusted client requests invoices by order code: #format"() {
	
	    assumeFalse(TestSetupUtils.isEndpointDisabled(DISABLED_ENDPOINTS_KEY,DISABLED_ENDPOINT_INVOICES));
	     
		given: "trusted client"
		authorizeTrustedClient(restClient)
		TestSetupUtils.updateSapInvoiceEnabled(false)

		when: "trusted client retrieves invoices by order code"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + "/users/" + CUSTOMER_WITH_INVOICES.id + "/orders/" + ORDER_CODE + "/invoices",
				contentType: format,
				query: ['fields': FIELD_SET_LEVEL_FULL],
				requestContentType: URLENC)

		then: "he receives no invoice as invoice feature is disabled in base store"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
			status == SC_OK
			data.pagination.count == 1
			data.pagination.totalPages == 0
			data.pagination.page == 0
			data.pagination.totalCount == 0
		}

		where:
		format << [XML, JSON]
	}
	
	

	def "Trusted client requests invoices by wrong order id: #format"() {
	
	    assumeFalse(TestSetupUtils.isEndpointDisabled(DISABLED_ENDPOINTS_KEY,DISABLED_ENDPOINT_INVOICES));
	    
		given: "trusted client"
		authorizeTrustedClient(restClient)
        TestSetupUtils.updateSapInvoiceEnabled(true); 
        
		when: "trusted client retrieves invoices by order code"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + "/users/" + CUSTOMER_WITH_INVOICES.id + "/orders/wrongOrderGuidOrCode" + "/invoices",
				contentType: format,
				query: ['fields': FIELD_SET_LEVEL_FULL],
				requestContentType: URLENC)

		then: "he receives list of invoices of that order"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].type == "UnknownIdentifierError"
		}

		where:
		format << [XML, JSON]
	}
	
	
	def "Trusted client requests invoice binary by order code and invoice id: #format"() {
	
		assumeFalse(TestSetupUtils.isEndpointDisabled(DISABLED_ENDPOINTS_KEY,DISABLED_ENDPOINT_INVOICE_BINARY));
		
		given: "trusted client"
		authorizeTrustedClient(restClient)
		TestSetupUtils.updateSapInvoiceEnabled(true)

		when: "trusted client retrieves invoice binary by order code and invoice id"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + "/users/" + CUSTOMER_WITH_INVOICES.id + "/orders/" + ORDER_CODE + "/invoices/" + INVOICE_ID_1 + "/download",
				contentType: format,
				query: ['fields': FIELD_SET_LEVEL_FULL],
				requestContentType: URLENC)

		then: "he receives invoice binary of the invoice"
		with(response) {
			status == SC_OK
			contentType == BINARY.toString()
			response.getFirstHeader('Content-Disposition').getValue().contains(INVOICE_ID_1)
			data instanceof ByteArrayInputStream	
		}

		where:
		format << [BINARY]
	}	
	
	def "Trusted client requests binary invoice by invoice id and order code: #format"() {
	
	    assumeFalse(TestSetupUtils.isEndpointDisabled(DISABLED_ENDPOINTS_KEY,DISABLED_ENDPOINT_INVOICE_BINARY));
	    
		given: "trusted client"
		authorizeTrustedClient(restClient)
		TestSetupUtils.updateSapInvoiceEnabled(true)

		when: "trusted client requests binary invoice by invoice id and order code"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + "/users/" + CUSTOMER_WITH_INVOICES.id + "/orders/" + ORDER_CODE + "/invoices/" + INVOICE_ID_2 + "/download",
				contentType: format,
				query: ['fields': FIELD_SET_LEVEL_FULL],
				requestContentType: URLENC)

		then: "he receives no invoice binary as it is not present"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
			status == SC_NOT_FOUND
		}

		where:
		format << [BINARY]
	}	
}