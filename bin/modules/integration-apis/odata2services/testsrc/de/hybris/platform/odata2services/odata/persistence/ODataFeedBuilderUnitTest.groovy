/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.odata2services.odata.persistence

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.odata2services.odata.persistence.utils.ODataEntryBuilder
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

import static de.hybris.platform.odata2services.odata.persistence.ODataFeedBuilder.oDataFeedBuilder

@UnitTest
class ODataFeedBuilderUnitTest extends JUnitPlatformSpecification {

    @Test
    def "entries list is empty by default"() {
        given:
        def feed = oDataFeedBuilder().build()

        expect:
        feed.entries.isEmpty()
    }

    @Test
    def "builder handles null for entries and sets an empty list"() {
        given:
        def feed = oDataFeedBuilder().withEntries(null).build()

        expect:
        feed.entries.isEmpty()
    }

    @Test
    def "entries list is immutable"() {
        given:
        def entries = [new ODataEntryBuilder().build()]
        def feed = oDataFeedBuilder().withEntries(entries).build()

        when:
        feed.entries.clear()

        then:
        thrown UnsupportedOperationException
    }

    @Test
    def "builder sets metadata inline count equals to entries.size"() {
        given:
        def entries = [new ODataEntryBuilder().build()]

        when:
        def feed = oDataFeedBuilder().withEntries(entries).build()

        then:
        feed.getEntries().containsAll entries
        feed.getFeedMetadata().getInlineCount().equals entries.size()
    }
}
