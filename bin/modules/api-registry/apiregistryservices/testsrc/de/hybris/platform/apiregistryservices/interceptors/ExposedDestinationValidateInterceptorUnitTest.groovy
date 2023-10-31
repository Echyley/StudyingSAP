/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.apiregistryservices.interceptors

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.apiregistryservices.exceptions.DestinationCredNotMatchException
import de.hybris.platform.apiregistryservices.model.ExposedDestinationModel
import de.hybris.platform.apiregistryservices.services.DestinationCredentialService
import de.hybris.platform.servicelayer.interceptor.InterceptorContext
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

import static org.mockito.Mockito.mock

@UnitTest
class ExposedDestinationValidateInterceptorUnitTest extends JUnitPlatformSpecification {
    def interceptor = new ExposedDestinationValidateInterceptor();
    def service = Stub(DestinationCredentialService.class);

    @Test
    def "onValidate failure should throw correct error"() {
        given:
        def ctx = mock(InterceptorContext.class);
        def exposedDestinationModel = new ExposedDestinationModel();

        interceptor.setDestinationCredentialService(service);
        service.isValidDestinationCredential(exposedDestinationModel) >> false;

        when:
        interceptor.onValidate(exposedDestinationModel, ctx);

        then:
        thrown(DestinationCredNotMatchException)
    }
}
