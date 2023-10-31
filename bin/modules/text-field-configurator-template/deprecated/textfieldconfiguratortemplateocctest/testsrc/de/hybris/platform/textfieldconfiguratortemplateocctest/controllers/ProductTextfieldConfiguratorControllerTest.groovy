/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.textfieldconfiguratortemplateocctest.controllers

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.XML
import static org.apache.http.HttpStatus.SC_BAD_REQUEST
import static org.apache.http.HttpStatus.SC_OK
import static org.apache.http.HttpStatus.SC_NOT_FOUND
import static org.apache.http.HttpStatus.SC_CREATED
import static groovyx.net.http.ContentType.URLENC


import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.RESTClient
import groovyx.net.http.ContentType

import org.apache.log4j.Logger

import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.carts.AbstractCartTest
import org.junit.Test
import spock.lang.Unroll
import org.junit.runner.RunWith
import de.hybris.platform.testframework.HybrisSpockRunner


@ManualTest
@Unroll
@RunWith(HybrisSpockRunner.class)
class ProductTextfieldConfiguratorControllerTest extends AbstractCartTest {

    private static final SLASH_CONFIGURATOR_TYPE_TEXTFIELD = '/configurator/textfield'
   
    private static final Logger LOG = Logger.getLogger(ProductTextfieldConfiguratorControllerTest.class);
    private final static String PRODUCT_CODE = "AK_CAMERA_KIT"

	 @Test
    def "Expected #configSize text field configurations for product #productCode in format #format"() {
        when: "creates a new configuration"
        def response = restClient.get(
                path: getBasePathWithSite() + '/products/' + productCode + SLASH_CONFIGURATOR_TYPE_TEXTFIELD,
                contentType: format
        )

        then: "it gets a new text field configuration"
        with(response) {
            status == SC_OK
            data.configurationInfos.size() == configSize
        }

        where:
        productCode  | configSize | format
        PRODUCT_CODE | 3          | JSON
        PRODUCT_CODE | 3          | XML
    }

	 @Test
    def "Product not found - format #format"() {
        when: "creates a new configuration"
        def response = restClient.get(
                path: getBasePathWithSite() + '/products/4711' + SLASH_CONFIGURATOR_TYPE_TEXTFIELD,
                contentType: format
        )

        then: "product code not found"
        with(response) { status == SC_BAD_REQUEST }

        where:
        format << [XML, JSON]
    }

	 @Test
    def "Add product #productCode with qty #qty to cart"() {
        given: "anonymous user with cart"
        def customer = ['id': 'anonymous']
        def cart = createAnonymousCart(restClient, responseFormat)

        when: "customer decides to add product to cart"
        def response = restClient.post(
                path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + cart.guid + '/entries'+SLASH_CONFIGURATOR_TYPE_TEXTFIELD,
                query: ['fields': FIELD_SET_LEVEL_FULL],
                body: postBody,
                contentType: responseFormat,
                requestContentType: requestFormat
        )

        then: "a new entry is added to the cart"
        with(response) {
            status == SC_OK
            data.statusCode == 'success'
            data.quantityAdded == 2
            data.entry.configurationInfos.size() == 1
            data.entry.configurationInfos[0].configurationValue == 'Hans'
        }

        where:
        productCode  | qty | requestFormat | responseFormat | postBody
        PRODUCT_CODE | 2   | JSON          | JSON           | "{\"configurationInfos\": [ {\"configurationLabel\": \"Font Type\", \"configurationValue\": \"Hans\", \"configuratorType\": \"TEXTFIELD\", \"status\": \"SUCCESS\" }], \"product\" : {\"code\" : \"" + productCode + "\"},\"quantity\" : " + qty + "}"
    }

