/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.inboundservices.persistence.hook.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.inboundservices.persistence.PersistenceContext
import de.hybris.platform.inboundservices.persistence.hook.PersistenceHookProvider
import de.hybris.platform.inboundservices.persistence.hook.PostPersistHook
import de.hybris.platform.inboundservices.persistence.hook.PrePersistHook
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Unroll

@UnitTest
class DefaultPersistenceHookExecutorUnitTest extends JUnitPlatformSpecification {
    private static final def ITEM = new ItemModel()
    private static final def HOOK_ITEM = new ItemModel()

    def executor = new DefaultPersistenceHookExecutor()

    @Test
    def 'returns the item when persistence context does not have pre-persist hook name'() {
        given: 'persistent context does not contain pre-persist hook name'
        def context = persistenceContext()

        when: 'executor runs runPrePersistHook'
        def item = executor.runPrePersistHook(ITEM, context)

        then:
        item.present
        item.get() == ITEM
    }

    @Test
    def 'throws exception when PrePersistHook fails to execute'() {
        given: 'persistent context contains pre-persist hook name'
        def context = persistenceContext('a-hook')

        and:
        executor.hookProviders = [preMatchingProvider(context, faultyPrePersistHook(context))]

        when: 'executor runs runPrePersistHook'
        executor.runPrePersistHook(ITEM, context)

        then: 'exception is thrown with correct context provided'
        def e = thrown(PersistenceHookExecutionException)
        with(e) {
            message.contains(context.prePersistHook)
            persistenceContext == context
        }
    }

    @Test
    def 'throws exception when PostPersistHook fails to execute'() {
        given: 'persistent context contains post-persist hook name'
        def context = persistenceContext('a-hook')

        and:
        executor.hookProviders = [postMatchingProvider(context, faultyPostPersistHook(context))]

        when: 'executor runs runPrePersistHook'
        executor.runPostPersistHook(ITEM, context)

        then: 'exception is thrown with correct context provided'
        def e = thrown(PersistenceHookExecutionException)
        with(e) {
            message.contains(context.postPersistHook)
            persistenceContext == context
        }
    }

    @Test
    def 'throws exception when there are no hook providers injected but context contains pre-persist hook name'() {
        given: 'no hook providers injected'
        executor.setHookProviders([])
        and: 'persistent context contains pre-persist hook name'
        def context = persistenceContext('a-hook')

        when: 'executor runs runPrePersistHook'
        executor.runPrePersistHook(ITEM, context)

        then: 'exception is thrown with correct context provided'
        def e = thrown(PersistenceHookNotFoundException)
        with(e) {
            message.contains(context.prePersistHook)
            persistenceContext == context
        }
    }

    @Test
    def 'throws exception when there are no hook providers injected but context contains post-persist hook name'() {
        given: 'no hook providers injected'
        executor.setHookProviders([])
        and: 'persistent context contains pre-persist hook name'
        def context = persistenceContext('a-hook')

        when: 'executor runs runPrePersistHook'
        executor.runPostPersistHook(ITEM, context)

        then: 'exception is thrown with correct context provided'
        def e = thrown(PersistenceHookNotFoundException)
        with(e) {
            message.contains(context.postPersistHook)
            persistenceContext == context
        }
    }

    @Test
    def 'throws exception when there are no matching hook providers and context contains pre-persist hook name'() {
        given: 'only non-matching providers injected'
        executor.setHookProviders([preNonMatchingProvider(), preNonMatchingProvider()])

        and: 'persistent context contains pre-persist hook name'
        def context = persistenceContext('a-hook')

        when: 'executor runs runPrePersistHook'
        executor.runPrePersistHook(ITEM, context)

        then: 'exception is thrown with correct context provided'
        def e = thrown(PersistenceHookNotFoundException)
        with(e) {
            message.contains(context.prePersistHook)
            persistenceContext == context
        }
    }

    @Test
    def 'throws exception when there are no matching hook providers and context contains post-persist hook name'() {
        given: 'only non-matching providers injected'
        executor.setHookProviders([postNonMatchingProvider(), postNonMatchingProvider()])

        and: 'persistent context contains post-persist hook name'
        def context = persistenceContext('a-hook')

        when: 'executor runs runPrePersistHook'
        executor.runPostPersistHook(ITEM, context)

        then: 'exception is thrown with correct context provided'
        def e = thrown(PersistenceHookNotFoundException)
        with(e) {
            message.contains(context.postPersistHook)
            persistenceContext == context
        }
    }

