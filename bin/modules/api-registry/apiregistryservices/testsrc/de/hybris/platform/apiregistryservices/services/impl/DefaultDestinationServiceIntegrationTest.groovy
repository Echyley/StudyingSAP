/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.apiregistryservices.services.impl

import de.hybris.platform.apiregistryservices.services.DestinationService
import de.hybris.platform.servicelayer.ServicelayerSpockSpecification
import org.junit.Test
import de.hybris.bootstrap.annotations.IntegrationTest

import javax.annotation.Resource

@IntegrationTest
class DefaultDestinationServiceIntegrationTest extends ServicelayerSpockSpecification {
    private static final def DESTINATION_TARGET_ID = 'template_default'
    private static final def CONSUMED_CERTIFICATE_CREDENTIAL_ID = 'consumed_certificate_credential'
    private static final def CONSUMED_OAUTH_CREDENTIAL_ID = 'consumed_oauth_credential'
    private static final def EXPOSED_OATH_CREDENTIAL_ID = 'exposed_oauth_credential'

    def credentialIdList = [CONSUMED_CERTIFICATE_CREDENTIAL_ID, CONSUMED_OAUTH_CREDENTIAL_ID, EXPOSED_OATH_CREDENTIAL_ID]

    @Resource
    private DestinationService destinationService

    def setup() {
        importCsv("/test/destinationServiceIntegrationTest.impex", "UTF-8")
    }

    @Test
    def "getAllDestinationsByCredentialId finds #result destination(s) by credential ID #credentialId used by #result destination(s)" () {
        expect:
        result == destinationService.getAllDestinationsByCredentialId(credentialId).size()

        where:
        result | credentialId
        1      | CONSUMED_CERTIFICATE_CREDENTIAL_ID
        1      | CONSUMED_OAUTH_CREDENTIAL_ID
        2      | EXPOSED_OATH_CREDENTIAL_ID
        0      | "notExistId"
    }

    @Test
    def "getDeletableCredentialsByDestinationTargetId find the expected deletable credentials for a given destination target ID" () {

        when:
        def credentials = destinationService.getDeletableCredentialsByDestinationTargetId(DESTINATION_TARGET_ID)

        then:
        credentials.size() == 3
        with(credentials[0]) {
            credentialIdList.contains(getId())
        }
        with(credentials[1]) {
            credentialIdList.contains(getId())
        }
        with(credentials[2]) {
            credentialIdList.contains(getId())
        }
    }
}
