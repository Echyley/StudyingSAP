/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.unit.indexer.service.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.searchservices.admin.data.SnField
import de.hybris.platform.searchservices.indexer.SnIndexerException
import de.hybris.platform.searchservices.indexer.service.SnIndexerContext
import de.hybris.platform.searchservices.indexer.service.SnIndexerFieldWrapper
import de.hybris.platform.searchservices.indexer.service.impl.DefaultSnIndexerFieldWrapper
import de.hybris.platform.searchservices.indexer.service.impl.OptionalParamSnIndexerValueProcessor
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

import spock.lang.Unroll


@UnitTest
public class OptionalParamSnIndexerValueProcessorSpec extends JUnitPlatformSpecification {

	static final String FIELD_ID = "field"

	static final String LANGUAGE_ID = "en"

	static final String QUALIFIER_TYPE_ID = "qualifierType"
	static final String QUALIFIER_ID = "qualifier"

	SnIndexerContext indexerContext = Mock()

	OptionalParamSnIndexerValueProcessor valueProcessor

	def setup() {
		valueProcessor = new OptionalParamSnIndexerValueProcessor()
	}

	@Test
	def "Value and blank strings are optional by default #testId: value=#value"(testId, value) {
		given:
		SnField field = new SnField(id: FIELD_ID, multiValued: false)
		Map<String, String> valueProviderParameters = [:]
		SnIndexerFieldWrapper fieldWrapper = new DefaultSnIndexerFieldWrapper(field: field, valueProviderParameters: valueProviderParameters)

		when:
		Object processedValue = valueProcessor.process(indexerContext, fieldWrapper, value)

		then:
		processedValue == value

		where:
		testId | value
		1      | null
		2      | ""
		3      | " "
		4      | "a"
		5      | Boolean.TRUE
	}

	def "Value and blank strings are optional #testId: value=#value"(testId, value) {
		given:
		SnField field = new SnField(id: FIELD_ID, multiValued: false)
		Map<String, String> valueProviderParameters = [
			(OptionalParamSnIndexerValueProcessor.OPTIONAL_PARAM): Boolean.TRUE.toString(),
			(OptionalParamSnIndexerValueProcessor.OPTIONAL_BLANK_STRING_PARAM): Boolean.TRUE.toString()
		]
		SnIndexerFieldWrapper fieldWrapper = new DefaultSnIndexerFieldWrapper(field: field, valueProviderParameters: valueProviderParameters)

		when:
		Object processedValue = valueProcessor.process(indexerContext, fieldWrapper, value)

		then:
		processedValue == value

		where:
		testId | value
		1      | null
		2      | ""
		3      | " "
		4      | "a"
		5      | Boolean.TRUE
	}

	def "Value is not optional, blank string are optional #testId: value=#value"(testId, value) {
		given:
		SnField field = new SnField(id: FIELD_ID, multiValued: false)
		Map<String, String> valueProviderParameters = [
			(OptionalParamSnIndexerValueProcessor.OPTIONAL_PARAM): Boolean.FALSE.toString(),
			(OptionalParamSnIndexerValueProcessor.OPTIONAL_BLANK_STRING_PARAM): Boolean.TRUE.toString()
		]
		SnIndexerFieldWrapper fieldWrapper = new DefaultSnIndexerFieldWrapper(field: field, valueProviderParameters: valueProviderParameters)

		when:
		Object processedValue = valueProcessor.process(indexerContext, fieldWrapper, value)

		then:
		processedValue == value

		where:
		testId | value
		1      | ""
		2      | " "
	}

	@Test
	def "Value and blank strings are not optional #testId: value=#value"(testId, value) {
		given:
		SnField field = new SnField(id: FIELD_ID, multiValued: false)
		Map<String, String> valueProviderParameters = [
			(OptionalParamSnIndexerValueProcessor.OPTIONAL_PARAM): Boolean.FALSE.toString(),
			(OptionalParamSnIndexerValueProcessor.OPTIONAL_BLANK_STRING_PARAM): Boolean.FALSE.toString()
		]
		SnIndexerFieldWrapper fieldWrapper = new DefaultSnIndexerFieldWrapper(field: field, valueProviderParameters: valueProviderParameters)

		when:
		valueProcessor.process(indexerContext, fieldWrapper, value)

		then:
		thrown(SnIndexerException)

		where:
		testId | value
		1      | null
		2      | ""
		3      | " "
	}

