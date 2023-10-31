/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.forgottenpasswords

import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.AbstractSpockFlowTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.users.AbstractUserTest
import groovyx.net.http.HttpResponseDecorator
import spock.lang.Unroll

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC
import static groovyx.net.http.ContentType.XML
import static org.apache.http.HttpStatus.SC_ACCEPTED
import static org.apache.http.HttpStatus.SC_ACCEPTED
import static org.apache.http.HttpStatus.SC_CREATED
import static org.apache.http.HttpStatus.SC_NOT_FOUND
import static org.apache.http.HttpStatus.SC_OK

@ManualTest
@Unroll
class ForgottenPasswordsTest extends AbstractUserTest
{
	static final SECURE_BASE_SITE = "/wsSecureTest"
	static final String CUSTOMER_ID = 'customer@test.com'

	def "Anonymous user could generate a token to restore the forgotten password: #format"() {

		given: "a registered user with trusted client"
		authorizeTrustedClient(restClient)

		when:
		HttpResponseDecorator response = restClient.post(
				path: getBasePathWithSite() + '/forgottenpasswordtokens',
				query: ['userId': CUSTOMER_ID],
				contentType: format,
				requestContentType: URLENC
		)

		then:
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
			status == SC_ACCEPTED
		}

		where:
		format << [JSON, XML]
	}

	def "Anonymous user could generate a token to restore the forgotten password when requiresAuthentication true: #format"() {
		given: "a registered user with trusted client"
		authorizeTrustedClient(restClient)

		when:
		HttpResponseDecorator response = restClient.post(
				path: getBasePath() + SECURE_BASE_SITE +'/forgottenpasswordtokens',
				query: ['userId': CUSTOMER_ID],
				contentType: format,
				requestContentType: URLENC
		)

		then:
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
			status == SC_ACCEPTED
		}

		where:
		format << [JSON, XML]
	}

	def "Anonymous user could reset password with token when requiresAuthentication true: #format"() {
		given: "token of a registered user with trusted client"
		authorizeClient(restClient)
		def customer1 = registerCustomer(restClient, format)
		def tokenData = getOAuth2TokenUsingPassword(restClient, getClientId(), getClientSecret(), customer1.id, customer1.password,)

		when:
		HttpResponseDecorator response = restClient.post(
				path: getBasePath() + SECURE_BASE_SITE + '/resetpassword',
				body: [
						'newPassword': 'PAss4321!',
						'token': tokenData.access_token
				],
				contentType: format,
				requestContentType: format
		)

		then:
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
			status != SC_NOT_FOUND
		}

		where:
		format << [JSON]
	}
}
