/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.apiregistryservices.services.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.apiregistryservices.dao.CredentialDao
import de.hybris.platform.apiregistryservices.model.ExposedOAuthCredentialModel
import de.hybris.platform.testframework.JUnitPlatformSpecification
import de.hybris.platform.webservicescommons.model.OAuthClientDetailsModel
import org.junit.Test
import spock.lang.Shared

@UnitTest
class DefaultCredentialServiceUnitTest extends JUnitPlatformSpecification {

	private static final String CREDENTIAL_ID_1 = 'credentialId_1'
	private static final String CREDENTIAL_ID_2 = 'credentialId_2'

	private static final String CLIENT_DETAILS_ID_1 = 'clientDetailId_1'
	private static final String CLIENT_DETAILS_ID_2 = 'clientDetailId_2'

	def credentialDao = Stub CredentialDao
	def credentialService = new DefaultCredentialService(credentialDao:credentialDao)

	@Shared
	def oauthCredential_1 = exposedOAuthCredentialModel(CREDENTIAL_ID_1)
	@Shared
	def oauthCredential_2 = exposedOAuthCredentialModel(CREDENTIAL_ID_2)

	@Shared
	def clientDetail_1 = oAuthClientDetailsModel(CLIENT_DETAILS_ID_1)
	@Shared
	def clientDetail_2 = oAuthClientDetailsModel(CLIENT_DETAILS_ID_2)

	@Test
	def 'getDeletableClientDetailsByCredentials expects #count #description' () {
		given:
		credentialDao.getAllExposedOAuthCredentialsByClientId(CLIENT_DETAILS_ID_1) >> oauthByCredential

		and:
		oauthCredential_1.setOAuthClientDetails(clientDetail_1)
		oauthCredential_2.setOAuthClientDetails(clientDetail_2)

		when:
		def clientDetail = credentialService.getDeletableClientDetailsByCredentials(credentialListForCheck as Set)

		then:
		clientDetail.size() == count

		where:
		count | oauthByCredential                      | credentialListForCheck                 | description
		2     | [oauthCredential_1, oauthCredential_2] | [oauthCredential_1, oauthCredential_2] | "client details will be deleted when they are not shared by credentials"
		1     | [oauthCredential_1]                    | [oauthCredential_1]                    | "client details will be deleted when they are not shared by credential"
		0     | [oauthCredential_1, oauthCredential_2] | [oauthCredential_1]                    | "client details will be deleted when they are shared by credential not be deleted"
	}

	private static ExposedOAuthCredentialModel exposedOAuthCredentialModel(final String id) {
		def model = new ExposedOAuthCredentialModel()
		model.setId(id)
		model
	}

	private static OAuthClientDetailsModel oAuthClientDetailsModel (final String clientId) {
		def model = new OAuthClientDetailsModel()
		model.setClientId(clientId)
		model.setClientSecret("secret")
		model.setOAuthUrl("url")
		model
	}
}