	def "Value and blank strings are optional on multi-valued field #testId: value=#value"(testId, value) {
		given:
		SnField field = new SnField(id: FIELD_ID, multiValued: true)
		Map<String, String> valueProviderParameters = [
			(OptionalParamSnIndexerValueProcessor.OPTIONAL_PARAM): Boolean.TRUE.toString(),
			(OptionalParamSnIndexerValueProcessor.OPTIONAL_BLANK_STRING_PARAM): Boolean.TRUE.toString()
		]
		SnIndexerFieldWrapper fieldWrapper = new DefaultSnIndexerFieldWrapper(field: field, valueProviderParameters: valueProviderParameters)

		when:
		Object processedValue = valueProcessor.process(indexerContext, fieldWrapper, value)

		then:
		processedValue == value

		where:
		testId | value
		1      | null
		2      | newList()
		3      | newList(null)
		4      | newList("")
		4      | newList(" ")
		5      | newList("a")
		6      | newList(Boolean.TRUE)
	}

	def "Value is not optional, blank strings are optional on multi-valued field #testId: value=#value"(testId, value) {
		given:
		SnField field = new SnField(id: FIELD_ID, multiValued: true)
		Map<String, String> valueProviderParameters = [
			(OptionalParamSnIndexerValueProcessor.OPTIONAL_PARAM): Boolean.FALSE.toString(),
			(OptionalParamSnIndexerValueProcessor.OPTIONAL_BLANK_STRING_PARAM): Boolean.TRUE.toString()
		]
		SnIndexerFieldWrapper fieldWrapper = new DefaultSnIndexerFieldWrapper(field: field, valueProviderParameters: valueProviderParameters)

		when:
		Object processedValue = valueProcessor.process(indexerContext, fieldWrapper, value)

		then:
		processedValue == value

		where:
		testId | value
		1      | newList("")
		2      | newList(" ")
	}

	@Test
	def "Value and blank strings are not optional on multi-valued field #testId: value=#value"(testId, value) {
		given:
		SnField field = new SnField(id: FIELD_ID, multiValued: true)
		Map<String, String> valueProviderParameters = [
			(OptionalParamSnIndexerValueProcessor.OPTIONAL_PARAM): Boolean.FALSE.toString(),
			(OptionalParamSnIndexerValueProcessor.OPTIONAL_BLANK_STRING_PARAM): Boolean.FALSE.toString()
		]
		SnIndexerFieldWrapper fieldWrapper = new DefaultSnIndexerFieldWrapper(field: field, valueProviderParameters: valueProviderParameters)

		when:
		valueProcessor.process(indexerContext, fieldWrapper, value)

		then:
		thrown(SnIndexerException)

		where:
		testId | value
		1      | null
		2      | newList()
		3      | newList(null)
		4      | newList("")
		5      | newList(" ")
	}


	def "Value and blank strings are optional on localized field #testId: value=#value"(testId, value) {
		given:
		SnField field = new SnField(id: FIELD_ID, localized: true, multiValued: false)
		Map<String, String> valueProviderParameters = [
			(OptionalParamSnIndexerValueProcessor.OPTIONAL_PARAM): Boolean.TRUE.toString(),
			(OptionalParamSnIndexerValueProcessor.OPTIONAL_BLANK_STRING_PARAM): Boolean.TRUE.toString()
		]
		SnIndexerFieldWrapper fieldWrapper = new DefaultSnIndexerFieldWrapper(field: field, valueProviderParameters: valueProviderParameters)

		when:
		Object processedValue = valueProcessor.process(indexerContext, fieldWrapper, value)

		then:
		processedValue == value

		where:
		testId | value
		1      | null
		2      | newMap()
		3      | newMap(LANGUAGE_ID, null)
		4      | newMap(LANGUAGE_ID, "")
		5      | newMap(LANGUAGE_ID, "")
		6      | newMap(LANGUAGE_ID, "a")
		7      | newMap(LANGUAGE_ID, Boolean.TRUE)
	}

