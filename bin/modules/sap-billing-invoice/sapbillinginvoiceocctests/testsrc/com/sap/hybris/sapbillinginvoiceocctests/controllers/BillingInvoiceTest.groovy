/**
 *
 */
package com.sap.hybris.sapbillinginvoiceocctests.controllers


import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.ANY
import static groovyx.net.http.ContentType.URLENC
import static groovyx.net.http.ContentType.BINARY
import static org.apache.http.HttpStatus.SC_OK
import static org.apache.http.HttpStatus.SC_NOT_FOUND
import de.hybris.platform.core.Registry
import java.io.ByteArrayInputStream

import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.commercewebservicestests.test.groovy.webservicetests.v2.spock.users.AbstractUserTest

import org.junit.BeforeClass
import org.junit.AfterClass

import groovyx.net.http.HttpResponseDecorator
import spock.lang.Unroll
import com.sap.hybris.sapbillinginvoicefacades.populator.ExternalSapBillingOrderPopulator
import com.sap.hybris.sapbillinginvoicefacades.strategy.SapBillingInvoiceStrategy
import com.sap.hybris.sapbillinginvoicefacades.facade.SapBillingInvoiceFacade
import groovyx.net.http.ResponseParseException
/**
 *
 *
 */
@ManualTest
@Unroll
class BillingInvoiceTest extends AbstractUserTest{
	
	private static Map<String, SapBillingInvoiceStrategy> originalHandlers;

	static final String username_with_orders = "orderhistoryuser@test.com"
	static final String ORDER_CODE = "testOrder1"
	static final String ORDER_CODE_2 = "testOrder2"
	static final String password_with_orders = "1234"
	static final customer_with_orders = ["id": username_with_orders, "password": password_with_orders]
	static final BILLING_ID_1 = "testBillingDocumentId1"
	static final BILLING_ID_2 = "testBillingDocumentId2"
	static final NON_EXISTING_BILLING_ID = "testBillingDocumentId3"
	static final BILLING_ID_1_PDF = "testBillingDocumentId1.pdf"
	static final ORDER_TYPE = "MOCK_ORDER_TYPE"
	private static final String HEADER_X_FILE_NAME = "filename"
	
	@BeforeClass
	public static void initMockStrategyInBillingPopulator() {
		final SapBillingInvoiceStrategy sapBillingInvoiceMockStrategy = Registry.getApplicationContext().getBean("sapBillingInvoiceMockStrategyImpl", SapBillingInvoiceStrategy.class);
		final ExternalSapBillingOrderPopulator externalSapBillingOrderPopulator = Registry.getApplicationContext().getBean("defaultExternalSapBillingOrderPopulator", ExternalSapBillingOrderPopulator.class);
		final SapBillingInvoiceFacade sapBillingInvoiceFacade = Registry.getApplicationContext().getBean("defaultBillingInvoiceOrderFacadeImpl", SapBillingInvoiceFacade.class);
		externalSapBillingOrderPopulator.registerHandler(ORDER_TYPE,sapBillingInvoiceMockStrategy)
		sapBillingInvoiceFacade.registerHandler(ORDER_TYPE,sapBillingInvoiceMockStrategy)
	}
	
	@AfterClass
	public static void settingOriginalStrategyInBillingPopulator() {
		final ExternalSapBillingOrderPopulator externalSapBillingOrderPopulator = Registry.getApplicationContext().getBean("defaultExternalSapBillingOrderPopulator", ExternalSapBillingOrderPopulator.class);
		final SapBillingInvoiceFacade sapBillingInvoiceFacade = Registry.getApplicationContext().getBean("defaultBillingInvoiceOrderFacadeImpl", SapBillingInvoiceFacade.class);
		externalSapBillingOrderPopulator.removeHandler(ORDER_TYPE);
		sapBillingInvoiceFacade.removeHandler(ORDER_TYPE);
	}

	def setup() {
		authorizeCustomer(restClient, customer_with_orders)
	}

	def "Customer retrieves all of his billing invoice : #format"() {

		when: "customer retrieves his billing invoice"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/users/' + customer_with_orders.id + "/orders/" + ORDER_CODE,
				contentType: format,
				requestContentType: URLENC)

		then: "he receives his invoices"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
			status == SC_OK
			data.code == ORDER_CODE
			data.billingDocuments.size() == 2
			data.billingDocuments[0].id == BILLING_ID_1
			data.billingDocuments[0].type == ORDER_TYPE
			data.billingDocuments[1].id == BILLING_ID_2
			data.billingDocuments[1].type == ORDER_TYPE
		}

		where:
		format << [JSON]
	}
	
	
	def "Customer retrieves order where there are no billing invoice : #format"() {
		
		when: "customer retrieves his billing invoice"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/users/' + customer_with_orders.id + "/orders/" + ORDER_CODE_2,
				contentType: format,
				requestContentType: URLENC)

		then: "he receives his invoices"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
			status == SC_OK
			data.code == ORDER_CODE_2
			data.billingDocuments.size() == 0
		}

		where:
		format << [JSON]
	}
	
	
	def "Customer retrieves his billing pdf : #format"() {

		when: "customer retrieves his billing pdf"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/users/' + customer_with_orders.id + "/orders/" + ORDER_CODE + "/billingDocuments/" + BILLING_ID_1 + ".pdf",
				contentType: format,
				requestContentType: URLENC)

		then:  "he receives his invoice details"
		
		with(response) {
			status == SC_OK
			contentType == BINARY.toString()
			response.getFirstHeader('Content-Disposition').getValue().contains(BILLING_ID_1_PDF)
			data instanceof ByteArrayInputStream	
		}

		where:
		format << [BINARY]
	}
	
	def "Customer tries to retrieve non-existing billing pdf : #billingDocumentId"() {
		
		when: "customer retrieves his billing pdf"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/users/' + customer_with_orders.id + "/orders/" + ORDER_CODE + "/billingDocuments/" + billingDocumentId + ".pdf",
				contentType: JSON,
				requestContentType: JSON)

		then:  "he gets not found response"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_NOT_FOUND
			data.errors.any { it.message == message && it.type == type }
		}

		where:
		billingDocumentId       | message              			 | type            
		NON_EXISTING_BILLING_ID | "Billing document not found" | "NotFoundError"

	}
	
	
}
