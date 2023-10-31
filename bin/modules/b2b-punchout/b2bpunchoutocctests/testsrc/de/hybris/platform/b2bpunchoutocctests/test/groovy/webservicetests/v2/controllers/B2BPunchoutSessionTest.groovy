/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bpunchoutocctests.test.groovy.webservicetests.v2.controllers

import de.hybris.platform.b2b.punchout.PunchOutSession
import de.hybris.platform.b2b.punchout.model.StoredPunchOutSessionModel
import de.hybris.platform.b2b.punchout.services.PunchOutSessionService
import de.hybris.platform.core.Registry
import de.hybris.platform.servicelayer.model.ModelService
import org.apache.commons.lang.StringUtils

import static groovyx.net.http.ContentType.JSON
import static org.apache.http.HttpStatus.SC_NOT_FOUND
import static org.apache.http.HttpStatus.SC_OK
import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR

import de.hybris.platform.commercewebservicestests.test.groovy.webservicetests.v2.spock.AbstractSpockFlowTest
import org.springframework.util.ResourceUtils
import org.w3c.dom.Document
import org.w3c.dom.NodeList
import org.xml.sax.InputSource

import javax.xml.XMLConstants
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPath
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory
import static groovyx.net.http.ContentType.XML

import de.hybris.bootstrap.annotations.ManualTest
import org.apache.http.util.EntityUtils
import spock.lang.Unroll

@ManualTest
@Unroll
public class B2BPunchoutSessionTest extends AbstractSpockFlowTest {
	private static final String XERCES_FEATURE_PREFIX = "http://apache.org/xml/features/";
	private static final String LOAD_EXTERNAL_DTD_FEATURE = "nonvalidating/load-external-dtd";
	protected static final String LOAD_EXTERNAL_DTD = XERCES_FEATURE_PREFIX + LOAD_EXTERNAL_DTD_FEATURE;
	private static final XPath xPath = XPathFactory.newInstance().newXPath();
	private static final DocumentBuilder builder;
	private static final String PRODUCT1_BASE_PRICE = "75.0";
	private static final String CART_TOTAL_PRICE = "375.0";
	private static String clientId = "punchout_client";
	private static String userName = "punchout.customer3@punchoutorg.com";
	private static String password = "pwd4all";
	static {
		final DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		builderFactory.setValidating(false);
		builderFactory.setFeature(LOAD_EXTERNAL_DTD, false);
		builderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		builder = builderFactory.newDocumentBuilder();
	}