    @Test
    def 'returns hook result when hook name is in the context and there is a matching hook provider'() {
        given: 'some persistence context to persist'
        def context = persistenceContext('PrePersistHook')

        and: 'there is persistence hook for the context'
        def hook = prePersistHook(context)

        and: 'the matching hook is injected into the executor along with non-matching hooks'
        executor.hookProviders = [preNonMatchingProvider(), preMatchingProvider(context, hook)]

        when:
        def item = executor.runPrePersistHook(ITEM, context)

        then:
        item.present
        item.get().is HOOK_ITEM
    }

    @Test
    def "post-persist hook executes when hook name is in the context and there is a matching hook provider"() {
        given: 'some persistence context to persist'
        def context = persistenceContext('PostPersistHook')

        and: 'there is persistence hook for the context'
        def hook = postPersistHook()

        and: 'the matching hook is injected into the executor along with non-matching hooks'
        executor.hookProviders = [postNonMatchingProvider(), postMatchingProvider(context, hook)]

        when:
        executor.runPostPersistHook(ITEM, context)

        then:
        1 * hook.execute(ITEM, context)
    }

    @Test
    @Unroll
    def "no interactions when pre-persist hook name is '#condition'"() {
        given:
        def context = persistenceContext(condition)

        and:
        def hook = prePersistHook(context)

        and:
        executor.hookProviders = [preMatchingProvider(context, hook)]

        when:
        def result = executor.runPrePersistHook(ITEM, context)

        then:
        0 * hook.execute(ITEM, context)
        and:
        result.get().is ITEM

        where:
        condition << [null, "", " "]
    }

    @Test
    @Unroll
    def "no interactions when post-persist hook name is '#condition'"() {
        given:
        def context = persistenceContext(condition)

        and:
        def hook = postPersistHook()

        and:
        executor.hookProviders = [postMatchingProvider(context, hook)]

        when:
        executor.runPostPersistHook(ITEM, context)

        then:
        0 * hook.execute(ITEM, context)

        where:
        condition << [null, "", " "]
    }

    @Test
    def "calling setHookProviders more than once does not concatenate hookProviders"() {
        given: 'some persistence context to persist'
        def context = persistenceContext()

        and: 'some hook is provided'
        def hook = prePersistHook(context)

        and: 'first invocation of setHookProviders'
        def providers1 = [preMatchingProvider(context, hook)]
        executor.setHookProviders(providers1)

        when: 'second invocation of setHookProviders'
        def providers2 = [preNonMatchingProvider()]
        executor.setHookProviders(providers2)

        then: 'hookProviders does not contain all additional providers from first invocation'
        !executor.getHookProviders().containsAll(providers1)
        executor.getHookProviders() == providers2
    }

    @Test
    def "reference to hookProviders does not escape"() {
        given: 'an external list of providers'
        def external = [Stub(PersistenceHookProvider)]

        and: 'providers are set into the executor'
        executor.hookProviders = external

        when: 'the external list is cleard'
        external.clear()

        then: 'executor providers are not affected'
        !executor.hookProviders.isEmpty()
        !executor.hookProviders.is(external)
    }

    PersistenceContext persistenceContext(String hookName = null) {
        Stub(PersistenceContext) {
            getPrePersistHook() >> hookName
            getPostPersistHook() >> hookName
        }
    }

    PrePersistHook prePersistHook(PersistenceContext ctx) {
        Mock(PrePersistHook) {
            execute(ITEM, ctx) >> Optional.of(HOOK_ITEM)
        }
    }

    PrePersistHook faultyPrePersistHook(PersistenceContext ctx) {
        Stub(PrePersistHook) {
            execute(ITEM, ctx) >> { throw new RuntimeException() }
        }
    }

    PostPersistHook postPersistHook() {
        Mock(PostPersistHook)
    }

    PostPersistHook faultyPostPersistHook(PersistenceContext ctx) {
        Mock(PostPersistHook) {
            execute(ITEM, ctx) >> { throw new RuntimeException() }
        }
    }

    PersistenceHookProvider preNonMatchingProvider() {
        Stub(PersistenceHookProvider) {
            getPrePersistHook(_ as PersistenceContext) >> Optional.empty()
        }
    }

    PersistenceHookProvider postNonMatchingProvider() {
        Stub(PersistenceHookProvider) {
            getPostPersistHook(_ as PersistenceContext) >> Optional.empty()
        }
    }

    PersistenceHookProvider preMatchingProvider(PersistenceContext context, PrePersistHook hook) {
        Stub(PersistenceHookProvider) {
            getPrePersistHook(context) >> Optional.of(hook)
        }
    }

    PersistenceHookProvider postMatchingProvider(PersistenceContext context, PostPersistHook hook) {
        Stub(PersistenceHookProvider) {
            getPostPersistHook(context) >> Optional.of(hook)
        }
    }
}
