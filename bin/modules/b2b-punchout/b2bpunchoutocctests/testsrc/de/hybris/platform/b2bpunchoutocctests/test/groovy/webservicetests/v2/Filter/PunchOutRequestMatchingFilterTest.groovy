/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bpunchoutocctests.test.groovy.webservicetests.v2.Filter

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.XML
import static org.apache.http.HttpStatus.SC_NOT_FOUND
import static org.apache.http.HttpStatus.SC_OK

import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.commercewebservicestests.test.groovy.webservicetests.v2.spock.carts.AbstractCartTest
import org.apache.http.util.EntityUtils
import org.springframework.util.ResourceUtils
import org.w3c.dom.Document
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import spock.lang.Unroll

import javax.xml.XMLConstants
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPath
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

@ManualTest
@Unroll
class PunchOutRequestMatchingFilterTest extends AbstractCartTest {
	private static final String XERCES_FEATURE_PREFIX = "http://apache.org/xml/features/";
	private static final String LOAD_EXTERNAL_DTD_FEATURE = "nonvalidating/load-external-dtd";
	protected static final String LOAD_EXTERNAL_DTD = XERCES_FEATURE_PREFIX + LOAD_EXTERNAL_DTD_FEATURE;
	private static final XPath xPath = XPathFactory.newInstance().newXPath();
	private static final DocumentBuilder builder;
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

	def "given a call to #method #url, and this url allowed is #allowed"() {
		def sid = getSIDFromSetupResponse();
		def punchOutSession = getPunchoutSessionFromSid(sid);
		given: "Construct request parameters"
		restClient.getHeaders().put('punchoutSid', sid)
		restClient.getHeaders().put('Authorization', ' Bearer ' + punchOutSession.token.accessToken)
		def pathUrl = url.replace("{cartId}", punchOutSession.cartId);
		when: "a valid response is obtained by #method #url"
		def args = [path              : getBasePathWithSite() + pathUrl,
					contentType       : JSON,
					requestContentType: JSON]
		if (url.equals('/basesites')) {
			args = [path              : pathUrl,
					contentType       : JSON,
					requestContentType: JSON]
		}
		def response = doRequestByRestClient(method, args)
		restClient.getHeaders().remove('Authorization')
		then: "A successful response should be returned"
		with(response) {
			if (allowed && response.status != SC_OK && response.responseData != null) {
				!response.responseData.errors.message.get(0).equals("Path not found！")
			}
			if (!allowed) {
				response.status == SC_NOT_FOUND
				response.responseData.errors.message.get(0).equals("Path not found！")
			}
		}
		where:
		method   | url                                                | allowed
		'get'    | '/languages'                                       | true
		'get'    | '/currencies'                                      | true
		'get'    | '/titles'                                          | true
		'get'    | '/cardtypes'                                       | true
		'put'    | '/orgUsers/current/carts/{cartId}/entries/'        | true
		'get'    | '/users/current/carts/{cartId}/entries'            | true
		'post'   | '/users/current/carts/{cartId}/entries'            | true
		'get'    | '/categories/123/products'                         | true
		'get'    | '/orgProducts/123'                                 | true
		'get'    | '/orgUsers/current'                                | true
		'get'    | '/users/current/carts/{cartId}/entries'            | true
		'get'    | '/users/current/carts/{cartId}/entries/123'        | true
		'get'    | '/users/current/carts/{cartId}/entrygroups/123'    | true
		'get'    | '/users/current/carts/{cartId}'                    | true
		'get'    | '/catalogs'                                        | true
		'get'    | '/catalogs/123'                                    | true
		'get'    | '/catalogs/123/123'                                | true
		'get'    | '/catalogs/123/123/categories/123'                 | true
		'get'    | '/cities/123/districts'                            | true
		'get'    | '/regions/123/cities'                              | true
		'get'    | '/cms/components'                                  | true
		'get'    | '/cms/components/123'                              | true
		'get'    | '/countries'                                       | true
		'get'    | '/countries/123/regions'                           | true
		'get'    | '/users/current/futureStocks'                      | true
		'get'    | '/users/current/futureStocks/123'                  | true
		'get'    | '/cms/pages'                                       | true
		'get'    | '/cms/pages/123'                                   | true
		'get'    | '/cms/sitepages'                                   | true
		'get'    | '/products/123'                                    | true
		'get'    | '/products/123/references'                         | true
		'get'    | '/products/123/reviews'                            | true
		'get'    | '/products/123/stock'                              | true
		'head'   | '/products/123/stock'                              | true
		'get'    | '/products/123/stock/123'                          | true
		'get'    | '/products/expressupdate'                          | true
		'get'    | '/products/search'                                 | true
		'get'    | '/products/suggestions'                            | true
		'get'    | '/promotions'                                      | true
		'get'    | '/promotions/123'                                  | true
		'get'    | '/stores'                                          | true
		'head'   | '/stores'                                          | true
		'get'    | '/stores/123'                                      | true
		'get'    | '/stores/country/123'                              | true
		'get'    | '/stores/country/123/region/123'                   | true
		'get'    | '/stores/storescounts'                             | true
		'get'    | '/users/current'                                   | true
		'get'    | '/users/current/customercoupons'                   | true
		'post'   | '/users/current/customercoupons/123/claim'         | true
		'get'    | '/users/current/carts/{cartId}/promotions'         | true
		'post'   | '/users/current/carts/{cartId}/promotions'         | true
		'get'    | '/users/current/carts/{cartId}/promotions/123'     | true
		'delete' | '/users/current/carts/{cartId}/promotions/123'     | true
		'get'    | '/users/current/carts/{cartId}/vouchers'           | false
		'post'   | '/users/current/carts/{cartId}/vouchers'           | false
		'get'    | '/users/current/carts/{cartId}/vouchers/123'       | false
		'get'    | '/users/current/address'                           | false
		'get'    | '/costcenters'                                     | false
		'get'    | '/orgUsers/current/orders'                         | false
		'get'    | '/users/current/budgets'                           | false
		'post'   | '/users/current/carts/{cartId}/bundles'            | false
		'post'   | '/users/current/carts/{cartId}/addresses/delivery' | false
		'get'    | '/users/current/carts/{cartId}/deliverymodes'      | false

	}