	def "When Ariba sends a setUp request asking for Punchout session transaction and the fields are #filedName"() {
		given: "a CXML Setup Request and get uid from response"
		File file = ResourceUtils.getFile("classpath:b2bpunchoutocctests/test/b2bpunchoutoccSetupRequestForSessionApi.xml")
		def cXMLRequest = file.text
		def sid = getSIDFromSetupResponse(cXMLRequest)
		when: "a valid punchOut session Http get request is made"
		String SessionAPIPath = getBasePathWithSite() + "/punchout/sessions/" + sid
		def responseForSession = restClient.get(
				path: SessionAPIPath,
				query: ["fields": fields],
				contentType: JSON,
				requestContentType: JSON)
		def punchOutOperationList = ["create", "edit", "inspect"]
		then: "a valid response is obtained by punchOut session api"
		with(responseForSession) {
			responseForSession.status == SC_OK
			def actualFieldList = getActualFieldsFromResponseForSession(responseForSession.data)
			def expectedFieldList = Arrays.asList(expectedFields.split(","))
			actualFieldList.containsAll(expectedFieldList)
			actualFieldList.size() == expectedFieldList.size()
			if (actualFieldList.contains("cartId")) {
				isNotEmpty(responseForSession.responseData.cartId)
			}
			if (actualFieldList.contains("accessToken")) {
				isNotEmpty(responseForSession.responseData.token.accessToken)
			}
			if (actualFieldList.contains("tokenType")) {
				isNotEmpty(responseForSession.responseData.token.tokenType)
			}
			if (actualFieldList.contains("customerId")) {
				isNotEmpty(responseForSession.responseData.customerId)
			}
			if (actualFieldList.contains("selectedItem")) {
				responseForSession.responseData.selectedItem.equalsIgnoreCase(getElementFromSetupResponse(cXMLRequest, "cXML//SelectedItem/ItemID/SupplierPartID"))
			}
			if (actualFieldList.contains("punchOutOperation")) {
				responseForSession.responseData.punchOutOperation.equalsIgnoreCase(getElementAttributeFromSetupResponse(cXMLRequest, "cXML//PunchOutSetupRequest", "operation"))
				punchOutOperationList.contains(responseForSession.responseData.punchOutOperation.toLowerCase())
			}
			if (actualFieldList.contains("punchOutLevel")) {
				responseForSession.responseData.punchOutLevel.equalsIgnoreCase(responseForSession.responseData.selectedItem.startsWith("product-") ? "PRODUCT" : "STORE")
			}
		}
		where:
		filedName                    | fields                       | expectedFields
		"cartId, token(accessToken)" | "cartId, token(accessToken)" | "cartId,token,accessToken"
		"BASIC"                      | "BASIC"                      | "cartId,punchOutLevel,punchOutOperation,selectedItem,token,accessToken,tokenType,customerId"
		"DEFAULT"                    | "DEFAULT"                    | "cartId,punchOutLevel,punchOutOperation,selectedItem,token,accessToken,tokenType,customerId"
		"FULL"                       | "FULL"                       | "cartId,punchOutLevel,punchOutOperation,selectedItem,token,accessToken,tokenType,customerId"
		"NULL"                       | ""                           | "cartId,punchOutLevel,punchOutOperation,selectedItem,token,accessToken,tokenType,customerId"
	}

	def "When Ariba sends a setUp request asking for Punchout session transaction and the specific fields are #filedName"() {
		given: "a CXML Setup Request and get uid from response"
		File file = ResourceUtils.getFile("classpath:b2bpunchoutocctests/test/b2bpunchoutoccSetupRequestForSessionApi.xml")
		def cXMLRequest = file.text
		def sid = getSIDFromSetupResponse(cXMLRequest)
		when: "a valid punchOut session Http get request is made"
		String SessionAPIPath = getBasePathWithSite() + "/punchout/sessions/" + sid
		def responseForSession = restClient.get(
				path: SessionAPIPath,
				query: ["fields": fields],
				contentType: JSON,
				requestContentType: JSON)
		def punchOutOperationList = ["create", "edit", "inspect"]
		then: "a valid response is obtained by punchOut session api"
		with(responseForSession) {
			responseForSession.status == SC_OK
			isNotEmpty(responseForSession.responseData.cartId)
			isNotEmpty(responseForSession.responseData.token.accessToken)
			isEmpty(responseForSession.responseData.token.tokenType)
			isEmpty(responseForSession.responseData.punchOutLevel)
			isEmpty(responseForSession.responseData.punchOutOperation)
			isEmpty(responseForSession.responseData.selectedItem)
			isEmpty(responseForSession.responseData.customerId)
		}
		where:
		filedName                    | fields                       | expectedFields
		"cartId, token(accessToken)" | "cartId, token(accessToken)" | "cartId,token,accessToken"
	}


