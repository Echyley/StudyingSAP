 /*
  * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
  */
 package de.hybris.platform.b2bocctests.test.groovy.webservicetests.v2.controllers

 import de.hybris.bootstrap.annotations.ManualTest
 import de.hybris.platform.commercewebservicestests.test.groovy.webservicetests.v2.spock.AbstractSpockFlowTest
 import groovyx.net.http.HttpResponseDecorator
 import spock.lang.Unroll

 import static groovyx.net.http.ContentType.JSON
 import static org.apache.http.HttpStatus.SC_OK

 @ManualTest
 @Unroll
 class BaseSiteSecureControllerTest extends AbstractSpockFlowTest {
 	static final Boolean REGISTRATION_ENABLED = true
 	static final Boolean REQUIRES_AUTHENTICATION = true

 	def "As a customer, I should get data about if the site supports registration and early login when specifying FULL as fields"() {
 		given: "A customer who wants to know if the site supports registration and early login"
 		String path = getBasePath()
 		when: "specifying FULL as fields"
 		HttpResponseDecorator response = restClient.get(
 				path: path + "/basesites",
 				query: [
 						"fields": "FULL"
 				],
 				contentType: JSON,
 				requestContentType: JSON)

 		then: "registrationEnabled and requiresAuthentication are returned in response"
 		with(response) {
 			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
 				println(data)
 			}
 			status == SC_OK
 			data.baseSites[0].registrationEnabled == REGISTRATION_ENABLED
 			data.baseSites[0].requiresAuthentication == false
			data.baseSites[2].registrationEnabled == REGISTRATION_ENABLED
			data.baseSites[2].requiresAuthentication == REQUIRES_AUTHENTICATION
 		}
 	}

 	def "As a customer, I should not get data about if the site supports registration and early login when specifying #scenario as fields"() {
 		given: "A customer who wants to know if the site supports registration and early login"
 		String path = getBasePath()
 		when: "specifying #scenario as fields"
 		HttpResponseDecorator response = restClient.get(
 				path: path + "/basesites",
 				query: [
 						"fields": requestedFields
 				],
 				contentType: JSON,
 				requestContentType: JSON)

 		then: "registrationEnabled and requiresAuthentication are not returned in response"
 		with(response) {
 			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
 				println(data)
 			}
 			status == SC_OK
 			data.baseSites[0].registrationEnabled == null
 			data.baseSites[0].requiresAuthentication == null
 		}
 		where:
 		scenario  | requestedFields
 		"BASIC"   | "BASIC"
 		"DEFAULT" | "DEFAULT"
 	}
 }
