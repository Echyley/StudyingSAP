/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundsync.interceptor

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.model.type.ComposedTypeModel
import de.hybris.platform.integrationservices.model.DescriptorFactory
import de.hybris.platform.integrationservices.model.IntegrationObjectDescriptor
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.outboundsync.config.impl.DefaultOutboundSyncConfiguration
import de.hybris.platform.outboundsync.config.impl.OutboundSyncConfiguration
import de.hybris.platform.outboundsync.job.InfoExpressionGenerator
import de.hybris.platform.outboundsync.model.OutboundChannelConfigurationModel
import de.hybris.platform.outboundsync.model.OutboundSyncStreamConfigurationModel
import de.hybris.platform.servicelayer.interceptor.InterceptorContext
import de.hybris.platform.servicelayer.interceptor.PersistenceOperation
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Unroll

@UnitTest
class StreamConfigurationInfoExpressionCreationInterceptorUnitTest extends JUnitPlatformSpecification {
    private static final def IO_CODE = 'MyTestIO'
    private static final def EXISTING_TYPE = 'ExistingType'
    private static final def NON_EXISTING_TYPE = 'NonExistingType'
    private static final def INFO_EXPRESSION = '{ "key": "123", "type": "ExistingType", "rootType": "ExistingType" }'
    private static final def EMPTY_EXPRESSION = '{}'

    def infoExpressionGenerator = Mock(InfoExpressionGenerator) {
        generateInfoExpression(null) >> EMPTY_EXPRESSION
        generateInfoExpression(_ as TypeDescriptor) >> INFO_EXPRESSION
    }
    def integrationObject = integrationObject()
    def existingItemType = Stub(ComposedTypeModel) {
        getCode() >> EXISTING_TYPE
    }
    def nonExistingItemType = Stub(ComposedTypeModel) {
        getCode() >> NON_EXISTING_TYPE
    }

    def defaultOutboundSyncConfiguration = Stub(OutboundSyncConfiguration)

    def context = Mock(InterceptorContext)

    def interceptor = new StreamConfigurationInfoExpressionCreationInterceptor(factory(), infoExpressionGenerator, defaultOutboundSyncConfiguration)

    @Test
    @Unroll
    def "info expression is #msg the auto generation disabled list"() {
        given:
        deleteIsEnabled()
        infoGenerationEnabledForStream(isGenerationEnabled)
        def stream = stream(existingItemType)

        when:
        interceptor.onLoad(stream, context)

        then:
        numberOfCalls * infoExpressionGenerator.generateInfoExpression(_ as TypeDescriptor)
        numberOfCalls * context.registerElementFor(_ as OutboundSyncStreamConfigurationModel, PersistenceOperation.SAVE)

        where:
        msg                                                                       | numberOfCalls | isGenerationEnabled
        "generated and persisted when the stream has generation enabled"          | 1             | true
        "not generated and not persisted when the stream has generation disabled" | 0             | false
    }

    @Test
    @Unroll
    def "cannot instantiate interceptor with null #condition"() {
        when:
        new StreamConfigurationInfoExpressionCreationInterceptor(factory, generator, outboundSyncConfig)

        then:
        def e = thrown(IllegalArgumentException)
        e.getMessage() == "$condition cannot be null"

        where:
        condition                   | factory                 | generator                     | outboundSyncConfig
        'DescriptorFactory'         | null                    | Stub(InfoExpressionGenerator) | Stub(DefaultOutboundSyncConfiguration)
        'InfoExpressionGenerator'   | Stub(DescriptorFactory) | null                          | Stub(DefaultOutboundSyncConfiguration)
        'OutboundSyncConfiguration' | Stub(DescriptorFactory) | Stub(InfoExpressionGenerator) | null
    }

    @Test
    @Unroll
    def "no infoExpression is persisted for the model when #msg"() {
        given:
        deleteIsEnabled(outboundSyncDeleteEnabled)
        infoGenerationEnabledForStream(outboundSyncDeleteEnabled)
        def stream = stream(existingItemType, channel)

        when:
        interceptor.onLoad(stream, context)

        then:
        stream.infoExpression == null
        0 * context.registerElementFor(_ as OutboundSyncStreamConfigurationModel, PersistenceOperation.SAVE)

        where:
        msg                                      | channel                | outboundSyncDeleteEnabled
        "outbound channel configuration is null" | null                   | true
        "outboundSync Delete is disabled"        | defaultChannelConfig() | false
    }

    @Test
    def "an empty value for infoExpression is persisted for the model when the item type of the stream is not found in the integration object"() {
        given:
        deleteIsEnabled()
        infoGenerationEnabledForStream()
        def stream = streamWithNonExistingType()

        when:
        interceptor.onLoad(stream, context)

        then:
        stream.infoExpression == EMPTY_EXPRESSION
        1 * context.registerElementFor(_ as OutboundSyncStreamConfigurationModel, PersistenceOperation.SAVE) >> { args ->
            assert args[0].infoExpression == EMPTY_EXPRESSION
        }
    }

    @Test
    def "a generated info expression is persisted in the stream when the item type matches a type in the integration object"() {
        given:
        deleteIsEnabled()
        infoGenerationEnabledForStream()
        def stream = streamWithExistingType()

        when:
        interceptor.onLoad(stream, context)

        then:
        stream.infoExpression == INFO_EXPRESSION
        1 * context.registerElementFor(_ as OutboundSyncStreamConfigurationModel, PersistenceOperation.SAVE) >> { args ->
            assert args[0].infoExpression == INFO_EXPRESSION
        }
    }

    private void infoGenerationEnabledForStream(boolean isEnabled = true) {
        defaultOutboundSyncConfiguration.isInfoGenerationEnabledForStream(_ as OutboundSyncStreamConfigurationModel) >> isEnabled
    }

    private void deleteIsEnabled(boolean isEnabled = true) {
        interceptor.isDeleteFeatureEnabled() >> isEnabled
    }

    private OutboundSyncStreamConfigurationModel streamWithExistingType() {
        stream(existingItemType)
    }

    private OutboundSyncStreamConfigurationModel streamWithNonExistingType() {
        stream(nonExistingItemType)
    }

    private OutboundSyncStreamConfigurationModel stream(ComposedTypeModel itemType, OutboundChannelConfigurationModel channelConfiguration = defaultChannelConfig()) {
        def model = new OutboundSyncStreamConfigurationModel()
        model.setOutboundChannelConfiguration(channelConfiguration)
        model.setItemTypeForStream(itemType)
        model
    }

    private OutboundChannelConfigurationModel defaultChannelConfig() {
        Stub(OutboundChannelConfigurationModel) {
            getIntegrationObject() >> integrationObject
        }
    }

    private IntegrationObjectModel integrationObject() {
        Stub(IntegrationObjectModel) {
            getCode() >> IO_CODE
        }
    }

    private IntegrationObjectDescriptor descriptor() {
        Stub(IntegrationObjectDescriptor) {
            getTypeDescriptor(existingItemType) >> Optional.of(Stub(TypeDescriptor))
            getTypeDescriptor(nonExistingItemType) >> Optional.empty()
        }
    }

    private DescriptorFactory factory() {
        Stub(DescriptorFactory) {
            createIntegrationObjectDescriptor(integrationObject) >> descriptor()
        }
    }
}
