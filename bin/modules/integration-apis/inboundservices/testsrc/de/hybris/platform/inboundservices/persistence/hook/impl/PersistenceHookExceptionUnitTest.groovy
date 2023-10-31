/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.inboundservices.persistence.hook.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.inboundservices.persistence.PersistenceContext
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Unroll

@UnitTest
class PersistenceHookExceptionUnitTest extends JUnitPlatformSpecification {
    private static final def PRE_PERSIST_HOOK = 'PrePersistHook'
    private static final def POST_PERSIST_HOOK = 'PostPersistHook'

    @Test
    def "all constructor parameters are null safe"() {
        given:
        def ex = new PersistenceHookException(null, null, null, null)

        expect:
        with(ex) {
            !message
            !persistenceContext
            !persistenceHookType
            !hookName
        }
    }

    @Test
    @Unroll
    def "correctly determines #hookName context when all parameters are not null"() {
        given:
        def context = Stub(PersistenceContext) {
            getPrePersistHook() >> PRE_PERSIST_HOOK
            getPostPersistHook() >> POST_PERSIST_HOOK
        }
        def msg = 'some message'
        def cause = new RuntimeException()
        def exception = new PersistenceHookException(msg, context, hookName, cause)

        expect:
        with(exception) {
            message == msg
            cause == cause
            persistenceContext == context
            persistenceHookType == hookType
        }

        where:
        hookName          | hookType
        PRE_PERSIST_HOOK  | PersistenceHookType.PRE
        POST_PERSIST_HOOK | PersistenceHookType.POST
    }
}
