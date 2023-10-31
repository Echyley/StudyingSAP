/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundsync.job.impl.info

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.model.ReferencePath
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Specification
import spock.lang.Unroll

@UnitTest
class RootTypeExpressionGeneratorUnitTest extends JUnitPlatformSpecification {
    def generator = new RootTypeExpressionGenerator()

    @Test
    @Unroll
    def "expression is null when #condition"() {
        expect:
        !generator.generateExpression(itemType)

        where:
        itemType             | condition
        null                 | 'item type is null'
        typeDescriptor(null) | 'root item cannot be navigated from the provided item type'
    }

    @Test
    @Unroll
    def "expression has root item type info when #condition"() {
        given:
        def itemType = typeDescriptor pathToRoot

        expect:
        generator.generateExpression(itemType) == expr

        where:
        pathToRoot                        | expr                                             | condition
        ''                                | '#{itemtype}'                                    | 'provided item type is the root item type'
        'owner'                           | '#{owner?.itemtype}'                             | 'root item can be navigated from the provided item type'
        'category.catalogVersion.catalog' | '#{category?.catalogVersion?.catalog?.itemtype}' | 'root item can be navigated from deeply nested item type'
    }

    @Test
    @Unroll
    def "expression checks for possible emptiness of #attribute attribute"() {
        given:
        def itemType = typeDescriptor pathToRoot

        expect:
        generator.generateExpression(itemType) == expr

        where:
        pathToRoot                  | expr                                                                                                             | attribute
        'products[0]'               | '#{(products?.empty ? null : products[0]?.itemtype)}'                                                            | 'collection'
        'categories[0].products[0]' | '#{(categories?.empty ? null : (categories[0]?.products?.empty ? null : categories[0]?.products[0]?.itemtype))}' | 'collection of collections'
    }

    @Test
    def 'chooses expression with shortest final length'() {
        given: 'shorter reference path becomes longer path when processed'
        def shorterRefPath = 'products[0]'
        def longerRefPath = 'order.product'
        def itemType = typeDescriptor([shorterRefPath, longerRefPath])

        expect:
        generator.generateExpression(itemType) == '#{order?.product?.itemtype}'
    }

    TypeDescriptor typeDescriptor(String path) {
        typeDescriptor([path])
    }

    TypeDescriptor typeDescriptor(List<String> paths = []) {
        Stub(TypeDescriptor) {
            getPathsToRoot() >> paths.collect({ refPath(it) })
        }
    }

    ReferencePath refPath(String path) {
        Stub(ReferencePath) {
            toPropertyPath() >> path
            length() >> (path ? path.count('.') + 1 : 0)
        }
    }
}
