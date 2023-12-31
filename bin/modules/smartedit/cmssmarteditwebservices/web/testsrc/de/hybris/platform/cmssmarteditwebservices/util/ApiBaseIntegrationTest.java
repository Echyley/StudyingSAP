/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmssmarteditwebservices.util;

import static de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother.CatalogVersion.STAGED;
import static de.hybris.platform.cmsfacades.util.models.ContentCatalogModelMother.CatalogTemplate.ID_APPLE;
import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.util.SpringCustomContextLoader;
import de.hybris.platform.cmsfacades.util.models.SiteModelMother;
import de.hybris.platform.cmssmarteditwebservices.constants.CmssmarteditwebservicesConstants;
import de.hybris.platform.core.Registry;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.webservicescommons.jaxb.Jaxb2HttpMessageConverter;
import de.hybris.platform.webservicescommons.testsupport.client.WsSecuredRequestBuilder;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.util.UriTemplate;


@NeedsEmbeddedServer(webExtensions =
{ CmssmarteditwebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
@ContextConfiguration(locations =
{ "classpath:/cmssmarteditwebservices-spring-test.xml" })
public class ApiBaseIntegrationTest extends ServicelayerTest
{
	public static final String OAUTH_CLIENT_ID = "trusted_client";
	public static final String OAUTH_CLIENT_PASS = "secret";
	public static final String OAUTH_ADMIN_ID = "testadmin";
	public static final String OAUTH_ADMIN_PASS = "12341234";
	public static final String OAUTH_INVALID_ID = "invalid";
	public static final String OAUTH_INVALID_PASS = "invalid";
	public static final String OAUTH_CMSMANAGER_ID = "cmsmanager";
	public static final String OAUTH_CMSMANAGER_PASS = "1234";
	public static String OAUTH_CMSEDITOR_ID = "cmseditor";
	public static String OAUTH_CMSEDITOR_PASS = "1234";

	public static String OAUTH_MULTICOUNTRY_CMSMANAGER_ID = "multicountrycmsmanager";
	public static String OAUTH_MULTICOUNTRY_CMSMANAGER_PASS = "1234";

	public static final String URI_CATALOG_ID = "catalogId";
	public static final String URI_VERSION_ID = "versionId";
	public static final String URI_SITE_ID = "siteId";

	protected static SpringCustomContextLoader springCustomContextLoader = null;

	@Resource(name = "cmsseJsonHttpMessageConverter")
	private Jaxb2HttpMessageConverter jsonHttpMessageConverter;

	@Resource
	private SiteModelMother siteModelMother;

	private static final String CONTENT_CATALOG_ENDPOINT = "/v1/sites/{siteId}/contentcatalogs";

	public ApiBaseIntegrationTest()
	{
		if (springCustomContextLoader == null)
		{
			try
			{
				springCustomContextLoader = new SpringCustomContextLoader(getClass());
				springCustomContextLoader.loadApplicationContexts((GenericApplicationContext) Registry.getCoreApplicationContext());
				springCustomContextLoader
						.loadApplicationContextByConvention((GenericApplicationContext) Registry.getCoreApplicationContext());
			}
			catch (final Exception e)
			{
				throw new RuntimeException(e.getMessage(), e);
			}
		}
	}

	@Before
	public void setUpTestData() throws ImpExException
	{
		importCsv("/cmssmarteditwebservices/test/impex/essentialTestDataAuth.impex", "utf-8");
	}

	@Test
	public void shouldGetWsSecuredRequestBuilder()
	{
		siteModelMother.createElectronicsWithAppleStagedAndOnlineCatalog();
		final Response response = getCmsManagerWsSecuredRequestBuilder()
				.path(replaceUriVariablesWithDefaults(CONTENT_CATALOG_ENDPOINT, new HashMap())).build()
				.accept(MediaType.APPLICATION_JSON).get();

		assertResponse(Response.Status.OK, response);
	}

	/**
	 * To retrieve a new authenticated <code>WsSecuredRequestBuilder</code> for the CMS Manager user.
	 *
	 * @return authenticated <code>WsSecuredRequestBuilder</code>
	 */
	protected WsSecuredRequestBuilder getWsSecuredRequestBuilder(final String userId, final String userPassword)
	{
		return new WsSecuredRequestBuilder() //
				.extensionName(CmssmarteditwebservicesConstants.EXTENSIONNAME) //
				.client(OAUTH_CLIENT_ID, OAUTH_CLIENT_PASS) //
				.resourceOwner(userId, userPassword) //
				.grantResourceOwnerPasswordCredentials();
	}

	/**
	 * This is used to convert the given input object into a json object and to return the string representation of the
	 * json object.
	 *
	 * This is useful when dealing with objects extending an Abstract type and the controller's method definition is tied
	 * to the Abstract type. The configuration of the <code>jsonHttpMessageConverter</code> handles the actual object
	 * conversion logic.
	 *
	 * @param input
	 *           - the object to be converted to json
	 * @param classType
	 *           - the class type of the input object
	 * @return json string representation of the input object
	 * @throws JAXBException
	 */
	protected String marshallDto(final Object input, final Class<?> classType) throws JAXBException
	{
		final Marshaller marshaller = jsonHttpMessageConverter.createMarshaller(classType);
		final StringWriter writer = new StringWriter();
		marshaller.marshal(input, writer);
		return writer.toString();
	}

	/**
	 * To retrieve a new authenticated <code>WsSecuredRequestBuilder</code> for the CMS Manager user. The request
	 * supports multi-part form data.
	 *
	 * @return authenticated <code>WsSecuredRequestBuilder</code>
	 */
	protected WsSecuredRequestBuilder getCmsManagerWsSecuredRequestBuilder()
	{
		return getWsSecuredRequestBuilder(OAUTH_CMSMANAGER_ID, OAUTH_CMSMANAGER_PASS);
	}

	/**
	 * To retrieve a new authenticated <code>WsSecuredRequestBuilder</code> for the CMS Manager user in the multi country
	 * setup. The request supports multi-part form data.
	 *
	 * @return authenticated <code>WsSecuredRequestBuilder</code>
	 */
	protected WsSecuredRequestBuilder getMultiCountryCmsManagerWsSecuredRequestBuilder()
	{
		return getWsSecuredRequestBuilder(OAUTH_MULTICOUNTRY_CMSMANAGER_ID, OAUTH_MULTICOUNTRY_CMSMANAGER_PASS);
	}

	/**
	 * To retrieve a new authenticated <code>WsSecuredRequestBuilder</code> for the CMS Editor user. The request supports
	 * multi-part form data.
	 *
	 * @return authenticated <code>WsSecuredRequestBuilder</code>
	 */
	protected WsSecuredRequestBuilder getCmsEditorWsSecuredRequestBuilder()
	{
		return getWsSecuredRequestBuilder(OAUTH_CMSEDITOR_ID, OAUTH_CMSEDITOR_PASS);
	}

	/**
	 * Replace all placeholders in the URI with their respective values provided in the map of variables. When no values
	 * are provided for site, catalog and catalog version, the default values are: <br>
	 * <ul>
	 * <li>site: <code>electronics</code>
	 * <li>catalog: <code>Apple's Content Catalog</code>
	 * <li>catalog version: <code>staged</code>
	 * </ul>
	 *
	 * @param uri
	 *           - with placeholders to be replaced by values in the map of variables
	 * @param variables
	 *           - to replace placeholders in the URI
	 * @return URI where placeholders will be populated by the values contained in the map of variables
	 */
	protected String replaceUriVariablesWithDefaults(final String uri, final Map<String, String> variables)
	{
		if (!variables.containsKey(URI_CATALOG_ID))
		{
			variables.put(URI_CATALOG_ID, ID_APPLE.name());
		}

		if (!variables.containsKey(URI_VERSION_ID))
		{
			variables.put(URI_VERSION_ID, STAGED.getVersion());
		}

		if (!variables.containsKey(URI_SITE_ID))
		{
			variables.put(URI_SITE_ID, SiteModelMother.ELECTRONICS);
		}

		return new UriTemplate(uri).expand(variables).toASCIIString();
	}

}
