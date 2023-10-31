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
import de.hybris.platform.searchservices.indexer.service.impl.ValueCoercionSnIndexerValueProcessor
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test


@UnitTest
public class ValueCoercionSnIndexerValueProcessorSpec extends JUnitPlatformSpecification {

	static final String FIELD_ID = "field"

	static final String LANGUAGE_ID = "en"

	static final String QUALIFIER_TYPE_ID = "qualifierType"
	static final String QUALIFIER_ID = "qualifier"

	SnIndexerContext indexerContext = Mock()

	ValueCoercionSnIndexerValueProcessor valueProcessor

	def setup() {
		valueProcessor = new ValueCoercionSnIndexerValueProcessor()
	}

	@Test
	def "Coerce value on single-valued field #testId: value=#value, expectedValue=#expectedValue"(testId, value, expectedValue) {
		given:
		SnField field = new SnField(id: FIELD_ID, multiValued: false)
		SnIndexerFieldWrapper fieldWrapper = new DefaultSnIndexerFieldWrapper(field: field)

		when:
		Object processedValue = valueProcessor.process(indexerContext, fieldWrapper, value)

		then:
		processedValue == expectedValue

		where:
		testId | value              || expectedValue
		1      | null               || null
		2      | "a"                || "a"
		3      | newList()          || null
		4      | newList(null)      || null
		5      | newList("a")       || "a"
		6      | newList(null, "a") || "a"
	}

	@Test
	def "Fail to coerce value on single-valued field #testId: value=#value"(testId, value) {
		given:
		SnField field = new SnField(id: FIELD_ID, multiValued: false)
		SnIndexerFieldWrapper fieldWrapper = new DefaultSnIndexerFieldWrapper(field: field)

		when:
		valueProcessor.process(indexerContext, fieldWrapper, value)

		then:
		thrown(SnIndexerException)

		where:
		testId | value
		1      | newMap()
		2      | newMap(LANGUAGE_ID, "a")
		3      | newMap(LANGUAGE_ID, newList("a"))
		4      | newMap(QUALIFIER_ID, "a")
		5      | newMap(QUALIFIER_ID, newList("a"))
	}

	@Test
	def "Coerce value on multi-valued field #testId: value=#value, expectedValue=#expectedValue"(testId, value, expectedValue) {
		given:
		SnField field = new SnField(id: FIELD_ID, multiValued: true)
		SnIndexerFieldWrapper fieldWrapper = new DefaultSnIndexerFieldWrapper(field: field)

		when:
		Object processedValue = valueProcessor.process(indexerContext, fieldWrapper, value)

		then:
		processedValue == expectedValue

		where:
		testId | value              || expectedValue
		1      | null               || null
		2      | "a"                || newList("a")
		3      | newList()          || null
		4      | newList(null)      || null
		5      | newList("a")       || newList("a")
		6      | newList(null, "a") || newList("a")
	}

	@Test
	def "Fail to coerce value on multi-valued field #testId: value=#value"(testId, value) {
		given:
		SnField field = new SnField(id: FIELD_ID, multiValued: true)
		SnIndexerFieldWrapper fieldWrapper = new DefaultSnIndexerFieldWrapper(field: field)

		when:
		valueProcessor.process(indexerContext, fieldWrapper, value)

		then:
		thrown(SnIndexerException)

		where:
		testId | value
		1      | newMap()
		2      | newMap(LANGUAGE_ID, "a")
		3      | newMap(LANGUAGE_ID, newList("a"))
		4      | newMap(QUALIFIER_ID, "a")
		5      | newMap(QUALIFIER_ID, newList("a"))
	}

	@Test
	def "Coerce value on single-valued localized field #testId: value=#value, expectedValue=#expectedValue"(testId, value, expectedValue) {
		given:
		SnField field = new SnField(id: FIELD_ID, localized: true, multiValued: false)
		SnIndexerFieldWrapper fieldWrapper = new DefaultSnIndexerFieldWrapper(field: field)

		when:
		Object processedValue = valueProcessor.process(indexerContext, fieldWrapper, value)

		then:
		processedValue == expectedValue

		where:
		testId | value                                   || expectedValue
		1      | null                                    || null
		2      | newMap(LANGUAGE_ID, null)               || null
		3      | newMap(LANGUAGE_ID, "a")                || newMap(LANGUAGE_ID, "a")
		4      | newMap(LANGUAGE_ID, newList())          || null
		5      | newMap(LANGUAGE_ID, newList(null))      || null
		6      | newMap(LANGUAGE_ID, newList("a"))       || newMap(LANGUAGE_ID, "a")
		7      | newMap(LANGUAGE_ID, newList(null, "a")) || newMap(LANGUAGE_ID, "a")
	}

	@Test
	def "Fail to coerce value on single-valued localized field #testId: value=#value"(testId, value) {
		given:
		SnField field = new SnField(id: FIELD_ID, localized: true, multiValued: false)
		SnIndexerFieldWrapper fieldWrapper = new DefaultSnIndexerFieldWrapper(field: field)

		when:
		valueProcessor.process(indexerContext, fieldWrapper, value)

		then:
		thrown(SnIndexerException)

		where:
		testId | value
		1      | newList()
		2      | newList("a")
	}

