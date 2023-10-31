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
import static org.apache.http.HttpStatus.SC_CREATED
import static org.apache.http.HttpStatus.SC_OK

import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.testframework.HybrisSpockRunner

import org.junit.Test
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory

import groovyx.net.http.HttpResponseDecorator



@ManualTest
@RunWith(HybrisSpockRunner.class)
class QuoteWsIntegrationTest extends BaseSpockTest {

	org.slf4j.Logger LOG = LoggerFactory.getLogger(QuoteWsIntegrationTest.class)
	protected static final PRODUCT_KEY = 'CONF_HOME_THEATER_ML'
	static final CUSTOMER = ["id": USERNAME, "password": PASSWORD]	 
	
	@Test
	def "Existing customer views configuration of a quote entry"() {
		
		if(isExtensionInSetup("sapproductconfigservicesssc")) {
			LOG.info("Session lifecycle implemented by sapproductconfigservicesssc is not fully compatible with OCC, hence skipping QuoteWsIntegrationTest");
			return;
		}
	 
		def quoteCode = "7777777776"
		authorizeCustomer(CUSTOMER)
		
		//We check the configuration for a quote that has been deployed via impex. If we wanted to create the configuration
		//via OCC, we would need extension b2bocc as dependency which we want to avoid here. 
		//The attached configuration is therefore not in memory, meaning it will be re-initialized by the mock configurator, 
		//but we can check for the groups and characteristics of the default configuration of CONF_HOME_THEATER_ML

		when: "Customer wants to retrieve the configuration details of the quote"
		def responseFromReadConfig = restClient.get(
				path: getBasePathWithSite() + '/users/' + USERNAME + '/quotes/' + quoteCode + '/entries/0'+SLASH_CONFIGURATOR_TYPE_OCC_SLASH + 'configurationOverview',
				query: ['fields': FIELD_SET_LEVEL_BASIC],
				contentType: JSON,
				requestContentType: URLENC
				)
		then: "they retrieve the configuration details and see the configuration that we attached, including 2 groups that are part of the default configuration"
		with(responseFromReadConfig) {
			status == SC_OK
			if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
			data.id == '34e5224f-00c9-43f5-8513-4bbaae393794@CONF_HOME_THEATER_ML@'
			data.productCode == PRODUCT_KEY
			data.groups.size() == 2
			data.groups[0].groupDescription == "Video System"
			data.groups[0].id == "CONF_HT_VIDEO_SYSTEM"
			data.groups[0].characteristicValues.size() == 1
			data.groups[0].characteristicValues[0].characteristic == "Projector Type"
			data.groups[0].characteristicValues[0].characteristicId == "PROJECTOR_TYPE"
			data.groups[0].characteristicValues[0].valueId == "PROJECTOR_NONE"
		}
	}
}