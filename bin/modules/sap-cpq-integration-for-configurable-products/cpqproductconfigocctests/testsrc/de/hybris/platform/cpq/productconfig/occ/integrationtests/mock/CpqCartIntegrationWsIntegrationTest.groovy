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
import static groovyx.net.http.ContentType.URLENC
import static groovyx.net.http.ContentType.XML
import static org.apache.http.HttpStatus.SC_OK

import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.testframework.HybrisSpockRunner

import org.junit.Test
import org.junit.runner.RunWith

@ManualTest
@RunWith(HybrisSpockRunner.class)
class CpqCartIntegrationWsIntegrationTest extends BaseSpockTest {

	@Test
	def "Create a new empty cart"() {

		given: "a registered and logged in B2B customer"
		authorizeCustomer(B2B_CUSTOMER)

		when: "consumer creates a new cart for an logged in user"
		def cart = createEmptyCart(format, B2B_USERNAME)

		then: "the new cart has been created sucessfully and is empty"
		if(isNotEmpty(cart) && isNotEmpty(cart.errors))
			println(cart)
		!isNotEmpty(cart.entries)
		isNotEmpty(cart.guid)
		isNotEmpty(cart.code)

		where:
		format <<[XML, JSON]
	}

	@Test
	def "Create a new cart entry"() {

		given: "a registered and logged in B2B customer"
		authorizeCustomer(B2B_CUSTOMER)
		def cart = createEmptyCart(format, B2B_USERNAME)
		def configId = generateTestConfigId(P_CODE_CONF_LAPTOP)

		when: "consumer creates a new cart entry for a given config id"
		def response = addConfigurationToCart(cart, P_CODE_CONF_LAPTOP, configId, format, B2B_USERNAME)

		then: "the item was added sucessfully"
		isNotEmpty(response.entry)
		isNotEmpty(response.entry.product)
		response.quantityAdded == 1
		response.statusCode == "success"
		response.entry.product.code == "CONF_LAPTOP"


		where:
		format <<[XML, JSON]
	}

	@Test
	def "Validate with configuration error"() {

		given: "a registered and logged in B2B customer"
		authorizeCustomer(B2B_CUSTOMER)
		def cart = createEmptyCart(format, B2B_USERNAME)
		def configId = generateTestConfigId(P_CODE_CONF_LAPTOP)

		when: "consumer creates a new cart entry for a given config id"
		def response = addConfigurationToCart(cart, P_CODE_CONF_LAPTOP, configId, format, B2B_USERNAME)

		then: "the item was added sucessfully"
		isNotEmpty(response.entry)
		isNotEmpty(response.entry.product)
		response.quantityAdded == 1
		response.statusCode == "success"
		response.entry.product.code == "CONF_LAPTOP"

		when: "is validated"
		def validateResponse = restClient.post(
				path: getBasePathWithSite() + '/users/' + B2B_USERNAME + '/carts/' + cart.code + '/validate',
				query: ["fields": FIELD_SET_LEVEL_FULL],
				contentType: format,
				requestContentType: URLENC
				)

		then: "with configuration error"
		with(validateResponse) {
			println(data)
			status == SC_OK
			isNotEmpty(data.cartModifications)
			data.cartModifications.size() == 1
			data.cartModifications[0].statusCode == 'reviewConfiguration'
			data.cartModifications[0].entry.product.code == "CONF_LAPTOP"
		}


		where:
		format <<[XML, JSON]
	}
	@Test
	def "Validate without configuration error"() {

		given: "a registered and logged in B2B customer"
		authorizeCustomer(B2B_CUSTOMER)
		def cart = createEmptyCart(format, B2B_USERNAME)
		def configId = generateTestConfigId(P_CODE_CONF_LAPTOP_COMPLETE)

		when: "consumer creates a new cart entry for a given config id"
		def response = addConfigurationToCart(cart, P_CODE_CONF_LAPTOP_COMPLETE, configId, format, B2B_USERNAME)

		then: "the item was added sucessfully"
		isNotEmpty(response.entry)
		isNotEmpty(response.entry.product)
		response.quantityAdded == 1
		response.statusCode == "success"
		response.entry.product.code == "CONF_LAPTOP"

		when: "is validated"
		def validateResponse = restClient.post(
				path: getBasePathWithSite() + '/users/' + B2B_USERNAME + '/carts/' + cart.code + '/validate',
				query: ["fields": FIELD_SET_LEVEL_FULL],
				contentType: format,
				requestContentType: URLENC
				)

		then: "successful without configuration error"
		with(validateResponse) {
			println(data)
			status == SC_OK
			isEmpty(data.cartModifications)
		}


		where:
		format <<[XML, JSON]
	}

