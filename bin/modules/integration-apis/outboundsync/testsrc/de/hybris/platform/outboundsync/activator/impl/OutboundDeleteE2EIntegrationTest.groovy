/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundsync.activator.impl

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.core.model.user.AddressModel
import de.hybris.platform.core.model.user.CustomerModel
import de.hybris.platform.core.model.user.EmployeeModel
import de.hybris.platform.cronjob.enums.CronJobResult
import de.hybris.platform.cronjob.enums.CronJobStatus
import de.hybris.platform.cronjob.model.CronJobModel
import de.hybris.platform.hmc.model.UserProfileModel
import de.hybris.platform.integrationservices.IntegrationObjectModelBuilder
import de.hybris.platform.integrationservices.util.IntegrationTestUtil
import de.hybris.platform.integrationservices.util.ItemTracker
import de.hybris.platform.outboundservices.service.DeleteRequestSender
import de.hybris.platform.outboundservices.util.TestDeleteRequestSender
import de.hybris.platform.outboundsync.OutboundChannelConfigurationBuilder
import de.hybris.platform.outboundsync.OutboundSyncFeature
import de.hybris.platform.outboundsync.TestOutboundItemConsumer
import de.hybris.platform.outboundsync.activator.OutboundItemConsumer
import de.hybris.platform.outboundsync.job.impl.DefaultFilteringService
import de.hybris.platform.outboundsync.job.impl.OutboundSyncCronJobPerformable
import de.hybris.platform.outboundsync.util.OutboundSyncEssentialData
import de.hybris.platform.servicelayer.ServicelayerSpockSpecification
import de.hybris.platform.servicelayer.cronjob.CronJobService
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import org.springframework.web.client.RestClientResponseException
import spock.lang.AutoCleanup
import spock.lang.Issue
import spock.lang.Shared
import spock.lang.Unroll

import javax.annotation.Resource

import static de.hybris.platform.integrationservices.IntegrationObjectItemAttributeModelBuilder.integrationObjectItemAttribute
import static de.hybris.platform.integrationservices.IntegrationObjectItemModelBuilder.integrationObjectItem
import static de.hybris.platform.integrationservices.IntegrationObjectModelBuilder.integrationObject
import static de.hybris.platform.integrationservices.util.IntegrationTestUtil.condition
import static de.hybris.platform.outboundservices.ConsumedDestinationBuilder.consumedDestinationBuilder
import static de.hybris.platform.outboundsync.OutboundChannelConfigurationBuilder.outboundChannelConfigurationBuilder
import static de.hybris.platform.outboundsync.OutboundSyncStreamConfigurationBuilder.outboundSyncStreamConfigurationBuilder

@IntegrationTest
class OutboundDeleteE2EIntegrationTest extends ServicelayerSpockSpecification {
    private static final def TEST_NAME = 'OutboundDeleteE2E'
    private static final def DESTINATION_ID = "${TEST_NAME}_Destination"
    private static final def CUSTOMER_IO = "${TEST_NAME}_CustomerIO"
    private static final def CHANNEL_CODE = "${TEST_NAME}_OCC"
    private static final def CUSTOMER_UID = "${TEST_NAME}_Customer".toLowerCase()
    private static final def ADDRESS_KEYS = ['addr1', 'addr2']

    @Shared
    @ClassRule
    OutboundSyncEssentialData essentialData = OutboundSyncEssentialData.outboundSyncEssentialData()

    @Rule
    ItemTracker itemTracker = ItemTracker.track(CustomerModel, EmployeeModel)
    @Rule
    TestDeleteRequestSender testDeleteService = new TestDeleteRequestSender()
    @Rule
    TestOutboundItemConsumer testItemChangeConsumer = new TestOutboundItemConsumer()
    @Rule
    OutboundChannelConfigurationBuilder outboundChannelBuilder = outboundChannelConfigurationBuilder()
            .withCode(CHANNEL_CODE)
            .withDeleteSynchronization()
            .withConsumedDestination(consumedDestinationBuilder().withId(DESTINATION_ID))
            .withIntegrationObjectCode CUSTOMER_IO
    @Rule
    IntegrationObjectModelBuilder customerIO = integrationObject().withCode(CUSTOMER_IO)
            .withItem(integrationObjectItem().withCode('Customer').root()
                    .withAttribute(integrationObjectItemAttribute().withName('uid').unique())
                    .withAttribute(integrationObjectItemAttribute('addresses').withReturnItem('Address')))
            .withItem(integrationObjectItem().withCode('Address')
                    .withAttribute(integrationObjectItemAttribute('owner').withReturnItem('Customer').unique())
                    .withAttribute(integrationObjectItemAttribute().withName('publicKey').unique()))
    @AutoCleanup('cleanup')
    def streamBuilder = outboundSyncStreamConfigurationBuilder()
            .withOutboundChannelCode(CHANNEL_CODE)

