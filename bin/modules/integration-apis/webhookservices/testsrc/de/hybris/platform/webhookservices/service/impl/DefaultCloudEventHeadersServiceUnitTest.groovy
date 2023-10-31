/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webhookservices.service.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.outboundservices.event.EventType
import de.hybris.platform.outboundservices.event.impl.DefaultEventType
import de.hybris.platform.testframework.JUnitPlatformSpecification
import de.hybris.platform.webhookservices.service.CloudEventConfigurationService
import org.junit.Test
import spock.lang.Issue

@UnitTest
@Issue("https://cxjira.sap.com/browse/IAPI-5118")
class DefaultCloudEventHeadersServiceUnitTest extends JUnitPlatformSpecification {

    private static final def SAP_COMMERCE_NAMESPACE = "sap.cx.commerce"
    private static final def INTEGRATION_OBJECT_CODE = "IntegrationObjectCode"
    private static final def INTEGRATION_KEY_VALUE = "integration|key"
    private static final def SAP_PASSPORT = "1234-5678-abcd"
    private static final def CLOUD_EVENT_SOURCE_REGION = "ccv2Region"
    private static final def CLOUD_EVENT_SOURCE_INSTANCE_ID = "instanceId"
    private static final def CREATED_OPERATION = "Created"
    private static final def CLOUD_EVENT_SOURCE = "/${CLOUD_EVENT_SOURCE_REGION}/${SAP_COMMERCE_NAMESPACE}/${CLOUD_EVENT_SOURCE_INSTANCE_ID}"
    private static final def CLOUD_EVENT_SOURCE_WO_INSTANCE_ID = "/${CLOUD_EVENT_SOURCE_REGION}/${SAP_COMMERCE_NAMESPACE}"
    private static final def CLOUD_EVENT_TYPE_VERSION = "v1"
    private static final def CLOUD_EVENT_TYPE = "${SAP_COMMERCE_NAMESPACE}.${INTEGRATION_OBJECT_CODE}.${CREATED_OPERATION}.${CLOUD_EVENT_TYPE_VERSION}"
    private static final def CLOUD_EVENT_SPECVERSION = "1.0"
    private static final def CE_ID_HEADER = "ce-id"
    private static final def CE_TYPE_HEADER = "ce-type"
    private static final def CE_SOURCE_HEADER = "ce-source"
    private static final def CE_SUBJECT_HEADER = "ce-subject"
    private static final def CE_SPECVERSION_HEADER = "ce-specversion"
    private static final def CE_SAPPASSPORT_HEADER = "ce-sappassport"

    def cloudEventConfigurationService = Stub(CloudEventConfigurationService) {
        getCloudEventSourceRegion() >> CLOUD_EVENT_SOURCE_REGION
        getCloudEventSpecVersion() >> CLOUD_EVENT_SPECVERSION
        getCloudEventTypeVersion() >> CLOUD_EVENT_TYPE_VERSION
    }
    def cloudEventHeadersService = new DefaultCloudEventHeadersService(cloudEventConfigurationService)

    @Test
    def "can generate CloudEvent headers #description"() {
        given:
        cloudEventConfigurationService.getCloudEventSourceInstanceId() >> instanceId
        when:
        def cloudEventHeaders = cloudEventHeadersService.generateCloudEventHeaders(INTEGRATION_OBJECT_CODE, integrationKey, DefaultEventType.CREATED, sapPassport)
        then:
        with(cloudEventHeaders) {
            getFirst(CE_ID_HEADER) != null
            getFirst(CE_SAPPASSPORT_HEADER) == sapPassport
            getFirst(CE_TYPE_HEADER) == CLOUD_EVENT_TYPE
            getFirst(CE_SOURCE_HEADER) == expectCloudEventSource
            getFirst(CE_SPECVERSION_HEADER) == CLOUD_EVENT_SPECVERSION
            getFirst(CE_SUBJECT_HEADER) == integrationKey
        }
        where:
        description               | integrationKey        | sapPassport  | instanceId                     | expectCloudEventSource
        "with full information"   | INTEGRATION_KEY_VALUE | SAP_PASSPORT | CLOUD_EVENT_SOURCE_INSTANCE_ID | CLOUD_EVENT_SOURCE
        "without Instance Id"     | INTEGRATION_KEY_VALUE | SAP_PASSPORT | ""                             | CLOUD_EVENT_SOURCE_WO_INSTANCE_ID
        "without integration key" | null                  | SAP_PASSPORT | CLOUD_EVENT_SOURCE_INSTANCE_ID | CLOUD_EVENT_SOURCE
        "without sap passport"    | INTEGRATION_KEY_VALUE | null         | CLOUD_EVENT_SOURCE_INSTANCE_ID | CLOUD_EVENT_SOURCE
    }

    @Test
    def "null CloudEventConfigurationService fails precondition"() {
        when:
        new DefaultCloudEventHeadersService(null)
        then:
        thrown IllegalArgumentException
    }

    @Test
    def "null #description fails precondition"() {
        when:
        cloudEventHeadersService.generateCloudEventHeaders(ioCode, INTEGRATION_KEY_VALUE, event, SAP_PASSPORT)
        then:
        thrown IllegalArgumentException

        where:
        description             | ioCode                  | event
        "integrationObjectCode" | null                    | Stub(EventType)
        "operation"             | INTEGRATION_OBJECT_CODE | null
    }

}
