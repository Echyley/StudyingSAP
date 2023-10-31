/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.inboundservices.persistence.hook.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.inboundservices.persistence.PersistenceContext
import de.hybris.platform.inboundservices.persistence.hook.PostPersistHook
import de.hybris.platform.inboundservices.persistence.hook.PrePersistHook
import de.hybris.platform.integrationservices.util.LoggerProbe
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Rule
import org.junit.Test
import org.springframework.context.ApplicationContext

@UnitTest
class SpringBeanPersistenceHookProviderUnitTest extends JUnitPlatformSpecification {
    private static final def HOOK_NAME = 'spring_bean_id'
    private static final def LOGGER_CLASS = SpringBeanPersistenceHookProvider

    @Rule
    public LoggerProbe probe = LoggerProbe.create(LOGGER_CLASS)

    def applicationContext = Stub ApplicationContext
    def provider = new SpringBeanPersistenceHookProvider(applicationContext: applicationContext)

    @Test
    def "getPrePersistHook cannot accept null persistence context"() {
        when:
        provider.getPrePersistHook(null)

        then:
        def e = thrown IllegalArgumentException
        e.message.contains 'PersistenceContext'
    }

    @Test
    def "getPrePersistHook returns optional of hook when prePersistHook #name bean exists in the app context"() {
        given:
        def hookBean = Stub PrePersistHook
        applicationContext.getBean(HOOK_NAME, PrePersistHook) >> hookBean

        when:
        def foundHook = provider.getPrePersistHook contextWithPrePersistHook(name)

        then:
        foundHook == Optional.of(hookBean)

        where:
        name << [HOOK_NAME]//, "bean://$HOOK_NAME"]
    }

    @Test
    def "getPrePersistHook returns empty when prePersistHook is #name"() {
        when:
        def foundHook = provider.getPrePersistHook contextWithPrePersistHook(name)

        then:
        foundHook.isEmpty()

        where:
        name << [null, "", "other://$HOOK_NAME"]
    }

    @Test
    def "getPrePersistHook returns no hook when application context throws RuntimeException"() {
        given:
        applicationContext.getBean(HOOK_NAME, PrePersistHook) >> { throw new RuntimeException() }

        when:
        def foundHook = provider.getPrePersistHook contextWithPrePersistHook("bean://$HOOK_NAME")

        then:
        foundHook.isEmpty()
    }

    @Test
    def "getPostPersistHook cannot accept null persistence context"() {
        when:
        provider.getPostPersistHook(null)

        then:
        def e = thrown IllegalArgumentException
        e.message.contains 'PersistenceContext'
    }

    @Test
    def "getPostPersistHook returns optional of hook when postPersistHook #name exists in the app context"() {
        given:
        def hookBean = Stub PostPersistHook
        applicationContext.getBean(HOOK_NAME, PostPersistHook) >> hookBean

        when:
        def foundHook = provider.getPostPersistHook contextWithPostPersistHook(name)

        then:
        foundHook == Optional.of(hookBean)

        where:
        name << [HOOK_NAME, "bean://$HOOK_NAME"]
    }

    @Test
    def "getPostPersistHook returns empty when postPersistHook is #name"() {
        given:
        def context = Stub(PersistenceContext) {
            getPostPersistHook() >> name
        }

        when:
        def foundHook = provider.getPostPersistHook(context)

        then:
        foundHook.isEmpty()

        where:
        name << [null, "", "other://$HOOK_NAME"]
    }

    @Test
    def "getPostPersistHook returns no hook when application context throws RuntimeException"() {
        given:
        applicationContext.getBean(HOOK_NAME, PostPersistHook) >> { throw new RuntimeException() }

        when:
        def foundHook = provider.getPostPersistHook contextWithPostPersistHook(HOOK_NAME)

        then:
        foundHook.isEmpty()
    }

    @Test
    def "#method logs correct bean name when RuntimeException is thrown using #hooktype"() {
        given:
        applicationContext.getBean(HOOK_NAME, hooktype) >> { throw new RuntimeException() }
        def context = contextWithPersistHook("bean://$HOOK_NAME")

        when:
        provider."$method"(context)

        then:
        String error_msg = "Exception while retrieving hook $HOOK_NAME"
        probe.getMessagesLoggedAtDebug().contains(error_msg)

        where:
        hooktype        | method
        PrePersistHook  | "getPrePersistHook"
        PostPersistHook | "getPostPersistHook"
    }

    PersistenceContext contextWithPrePersistHook(String hookName) {
        Stub(PersistenceContext) {
            getPrePersistHook() >> hookName
        }
    }

    PersistenceContext contextWithPostPersistHook(String hookName) {
        Stub(PersistenceContext) {
            getPostPersistHook() >> hookName
        }
    }

    PersistenceContext contextWithPersistHook(String hookName) {
        Stub(PersistenceContext) {
            getPrePersistHook() >> hookName
            getPostPersistHook() >> hookName
        }
    }
}
