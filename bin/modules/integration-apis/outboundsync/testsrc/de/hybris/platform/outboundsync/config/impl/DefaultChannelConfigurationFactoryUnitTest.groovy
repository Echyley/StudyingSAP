/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundsync.config.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.deltadetection.model.StreamConfigurationContainerModel
import de.hybris.deltadetection.model.StreamConfigurationModel
import de.hybris.platform.core.model.c2l.LanguageModel
import de.hybris.platform.core.model.type.ComposedTypeModel
import de.hybris.platform.integrationservices.model.DescriptorFactory
import de.hybris.platform.integrationservices.model.IntegrationObjectDescriptor
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.integrationservices.model.impl.ItemTypeDescriptor
import de.hybris.platform.integrationservices.util.TestApplicationContext
import de.hybris.platform.outboundsync.config.IdentifierGenerator
import de.hybris.platform.outboundsync.config.StreamIdentifierGenerator
import de.hybris.platform.outboundsync.model.OutboundChannelConfigurationModel
import de.hybris.platform.outboundsync.model.OutboundSyncJobModel
import de.hybris.platform.outboundsync.model.OutboundSyncStreamConfigurationContainerModel
import de.hybris.platform.servicelayer.i18n.CommonI18NService
import de.hybris.platform.servicelayer.session.SessionService
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Rule
import org.junit.Test
import spock.lang.Unroll

@UnitTest
class DefaultChannelConfigurationFactoryUnitTest extends JUnitPlatformSpecification {
    def descriptorFactory = Stub DescriptorFactory
    def channelConfigurationFactory = new DefaultChannelConfigurationFactory(descriptorFactory: descriptorFactory)

    @Rule
    TestApplicationContext applicationContext = new TestApplicationContext()

    @Test
    def "create OutboundSyncStreamConfigurationContainerModel"() {
        given:
        def newChannel = Stub(OutboundChannelConfigurationModel)
        def containerId = "channelCodeContainer"
        channelConfigurationFactory.streamContainerIdentifierGenerator = Stub(IdentifierGenerator) {
            generate(newChannel) >> containerId
        }

        when:
        def container = channelConfigurationFactory.createStreamContainer(newChannel)

        then:
        container.id == containerId
    }

    @Test
    def "create OutboundSyncJobModel"(){
        given:
        def jobGeneratedCode = "testThisJobCode"
        def channelCode = "testChannel"
        def container = Stub(OutboundSyncStreamConfigurationContainerModel)
        def channel = channelWithCode(channelCode)
        channelConfigurationFactory.jobIdentifierGenerator = Stub(IdentifierGenerator) {
            generate(channel) >> jobGeneratedCode
        }

        when:
        def job = channelConfigurationFactory.createJob(channel, container)

        then:
        job.code == jobGeneratedCode
        job.streamConfigurationContainer == container
    }

    @Test
    def "create OutboundSyncCronJobModel"(){
        given:
        def cronJobGeneratedCode = "testThisCronJobCode"
        def channelCode = "testChannel"
        def job = Stub(OutboundSyncJobModel)
        def channel = channelWithCode(channelCode)
        channelConfigurationFactory.cronJobIdentifierGenerator = Stub(IdentifierGenerator) {
            generate(channel) >> cronJobGeneratedCode
        }
        channelConfigurationFactory.internationalizationService = Stub(CommonI18NService) {
            getCurrentLanguage() >> Stub(LanguageModel){
                getIsocode() >> 'en'
            }
        }

        when:
        def cronJob = channelConfigurationFactory.createCronJob(channel, job)

        then:
        cronJob.code == cronJobGeneratedCode
        cronJob.sessionLanguage.getIsocode() == 'en'
        cronJob.job == job
    }

    @Test
    def "OutboundSyncCronJobModel session language is null if internationalization service is null"(){
        given:
        def cronJobGeneratedCode = "testThisCronJobCode"
        def channelCode=  "testChannel"
        def job = Stub(OutboundSyncJobModel)
        def channel = channelWithCode(channelCode)
        channelConfigurationFactory.cronJobIdentifierGenerator = Stub(IdentifierGenerator){
            generate(channel) >> cronJobGeneratedCode
        }
        channelConfigurationFactory.internationalizationService = null

        when:
        def cronJob = channelConfigurationFactory.createCronJob(channel, job)

        then:
        cronJob.code == cronJobGeneratedCode
        cronJob.sessionLanguage == null
        cronJob.job == job
    }

    @Test
    def "populate OutboundSyncCronJobModel session language with locale if current language is null"(){
        given:
        def cronJobGeneratedCode = "testThisCronJobCode"
        def channelCode=  "testChannel"
        def job = Stub(OutboundSyncJobModel)
        def channel = channelWithCode(channelCode)
        channelConfigurationFactory.cronJobIdentifierGenerator = Stub(IdentifierGenerator){
            generate(channel) >> cronJobGeneratedCode
        }
        channelConfigurationFactory.internationalizationService = Stub(CommonI18NService){
            getCurrentLanguage() >> null
            getLanguage(Locale.GERMAN.getLanguage()) >> Stub(LanguageModel){
                getIsocode() >> "de"
            }
        }
        channelConfigurationFactory.sessionService = Stub(SessionService){
            getAttribute("locale") >> Locale.GERMAN
        }

        when:
        def cronJob = channelConfigurationFactory.createCronJob(channel, job)

        then:
        cronJob.code == cronJobGeneratedCode
        cronJob.sessionLanguage.getIsocode() == "de"
        cronJob.job == job
    }

