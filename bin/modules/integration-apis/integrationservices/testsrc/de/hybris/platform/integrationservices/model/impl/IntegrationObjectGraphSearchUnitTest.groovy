package de.hybris.platform.integrationservices.model.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.model.IntegrationObjectDescriptor
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class IntegrationObjectGraphSearchUnitTest extends JUnitPlatformSpecification {

    @Test
    def "findReferencedTypes returns empty set when the input descriptor is not in the integration object"()
    {
        given:
        def node = typeDescriptor('Root')
        def io = integrationObject([node])

        and:
        def someOtherType = typeDescriptor('NotInIO')

        and:
        def search = new IntegrationObjectGraphSearch(io)

        expect:
        search.findTypesRelatedTo(someOtherType.itemCode).empty
    }

    @Test
    def "findReferencedTypes returns input when the descriptor is the only descriptor in the integration object"()
    {
        given:
        def theOnlyType = typeDescriptor('Root')
        def io = integrationObject([theOnlyType])

        and:
        def search = new IntegrationObjectGraphSearch(io)

        expect:
        search.findTypesRelatedTo(theOnlyType.itemCode) == [theOnlyType] as Set
    }

    @Test
    def "finds #result when integration object graph is Parent -> Child and search is for #itemCode"()
    {
        given:
        def child = typeDescriptor('Child')
        def parent = typeDescriptor('Parent', [child])
        def io = integrationObject([parent, child])

        and:
        def search = new IntegrationObjectGraphSearch(io)

        when:
        def searchResult = search.findTypesRelatedTo(itemCode).collect {
            it.itemCode
        } as Set

        then:
        searchResult == (result as Set)

        where:
        itemCode | result
        'Parent' | ['Parent', 'Child']
        'Child'  | ['Child']
    }

    @Test
    def "finds #result when integration object graph has parent and multiple children and search is for #itemCode"()
    {
        given:
        def childOne = typeDescriptor('Child1')
        def childTwo = typeDescriptor('Child2')
        def parent = typeDescriptor('Parent', [childOne, childTwo])
        def io = integrationObject([parent, childOne, childTwo])

        and:
        def search = new IntegrationObjectGraphSearch(io)

        when:
        def searchResult = search.findTypesRelatedTo(itemCode).collect {
            it.itemCode
        } as Set

        then:
        searchResult == (result as Set)

        where:
        itemCode | result
        'Parent' | ['Parent', 'Child1', 'Child2']
        'Child1' | ['Child1']
        'Child2' | ['Child2']
    }

    @Test
    def "finds #result when integration object graph has several generations and search is for #itemCode"()
    {
        given:
        def childOne = typeDescriptor('Child1')
        def childTwo = typeDescriptor('Child2')
        def parent = typeDescriptor('Parent', [childOne, childTwo])
        def grandparent = typeDescriptor('Grandparent', [parent])
        def io = integrationObject([grandparent, parent, childOne, childTwo])

        and:
        def search = new IntegrationObjectGraphSearch(io)

        when:
        def searchResult = search.findTypesRelatedTo(itemCode).collect {
            it.itemCode
        } as Set

        then:
        searchResult == (result as Set)

        where:
        itemCode      | result
        'Grandparent' | ['Grandparent', 'Parent', 'Child1', 'Child2']
        'Parent'      | ['Parent', 'Child1', 'Child2']
        'Child1'      | ['Child1']
        'Child2'      | ['Child2']
    }

    @Test
    def "finds #result when integration object graph is cyclic and search is for #itemCode"()
    {
        given:
        def nodeOne = typeDescriptor('Node1')
        def nodeTwo = typeDescriptor('Node2', [nodeOne])
        defineEdges(nodeOne, [nodeTwo])
        def io = integrationObject([nodeOne, nodeTwo])

        and:
        def search = new IntegrationObjectGraphSearch(io)

        when:
        def searchResult = search.findTypesRelatedTo(itemCode).collect {
            it.itemCode
        } as Set

        then:
        searchResult == (result as Set)

        where:
        itemCode | result
        'Node1'  | ['Node1', 'Node2']
        'Node2'  | ['Node1', 'Node2']
    }

    @Test
    def "finds #result when integration object graph is three-way cyclic and search is for #itemCode"()
    {
        given:
        def nodeOne = typeDescriptor('Node1')
        def nodeTwo = typeDescriptor('Node2', [nodeOne])
        def nodeThree = typeDescriptor('Node3', [nodeTwo])
        defineEdges(nodeOne, [nodeThree])

        def io = integrationObject([nodeOne, nodeTwo, nodeThree])

        and:
        def search = new IntegrationObjectGraphSearch(io)

        when:
        def searchResult = search.findTypesRelatedTo(itemCode).collect {
            it.itemCode
        } as Set

        then:
        searchResult == (result as Set)

        where:
        itemCode | result
        'Node1'  | ['Node1', 'Node2', 'Node3']
        'Node2'  | ['Node1', 'Node2', 'Node3']
        'Node3'  | ['Node1', 'Node2', 'Node3']
    }

    private TypeDescriptor typeDescriptor(String name)
    {
        Stub(TypeDescriptor) {
            getItemCode() >> name
        }
    }

    private TypeDescriptor typeDescriptor(String name, List<TypeDescriptor> edges)
    {
        def type = typeDescriptor(name)
        defineEdges(type, edges)
        type
    }

    private void defineEdges(type, List<TypeDescriptor> edges)
    {
        def attributes = edges.collect { desc ->
            Stub(TypeAttributeDescriptor) {
                getTypeDescriptor() >> type
                getAttributeType() >> desc
            }
        }
        type.getAttributes() >> attributes
    }

    private IntegrationObjectDescriptor integrationObject(List<TypeDescriptor> types)
    {
        Stub(IntegrationObjectDescriptor) {
            getItemTypeDescriptors() >> (types as Set)
            contains(_) >> { List args ->
                types.contains(args[0])
            }
        }
    }
}
