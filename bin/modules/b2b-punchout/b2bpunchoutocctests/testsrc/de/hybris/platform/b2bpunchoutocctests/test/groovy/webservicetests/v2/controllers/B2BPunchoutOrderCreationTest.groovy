/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bpunchoutocctests.test.groovy.webservicetests.v2.controllers

import de.hybris.platform.core.model.order.CartModel
import de.hybris.platform.servicelayer.config.ConfigurationService
import org.w3c.dom.Document
import org.w3c.dom.NodeList
import org.xml.sax.InputSource

import javax.xml.XMLConstants
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPath
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.XML
import static org.apache.http.HttpStatus.*

import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.b2b.services.B2BOrderService
import de.hybris.platform.commercewebservicestests.test.groovy.webservicetests.v2.spock.AbstractSpockFlowTest
import de.hybris.platform.core.Registry
import de.hybris.platform.core.model.order.OrderModel
import de.hybris.platform.core.model.product.ProductModel
import de.hybris.platform.core.model.user.UserModel
import de.hybris.platform.order.CartService
import de.hybris.platform.product.ProductService
import de.hybris.platform.servicelayer.user.UserService
import org.apache.http.util.EntityUtils
import org.springframework.util.ResourceUtils
import spock.lang.Unroll

import java.util.regex.Pattern

@ManualTest
@Unroll
public class B2BPunchoutOrderCreationTest extends AbstractSpockFlowTest {

	static final TOTAL_PRICE = 27.5
	static final PUNCHOUT_CUSTOMER = "punchout.customer@punchoutorg.com"

	private static final String XERCES_FEATURE_PREFIX = "http://apache.org/xml/features/";
	private static final String LOAD_EXTERNAL_DTD_FEATURE = "nonvalidating/load-external-dtd";
	protected static final String LOAD_EXTERNAL_DTD = XERCES_FEATURE_PREFIX + LOAD_EXTERNAL_DTD_FEATURE;
	private static final XPath xPath = XPathFactory.newInstance().newXPath();
	private static final DocumentBuilder builder;
	static {
		final DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		builderFactory.setValidating(false);
		builderFactory.setFeature(LOAD_EXTERNAL_DTD, false);
		builderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		builder = builderFactory.newDocumentBuilder();
	}

	def "When Ariba sends a request asking for Punchout Order Creation transaction "() {
		given: "a CXML Order Request"
		File file = ResourceUtils.getFile("classpath:b2bpunchoutocctests/test/b2bpunchoutoccOrderCreation.xml")
		def cXMLRequest = file.text

		when: "a valid Http POST request is made"
		def fullpath = getBasePathWithSite() + "/punchout/cxml/order"
		def pr = restClient.getParser();
		Pattern myRegex = ~/Response/
		pr.putAt(XML) { response ->
			String responseXml = EntityUtils.toString(response.entity)
			EntityUtils.consume(response.entity)
			return responseXml
		}
		ProductService productService = Registry.getApplicationContext().getBean("productService")
		CartService cartService = Registry.getApplicationContext().getBean("cartService")
		B2BOrderService orderService = Registry.getApplicationContext().getBean("b2bOrderService");
		UserService userService = Registry.getApplicationContext().getBean("userService");
		ProductModel product = productService.getProductForCode("TW1210-9342")
		println "Product Description " + product.getName()

		def response = restClient.post(
				path: fullpath,
				contentType: XML,
				body: cXMLRequest,
				headers: ["accept": XML],
				requestContentType: XML)


		then: "a valid response is obtained"
		with(response) {
			response.status == SC_OK
			println "RESPONSE data " + response.data
			myRegex.matcher(response.data)
			def elementContent = getElementFromSetupResponse(response.data, "cXML/Response/Status");
			OrderModel order = orderService.getOrderForCode(elementContent);
			order.getTotalPrice() == TOTAL_PRICE
			order.getEntries().size() == 1
		}
	}

