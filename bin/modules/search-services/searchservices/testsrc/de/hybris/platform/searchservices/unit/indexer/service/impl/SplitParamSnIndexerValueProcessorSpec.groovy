/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.unit.indexer.service.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.searchservices.admin.data.SnField
import de.hybris.platform.searchservices.enums.SnFieldType
import de.hybris.platform.searchservices.indexer.SnIndexerException
import de.hybris.platform.searchservices.indexer.service.SnIndexerContext
import de.hybris.platform.searchservices.indexer.service.SnIndexerFieldWrapper
import de.hybris.platform.searchservices.indexer.service.impl.DefaultSnIndexerFieldWrapper
import de.hybris.platform.searchservices.indexer.service.impl.SplitParamSnIndexerValueProcessor
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test


@UnitTest
public class SplitParamSnIndexerValueProcessorSpec extends JUnitPlatformSpecification {

	static final String FIELD_ID = "field"

	static final String LANGUAGE_ID = "en"

	static final String QUALIFIER_TYPE_ID = "qualifierType"
	static final String QUALIFIER_ID = "qualifier"

	SnIndexerContext indexerContext = Mock()

	SplitParamSnIndexerValueProcessor valueProcessor

	def setup() {
		valueProcessor = new SplitParamSnIndexerValueProcessor()
	}

	@Test
	def "Don't split value by default"() {
		given:
		def value = "a b"

		SnField field = new SnField(id: FIELD_ID, fieldType: SnFieldType.STRING)
		SnIndexerFieldWrapper fieldWrapper = new DefaultSnIndexerFieldWrapper(field: field)

		when:
		Object processedValue = valueProcessor.process(indexerContext, fieldWrapper, value)

		then:
		processedValue == value
	}

	@Test
	def "Cannot split value on not supported field type #testId: fieldType=#fieldType"(testId, fieldType) {
		given:
		def value = "a b"

		SnField field = new SnField(id: FIELD_ID, fieldType: fieldType)
		Map<String, String> valueProviderParameters = [
			(SplitParamSnIndexerValueProcessor.SPLIT_PARAM): Boolean.TRUE.toString()
		]
		SnIndexerFieldWrapper fieldWrapper = new DefaultSnIndexerFieldWrapper(field: field, valueProviderParameters: valueProviderParameters)

		when:
		Object processedValue = valueProcessor.process(indexerContext, fieldWrapper, value)

		then:
		thrown(SnIndexerException)

		where:
		testId | fieldType
		1      | SnFieldType.BOOLEAN
		2      | SnFieldType.INTEGER
		3      | SnFieldType.LONG
		4      | SnFieldType.FLOAT
		5      | SnFieldType.DOUBLE
		6      | SnFieldType.DATE_TIME
	}

	@Test
	def "Split value on single-valued field #testId: fieldType=#fieldType, value=#value, expectedValue=#expectedValue"(testId, fieldType, value, expectedValue) {
		given:
		SnField field = new SnField(id: FIELD_ID, fieldType: fieldType)
		Map<String, String> valueProviderParameters = [
			(SplitParamSnIndexerValueProcessor.SPLIT_PARAM): Boolean.TRUE.toString()
		]
		SnIndexerFieldWrapper fieldWrapper = new DefaultSnIndexerFieldWrapper(field: field, valueProviderParameters: valueProviderParameters)

		when:
		Object processedValue = valueProcessor.process(indexerContext, fieldWrapper, value)

		then:
		processedValue == expectedValue

		where:
		testId | fieldType          | value     || expectedValue
		1      | SnFieldType.STRING | null      || null
		2      | SnFieldType.STRING | ""        || ""
		3      | SnFieldType.STRING | "a"       || "a"
		4      | SnFieldType.STRING | "a b"     || "a"
		5      | SnFieldType.STRING | "a b c d" || "a"
		6      | SnFieldType.TEXT   | null      || null
		7      | SnFieldType.TEXT   | ""        || ""
		8      | SnFieldType.TEXT   | "a"       || "a"
		9      | SnFieldType.TEXT   | "a b"     || "a"
		10     | SnFieldType.TEXT   | "a b c d" || "a"
	}

