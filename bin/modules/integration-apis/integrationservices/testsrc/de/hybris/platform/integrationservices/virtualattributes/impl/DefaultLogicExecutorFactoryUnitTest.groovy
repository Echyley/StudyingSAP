/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.virtualattributes.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.integrationservices.scripting.LogicLocation
import de.hybris.platform.integrationservices.virtualattributes.LogicExecutorContext
import de.hybris.platform.integrationservices.virtualattributes.LogicParams
import de.hybris.platform.integrationservices.virtualattributes.VirtualAttributeConfigurationException
import de.hybris.platform.scripting.engine.ScriptingLanguagesService
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class DefaultLogicExecutorFactoryUnitTest extends JUnitPlatformSpecification {
    private static final def UNSUPPORTED_LOCATION = LogicLocation.from 'bean://NotSupportedLocation'

    def factory = new DefaultLogicExecutorFactory(Stub(ScriptingLanguagesService))

    @Test
    def 'factory checks required parameters are provided'() {
        when:
        new DefaultLogicExecutorFactory(null)

        then:
        def e = thrown IllegalArgumentException
        e.message == 'Scripting languages service must be provided'
    }

    @Test
    def 'factory creates ScriptExecutor when logic location is a model'() {
        def location = LogicLocation.from('model://abc')
        def context = LogicExecutorContext.create location, Stub(TypeAttributeDescriptor)

        expect:
        factory.create(context) instanceof ScriptLogicExecutor
    }

    @Test
    def 'factory fails to create a logic executor when context is null'() {
        when:
        factory.create(null)

        then:
        def e = thrown IllegalArgumentException
        e.message == 'LogicExecutorContext must be provided'
    }

    @Test
    def 'factory creates default logic executor when context is not null, and logic location is not model'() {
        given:
        def descriptor = Stub(TypeAttributeDescriptor)
        def context = LogicExecutorContext.create UNSUPPORTED_LOCATION, descriptor

        when:
        def defaultExecutor = factory.create(context)

        then:
        !(defaultExecutor instanceof ScriptLogicExecutor)
    }

    @Test
    def 'default logic executor throws exception with detail message when executor is created with non-null context, and logic location is not model'() {
        given:
        def attrName = 'vaName'
        def itemCode = 'vaItemCode'
        def descriptor = Stub(TypeAttributeDescriptor) {
            getAttributeName() >> attrName
            getTypeDescriptor() >> Stub(TypeDescriptor) {
                getItemCode() >> itemCode
            }
        }

        def context = LogicExecutorContext.create UNSUPPORTED_LOCATION, descriptor
        def executor = factory.create context

        when:
        executor.execute Stub(LogicParams)

        then:
        def e = thrown VirtualAttributeConfigurationException
        e.message.contains attrName
        e.message.contains itemCode
        !e.cause
        e.executorContext == context
        e.message.contains "Schema ${UNSUPPORTED_LOCATION.scheme} is unsupported in logic locations"
    }
}
