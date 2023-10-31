/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webhookservices.service.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.testframework.JUnitPlatformSpecification
import spock.lang.Specification
import org.junit.Test
import spock.lang.Issue
import spock.lang.Unroll;

@UnitTest
@Issue("https://cxjira.sap.com/browse/IAPI-5118")
class DefaultCloudEventConfigurationServiceUnitTest extends JUnitPlatformSpecification {

    private static final def CCV2_SERVICE_API_URL_PROPERTY_KEY = "ccv2.services.api.url.0";
    private static final def CCV2_SERVICE_API_URL_DEFAULT_VALUE = "https://localhost:9002";
    private static final def CCV2_SERVICE_API_URL_REGION_VALUE = "https://www.ccv2Region.com:9002"
    private static final def CCV2_SERVICE_API_URL_WRONG_REGION_VALUE = "https://wwwccv2Regioncom:9002"
    private static final def MACHINE_NAME = "machineName"

    def cloudEventConfigurationService = Spy(DefaultCloudEventConfigurationService)

    @Test
    @Unroll
    def "#description"(){
        given:
        cloudEventConfigurationService.getPropertyValue(CCV2_SERVICE_API_URL_PROPERTY_KEY, CCV2_SERVICE_API_URL_DEFAULT_VALUE) >> propertyValue
        cloudEventConfigurationService.getMachineName() >> machineName

        expect:
        cloudEventConfigurationService.getCloudEventSourceInstanceId() == expecResut

        where:
        description                                                             | propertyValue                           | machineName  | expecResut
        "no instance id when no region configuration and no local machine name" | CCV2_SERVICE_API_URL_DEFAULT_VALUE      | ""           | ""
        "local machine name as instance id when no region configuration "       | CCV2_SERVICE_API_URL_DEFAULT_VALUE      | MACHINE_NAME | MACHINE_NAME
        "region as instance id when configured region properly"                 | CCV2_SERVICE_API_URL_REGION_VALUE       | MACHINE_NAME | "ccv2Region"
        "no instance id when configured region wrong"                           | CCV2_SERVICE_API_URL_WRONG_REGION_VALUE | MACHINE_NAME | ""
    }
    @Test
    def "get CloudEvent source region"() {
        expect:
        cloudEventConfigurationService.getCloudEventSourceRegion() == "ccv2Region"
    }

    @Test
    def "get CloudEvent type version"() {
        expect:
        cloudEventConfigurationService.getCloudEventTypeVersion() == "v1"
    }

    @Test
    def "get CloudEvent specVersion"() {
        expect:
        cloudEventConfigurationService.getCloudEventSpecVersion() == "1.0"
    }
}