	@Test
	def "Split value on multi-valued field #testId: fieldType=#fieldType, value=#value, expectedValue=#expectedValue"(testId, fieldType, value, expectedValue) {
		given:
		SnField field = new SnField(id: FIELD_ID, fieldType: fieldType, multiValued: true)
		Map<String, String> valueProviderParameters = [
			(SplitParamSnIndexerValueProcessor.SPLIT_PARAM): Boolean.TRUE.toString()
		]
		SnIndexerFieldWrapper fieldWrapper = new DefaultSnIndexerFieldWrapper(field: field, valueProviderParameters: valueProviderParameters)

		when:
		Object processedValue = valueProcessor.process(indexerContext, fieldWrapper, value)

		then:
		processedValue == expectedValue

		where:
		testId | fieldType          | value                 || expectedValue
		1      | SnFieldType.STRING | null                  || null
		2      | SnFieldType.STRING | newList()             || newList()
		3      | SnFieldType.STRING | newList(null)         || newList(null)
		4      | SnFieldType.STRING | newList("")           || newList("")
		5      | SnFieldType.STRING | newList("a")          || newList("a")
		6      | SnFieldType.STRING | newList("a b")        || newList("a", "b")
		7      | SnFieldType.STRING | newList("a b", "c d") || newList("a", "b", "c", "d")
		8      | SnFieldType.TEXT   | null                  || null
		9      | SnFieldType.TEXT   | newList()             || newList()
		10     | SnFieldType.TEXT   | newList(null)         || newList(null)
		11     | SnFieldType.TEXT   | newList("")           || newList("")
		12     | SnFieldType.TEXT   | newList("a")          || newList("a")
		13     | SnFieldType.TEXT   | newList("a b")        || newList("a", "b")
		14     | SnFieldType.TEXT   | newList("a b", "c d") || newList("a", "b", "c", "d")
	}

	@Test
	def "Split value on single-valued localized field #testId: fieldType=#fieldType, value=#value, expectedValue=#expectedValue"(testId, fieldType, value, expectedValue) {
		given:
		SnField field = new SnField(id: FIELD_ID, fieldType: fieldType, localized: true)
		Map<String, String> valueProviderParameters = [
			(SplitParamSnIndexerValueProcessor.SPLIT_PARAM): Boolean.TRUE.toString()
		]
		SnIndexerFieldWrapper fieldWrapper = new DefaultSnIndexerFieldWrapper(field: field, valueProviderParameters: valueProviderParameters)

		when:
		Object processedValue = valueProcessor.process(indexerContext, fieldWrapper, value)

		then:
		processedValue == expectedValue

		where:
		testId | fieldType          | value                          || expectedValue
		1      | SnFieldType.STRING | null                           || null
		2      | SnFieldType.STRING | newMap()                       || newMap()
		3      | SnFieldType.STRING | newMap(LANGUAGE_ID, null)      || newMap(LANGUAGE_ID, null)
		4      | SnFieldType.STRING | newMap(LANGUAGE_ID, "")        || newMap(LANGUAGE_ID, "")
		5      | SnFieldType.STRING | newMap(LANGUAGE_ID, "a")       || newMap(LANGUAGE_ID, "a")
		6      | SnFieldType.STRING | newMap(LANGUAGE_ID, "a b")     || newMap(LANGUAGE_ID, "a")
		7      | SnFieldType.STRING | newMap(LANGUAGE_ID, "a b c d") || newMap(LANGUAGE_ID, "a")
		8      | SnFieldType.TEXT   | null                           || null
		9      | SnFieldType.TEXT   | newMap()                       || newMap()
		10     | SnFieldType.TEXT   | newMap(LANGUAGE_ID, null)      || newMap(LANGUAGE_ID, null)
		11     | SnFieldType.TEXT   | newMap(LANGUAGE_ID, "")        || newMap(LANGUAGE_ID, "")
		12     | SnFieldType.TEXT   | newMap(LANGUAGE_ID, "a")       || newMap(LANGUAGE_ID, "a")
		13     | SnFieldType.TEXT   | newMap(LANGUAGE_ID, "a b")     || newMap(LANGUAGE_ID, "a")
		14     | SnFieldType.TEXT   | newMap(LANGUAGE_ID, "a b c d") || newMap(LANGUAGE_ID, "a")
	}

