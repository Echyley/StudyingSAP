package de.hybris.platform.integrationservices.interceptor

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.model.type.AttributeDescriptorModel
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel
import de.hybris.platform.servicelayer.interceptor.InterceptorContext
import de.hybris.platform.servicelayer.interceptor.InterceptorException
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class IntegrationObjectItemPrivateAttributeValidateInterceptorUnitTest extends JUnitPlatformSpecification {
    private static final def TYPE_CODE = "AnyTypeCode"
    private static final def ATTR_NAME = "AnyAttrName"

    def interceptor = new IntegrationObjectItemPrivateAttributeValidateInterceptor()

    @Test
    def "exception is thrown when attribute's private modifier is true"() {
        given:
        def attribute = stubAttributeModel([private: true])

        when:
        interceptor.onValidate attribute, Stub(InterceptorContext)

        then:
        def e = thrown InterceptorException
        e.message.contains "Private attribute [$TYPE_CODE.$ATTR_NAME] can not be used in integration object item."
    }

    @Test
    def "no exception is thrown when private modifier is #value"() {
        given:
        def attribute = stubAttributeModel([private: value])

        when:
        interceptor.onValidate attribute, Stub(InterceptorContext)

        then:
        noExceptionThrown()

        where:
        value << [false, null]
    }

    private stubAttributeModel(Map params) {
        Stub(IntegrationObjectItemAttributeModel) {
            getAttributeName() >> ATTR_NAME
            getAttributeDescriptor() >> Stub(AttributeDescriptorModel) {
                getItemtype() >> TYPE_CODE
                getPrivate() >> params['private']
            }
        }
    }
}
