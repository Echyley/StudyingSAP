/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2webservices.odata;

import static de.hybris.platform.integrationservices.IntegrationObjectItemAttributeModelBuilder.integrationObjectItemAttribute;
import static de.hybris.platform.integrationservices.IntegrationObjectItemModelBuilder.integrationObjectItem;
import static de.hybris.platform.integrationservices.IntegrationObjectModelBuilder.integrationObject;
import static de.hybris.platform.integrationservices.util.IntegrationTestUtil.assertModelDoesNotExist;
import static de.hybris.platform.integrationservices.util.IntegrationTestUtil.assertModelExists;
import static de.hybris.platform.odata2webservices.odata.ODataFacadeTestUtils.assertBadRequestWithErrorCode;
import static de.hybris.platform.odata2webservices.odata.ODataFacadeTestUtils.postRequestBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.integrationservices.IntegrationObjectModelBuilder;
import de.hybris.platform.integrationservices.util.IntegrationTestUtil;
import de.hybris.platform.integrationservices.util.ItemTracker;
import de.hybris.platform.integrationservices.util.JsonBuilder;
import de.hybris.platform.odata2services.odata.asserts.ODataAssertions;
import de.hybris.platform.odata2webservices.odata.builders.ODataRequestBuilder;
import de.hybris.platform.odata2webservices.odata.persistence.hooks.SamplePostPersistHook;
import de.hybris.platform.odata2webservices.odata.persistence.hooks.SamplePrePersistHook;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.util.AppendSpringConfiguration;

import java.util.Locale;
import java.util.Optional;

import javax.annotation.Resource;

import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.odata2.api.processor.ODataRequest;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

@IntegrationTest
@AppendSpringConfiguration("classpath:/test/odata2webservices-test-beans-spring.xml")
public class ODataFacadeLegacyPersistHooksIntegrationTest extends ServicelayerTest
{
	private static final String TEST_NAME = "DataFacadePersistHooks";
	private static final String PRE_PERSIST_HOOK = "Pre-Persist-Hook";
	private static final String POST_PERSIST_HOOK = "Post-Persist-Hook";
	private static final String NON_EXISTING_HOOK_NAME = "Non-Existing-Hook";
	private static final String PRE_HOOK_NAME = "samplePrePersistHook";
	private static final String POST_HOOK_NAME = "samplePostPersistHook";
	private static final String SERVICE_NAME = TEST_NAME.concat("_Inbound_IO");
	private static final String ENTITY_SET = "Catalogs";

	@Rule
	@Resource(name = PRE_HOOK_NAME)
	public SamplePrePersistHook samplePrePersistHook;
	@Rule
	@Resource(name = POST_HOOK_NAME)
	public SamplePostPersistHook samplePostPersistHook;
	@Rule
	public IntegrationObjectModelBuilder io = integrationObject().withCode(SERVICE_NAME)
			.withItem(integrationObjectItem().withCode("Catalog").root()
					.withAttribute(integrationObjectItemAttribute().withName("id").unique())
					.withAttribute(integrationObjectItemAttribute().withName("name")));
	@Rule
	public ItemTracker tracker = ItemTracker.track(CatalogModel.class, LanguageModel.class);

	@Resource(name = "defaultODataFacade")
	private ODataFacade facade;

	@Before
	public void setUp() throws ImpExException
	{
		IntegrationTestUtil.importImpEx(
				"INSERT_UPDATE Language;isocode[unique=true];name[lang=en]",
				"                      ;de                  ;German");
	}

	@Test
	public void testExecutesPrePersistHook()
	{
		samplePrePersistHook.givenDoesInExecute(it -> {
			((CatalogModel)it).setId("Hook-Id");
			return Optional.of(it);
		});
		final ODataContext context = createContextWithHeader(jsonCatalog("Submitted-Id"), PRE_PERSIST_HOOK, PRE_HOOK_NAME);

		facade.handleRequest(context);

		assertModelDoesNotExist(catalogModel("Submitted-Id"));
		assertModelExists(catalogModel("Hook-Id"));
	}

	@Test
	public void testExecutesPostPersistHook()
	{
		final ODataContext context = createContextWithHeader(jsonCatalog("Some-Catalog"), POST_PERSIST_HOOK, POST_HOOK_NAME);

		facade.handleRequest(context);

		assertThat(samplePostPersistHook.isExecuted()).isTrue();
	}

	@Test
	public void testPrePersistHookNotFound()
	{
		final ODataContext context = createContextWithHeader(jsonCatalog("MyCatalog"), PRE_PERSIST_HOOK, NON_EXISTING_HOOK_NAME);

		final ODataResponse response = facade.handleRequest(context);

		assertBadRequestWithErrorCode("hook_not_found", response);
		assertThat(samplePostPersistHook.isExecuted()).isFalse();
		assertModelDoesNotExist(catalogModel("MyCatalog"));
	}

	@Test
	public void testRequestBodyIsReturnedWhenPrePersistHookFiltersTheSubmittedNewItemOut()
	{
		samplePrePersistHook.givenDoesInExecute(it -> Optional.empty());
		final ODataContext context = createContextWithBothHooks(jsonCatalog("Excluded"));

		final ODataResponse response = facade.handleRequest(context);

		ODataAssertions.assertThat(response)
				.hasStatus(HttpStatusCodes.CREATED)
				.jsonBody()
				.hasPathWithValue("d.id", "Excluded");
		assertThat(samplePostPersistHook.isExecuted()).isFalse();
		assertModelDoesNotExist(catalogModel("Excluded"));
	}

	@Test
	public void testRequestedLocaleForExistingItemIsReturnedWhenPrePersistHookFiltersTheSubmittedItemOut() throws ImpExException
	{
		final String catalog = "Existing-ReadOnly";
		IntegrationTestUtil.importImpEx(
				"INSERT_UPDATE Catalog; id[unique = true]; name[lang = 'de']",
				"; " + catalog + " ; Katalog");
		samplePrePersistHook.givenDoesInExecute(it -> Optional.empty());
		final ODataContext context = ODataFacadeTestUtils.createContext(postRequest()
				.withHeader(PRE_PERSIST_HOOK, PRE_HOOK_NAME)
				.withAcceptLanguage(Locale.GERMAN)
				.withBody(jsonCatalog(catalog)));

		final ODataResponse response = facade.handleRequest(context);

		ODataAssertions.assertThat(response)
				.hasStatus(HttpStatusCodes.CREATED)
				.jsonBody()
				.hasPathWithValue("d.id", catalog)
				.hasPathWithValue("d.name", "Katalog");
	}

	private String jsonCatalog(final String id)
	{
		return JsonBuilder.json().withId(id).build();
	}

	private ItemModel catalogModel(final String id)
	{
		final CatalogModel model = new CatalogModel();
		model.setId(id);
		return model;
	}

	private ODataContext createContextWithBothHooks(final String content)
	{
		final ODataRequest req = postRequest()
				.withHeader(PRE_PERSIST_HOOK, PRE_HOOK_NAME)
				.withHeader(POST_PERSIST_HOOK, POST_HOOK_NAME)
				.withBody(content)
				.build();
		return ODataFacadeTestUtils.createContext(req);
	}

	private ODataContext createContextWithHeader(final String content, final String headerName, final String headerValue)
	{
		final ODataRequest request = postRequest()
				.withHeader(headerName, headerValue)
				.withBody(content)
				.build();
		return ODataFacadeTestUtils.createContext(request);
	}

	private ODataRequestBuilder postRequest()
	{
		return postRequestBuilder(SERVICE_NAME, ENTITY_SET, APPLICATION_JSON_VALUE);
	}
}