	@Test
	def "Split value on multi-valued localized field #testId: fieldType=#fieldType, value=#value, expectedValue=#expectedValue"(testId, fieldType, value, expectedValue) {
		given:
		SnField field = new SnField(id: FIELD_ID, fieldType: fieldType, localized: true, multiValued: true)
		Map<String, String> valueProviderParameters = [
			(SplitParamSnIndexerValueProcessor.SPLIT_PARAM): Boolean.TRUE.toString()
		]
		SnIndexerFieldWrapper fieldWrapper = new DefaultSnIndexerFieldWrapper(field: field, valueProviderParameters: valueProviderParameters)

		when:
		Object processedValue = valueProcessor.process(indexerContext, fieldWrapper, value)

		then:
		processedValue == expectedValue

		where:
		testId | fieldType          | value                                      || expectedValue
		1      | SnFieldType.STRING | null                                       || null
		2      | SnFieldType.STRING | newMap()                                   || newMap()
		3      | SnFieldType.STRING | newMap(LANGUAGE_ID, null)                  || newMap(LANGUAGE_ID, null)
		4      | SnFieldType.STRING | newMap(LANGUAGE_ID, newList())             || newMap(LANGUAGE_ID, newList())
		5      | SnFieldType.STRING | newMap(LANGUAGE_ID, newList(null))         || newMap(LANGUAGE_ID, newList(null))
		6      | SnFieldType.STRING | newMap(LANGUAGE_ID, newList(""))           || newMap(LANGUAGE_ID, newList(""))
		7      | SnFieldType.STRING | newMap(LANGUAGE_ID, newList("a"))          || newMap(LANGUAGE_ID, newList("a"))
		8      | SnFieldType.STRING | newMap(LANGUAGE_ID, newList("a b"))        || newMap(LANGUAGE_ID, newList("a", "b"))
		9      | SnFieldType.STRING | newMap(LANGUAGE_ID, newList("a b", "c d")) || newMap(LANGUAGE_ID, newList("a", "b", "c", "d"))
		10     | SnFieldType.TEXT   | null                                       || null
		11     | SnFieldType.TEXT   | newMap()                                   || newMap()
		12     | SnFieldType.TEXT   | newMap(LANGUAGE_ID, null)                  || newMap(LANGUAGE_ID, null)
		13     | SnFieldType.TEXT   | newMap(LANGUAGE_ID, newList())             || newMap(LANGUAGE_ID, newList())
		14     | SnFieldType.TEXT   | newMap(LANGUAGE_ID, newList(null))         || newMap(LANGUAGE_ID, newList(null))
		15     | SnFieldType.TEXT   | newMap(LANGUAGE_ID, newList(""))           || newMap(LANGUAGE_ID, newList(""))
		16     | SnFieldType.TEXT   | newMap(LANGUAGE_ID, newList("a"))          || newMap(LANGUAGE_ID, newList("a"))
		17     | SnFieldType.TEXT   | newMap(LANGUAGE_ID, newList("a b"))        || newMap(LANGUAGE_ID, newList("a", "b"))
		18     | SnFieldType.TEXT   | newMap(LANGUAGE_ID, newList("a b", "c d")) || newMap(LANGUAGE_ID, newList("a", "b", "c", "d"))
	}

