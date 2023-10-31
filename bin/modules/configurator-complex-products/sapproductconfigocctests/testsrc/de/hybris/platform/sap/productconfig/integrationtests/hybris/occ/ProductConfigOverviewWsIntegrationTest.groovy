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
package de.hybris.platform.sap.productconfig.integrationtests.hybris.occ

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.XML
import static org.apache.http.HttpStatus.SC_OK

import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.testframework.HybrisSpockRunner

import org.junit.Test
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory

import groovy.json.JsonSlurper
import groovyx.net.http.HttpResponseDecorator


@ManualTest
@RunWith(HybrisSpockRunner.class)
class ProductConfigOverviewWsIntegrationTest extends BaseSpockTest {

	def org.slf4j.Logger LOG = LoggerFactory.getLogger(ProductConfigOverviewWsIntegrationTest.class)

	def PRODUCT_CODE = "CONF_HOME_THEATER_ML"

	@Test
	def "Get a configurationOverview by the config id"() {

		when: "creates a new configuration"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/products/YSAP_SIMPLE_POC/configurators' + SLASH_CONFIGURATOR_TYPE_OCC,
				contentType: format
				)

		then: "gets a new default configuration with"
		with(response) {
			status == SC_OK
			data.configId.isEmpty() == false
		}

		when: "Afterwards get the created configuration by the config ID"
		String configId = response.data.configId

		response = restClient.get(
				path: getBasePathWithSite() + SLASH_CONFIGURATOR_TYPE_OCC_SLASH+ configId +'/configurationOverview'  ,
				contentType: format
				)

		then: "gets the newly create configuration by config ID with all fields mapped"
		with(response) {
			status == SC_OK
			data.id == configId
			data.pricingEnabled == true
			data.groups.size() == 1
			data.groups[0].id == "_GEN"
			data.groups[0].groupDescription == "[_GEN]"
			data.groups[0].characteristicValues.size() == 1
			data.groups[0].characteristicValues[0].characteristic == "Simple Flag: Hide options"
			data.groups[0].characteristicValues[0].characteristicId == "YSAP_POC_SIMPLE_FLAG"
			data.groups[0].characteristicValues[0].value == "Hide"
			data.groups[0].characteristicValues[0].valueId == "X"
			data.pricing.basePrice.priceType == "BUY"
		}

