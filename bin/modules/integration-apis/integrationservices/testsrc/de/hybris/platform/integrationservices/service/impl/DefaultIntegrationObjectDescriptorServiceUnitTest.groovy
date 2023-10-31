package de.hybris.platform.integrationservices.service.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.exception.LocaleNotSupportedException
import de.hybris.platform.integrationservices.model.DescriptorFactory
import de.hybris.platform.integrationservices.model.IntegrationObjectDescriptor
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.integrationservices.service.IntegrationObjectService
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class DefaultIntegrationObjectDescriptorServiceUnitTest extends JUnitPlatformSpecification
{
    private static final TEST_CODE = "some integration object code"
    private static final IO_MODEL = new IntegrationObjectModel()

    def integrationObjectService = Stub IntegrationObjectService
    def descriptorFactory = Stub DescriptorFactory
    def service = new DefaultIntegrationObjectDescriptorService(integrationObjectService, descriptorFactory)

    @Test
    def "retrieval requires IntegrationObjectService when instantiating the descriptor service"()
    {
        when:
        new DefaultIntegrationObjectDescriptorService(null, descriptorFactory)

        then:
        def e = thrown IllegalArgumentException
        e.message == "IntegrationObjectService must be provided to instantiate the service."
    }

    @Test
    def "retrieval requires DescriptorFactory when instantiating the descriptor service"()
    {
        when:
        new DefaultIntegrationObjectDescriptorService(integrationObjectService, null)

        then:
        def e = thrown IllegalArgumentException
        e.message == "DescriptorFactory must be provided to instantiate the service."
    }

    @Test
    def 'retrieve method returns the integration object descriptor when found by code'()
    {
        given:
        integrationObjectService.findIntegrationObject(TEST_CODE) >> IO_MODEL
        def ioDescriptor = Stub IntegrationObjectDescriptor
        descriptorFactory.createIntegrationObjectDescriptor(IO_MODEL) >> ioDescriptor

        when:
        def found = service.retrieve(TEST_CODE)

        then:
        found.is ioDescriptor
    }

    @Test
    def 'retrieve method returns null when model is not found'()
    {
        given:
        integrationObjectService.findIntegrationObject(TEST_CODE) >> {throw new ModelNotFoundException("Not Found")}

        expect:
        service.retrieve(TEST_CODE) == null
    }
}