	@Test
	def "Update an existing cart entry"() {

		given: "a registered and logged in B2B customer, with an existing cart entry"
		authorizeCustomer(B2B_CUSTOMER)
		def cart = createEmptyCart(format, B2B_USERNAME)
		def configId = generateTestConfigId(P_CODE_CONF_LAPTOP)
		def addResponse = addConfigurationToCart(cart, P_CODE_CONF_LAPTOP, configId, format, B2B_USERNAME)
		def entryNumber = addResponse.entry.entryNumber;

		when: "consumer updates a new cart entry for a given config id"
		def newConfigId = generateTestConfigId(P_CODE_CONF_LAPTOP)
		def updateResponse = updateCartEntryConfiguration(cart, entryNumber, newConfigId, format, B2B_USERNAME)

		then: "the item was updated sucessfully"
		isNotEmpty(updateResponse.entry)
		isNotEmpty(updateResponse.entry.product)
		updateResponse.quantityAdded == 0
		updateResponse.statusCode == "success"
		updateResponse.entry.product.code == "CONF_LAPTOP"
		updateResponse.entry.entryNumber == addResponse.entry.entryNumber

		where:
		format <<[XML, JSON]
	}

	@Test
	def "Get configId of existing cart entry"() {

		given: "a registered and logged in B2B customer, with an existing cart entry"
		authorizeCustomer(B2B_CUSTOMER)
		def cart = createEmptyCart(format, B2B_USERNAME)
		def configId = generateTestConfigId(P_CODE_CONF_LAPTOP)
		def addResponse = addConfigurationToCart(cart, P_CODE_CONF_LAPTOP, configId, format, B2B_USERNAME)
		def entryNumber = addResponse.entry.entryNumber;

		when: "consumer gets config id for a cart entry"
		def entry = getCartEntryConfigurationId(cart, entryNumber, format, B2B_USERNAME)

		then: "the draft configId is returned"
		isNotEmpty(entry)
		entry.configId != configId;

		where:
		format <<[XML, JSON]
	}

	@Test
	def "Get config infos of incomplete cart entry"() {

		given: "a registered and logged in B2B customer, with an existing cart entry"
		authorizeCustomer(B2B_CUSTOMER)
		def cart = createEmptyCart(format, B2B_USERNAME)
		def configId = generateTestConfigId(P_CODE_CONF_LAPTOP)
		def addResponse = addConfigurationToCart(cart, P_CODE_CONF_LAPTOP, configId, format, B2B_USERNAME)
		def entryNumber = addResponse.entry.entryNumber;

		when: "consumer gets config infos for a cart entry"
		def entry = getCartEntry(cart, entryNumber, format, B2B_USERNAME)

		then: "the config infos are returned"
		isNotEmpty(entry)
		isNotEmpty(entry.configurationInfos)

		entry.configurationInfos.size() == 7

		entry.configurationInfos[0].configurationLabel == 'CI#@#VERSION'
		entry.configurationInfos[0].configurationValue == '2'
		entry.configurationInfos[0].configuratorType == CONFIGURATOR_TYPE
		entry.configurationInfos[0].status == 'ERROR' // incomplete config

		entry.configurationInfos[1].configurationLabel == 'CI#@#CURRENCY_ISO_CODE'
		entry.configurationInfos[1].configurationValue == 'USD'

		entry.configurationInfos[2].configurationLabel == 'LI#0#KEY'
		entry.configurationInfos[2].configurationValue == 'CONF_POWER_SUPPLY_cpq'

		entry.configurationInfos[3].configurationLabel == 'LI#0#NAME'
		entry.configurationInfos[3].configurationValue == 'CONF_POWER_SUPPLY'

		entry.configurationInfos[4].configurationLabel == 'LI#0#QTY'
		entry.configurationInfos[4].configurationValue == '1'

		entry.configurationInfos[5].configurationLabel == 'LI#0#FORMATTED_PRICE'
		entry.configurationInfos[5].configurationValue.toString().charAt(0) == '$'

		entry.configurationInfos[6].configurationLabel == 'LI#0#PRICE_VALUE'
		entry.configurationInfos[6].configurationValue == '30.00'




		where:
		format <<[XML, JSON]
	}

