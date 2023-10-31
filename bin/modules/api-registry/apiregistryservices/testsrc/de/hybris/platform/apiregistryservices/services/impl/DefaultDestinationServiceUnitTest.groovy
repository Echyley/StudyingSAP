package de.hybris.platform.apiregistryservices.services.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.apiregistryservices.dao.DestinationDao
import de.hybris.platform.apiregistryservices.model.DestinationTargetModel
import de.hybris.platform.apiregistryservices.model.ExposedDestinationModel
import de.hybris.platform.apiregistryservices.model.ExposedOAuthCredentialModel

import de.hybris.platform.testframework.JUnitPlatformSpecification
import de.hybris.platform.webservicescommons.model.OAuthClientDetailsModel
import org.junit.Test
import spock.lang.Shared

@UnitTest
class DefaultDestinationServiceUnitTest extends JUnitPlatformSpecification {
	private static final String OAUTH_CLIENT_DETAIL= 'clientDetail'
	private static final String OAUTH_CREDENTIAL_ID = 'oAuthCredentialId'
	private static final String FIRST_DESTINATION_TARGET_ID = 'targetId_1'
	private static final String SECOND_DESTINATION_TARGET_ID = 'targetId_2'
	private static final String FIRST_DESTINATION_ID = 'destination_1'
	private static final String SECOND_DESTINATION_ID = 'destination_2'

	def destinationDao = Stub DestinationDao

	def oauthClientDetail = oAuthClientDetailsModel(OAUTH_CLIENT_DETAIL)

	@Shared
	def exposedOAuthCredential_1 = exposedOAuthCredentialModel(OAUTH_CREDENTIAL_ID)
	@Shared
	def exposedOAuthCredential_2 = exposedOAuthCredentialModel(OAUTH_CREDENTIAL_ID)

	@Shared
	def firstDestinationTarget = Stub(DestinationTargetModel) {
		getId() >> FIRST_DESTINATION_TARGET_ID
	}
	@Shared
	def secondDestinationTarget = Stub(DestinationTargetModel) {
		getId() >> SECOND_DESTINATION_TARGET_ID
	}

	@Shared
	def destination_1 = exposedDestinationModel(FIRST_DESTINATION_ID)
	@Shared
	def destination_2 = exposedDestinationModel(SECOND_DESTINATION_ID)

	def destinationService = new DefaultDestinationService()

	def setup() {
		exposedOAuthCredential_1.setOAuthClientDetails(oauthClientDetail)
		exposedOAuthCredential_2.setOAuthClientDetails(oauthClientDetail)
		destination_1.setCredential(exposedOAuthCredential_1)

	}

	@Test
	def "expect #result credential(s) to be deleted when #description" () {
		given:
		destinationDao.getDestinationsByDestinationTargetId(FIRST_DESTINATION_TARGET_ID) >> destinationsByTarget
		destinationDao.findAllDestinationsByCredentialId(OAUTH_CREDENTIAL_ID) >> destinationsByCredential
		destinationService.setDestinationDao(destinationDao)

		and:
		destination_2.setDestinationTarget(destinationTargetForSecondDestination)
		destination_2.setCredential(credentialForSecondDestiantion)

		when:
		def credentials = destinationService.getDeletableCredentialsByDestinationTargetId(FIRST_DESTINATION_TARGET_ID)

		then:
		credentials.size() == result
		where:
		result | destinationsByTarget           | destinationsByCredential       | destinationTargetForSecondDestination | credentialForSecondDestiantion | description
		0      | [destination_1]                | [destination_1, destination_2] | secondDestinationTarget               | exposedOAuthCredential_1       | "credential is used for different destination with different destination target"
		1      | [destination_1, destination_2] | [destination_1, destination_2] | firstDestinationTarget                | exposedOAuthCredential_1       | "credential is used for different destination with same destination target"
		2      | [destination_1, destination_2] | [destination_1, destination_2] | firstDestinationTarget                | exposedOAuthCredential_2       | "different credential is used for different destination with different destination target"
	}

	private ExposedDestinationModel exposedDestinationModel(final String destinationId){
		def model = new ExposedDestinationModel()
		model.setId(destinationId)
		model.setDestinationTarget(firstDestinationTarget)
		model
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
