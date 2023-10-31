/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.model.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.model.order.OrderModel
import de.hybris.platform.integrationservices.model.IntegrationObjectClassAttributeModel
import de.hybris.platform.integrationservices.model.IntegrationObjectClassModel
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class ClassMapDescriptorUnitTest extends JUnitPlatformSpecification {
	private static final def IO_CODE = 'anyIO'

	private static final def NO_MATTER_ATTRIBUTE = "NoMatter"
	private static final def MAP_ATTRIBUTE_NAME = "mapStringValue"
	private static final def COMPOSED_ATTRIBUTE_NAME = "order"
	private static final def COLLECTION_ATTRIBUTE_NAME = "collectionType"
	private static final def PRIMITIVE_ATTRIBUTE_NAME = "primitiveType"

	private static final def COMPOSED_READ_METHOD_NAME = "getOrder"
	private static final def MAP_READ_METHOD_NAME = "getMapStringValue"
	private static final def COLLECTION_READ_METHOD_NAME = "getCollectionType"
	private static final def PRIMITIVE_READ_METHOD_NAME = "getPrimitiveType"

	private static final def NOT_FOUND = "NotFound"

	private static final def IO_MODEL = new IntegrationObjectModel(code: IO_CODE)
	private static final def CLASS_MODEL = new IntegrationObjectClassModel(type: TestPojo, integrationObject: IO_MODEL)

	@Test
	def 'cannot be instantiated with null PojoIntrospector'() {
		when:
		new ClassMapDescriptor(null)

		then:
		def e = thrown IllegalArgumentException
		e.message == "PojoIntrospector cannot be null"
	}

	@Test
	def "cannot be instantiated for a read method that has #typeName return type"() {
		given:
		def introspector = PojoIntrospector(attrName, method)

		when:
		new ClassMapDescriptor(introspector)

		then:
		def e = thrown IllegalArgumentException
		e.message.contains 'MapType'

		where:
		method                       | attrName                  | typeName
		COLLECTION_READ_METHOD_NAME  | NO_MATTER_ATTRIBUTE       | 'collection method'
		COMPOSED_READ_METHOD_NAME    | NO_MATTER_ATTRIBUTE       | 'composed method'
		PRIMITIVE_READ_METHOD_NAME   | NO_MATTER_ATTRIBUTE       | 'primitive method'
		NOT_FOUND                    | NO_MATTER_ATTRIBUTE       | "not found method"
		null                         | COLLECTION_ATTRIBUTE_NAME | "collection attribute"
		null                         | COMPOSED_ATTRIBUTE_NAME   | "composed attribute"
		null                         | PRIMITIVE_ATTRIBUTE_NAME  | "primitive attribute"
		null                         | NOT_FOUND                 | "not found attribute"
	}

	@Test
	def "provides type descriptor for that #desc with primitive type"() {
		given:
		def introspector = PojoIntrospector(attrName, method)

		when:
		def mapDescriptor = new ClassMapDescriptor(introspector)

		then:
		mapDescriptor.keyType instanceof PrimitiveClassTypeDescriptor
		mapDescriptor.valueType instanceof PrimitiveClassTypeDescriptor

		where:
		method                    | attrName            |desc
		MAP_READ_METHOD_NAME      | NO_MATTER_ATTRIBUTE |'read method has map type'
		null                      | MAP_ATTRIBUTE_NAME  |'attribute has map type'
	}

	def PojoIntrospector(String attrName, String readMethod) {
		def classAttr = new IntegrationObjectClassAttributeModel(integrationObjectClass: CLASS_MODEL, attributeName: attrName, readMethod: readMethod)
		new PojoIntrospector(classAttr)
	}

	private static class TestPojo {
		private Collection<String> collectionType
		private Map<String, Object> mapObjectValue
		private Map<Object, Integer> mapObjectKey
		private Map<String, String> mapStringValue
		private String primitiveType
		private OrderModel order

		Collection<String> getCollectionType() { collectionType }

		Map<String, Object> getMapObjectValue() { mapObjectValue }

		Map<Object, Integer> getMapObjectKey() { mapObjectKey }

		Map<String, String> getMapStringValue() { mapStringValue }

		String getPrimitiveType() { primitiveType }

		OrderModel getOrder() { order }
	}
}