	def "given a call to #method #url when #tokenFlag.value, #cartIdFlag.value, #sidFlag.value"() {
		given: "Construct request parameters"
		def sid = getSIDFromSetupResponse()
		def punchOutSession = getPunchoutSessionFromSid(sid);
		if (sidFlag == PunchoutFlagEnum.validSid) {
			restClient.getHeaders().put('punchoutSid', sid)
		} else if (sidFlag == PunchoutFlagEnum.invalidSid) {
			restClient.getHeaders().put('punchoutSid', getSIDFromSetupResponse())
		}

		String pathUrl = '';
		if (cartIdFlag == PunchoutFlagEnum.cartIdNotBelonged) {
			def tempSid = getSIDFromSetupResponse();
			def cartIdForNotBelong = getPunchoutSessionFromSid(tempSid);
			pathUrl = url.replace('{cartId}', cartIdForNotBelong.cartId);
		} else if (cartIdFlag == PunchoutFlagEnum.cartIdBelonged) {
			pathUrl = url.replace("{cartId}", punchOutSession.cartId);
		}
		if (tokenFlag == PunchoutFlagEnum.sidToken) {
			restClient.getHeaders().put('Authorization', ' Bearer ' + punchOutSession.token.accessToken)
		} else if (tokenFlag == PunchoutFlagEnum.oauthToken) {
			def oauth2Token = getOAuth2TokenUsingPassword(restClient, clientId, "", userName, password, false);
			restClient.getHeaders().put('Authorization', ' Bearer ' + oauth2Token.access_token)
		}
		when: "a valid response is obtained by #method #url"
		def response = restClient.get(
				path: getBasePathWithSite() + '' + pathUrl,
				query: params,
				contentType: JSON,
				requestContentType: JSON
		)
		restClient.getHeaders().remove('Authorization')
		then: "A successful response should be returned"
		with(response) {
			response.status == Integer.valueOf(status.code)
			if (status != PunchoutFlagEnum.Success) {
				response.responseData.errors.type.get(0).equals(status.value)
			}
		}
		where:
		module         | method | url                                     | params                | body | status                             | tokenFlag                   | cartIdFlag                         | sidFlag
		'Cart Entries' | 'get'  | '/users/current/carts/{cartId}/entries' | ["fields": "DEFAULT"] | []   | PunchoutFlagEnum.Success           | PunchoutFlagEnum.sidToken   | PunchoutFlagEnum.cartIdBelonged    | PunchoutFlagEnum.validSid
		'Cart Entries' | 'get'  | '/users/current/carts/{cartId}/entries' | ["fields": "DEFAULT"] | []   | PunchoutFlagEnum.Success           | PunchoutFlagEnum.oauthToken | PunchoutFlagEnum.cartIdNotBelonged | PunchoutFlagEnum.sidNull
		'Cart Entries' | 'get'  | '/users/current/carts/{cartId}/entries' | ["fields": "DEFAULT"] | []   | PunchoutFlagEnum.Success           | PunchoutFlagEnum.oauthToken | PunchoutFlagEnum.cartIdNotBelonged | PunchoutFlagEnum.validSid
		'Cart Entries' | 'get'  | '/users/current/carts/{cartId}/entries' | ["fields": "DEFAULT"] | []   | PunchoutFlagEnum.HeaderException   | PunchoutFlagEnum.sidToken   | PunchoutFlagEnum.cartIdBelonged    | PunchoutFlagEnum.sidNull
		'Cart Entries' | 'get'  | '/users/current/carts/{cartId}/entries' | ["fields": "DEFAULT"] | []   | PunchoutFlagEnum.NotFoundException | PunchoutFlagEnum.sidToken   | PunchoutFlagEnum.cartIdBelonged    | PunchoutFlagEnum.invalidSid
		'Cart Entries' | 'get'  | '/users/current/carts/{cartId}/entries' | ["fields": "DEFAULT"] | []   | PunchoutFlagEnum.NotFoundException | PunchoutFlagEnum.sidToken   | PunchoutFlagEnum.cartIdNotBelonged | PunchoutFlagEnum.validSid
		'Address'      | 'get'  | '/users/current/addresses'              | ["fields": "DEFAULT"] | []   | PunchoutFlagEnum.NotFoundException | PunchoutFlagEnum.sidToken   | PunchoutFlagEnum.cartIdBelonged    | PunchoutFlagEnum.validSid
	}

