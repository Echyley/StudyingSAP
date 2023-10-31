/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmswebservices.sites.controller;

import static de.hybris.platform.cmsfacades.util.models.CMSSiteModelMother.TemplateSite.APPAREL;
import static de.hybris.platform.cmsfacades.util.models.CMSSiteModelMother.TemplateSite.ELECTRONICS;
import static de.hybris.platform.cmsfacades.util.models.CMSSiteModelMother.TemplateSite.POWER_TOOLS;
import static de.hybris.platform.cmsfacades.util.models.ContentCatalogModelMother.CatalogTemplate.ID_APPLE;
import static de.hybris.platform.cmsfacades.util.models.ContentCatalogModelMother.CatalogTemplate.ID_ONLINE;
import static de.hybris.platform.cmsfacades.util.models.ContentCatalogModelMother.CatalogTemplate.ID_READONLY;
import static de.hybris.platform.cmsfacades.util.models.ContentCatalogModelMother.CatalogTemplate.ID_STAGED;
import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cmsfacades.util.models.CMSSiteModelMother;
import de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother;
import de.hybris.platform.cmsfacades.util.models.ContentPageModelMother;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.data.SiteData;
import de.hybris.platform.cmswebservices.data.SiteListData;
import de.hybris.platform.cmswebservices.dto.CatalogDataListWsDTO;
import de.hybris.platform.cmswebservices.util.ApiBaseIntegrationTest;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.util.Arrays;
import java.util.Collection;

import javax.annotation.Resource;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.JAXBException;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;


