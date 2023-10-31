/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bpunchoutocctests.test.groovy.webservicetests.v2.controllers;

import static groovyx.net.http.ContentType.XML
import static org.apache.http.HttpStatus.SC_BAD_REQUEST
import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR
import static org.apache.http.HttpStatus.SC_OK
import static org.apache.http.HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE

import java.util.regex.Pattern

import org.apache.http.util.EntityUtils
import org.springframework.util.ResourceUtils

import de.hybris.bootstrap.annotations.ManualTest;
import de.hybris.platform.commercewebservicestests.test.groovy.webservicetests.v2.spock.AbstractSpockFlowTest;
import spock.lang.Unroll;

@ManualTest
@Unroll
public class B2BPunchoutProfileTest extends AbstractSpockFlowTest {

	def "When Ariba sends a request asking for Punchout Profile transaction "() {
		given: "a CXML Profile Request"
		File file = ResourceUtils.getFile("classpath:b2bpunchoutocctests/test/b2bpunchoutoccProfileRequest.xml")
		def  cXMLRequest = file.text
		
		when: "a valid Http POST request is made"
		def fullpath = getBasePathWithSite() +  "/punchout/cxml/profile"
		def pr = restClient.getParser();
		Pattern myRegex = ~/ProfileResponse/
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

		
		then: "a valid response is obtained"
		with (response){
			response.status == SC_OK
			myRegex.matcher(response.data)
			response.data.contains("<Transaction requestName=\"PunchOutSetUpRequest\">")
			response.data.contains("<Transaction requestName=\"OrderRequest\">")
			response.data.contains("</ProfileResponse>")
			response.data.contains("/occ/v2/")
		}
	}

	def "When Ariba sends a request asking for Punchout Profile transaction with Content-Type #contentType"() {
		given: "a CXML Profile Request"
		File file = ResourceUtils.getFile("classpath:b2bpunchoutocctests/test/b2bpunchoutoccProfileRequest.xml")
		def  cXMLRequest = file.text

		when: "a valid Http POST request is made"
		def fullpath = getBasePathWithSite() +  "/punchout/cxml/profile"
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
		with (response){
			response.status == statusCode
		}

		where: "possible values header Content-Type are:"
		contentType        | statusCode
		"application/xml"  | SC_OK
		"text/xml"         | SC_OK
		"text/plain"       | SC_UNSUPPORTED_MEDIA_TYPE
	}

	def "When Ariba sends a request asking for Punchout Profile transaction with a malformed XML"() {
		given: "a CXML Profile Request"
		File file = ResourceUtils.getFile("classpath:b2bpunchoutocctests/test/b2bpunchoutoccMalformedRequest.xml")
		def  cXMLRequest = file.text

		when: "a Http POST request is made with malformed cxml"
		def fullpath = getBasePathWithSite() +  "/punchout/cxml/profile"
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
		with (response){
			response.status == SC_INTERNAL_SERVER_ERROR
			response.data.contains("UnmarshalException")
			response.data.contains("Content is not allowed in prolog.")
		}
	}

	def "When Ariba sends a request asking for Punchout Profile transaction with wrong from node"() {
		given: "a CXML Profile Request"
		File file = ResourceUtils.getFile("classpath:b2bpunchoutocctests/test/profileRequestWithIncorrectFrom.xml")
		def  cXMLRequest = file.text

		when: "a Http POST request is made with malformed cxml"
		def fullpath = getBasePathWithSite() +  "/punchout/cxml/profile"
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


		then: "return authentication failed error message"
		with (response){
			response.status == SC_OK
			response.data.contains("<Status code=\"401\" text=\"Authentication failed, please check if the credential for originator of the cXML request is mapped to an hybris user.\"/>")
		}
	}

	def "When Ariba sends a request asking for Punchout Profile transaction with lower case value of network id "() {
		given: "a CXML Profile Request"
		File file = ResourceUtils.getFile("classpath:b2bpunchoutocctests/test/profileRequestNetworkIDCaseInsensitive.xml")
		def  cXMLRequest = file.text

		when: "a valid Http POST request is made"
		def fullpath = getBasePathWithSite() +  "/punchout/cxml/profile"
		def pr = restClient.getParser();
		Pattern myRegex = ~/ProfileResponse/
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


		then: "a valid response is obtained"
		with (response){
			response.status == SC_OK
			myRegex.matcher(response.data)
			response.data.contains("<Transaction requestName=\"PunchOutSetUpRequest\">")
			response.data.contains("<Transaction requestName=\"OrderRequest\">")
			response.data.contains("</ProfileResponse>")
			response.data.contains("/occ/v2/")
		}
	}
}