	def "When Ariba sends a setUp request asking for Punchout session transaction with expire sid"() {
		given: "a CXML Setup Request and get uid from response"
		File file = ResourceUtils.getFile("classpath:b2bpunchoutocctests/test/b2bpunchoutoccSetupRequestForSessionApi.xml")
		def cXMLRequest = file.text
		def sid = getSIDFromSetupResponse(cXMLRequest)
		PunchOutSessionService punchoutSessionService = Registry.getApplicationContext().getBean("defaultPunchOutSessionService")
		ModelService modelService = Registry.getApplicationContext().getBean("modelService")
		StoredPunchOutSessionModel storedSession = punchoutSessionService.loadStoredPunchOutSessionModel(sid)
		PunchOutSession punchoutSession = storedSession.getPunchOutSession()
		Calendar cal = Calendar.getInstance();
		cal.setTime(punchoutSession.getTime());
		cal.add(Calendar.DATE, -10);
		punchoutSession.setTime(cal.getTime())
		modelService.save(storedSession)

		when: "a expire sid punchOut session Http get request is made"
		String SessionAPIPath = getBasePathWithSite() + "/punchout/sessions/" + sid
		def responseForSession = restClient.get(
				path: SessionAPIPath,
				contentType: JSON,
				requestContentType: JSON)
		then: "a invalid response is obtained"
		with(responseForSession) {
			responseForSession.status == SC_NOT_FOUND
			responseForSession.responseData.errors.type.get(0).equalsIgnoreCase("NotFoundError")
			responseForSession.responseData.errors.message.get(0).equalsIgnoreCase("no valid sid found")
		}
	}

	def "Get PunchOut session info when cart empty"() {
		given: "a CXML Setup Request and get uid from response"
		File file = ResourceUtils.getFile("classpath:b2bpunchoutocctests/test/b2bpunchoutoccSetupRequestForSessionApi.xml")
		def cXMLRequest = file.text
		def sid = getSIDFromSetupResponse(cXMLRequest)
		String SessionAPIPath = getBasePathWithSite() + "/punchout/sessions/" + sid
		def responseForSession = restClient.get(
				path: SessionAPIPath,
				contentType: JSON,
				requestContentType: JSON)

		and: "delete cart throw current punchOut session"
		def oauth2Token = getOAuth2TokenUsingPassword(restClient, clientId, "", userName, password, false);
		restClient.getHeaders().put('Authorization', ' Bearer ' + oauth2Token.access_token)
		restClient.delete(
				path: getBasePathWithSite() + "/users/current/carts/" + responseForSession.responseData.cartId,
				contentType: JSON,
				requestContentType: JSON)
		restClient.getHeaders().remove('Authorization')

		when: "get punchOut session info throw current sid again"
		def responseForSessionToEmptyCart = restClient.get(
				path: SessionAPIPath,
				contentType: JSON,
				requestContentType: JSON)
		then: "a invalid response is obtained"
		with(responseForSessionToEmptyCart) {
			responseForSessionToEmptyCart.status == SC_INTERNAL_SERVER_ERROR
			responseForSessionToEmptyCart.responseData.errors.type[0].equalsIgnoreCase("PunchOutCartMissingError")
			responseForSessionToEmptyCart.responseData.errors.message[0].contains("empty cart")
		}
	}

	def "When Ariba sends a setUp request asking for Punchout session transaction with invalid sid"() {
		given: "error parameter about invalid sid"
		def sid = 'invalidSid';
		when: "a invalid sid Http POST request is made"
		String SessionAPIPath = getBasePathWithSite() + "/punchout/sessions/" + sid
		def responseForSession = restClient.get(
				path: SessionAPIPath,
				contentType: JSON,
				requestContentType: JSON)
		then: "a invalid response is obtained"
		with(responseForSession) {
			responseForSession.status == SC_NOT_FOUND
			responseForSession.responseData.errors.type.get(0).equalsIgnoreCase("NotFoundError")
		}
	}

