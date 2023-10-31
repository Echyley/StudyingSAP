/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmswebservices.media.controller;

import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.API_VERSION;

import de.hybris.platform.cms2.data.PageableData;
import de.hybris.platform.cmsfacades.data.MediaData;
import de.hybris.platform.cmsfacades.media.MediaFacade;
import de.hybris.platform.cmswebservices.data.MediaListData;
import de.hybris.platform.cmswebservices.dto.PageableWsDTO;
import de.hybris.platform.cmswebservices.security.IsAuthorizedCmsManager;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.pagination.WebPaginationUtils;

import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;


/**
 * Controller that handles searching and creating media container
 */
@RestController
@IsAuthorizedCmsManager
@RequestMapping(API_VERSION + "/catalogs/{catalogId}/versions/{versionId}/medias")
@Tag(name = "medias")
public class MediasController
{
    @Resource
    private MediaFacade mediaFacade;

    @Resource
    private DataMapper dataMapper;

    @Resource
    private WebPaginationUtils webPaginationUtils;

    @GetMapping(params = { "pageSize" })
    @ResponseBody
    @Operation(summary = "Finds medias by partial to full code matching.", description = "Retrieves a list of available medias using a free text search field.", operationId = "getMediasByText")

    @ApiResponse(responseCode = "200", description = "Item which serves as a wrapper object that contains a list of MediaData; never null")

    @Parameter(name = "catalogId", description = "The catalog id", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
    @Parameter(name = "versionId", description = "The catalog version identifier", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
    @Parameter(name = "pageSize", description = "The maximum number of elements in the result list.", required = true, schema = @Schema(type = "string"), in = ParameterIn.QUERY)
    @Parameter(name = "currentPage", description = "The requested page number", required = false, schema = @Schema(type = "string"), in = ParameterIn.QUERY)

    public MediaListData findMedias(
            @Parameter(description = "The string value on which medias will be filtered")
            @RequestParam(required = false) final String mask,
            @Parameter(description = "The string value on which medias will be filtered")
            @RequestParam(required = false) final String mimeType,
            @Parameter(description = "PageableWsDTO", required = true)
            @ModelAttribute final PageableWsDTO pageableInfo)
    {
        final Optional<PageableData> pageableDataOption = Optional.of(pageableInfo).map(pageableWsDTO -> getDataMapper().map(pageableWsDTO, PageableData.class));
        final PageableData pageableData = pageableDataOption.isPresent() ? pageableDataOption.get() : null;

        final SearchResult<MediaData> mediaSearchResult = getMediaFacade().findMedias(mask, mimeType, pageableData);

        final MediaListData medias = new MediaListData();
        medias.setMedia(mediaSearchResult.getResult().stream() //
                .map(mediaData -> getDataMapper().map(mediaData, de.hybris.platform.cmswebservices.data.MediaData.class)) //
                .collect(Collectors.toList()));
        medias.setPagination(getWebPaginationUtils().buildPagination(mediaSearchResult));
        return medias;
    }

    protected MediaFacade getMediaFacade()
    {
        return mediaFacade;
    }

    public void setMediaFacade(final MediaFacade mediaFacade)
    {
        this.mediaFacade = mediaFacade;
    }

    protected DataMapper getDataMapper()
    {
        return dataMapper;
    }

    public void setDataMapper(final DataMapper dataMapper)
    {
        this.dataMapper = dataMapper;
    }

    protected WebPaginationUtils getWebPaginationUtils()
    {
        return webPaginationUtils;
    }

    public void setWebPaginationUtils(final WebPaginationUtils webPaginationUtils)
    {
        this.webPaginationUtils = webPaginationUtils;
    }
}