	 @Test
    def "Get configuration for cart entry"() {
        given: "anonymous user with cart"
        def customer = ['id': 'anonymous']
        def cart = createAnonymousCart(restClient, JSON)

        and: "cart with added textfield configuration"
        def cartEntry = restClient.post(
                path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + cart.guid + '/entries'+SLASH_CONFIGURATOR_TYPE_TEXTFIELD,
                query: ['fields': FIELD_SET_LEVEL_FULL],
                body: "{\"product\" : {\"code\" : \"" + PRODUCT_CODE + "\"},\"quantity\" : 2, \"configurationInfos\": [{\"configurationLabel\": \"Font Type\", \"configurationValue\": \"Hans\", \"configuratorType\": \"TEXTFIELD\", \"status\": \"SUCCESS\" }]}",
                contentType: JSON,
                requestContentType: JSON).data

        when: "customer want configuration of a cart entry"
        def response = restClient.get(
                path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + cart.guid + '/entries/' + cartEntry.entry.entryNumber + SLASH_CONFIGURATOR_TYPE_TEXTFIELD,
                contentType: JSON,
        )

        then: "the configuration infos of the cart entry"
        with(response) {
            status == SC_OK
            data.configurationInfos.size() == 1
            data.configurationInfos[0].configurationValue == 'Hans'
        }

        when: "customer want to update configuration of a cart entry"
        def updateReponse = restClient.post(
                path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + cart.guid + '/entries/' + cartEntry.entry.entryNumber + SLASH_CONFIGURATOR_TYPE_TEXTFIELD,
                body: "{\"product\" : {\"code\" : \"" + PRODUCT_CODE + "\"},\"quantity\" : 2, \"configurationInfos\": [{\"configurationLabel\": \"Font Type\", \"configurationValue\": \"Max\", \"configuratorType\": \"TEXTFIELD\", \"status\": \"SUCCESS\" }]}",
                contentType: JSON,
                requestContentType: JSON)

        then: "the configuration info was updated in the cart entry"
        with(updateReponse) {
            status == SC_OK
            data.quantityAdded == 0
            data.quantity == 2
            data.entry.configurationInfos.size() == 1
            data.entry.configurationInfos[0].configurationValue == 'Max'
        }
    }
  
  @Test
  def "Newly registered customer saves a cart and checks for textfield configuration"() {
  
      given: "a registered customer with one cart"
      def (customer, cart)= createCartWithOneEntry(requestFormat, responseFormat, postBody) 

      when: "Customer wants to save the cart"
      def responseAfterSave = restClient.patch(
            path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + cart.code + '/save',
            query: ['fields': FIELD_SET_LEVEL_FULL],
            contentType: responseFormat,
            requestContentType: URLENC
      )

      then: "they receive success and retrieve the saved cart"
      with(responseAfterSave) {
         if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
         status == SC_OK
         isNotEmpty(data.savedCartData)
         data.savedCartData.code == cart.code
         data.savedCartData.name == cart.code 
      }
      LOG.info("Saved cart code: " + cart.code)
      
      when: "Customer wants to retrieve the configuration details of the saved cart"
      def responseFromReadConfig = restClient.get(
            path: getBasePathWithSite() + '/users/' + customer.id + '/savedCarts/' + cart.code + '/entries/0'+SLASH_CONFIGURATOR_TYPE_TEXTFIELD,
            query: ['fields': FIELD_SET_LEVEL_BASIC],
            contentType: responseFormat,
            requestContentType: URLENC
      )
      then: "they retrieve the configuration details and see the default configuration"
      checkDefaultConfiguration(responseFromReadConfig)==true 
 

      where:
      requestFormat | responseFormat | postBody
      JSON          | JSON           | "{\"product\" : {\"code\" : \"${PRODUCT_CODE}\"},\"quantity\" : 1}"
      XML           | XML            | "<?xml version=\"1.0\" encoding=\"UTF-8\"?><orderEntry><product><code>${PRODUCT_CODE}</code></product><quantity>1</quantity></orderEntry>"

   }
   
