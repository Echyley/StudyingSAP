/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmswebservices.media.controller;

import com.google.common.collect.Maps;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother;
import de.hybris.platform.cmsfacades.util.models.MediaModelMother;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.data.MediaListData;
import de.hybris.platform.cmswebservices.util.ApiBaseIntegrationTest;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.URI_CURRENT_PAGE;
import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.URI_PAGE_SIZE;
import static de.hybris.platform.core.model.media.MediaModel.CODE;
import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

@NeedsEmbeddedServer(webExtensions =
        { CmswebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class MediasControllerWebServiceTest extends ApiBaseIntegrationTest
{
    private static final String ENDPOINT = "/v1/catalogs/{catalogId}/versions/{versionId}/medias";

    private static final String CODE_PHONE_1 = "some.awesome.phone.from.Apple";
    private static final String CODE_PHONE_2 = "another-PHONE-from-Apple";
    private static final String CODE_PHONE_3 = "standard iPhone";
    private static final String CODE_PHONE_4 = "phone... by Apple";
    private static final String CODE_CAMERA_1 = "that-red-camera";
    private static final String CODE_CAMERA_2 = "that.phony/fake camera";

    @Resource
    private CatalogVersionModelMother catalogVersionModelMother;
    @Resource
    private MediaModelMother mediaModelMother;
    @Resource
    private ModelService modelService;

    private CatalogVersionModel catalogVersion;

    @Before
    public void setUp()
    {
        catalogVersion = catalogVersionModelMother.createAppleStagedCatalogVersionModel();
    }

    @Test
    public void shouldFindMedias()
    {
        createMediaModels();

        final String endpoint = replaceUriVariablesWithDefaults(ENDPOINT, Maps.newHashMap());

        final Response response = getCmsManagerWsSecuredRequestBuilder() //
                .path(endpoint) //
                .queryParam(URI_PAGE_SIZE, 10) //
                .queryParam(URI_CURRENT_PAGE, 0) //
                .queryParam("mask", "from")
                .build() //
                .accept(MediaType.APPLICATION_JSON) //
                .get();

        assertResponse(Response.Status.OK, response);

        final MediaListData entity = response.readEntity(MediaListData.class);
        assertThat(entity.getMedia(), hasSize(2));
        assertThat(entity.getMedia(), hasItems(
                allOf(hasProperty(CODE, equalTo("some.awesome.phone.from.Apple"))),
                allOf(hasProperty(CODE, equalTo("another-PHONE-from-Apple")))));
        assertThat(entity.getPagination().getCount(), is(2));
    }

    @Test
    public void shouldNotFindMediasWithInValidMask()
    {
        createMediaModels();

        final String endpoint = replaceUriVariablesWithDefaults(ENDPOINT, Maps.newHashMap());

        final Response response = getCmsManagerWsSecuredRequestBuilder() //
                .path(endpoint) //
                .queryParam(URI_PAGE_SIZE, 10) //
                .queryParam(URI_CURRENT_PAGE, 0) //
                .queryParam("mask", "invalid")
                .build() //
                .accept(MediaType.APPLICATION_JSON) //
                .get();

        assertResponse(Response.Status.OK, response);

        final MediaListData entity = response.readEntity(MediaListData.class);
        assertThat(entity.getMedia(), hasSize(0));
        assertThat(entity.getPagination().getCount(), is(0));
    }

    protected List<MediaModel> createMediaModels()
    {
        return Stream.of(CODE_PHONE_1, CODE_PHONE_2, CODE_PHONE_3, CODE_PHONE_4, CODE_CAMERA_1, CODE_CAMERA_2)
                .map(this::createMediaModel).collect(Collectors.toList());
    }

    protected MediaModel createMediaModel(final String code)
    {
        final MediaModel media = modelService.create(MediaModel.class);
        media.setCode(code);
        media.setCatalogVersion(catalogVersion);
        modelService.save(media);
        return media;
    }
}
