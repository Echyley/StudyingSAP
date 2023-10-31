/*
 * [y] hybris Platform
 *
 * Copyright (c) 2020 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.productconfig.integrationtests.hybris.occ

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC
import static groovyx.net.http.ContentType.XML
import static org.apache.http.HttpStatus.SC_BAD_REQUEST
import static org.apache.http.HttpStatus.SC_CREATED
import static org.apache.http.HttpStatus.SC_OK

import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.testframework.HybrisSpockRunner

import org.junit.Test
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory

import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.RESTClient

@ManualTest
@RunWith(HybrisSpockRunner.class)
class CartIntegrationWsIntegrationTest extends BaseSpockTest {


	protected static final PRODUCT_KEY = 'CPQ_HOME_THEATER'
	protected static final String ORDER_ENTRY_JSON = "{ \"orderEntry\" : {\"product\":{\"code\": \"${PRODUCT_KEY}\" }},\"qantity\": \"1\", \"configId\": \"1\"  }"
	protected static final String ORDER_ENTRY_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><orderEntry><product><code>${PRODUCT_KEY}</code></product><quantity>1</quantity><configId></configId></orderEntry>"
	org.slf4j.Logger LOG = LoggerFactory.getLogger(CartIntegrationWsIntegrationTest.class)

	@Test
	def  "Create a new empty cart"() {

		when: "consumer creates a new cart for an anonymous user"

		def cart = createEmptyCart(format)

		then: "the new cart has been created sucessfully and is empty"

		if(isNotEmpty(cart)&&isNotEmpty(cart.errors))
			println(cart)
		!isNotEmpty(cart.entries)
		isNotEmpty(cart.guid)
		isNotEmpty(cart.code)

		where:
		format <<[XML, JSON]
	}

	@Test
	def "Create a new cart with a configurable entry from catalog"() {

		when: "consumer tries to add a configurable product to the cart"

		def cart = createEmptyCart(format)
		def modification = addProductToCart(cart.guid, PRODUCT_KEY, format)

		then: "the item was added sucessfully"
		if(isNotEmpty(modification)&&isNotEmpty(modification.errors))
			println(modification)
		modification.statusCode == 'success'


		where:
		format <<[XML, JSON]
	}

	@Test
	def "Read configuration for an existing cart entry"() {

		when: "consumer reads configuration for a cart entry"
		def cart = createEmptyCart(format);
		def modification = addProductToCart(cart.guid, PRODUCT_KEY, format)
		def entryNumber = 0

		HttpResponseDecorator response = restClient.get(
				path: basePathWithSite + '/users/anonymous/carts/'+cart.guid+'/entries/' + entryNumber+SLASH_CONFIGURATOR_TYPE_OCC,
				contentType : format,
				query : ['fields' : FIELD_SET_LEVEL_BASIC],
				requestContentType: URLENC
				)

		then: "the configuration attached to the item could be read sucessfully and contains a configuration id"
		LOG.info("RESPONSE: "+response.data.toString())
		with(response) {
			status == SC_OK
			data.rootProduct == PRODUCT_KEY
			data.pricingEnabled == true
		}

		where:
		format <<[XML, JSON]
	}

	@Test
	def "Read generic configuration aspects for an existing cart entry"() {

		when: "consumer reads configuration for a cart entry via generic endpoint"
		def cart = createEmptyCart(format)
		def modification = addProductToCart(cart.guid, 'YSAP_SIMPLE_POC', format)
		def entryNumber = 0

		HttpResponseDecorator response = restClient.get(
				path: basePathWithSite + '/users/anonymous/carts/'+cart.guid,
				contentType : format,
				query : ['fields' : 'DEFAULT'],
				requestContentType: URLENC
				)

		then: "the generic configuration aspects attached to the item could be read sucessfully, showing issues as we cannot checkout"
		LOG.info("RESPONSE FROM GENERIC CART READ: "+response.data.toString())
		with(response) {
			status == SC_OK
			data.entries[0].entryNumber == 0
			data.entries[0].configurationInfos.size() == 1
			data.entries[0].configurationInfos[0].configurationLabel == 'Checkout'
			data.entries[0].configurationInfos[0].status == 'ERROR'
			data.entries[0].statusSummaryList.size() == 1
			data.entries[0].statusSummaryList[0].status == 'ERROR'
			data.entries[0].statusSummaryList[0].numberOfIssues == 0
		}

		where:
		format <<[XML, JSON]
	}

	@Test
	def "Create a new cart with a configurable entry from configuration "() {

		when: "consumer tries to add a configurable product to the cart"

		def cart = createEmptyCart(format)
		def configuration = createDefaultConfiguration(PRODUCT_KEY, restClient, format)
		LOG.info(configuration.toString())
		def postBody = orderToJsonMapping(PRODUCT_KEY, configuration.configId)
		//def modification = addConfigurableProductToCart(cart.guid, postbody, restClient, format)
		HttpResponseDecorator response = restClient.post(
				path: basePathWithSite + '/users/anonymous/carts/' + cart.guid + '/entries' + SLASH_CONFIGURATOR_TYPE_OCC,
				body: postBody,
				query: ['fields': FIELD_SET_LEVEL_FULL],
				contentType: format,
				requestContentType: requestFormat
				)
		then: "the item was added sucessfully"
		with(response) { status == SC_CREATED }

		where:
		requestFormat | format
		JSON          | JSON
	}
	

	@Test
	def "Update configuration in cart entry"(){

		when: "Consumer adds configurable product to the cart"
		def cart = createEmptyCart(format)
		def entryNumber = 0
		def modification = addConfigurableProductToCartWithId(cart.guid, PRODUCT_KEY, restClient, requestFormat, format)

		HttpResponseDecorator response = restClient.get(
				path: basePathWithSite + '/users/anonymous/carts/'+cart.guid+'/entries/' + entryNumber+ SLASH_CONFIGURATOR_TYPE_OCC,
				contentType: format,
				query : ['fields' : FIELD_SET_LEVEL_BASIC],
				requestContentType : requestFormat
				)

		then: "A consistent, incomplete configuration attached to the entry can be read sucessfully"
		LOG.info("Response after adding product to cart: "+response.data.toString())
		with(response){
			status == SC_OK
			data.rootProduct == PRODUCT_KEY
			data.consistent == true
			data.complete == false
			data.totalNumberOfIssues == 1
		}


		when: "Configuration is changed afterwards"
		def putBody = response.data
		putBody.groups[0].attributes[0].value = "STEREO"


		HttpResponseDecorator responseAfterUpdate = restClient.patch(
				path: getBasePathWithSite() + SLASH_CONFIGURATOR_TYPE_OCC_SLASH + response.data.configId,
				body : putBody,
				contentType: format
				)

		then: "Change is accepted and new value is available, total number of issues increases to 2"
		LOG.info("Response after update: "+responseAfterUpdate.data.toString())

		with(responseAfterUpdate){
			status == SC_OK
			data.rootProduct == PRODUCT_KEY
			data.consistent == true
			data.complete == false
			data.groups[0].attributes[0].value == "STEREO"
			data.totalNumberOfIssues == 2
		}

		when: "Afterwards change the group"
		String configId = response.data.configId

		HttpResponseDecorator response2 = restClient.get(
				path: getBasePathWithSite() + SLASH_CONFIGURATOR_TYPE_OCC_SLASH + configId  ,
				contentType: format,
				query:['groupId': '3-CPQ_FRONT_SPEAKERS@_GEN']
				)

		then: "Group is changed"
		LOG.info("Response after changing group: "+response2.data.toString())
		with(response2){
			status == SC_OK
			data.totalNumberOfIssues == 2
		}


		when: "Configuration is changed afterwards"
		def putBody2 = response2.data
		putBody2.groups[3].subGroups[0].attributes[0].value = "YM_NS_F160"

		HttpResponseDecorator responseAfterUpdate2 = restClient.patch(
				path: getBasePathWithSite() + SLASH_CONFIGURATOR_TYPE_OCC_SLASH + response.data.configId,
				body : putBody2,
				contentType: format
				)

		then: "Change is accepted, configuration is still incomplete but total number of issues decreased"
		LOG.info("Response after update2: "+responseAfterUpdate2.data.toString())

		with(responseAfterUpdate2){
			status == SC_OK
			data.rootProduct == PRODUCT_KEY
			data.consistent == true
			data.complete == false
			data.totalNumberOfIssues == 1
		}

		when: "Configuration is changed afterwards"
		def putBody3 = responseAfterUpdate2.data
		putBody3.groups[3].subGroups[0].attributes[1].value = "BLK"

		HttpResponseDecorator responseAfterUpdate3 = restClient.patch(
				path: getBasePathWithSite() + SLASH_CONFIGURATOR_TYPE_OCC_SLASH + response.data.configId,
				body : putBody3,
				contentType: format
				)

		then: "Change is accepted, configuration is complete and has no issues"
		LOG.info("Response after update3: "+responseAfterUpdate3.data.toString())

		with(responseAfterUpdate3){
			status == SC_OK
			data.rootProduct == PRODUCT_KEY
			data.consistent == true
			data.complete == true
			data.totalNumberOfIssues == 0
		}

		if(isExtensionInSetup("sapproductconfigservicesssc")) {
			LOG.info("Session lifecycle implemented by sapproductconfigservicesssc is not fully compatible with OCC, hence skipping update cart part of the test");
			return;
		}

		when: "Cart is updated with new configuration state"
		def patchBody = orderToJsonMapping(PRODUCT_KEY, responseAfterUpdate.data.configId)
		HttpResponseDecorator responseAfterUpdateCart = restClient.put(
				path: basePathWithSite + '/users/anonymous/carts/' + cart.guid + '/entries/'+ entryNumber + SLASH_CONFIGURATOR_TYPE_OCC,
				body: patchBody,
				query: ['fields': FIELD_SET_LEVEL_FULL],
				contentType: format,
				requestContentType: requestFormat
				)
		then: "Cart update is accepted"
		with(responseAfterUpdateCart) {  status == SC_OK  }


		when: "Consumer attempts to update the configuration again that is no draft anymore"
		def putBodySecondChange = responseAfterUpdate.data
		putBodySecondChange.groups[0].attributes[0].value = "SURROUND"
		HttpResponseDecorator responseAfterSecondUpdate = restClient.patch(
				path: getBasePathWithSite() + SLASH_CONFIGURATOR_TYPE_OCC_SLASH + responseAfterUpdate.data.configId,
				body : putBodySecondChange,
				contentType: format
				)

		then: "Change is rejected"
		with(responseAfterSecondUpdate){ status == SC_BAD_REQUEST }



		where:
		requestFormat | format
		JSON | JSON
	}

	protected addConfigurableProductToCart(guid, postbody , RESTClient client, requestFormat, responseFormat, basePathWithSite=getBasePathWithSite()) {
		def modification = returningWith(client.post(
				//Note that DefaultCartLoaderStrategy calls commerceCartService.getCartForGuidAndSite when searching a cart with ID.
				//So for anonymous carts we need to call with the guid, while for user carts we would need the cart code
				path: basePathWithSite + '/users/anonymous/carts/'+guid+'/entries'+ SLASH_CONFIGURATOR_TYPE_OCC,
				body: postbody,
				contentType: responseFormat,
				query : ['fields' : FIELD_SET_LEVEL_FULL],
				requestContentType: requestFormat), {
					if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
					status == SC_OK
				}).data

		return modification
	}

	protected addConfigurableProductToCartWithId(guid, productId , RESTClient client, requestFormat, responseFormat, basePathWithSite=getBasePathWithSite()) {
		def postbody = orderToJsonMapping(productId, "")
		def modification = returningWith(client.post(
				//Note that DefaultCartLoaderStrategy calls commerceCartService.getCartForGuidAndSite when searching a cart with ID.
				//So for anonymous carts we need to call with the guid, while for user carts we would need the cart code
				path: basePathWithSite + '/users/anonymous/carts/'+guid+'/entries' + SLASH_CONFIGURATOR_TYPE_OCC,
				body: postbody,
				contentType: responseFormat,
				query : ['fields' : FIELD_SET_LEVEL_FULL],
				requestContentType: requestFormat), {
					if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
					status == SC_OK
				}).data

		return modification
	}



}
