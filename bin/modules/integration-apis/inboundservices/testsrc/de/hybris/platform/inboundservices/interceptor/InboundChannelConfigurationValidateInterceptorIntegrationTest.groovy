/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.inboundservices.interceptor

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.apiregistryservices.model.BasicCredentialModel
import de.hybris.platform.apiregistryservices.model.DestinationTargetModel
import de.hybris.platform.apiregistryservices.model.EndpointModel
import de.hybris.platform.apiregistryservices.model.ExposedDestinationModel
import de.hybris.platform.apiregistryservices.model.ExposedOAuthCredentialModel
import de.hybris.platform.integrationservices.enums.AuthenticationType
import de.hybris.platform.integrationservices.model.InboundChannelConfigurationModel
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.integrationservices.util.IntegrationTestUtil
import de.hybris.platform.servicelayer.ServicelayerSpockSpecification
import de.hybris.platform.webservicescommons.model.OAuthClientDetailsModel
import org.junit.Test

@IntegrationTest
class InboundChannelConfigurationValidateInterceptorIntegrationTest extends ServicelayerSpockSpecification {

	private static final String TEST_NAME = "ExposedDestinationICCMatchedCredential"
	private static final String IO = "${TEST_NAME}_IO"
	private static final String URL = "http://localhost:9002/test"
	private static final String BASIC_CRED = "${TEST_NAME}_BasicCred"
	private static final String OAUTH_CRED = "${TEST_NAME}_OAuthCred"
	private static final String OAUTH_DETAILS = "${TEST_NAME}_OAuthClientDetails"
	private static final String OAUTH_TYPE = "OAUTH"
	private static final String BASIC_TYPE = "BASIC"
	private static final String ENDPOINT = "${TEST_NAME}_Endpoint"
	private static final String TARGET = "${TEST_NAME}_DestinationTarget"
	private static final String DESTINATION = "${TEST_NAME}_Destination"

	def setup() {
		IntegrationTestUtil.importImpEx(
				'INSERT_UPDATE IntegrationObject; code[unique = true]',
				"                               ; $IO ",
				'INSERT_UPDATE BasicCredential;id[unique=true]; username; password',
				"                                ;$BASIC_CRED ; basic   ; password ",
				'INSERT_UPDATE DestinationTarget;id[unique=true]; destinationChannel(code)[default=DEFAULT]; template',
				"                               ;$TARGET        ;                                          ; true",
				'INSERT_UPDATE Endpoint;id[unique=true]; version; specUrl; specData; name; description',
				"                       ;$ENDPOINT     ; v1     ; $URL   ; e1      ; n1  ; des",
				'INSERT_UPDATE OAuthClientDetails;clientId[unique=true];resourceIds;scope;authorizedGrantTypes;clientSecret;authorities',
				"                                ;$OAUTH_DETAILS;hybris;basic;authorization_code,refresh_token,password,client_credentials;password;ROLE_TRUSTED_CLIENT",
				'INSERT_UPDATE ExposedOAuthCredential;id[unique=true];oAuthClientDetails(clientId);password',
				"                                    ;$OAUTH_CRED;$OAUTH_DETAILS;secret",
		)
	}

	def cleanup() {
		IntegrationTestUtil.remove ExposedDestinationModel, { ed -> ed.id == DESTINATION }
		IntegrationTestUtil.remove DestinationTargetModel, { dt -> dt.id == TARGET }
		IntegrationTestUtil.remove InboundChannelConfigurationModel, { icc -> icc.integrationObject.code == IO }
		IntegrationTestUtil.remove IntegrationObjectModel, { io -> io.code == IO }
		IntegrationTestUtil.remove EndpointModel, { e -> e.id == ENDPOINT }
		IntegrationTestUtil.remove BasicCredentialModel, { bc -> bc.id == BASIC_CRED }
		IntegrationTestUtil.remove OAuthClientDetailsModel, { oa -> oa.clientId == OAUTH_DETAILS }
		IntegrationTestUtil.remove ExposedOAuthCredentialModel, { e -> e.id == OAUTH_CRED }
	}

	@Test
	def "throw exception when authentication type of InboundChannelConfiguration does not match credential type of ExposedDestination"() {
		given: 'Existed ICC and Exposed Destination'
		IntegrationTestUtil.importImpEx(
				'INSERT_UPDATE InboundChannelConfiguration; integrationObject(code)[unique = true]; authenticationType(code)',
				"                                         ; $IO                                   ; $ICCOrignalAuth",
				"INSERT_UPDATE ExposedDestination;id[unique=true]; url ; endpoint(id); additionalProperties; destinationTarget(id)[default=$TARGET]; active; credential(id); inboundChannelConfiguration(integrationObject(code))",
				"                                ;$DESTINATION   ; $URL; $ENDPOINT   ;                     ;                                       ; true  ; $EDAuthType   ; $IO"
		)

		when:
		IntegrationTestUtil.importImpEx(
				'INSERT_UPDATE InboundChannelConfiguration; integrationObject(code)[unique = true]; authenticationType(code)',
				"                                         ; $IO                                   ; $ICCChangedAuth"
		)

		then:
		def e = thrown AssertionError
		e.message.contains "InboundChannelConfiguration does not match assigned credential type"

		where:
		EDAuthType | ICCOrignalAuth | ICCChangedAuth
		BASIC_CRED | BASIC_TYPE     | OAUTH_TYPE
		OAUTH_CRED | OAUTH_TYPE     | BASIC_TYPE
	}

	@Test
	def "no exception when authentication type of InboundChannelConfiguration matches credential type of ExposedDestination"() {
		given: 'Existed ICC and Exposed Destination'
		IntegrationTestUtil.importImpEx(
				'INSERT_UPDATE InboundChannelConfiguration; integrationObject(code)[unique = true]; authenticationType(code)',
				"                                         ; $IO                                   ; $ICCOrignalAuth",
				"INSERT_UPDATE ExposedDestination;id[unique=true]; url ; endpoint(id); additionalProperties; destinationTarget(id)[default=$TARGET]; active; credential(id); inboundChannelConfiguration(integrationObject(code))",
				"                                ;$DESTINATION   ; $URL; $ENDPOINT   ;                     ;                                       ; true  ; $EDAuthType   ; $IO"
		)

		when:
		IntegrationTestUtil.importImpEx(
				'INSERT_UPDATE InboundChannelConfiguration; integrationObject(code)[unique = true]; authenticationType(code)',
				"                                         ; $IO                                   ; $ICCChangedAuth"
		)

		then:
		noExceptionThrown()

		where:
		EDAuthType | ICCOrignalAuth | ICCChangedAuth
		BASIC_CRED | BASIC_TYPE     | AuthenticationType.BASIC
		OAUTH_CRED | OAUTH_TYPE     | AuthenticationType.OAUTH
	}
}
