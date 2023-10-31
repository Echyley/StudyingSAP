/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.smarteditwebservices.controllers;

import com.hybris.backoffice.model.ThemeModel;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.smarteditwebservices.constants.SmarteditwebservicesConstants;
import de.hybris.platform.smarteditwebservices.data.Theme;
import de.hybris.platform.smarteditwebservices.dto.ThemeListWsDto;
import de.hybris.platform.webservicescommons.testsupport.client.WsSecuredRequestBuilder;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;
import static org.junit.Assert.assertEquals;

@NeedsEmbeddedServer(webExtensions =
{ SmarteditwebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class ThemeControllerWebServiceTest extends ServicelayerTest
{
	public static final String OAUTH_CLIENT_ID = "trusted_client";
	public static final String OAUTH_CLIENT_PASS = "secret";
	public static final String OAUTH_CMSMANAGER_ID = "cmsmanager";
	public static final String OAUTH_CMSMANAGER_PASS = "1234";

	private static final String THEMES_URI = "v1/themes";
	private static final String CURRENT_USER_THEME_URI = "v1/themes/currentUser/theme";

	private static final String THEME_1 = "theme1";
	private static final String THEME_2 = "theme2";
	private static final String DEFAULT_THEME = "sap_fiori_3";

	@Resource
	private ModelService modelService;

	private WsSecuredRequestBuilder wsSecuredRequestBuilder;

	@Before
	public void setUpUsers() throws Exception
	{
		importCsv("/smarteditwebservices/test/impex/essentialTestDataAuth.impex", "utf-8");
	}

	@Before
	public void setUpConfigurations()
	{
		final ThemeModel theme1 = modelService.create(ThemeModel.class);
		theme1.setCode(THEME_1);
		theme1.setActiveForSmartedit(true);

		final ThemeModel theme2 = modelService.create(ThemeModel.class);
		theme2.setCode(THEME_2);
		theme2.setActiveForSmartedit(true);

		final ThemeModel defaultTheme2 = modelService.create(ThemeModel.class);
		defaultTheme2.setCode(DEFAULT_THEME);
		defaultTheme2.setActiveForSmartedit(true);

		modelService.saveAll(theme1, theme2, defaultTheme2);
	}

	protected void setUpWebRequest(final String username, final String password)
	{
		wsSecuredRequestBuilder = new WsSecuredRequestBuilder() //
				.extensionName(SmarteditwebservicesConstants.EXTENSIONNAME) //
				.client(OAUTH_CLIENT_ID, OAUTH_CLIENT_PASS) //
				.resourceOwner(username, password) //
				.grantResourceOwnerPasswordCredentials();
	}

	@Test
	public void shouldGetThemes()
	{
		setUpWebRequest(OAUTH_CMSMANAGER_ID, OAUTH_CMSMANAGER_PASS);
		final Response allResponse = wsSecuredRequestBuilder //
				.path(THEMES_URI) //
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, allResponse);

		final ThemeListWsDto allEntity = allResponse.readEntity(ThemeListWsDto.class);
		assertEquals(3, allEntity.getThemes().size());

		final Response singleResponse = wsSecuredRequestBuilder //
				.path(THEME_1) //
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, singleResponse);

		final Theme singleEntity = singleResponse.readEntity(Theme.class);
		assertEquals(THEME_1, singleEntity.getCode());
	}

	@Test
	public void shouldGetDefaultThemeWhenCurrentUserNoTheme()
	{
		setUpWebRequest(OAUTH_CMSMANAGER_ID, OAUTH_CMSMANAGER_PASS);
		final Response allResponse = wsSecuredRequestBuilder //
				.path(CURRENT_USER_THEME_URI) //
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, allResponse);

		final Theme entity = allResponse.readEntity(Theme.class);
		assertEquals(DEFAULT_THEME, entity.getCode());
	}

	@Test
	public void shouldSetAndGetThemeForCurrentUser()
	{
		setUpWebRequest(OAUTH_CMSMANAGER_ID, OAUTH_CMSMANAGER_PASS);
		final Response putResponse = wsSecuredRequestBuilder //
				.path(CURRENT_USER_THEME_URI).path(THEME_1) //
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.put(Entity.entity(THEME_1, MediaType.APPLICATION_JSON));

		assertResponse(Status.OK, putResponse);

		var newWsSecuredRequestBuilder = new WsSecuredRequestBuilder() //
				.extensionName(SmarteditwebservicesConstants.EXTENSIONNAME) //
				.client(OAUTH_CLIENT_ID, OAUTH_CLIENT_PASS) //
				.resourceOwner(OAUTH_CMSMANAGER_ID, OAUTH_CMSMANAGER_PASS) //
				.grantResourceOwnerPasswordCredentials();

		final Response currentUserResponse = newWsSecuredRequestBuilder //
				.path(CURRENT_USER_THEME_URI)
				.build()
				.accept(MediaType.APPLICATION_JSON)
				.get();
		assertResponse(Status.OK, currentUserResponse);

		final Theme entity = currentUserResponse.readEntity(Theme.class);
		assertEquals(THEME_1, entity.getCode());
	}

	@Test
	public void shouldThrowErrorWhenCodeIsInValid()
	{
		var invalidCode = "test";
		setUpWebRequest(OAUTH_CMSMANAGER_ID, OAUTH_CMSMANAGER_PASS);
		final Response putResponse = wsSecuredRequestBuilder //
				.path(CURRENT_USER_THEME_URI).path(invalidCode) //
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.put(Entity.entity(invalidCode, MediaType.APPLICATION_JSON));

		assertResponse(Status.BAD_REQUEST, putResponse);
	}
}
