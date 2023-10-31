/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.odata2services.odata.persistence

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.apache.olingo.odata2.api.edm.*
import org.apache.olingo.odata2.api.uri.NavigationPropertySegment
import org.apache.olingo.odata2.api.uri.NavigationSegment
import org.junit.Test
import spock.lang.Unroll

import static de.hybris.platform.odata2services.odata.persistence.ConversionOptions.conversionOptionsBuilder
import static de.hybris.platform.odata2services.odata.persistence.ItemConversionRequest.itemConversionRequestBuilder

@UnitTest
class ItemConversionRequestUnitTest extends JUnitPlatformSpecification {
    @Test
    def "conversionLevel reflects how deep the property is nested from the original request"() {
        given:
        def parent = requestBuilder().build()

        when:
        def child1 = parent.propertyConversionRequest('son', new Object())
        def child2 = parent.propertyConversionRequest('daughter', new Object())
        def grandchild = child1.propertyConversionRequest('child', new Object())

        then:
        parent.conversionLevel == 0
        child1.conversionLevel == 1
        child2.conversionLevel == 1
        grandchild.conversionLevel == 2
    }

    @Test
    @Unroll
    def "isPropertyValueShouldBeConverted is #res when property is #condition"() {
        given:
        def request = requestBuilder().withOptions(options).build()

        expect:
        request.isPropertyValueShouldBeConverted('referenced') == res

        where:
        condition                             | res   | options
        'not in the key/navigation/expand'    | false | conversionOptionsBuilder()
        'next segment in the navigation path' | true  | conversionOptionsBuilder().withNavigationSegment(navigationSegment('referenced'))
        'next segment in the expand path'     | true  | conversionOptionsBuilder().withExpand([[expandSegment('referenced')]])
    }

    @Test
    def 'isPropertyValueShouldBeConverted is true when property is key'() {
        given: 'the property is annotated with Unique'
        def entitySet = Stub(EdmEntitySet) {
            getEntityType() >> Stub(EdmEntityType) {
                getProperty('someProperty') >> Stub(EdmProperty) {
                    getAnnotations() >> Stub(EdmAnnotations) {
                        getAnnotationAttributes() >> [annotation('s:IsUnique', 'true')]
                    }
                }
            }
        }
        def request = requestBuilder().withEntitySet(entitySet).build()

        expect:
        request.isPropertyValueShouldBeConverted('someProperty')
    }

    @Test
    def 'isPropertyValueShouldBeConverted is false when EdmException is thrown in the process'() {
        given: 'the property is annotated with Unique'
        def entitySet = Stub(EdmEntitySet) {
            getEntityType() >> Stub(EdmEntityType) {
                getProperty(_ as String) >> { throw Stub(EdmException) }
            }
        }
        def request = requestBuilder().withEntitySet(entitySet).build()

        expect:
        !request.isPropertyValueShouldBeConverted('someProperty')
    }

    @Test
    @Unroll
    def "isPropertyValueShouldBeConverted is false when conversion level is #level"() {
        given: 'property is next navigation segment and therefore is eligible but the conversion level is high'
        def request = requestBuilder()
                .withOptions(conversionOptionsBuilder().withNavigationSegment(navigationSegment('itemRef')))
                .withConversionLevel(level)
                .build()

        expect:
        !request.isPropertyValueShouldBeConverted('itemRef')

        where:
        level << [20, 21]
    }

    @Test
    @Unroll
    def "request built from another request of conversion level #request.conversionLevel has the fields as original"() {
        given:
        def requestCopy = itemConversionRequestBuilder().from(request).build()

        expect:
        requestCopy.value == request.value
        requestCopy.options == request.options
        requestCopy.conversionLevel == request.conversionLevel

        where:
        request << [requestBuilder().build(), requestBuilder().build().propertyConversionRequest('reference', new Object())]
    }

    @Test
    @Unroll
    def "get property names is #all when property names are #props and navigation properties are #navProps"() {
        given:
        def entitySet = Stub(EdmEntitySet) {
            getEntityType() >> Stub(EdmEntityType) {
                getPropertyNames() >> props
                getNavigationPropertyNames() >> navProps
            }
        }
        def request = requestBuilder().withEntitySet(entitySet).build()

        expect:
        request.allPropertyNames == all

        where:
        props      | navProps   | all
        null       | null       | []
        []         | null       | []
        null       | []         | []
        []         | []         | []
        ['a']      | ['b', 'c'] | ['a', 'b', 'c']
        ['a', 'b'] | ['c']      | ['a', 'b', 'c']
    }

    def requestBuilder() {
        itemConversionRequestBuilder()
                .withEntitySet(entitySet())
                .withValue(Stub(ItemModel))
                .withIntegrationObject("SomeObject")
                .withAcceptLocale(Locale.ENGLISH)
                .withOptions(conversionOptionsBuilder())
    }

    def entitySet() {
        Stub(EdmEntitySet) {
            getEntityType() >> Stub(EdmEntityType) {
                getProperty("referenced") >> Stub(EdmProperty) {
                    getType() >> Stub(EdmType) {
                        getName() >> 'ReferencedItem'
                    }
                }
            }
            getEntityContainer() >> Stub(EdmEntityContainer) {
                getEntitySet('ReferencedItems') >> Stub(EdmEntitySet) {
                    getName() >> 'ReferenceItems'
                }
            }
        }
    }

    def navigationSegment(String name) {
        Stub(NavigationSegment) {
            getNavigationProperty() >> Stub(EdmNavigationProperty) {
                getName() >> name
            }
        }
    }

    def expandSegment(String name) {
        Stub(NavigationPropertySegment) {
            getNavigationProperty() >> Stub(EdmNavigationProperty) {
                getName() >> name
            }
        }
    }

    def annotation(String name, String value) {
        Stub(EdmAnnotationAttribute) {
            getName() >> name
            getText() >> value
        }
    }
}