	def getSIDFromSetupResponse() {
		File file = ResourceUtils.getFile("classpath:b2bpunchoutocctests/test/b2bpunchoutoccSetupRequestForSessionApi.xml")
		def cXMLRequest = file.text
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

	def doRequestByRestClient(method, args) {
		def response = null;
		switch (method) {
			case "get":
				response = restClient.get(args)
				break;
			case "post":
				response = restClient.post(args)
				break;
			case "delete":
				response = restClient.delete(args)
				break;
			case "head":
				response = restClient.head(args)
				break;
			case "put":
				response = restClient.put(args)
				break;
		}
		return response;
	}

	enum PunchoutFlagEnum {
		oauthToken("oauthToken", "a valid token(fetched from via OAuth endpoint) provided"),
		sidToken("sidToken", "a valid token(exchanged from sid) provided"),

		validSid("validSid", "header punchoutSid provided with a valid sid as its value"),
		sidNull("sidNull", "header punchoutSid not provided"),
		invalidSid("invalidSid", "header punchoutSid provied with invalid sid value"),

		cartIdBelonged("cartIdBelonged", "cartId provied in path belongs to current punchout session"),
		cartIdNotBelonged("cartIdNotBelonged", "cartId do not belongs to current punchout session"),

		HeaderException("400", "BadPunchOutRequestException"),
		NotFoundException("404", "NotFoundError"),
		Success("200", "success")


		private final String value;
		private final String code;

		PunchoutFlagEnum(final String code, final String value) {
			this.value = value
			this.code = code
		}

		String getCode() {
			return code
		}

		String getValue() { value }
	}
}