	@Test
	def "Coerce value on multi-valued localized field #testId: value=#value, expectedValue=#expectedValue"(testId, value, expectedValue) {
		given:
		SnField field = new SnField(id: FIELD_ID, localized: true, multiValued: true)
		SnIndexerFieldWrapper fieldWrapper = new DefaultSnIndexerFieldWrapper(field: field)

		when:
		Object processedValue = valueProcessor.process(indexerContext, fieldWrapper, value)

		then:
		processedValue == expectedValue

		where:
		testId | value                                   || expectedValue
		1      | null                                    || null
		2      | newMap(LANGUAGE_ID, null)               || null
		3      | newMap(LANGUAGE_ID, "a")                || newMap(LANGUAGE_ID, newList("a"))
		4      | newMap(LANGUAGE_ID, newList())          || null
		5      | newMap(LANGUAGE_ID, newList(null))      || null
		6      | newMap(LANGUAGE_ID, newList("a"))       || newMap(LANGUAGE_ID, newList("a"))
		6      | newMap(LANGUAGE_ID, newList(null, "a")) || newMap(LANGUAGE_ID, newList("a"))
	}

	@Test
	def "Fail to coerce value on multi-valued localized field #testId: value=#value"(testId, value) {
		given:
		SnField field = new SnField(id: FIELD_ID, localized: true, multiValued: true)
		SnIndexerFieldWrapper fieldWrapper = new DefaultSnIndexerFieldWrapper(field: field)

		when:
		valueProcessor.process(indexerContext, fieldWrapper, value)

		then:
		thrown(SnIndexerException)

		where:
		testId | value
		1      | newList()
		2      | newList("a")
	}

	@Test
	def "Coerce value on single-valued qualified field #testId: value=#value, expectedValue=#expectedValue"(testId, value, expectedValue) {
		given:
		SnField field = new SnField(id: FIELD_ID, qualifierTypeId: QUALIFIER_TYPE_ID, multiValued: false)
		SnIndexerFieldWrapper fieldWrapper = new DefaultSnIndexerFieldWrapper(field: field)

		when:
		Object processedValue = valueProcessor.process(indexerContext, fieldWrapper, value)

		then:
		processedValue == expectedValue

		where:
		testId | value                                    || expectedValue
		1      | null                                     || null
		2      | newMap(QUALIFIER_ID, null)               || null
		3      | newMap(QUALIFIER_ID, "a")                || newMap(QUALIFIER_ID, "a")
		4      | newMap(QUALIFIER_ID, newList())          || null
		5      | newMap(QUALIFIER_ID, newList(null))      || null
		6      | newMap(QUALIFIER_ID, newList("a"))       || newMap(QUALIFIER_ID, "a")
		7      | newMap(QUALIFIER_ID, newList(null, "a")) || newMap(QUALIFIER_ID, "a")
	}

	@Test
	def "Fail to coerce value on single-valued qualified field #testId: value=#value"(testId, value) {
		given:
		SnField field = new SnField(id: FIELD_ID, qualifierTypeId: QUALIFIER_TYPE_ID, multiValued: false)
		SnIndexerFieldWrapper fieldWrapper = new DefaultSnIndexerFieldWrapper(field: field)

		when:
		valueProcessor.process(indexerContext, fieldWrapper, value)

		then:
		thrown(SnIndexerException)

		where:
		testId | value
		1      | newList()
		2      | newList("a")
	}

	@Test
	def "Coerce value on multi-valued qualified field #testId: value=#value, expectedValue=#expectedValue"(testId, value, expectedValue) {
		given:
		SnField field = new SnField(id: FIELD_ID, qualifierTypeId: QUALIFIER_TYPE_ID, multiValued: true)
		SnIndexerFieldWrapper fieldWrapper = new DefaultSnIndexerFieldWrapper(field: field)

		when:
		Object processedValue = valueProcessor.process(indexerContext, fieldWrapper, value)

		then:
		processedValue == expectedValue

		where:
		testId | value                                    || expectedValue
		1      | null                                     || null
		2      | newMap(QUALIFIER_ID, null)               || null
		3      | newMap(QUALIFIER_ID, "a")                || newMap(QUALIFIER_ID, newList("a"))
		4      | newMap(QUALIFIER_ID, newList())          || null
		5      | newMap(QUALIFIER_ID, newList(null))      || null
		6      | newMap(QUALIFIER_ID, newList("a"))       || newMap(QUALIFIER_ID, newList("a"))
		7      | newMap(QUALIFIER_ID, newList(null, "a")) || newMap(QUALIFIER_ID, newList("a"))
	}

	@Test
	def "Fail to coerce value on multi-valued qualified field #testId: value=#value"(testId, value) {
		given:
		SnField field = new SnField(id: FIELD_ID, qualifierTypeId: QUALIFIER_TYPE_ID, multiValued: true)
		SnIndexerFieldWrapper fieldWrapper = new DefaultSnIndexerFieldWrapper(field: field)

		when:
		valueProcessor.process(indexerContext, fieldWrapper, value)

		then:
		thrown(SnIndexerException)

		where:
		testId | value
		1      | newList()
		2      | newList("a")
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
