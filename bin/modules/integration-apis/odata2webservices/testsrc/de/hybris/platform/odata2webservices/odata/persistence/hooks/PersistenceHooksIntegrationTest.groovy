/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2webservices.odata.persistence.hooks

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.core.model.c2l.CountryModel
import de.hybris.platform.inboundservices.persistence.PersistenceContext
import de.hybris.platform.inboundservices.persistence.hook.PersistenceHookProvider
import de.hybris.platform.inboundservices.persistence.hook.impl.DefaultPersistenceHookExecutor
import de.hybris.platform.integrationservices.IntegrationObjectModelBuilder
import de.hybris.platform.integrationservices.util.IntegrationTestUtil
import de.hybris.platform.integrationservices.util.ItemTracker
import de.hybris.platform.integrationservices.util.JsonBuilder
import de.hybris.platform.integrationservices.util.JsonObject
import de.hybris.platform.odata2webservices.odata.ODataFacade
import de.hybris.platform.odata2webservices.odata.ODataFacadeTestUtils
import de.hybris.platform.odata2webservices.odata.builders.ODataRequestBuilder
import de.hybris.platform.servicelayer.ServicelayerSpockSpecification
import de.hybris.platform.util.AppendSpringConfiguration
import org.apache.olingo.odata2.api.commons.HttpStatusCodes
import org.apache.olingo.odata2.api.processor.ODataContext
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import spock.lang.Shared
import spock.lang.Unroll

import javax.annotation.Resource

import static de.hybris.platform.integrationservices.IntegrationObjectItemAttributeModelBuilder.integrationObjectItemAttribute
import static de.hybris.platform.integrationservices.IntegrationObjectItemModelBuilder.integrationObjectItem
import static de.hybris.platform.integrationservices.IntegrationObjectModelBuilder.integrationObject
import static de.hybris.platform.odata2webservices.odata.ODataFacadeTestUtils.postRequestBuilder
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@IntegrationTest
@AppendSpringConfiguration("classpath:/test/odata2webservices-test-beans-spring.xml")
class PersistenceHooksIntegrationTest extends ServicelayerSpockSpecification {
    private static final def TEST_NAME = 'PersistenceHooks'
    private static final def IO_CODE = "${TEST_NAME}_Country"
    private static final def ENTITY_SET = 'Countries'
    private static final def PRE_PERSIST_HOOK_HEADER = 'Pre-Persist-Hook'
    private static final def POST_PERSIST_HOOK_HEADER = 'Post-Persist-Hook'
    private static final def PRE_HOOK_NAME = 'samplePrePersistHook'
    private static final def POST_HOOK_NAME = 'samplePostPersistHook'
    private static final def COUNTRY_CODE = 'us'
    private static final def ERROR_CODE_PATH = 'error.code'
    private static final def ERROR_MSG_PATH = 'error.message.value'
    private static final def ERROR_KEY_PATH = 'error.innererror'

    @Shared
    @ClassRule
    IntegrationObjectModelBuilder IO = integrationObject().withCode(IO_CODE)
            .withItem(integrationObjectItem().withCode('Country').root()
                    .withAttribute(integrationObjectItemAttribute().withName('isocode').unique()))
    @Rule
    ItemTracker itemTracker = ItemTracker.track CountryModel

    SamplePrePersistHook samplePrePersistHook
    SamplePostPersistHook samplePostPersistHook
    def originalHookProviders
    @Resource(name = "testPersistenceHookProvider")
    PersistenceHookProvider testHookProvider
    @Resource(name = "defaultPersistenceHookExecutor")
    DefaultPersistenceHookExecutor hookExecutor
    @Resource(name = "defaultODataFacade")
    ODataFacade facade

    def setup() {
        originalHookProviders = hookExecutor.hookProviders
        hookExecutor.hookProviders = [testHookProvider]
        def context = persistenceContext()
        samplePrePersistHook = testHookProvider.getPrePersistHook(context).get() as SamplePrePersistHook
        samplePostPersistHook = testHookProvider.getPostPersistHook(context).get() as SamplePostPersistHook
    }

    def cleanup() {
        hookExecutor.hookProviders = originalHookProviders
        samplePrePersistHook.after()
        samplePostPersistHook.after()
    }

    @Test
    def 'hook provider has hooks'() {
        given:
        def context = persistenceContext()

        expect:
        testHookProvider.getPrePersistHook(context).get().is samplePrePersistHook
        testHookProvider.getPostPersistHook(context).get().is samplePostPersistHook
    }

    @Test
    def 'hooks not called when they are not specified in the request'() {
        given: 'a request without hook headers'
        def context = createContext jsonCountry(COUNTRY_CODE)

        when:
        def response = facade.handleRequest context

        then:
        response.status == HttpStatusCodes.CREATED
        !samplePrePersistHook.executed
        !samplePostPersistHook.executed
        countryPersisted(COUNTRY_CODE)
    }

    @Test
    def 'pre-persist hook can modify the item being persisted'() {
        given: 'pre-persist hook changes the country code'
        samplePrePersistHook.doesInExecute { item, ctx ->
            ((CountryModel) item).isocode = 'de'
            return Optional.of(item)
        }
        and: 'a request with the pre-persist hook specified'
        def context = createContextWithPrePersistHook jsonCountry(COUNTRY_CODE)

        when:
        def response = facade.handleRequest context

        then:
        response.status == HttpStatusCodes.CREATED
        samplePrePersistHook.executed
        !countryPersisted(COUNTRY_CODE)
        countryPersisted('de')
    }

