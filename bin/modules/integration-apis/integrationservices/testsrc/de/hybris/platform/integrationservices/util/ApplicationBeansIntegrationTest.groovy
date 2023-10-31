/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.util

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.integrationservices.service.IntegrationObjectService
import de.hybris.platform.integrationservices.service.ItemTypeDescriptorService
import de.hybris.platform.servicelayer.ServicelayerSpockSpecification
import org.junit.Test
import org.springframework.beans.factory.BeanNotOfRequiredTypeException
import org.springframework.beans.factory.NoSuchBeanDefinitionException
import org.springframework.context.ApplicationContext

@IntegrationTest
class ApplicationBeansIntegrationTest extends ServicelayerSpockSpecification {
    @Test
    void 'retrieves a spring bean by name and type when it exists in the context'() {
        expect:
        ApplicationBeans.getBean 'integrationObjectService', IntegrationObjectService
    }

    @Test
    void 'retrieves a spring bean by name and type the same as getBean'() {
        expect:
        ApplicationBeans.getFreshBean 'integrationObjectService', IntegrationObjectService
    }

    @Test
    void 'throws exception when a spring bean with the specified name does not exist in the context'() {
        when:
        ApplicationBeans.getBean 'wrong-name', IntegrationObjectService

        then:
        thrown NoSuchBeanDefinitionException
    }

    @Test
    void 'throws exception when request spring bean type does not match the type of the bean in the context'() {
        when:
        ApplicationBeans.getBean 'integrationObjectService', ItemTypeDescriptorService

        then:
        thrown BeanNotOfRequiredTypeException
    }

    @Test
    void 'a test application context can be injected'() {
        given:
        def beanStub = Stub IntegrationObjectService
        def testAppCtx = Stub(ApplicationContext) {
            getBean('wrong-name', IntegrationObjectService) >> beanStub
        }

        when: 'the test application context is injected'
        ApplicationBeans.applicationContext = testAppCtx

        then: 'the test application context is used for the bean lookup'
        ApplicationBeans.getBean('wrong-name', IntegrationObjectService) == beanStub

        cleanup: 'resetting static application context'
        ApplicationBeans.applicationContext = null
    }
}