 @Test
 def "Newly registered customer orders a cart and checks for textfield configuration"() {
      given: "a registered customer with one cart"
      def (customer, cart)= createCartWithOneEntry(requestFormat, responseFormat, postBody) 

      when: "Customer orders cart"
      def address = createAddress(restClient, customer)
      setDeliveryAddressForCart(restClient, customer, cart.code, address.id, requestFormat)
      setDeliveryModeForCart(restClient, customer, cart.code, DELIVERY_STANDARD, requestFormat)
      createPaymentInfo(restClient, customer, cart.code)
      def order = placeOrder(restClient, customer, cart.code, "123", requestFormat)
      
      then: "An order number is available"
      with(order) {
         order.code != ""
      }
      LOG.info("Order has been created with id: " + order.code)
      
      when: "Customer wants to retrieve the configuration details of the order"
      def responseFromReadConfig = restClient.get(
            path: getBasePathWithSite() + '/users/' + customer.id + '/orders/' + order.code + '/entries/0'+SLASH_CONFIGURATOR_TYPE_TEXTFIELD,
            query: ['fields': FIELD_SET_LEVEL_BASIC],
            contentType: responseFormat,
            requestContentType: URLENC
      )
      then: "they retrieve the configuration details and see the default configuration"
      checkDefaultConfiguration(responseFromReadConfig) == true 

      where:
      requestFormat | responseFormat | postBody
      JSON         | JSON          | "{\"product\" : {\"code\" : \"${PRODUCT_CODE}\"},\"quantity\" : 1}"
      XML          | XML           | "<?xml version=\"1.0\" encoding=\"UTF-8\"?><orderEntry><product><code>${PRODUCT_CODE}</code></product><quantity>1</quantity></orderEntry>"

   }
   
 @Test
 def "Existing customer views textfield configuration of a quote entry"() {
      def customerId = "tiger@rustic-hw.com"
      def quoteCode = "7777777776"
      authorizeTrustedClient(restClient)
      
      when: "Customer wants to retrieve the configuration details of the quote"
      def responseFromReadConfig = restClient.get(
            path: getBasePathWithSite() + '/users/' + customerId + '/quotes/' + quoteCode + '/entries/0'+SLASH_CONFIGURATOR_TYPE_TEXTFIELD,
            query: ['fields': FIELD_SET_LEVEL_BASIC],
            contentType: responseFormat,
            requestContentType: URLENC
      )
      then: "they retrieve the configuration details and see the configuration that we attached"
      with(responseFromReadConfig) {
         if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
         data.configurationInfos.size() == 1
         data.configurationInfos[0].configurationLabel == 'TheLabel'
         data.configurationInfos[0].configurationValue == 'TheValue' 
      }

      where:
      requestFormat | responseFormat | postBody
      JSON         | JSON          | "{\"product\" : {\"code\" : \"${PRODUCT_CODE}\"},\"quantity\" : 1}"
      XML          | XML           | "<?xml version=\"1.0\" encoding=\"UTF-8\"?><orderEntry><product><code>${PRODUCT_CODE}</code></product><quantity>1</quantity></orderEntry>"

   }      

   
    protected placeOrder(RESTClient client, customer, cartID, securityCode, format) {
      HttpResponseDecorator response = restClient.post(
        path: getBasePathWithSite() + '/users/' + customer.id + '/orders',
        query: [
          'cartId'      : cartID,
          'securityCode': securityCode,
          'fields'      : FIELD_SET_LEVEL_FULL
        ],
        contentType: format,
        requestContentType: URLENC
      )

      with(response) {
        if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
          status == SC_CREATED
        }
      return response.data
    }

    protected createCartWithOneEntry(ContentType requestFormat, ContentType responseFormat, postBody) {
      authorizeTrustedClient(restClient)
      def customer = registerCustomer(restClient)
      LOG.info("Customer has been created with id: " + customer.id)
      
      def cart = createCart(restClient, customer, requestFormat)
      
      def response = restClient.post(
            path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + cart.code + '/entries',
            query: ['fields': FIELD_SET_LEVEL_FULL],
            body: postBody,
            contentType: responseFormat,
            requestContentType: requestFormat
      )
      
      with(response) {
         status == SC_OK
         data.statusCode == 'success'
         data.quantityAdded == 1;
      } 
      new Tuple2(customer, cart)
    }

    protected checkDefaultConfiguration(responseFromReadConfig) {
      with(responseFromReadConfig) {
         if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
         data.configurationInfos.size() == 3
         data.configurationInfos[0].configurationLabel == 'Font Type'
         data.configurationInfos[0].configurationValue == 'Webdings'
         data.configurationInfos[1].configurationLabel == 'Font Size'
         data.configurationInfos[1].configurationValue == '14'
         data.configurationInfos[2].configurationLabel == 'Engraved Text'
      }
      return responseFromReadConfig.status == SC_OK
    }

}
