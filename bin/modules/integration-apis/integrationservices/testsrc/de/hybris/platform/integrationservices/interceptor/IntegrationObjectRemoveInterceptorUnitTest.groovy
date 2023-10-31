package de.hybris.platform.integrationservices.interceptor

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.interceptor.interfaces.BeforeRemoveIntegrationObjectChecker
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.servicelayer.interceptor.InterceptorContext
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import org.springframework.context.ApplicationContext
import spock.lang.Unroll

@UnitTest
class IntegrationObjectRemoveInterceptorUnitTest extends JUnitPlatformSpecification {

    @Test
    def "IntegrationObjectRemoveInterceptor requires a non-null list of BeforeRemoveIntegrationObjectCheckers"() {
        when:
        new IntegrationObjectRemoveInterceptor(null)

        then:
        def e = thrown IllegalArgumentException
        e.message.contains "beforeRemoveIntegrationObjectCheckers can't be null"
    }

    @Test
    @Unroll
    def "checker is invoked #times when the integration object is #integrationObject"() {
        given:
        def checker = Mock(BeforeRemoveIntegrationObjectChecker)
        def checkers = ['someChecker':checker]
        def applicationContext = Stub(ApplicationContext) {
            getBeansOfType(BeforeRemoveIntegrationObjectChecker) >> checkers
        }
        def interceptor = new IntegrationObjectRemoveInterceptor()
        interceptor.applicationContext = applicationContext;

        when:
        interceptor.onRemove integrationObject, Stub(InterceptorContext)

        then:
        times * checker.checkIfIntegrationObjectInUse(integrationObject)

        where:
        integrationObject            | times
        null                         | 0
        Stub(IntegrationObjectModel) | 1
    }

    @Test
    def "IntegrationObjectRemoveInterceptor does not leak references when validator list is not null"() {
        given:
        def integrationObject = Stub IntegrationObjectModel
        def checker = Mock(BeforeRemoveIntegrationObjectChecker)
        def checkers = ['someChecker':checker]
        def applicationContext = Stub(ApplicationContext) {
            getBeansOfType(BeforeRemoveIntegrationObjectChecker) >> checkers
        }
        def interceptor = new IntegrationObjectRemoveInterceptor()
        interceptor.applicationContext = applicationContext

        when:
        checkers.clear()
        interceptor.onRemove integrationObject, Stub(InterceptorContext)

        then:
        1 * checker.checkIfIntegrationObjectInUse(integrationObject)
    }
}