	def "When Ariba sends a request with not exist cart id asking for Punchout Order Creation transaction "() {
		given: "a CXML Order Request"
		File file = ResourceUtils.getFile("classpath:b2bpunchoutocctests/test/orderCreationWithNotExistCartId.xml")
		def cXMLRequest = file.text

		when: "a valid Http POST request is made"
		def fullpath = getBasePathWithSite() + "/punchout/cxml/order"
		def pr = restClient.getParser();
		Pattern myRegex = ~/Response/
		pr.putAt(XML) { response ->
			String responseXml = EntityUtils.toString(response.entity)
			EntityUtils.consume(response.entity)
			return responseXml
		}
		ProductService productService = Registry.getApplicationContext().getBean("productService")
		CartService cartService = Registry.getApplicationContext().getBean("cartService")
		B2BOrderService orderService = Registry.getApplicationContext().getBean("b2bOrderService");
		UserService userService = Registry.getApplicationContext().getBean("userService");
		ProductModel product = productService.getProductForCode("TW1210-9342")
		println "Product Description " + product.getName()

		def response = restClient.post(
				path: fullpath,
				contentType: XML,
				body: cXMLRequest,
				headers: ["accept": XML],
				requestContentType: XML)


		then: "a valid response is obtained"
		with(response) {
			response.status == SC_OK
			println "RESPONSE data " + response.data
			myRegex.matcher(response.data)
			def elementContent = getElementFromSetupResponse(response.data, "cXML/Response/Status");
			OrderModel order = orderService.getOrderForCode(elementContent);
			order.getTotalPrice() == TOTAL_PRICE
			order.getEntries().size() == 1
		}
	}

	def "When Ariba sends a request asking for Punchout Order Creation transaction with Content-Type #contentType"() {
		given: "a CXML Order Request"
		File file = ResourceUtils.getFile("classpath:b2bpunchoutocctests/test/b2bpunchoutoccOrderCreation.xml")
		def cXMLRequest = file.text

		when: "a Http POST request is made with Content-Type: #contentType"
		def fullpath = getBasePathWithSite() + "/punchout/cxml/order"
		def pr = restClient.getParser();
		pr.putAt(XML) { response ->
			String responseXml = EntityUtils.toString(response.entity)
			EntityUtils.consume(response.entity)
			return responseXml
		}

		def response = restClient.post(
				path: fullpath,
				contentType: contentType,
				body: cXMLRequest,
				headers: ["accept": contentType],
				requestContentType: contentType)


		then: "a response is obtained"
		with(response) {
			response.status == statusCode
		}

		where: "possible values header Content-Type are:"
		contentType        | statusCode
		"application/xml"  | SC_OK
		"text/xml"         | SC_OK
		"text/plain"       | SC_UNSUPPORTED_MEDIA_TYPE
	}

	def "When Ariba sends a request asking for Punchout Order Creation transaction with bad or missing data: #fileName "() {
		given: "a CXML Order Request"
		File file = ResourceUtils.getFile("classpath:b2bpunchoutocctests/test/" + fileName)
		def cXMLRequest = file.text

		when: "a Http POST request is made with bad data"
		def fullpath = getBasePathWithSite() + "/punchout/cxml/order"
		def pr = restClient.getParser();
		pr.putAt(XML) { response ->
			String responseXml = EntityUtils.toString(response.entity)
			EntityUtils.consume(response.entity)
			return responseXml
		}
		def response = restClient.post(
				path: fullpath,
				contentType: XML,
				body: cXMLRequest,
				headers: ["accept": XML],
				requestContentType: XML)


		then: "a server error response is obtained"
		with(response) {
			response.status == statusCode
			println "RESPONSE data " + response.data
			response.data.contains(errorMessage)
		}

		where: "possible values for bad data are:"
		fileName                                              | statusCode     | errorMessage
		"b2bpunchoutoccOrderCreationWithNonExisitingItem.xml" | SC_OK          | "Product with code TW1210-9342-NE is not found."
		"b2bpunchoutoccOrderCreationWithoutBillTo.xml"        | SC_OK          | "Miss ShipTo or BillTo in cxml request."
		"b2bpunchoutoccOrderCreationWithoutItemOut.xml"       | SC_OK          | "Miss ItemOut in cxml request."
		"b2bpunchoutoccOrderCreationWithoutShipTo.xml"        | SC_OK          | "Miss ShipTo or BillTo in cxml request."
		"b2bpunchoutoccOrderCreationWithoutCity.xml"          | SC_OK          | "Miss required information in address"
	}

