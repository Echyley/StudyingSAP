/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.search

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Shared

@UnitTest
class RootItemSearchResultUnitTest extends JUnitPlatformSpecification {

    @Shared
    def ITEM = Stub(ItemModel)
    @Shared
    def RESULT = RootItemSearchResult.createFrom([ITEM])

    @Test
    def "search result is empty when instantiating it with a #referencePathToRoot collection"() {
        given:
        def result = RootItemSearchResult.createFrom(referencePathToRoot)

        expect:
        result.refPathExecutionResult.empty

        where:
        referencePathToRoot << [null, []]
    }

    @Test
    def 'search result does not leak reference'() {
        given:
        def referencePathToRoot = ['some path']
        def result = RootItemSearchResult.createFrom(referencePathToRoot)

        when:
        referencePathToRoot.clear()

        then:
        !result.refPathExecutionResult.empty
    }

    @Test
    def "has reference path to root is #expected when the collection is #referencePathToRoot"() {
        given:
        def result = RootItemSearchResult.createFrom(referencePathToRoot)

        expect:
        result.hasAnyObjectInRefPathExecutionResult() == expected

        where:
        referencePathToRoot | expected
        ["some path"]       | true
        [null]              | true
        []                  | false
        null                | false
    }

    @Test
    def "getting root items is #expected when the collection is #referencePathToRoot"() {
        given:
        def result = RootItemSearchResult.createFrom(referencePathToRoot)

        expect:
        result.getRootItems() == expected

        where:
        referencePathToRoot     | expected
        null                    | []
        [null]                  | []
        []                      | []
        ["some path"]           | []
        [ITEM, Stub(ItemModel)] | referencePathToRoot
    }

    @Test
    def "getting root items does not leak reference"() {
        given:
        def result = RootItemSearchResult.createFrom([ITEM])

        when:
        result.getRootItems().clear()

        then:
        !result.getRootItems().empty
    }

    @Test
    def "equals is #expected when #condition"() {
        expect:
        (RESULT == anotherResult) == expected

        where:
        condition                                       | anotherResult                                  | expected
        'the results are the same result object'        | RESULT                                         | true
        'different result, but same collection content' | RootItemSearchResult.createFrom([ITEM])        | true
        'one result is null'                            | null                                           | false
        'one result has different collection content'   | RootItemSearchResult.createFrom(["some path"]) | false
        'one result is another object type'             | new Object()                                   | false
    }

    @Test
    def "hashcodes being equal is #expected when #condition"() {
        expect:
        (RESULT.hashCode() == anotherResult.hashCode()) == expected

        where:
        condition                                       | anotherResult                                  | expected
        'the results are the same result object'        | RESULT                                         | true
        'different result, but same collection content' | RootItemSearchResult.createFrom([ITEM])        | true
        'one result has different collection content'   | RootItemSearchResult.createFrom(["some path"]) | false
        'one result is another object type'             | new Object()                                   | false
    }
}