	def "Value is not optional, blank strings are optional on localized field #testId: value=#value"(testId, value) {
		given:
		SnField field = new SnField(id: FIELD_ID, localized: true, multiValued: false)
		Map<String, String> valueProviderParameters = [
			(OptionalParamSnIndexerValueProcessor.OPTIONAL_PARAM): Boolean.FALSE.toString(),
			(OptionalParamSnIndexerValueProcessor.OPTIONAL_BLANK_STRING_PARAM): Boolean.TRUE.toString()
		]
		SnIndexerFieldWrapper fieldWrapper = new DefaultSnIndexerFieldWrapper(field: field, valueProviderParameters: valueProviderParameters)

		when:
		Object processedValue = valueProcessor.process(indexerContext, fieldWrapper, value)

		then:
		processedValue == value

		where:
		testId | value
		1      | newMap(LANGUAGE_ID, "")
		2      | newMap(LANGUAGE_ID, " ")
	}

	@Test
	def "Value and blank strings are not optional on localized field #testId: value=#value"(testId, value) {
		given:
		SnField field = new SnField(id: FIELD_ID, localized: true, multiValued: false)
		Map<String, String> valueProviderParameters = [
			(OptionalParamSnIndexerValueProcessor.OPTIONAL_PARAM): Boolean.FALSE.toString(),
			(OptionalParamSnIndexerValueProcessor.OPTIONAL_BLANK_STRING_PARAM): Boolean.FALSE.toString()
		]
		SnIndexerFieldWrapper fieldWrapper = new DefaultSnIndexerFieldWrapper(field: field, valueProviderParameters: valueProviderParameters)

		when:
		valueProcessor.process(indexerContext, fieldWrapper, value)

		then:
		thrown(SnIndexerException)

		where:
		testId | value
		1      | null
		2      | newMap()
		3      | newMap(LANGUAGE_ID, null)
		4      | newMap(LANGUAGE_ID, "")
		5      | newMap(LANGUAGE_ID, " ")
	}

	def "Value and blank strings are optional on qualified field #testId: value=#value"(testId, value) {
		given:
		SnField field = new SnField(id: FIELD_ID, qualifierTypeId: QUALIFIER_TYPE_ID, multiValued: false)
		Map<String, String> valueProviderParameters = [
			(OptionalParamSnIndexerValueProcessor.OPTIONAL_PARAM): Boolean.TRUE.toString(),
			(OptionalParamSnIndexerValueProcessor.OPTIONAL_BLANK_STRING_PARAM): Boolean.TRUE.toString()
		]
		SnIndexerFieldWrapper fieldWrapper = new DefaultSnIndexerFieldWrapper(field: field, valueProviderParameters: valueProviderParameters)

		when:
		Object processedValue = valueProcessor.process(indexerContext, fieldWrapper, value)

		then:
		processedValue == value

		where:
		testId | value
		1      | null
		2      | newMap()
		3      | newMap(QUALIFIER_ID, null)
		4      | newMap(QUALIFIER_ID, "")
		5      | newMap(QUALIFIER_ID, " ")
		6      | newMap(QUALIFIER_ID, "a")
		7      | newMap(QUALIFIER_ID, Boolean.TRUE)
	}

	def "Value is not optional, blank strings are optional on qualified field #testId: value=#value"(testId, value) {
		given:
		SnField field = new SnField(id: FIELD_ID, qualifierTypeId: QUALIFIER_TYPE_ID, multiValued: false)
		Map<String, String> valueProviderParameters = [
			(OptionalParamSnIndexerValueProcessor.OPTIONAL_PARAM): Boolean.FALSE.toString(),
			(OptionalParamSnIndexerValueProcessor.OPTIONAL_BLANK_STRING_PARAM): Boolean.TRUE.toString()
		]
		SnIndexerFieldWrapper fieldWrapper = new DefaultSnIndexerFieldWrapper(field: field, valueProviderParameters: valueProviderParameters)

		when:
		Object processedValue = valueProcessor.process(indexerContext, fieldWrapper, value)

		then:
		processedValue == value

		where:
		testId | value
		1      | newMap(QUALIFIER_ID, "")
		2      | newMap(QUALIFIER_ID, " ")
	}

