/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.processor.handler.delete

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.inboundservices.persistence.ItemDeletionService
import de.hybris.platform.integrationservices.search.ItemNotFoundException
import de.hybris.platform.odata2services.odata.persistence.ItemLookupRequest
import de.hybris.platform.odata2services.odata.persistence.ItemLookupRequestFactory
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.apache.olingo.odata2.api.commons.HttpStatusCodes
import org.apache.olingo.odata2.api.processor.ODataContext
import org.apache.olingo.odata2.api.uri.info.DeleteUriInfo
import org.junit.Test
import spock.lang.Shared

@UnitTest
class DeleteHandlerUnitTest extends JUnitPlatformSpecification {
    @Shared
    def uriInfo = Stub(DeleteUriInfo)
    @Shared
    def oDataContext = Stub(ODataContext)
    @Shared
    def request = Stub ItemLookupRequest

    def itemLookupFactory = Stub(ItemLookupRequestFactory) {
        create(uriInfo, oDataContext) >> request
    }
    def itemDeletionService = Stub ItemDeletionService

    def handler = new DeleteHandler(itemLookupRequestFactory: itemLookupFactory, itemDeletionService: itemDeletionService)

    @Test
    def "item is deleted"() {
        when:
        def response = handler.handle deleteParam()

        then:
        with(response) {
            status == HttpStatusCodes.OK
            entity == ''
        }
    }

    @Test
    def "fails to delete item"() {
        given:
        itemDeletionService.deleteItem(request) >> { throw new ItemNotFoundException(null) }

        when:
        handler.handle deleteParam()

        then:
        thrown(ItemNotFoundException)
    }

    def deleteParam() {
        DefaultDeleteParam.deleteParam()
            .withUriInfo(uriInfo)
            .withContext(oDataContext)
            .build()
    }
}
