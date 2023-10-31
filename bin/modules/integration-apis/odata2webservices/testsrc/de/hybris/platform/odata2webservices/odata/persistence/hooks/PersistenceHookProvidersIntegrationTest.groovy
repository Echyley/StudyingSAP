/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2webservices.odata.persistence.hooks

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.core.model.c2l.CountryModel
import de.hybris.platform.inboundservices.persistence.PersistenceContext
import de.hybris.platform.inboundservices.persistence.hook.PersistenceHookProvider
import de.hybris.platform.inboundservices.persistence.hook.impl.DefaultPersistenceHookExecutor
import de.hybris.platform.inboundservices.persistence.hook.impl.SpringBeanPersistenceHookProvider
import de.hybris.platform.integrationservices.IntegrationObjectModelBuilder
import de.hybris.platform.integrationservices.util.IntegrationTestUtil
import de.hybris.platform.integrationservices.util.ItemTracker
import de.hybris.platform.integrationservices.util.JsonBuilder
import de.hybris.platform.integrationservices.util.JsonObject
import de.hybris.platform.odata2webservices.odata.ODataFacade
import de.hybris.platform.odata2webservices.odata.ODataFacadeTestUtils
import de.hybris.platform.servicelayer.ServicelayerSpockSpecification
import de.hybris.platform.util.AppendSpringConfiguration
import org.apache.olingo.odata2.api.commons.HttpStatusCodes
import org.apache.olingo.odata2.api.processor.ODataContext
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import spock.lang.Shared

import javax.annotation.Resource

import static de.hybris.platform.integrationservices.IntegrationObjectItemAttributeModelBuilder.integrationObjectItemAttribute
import static de.hybris.platform.integrationservices.IntegrationObjectItemModelBuilder.integrationObjectItem
import static de.hybris.platform.integrationservices.IntegrationObjectModelBuilder.integrationObject
import static de.hybris.platform.odata2webservices.odata.ODataFacadeTestUtils.postRequestBuilder
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@IntegrationTest
@AppendSpringConfiguration("classpath:/test/odata2webservices-test-beans-spring.xml")
class PersistenceHookProvidersIntegrationTest extends ServicelayerSpockSpecification {
    private static final def TEST_NAME = 'PersistenceHooks'
    private static final def IO_CODE = "${TEST_NAME}_Country"
    private static final def ENTITY_SET = 'Countries'
    private static final def HOOK_HEADER = 'Pre-Persist-Hook'
    private static final def HOOK_NAME = 'samplePrePersistHook'
    private static final def COUNTRY_CODE = 'us'
    private static final def COUNTRY_PAYLOAD = JsonBuilder.json().withField('isocode', COUNTRY_CODE).build()
    private static final def NAME_SET_BY_BEAN = 'Beanland'
    private static final def ERROR_CODE_PATH = 'error.code'
    private static final def ERROR_MSG_PATH = 'error.message.value'
    private static final def ERROR_KEY_PATH = 'error.innererror'

    @Shared
    @ClassRule
    IntegrationObjectModelBuilder IO = integrationObject().withCode(IO_CODE)
            .withItem(integrationObjectItem().withCode('Country').root()
                    .withAttribute(integrationObjectItemAttribute().withName('isocode').unique())
                    .withAttribute(integrationObjectItemAttribute().withName('name')))

    @Rule
    ItemTracker itemTracker = ItemTracker.track CountryModel

    SamplePrePersistHook beanHook
    def originalHookProviders
    @Resource(name = "testPersistenceHookProvider")
    PersistenceHookProvider beanHookProvider
    @Resource(name = "defaultPersistenceHookExecutor")
    DefaultPersistenceHookExecutor hookExecutor
    @Resource(name = "defaultODataFacade")
    ODataFacade facade

    def setup() {
        originalHookProviders = hookExecutor.hookProviders
        replaceSpringBeanProviderWithProviderFromTestAppContext()
        beanHook = beanHookProvider.getPrePersistHook(persistenceContext()).get() as SamplePrePersistHook
        beanHook.doesInExecute { item, ctx ->
            def country = item as CountryModel
            country.name = NAME_SET_BY_BEAN
            return Optional.of(country)
        }
    }

    def cleanup() {
        hookExecutor.hookProviders = originalHookProviders
    }

    @Test
    def "request can specify the #hook hook explicitly"() {
        given:
        def request = createRequestContext COUNTRY_PAYLOAD, "$scheme://$HOOK_NAME"

        when:
        facade.handleRequest request

        then:
        persistedCountry().name == expectedName

        where:
        hook   | scheme | expectedName
        'bean' | 'bean' | NAME_SET_BY_BEAN
    }

    @Test
    def 'error is reported when #hookName is not found by any provider'() {
        given:
        def request = createRequestContext COUNTRY_PAYLOAD, hookName

        when:
        def response = facade.handleRequest request

        then:
        response.status == HttpStatusCodes.BAD_REQUEST
        def json = JsonObject.createFrom response.entityAsStream
        json.getString(ERROR_CODE_PATH) == 'hook_not_found'
        json.getString(ERROR_MSG_PATH).contains 'PrePersistHook'
        json.getString(ERROR_MSG_PATH).contains hookName
        json.getString(ERROR_KEY_PATH) == COUNTRY_CODE

        and:
        persistedCountry().is null

        where:
        hookName << ['bean://missing_hook', 'missing_hook_without_scheme']
    }

    private static ODataContext createRequestContext(String content, String hook = HOOK_NAME) {
        ODataFacadeTestUtils.createContext postRequestBuilder(IO_CODE, ENTITY_SET, APPLICATION_JSON_VALUE)
                .withHeader(HOOK_HEADER, hook)
                .withBody(content)
                .build()
    }

    private static CountryModel persistedCountry() {
        IntegrationTestUtil.findAny(CountryModel, { it.isocode == COUNTRY_CODE })
                .orElse(null) as CountryModel
    }

    private PersistenceContext persistenceContext() {
        Stub(PersistenceContext) {
            getPrePersistHook() >> HOOK_NAME
        }
    }

    private def replaceSpringBeanProviderWithProviderFromTestAppContext() {
        def newProviders = hookExecutor.hookProviders.collect {
            it instanceof SpringBeanPersistenceHookProvider ? beanHookProvider : it
        }
        hookExecutor.hookProviders = newProviders
    }
}