	@Test
	def "Split value on single-valued qualified field #testId: fieldType=#fieldType, value=#value, expectedValue=#expectedValue"(testId, fieldType, value, expectedValue) {
		given:
		SnField field = new SnField(id: FIELD_ID, fieldType: fieldType, qualifierTypeId: QUALIFIER_TYPE_ID)
		Map<String, String> valueProviderParameters = [
			(SplitParamSnIndexerValueProcessor.SPLIT_PARAM): Boolean.TRUE.toString()
		]
		SnIndexerFieldWrapper fieldWrapper = new DefaultSnIndexerFieldWrapper(field: field, valueProviderParameters: valueProviderParameters)

		when:
		Object processedValue = valueProcessor.process(indexerContext, fieldWrapper, value)

		then:
		processedValue == expectedValue

		where:
		testId | fieldType          | value                           || expectedValue
		1      | SnFieldType.STRING | null                            || null
		2      | SnFieldType.STRING | newMap()                        || newMap()
		3      | SnFieldType.STRING | newMap(QUALIFIER_ID, null)      || newMap(QUALIFIER_ID, null)
		4      | SnFieldType.STRING | newMap(QUALIFIER_ID, "")        || newMap(QUALIFIER_ID, "")
		5      | SnFieldType.STRING | newMap(QUALIFIER_ID, "a")       || newMap(QUALIFIER_ID, "a")
		6      | SnFieldType.STRING | newMap(QUALIFIER_ID, "a b")     || newMap(QUALIFIER_ID, "a")
		7      | SnFieldType.STRING | newMap(QUALIFIER_ID, "a b c d") || newMap(QUALIFIER_ID, "a")
		8      | SnFieldType.TEXT   | null                            || null
		9      | SnFieldType.TEXT   | newMap()                        || newMap()
		10     | SnFieldType.TEXT   | newMap(QUALIFIER_ID, null)      || newMap(QUALIFIER_ID, null)
		11     | SnFieldType.TEXT   | newMap(QUALIFIER_ID, "")        || newMap(QUALIFIER_ID, "")
		12     | SnFieldType.TEXT   | newMap(QUALIFIER_ID, "a")       || newMap(QUALIFIER_ID, "a")
		13     | SnFieldType.TEXT   | newMap(QUALIFIER_ID, "a b")     || newMap(QUALIFIER_ID, "a")
		14     | SnFieldType.TEXT   | newMap(QUALIFIER_ID, "a b c d") || newMap(QUALIFIER_ID, "a")
	}

	@Test
	def "Split value on multi-valued qualified field #testId: fieldType=#fieldType, value=#value, expectedValue=#expectedValue"(testId, fieldType, value, expectedValue) {
		given:
		SnField field = new SnField(id: FIELD_ID, fieldType: fieldType, qualifierTypeId: QUALIFIER_TYPE_ID, multiValued: true)
		Map<String, String> valueProviderParameters = [
			(SplitParamSnIndexerValueProcessor.SPLIT_PARAM): Boolean.TRUE.toString()
		]
		SnIndexerFieldWrapper fieldWrapper = new DefaultSnIndexerFieldWrapper(field: field, valueProviderParameters: valueProviderParameters)

		when:
		Object processedValue = valueProcessor.process(indexerContext, fieldWrapper, value)

		then:
		processedValue == expectedValue

		where:
		testId | fieldType          | value                                       || expectedValue
		1      | SnFieldType.STRING | null                                        || null
		2      | SnFieldType.STRING | newMap()                                    || newMap()
		3      | SnFieldType.STRING | newMap(QUALIFIER_ID, null)                  || newMap(QUALIFIER_ID, null)
		4      | SnFieldType.STRING | newMap(QUALIFIER_ID, newList())             || newMap(QUALIFIER_ID, newList())
		5      | SnFieldType.STRING | newMap(QUALIFIER_ID, newList(null))         || newMap(QUALIFIER_ID, newList(null))
		6      | SnFieldType.STRING | newMap(QUALIFIER_ID, newList(""))           || newMap(QUALIFIER_ID, newList(""))
		7      | SnFieldType.STRING | newMap(QUALIFIER_ID, newList("a"))          || newMap(QUALIFIER_ID, newList("a"))
		8      | SnFieldType.STRING | newMap(QUALIFIER_ID, newList("a b"))        || newMap(QUALIFIER_ID, newList("a", "b"))
		9      | SnFieldType.STRING | newMap(QUALIFIER_ID, newList("a b", "c d")) || newMap(QUALIFIER_ID, newList("a", "b", "c", "d"))
		10     | SnFieldType.TEXT   | null                                        || null
		11     | SnFieldType.TEXT   | newMap()                                    || newMap()
		12     | SnFieldType.TEXT   | newMap(QUALIFIER_ID, null)                  || newMap(QUALIFIER_ID, null)
		13     | SnFieldType.TEXT   | newMap(QUALIFIER_ID, newList())             || newMap(QUALIFIER_ID, newList())
		14     | SnFieldType.TEXT   | newMap(QUALIFIER_ID, newList(null))         || newMap(QUALIFIER_ID, newList(null))
		15     | SnFieldType.TEXT   | newMap(QUALIFIER_ID, newList(""))           || newMap(QUALIFIER_ID, newList(""))
		16     | SnFieldType.TEXT   | newMap(QUALIFIER_ID, newList("a"))          || newMap(QUALIFIER_ID, newList("a"))
		17     | SnFieldType.TEXT   | newMap(QUALIFIER_ID, newList("a b"))        || newMap(QUALIFIER_ID, newList("a", "b"))
		18     | SnFieldType.TEXT   | newMap(QUALIFIER_ID, newList("a b", "c d")) || newMap(QUALIFIER_ID, newList("a", "b", "c", "d"))
	}