	@Test
	def "Value and blank strings are not optional on qualified field #testId: value=#value"(testId, value) {
		given:
		SnField field = new SnField(id: FIELD_ID, qualifierTypeId: QUALIFIER_TYPE_ID, multiValued: false)
		Map<String, String> valueProviderParameters = [
			(OptionalParamSnIndexerValueProcessor.OPTIONAL_PARAM): Boolean.FALSE.toString(),
			(OptionalParamSnIndexerValueProcessor.OPTIONAL_BLANK_STRING_PARAM): Boolean.FALSE.toString()
		]
		SnIndexerFieldWrapper fieldWrapper = new DefaultSnIndexerFieldWrapper(field: field, valueProviderParameters: valueProviderParameters)

		when:
		valueProcessor.process(indexerContext, fieldWrapper, value)

		then:
		thrown(SnIndexerException)

		where:
		testId | value
		1      | null
		2      | newMap()
		3      | newMap(QUALIFIER_ID, null)
		4      | newMap(QUALIFIER_ID, "")
		5      | newMap(QUALIFIER_ID, " ")
	}

	def "Value and blank strings are optional on multi-valued localized field #testId: value=#value"(testId, value) {
		given:
		SnField field = new SnField(id: FIELD_ID, localized: true, multiValued: true)
		Map<String, String> valueProviderParameters = [
			(OptionalParamSnIndexerValueProcessor.OPTIONAL_PARAM): Boolean.TRUE.toString(),
			(OptionalParamSnIndexerValueProcessor.OPTIONAL_BLANK_STRING_PARAM): Boolean.TRUE.toString()
		]
		SnIndexerFieldWrapper fieldWrapper = new DefaultSnIndexerFieldWrapper(field: field, valueProviderParameters: valueProviderParameters)

		when:
		Object processedValue = valueProcessor.process(indexerContext, fieldWrapper, value)

		then:
		processedValue == value

		where:
		testId | value
		1      | null
		2      | newMap()
		3      | newMap(LANGUAGE_ID, null)
		4      | newMap(LANGUAGE_ID, newList())
		5      | newMap(LANGUAGE_ID, newList(null))
		6      | newMap(LANGUAGE_ID, newList(""))
		7      | newMap(LANGUAGE_ID, newList("a"))
		8      | newMap(LANGUAGE_ID, newList(Boolean.TRUE))
	}

	def "Value is not optional, blank strings are optional on multi-valued localized field #testId: value=#value"(testId, value) {
		given:
		SnField field = new SnField(id: FIELD_ID, localized: true, multiValued: true)
		Map<String, String> valueProviderParameters = [
			(OptionalParamSnIndexerValueProcessor.OPTIONAL_PARAM): Boolean.FALSE.toString(),
			(OptionalParamSnIndexerValueProcessor.OPTIONAL_BLANK_STRING_PARAM): Boolean.TRUE.toString()
		]
		SnIndexerFieldWrapper fieldWrapper = new DefaultSnIndexerFieldWrapper(field: field, valueProviderParameters: valueProviderParameters)

		when:
		Object processedValue = valueProcessor.process(indexerContext, fieldWrapper, value)

		then:
		processedValue == value

		where:
		testId | value
		1      | newMap(LANGUAGE_ID, newList(""))
		2      | newMap(LANGUAGE_ID, newList(" "))
	}

	@Test
	def "Value and blank strings are not optional on multi-valued localized field #testId: value=#value"(testId, value) {
		given:
		SnField field = new SnField(id: FIELD_ID, localized: true, multiValued: true)
		Map<String, String> valueProviderParameters = [
			(OptionalParamSnIndexerValueProcessor.OPTIONAL_PARAM): Boolean.FALSE.toString(),
			(OptionalParamSnIndexerValueProcessor.OPTIONAL_BLANK_STRING_PARAM): Boolean.FALSE.toString()
		]
		SnIndexerFieldWrapper fieldWrapper = new DefaultSnIndexerFieldWrapper(field: field, valueProviderParameters: valueProviderParameters)

		when:
		valueProcessor.process(indexerContext, fieldWrapper, value)

		then:
		thrown(SnIndexerException)

		where:
		testId | value
		1      | null
		2      | newMap()
		3      | newMap(LANGUAGE_ID, null)
		4      | newMap(LANGUAGE_ID, newList())
		5      | newMap(LANGUAGE_ID, newList(null))
		6      | newMap(LANGUAGE_ID, newList(""))
		7      | newMap(LANGUAGE_ID, newList(" "))
	}