	def "When Ariba sends a request asking for Punchout Order Creation transaction with a malformed XML"() {
		given: "a malformed CXML Request"
		File file = ResourceUtils.getFile("classpath:b2bpunchoutocctests/test/b2bpunchoutoccMalformedRequest.xml")
		def cXMLRequest = file.text

		when: "a Http POST request is made with malformed cxml"
		def fullpath = getBasePathWithSite() + "/punchout/cxml/order"
		def pr = restClient.getParser();
		pr.putAt(XML) { response ->
			String responseXml = EntityUtils.toString(response.entity)
			EntityUtils.consume(response.entity)
			return responseXml
		}

		def response = restClient.post(
				path: fullpath,
				contentType: XML,
				body: cXMLRequest,
				headers: ["accept": XML],
				requestContentType: XML)


		then: "an internal server error response is obtained"
		with(response) {
			response.status == SC_INTERNAL_SERVER_ERROR
			response.data.contains("UnmarshalException")
			response.data.contains("Content is not allowed in prolog.")
		}
	}

	def "When Ariba sends a request asking for Punchout Order Creation transaction with incorrect from node"() {
		given: "a malformed CXML Request"
		File file = ResourceUtils.getFile("classpath:b2bpunchoutocctests/test/orderCreationWithIncorrctFrom.xml")
		def cXMLRequest = file.text

		when: "a Http POST request is made with malformed cxml"
		def fullpath = getBasePathWithSite() + "/punchout/cxml/order"
		def pr = restClient.getParser();
		pr.putAt(XML) { response ->
			String responseXml = EntityUtils.toString(response.entity)
			EntityUtils.consume(response.entity)
			return responseXml
		}

		def response = restClient.post(
				path: fullpath,
				contentType: XML,
				body: cXMLRequest,
				headers: ["accept": XML],
				requestContentType: XML)


		then: "an internal server error response is obtained"
		with(response) {
			response.status == SC_OK
			response.data.contains("<Status code=\"401\" text=\"Authentication failed, please check if the credential for originator of the cXML request is mapped to an hybris user.\"/>")
		}
	}

	def "When Ariba sends a request asking for Punchout Order Creation transaction with different cart ids"() {
		given: "a malformed CXML Request"
		File file = ResourceUtils.getFile("classpath:b2bpunchoutocctests/test/orderCreationWithDiffCartIds.xml")
		def cXMLRequest = file.text

		when: "a Http POST request is made with malformed cxml"
		def fullpath = getBasePathWithSite() + "/punchout/cxml/order"
		def pr = restClient.getParser();
		pr.putAt(XML) { response ->
			String responseXml = EntityUtils.toString(response.entity)
			EntityUtils.consume(response.entity)
			return responseXml
		}

		def response = restClient.post(
				path: fullpath,
				contentType: XML,
				body: cXMLRequest,
				headers: ["accept": XML],
				requestContentType: XML)


		then: "an internal server error response is obtained"
		with(response) {
			response.status == SC_OK
			response.data.contains("<Status code=\"400\" text=\"supplierPartAuxiliaryID of ItemID in request cxml should be consistent.\"/>")
		}
	}

	def "When Ariba sends a request asking for Punchout Order Creation transaction with good cart id and validated pass"() {
		given: "a CXML Order Request"
		File file = ResourceUtils.getFile("classpath:b2bpunchoutocctests/test/b2bpunchoutoccOrderCreationWithCorrectCart.xml")
		def cXMLRequest = file.text

		when: "a Http POST request is made with good cart data"
		def fullpath = getBasePathWithSite() + "/punchout/cxml/order"
		def pr = restClient.getParser();
		Pattern myRegex = ~/Response/
		pr.putAt(XML) { response ->
			String responseXml = EntityUtils.toString(response.entity)
			EntityUtils.consume(response.entity)
			return responseXml
		}
		ConfigurationService configurationService = Registry.getApplicationContext().getBean("configurationService");
		configurationService.getConfiguration().setProperty("b2bpunchout.order.verification.enabled", "true")

		def response = restClient.post(
				path: fullpath,
				contentType: XML,
				body: cXMLRequest,
				headers: ["accept": XML],
				requestContentType: XML)

		then: "the cart validated pass and a response is obtained"
		with(response) {
			println "RESPONSE data " + response.data
			myRegex.matcher(response.data)
			def elementContent = getElementFromSetupResponse(response.data, "cXML/Response/Status");
			B2BOrderService orderService = Registry.getApplicationContext().getBean("b2bOrderService");
			OrderModel order = orderService.getOrderForCode(elementContent);

			response.status == SC_OK
			order.getCalculated() == Boolean.TRUE
		}
	}

