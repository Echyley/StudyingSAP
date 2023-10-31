/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.cpq.productconfig.occ.integrationtests.mock

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.XML
import static org.apache.http.HttpStatus.SC_OK

import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.cpq.productconfig.services.strategies.impl.mock.MockAuthorizationStrategy
import de.hybris.platform.testframework.HybrisSpockRunner

import org.junit.Test
import org.junit.runner.RunWith

import groovyx.net.http.HttpResponseDecorator

@ManualTest
@RunWith(HybrisSpockRunner.class)
class CpqAccessControlWsIntegrationTest extends BaseSpockTest {

	@Test
	def "Get cpq configuration engine access data"() {

		given: "a registered and logged in B2B customer"
		authorizeCustomer(B2B_CUSTOMER)

		when: "reading client credentials for CPQ engine"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/users/' + B2B_USERNAME + '/access/cpqconfigurator',
				contentType: format
				)

		then: "consumer gets product details with configuration type"
		with(response) {
			status == SC_OK;
			data.endpoint == MockAuthorizationStrategy.MOCK_ENDPOINT_URL
			data.accessToken.toString().startsWith('MOCK_TOKEN|ownerId=TESTUNIT') == true
			data.accessTokenExpirationTime != null
		}

		where:
		format << [XML, JSON]
	}
}