@NeedsEmbeddedServer(webExtensions =
{ CmswebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class SiteControllerWebServiceTest extends ApiBaseIntegrationTest
{
	private static final String SITES_ENDPOINT = "/v1/sites";
	private static final String HEADER_CACHE_CONTROL = "Cache-Control";

	private static final String NAME = "name";
	private static final String UID = "uid";
	private static final int NUMBER_OF_SITE = 2;

	private Matcher<Object> expectedApparelSite;
	private Matcher<Object> expectedElectronicsSite;

	@Resource
	private CMSSiteModelMother cmsSiteModelMother;

	@Resource
	private CatalogVersionModelMother catalogVersionModelMother;

	@Resource
	private ContentPageModelMother contentPageModelMother;

	@Before
	public void setUp()
	{
		expectedApparelSite = allOf( //
				hasProperty(NAME, equalTo(APPAREL.getNames())), //
				hasProperty(UID, equalTo(APPAREL.getUid())));

		expectedElectronicsSite = allOf( //
				hasProperty(NAME, equalTo(ELECTRONICS.getNames())), //
				hasProperty(UID, equalTo(ELECTRONICS.getUid())));
	}

	@Test
	public void getAllSitesWillReturnAnEmptyListOfSitesWhenNothingIsAvailable()
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(SITES_ENDPOINT).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final MultivaluedMap<String, Object> headers = response.getHeaders();
		assertThat(headers, hasEntry(equalToIgnoringCase(HEADER_CACHE_CONTROL), contains("private")));

		final SiteListData entity = response.readEntity(SiteListData.class);
		assertThat(entity.getSites(), empty());
	}

	@Test
	public void getAllSitesShouldReturnEmptyListIfUserHasNoAccess()
	{
		// Arrange
		// Site exists, but user has no permissions
		final CatalogVersionModel[] allowedCatalogVersionModels = new CatalogVersionModel[]
		{ catalogVersionModelMother.createOnlineCatalogVersionModelWithId(ID_ONLINE),
				catalogVersionModelMother.createStagedCatalogVersionModelWithId(ID_STAGED) };

		contentPageModelMother.homePage(allowedCatalogVersionModels[0]);
		cmsSiteModelMother.createSiteWithTemplate(APPAREL, allowedCatalogVersionModels);

		// Act
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(SITES_ENDPOINT).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		// Assert
		assertResponse(Status.OK, response);

		final SiteListData entity = response.readEntity(SiteListData.class);
		assertThat(entity.getSites(), empty());
	}

	@Test
	public void getAllSitesWillReturnAListOfSitesWithApparelWhenApparelIsAvailable()
	{
		// cmsmanager does not have any read or write permissions on the ID_ONLINE and ID_STAGED catalog versions
		final CatalogVersionModel[] notAllowedCatalogVersionModels = new CatalogVersionModel[]
		{ catalogVersionModelMother.createOnlineCatalogVersionModelWithId(ID_ONLINE),
				catalogVersionModelMother.createStagedCatalogVersionModelWithId(ID_STAGED) };

		// cmsmanager has all the permissions defined in resources/cmswebservices/test/impex/essentialTestDataAuth.impex
		final CatalogVersionModel[] allowedCatalogVersionModels = new CatalogVersionModel[]
		{ catalogVersionModelMother.createAppleOnlineCatalogVersionModel(),
				catalogVersionModelMother.createAppleStagedCatalogVersionModel() };

		// create a homepage for the thumbnail
		contentPageModelMother.homePage(allowedCatalogVersionModels[0]);
		cmsSiteModelMother.createSiteWithTemplate(APPAREL, allowedCatalogVersionModels);
		cmsSiteModelMother.createSiteWithTemplate(ELECTRONICS, allowedCatalogVersionModels);
		cmsSiteModelMother.createSiteWithTemplate(POWER_TOOLS, notAllowedCatalogVersionModels);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(SITES_ENDPOINT).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final SiteListData entity = response.readEntity(SiteListData.class);
		assertThat(entity.getSites(), hasSize(NUMBER_OF_SITE));
		final Collection<SiteData> sites = entity.getSites();
		assertThat(sites, containsInAnyOrder(is(expectedApparelSite), is(expectedElectronicsSite)));
	}

	@Test
	public void getAllSitesWillReturnOnlyThoseSitesThatHaveAtleastReadAccessToInactiveCatalogVersion()
	{
		// cmsmanager has all the permissions defined in resources/cmswebservices/test/impex/essentialTestDataAuth.impex
		final CatalogVersionModel[] notAllowedCatalogVersionModels = new CatalogVersionModel[]
		{ catalogVersionModelMother.createOnlineCatalogVersionModelWithId(ID_ONLINE),
				catalogVersionModelMother.createStagedCatalogVersionModelWithId(ID_STAGED) };

		// cmsmanager has all the permissions defined in resources/cmswebservices/test/impex/essentialTestDataAuth.impex
		final CatalogVersionModel[] catalogVersionModelsWithReadAccessToStagedVersion = new CatalogVersionModel[]
		{ catalogVersionModelMother.createStagedCatalogVersionModelWithId(ID_READONLY),
				catalogVersionModelMother.createOnlineCatalogVersionModelWithId(ID_READONLY) };

		// create a homepage for the thumbnail
		contentPageModelMother.homePage(catalogVersionModelsWithReadAccessToStagedVersion[0]);
		cmsSiteModelMother.createSiteWithTemplate(APPAREL, catalogVersionModelsWithReadAccessToStagedVersion);
		cmsSiteModelMother.createSiteWithTemplate(ELECTRONICS, notAllowedCatalogVersionModels);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(SITES_ENDPOINT).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final SiteListData entity = response.readEntity(SiteListData.class);
		assertThat(entity.getSites(), hasSize(1));
	}

	@Test
	public void theApparelSiteItemWillHaveUidAndBaseUrlAndSiteName()
	{
		// cmsmanager has all the permissions defined in resources/cmswebservices/test/impex/essentialTestDataAuth.impex
		final CatalogVersionModel[] allowedCatalogVersionModels = new CatalogVersionModel[]
		{ catalogVersionModelMother.createAppleOnlineCatalogVersionModel(),
				catalogVersionModelMother.createAppleStagedCatalogVersionModel() };

		// create a homepage for the thumbnail
		contentPageModelMother.homePage(allowedCatalogVersionModels[0]);
		cmsSiteModelMother.createSiteWithTemplate(APPAREL, allowedCatalogVersionModels);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(SITES_ENDPOINT).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final SiteListData entity = response.readEntity(SiteListData.class);
		final SiteData siteData = entity.getSites().iterator().next();

		assertThat(siteData, is(expectedApparelSite));
	}

	@Test
	public void getAllSitesWithCatalogIdsWillReturnAListOfSites() throws JAXBException
	{
		// cmsmanager has all the permissions defined in resources/cmswebservices/test/impex/essentialTestDataAuth.impex
		final CatalogVersionModel[] allowedCatalogVersionModels = new CatalogVersionModel[]
				{ catalogVersionModelMother.createAppleOnlineCatalogVersionModel(),
						catalogVersionModelMother.createAppleStagedCatalogVersionModel() };

		// create a homepage for the thumbnail
		contentPageModelMother.homePage(allowedCatalogVersionModels[0]);
		cmsSiteModelMother.createSiteWithTemplate(APPAREL, allowedCatalogVersionModels);
		cmsSiteModelMother.createSiteWithTemplate(ELECTRONICS, allowedCatalogVersionModels);

		final CatalogDataListWsDTO catalogDataListWsDTO = new CatalogDataListWsDTO();
		catalogDataListWsDTO.setCatalogIds(Arrays.asList(ID_APPLE.name()));

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(SITES_ENDPOINT) //
				.path("catalogs") //
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(marshallDto(catalogDataListWsDTO, CatalogDataListWsDTO.class), MediaType.APPLICATION_JSON));

		assertResponse(Status.OK, response);

		final SiteListData entity = response.readEntity(SiteListData.class);
		assertThat(entity.getSites(), hasSize(NUMBER_OF_SITE));
		final Collection<SiteData> sites = entity.getSites();
		assertThat(sites, containsInAnyOrder(is(expectedApparelSite), is(expectedElectronicsSite)));
	}

}