    @Resource
    private CronJobService cronJobService
    @Resource(name = 'defaultDeleteOutboundSyncService')
    private DefaultDeleteOutboundSyncService outboundSyncService

    @Resource(name = 'outboundSyncFilteringService')
    private DefaultFilteringService filteringService
    @Resource(name = 'outboundSyncCronJobPerformable')
    private OutboundSyncCronJobPerformable outboundSyncCronJobPerformable

    private DeleteRequestSender originalRequestSender
    private OutboundItemConsumer originalItemConsumer
    private OutboundItemConsumer originalFilteringServiceItemConsumer
    private CronJobModel cronJob

    def setup() {
        cronJob = essentialData.outboundCronJob()

        originalItemConsumer = outboundSyncService.outboundItemConsumer
        outboundSyncService.outboundItemConsumer = testItemChangeConsumer

        originalRequestSender = outboundSyncService.deleteRequestSender
        outboundSyncService.deleteRequestSender = testDeleteService

        originalFilteringServiceItemConsumer = filteringService.getOutboundItemConsumer()
        filteringService.outboundItemConsumer = testItemChangeConsumer

        createCustomer()
    }

    def cleanup() {
        outboundSyncService.outboundItemConsumer = originalItemConsumer
        outboundSyncService.deleteRequestSender = originalRequestSender
        filteringService.outboundItemConsumer = originalFilteringServiceItemConsumer
    }

    @Test
    @Issue('https://cxjira.sap.com/browse/IAPI-3466, https://cxjira.sap.com/browse/IAPI-5187')
    // Remove this test when the delete feature is fully implemented
    def 'no delete is sent for an eligible item when delete feature is disabled'() {
        given: 'disable delete feature'
        OutboundSyncFeature.DELETE.disable()

        and: 'there streams for the root and its partOf items in the IO'
        streamBuilder.withItemType('Customer').build()
        streamBuilder.withItemType('Address').build()
        and: 'the customer is deleted'
        deleteCustomer()

        when:
        cronJobService.performCronJob cronJob, true

        then: "the job filters out the delete because the delete feature is disabled"
        condition().eventually {
            assert refresh(cronJob).status == CronJobStatus.FINISHED
            assert testDeleteService.invocations() == 0
            assert testItemChangeConsumer.invocations() == 3
        }

        cleanup: 're-enable delete feature for the other tests'
        OutboundSyncFeature.DELETE.enable()
    }

    @Test
    @Issue('https://cxjira.sap.com/browse/IAPI-3466')
    def 'no deletes sent when delete synchronization is disabled'() {
        given: 'delete synchronization is disabled'
        outboundChannelBuilder.withoutDeleteSynchronization().build()
        and: 'there a change stream for Customer'
        streamBuilder.withItemType('Customer').build()
        and: 'the customer is deleted'
        deleteCustomer()

        when:
        cronJobService.performCronJob cronJob, true

        then: "the job sent no deletes"
        condition().eventually {
            assert refresh(cronJob).status == CronJobStatus.FINISHED
            assert testDeleteService.invocations() == 0
        }
    }

    @Test
    @Issue('https://cxjira.sap.com/browse/IAPI-3466')
    def 'no deletes sent when deleted item is not in the integration object'() {
        given: 'there is a change stream for the UserProfile items'
        streamBuilder.withItemType('UserProfile').build()
        and: 'a UserProfile item that is not present in the IO but related to the root item is deleted'
        deleteCustomerProfile()

        when: 'the outbound sync job is executed'
        cronJobService.performCronJob cronJob, true

        then: 'no deletes sent'
        condition().eventually {
            assert refresh(cronJob).status == CronJobStatus.FINISHED
            assert testDeleteService.invocations() == 0
        }
    }

    @Test
    @Issue('https://cxjira.sap.com/browse/IAPI-3466')
    @Unroll
    def "sends delete of an item that is not in the integration object when info expression #condition"() {
        given: 'the change stream for items not IO does not have type info in the info expression'
        streamBuilder.withItemType('UserProfile').build()
        and: 'a UserProfile item that is not present in the IO but related to the root item is deleted'
        deleteCustomerProfile()

        when: 'the outbound sync job is executed'
        cronJobService.performCronJob cronJob, true

        then: 'no deletes sent'
        condition().eventually {
            assert refresh(cronJob).status == CronJobStatus.FINISHED
            assert testDeleteService.invocations() == 0
        }

        where:
        infoExpr                    | condition
        '{ "key": "#{owner.uid}" }' | 'does not contain type info'
        null                        | 'is not present'
        'not a json string'         | 'is malformed'
    }

