/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundservices.client.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel
import de.hybris.platform.apiregistryservices.model.ConsumedOAuthCredentialModel
import de.hybris.platform.apiregistryservices.model.ExposedOAuthCredentialModel
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Unroll

import static de.hybris.platform.apiregistryservices.services.impl.DefaultApiRegistryClientService.CLIENT_SCOPE

@UnitTest
class ConsumedOAuth2ResourceDetailsGeneratorUnitTest extends JUnitPlatformSpecification {

    private static final def TEST_CLIENT = "test_client"
    private static final def TEST_PASSWORD = "test_password"
    private static final def TEST_URL = "test_url"
    private static final def TEST_SCOPE_1 = "test_scope_1"
    private static final def TEST_SCOPE_2 = "test_scope_2"
    private def consumedOAuth2ResourceDetailsGenerator = new ConsumedOAuth2ResourceDetailsGenerator()

    @Test
    def "isApplicable should be #applicable when credential is of type #credentialType"() {

        expect:
        consumedOAuth2ResourceDetailsGenerator.isApplicable(credentialType) == applicable

        where:
        credentialType                     | applicable
        new ConsumedOAuthCredentialModel() | true
        new ExposedOAuthCredentialModel()  | false
    }

    @Test
    def "createResourceDetails returns a correctly formed resource details model where additional property is #key = #value."() {
        given:
        def destination = new ConsumedDestinationModel(
                credential: defaultCredentialModel(),
                additionalProperties: [(key): value as String])

        when:
        def details = consumedOAuth2ResourceDetailsGenerator.createResourceDetails destination

        then:
        with(details) {
            getClientId() == TEST_CLIENT
            getClientSecret() == TEST_PASSWORD
            getAccessTokenUri() == TEST_URL
            getScope() == expectedScope as List
        }

        where:
        key          | value                         | expectedScope
        null         | null                          | []
        'some-key'   | 'some-value'                  | []
        CLIENT_SCOPE | null                          | []
        CLIENT_SCOPE | "$TEST_SCOPE_1,$TEST_SCOPE_2" | [TEST_SCOPE_1, TEST_SCOPE_2]
    }

    private static def defaultCredentialModel() {
        new ConsumedOAuthCredentialModel(
                clientId: TEST_CLIENT,
                clientSecret: TEST_PASSWORD,
                OAuthUrl: TEST_URL)
    }
}