	def "When Punchout customer request to get requisition data with cart entries(discardCartEntries is false)"() {
		given: "a CXML Setup Request and get sid from response"
		File file = ResourceUtils.getFile("classpath:b2bpunchoutocctests/test/b2bpunchoutoccSetupRequest.xml")
		def cXMLRequest = file.text

		and: "he request to get sid from Setup Response"
		def sid = getSIDFromSetupResponse(cXMLRequest)

		and: "he request to get punchoutSession from sid"
		def punchoutSession = getPunchoutSessionFromSid(sid)

		and: "he request to add some product to cart"
		addSpecificProductToCart(punchoutSession, 5, sid)

		when: "he request to get requisition data with cart entries"
		String requisitionAPIPath = getBasePathWithSite() + "/punchout/sessions/" + sid + "/requisition"
		def responseForRequisition = restClient.get(
				path: requisitionAPIPath,
				query: [
						"fields": fields
				],
				contentType: JSON,
				requestContentType: JSON)
		then: "A successful response should be returned"
		with(responseForRequisition) {
			responseForRequisition.status == SC_OK
			def actualFieldList = getActualFieldsFromResponseForSession(responseForRequisition.data)
			def expectedFieldList = Arrays.asList(expectedFields.split(","))
			actualFieldList.containsAll(expectedFieldList)
			StringUtils.isNotBlank(responseForRequisition.responseData.orderAsCXML)
			getElementFromSetupResponse(new String(responseForRequisition.responseData.orderAsCXML.decodeBase64()),
					"cXML//PunchOutOrderMessage/ItemIn/ItemID/SupplierPartID").equalsIgnoreCase(punchoutSession.selectedItem)
			getElementFromSetupResponse(new String(responseForRequisition.responseData.orderAsCXML.decodeBase64()),
					"cXML//PunchOutOrderMessage/ItemIn/ItemID/SupplierPartAuxiliaryID").equalsIgnoreCase(punchoutSession.cartId)
			getElementFromSetupResponse(new String(responseForRequisition.responseData.orderAsCXML.decodeBase64()),
					"cXML//PunchOutOrderMessage/ItemIn/ItemDetail/UnitPrice/Money").equalsIgnoreCase(PRODUCT1_BASE_PRICE)
			getElementFromSetupResponse(new String(responseForRequisition.responseData.orderAsCXML.decodeBase64()),
					"cXML//PunchOutOrderMessage/PunchOutOrderMessageHeader/Total/Money").equalsIgnoreCase(CART_TOTAL_PRICE)
			if (responseForRequisition.responseData.browseFormPostUrl) {
				responseForRequisition.responseData.browseFormPostUrl.equalsIgnoreCase(getElementFromSetupResponse(cXMLRequest, "cXML//BrowserFormPost/URL"))
			}
		}
		where:
		fields        | expectedFields
		"BASIC"       | "browseFormPostUrl,orderAsCXML"
		"DEFAULT"     | "browseFormPostUrl,orderAsCXML"
		"FULL"        | "browseFormPostUrl,orderAsCXML"
		""            | "browseFormPostUrl,orderAsCXML"
		"orderAsCXML" | "orderAsCXML"
	}

	def "When Punchout customer request to get requisition data without cart entries(discardCartEntries is true)"() {
		given: "a CXML Setup Request and get sid from response"
		File file = ResourceUtils.getFile("classpath:b2bpunchoutocctests/test/b2bpunchoutoccSetupRequest.xml")
		def cXMLRequest = file.text
		def sid = getSIDFromSetupResponse(cXMLRequest);

		when: "he request to get requisition data without cart entries(discardCartEntries = true)"
		String requisitionAPIPath = getBasePathWithSite() + "/punchout/sessions/" + sid + "/requisition"
		def responseForRequisition = restClient.get(
				path: requisitionAPIPath,
				query: [
						'discardCartEntries': true,
						"fields"            : fields
				],
				contentType: JSON,
				requestContentType: JSON)
		then: "A successful response should be returned"
		with(responseForRequisition) {
			responseForRequisition.status == SC_OK
			def actualFieldList = getActualFieldsFromResponseForSession(responseForRequisition.data)
			def expectedFieldList = Arrays.asList(expectedFields.split(","))
			actualFieldList.containsAll(expectedFieldList)
			if (responseForRequisition.responseData.browseFormPostUrl) {
				responseForRequisition.responseData.browseFormPostUrl.equalsIgnoreCase(getElementFromSetupResponse(cXMLRequest, "cXML//BrowserFormPost/URL"))
			}
			StringUtils.isNotBlank(responseForRequisition.responseData.orderAsCXML)
			getElementFromSetupResponse(new String(responseForRequisition.responseData.orderAsCXML.decodeBase64()), "cXML//PunchOutOrderMessage/PunchOutOrderMessageHeader/Total/Money").equals("0.0")
		}
		where:
		fields        | expectedFields
		"BASIC"       | "browseFormPostUrl,orderAsCXML"
		"DEFAULT"     | "browseFormPostUrl,orderAsCXML"
		"FULL"        | "browseFormPostUrl,orderAsCXML"
		""            | "browseFormPostUrl,orderAsCXML"
		"orderAsCXML" | "orderAsCXML"
	}