    @Test
    @Issue('https://cxjira.sap.com/browse/IAPI-3466')
    def 'sends delete of the root item in the integration object'() {
        given: 'existing streams for the root and its partOf items in the IO'
        streamBuilder.withItemType('Customer').build()
        streamBuilder.withItemType('Address').build()
        and: 'the customer is deleted'
        deleteCustomer()

        when:
        cronJobService.performCronJob cronJob, true

        then: "the job sends only the Customer for deletion and all changes are consumed"
        condition().eventually {
            with(refresh(cronJob)) {
                assert status == CronJobStatus.FINISHED
                assert result == CronJobResult.SUCCESS
            }
            assert testDeleteService.keysFromInvocationsTo(DESTINATION_ID, CUSTOMER_IO) == [CUSTOMER_UID]
            and: 'all changes are consumed'
            assert testItemChangeConsumer.invocations() == 1 + ADDRESS_KEYS.size()
        }
    }

    @Test
    @Issue('https://cxjira.sap.com/browse/IAPI-3466')
    def 'does not send delete for non-root item in the integration object'() {
        given: 'existing streams for the root item and its partOf items in the IO'
        streamBuilder.withItemType('Customer').build()
        streamBuilder.withItemType('Address').build()
        and: 'the non-root Address item is deleted'
        deleteAddress ADDRESS_KEYS[0]

        when: 'the outbound sync job is executed'
        cronJobService.performCronJob cronJob, true

        then: 'no deletion is sent but the change is consumed'
        condition().eventually {
            with(refresh(cronJob)) {
                assert status == CronJobStatus.FINISHED
                assert result == CronJobResult.SUCCESS
            }
            assert testDeleteService.invocations() == 0
            assert testItemChangeConsumer.invocations() == 1
        }
    }

    @Test
    @Issue('https://cxjira.sap.com/browse/IAPI-3466')
    def 'no deletes sent when the deleted item is associated with the root item that is not in the IO'() {
        given: 'there is an Employee with an Address'
        def employeeId = "${TEST_NAME}_Employee".toLowerCase()
        def addressKey = 'empl_addr'
        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE Employee; uid[unique = true]; defaultPaymentAddress( &addrID )',
                "                      ; $employeeId       ; theAddress",
                'INSERT_UPDATE Address; &addrID   ; owner(Employee.uid); publicKey[unique = true]',
                "                     ; theAddress; $employeeId        ; $addressKey")
        and: 'there are streams for IO item types'
        streamBuilder.withItemType('Customer').build()
        streamBuilder.withItemType('Address').build()
        and: 'the address is deleted'
        deleteAddress addressKey

        when: 'the outbound sync job is executed'
        cronJobService.performCronJob cronJob, true

        then: 'the Address deletion is not sent'
        condition().eventually {
            assert refresh(cronJob).status == CronJobStatus.FINISHED
            assert testDeleteService.invocations() == 0
        }
    }

    @Test
    @Issue('https://cxjira.sap.com/browse/IAPI-3466')
    def 'delete is consumed even if it failed to send'() {
        given: 'there is a change stream for IO types'
        streamBuilder.withItemType('Customer').build()
        streamBuilder.withItemType('Address').build()
        and: 'external system responds with 404 Not Found'
        testDeleteService.throwException(new RestClientResponseException('not found message', 404, 'Not Found', null, null, null))
        and: 'the root item is deleted'
        deleteCustomer()

        when: 'the job is executed'
        cronJobService.performCronJob cronJob, true

        then: 'changes for the root item and its partOf items are consumed'
        condition().eventually {
            assert refresh(cronJob).status == CronJobStatus.FINISHED
            assert testItemChangeConsumer.invocations() == 1 + ADDRESS_KEYS.size()
        }
    }

    private static def createCustomer() {
        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE Customer; uid[unique = true]; userprofile( &profId )',
                "                      ; $CUSTOMER_UID     ; theProfile",
                'INSERT_UPDATE UserProfile; &profId   ; owner(Customer.uid)[unique = true]',
                "                         ; theProfile; $CUSTOMER_UID"
        )
        ADDRESS_KEYS.forEach() { createAddress(it) }
    }

    private static def createAddress(String key) {
        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE Address; publicKey[unique = true]; owner(Customer.uid)',
                "                     ; $key                    ; $CUSTOMER_UID")
    }

    private static def deleteCustomer() {
        IntegrationTestUtil.remove(CustomerModel) { it.uid == CUSTOMER_UID }
    }

    private static def deleteCustomerProfile() {
        IntegrationTestUtil.remove(UserProfileModel) { it.owner.uid == CUSTOMER_UID }
    }

    private static def deleteAddress(String key) {
        IntegrationTestUtil.remove(AddressModel) { it.publicKey == key }
    }

    private static CronJobModel refresh(CronJobModel model) {
        IntegrationTestUtil.refresh model
    }
}
