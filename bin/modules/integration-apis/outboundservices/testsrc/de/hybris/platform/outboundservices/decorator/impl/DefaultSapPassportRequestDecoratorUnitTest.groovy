package de.hybris.platform.outboundservices.decorator.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.service.SapPassportService
import de.hybris.platform.outboundservices.decorator.DecoratorContext
import de.hybris.platform.outboundservices.decorator.DecoratorExecution
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders

import static de.hybris.platform.integrationservices.constants.IntegrationservicesConstants.SAP_PASSPORT_HEADER_NAME

@UnitTest
class DefaultSapPassportRequestDecoratorUnitTest extends JUnitPlatformSpecification {

    private static final def MAP_KEY = "key"
    private static final def MAP_VALUE = "value"
    private static final def INTEGRATION_OBJECT = "MyIntegrationObject";
    private static final def INTEGRATION_KEY_NAME = "integrationKey"
    private static final def INTEGRATION_KEY_VALUE = "${MAP_KEY}|${MAP_VALUE}"
    private static final def PAYLOAD = [(MAP_KEY): MAP_VALUE, (INTEGRATION_KEY_NAME): INTEGRATION_KEY_VALUE]
    private static final def SAP_PASSPORT_HEADER = 'SAP-PASSPORT'
    private static final def SAP_PASSPORT_VALUE = 'MY-SAP-PASSPORT'

    def httpHeaders = headersWithSapPassport()
    def passportService = Stub(SapPassportService)

    def sapPassportRequestDecorator = new DefaultSapPassportRequestDecorator(sapPassportService: passportService)

    @Test
    def 'passport request decorator is applicable'() {
        expect:
        sapPassportRequestDecorator.isApplicable(Stub(DecoratorContext))
    }

    @Test
    def 'decorator adds passport header to request when executed'() {
        given:
        passportService.generate(INTEGRATION_OBJECT) >> SAP_PASSPORT_VALUE

        when:
        def result = sapPassportRequestDecorator.decorate httpHeaders, PAYLOAD, decoratorContext(), decoratorExecution()

        then:
        result.getHeaders().getFirst(SAP_PASSPORT_HEADER_NAME) == SAP_PASSPORT_VALUE
    }

    @Test
    def 'decorator does not handle exceptions'() {
        given:
        passportService.generate(INTEGRATION_OBJECT) >> { throw new NullPointerException() }

        when:
        sapPassportRequestDecorator.decorate httpHeaders, PAYLOAD, decoratorContext(), decoratorExecution()

        then:
        thrown(NullPointerException)
    }

    private DecoratorContext decoratorContext() {
        Stub(DecoratorContext) {
            getIntegrationObjectCode() >> INTEGRATION_OBJECT;
        }
    }

    private DecoratorExecution decoratorExecution() {
        Stub(DecoratorExecution) {
            createHttpEntity(_, _, _) >> { args -> new HttpEntity<>(args[1] as Map, args[0] as HttpHeaders) }
        }
    }

    private static HttpHeaders headersWithSapPassport() {
        new HttpHeaders([(SAP_PASSPORT_HEADER): SAP_PASSPORT_VALUE])
    }

}