	def "When Punchout customer request to get requisition data but sid is invalid"() {
		given: "an invalid sid"
		def sid = "notvalidsid001";

		when: "he request to get requisition data"
		String requisitionAPIPath = getBasePathWithSite() + "/punchout/sessions/" + sid + "/requisition"
		def responseForRequisition = restClient.get(
				path: requisitionAPIPath,
				query: ['discardCartEntries': true],
				contentType: JSON,
				requestContentType: JSON)
		then: "a response with error message will obtained"
		with(responseForRequisition) {
			responseForRequisition.status == SC_NOT_FOUND
			responseForRequisition.responseData.errors.type.get(0).equalsIgnoreCase("NotFoundError")
		}
	}

	def getElementFromSetupResponse(cxml, regex) {
		final Document xmlDocument = builder.parse(new InputSource(new StringReader(cxml)));
		final NodeList nodeList = ((NodeList) xPath.compile(regex).evaluate(xmlDocument, XPathConstants.NODESET));
		return nodeList.item(0).getTextContent();
	}

	def getElementAttributeFromSetupResponse(cxml, regex, attribute) {
		final Document xmlDocument = builder.parse(new InputSource(new StringReader(cxml)));
		final NodeList nodeList = ((NodeList) xPath.compile(regex).evaluate(xmlDocument, XPathConstants.NODESET))
		return nodeList.item(0).getAttributes().getNamedItem(attribute).getTextContent()
	}

	def getActualFieldsFromResponseForSession(data) {
		def actualFields = []
		actualFields.addAll(data.keySet())
		if (data.token) {
			actualFields.addAll(data.token.keySet())
		}
		return actualFields
	}

	def getSIDFromSetupResponse(cXMLRequest) {
		def fullpath = getBasePathWithSite() + "/punchout/cxml/setup"
		def pr = restClient.getParser();
		pr.putAt(XML) { response ->
			String responseXml = EntityUtils.toString(response.entity)
			EntityUtils.consume(response.entity)
			return responseXml
		}
		def responseForSetup = restClient.post(
				path: fullpath,
				contentType: XML,
				body: cXMLRequest,
				headers: ["accept": XML],
				requestContentType: XML)
		def elementContent = getElementFromSetupResponse(responseForSetup.responseData, "cXML//PunchOutSetupResponse/StartPage/URL");
		return elementContent.substring(elementContent.indexOf("?") + 5);
	}

	def getPunchoutSessionFromSid(sid) {
		String SessionAPIPath = getBasePathWithSite() + "/punchout/sessions/" + sid
		def responseForSession = restClient.get(
				path: SessionAPIPath,
				contentType: JSON,
				requestContentType: JSON)
		return responseForSession.responseData
	}

	def addSpecificProductToCart(punchoutSession, qty, sid) {
		restClient.getHeaders().put('Authorization', ' Bearer ' + punchoutSession.token.accessToken)
		restClient.getHeaders().put('punchoutSid', sid)
		restClient.post(
				path: getBasePathWithSite() + '/users/current/carts/' + punchoutSession.cartId + '/entries/',
				body: [
						'product' : ['code': punchoutSession.selectedItem],
						'quantity': qty
				],
				contentType: JSON,
				requestContentType: JSON
		)
		restClient.getHeaders().remove('Authorization')
	}
}
