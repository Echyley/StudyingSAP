/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.search.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.integrationservices.model.DescriptorFactory
import de.hybris.platform.integrationservices.model.IntegrationObjectDescriptor
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.integrationservices.model.ReferencePath
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class DefaultRootItemSearchServiceUnitTest extends JUnitPlatformSpecification  {
    private static final Long ROOT_ITEM_PK = 3L
    private static final Long ROOT_ITEM_PK2 = 5L
    
    def descriptorFactory = Stub DescriptorFactory

    def searchService = new DefaultRootItemSearchService(descriptorFactory)

    @Test
    def 'fails to instantiate the search service when DescriptorFactory is null'() {
        when:
        new DefaultRootItemSearchService(null)

        then:
        def e = thrown IllegalArgumentException
        e.message == 'descriptorFactory cannot be null'
    }

    @Test
    def 'findRoots for IO returns a result with no reference path to root when #msg'() {
        given:
        descriptorFactory.createIntegrationObjectDescriptor(_ as IntegrationObjectModel) >> descriptor

        expect:
        !searchService.findRoots(Stub(ItemModel), Stub(IntegrationObjectModel)).hasAnyObjectInRefPathExecutionResult()

        where:
        descriptor                                      | msg
        null                                            | "IntegrationObjectDescriptor is null"
        integrationObjectDescriptor()                   | "TypeDescriptor is not found"
        integrationObjectDescriptor(typeDescriptor([])) | "TypeDescriptor is found with no attribute reference to any root"
    }

    @Test
    def "findRoots returns a result with no reference path to root when #msg"() {
        expect:
        !searchService.findRoots(Stub(ItemModel), descriptor as TypeDescriptor).hasAnyObjectInRefPathExecutionResult()

        where:
        descriptor         | msg
        null               | "TypeDescriptor is null"
        typeDescriptor([]) | "TypeDescriptor is found with no attribute reference to any root"
    }

    @Test
    def 'findRoots for IO returns a result that has reference path to root when item has an attribute reference to a null root'() {
        given: "type descriptor has a reference to a null root"
        def typeDescriptor = typeDescriptor([referencePath(null)])

        and: "IntegrationObject has a Type Descriptor for the provided item"
        def io = Stub(IntegrationObjectModel)
        descriptorFactory.createIntegrationObjectDescriptor(io) >> integrationObjectDescriptor(typeDescriptor)

        expect:
        searchService.findRoots(Stub(ItemModel), io).hasAnyObjectInRefPathExecutionResult()
    }

    @Test
    def "findRoots returns a result that has reference path to root when item has an attribute reference to a null root"() {
        given:
        def typeDescriptor = typeDescriptor([referencePath(null)])

        expect:
        searchService.findRoots(Stub(ItemModel), typeDescriptor).hasAnyObjectInRefPathExecutionResult()
    }

    @Test
    def "findRoots for IO returns a result with root item when item has an attribute reference to a root"() {
        given:
        def root1 = itemModel(ROOT_ITEM_PK)
        def typeDescriptor = typeDescriptor([referencePath(root1)])

        and: "IntegrationObject has a Type Descriptor for the provided item"
        def io = Stub(IntegrationObjectModel)
        descriptorFactory.createIntegrationObjectDescriptor(io) >> integrationObjectDescriptor(typeDescriptor)

        when:
        def result = searchService.findRoots(Stub(ItemModel), io)

        then:
        result.rootItems == [root1]
    }

    @Test
    def "findRoots returns a result with root item when item has an attribute reference to a root"() {
        given:
        def root1 = itemModel(ROOT_ITEM_PK)
        def typeDescriptor = typeDescriptor([referencePath(root1)])

        when:
        def result = searchService.findRoots(Stub(ItemModel), typeDescriptor)

        then:
        result.rootItems == [root1]
    }

    @Test
    def "findRoots for IO returns result with root items when item has multiple attribute references roots via different properties"() {
        given:
        def root1 = itemModel(ROOT_ITEM_PK)
        def root2 = itemModel(ROOT_ITEM_PK2)
        def typeDescriptor = typeDescriptor([referencePath(root1), referencePath(root2)])

        and: "IntegrationObject has a Type Descriptor for the provided item"
        def io = Stub(IntegrationObjectModel)
        descriptorFactory.createIntegrationObjectDescriptor(io) >> integrationObjectDescriptor(typeDescriptor)

        when:
        def result = searchService.findRoots(Stub(ItemModel), io)

        then:
        def roots = result.rootItems
        roots.size() == 2
        roots.containsAll([root1, root2])
    }

    @Test
    def "findRoots returns result with root items when item has multiple attribute references roots via different properties"() {
        given:
        def root1 = itemModel(ROOT_ITEM_PK)
        def root2 = itemModel(ROOT_ITEM_PK2)
        def typeDescriptor = typeDescriptor([referencePath(root1), referencePath(root2)])

        when:
        def result = searchService.findRoots(Stub(ItemModel), typeDescriptor)

        then:
        def roots = result.rootItems
        roots.size() == 2
        roots.containsAll([root1, root2])
    }

    private IntegrationObjectDescriptor integrationObjectDescriptor() {
        Stub(IntegrationObjectDescriptor) {
            getTypeDescriptor(_ as ItemModel) >> Optional.empty()
        }
    }

    private IntegrationObjectDescriptor integrationObjectDescriptor(typeDescriptor) {
        Stub(IntegrationObjectDescriptor) {
            getTypeDescriptor(_ as ItemModel) >> Optional.of(typeDescriptor)
        }
    }

    private TypeDescriptor typeDescriptor(List<ReferencePath> rootPaths) {
        Stub(TypeDescriptor) {
            getPathsToRoot() >> rootPaths
        }
    }

    private ItemModel itemModel(Long pk) {
        Stub(ItemModel) {
            getPk() >> de.hybris.platform.core.PK.fromLong(pk)
        }
    }

    private ReferencePath referencePath(final ItemModel item) {
        Stub(ReferencePath) {
            execute(_ as ItemModel) >> [item]
        }
    }
}
