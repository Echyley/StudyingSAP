/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundservices.facade

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod

import static de.hybris.platform.outboundservices.facade.OutboundBatchRequestPartDTO.OutboundBatchRequestPartDTOBuilder.outboundBatchRequestPartDTO

@UnitTest
class OutboundBatchRequestPartDTOUnitTest extends JUnitPlatformSpecification {
    public static final String TEST_ENTITY_TYPE = 'myItem'
    public static final HttpEntity<Object> TEST_HTTP_ENTITY = new HttpEntity([:])
    public static final String TEST_CHANGE_ID = "9"

    @Test
    def 'exception is thrown when creating DTO with null #param'() {
        when:
        outboundBatchRequestPartDTO()
                .withHttpEntity(entity)
                .withItemType(itemType)
                .withRequestType(requestType)
                .build();

        then:
        def e = thrown IllegalArgumentException
        e.message == "$param cannot be null"

        where:
        param         | entity           | itemType         | requestType     | changeId
        "httpEntity"  | null             | TEST_ENTITY_TYPE | HttpMethod.POST | TEST_CHANGE_ID
        "itemType"    | TEST_HTTP_ENTITY | null             | HttpMethod.POST | TEST_CHANGE_ID
        "requestType" | TEST_HTTP_ENTITY | TEST_ENTITY_TYPE | null            | TEST_CHANGE_ID
        "changeId"    | TEST_HTTP_ENTITY | TEST_ENTITY_TYPE | HttpMethod.POST | null
    }

    @Test
    def "builder creates batch request DTO successfully when all required parameters are provided"() {
        given:
        def requestDTO = outboundBatchRequestPartDTO()
                .withHttpEntity(TEST_HTTP_ENTITY)
                .withItemType(TEST_ENTITY_TYPE)
                .withRequestType(HttpMethod.POST)
                .withChangeId(TEST_CHANGE_ID)
                .build();

        expect:
        with(requestDTO) {
            getHttpEntity() == TEST_HTTP_ENTITY
            getItemType() == TEST_ENTITY_TYPE
            getRequestType() == HttpMethod.POST
            getChangeID() == TEST_CHANGE_ID
        }
    }

    @Test
    def "two outbound batch request part DTO's are equal"() {
        given:
        def item1 = defaultItem()
        def item2 = defaultItem()

        expect:
        item1 == item1
        item1 == item2
    }

    @Test
    def "two outbound batch request part DTO's are not equal when #condition"() {
        given:
        def item1 = defaultItem()

        expect:
        item1 != item2

        where:
        condition                  | item2
        "httpEntity is different"  | defaultItem(httpEntity: new HttpEntity(['a': 'testValue']))
        "itemType is different"    | defaultItem(itemType: "differentType")
        "requestType is different" | defaultItem(requestType: HttpMethod.PUT)
        "changeId is different"    | defaultItem(changeId: "differentChangeId")
    }

    @Test
    def "two outbound batch request part DTO's have the same hashcode"() {
        given:
        def item1 = defaultItem()
        def item2 = defaultItem()

        expect:
        item1.hashCode() == item2.hashCode()
    }

    @Test
    def "two outbound batch request part DTO's have different hashcode when #condition is different"() {
        given:
        def item1 = defaultItem()

        expect:
        item1.hashCode() != item2.hashCode()

        where:
        condition                  | item2
        "httpEntity is different"  | defaultItem(httpEntity: new HttpEntity(['a': 'testValue']))
        "itemType is different"    | defaultItem(itemType: "differentType")
        "requestType is different" | defaultItem(requestType: HttpMethod.PUT)
        "changeId is different"    | defaultItem(changeId: "differentChangeId")
    }

    @Test
    def 'toString contains the class attributes in the item'() {
        given:
        def item1 = defaultItem()

        expect:
        with(item1.toString()) {
            contains TEST_HTTP_ENTITY.toString()
            contains TEST_ENTITY_TYPE.toString()
            contains HttpMethod.POST.toString()
            contains TEST_CHANGE_ID
        }
    }

    OutboundBatchRequestPartDTO defaultItem(Map customValues = [:]) {
        def dtoProperties = defaultDtoAttributes()
        dtoProperties.putAll customValues
        dto dtoProperties
    }

    OutboundBatchRequestPartDTO dto(Map attrs) {
        outboundBatchRequestPartDTO()
                .withHttpEntity(attrs.httpEntity)
                .withItemType(attrs.itemType)
                .withRequestType(attrs.requestType)
                .withChangeId(attrs.changeId)
                .build()
    }

    Map defaultDtoAttributes() {
        [
                httpEntity : TEST_HTTP_ENTITY,
                itemType   : TEST_ENTITY_TYPE,
                requestType: HttpMethod.POST,
                changeId   : TEST_CHANGE_ID
        ]
    }
}