	def "Value and blank strings are optional on multi-valued qualified field #testId: value=#value"(testId, value) {
		given:
		SnField field = new SnField(id: FIELD_ID, qualifierTypeId: QUALIFIER_TYPE_ID, multiValued: true)
		Map<String, String> valueProviderParameters = [
			(OptionalParamSnIndexerValueProcessor.OPTIONAL_PARAM): Boolean.TRUE.toString(),
			(OptionalParamSnIndexerValueProcessor.OPTIONAL_BLANK_STRING_PARAM): Boolean.TRUE.toString()
		]
		SnIndexerFieldWrapper fieldWrapper = new DefaultSnIndexerFieldWrapper(field: field, valueProviderParameters: valueProviderParameters)

		when:
		Object processedValue = valueProcessor.process(indexerContext, fieldWrapper, value)

		then:
		processedValue == value

		where:
		testId | value
		1      | null
		2      | newMap()
		3      | newMap(QUALIFIER_ID, null)
		4      | newMap(QUALIFIER_ID, newList())
		5      | newMap(QUALIFIER_ID, newList(null))
		6      | newMap(QUALIFIER_ID, newList(""))
		7      | newMap(QUALIFIER_ID, newList(" "))
		8      | newMap(QUALIFIER_ID, newList("a"))
		9      | newMap(QUALIFIER_ID, newList(Boolean.TRUE))
	}

	def "Value is not optional, blank strings are optional on multi-valued qualified field #testId: value=#value"(testId, value) {
		given:
		SnField field = new SnField(id: FIELD_ID, qualifierTypeId: QUALIFIER_TYPE_ID, multiValued: true)
		Map<String, String> valueProviderParameters = [
			(OptionalParamSnIndexerValueProcessor.OPTIONAL_PARAM): Boolean.FALSE.toString(),
			(OptionalParamSnIndexerValueProcessor.OPTIONAL_BLANK_STRING_PARAM): Boolean.TRUE.toString()
		]
		SnIndexerFieldWrapper fieldWrapper = new DefaultSnIndexerFieldWrapper(field: field, valueProviderParameters: valueProviderParameters)

		when:
		Object processedValue = valueProcessor.process(indexerContext, fieldWrapper, value)

		then:
		processedValue == value

		where:
		testId | value
		1      | newMap(QUALIFIER_ID, newList(""))
		2      | newMap(QUALIFIER_ID, newList(" "))
	}

	@Test
	def "Value and blank strings are not optional on multi-valued qualified field #testId: value=#value"(testId, value) {
		given:
		SnField field = new SnField(id: FIELD_ID, qualifierTypeId: QUALIFIER_TYPE_ID, multiValued: true)
		Map<String, String> valueProviderParameters = [
			(OptionalParamSnIndexerValueProcessor.OPTIONAL_PARAM): Boolean.FALSE.toString(),
			(OptionalParamSnIndexerValueProcessor.OPTIONAL_BLANK_STRING_PARAM): Boolean.FALSE.toString()
		]
		SnIndexerFieldWrapper fieldWrapper = new DefaultSnIndexerFieldWrapper(field: field, valueProviderParameters: valueProviderParameters)

		when:
		valueProcessor.process(indexerContext, fieldWrapper, value)

		then:
		thrown(SnIndexerException)

		where:
		testId | value
		1      | null
		2      | newMap()
		3      | newMap(QUALIFIER_ID, null)
		4      | newMap(QUALIFIER_ID, newList())
		5      | newMap(QUALIFIER_ID, newList(""))
		6      | newMap(QUALIFIER_ID, newList(" "))
	}

	def final <T> List<T> newList() {
		return Collections.emptyList()
	}

	def final <T> List<T> newList(T... values) {
		ArrayList list = new ArrayList()

		for (T value : values) {
			list.add(value)
		}

		return Collections.unmodifiableList(list)
	}

	def final <K, V> Map<K, V> newMap() {
		return Collections.emptyMap()
	}

	def final <K, V> Map<K, V> newMap(K key, V value) {
		return Collections.singletonMap(key, value)
	}
}