    @Test
    def "populate OutboundSyncCronJobModel session language with default locale if session service and current language is null"(){
        given:
        def cronJobGeneratedCode = "testThisCronJobCode"
        def channelCode=  "testChannel"
        def job = Stub(OutboundSyncJobModel)
        def channel = channelWithCode(channelCode)
        channelConfigurationFactory.cronJobIdentifierGenerator = Stub(IdentifierGenerator){
            generate(channel) >> cronJobGeneratedCode
        }
        channelConfigurationFactory.internationalizationService = Stub(CommonI18NService){
            getCurrentLanguage() >> null
            getLanguage(Locale.ENGLISH.getLanguage()) >> Stub(LanguageModel){
                getIsocode() >> "en"
            }
        }
        channelConfigurationFactory.sessionService = null

        when:
        def cronJob = channelConfigurationFactory.createCronJob(channel, job)

        then:
        cronJob.code == cronJobGeneratedCode
        cronJob.sessionLanguage.getIsocode() == 'en'
        cronJob.job == job
    }

    @Test
    @Unroll
    def "does not create streams when the channel is associated with an integration object that #condition"() {
        given: "a channel without an integration object"
        def channel = Stub(OutboundChannelConfigurationModel) {
            getIntegrationObject() >> io
        }

        expect: "no streams created"
        channelConfigurationFactory.createStreams(channel, Stub(StreamConfigurationContainerModel)).empty

        where:
        condition      | io
        'is null'      | null
        'has no items' | ioModel(rootItem: null, items: [])
    }

    @Test
    def "creates streams for every item type in the integration object when the integration object does not have a root item"() {
        given: "a channel without a root item and 3 items in the integration object"
        def channel = Stub(OutboundChannelConfigurationModel) {
            getIntegrationObject() >> Stub(IntegrationObjectModel) {
                getRootItem() >> null
                getItems() >> [ioItemModel('Type1'), ioItemModel('Type2'), ioItemModel('Type3')]
            }
        }
        and: "a stream identifier generator returning these values"
        channelConfigurationFactory.streamIdentifierGenerator = Stub(StreamIdentifierGenerator) {
            generate(channel, _ as IntegrationObjectItemModel) >>> ['streamType1', 'streamType2', 'streamType3']
        }
        and: "other required components"
        def container = Stub(StreamConfigurationContainerModel)

        when:
        List<StreamConfigurationModel> streams = channelConfigurationFactory.createStreams(channel, container)

        then:
        streams.collect({it.streamId}) == ['streamType1', 'streamType2', 'streamType3']
        streams.collect({it.itemTypeForStream.code}) == ['Type1', 'Type2', 'Type3']
        streams.forEach { stream ->
            with(stream) {
                outboundChannelConfiguration == channel
                container == container
                active
            }
        }
    }

    @Test
    def "creates streams only for item types with references to root when the integration object has a root item"() {
        given: 'an integration object with root item'
        def io = Stub IntegrationObjectModel
        io.getRootItem() >> ioItemModel(io, 'RootType')
        and: 'an integration object descriptor for the integration object model'
        descriptorFactory.createIntegrationObjectDescriptor(io) >> Stub(IntegrationObjectDescriptor) {
            getItemTypeDescriptors() >> [
                    descriptor(item: ioItemModel(io, 'RootType'), refToRoot: true),
                    descriptor(item: ioItemModel(io, 'NoRootRef'), refToRoot: false),
                    descriptor(item: ioItemModel('RootRef'), refToRoot: true)]
        }
        and: "a channel with integration object having one type referencing root"
        def channel = Stub(OutboundChannelConfigurationModel) {
            getIntegrationObject() >> io
        }
        and: "a stream identifier generator returning these values"
        channelConfigurationFactory.streamIdentifierGenerator = Stub(StreamIdentifierGenerator) {
            generate(channel, _ as IntegrationObjectItemModel) >>> ['type1', 'type2']
        }

        when:
        List<StreamConfigurationModel> streams = channelConfigurationFactory.createStreams(channel, Stub(StreamConfigurationContainerModel))

        then:
        streams.collect({ it.streamId }) == ['type1', 'type2']
        streams.collect({ it.itemTypeForStream.code }) == ['RootType', 'RootRef']
    }

    @Test
    def 'descriptorFactory is retrieved from application context when it is not injected'() {
        given: 'descriptor factory is not injected'
        channelConfigurationFactory.descriptorFactory = null
        and: 'descriptor factory is present in the application context'
        applicationContext.addBean 'integrationServicesDescriptorFactory', Stub(DescriptorFactory)

        expect:
        channelConfigurationFactory.getDescriptorFactory() != null
    }

    private IntegrationObjectItemModel ioItemModel(String type, List<IntegrationObjectItemAttributeModel> attributes = []) {
        ioItemModel(null, type, attributes)
    }

    private IntegrationObjectItemModel ioItemModel(IntegrationObjectModel io, String type, List<IntegrationObjectItemAttributeModel> attributes = []) {
        Stub(IntegrationObjectItemModel) {
            getCode() >> type
            getIntegrationObject() >> (io ?: Stub(IntegrationObjectModel))
            getAttributes() >> attributes
            getType() >> Stub(ComposedTypeModel) {
                getCode() >> type
            }
        }
    }

    private IntegrationObjectModel ioModel(Map attributes) {
        Stub(IntegrationObjectModel) {
            getRootItem() >> attributes['rootItem']
            getItems() >> attributes['items']
        }
    }

    private TypeDescriptor descriptor(Map attributes) {
        Stub(ItemTypeDescriptor) {
            hasPathToRoot() >> attributes['refToRoot']
            getItemTypeModel() >> attributes['item']
        }
    }

    private OutboundChannelConfigurationModel channelWithCode(channelCode) {
        Stub(OutboundChannelConfigurationModel) {
            getCode() >> channelCode
        }
    }
}