		where:
		format << [XML, JSON]
	}

	@Test
	def "Get a configurationOverview with issues"() {

		when: "creates a new configuration"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/products/CPQ_HOME_THEATER/configurators' + SLASH_CONFIGURATOR_TYPE_OCC,
				contentType: format,
				)

		then: "gets a new default configuration"
		with(response) {
			status == SC_OK
			data.configId.isEmpty() == false
		}

		when: "Afterwards get the created configuration by the config ID"
		String configId = response.data.configId

		response = restClient.get(
				path: getBasePathWithSite() + SLASH_CONFIGURATOR_TYPE_OCC_SLASH+ configId +'/configurationOverview'  ,
				contentType: format
				)

		then: "gets the newly create configuration by config ID with all fields mapped"
		with(response) {
			status == SC_OK
			data.id == configId
			data.totalNumberOfIssues == 1
			data.numberOfIncompleteCharacteristics == 1
			data.numberOfConflicts == 0
		}

		where:
		format << [XML, JSON]
	}

	@Test
	def "Get a filtered configurationOverview where the first group is not selected and only the second and third group will be shown"() {

		String configId = getConfigIdForPreparedOverviewTests()

		//set a groupfilter, where the first group is not selected, but the next two groups are selected
		def jsonSlurper = new JsonSlurper()
		def jsonBodyWithGroupFilter = jsonSlurper.parseText('{ "groupFilterList": [{"key":"1","selected": false},{"key": "2","selected": true},{"key": "CPQ_BLU_RAY_PLAYER","selected": true}] }')


		when: "patch the configurationOverview for configId with filter informations"
		HttpResponseDecorator responseAfterUpdate = restClient.patch(
				path: getBasePathWithSite() + SLASH_CONFIGURATOR_TYPE_OCC_SLASH + configId + SLASH_CONFIG_OVERVIEW,
				body: jsonBodyWithGroupFilter,
				contentType: format,
				)

		then: "get the new configuration overview with the applied filters"
		with(responseAfterUpdate){
			status == SC_OK
			// check if the first returned group matches the first filtered group. also check if the size matches the amount of groups where " selected" is true).
			data.groups[0].groupDescription == "Visual entertainment"
			data.groups[1].groupDescription == "Blu-Ray Player"
			data.groups.size() == 2
		}
		where:
		format << [JSON]
	}

	@Test
	def "Get a filtered configurationOverview where no group is selected and all groups should be returned"() {

		String configId = getConfigIdForPreparedOverviewTests()

		//set a groupfilter without any entry.
		def jsonSlurper = new JsonSlurper()
		def jsonBodyWithGroupFilter = jsonSlurper.parseText('{ "groupFilterList": [] }')

		when: "patch the configurationOverviw for configId with filter informations"
		HttpResponseDecorator responseAfterUpdate = restClient.patch(
				path: getBasePathWithSite() + SLASH_CONFIGURATOR_TYPE_OCC_SLASH + configId + SLASH_CONFIG_OVERVIEW,
				body: jsonBodyWithGroupFilter,
				contentType: format,
				)

		then: "get the new configuration overview with the applied filters"
		with(responseAfterUpdate){
			status == SC_OK
			// since the groupFilterList contains no entries, all entries will be returned
			data.groups[0].groupDescription =="Powerful audio"
			data.groups[1].groupDescription =="Visual entertainment"
			data.groups[2].groupDescription =="Blu-Ray Player"
			data.groups.size() == 3
		}
		where:
		format << [JSON]
	}

	@Test
	def "Get a filtered configurationOverview for cstic filter"() {

		String configId = getConfigIdForPreparedOverviewTests()

		//set a cstic filter with:
		//visible 			= true
		//user input 		= true
		//price relevant	= false
		def jsonSlurper = new JsonSlurper()
		def jsonBodyWithGroupFilter = jsonSlurper.parseText('{ "appliedCsticFilter": [{"key":"VISIBLE","selected": true},{"key": "USER_INPUT","selected": true},{"key": "PRICE_RELEVANT","selected": false}] }')

		when: "patch the configurationOverviw for configId with filter informations"
		HttpResponseDecorator responseAfterUpdate = restClient.patch(
				path: getBasePathWithSite() + SLASH_CONFIGURATOR_TYPE_OCC_SLASH + configId + SLASH_CONFIG_OVERVIEW,
				body: jsonBodyWithGroupFilter,
				contentType: format,
				)

		then: "get the new configuration overview with the applied cstic filters"
		with(responseAfterUpdate){
			status == SC_OK
			// since the csticfilter contains "USER_INPUT" the "Blu-Ray Player" (which is not a user selected group) should not be shown anymore and therefore only 2 groups will be returned!
			data.groups[0].groupDescription == "Powerful audio"
			data.groups[1].groupDescription == "Visual entertainment"
			data.groups.size() == 2
		}
		where:
		format << [JSON]
	}

	protected String getConfigIdForPreparedOverviewTests() {
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/products/CPQ_HOME_THEATER/configurators' + SLASH_CONFIGURATOR_TYPE_OCC,
				contentType: JSON,
				query: ['provideAllAttributes': true]
				)
		String configId = response.data.configId

		with(response) {
			status == SC_OK
			data.configId.isEmpty() == false
		}

		//Group 0 = "Powerful audio"
		//Group 1 = "Visual entertainment"
		//Group 2 = "Blu-Ray Player"
		def patchBodyForConfiguration = response.data
		patchBodyForConfiguration.groups[0].attributes[0].value = "STEREO"					 //Set soundsystem to Stereo
		patchBodyForConfiguration.groups[0].attributes[1].domainValues[0].selected = true //Subwoofer checkbox
		patchBodyForConfiguration.groups[0].attributes[2].value = "0_120"						 //AMP power per channel
		patchBodyForConfiguration.groups[1].attributes[0].domainValues[0].selected = true //add TV checkbox
		patchBodyForConfiguration.groups[1].attributes[1].domainValues[0].selected = true //add BlueRay Player
		patchBodyForConfiguration.groups[1].attributes[2].domainValues[0].selected = true //Apple TV
		patchBodyForConfiguration.groups[1].attributes[2].domainValues[1].selected = true //Amazon Fire TV
		patchBodyForConfiguration.groups[1].attributes[2].domainValues[2].selected = true //Google Chrome cast
		patchBodyForConfiguration.groups[1].attributes[2].domainValues[3].selected = true //Amazon Fire TV
		patchBodyForConfiguration.groups[1].attributes[2].domainValues[4].selected = true //Xbox One
		patchBodyForConfiguration.groups[1].attributes[2].domainValues[5].selected = true //Nintendo Wii U

		response = restClient.patch(
				path:  getBasePathWithSite() + SLASH_CONFIGURATOR_TYPE_OCC_SLASH + configId ,
				body: patchBodyForConfiguration,
				contentType: JSON
				)

		with(response) {
			status == SC_OK
		}

		response = restClient.get(
				path: getBasePathWithSite() + SLASH_CONFIGURATOR_TYPE_OCC_SLASH + configId + SLASH_CONFIG_OVERVIEW,
				contentType : JSON,
				query : ['fields' : FIELD_SET_LEVEL_BASIC],
				requestContentType: JSON
				)

		with(response) {
			status == SC_OK
			data.id == configId
			data.groups[0].groupDescription =="Powerful audio"
			data.groups[1].groupDescription =="Visual entertainment"
			data.groups[2].groupDescription =="Blu-Ray Player"
			data.groups.size() == 3
		}
		return configId;
	}

	@Test
	def "Check value price on configurationOverview"() {

		when: "client requests a new configuration"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/products/'+PRODUCT_CODE+'/configurators'+ SLASH_CONFIGURATOR_TYPE_OCC,
				contentType: format
				)

		then: "it gets a new default configuration"
		LOG.info("RESPONSE: "+response.data.toString())
		with(response) {
			status == SC_OK
			data.configId.isEmpty() == false
			data.consistent == true
			data.complete == true
			data.rootProduct == PRODUCT_CODE
		}

		when: "afterwards change the group"
		String configId = response.data.configId

		HttpResponseDecorator response2 = restClient.get(
				path: getBasePathWithSite() + SLASH_CONFIGURATOR_TYPE_OCC_SLASH + configId  ,
				contentType: format,
				query:['groupId': '3-CONF_HT_AUDIO_SYSTEM@Front_Speakers']
				)

		then: "group is changed"
		LOG.info("Response after changing group: "+response2.data.toString())

		when: "configuration is changed afterwards"
		def putBody2 = response2.data
		putBody2.groups[2].subGroups[0].attributes[1].value = "COLUMN_SPEAKER_1250"

		HttpResponseDecorator responseAfterUpdate2 = restClient.patch(
				path: getBasePathWithSite() + SLASH_CONFIGURATOR_TYPE_OCC_SLASH + response.data.configId,
				body : putBody2,
				contentType: format
				)

		then: "change is accepted"
		LOG.info("RESPONSE: "+responseAfterUpdate2.data.toString())
		with(responseAfterUpdate2) {
			status == SC_OK
		}

		when: "afterwards get the overview data by config ID"
		HttpResponseDecorator overviewResponse = restClient.get(
				path: getBasePathWithSite() + SLASH_CONFIGURATOR_TYPE_OCC_SLASH+ configId +'/configurationOverview'  ,
				contentType: format
				)

		then: "check the price information"
		LOG.info("RESPONSE: " + overviewResponse.data.toString())
		with(overviewResponse) {
			status == SC_OK
			data.id == configId
			data.groups.size() == 2
			data.groups[1].id == "CONF_HT_AUDIO_SYSTEM"
			data.groups[1].characteristicValues.size() == 2
			data.groups[1].characteristicValues[1].value == "Sony SA-VS700ED"
			data.groups[1].characteristicValues[1].valueId == "COLUMN_SPEAKER_1250"
			data.groups[1].characteristicValues[1].price.value == 70
			data.groups[1].characteristicValues[1].price.currencyIso == "USD"
			data.groups[1].characteristicValues[1].price.formattedValue == "\$70.00"
			data.groups[1].characteristicValues[1].price.priceType == "BUY"
		}

		where:
		format << [JSON]
	}
}
