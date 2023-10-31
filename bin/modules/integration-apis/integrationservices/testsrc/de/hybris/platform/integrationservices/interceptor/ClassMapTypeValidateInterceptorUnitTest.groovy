/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.interceptor

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.model.order.OrderModel
import de.hybris.platform.integrationservices.model.IntegrationObjectClassAttributeModel
import de.hybris.platform.integrationservices.model.IntegrationObjectClassModel
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.servicelayer.interceptor.InterceptorContext
import de.hybris.platform.servicelayer.interceptor.InterceptorException
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class ClassMapTypeValidateInterceptorUnitTest extends JUnitPlatformSpecification {
	private static final def IO_CODE = 'anyIO'

	private static final def NO_MATTER_ATTRIBUTE = "NoMatter"
	private static final def PRIMITIVE_MAP_ATTRIBUTE_1 = "mapPrimitiveKeyValue1"
	private static final def PRIMITIVE_MAP_ATTRIBUTE_5 = "mapPrimitiveKeyValue5"
	private static final def PRIMITIVE_MAP_ATTRIBUTE_6 = "mapPrimitiveKeyValue6"
	private static final def PRIMITIVE_MAP_ATTRIBUTE_7 = "mapPrimitiveKeyValue7"
	private static final def NO_PRIMITIVE_MAP_VALUE = "mapObjectValue"
	private static final def NO_PRIMITIVE_MAP_KEY = "mapNoPrimitiveKey"
	private static final def COMPOSED_ATTRIBUTE_NAME = "order"
	private static final def COLLECTION_ATTRIBUTE_NAME = "collectionType"
	private static final def PRIMITIVE_ATTRIBUTE_NAME = "primitiveType"
	private static final def NON_GENERIC_MAP_ATTRIBUTE = "mapNonGenericValue"

	private static final def COMPOSED_READ_METHOD_NAME = "getOrder"
	private static final def PRIMITIVE_MAP_READ_METHOD_1 = "mapPrimitiveKeyValue1"
	private static final def PRIMITIVE_MAP_READ_METHOD_2 = "mapPrimitiveKeyValue2"
	private static final def PRIMITIVE_MAP_READ_METHOD_3 = "mapPrimitiveKeyValue3"
	private static final def PRIMITIVE_MAP_READ_METHOD_4 = "mapPrimitiveKeyValue4"

	private static final def COLLECTION_READ_METHOD_NAME = "getCollectionType"
	private static final def PRIMITIVE_READ_METHOD_NAME = "getPrimitiveType"
	private static final def NO_PRIMITIVE_MAP_METHOD_VALUE = "getMapObjectValue"
	private static final def NO_PRIMITIVE_MAP_METHOD_KEY = "getMapNoPrimitiveKey"
	private static final def NON_GENERIC_MAP_METHOD = "getMapNonGenericValue"

	private static final def NOT_FOUND = "NotFound"

	private static final def IO_MODEL = new IntegrationObjectModel(code: IO_CODE)
	private static final def CLASS_MODEL = new IntegrationObjectClassModel(type: TestPojo, integrationObject: IO_MODEL)

	def interceptor = new ClassMapTypeValidateInterceptor()

	@Test
	def "#condition is allowed"() {
		when:
		interceptor.onValidate IntegrationObjectClassAttributeModel(attribute, method), Stub(InterceptorContext)

		then:
		noExceptionThrown()

		where:
		condition                     | attribute                 | method
		'map of primitives method'    | NO_MATTER_ATTRIBUTE       | PRIMITIVE_MAP_READ_METHOD_1
		'map of primitives method'    | NO_MATTER_ATTRIBUTE       | PRIMITIVE_MAP_READ_METHOD_2
		'map of primitives method'    | NO_MATTER_ATTRIBUTE       | PRIMITIVE_MAP_READ_METHOD_3
		'map of primitives method'    | NO_MATTER_ATTRIBUTE       | PRIMITIVE_MAP_READ_METHOD_4
		'map of primitives attribute' | PRIMITIVE_MAP_ATTRIBUTE_1 | null
		'map of primitives attribute' | PRIMITIVE_MAP_ATTRIBUTE_5 | null
		'map of primitives attribute' | PRIMITIVE_MAP_ATTRIBUTE_6 | null
		'map of primitives attribute' | PRIMITIVE_MAP_ATTRIBUTE_7 | null
		'primitive type'              | NO_MATTER_ATTRIBUTE       | PRIMITIVE_READ_METHOD_NAME
		'Composed type'               | NO_MATTER_ATTRIBUTE       | COMPOSED_READ_METHOD_NAME
		'collection type'             | NO_MATTER_ATTRIBUTE       | COLLECTION_READ_METHOD_NAME
		'not found'                   | NO_MATTER_ATTRIBUTE       | NOT_FOUND
		'primitive type'              | PRIMITIVE_ATTRIBUTE_NAME  | null
		'Composed type'               | COMPOSED_ATTRIBUTE_NAME   | null
		'collection type'             | COLLECTION_ATTRIBUTE_NAME | null
		'not found'                   | NOT_FOUND                 | null
	}

	@Test
	def "exception is thrown for #condition"() {
		when:
		interceptor.onValidate IntegrationObjectClassAttributeModel(attribute, method), Stub(InterceptorContext)

		then:
		def e = thrown InterceptorException
		e.message.contains "Map type attribute [$attribute] must have primitive key and value type"

		where:
		condition                  | attribute                 | method
		'non-primitive key type'   | NO_MATTER_ATTRIBUTE       | NO_PRIMITIVE_MAP_METHOD_KEY
		'non-primitive value type' | NO_MATTER_ATTRIBUTE       | NO_PRIMITIVE_MAP_METHOD_VALUE
		'non-primitive key type'   | NO_PRIMITIVE_MAP_KEY      | null
		'non-primitive value type' | NO_PRIMITIVE_MAP_VALUE    | null
		'non-generic map'          | NO_MATTER_ATTRIBUTE       | NON_GENERIC_MAP_METHOD
		'non-generic map'          | NON_GENERIC_MAP_ATTRIBUTE | null
	}

	def IntegrationObjectClassAttributeModel(String attrName, String readMethod) {
		new IntegrationObjectClassAttributeModel(integrationObjectClass: CLASS_MODEL, attributeName: attrName, readMethod: readMethod)
	}

	private static class TestPojo {
		private Map<String, Integer> mapPrimitiveKeyValue1
		private Map<BigInteger, Date> mapPrimitiveKeyValue2
		private Map<Day, Day> mapPrimitiveKeyValue3
		private Map<BigDecimal, Long> mapPrimitiveKeyValue4
		private Map<Number, Short> mapPrimitiveKeyValue5
		private Map<Boolean, Character> mapPrimitiveKeyValue6
		private Map<Locale, Locale> mapPrimitiveKeyValue7

		private Map<String, Object> mapObjectValue
		private Map<Calendar, String> mapNoPrimitiveKey

		private Collection<String> collectionType
		private String primitiveType
		private OrderModel order

		Map<String, Integer> getMapPrimitiveKeyValue1() { mapPrimitiveKeyValue1 }
		Map<BigInteger, Date> getMapPrimitiveValue2() { mapPrimitiveKeyValue2 }
		Map<Day, Day> getMapPrimitiveKeyValue3() { mapPrimitiveKeyValue3 }
		Map<BigDecimal, Long> getMapPrimitiveKeyValue4() { mapPrimitiveKeyValue4 }
		Map<Number, Short> getMapPrimitiveKeyValue5() { mapPrimitiveKeyValue5 }
		Map<Boolean, Character> getMapPrimitiveKeyValue6() { mapPrimitiveKeyValue6 }
		Map<Locale, Locale> getMapPrimitiveKeyValue7() { mapPrimitiveKeyValue7 }

		Collection<String> getMapPrimitiveValue1() { collectionType }
		Map<String, Object> getMapObjectValue() { mapObjectValue }
		Map<Calendar, String> getMapNoPrimitiveKey() { mapNoPrimitiveKey }

		Map getMapNonGenericValue() { mapPrimitiveKeyValue1 }
		String getPrimitiveType() { primitiveType }
		OrderModel getOrder() { order }
	}

	private static enum Day {
		SUNDAY, MONDAY, TUESDAY, WEDNESDAY,
		THURSDAY, FRIDAY, SATURDAY
	}
}
