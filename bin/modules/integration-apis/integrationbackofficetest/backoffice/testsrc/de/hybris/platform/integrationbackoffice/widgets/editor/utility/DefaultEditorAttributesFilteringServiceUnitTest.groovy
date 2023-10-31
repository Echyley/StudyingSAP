/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.editor.utility

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.model.type.AtomicTypeModel
import de.hybris.platform.core.model.type.AttributeDescriptorModel
import de.hybris.platform.core.model.type.CollectionTypeModel
import de.hybris.platform.core.model.type.ComposedTypeModel
import de.hybris.platform.core.model.type.MapTypeModel
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.ReadService
import de.hybris.platform.integrationbackoffice.widgets.modeling.utility.DefaultEditorAttributesFilteringService
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class DefaultEditorAttributesFilteringServiceUnitTest extends JUnitPlatformSpecification {
	private static final def PARENT_TYPE = new ComposedTypeModel()
	private static final def ATOMIC_TYPE = new AtomicTypeModel()
	private static final def MAP_TYPE = new MapTypeModel()
	private static final def COLLECTION_TYPE = new CollectionTypeModel()
	private static final def COMPOSED_TYPE = new ComposedTypeModel()

	def readService = Stub(ReadService) {
		isComplexType(ATOMIC_TYPE) >> false
		isComplexType(MAP_TYPE) >> false
		isComplexType(COLLECTION_TYPE) >> false
		isComplexType(COMPOSED_TYPE) >> true
		isAtomicType("AtomicType") >> true
		isMapType("MapType") >> true
		isCollectionType("CollectionType") >> true
	}

	def editorAttributesFilteringService = new DefaultEditorAttributesFilteringService(readService)

	@Test
	def "#attrType #isOrIsNot filtered for tree."() {
		given:
		readService.getAttributesForType(PARENT_TYPE) >> [attribute]

		expect:
		editorAttributesFilteringService.filterAttributesForTree(PARENT_TYPE).size() == expected

		where:
		attrType                | isOrIsNot | attribute              | expected
		"Blacklisted attribute" | "is"      | blacklistedAttribute() | 0
		"Primitive attribute"   | "is"      | atomicAttribute()      | 0
		"Complex attribute"     | "is not"  | complexAttribute()     | 1
	}

	@Test
	def "#attrType #isOrIsNot filtered for attributes map."() {
		given:
		readService.getAttributesForType(PARENT_TYPE) >> [attribute]

		expect:
		editorAttributesFilteringService.filterAttributesForAttributesMap(PARENT_TYPE).size() == expected

		where:
		attrType                | isOrIsNot | attribute              | expected
		"Blacklisted attribute" | "is"      | blacklistedAttribute() | 0
		"Atomic attribute"      | "is not"  | atomicAttribute()      | 1
		"Map attribute"         | "is not"  | mapAttribute()         | 1
		"Collection attribute"  | "is not"  | collectionAttribute()  | 1
		"Complex attribute"     | "is not"  | complexAttribute()     | 1
	}

	private AttributeDescriptorModel blacklistedAttribute() {
		Stub(AttributeDescriptorModel) {
			getQualifier() >> "pk"
			getAttributeType() >> COMPOSED_TYPE
		}
	}

	private AttributeDescriptorModel atomicAttribute() {
		Stub(AttributeDescriptorModel) {
			getQualifier() >> "someQualifier"
			getAttributeType() >> ATOMIC_TYPE
		}
	}

	private AttributeDescriptorModel mapAttribute() {
		Stub(AttributeDescriptorModel) {
			getQualifier() >> "someQualifier"
			getAttributeType() >> MAP_TYPE
		}
	}

	private AttributeDescriptorModel collectionAttribute() {
		Stub(AttributeDescriptorModel) {
			getQualifier() >> "someQualifier"
			getAttributeType() >> COLLECTION_TYPE
		}
	}

	private AttributeDescriptorModel complexAttribute() {
		Stub(AttributeDescriptorModel) {
			getQualifier() >> "someQualifier"
			getAttributeType() >> COMPOSED_TYPE
		}
	}
}
