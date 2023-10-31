/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.odata2services.odata.errors

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.inboundservices.persistence.PersistenceContext
import de.hybris.platform.inboundservices.persistence.hook.impl.PersistenceHookException
import de.hybris.platform.inboundservices.persistence.hook.impl.PersistenceHookExecutionException
import de.hybris.platform.inboundservices.persistence.hook.impl.PersistenceHookNotFoundException
import de.hybris.platform.integrationservices.item.IntegrationItem
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.apache.olingo.odata2.api.commons.HttpStatusCodes
import org.apache.olingo.odata2.api.processor.ODataErrorContext
import org.junit.Test
import spock.lang.Unroll

@UnitTest
class PersistenceHookExceptionContextPopulatorUnitTest extends JUnitPlatformSpecification {
    private static final def INTEGRATION_KEY = 'test-123'

    def populator = new PersistenceHookExceptionContextPopulator()

    @Test
    def "ODataErrorContext gets checked for null"() {
        when:
        populator.populate(null)

        then:
        def e = thrown IllegalArgumentException
        e.message.contains("ODataErrorContext")
    }

    @Test
    @Unroll
    def "error context populated correctly for #hook named #name when PersistenceHookNotFoundException is thrown"() {
        given:
        def ex = new PersistenceHookNotFoundException(persistenceContext, name)
        def context = new ODataErrorContext(exception: ex)

        when:
        populator.populate(context)

        then:
        with(context) {
            httpStatus == HttpStatusCodes.BAD_REQUEST
            context.errorCode == 'hook_not_found'
            message == "$hook [$name] does not exist. Payload will not be persisted."
            innerError == key
        }

        where:
        hook              | name                | persistenceContext           | key
        "PrePersistHook"  | "pre-persist-hook"  | prePersistenceContext(name)  | INTEGRATION_KEY
        "PostPersistHook" | "post-persist-hook" | postPersistenceContext(name) | INTEGRATION_KEY
        "PersistenceHook" | "some-hook"         | null                         | null
    }

    @Test
    @Unroll
    def "error context populated correctly for #hook named #name when PersistenceHookExecutionException is thrown"() {
        given:
        def cause = new RuntimeException()
        def ex = new PersistenceHookExecutionException(persistenceContext, name, cause)
        def context = new ODataErrorContext(exception: ex)

        when:
        populator.populate(context)

        then:
        with(context) {
            httpStatus == HttpStatusCodes.BAD_REQUEST
            context.errorCode == error
            message == "Exception occurred during the execution of $hook: [$name]."
            innerError == key
        }

        where:
        hook              | name            | persistenceContext           | error                | key
        "PrePersistHook"  | "item-filter"   | prePersistenceContext(name)  | 'pre_persist_error'  | INTEGRATION_KEY
        "PostPersistHook" | "locale-filter" | postPersistenceContext(name) | 'post_persist_error' | INTEGRATION_KEY
        "PersistenceHook" | "some-hook"     | null                         | 'pre_persist_error'  | null
    }

    @Test
    def "context is not populated when ex is not an instance of PersistenceHookException"() {
        given:
        def ex = new RuntimeException()
        def context = new ODataErrorContext(exception: ex)

        when:
        populator.populate(context)

        then:
        with(context) {
            httpStatus.is null
            errorCode.is null
            message.is null
            innerError.is null
        }
    }

    @Test
    def "getExceptionClass is always PersistenceHookException"() {
        expect:
        populator.getExceptionClass().is PersistenceHookException
    }

    PersistenceContext prePersistenceContext(String hookName = null) {
        Stub(PersistenceContext) {
            getPrePersistHook() >> hookName
            getIntegrationItem() >> Stub(IntegrationItem) {
                getIntegrationKey() >> INTEGRATION_KEY
            }
        }
    }

    PersistenceContext postPersistenceContext(String hookName = null) {
        Stub(PersistenceContext) {
            getPostPersistHook() >> hookName
            getIntegrationItem() >> Stub(IntegrationItem) {
                getIntegrationKey() >> INTEGRATION_KEY
            }
        }
    }
}