	def "When Ariba sends a request asking for Punchout Order Creation transaction with cart code: #fileName but validated failed"() {
		given: "a CXML Order Request"
		File file = ResourceUtils.getFile("classpath:b2bpunchoutocctests/test/" + fileName)
		def cXMLRequest = file.text

		when: "a Http POST request is made with wrong cart data"
		def fullpath = getBasePathWithSite() + "/punchout/cxml/order"
		def pr = restClient.getParser();
		Pattern myRegex = ~/Response/
		pr.putAt(XML) { response ->
			String responseXml = EntityUtils.toString(response.entity)
			EntityUtils.consume(response.entity)
			return responseXml
		}
		ConfigurationService configurationService = Registry.getApplicationContext().getBean("configurationService");
		configurationService.getConfiguration().setProperty("b2bpunchout.order.verification.enabled", "true")

		def response = restClient.post(
				path: fullpath,
				contentType: XML,
				body: cXMLRequest,
				headers: ["accept": XML],
				requestContentType: XML)

		then: "cart validated fail and a response is obtained"
		with(response) {
			println "RESPONSE data " + response.data
			myRegex.matcher(response.data)
			def elementContent = getElementFromSetupResponse(response.data, "cXML/Response/Status");
			B2BOrderService orderService = Registry.getApplicationContext().getBean("b2bOrderService");
			OrderModel order = orderService.getOrderForCode(elementContent);

			response.status == statusCode
			order.getCalculated() == calculation
		}

		where: "possible values for wrong cart data are:"
		fileName                                                  | statusCode | calculation
		"b2bpunchoutoccOrderCreationWithWrongBasePriceCart.xml"   | SC_OK      | Boolean.FALSE
		"b2bpunchoutoccOrderCreationWithWrongProductCart.xml"     | SC_OK      | Boolean.FALSE
		"b2bpunchoutoccOrderCreationWithWrongQuantityCart.xml"    | SC_OK      | Boolean.FALSE
	}

	def "When Ariba sends a request asking for Punchout Order Creation transaction with cart code but verification property is not enabled"() {
		given: "a CXML Order Request"
		File file = ResourceUtils.getFile("classpath:b2bpunchoutocctests/test/b2bpunchoutoccOrderCreationWithCorrectCart.xml")
		def cXMLRequest = file.text

		when: "a Http POST request is made"
		def fullpath = getBasePathWithSite() + "/punchout/cxml/order"
		def pr = restClient.getParser();
		Pattern myRegex = ~/Response/
		pr.putAt(XML) { response ->
			String responseXml = EntityUtils.toString(response.entity)
			EntityUtils.consume(response.entity)
			return responseXml
		}
		ConfigurationService configurationService = Registry.getApplicationContext().getBean("configurationService");
		configurationService.getConfiguration().setProperty("b2bpunchout.order.verification.enabled", "false")

		def response = restClient.post(
				path: fullpath,
				contentType: XML,
				body: cXMLRequest,
				headers: ["accept": XML],
				requestContentType: XML)

		then: "the cart will be not validated and a response is obtained"
		with(response) {
			println "RESPONSE data " + response.data
			myRegex.matcher(response.data)
			def elementContent = getElementFromSetupResponse(response.data, "cXML/Response/Status");
			B2BOrderService orderService = Registry.getApplicationContext().getBean("b2bOrderService");
			OrderModel order = orderService.getOrderForCode(elementContent);

			response.status == SC_OK
			order.getCalculated() == Boolean.FALSE
		}
	}

	def getElementFromSetupResponse(cxml, regex) {
		final Document xmlDocument = builder.parse(new InputSource(new StringReader(cxml)));
		final NodeList nodeList = ((NodeList) xPath.compile(regex).evaluate(xmlDocument, XPathConstants.NODESET));
		return nodeList.item(0).getTextContent();
	}
}
