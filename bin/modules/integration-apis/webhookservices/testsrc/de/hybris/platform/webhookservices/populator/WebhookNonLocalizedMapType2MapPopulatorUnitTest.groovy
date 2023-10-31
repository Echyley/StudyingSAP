/*
 *  Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.webhookservices.populator

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.apiregistryservices.model.events.EventPropertyConfigurationModel
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.integrationservices.model.AttributeValueAccessor
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.integrationservices.populator.ItemToMapConversionContext
import de.hybris.platform.testframework.JUnitPlatformSpecification
import de.hybris.platform.webhookservices.model.WebhookPayloadModel
import org.apache.commons.lang3.StringUtils
import org.junit.Test

@UnitTest
class WebhookNonLocalizedMapType2MapPopulatorUnitTest extends JUnitPlatformSpecification {

	private static final String KEY = "key"
	private static final String VALUE = "value"
	private static final String MAP_KEY = "map_key"
	private static final String ATTR_KEY = "attr_key"
	private static final String ATTR_VALUE = "attr_value"
	private Map<String, Object> targetMap = [:]

	def populator = new WebhookNonLocalizedMapType2MapPopulator()

	@Test
	def "populates a map from a non-localized map attribute #typeCode-#mapKey"() {
		when:
		populator.populate(conversionContext(typeCode: typeCode, mapKey: mapKey) as ItemToMapConversionContext, targetMap)

		then:
		targetMap == result

		where:
		typeCode                                  | mapKey                                   | result
		WebhookPayloadModel._TYPECODE             | WebhookPayloadModel.DATA                 | [(mapKey): [(ATTR_KEY): ATTR_VALUE]]
		EventPropertyConfigurationModel._TYPECODE | EventPropertyConfigurationModel.EXAMPLES | [(mapKey): [[(KEY): ATTR_KEY, (VALUE): ATTR_VALUE]]]
	}

	@Test
	def "populates a map from a non-localized map attribute with #description map key"() {
		when:
		populator.populate(conversionContext(mapKey: mapKey), targetMap)

		then:
		targetMap == result

		where:
		result                                          | mapKey            | description
		[:]                                             | null              | "a null"
		[(StringUtils.EMPTY): [(ATTR_KEY): ATTR_VALUE]] | StringUtils.EMPTY | "an empty"
	}

	@Test
	def "populates a map from a non-localized map attribute with #description attribute key"() {
		when:
		populator.populate(conversionContext(key: attributeKey), targetMap)

		then:
		targetMap == result

		where:
		result                                         | attributeKey      | description
		[(MAP_KEY): [:]]                               | null              | "a null"
		[(MAP_KEY): [(StringUtils.EMPTY): ATTR_VALUE]] | StringUtils.EMPTY | "an empty"
	}

	@Test
	def "populates a map from a non-localized map attribute with a null attribute value"() {
		when:
		populator.populate(conversionContext(value: null), targetMap)

		then:
		targetMap == [(MAP_KEY): [(ATTR_KEY): null]]
	}

	@Test
	def "does not populate a map from a #decription attribute"() {
		when:
		populator.populate(conversionContext(isLocalizedType: isLocalized, isMapType: isMapType), targetMap)

		then:
		targetMap == [:]

		where:
		isLocalized | isMapType | decription
		true        | true      | "localized map"
		true        | false     | "localized non-map"
		false       | false     | "non-localized non-map"
	}

	ItemToMapConversionContext conversionContext(final Map params = [:]) {
		def itemModel = new ItemModel()
		Stub(ItemToMapConversionContext) {
			getItemModel() >> itemModel
            getPayloadObject() >> itemModel
			getTypeDescriptor() >>
					Stub(TypeDescriptor) {
						getAttributes() >> [Stub(TypeAttributeDescriptor) {
							getTypeDescriptor() >> Stub(TypeDescriptor) {
								getTypeCode() >> params.get('typeCode', WebhookPayloadModel._TYPECODE)
							}
							isLocalized() >> params.get('isLocalizedType', false)
							isMap() >> params.get('isMapType', true)
							getAttributeName() >> params.get('mapKey', MAP_KEY)
							accessor() >> Stub(AttributeValueAccessor) {
								getValue(itemModel) >> [(params.get('key', ATTR_KEY)): params.get('value', ATTR_VALUE)]
							}
						}]
					}
		}
	}

}
