/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundservices.interceptor

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.regioncache.region.impl.EHCacheRegion
import de.hybris.platform.servicelayer.interceptor.InterceptorContext
import de.hybris.platform.testframework.JUnitPlatformSpecification
import de.hybris.platform.webservicescommons.model.OAuthClientDetailsModel
import org.junit.Test

@UnitTest
class OAuthClientDetailsValidateInterceptorUnitTest extends JUnitPlatformSpecification {

    private def model = Stub OAuthClientDetailsModel
    private def cache = Mock EHCacheRegion

    private def interceptor = new OAuthClientDetailsValidateInterceptor(cache)

    @Test
    def "throws IllegalArgumentException when outboundServicesRestTemplateCacheRegion is null"() {
        when:
        new OAuthClientDetailsValidateInterceptor(null)

        then:
        def e = thrown IllegalArgumentException
        e.message.contains 'outboundServicesRestTemplateCacheRegion cannot be null.'
    }

    @Test
    def "cache is not cleared when model is in original state"() {
        given:
        def ctx = Stub(InterceptorContext) {
            isModified(model) >> false
        }

        when:
        interceptor.onValidate(model, ctx)

        then:
        0 * cache.clearCache()
    }

    @Test
    def "cache is cleared when model is modified"() {
        given:
        def ctx = Stub(InterceptorContext) {
            isModified(model) >> true
        }

        when:
        interceptor.onValidate(model, ctx)

        then:
        1 * cache.clearCache()
    }
}