/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmswebservices.media.controller;

import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.dto.MediaFolderListWsDTO;
import de.hybris.platform.cmswebservices.util.ApiBaseIntegrationTest;
import de.hybris.platform.core.model.media.MediaFolderModel;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Test;


@NeedsEmbeddedServer(webExtensions =
		{ CmswebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class MediaFolderControllerWebServiceTest extends ApiBaseIntegrationTest
{
	private static final String GET_ENDPOINT = "/v1/mediafolders";
	@Resource
	private ModelService modelService;

	@Test
	public void shouldGetAllMediaFolders() {
		createMediaFolder("images");
		createMediaFolder("documents");
		createMediaFolder("pdf");
		final Response response = getCmsManagerWsSecuredRequestBuilder()
				.path(GET_ENDPOINT)
				.queryParam("mask", "")
				.queryParam("pageSize", "25")
				.queryParam("currentPage", "0")
			   .build()
				.accept(MediaType.APPLICATION_JSON)
				.get();

		assertResponse(Response.Status.OK, response);
		assertResponse(Response.Status.OK, response);
		final MediaFolderListWsDTO mediaFolderList = response.readEntity(MediaFolderListWsDTO.class);
		assertThat(mediaFolderList.getMediaFolders(), hasSize(5));
	}

	@Test
	public void shouldGetAllMediaFoldersByQuery() {
		createMediaFolder("images");
		createMediaFolder("images2");
		createMediaFolder("pdf");
		final Response response = getCmsManagerWsSecuredRequestBuilder()
				.path(GET_ENDPOINT)
				.queryParam("mask", "images")
				.queryParam("pageSize", "25")
				.queryParam("currentPage", "0")
				.build()
				.accept(MediaType.APPLICATION_JSON)
				.get();

		assertResponse(Response.Status.OK, response);
		final MediaFolderListWsDTO mediaFolderList = response.readEntity(MediaFolderListWsDTO.class);
		assertThat(mediaFolderList.getMediaFolders(), hasSize(2));
	}

	protected MediaFolderModel createMediaFolder(final String code)
	{
		final MediaFolderModel mediaFolder = modelService.create(MediaFolderModel.class);
		mediaFolder.setQualifier(code);
		mediaFolder.setPath(code);
		modelService.save(mediaFolder);
		return mediaFolder;
	}
}