	@Test
	def "Split value with regex on single-valued field #testId: #splitRegex -> #expectedValue"(testId, fieldType, value, splitRegex, expectedValue) {
		given:
		SnField field = new SnField(id: FIELD_ID, fieldType: fieldType)
		Map<String, String> valueProviderParameters = [
			(SplitParamSnIndexerValueProcessor.SPLIT_PARAM): Boolean.TRUE.toString(),
			(SplitParamSnIndexerValueProcessor.SPLIT_REGEX_PARAM): splitRegex
		]
		SnIndexerFieldWrapper fieldWrapper = new DefaultSnIndexerFieldWrapper(field: field, valueProviderParameters: valueProviderParameters)

		when:
		Object processedValue = valueProcessor.process(indexerContext, fieldWrapper, value)

		then:
		processedValue == expectedValue

		where:
		testId | fieldType          | value   | splitRegex || expectedValue
		1      | SnFieldType.STRING | null    | "\\/"      || null
		2      | SnFieldType.STRING | ""      | "\\/"      || ""
		3      | SnFieldType.STRING | "a/b"   | "\\/"      || "a"
		4      | SnFieldType.TEXT   | null    | "\\/"      || null
		5      | SnFieldType.TEXT   | ""      | "\\/"      || ""
		6      | SnFieldType.TEXT   | "a/b"   | "\\/"      || "a"
	}

	@Test
	def "Split value with regex on multi-valued field #testId: #splitRegex -> #expectedValue"(testId, fieldType, value, splitRegex, expectedValue) {
		given:
		SnField field = new SnField(id: FIELD_ID, fieldType: fieldType, multiValued: true)
		Map<String, String> valueProviderParameters = [
			(SplitParamSnIndexerValueProcessor.SPLIT_PARAM): Boolean.TRUE.toString(),
			(SplitParamSnIndexerValueProcessor.SPLIT_REGEX_PARAM): splitRegex
		]
		SnIndexerFieldWrapper fieldWrapper = new DefaultSnIndexerFieldWrapper(field: field, valueProviderParameters: valueProviderParameters)

		when:
		Object processedValue = valueProcessor.process(indexerContext, fieldWrapper, value)

		then:
		processedValue == expectedValue

		where:
		testId | fieldType          | value            | splitRegex || expectedValue
		1      | SnFieldType.STRING | null             | "\\/"      || null
		2      | SnFieldType.STRING | newList("")      | "\\/"      || newList("")
		3      | SnFieldType.STRING | newList("a b")   | "\\/"      || newList("a b")
		4      | SnFieldType.STRING | newList("a/b")   | "\\/"      || newList("a", "b")
		5      | SnFieldType.TEXT   | null             | "\\/"      || null
		6      | SnFieldType.TEXT   | newList("")      | "\\/"      || newList("")
		7      | SnFieldType.TEXT   | newList("a b")   | "\\/"      || newList("a b")
		8      | SnFieldType.TEXT   | newList("a/b")   | "\\/"      || newList("a", "b")
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