    @Test
    def 'pre-persist hook can filter item out from being persisted'() {
        given: 'pre-persist hook returns no item model'
        samplePrePersistHook.doesInExecute { item, ctx -> Optional.empty() }
        and: 'a request with the pre- and post- hooks specified'
        def context = createContextWithBothHooks(jsonCountry(COUNTRY_CODE))

        when:
        def response = facade.handleRequest context

        then:
        response.status == HttpStatusCodes.CREATED
        samplePrePersistHook.executed
        !samplePostPersistHook.executed
        !countryPersisted(COUNTRY_CODE)
    }

    @Test
    def "post-persist hook is executed when it's specified in the context"() {
        given: 'a request with the post-persist hook specified'
        def context = createContextWithPostPersistHook jsonCountry(COUNTRY_CODE)

        when:
        def resp = facade.handleRequest context

        then:
        resp.status == HttpStatusCodes.CREATED
        samplePostPersistHook.executed
        countryPersisted(COUNTRY_CODE)
    }

    @Test
    @Unroll
    def "error is reported when the specified #hookType is not found"() {
        given: 'a request that specifies a hook not existing in the application'
        def hookName = 'absent-hook'
        def context = createContextWithHeader jsonCountry(COUNTRY_CODE), hookHeader, hookName

        when:
        def response = facade.handleRequest context

        then:
        response.status == HttpStatusCodes.BAD_REQUEST
        def json = JsonObject.createFrom response.entityAsStream
        json.getString(ERROR_CODE_PATH) == 'hook_not_found'
        json.getString(ERROR_MSG_PATH).contains hookType
        json.getString(ERROR_MSG_PATH).contains hookName
        json.getString(ERROR_KEY_PATH) == COUNTRY_CODE
        and:
        !countryPersisted(COUNTRY_CODE)

        where:
        hookHeader               | hookType
        PRE_PERSIST_HOOK_HEADER  | 'PrePersistHook'
        POST_PERSIST_HOOK_HEADER | 'PostPersistHook'
    }

    @Test
    def 'item is not persisted when pre-persist hook crashes'() {
        given:
        samplePrePersistHook.doesInExecute() { throw new RuntimeException('The hook crashes') }
        and:
        def context = createContextWithBothHooks jsonCountry(COUNTRY_CODE)

        when:
        def response = facade.handleRequest context

        then:
        response.status == HttpStatusCodes.BAD_REQUEST
        def json = JsonObject.createFrom response.entityAsStream
        json.getString(ERROR_CODE_PATH) == 'pre_persist_error'
        json.getString(ERROR_MSG_PATH).contains 'PrePersistHook'
        json.getString(ERROR_MSG_PATH).contains PRE_HOOK_NAME
        json.getString(ERROR_KEY_PATH) == COUNTRY_CODE
        and:
        !countryPersisted(COUNTRY_CODE)
        !samplePostPersistHook.executed
    }

    @Test
    def 'item is not persisted when post-persist hook crashes'() {
        given:
        samplePostPersistHook.doesInExecute {throw new RuntimeException('The hook crashes') }
        and:
        def context = createContextWithPostPersistHook jsonCountry(COUNTRY_CODE)

        when:
        def response = facade.handleRequest context

        then:
        response.status == HttpStatusCodes.BAD_REQUEST
        def json = JsonObject.createFrom response.entityAsStream
        json.getString(ERROR_CODE_PATH) == 'post_persist_error'
        json.getString(ERROR_MSG_PATH).contains 'PostPersistHook'
        json.getString(ERROR_MSG_PATH).contains POST_HOOK_NAME
        json.getString(ERROR_KEY_PATH) == COUNTRY_CODE
        and:
        !countryPersisted(COUNTRY_CODE)
    }

    private static String jsonCountry(String code) {
        JsonBuilder.json()
                .withField('isocode', code)
                .build()
    }

    private static ODataContext createContextWithBothHooks(String content) {
        ODataFacadeTestUtils.createContext postRequest()
                .withHeader(PRE_PERSIST_HOOK_HEADER, PRE_HOOK_NAME)
                .withHeader(POST_PERSIST_HOOK_HEADER, POST_HOOK_NAME)
                .withBody(content)
                .build()
    }

    private static ODataContext createContextWithPrePersistHook(String content, String hook = PRE_HOOK_NAME) {
        createContextWithHeader(content, PRE_PERSIST_HOOK_HEADER, hook)
    }

    private static ODataContext createContextWithPostPersistHook(String content, String hook = POST_HOOK_NAME) {
        createContextWithHeader(content, POST_PERSIST_HOOK_HEADER, hook)
    }

    private static ODataContext createContextWithHeader(String content, String headerName, String headerValue) {
        ODataFacadeTestUtils.createContext postRequest()
                .withHeader(headerName, headerValue)
                .withBody(content)
                .build()
    }

    private static ODataContext createContext(String content) {
        ODataFacadeTestUtils.createContext postRequest().withBody(content).build()
    }

    private static ODataRequestBuilder postRequest() {
        postRequestBuilder(IO_CODE, ENTITY_SET, APPLICATION_JSON_VALUE)
    }

    private static boolean countryPersisted(String code) {
        !IntegrationTestUtil.findAll(CountryModel, { it.isocode == code }).empty
    }

    private PersistenceContext persistenceContext() {
        Stub(PersistenceContext) {
            getPrePersistHook() >> PRE_HOOK_NAME
            getPostPersistHook() >> POST_HOOK_NAME
        }
    }
}
