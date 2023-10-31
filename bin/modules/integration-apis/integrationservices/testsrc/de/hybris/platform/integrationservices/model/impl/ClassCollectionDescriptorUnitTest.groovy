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
class ClassCollectionDescriptorUnitTest extends JUnitPlatformSpecification {
	private static final def IO_CODE = 'anyIO'
	private static final def NO_MATTER_ATTRIBUTE = 'anyName'
	private static final def COMPOSED_ATTRIBUTE_NAME = "order"
	private static final def SET_ATTRIBUTE_NAME = "setType"
	private static final def MAP_ATTRIBUTE_NAME = "mapType"
	private static final def LIST_ATTRIBUTE_NAME = "listType"
	private static final def COLLECTION_ATTRIBUTE_NAME = "collectionType"
	private static final def PRIMITIVE_ATTRIBUTE_NAME = "primitiveType"

	private static final def COMPOSED_READ_METHOD_NAME = "getOrder"
	private static final def SET_READ_METHOD_NAME = "getSetType"
	private static final def MAP_READ_METHOD_NAME = "getMapType"
	private static final def LIST_READ_METHOD_NAME = "getListType"
	private static final def COLLECTION_READ_METHOD_NAME = "getCollectionType"
	private static final def PRIMITIVE_READ_METHOD_NAME = "getPrimitiveType"

	private static final def IO_MODEL = new IntegrationObjectModel(code: IO_CODE)
	private static final def CLASS_MODEL = new IntegrationObjectClassModel(type: TestPojo, integrationObject: IO_MODEL)

	@Test
	def "cannot be instantiated when PojoIntrospector is null"() {
		when:
		ClassCollectionDescriptor.create(null)

		then:
		thrown IllegalArgumentException
	}

	@Test
	def "null is returned if underlying attribute is not a Collection type"() {
		given:
		def descriptor = ClassCollectionDescriptor(attribute, method)

		expect:
		descriptor.newCollection() == null

		where:
		method                     | attribute
		COMPOSED_READ_METHOD_NAME  | NO_MATTER_ATTRIBUTE
		MAP_READ_METHOD_NAME       | NO_MATTER_ATTRIBUTE
		PRIMITIVE_READ_METHOD_NAME | NO_MATTER_ATTRIBUTE
		null                       | COMPOSED_ATTRIBUTE_NAME
		null                       | MAP_ATTRIBUTE_NAME
		null                       | PRIMITIVE_ATTRIBUTE_NAME
	}

	@Test
	def "collection is a List for #type"() {
		given:
		def descriptor = ClassCollectionDescriptor(attribute, method)

		when:
		def collection = descriptor.newCollection()

		then:
		collection instanceof List
		collection.empty

		where:
		method                      | attribute                 | type
		LIST_READ_METHOD_NAME       | NO_MATTER_ATTRIBUTE       | "List Method"
		COLLECTION_READ_METHOD_NAME | NO_MATTER_ATTRIBUTE       | "Collection Method"
		null                        | COLLECTION_ATTRIBUTE_NAME | "Collection attribute"
		null                        | LIST_ATTRIBUTE_NAME       | "List attribute"
	}

	@Test
	def "collection is a Set"() {
		given:
		def descriptor = ClassCollectionDescriptor(attribute, method)

		when:
		def collection = descriptor.newCollection()

		then:
		collection instanceof Set
		collection.empty

		where:
		method                      | attribute
		SET_READ_METHOD_NAME        | NO_MATTER_ATTRIBUTE
		null                        | SET_ATTRIBUTE_NAME
	}

	def ClassCollectionDescriptor(final String attribute, final String readMethod) {
		ClassCollectionDescriptor.create(PojoIntrospector(attribute, readMethod))
	}

	def PojoIntrospector(final String attribute, final String readMethod) {
		def classAttr = new IntegrationObjectClassAttributeModel(integrationObjectClass: CLASS_MODEL, attributeName: attribute, readMethod: readMethod)
		new PojoIntrospector(classAttr)
	}

	private static class TestPojo {
		private List<String> listType
		private Collection<String> collectionType
		private Map<String, String> mapType
		private Set<Integer> setType
		private String primitiveType
		private OrderModel order

		List<String> getListType() { listType }

		Collection<String> getCollectionType() { collectionType }

		Map<String, String> getMapType() { mapType }

		Set<Integer> getSetType() { setType }

		String getPrimitiveType() { primitiveType }

		OrderModel getOrder() { order }
	}
}
