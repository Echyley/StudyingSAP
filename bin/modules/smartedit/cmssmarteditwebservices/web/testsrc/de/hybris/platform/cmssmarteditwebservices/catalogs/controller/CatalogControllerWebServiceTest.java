/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmssmarteditwebservices.catalogs.controller;

import static de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother.CatalogVersion.ONLINE;
import static de.hybris.platform.cmsfacades.util.models.ContentCatalogModelMother.CatalogTemplate.*;
import static de.hybris.platform.cmsfacades.util.models.SiteModelMother.MULTI_COUNTRY_EUROPE_CARS_SITE;
import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;

import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cmsfacades.util.models.SiteModelMother;
import de.hybris.platform.cmssmarteditwebservices.constants.CmssmarteditwebservicesConstants;
import de.hybris.platform.cmssmarteditwebservices.dto.CatalogListWsDTO;
import de.hybris.platform.cmssmarteditwebservices.dto.CatalogWsDTO;
import de.hybris.platform.cmssmarteditwebservices.util.ApiBaseIntegrationTest;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.hamcrest.Matcher;
import org.junit.Test;


@NeedsEmbeddedServer(webExtensions =
{ CmssmarteditwebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class CatalogControllerWebServiceTest extends ApiBaseIntegrationTest
{
	private static final String HEADER_CACHE_CONTROL = "Cache-Control";
	private static final String CONTENT_CATALOG_ENDPOINT = "/v1/sites/{siteId}/contentcatalogs";
	private static final String PRODUCT_CATALOG_ENDPOINT = "/v1/sites/{siteId}/productcatalogs";

	private static final String ACTIVE = "active";
	private static final String CATALOG_ID = "catalogId";
	private static final String CATALOG_NAME = "catalogName";
	private static final String NAME = "name";
	private static final String PAGE_DISPLAY_CONDITIONS = "pageDisplayConditions";
	private static final String VERSION = "version";
	private static final String VERSIONS = "versions";
	private static final String PARENTS = "parents";
	private static final String SITES = "sites";

	@Resource
	private SiteModelMother siteModelMother;

	@Test
	public void shouldGetAllContentCatalogs()
	{
		siteModelMother.createElectronicsWithAppleStagedAndOnlineCatalog();

		final Response response = getCmsManagerWsSecuredRequestBuilder()
				.path(replaceUriVariablesWithDefaults(CONTENT_CATALOG_ENDPOINT, new HashMap<String, String>())).build()
				.accept(MediaType.APPLICATION_JSON).get();

		assertResponse(Status.OK, response);

		final MultivaluedMap<String, Object> headers = response.getHeaders();
		assertThat(headers, hasEntry(equalToIgnoringCase(HEADER_CACHE_CONTROL), contains("no-cache")));

		final CatalogListWsDTO catalogs = response.readEntity(CatalogListWsDTO.class);

		assertThat(catalogs.getCatalogs(), hasSize(greaterThanOrEqualTo(1)));

		final Matcher<CatalogWsDTO> expectedAppleContentCatalog = //
				allOf(hasProperty(CATALOG_ID, equalTo(ID_APPLE.name())), //
						hasProperty(NAME, equalTo(ID_APPLE.getHumanName())), //
						hasProperty(VERSIONS, hasSize(greaterThanOrEqualTo(2))), //
						hasProperty(VERSIONS,
								hasItem( //
										allOf(hasProperty(ACTIVE, equalTo(true)), //
												hasProperty(VERSION, equalTo(ONLINE.getVersion())), //
												hasProperty(PAGE_DISPLAY_CONDITIONS, hasSize(greaterThanOrEqualTo(3)))))));
		assertThat(catalogs.getCatalogs(), hasItem(expectedAppleContentCatalog));
	}

	@Test
	public void shouldGetAllContentCatalogsWithParents() throws ImpExException
	{
		// GIVEN
		importCsv("/cmssmarteditwebservices/test/impex/essentialMultiCountryTestDataAuth.impex", "utf-8");

		final Map<String, String> myParams = new HashMap<>();
		myParams.put(URI_SITE_ID, MULTI_COUNTRY_EUROPE_CARS_SITE);

		// WHEN
		final Response response = getMultiCountryCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(CONTENT_CATALOG_ENDPOINT, myParams)).build()
				.accept(MediaType.APPLICATION_JSON).get();

		// THEN
		assertResponse(Response.Status.OK, response);
		final CatalogListWsDTO entity = response.readEntity(CatalogListWsDTO.class);

		assertThat(entity.getCatalogs(), hasSize(greaterThanOrEqualTo(1)));
		final Matcher<CatalogWsDTO> expectedAppleContentCatalog = //
				allOf(hasProperty(CATALOG_ID, equalTo(MULTI_COUNTRY_ID_EUROPE_CARS.name())), //
						hasProperty(NAME, equalTo(MULTI_COUNTRY_ID_EUROPE_CARS.getHumanName())), //
						hasProperty(VERSIONS, hasSize(greaterThanOrEqualTo(2))), //
						hasProperty(VERSIONS,
								hasItem( //
										allOf(hasProperty(ACTIVE, equalTo(true)), //
												hasProperty(VERSION, equalTo(ONLINE.getVersion())), //
												hasProperty(PAGE_DISPLAY_CONDITIONS, hasSize(greaterThanOrEqualTo(3)))))), //
						hasProperty(PARENTS, hasSize(greaterThanOrEqualTo(1))), //
		 				hasProperty(PARENTS,
								hasItem( //
										allOf(hasProperty(CATALOG_ID, equalTo(MULTI_COUNTRY_ID_CARS.name())),
												hasProperty(CATALOG_NAME, equalTo(MULTI_COUNTRY_ID_CARS.getHumanName())),
												hasProperty(VERSIONS, hasSize(greaterThanOrEqualTo(2))),
												hasProperty(SITES, hasSize(greaterThanOrEqualTo(1))))
								)));
		assertThat(entity.getCatalogs(), hasItem(expectedAppleContentCatalog));

	}

	@Test
	public void shouldGetAllProductCatalogs()
	{
		siteModelMother.createNorthAmericaElectronicsWithAppleStagedCatalog();

		final Response response = getCmsManagerWsSecuredRequestBuilder()
				.path(replaceUriVariablesWithDefaults(PRODUCT_CATALOG_ENDPOINT, new HashMap<String, String>())).build()
				.accept(MediaType.APPLICATION_JSON).get();

		assertResponse(Status.OK, response);

		final CatalogListWsDTO catalogs = response.readEntity(CatalogListWsDTO.class);

		assertThat(catalogs.getCatalogs(), hasSize(greaterThanOrEqualTo(1)));

		final Matcher<CatalogWsDTO> expectedPhoneProductCatalog = //
				allOf(hasProperty(CATALOG_ID, equalTo(ID_PHONES.name())), //
						hasProperty(NAME, equalTo(ID_PHONES.getHumanName())), //
						hasProperty(VERSIONS, hasSize(greaterThanOrEqualTo(3))), //
						hasProperty(VERSIONS,
								hasItem( //
										allOf(hasProperty(ACTIVE, equalTo(true)), //
												hasProperty(VERSION, equalTo(ONLINE.getVersion()))))));
		assertThat(catalogs.getCatalogs(), hasItem(expectedPhoneProductCatalog));
	}


}
