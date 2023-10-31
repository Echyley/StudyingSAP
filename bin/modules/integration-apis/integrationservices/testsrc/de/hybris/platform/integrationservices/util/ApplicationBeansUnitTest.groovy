/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 *
 */

package de.hybris.platform.integrationservices.util

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.service.IntegrationObjectService
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import org.springframework.beans.factory.BeanNotOfRequiredTypeException
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.context.ApplicationContext

@UnitTest
class ApplicationBeansUnitTest extends JUnitPlatformSpecification {

    @Test
    void 'returns bean correctly'() {
        given:
        def beanID = 'notMatter'
        def beanStub = Stub IntegrationObjectService
        def testAppCtx = Stub(ApplicationContext) {
            getBean(beanID, IntegrationObjectService) >> beanStub
        }

        when: 'the test application context is injected'
        ApplicationBeans.applicationContext = testAppCtx

        then: 'the test application context is used for the bean lookup'
        ApplicationBeans.getFreshBean(beanID, IntegrationObjectService) == beanStub
    }

    @Test
    void 'catches exception and returns a newly generated bean'() {
        given:
        def beanID = 'notMatter'
        def beanStub = Stub IntegrationObjectService
        def testAppCtx = Stub(ApplicationContext) {
            getBean(beanID, IntegrationObjectService) >> { throw Stub(BeanNotOfRequiredTypeException) } >> beanStub
            getAutowireCapableBeanFactory() >> Stub(ConfigurableListableBeanFactory)
        }

        when: 'the test application context is injected'
        ApplicationBeans.applicationContext = testAppCtx

        then: 'the test application context is used for the bean lookup'
        ApplicationBeans.getFreshBean(beanID, IntegrationObjectService) == beanStub
    }
}
