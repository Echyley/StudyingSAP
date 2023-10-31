/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.apiregistryservices.strategies.impl

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.apiregistryservices.dao.EventConfigurationDao
import de.hybris.platform.apiregistryservices.model.AbstractCredentialModel
import de.hybris.platform.apiregistryservices.model.EndpointModel
import de.hybris.platform.apiregistryservices.services.CredentialService
import de.hybris.platform.apiregistryservices.services.DestinationTargetService
import de.hybris.platform.servicelayer.ServicelayerSpockSpecification
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import de.hybris.platform.webservicescommons.model.OAuthClientDetailsModel
import org.junit.Test

import javax.annotation.Resource

@IntegrationTest
class DefaultDestinationTargetDeletionStrategyIntegrationTest extends ServicelayerSpockSpecification {

    private static final String TEST_DESTINATION_TARGET_ID_1 = "test_destination_target_1"
    private static final String TEST_DESTINATION_TARGET_ID_2 = "test_destination_target_2"
    private static final String TEST_EXPOSED_OAUTH_CREDENTIAL_1 = "test_exposed_oauth_credential_1"
    private static final String TEST_CONSUMED_OAUTH_CREDENTIAL_1 = "test_consumed_oauth_credential_1"
    private static final String TEST_CONSUMED_OAUTH_CREDENTIAL_2 = "test_consumed_oauth_credential_2"
    private static final String TEST_OAUTH_CLIENT_DETAIL_1 = "test_oauth_client_detail_1"
    private static final String TEST_OAUTH_CLIENT_DETAIL_2 = "test_oauth_client_detail_2"
    private static final String TEST_ENDPOINT_ID_1 = "test_endpoint_1"
    private static final String TEST_ENDPOINT_ID_2 = "test_endpoint_2"

    private static final int EXPECT_RESULT_COUNT = 1

    @Resource
    private DefaultDestinationTargetDeletionStrategy destinationTargetDeletionStrategy

    @Resource
    private DestinationTargetService destinationTargetService

    @Resource
    private FlexibleSearchService flexibleSearchService

    @Resource
    private CredentialService credentialService

    @Resource
    private EventConfigurationDao eventConfigurationDao

    @Test
    def 'successfully delete Destination Target and related entities'() {
        given: "import destination target and related event configurations, consumed and exposed destinations with same endpoint, credentials and client details"
        importCsv("/test/destinationTargetDeletionStrategy-test.impex", "UTF-8")

        and: "get test destination target 1"
        def templateDestinationTarget = destinationTargetService.getDestinationTargetById(TEST_DESTINATION_TARGET_ID_1)

        when:
        destinationTargetDeletionStrategy.deleteDestinationTarget(templateDestinationTarget)

        then: 'related destinations were deleted'
        destinationTargetDeletionStrategy.destinationService.getDestinationsByDestinationTargetId(TEST_DESTINATION_TARGET_ID_1).empty

        and: 'credential was deleted when it used by one destination or multiple destinations with same destination target'
        findCredentialsById(TEST_EXPOSED_OAUTH_CREDENTIAL_1).empty
        findCredentialsById(TEST_CONSUMED_OAUTH_CREDENTIAL_1).empty

        and: 'related event configurations were deleted'
        eventConfigurationDao.findEventConfigsByDestinationTargetId(TEST_DESTINATION_TARGET_ID_1).empty

        and: "endpoint was deleted"
        findEndpointsById(TEST_ENDPOINT_ID_1).empty

        and: "client details were deleted"
        findClientDetailsById(TEST_OAUTH_CLIENT_DETAIL_1).empty

        when:
        destinationTargetService.getDestinationTargetById(TEST_DESTINATION_TARGET_ID_1)

        then: "destination target was deleted"
        thrown ModelNotFoundException
    }

    @Test
    def 'when deleting a destination target, its related entities will not be deleted if they are referenced by other destination targets or entities'() {
        given: 'import destination target and consumed and exposed destinations with same endpoint, credentials and client details'
        importCsv("/test/destinationTargetDeletionStrategy-can-not-be-deleted-test.impex", "UTF-8")

        and:
        def templateDestinationTarget = destinationTargetService.getDestinationTargetById(TEST_DESTINATION_TARGET_ID_2)

        when:
        destinationTargetDeletionStrategy.deleteDestinationTarget(templateDestinationTarget)

        then: 'related destinations were deleted'
        destinationTargetDeletionStrategy.destinationService.getDestinationsByDestinationTargetId(TEST_DESTINATION_TARGET_ID_2).empty

        and: 'credential was not deleted when linked destination(s) were not deleted'
        EXPECT_RESULT_COUNT == findCredentialsById(TEST_CONSUMED_OAUTH_CREDENTIAL_2).size()

        and: 'endpoint was not deleted when referenced by destinations used by other destination target'
        EXPECT_RESULT_COUNT == findEndpointsById(TEST_ENDPOINT_ID_2).size()

        and: 'client detail was not deleted when it was referenced by other credentials in use'
        EXPECT_RESULT_COUNT == findClientDetailsById(TEST_OAUTH_CLIENT_DETAIL_2).size()
    }

    private List<AbstractCredentialModel> findCredentialsById(final String credentialId) {
        def sampleCredentialModel = new AbstractCredentialModel()
        sampleCredentialModel.setId(credentialId)
        return flexibleSearchService.getModelsByExample(sampleCredentialModel)
    }

    private List<EndpointModel> findEndpointsById(final String endpointId) {
        def endpointModel = new EndpointModel()
        endpointModel.setId(endpointId)
        return flexibleSearchService.getModelsByExample(endpointModel)
    }

    private List<OAuthClientDetailsModel> findClientDetailsById(final String clientId) {
        def sampleClientDetailsModel = new OAuthClientDetailsModel()
        sampleClientDetailsModel.setClientId(clientId)
        return flexibleSearchService.getModelsByExample(sampleClientDetailsModel)
    }
}