	@Test
	def "Get config infos of complete cart entry"() {

		given: "a registered and logged in B2B customer, with an existing cart entry"
		authorizeCustomer(B2B_CUSTOMER)
		def cart = createEmptyCart(format, B2B_USERNAME)
		def configId = generateTestConfigId(P_CODE_CONF_CAMERA_BUNDLE)
		def addResponse = addConfigurationToCart(cart, P_CODE_CONF_CAMERA_BUNDLE, configId, format, B2B_USERNAME)
		def entryNumber = addResponse.entry.entryNumber;

		when: "consumer gets config infos for a cart entry"
		def entry = getCartEntry(cart, entryNumber, format, B2B_USERNAME)

		then: "the config infos are returned"
		isNotEmpty(entry)
		isNotEmpty(entry.configurationInfos)


		entry.configurationInfos.size() == 20
		for(def item :entry.configurationInfos) {
			item.configuratorType == CONFIGURATOR_TYPE
			item.status == 'SUCCESS'
		}

		entry.configurationInfos[0].configurationLabel == 'CI#@#VERSION'
		entry.configurationInfos[0].configurationValue == '2'

		entry.configurationInfos[1].configurationLabel == 'CI#@#CURRENCY_ISO_CODE'
		entry.configurationInfos[1].configurationValue == 'USD'

		entry.configurationInfos[2].configurationLabel == 'LI#0#KEY'
		entry.configurationInfos[2].configurationValue == 'CANON_EOS_80D'

		entry.configurationInfos[3].configurationLabel == 'LI#0#NAME'
		entry.configurationInfos[3].configurationValue == 'Canon EOS 80D'

		entry.configurationInfos[4].configurationLabel == 'LI#0#QTY'
		entry.configurationInfos[4].configurationValue == ''

		entry.configurationInfos[5].configurationLabel == 'LI#1#KEY'
		entry.configurationInfos[5].configurationValue == 'SANDISK_EXTREME_PRO_128GB_SDXC'

		entry.configurationInfos[6].configurationLabel == 'LI#1#NAME'
		entry.configurationInfos[6].configurationValue == 'SanDisk Extreme Pro 128GB SDXC'

		entry.configurationInfos[7].configurationLabel == 'LI#1#QTY'
		entry.configurationInfos[7].configurationValue == '1'

		entry.configurationInfos[8].configurationLabel == 'LI#1#FORMATTED_PRICE'
		entry.configurationInfos[8].configurationValue.toString().charAt(0) == '$'

		entry.configurationInfos[9].configurationLabel == 'LI#1#PRICE_VALUE'
		entry.configurationInfos[9].configurationValue == '100.00'

		entry.configurationInfos[10].configurationLabel == 'LI#2#KEY'
		entry.configurationInfos[10].configurationValue == 'CANON_RF_24-105MM_F/4L_IS_USM'

		entry.configurationInfos[11].configurationLabel == 'LI#2#NAME'
		entry.configurationInfos[11].configurationValue == 'Canon RF 24-105mm f/4L IS USM'

		entry.configurationInfos[12].configurationLabel == 'LI#2#QTY'
		entry.configurationInfos[12].configurationValue == ''

		entry.configurationInfos[14].configurationLabel == 'LI#2#PRICE_VALUE'
		entry.configurationInfos[14].configurationValue == '1500.00'

		entry.configurationInfos[15].configurationLabel == 'LI#3#KEY'
		entry.configurationInfos[15].configurationValue == 'LOWEPRO_STREETLINE_SL_140'

		entry.configurationInfos[16].configurationLabel == 'LI#3#NAME'
		entry.configurationInfos[16].configurationValue == 'LowePro Streetline SL 140'

		entry.configurationInfos[17].configurationLabel == 'LI#3#QTY'
		entry.configurationInfos[17].configurationValue == ''

		entry.configurationInfos[19].configurationLabel == 'LI#3#PRICE_VALUE'
		entry.configurationInfos[19].configurationValue == '110.00'

		where:
		format <<[XML, JSON]
	}
}
