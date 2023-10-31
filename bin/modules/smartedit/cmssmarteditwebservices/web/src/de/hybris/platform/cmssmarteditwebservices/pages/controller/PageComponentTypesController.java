/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmssmarteditwebservices.pages.controller;

import static de.hybris.platform.cmssmarteditwebservices.constants.CmssmarteditwebservicesConstants.API_VERSION;
import static de.hybris.platform.cmssmarteditwebservices.constants.CmssmarteditwebservicesConstants.DEFAULT_CURRENT_PAGE;

import de.hybris.platform.cms2.data.PageableData;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cmsfacades.data.CMSComponentTypesForPageSearchData;
import de.hybris.platform.cmsfacades.exception.ValidationException;
import de.hybris.platform.cmsfacades.types.ComponentTypeFacade;
import de.hybris.platform.cmssmarteditwebservices.data.ComponentTypeData;
import de.hybris.platform.cmssmarteditwebservices.dto.CMSComponentTypeListWsDTO;
import de.hybris.platform.cmssmarteditwebservices.dto.CMSComponentTypesForPageSearchWsDTO;
import de.hybris.platform.cmssmarteditwebservices.dto.PageableWsDTO;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.mapping.config.FieldSetLevelMapping;
import de.hybris.platform.webservicescommons.pagination.WebPaginationUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller that provides an API to retrieve information about component types applicable to the current page.
 */
@RestController
@RequestMapping(API_VERSION + "/catalogs/{catalogId}/versions/{versionId}/pages/{pageId}/types")
@Tag(name = "page component types")
public class PageComponentTypesController
{
    @Resource
    private ComponentTypeFacade componentTypeFacade;

    @Resource
    private DataMapper dataMapper;

    @Resource
    private WebPaginationUtils webPaginationUtils;

    @Resource
    private FieldSetLevelMapping cmsComponentTypeDataMapping;

    @GetMapping(params = { "pageSize", "currentPage" })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @Operation(operationId = "getComponentTypesAvailableToPage", summary = "Retrieves component types.", description = "Retrieves the component types that can be added to a page.")
    @ApiResponse(responseCode = "400", description = "If the given page cannot be found (CMSItemNotFoundException).")
    @ApiResponse(responseCode = "200", description = "The dto containing the list of component types applicable to a page. The results are paged.")
    @Parameter(name = "catalogId", description = "The catalog id", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
    @Parameter(name = "versionId", description = "The uid of the catalog version", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
    @Parameter(name = "pageId", description = "The uid of the page for which to find its valid component types", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
    @Parameter(name = "pageSize", description = "Page size for paging", required = true, schema = @Schema(type = "string"), in = ParameterIn.QUERY)
    @Parameter(name = "currentPage", description = "The requested page number", schema = @Schema(type = "string", defaultValue = DEFAULT_CURRENT_PAGE), in = ParameterIn.QUERY)
    @Parameter(name = "mask", description = "Search mask applied to the type code and name fields, Uses partial matching", schema = @Schema(type = "string"), in = ParameterIn.QUERY)
    @Parameter(name = "langIsoCode", description = "language ISO Code", schema = @Schema(type = "string"), in = ParameterIn.QUERY)
    @Parameter(name = "readOnly", description = "Read only mode for attributes. Is only used if attributes are returned (FULL fields option is used) ", schema = @Schema(type = "boolean", defaultValue = "false"), in = ParameterIn.QUERY)
    public CMSComponentTypeListWsDTO getComponentTypesAvailableToPage(
            @Parameter(description = "Component Types For Page Search DTO", required = true) //
            @ModelAttribute //
            final CMSComponentTypesForPageSearchWsDTO componentTypesForPageSearchWsDTO, //
            @Parameter(description = "Pageable DTO", required = true) //
            @ModelAttribute //
            final PageableWsDTO pageableInfo, //
            @Parameter(description = "Response configuration (list of fields, which should be returned in response)", schema = @Schema(allowableValues = {"BASIC", "DEFAULT", "FULL"})) //
            @RequestParam(defaultValue = "DEFAULT") //
            final String fields
            )
            throws CMSItemNotFoundException
    {
        try
        {
            final PageableData pageableData = getDataMapper().map(pageableInfo, PageableData.class);
            final CMSComponentTypesForPageSearchData searchData = getDataMapper().map(componentTypesForPageSearchWsDTO, CMSComponentTypesForPageSearchData.class);
            searchData.setRequiredFields(getRequiredFields(fields));

            return buildReply(getComponentTypeFacade().getAllComponentTypesForPage(searchData, pageableData), fields);
        }
        catch (final ValidationException e)
        {
            throw new WebserviceValidationException(e.getValidationObject());
        }
    }

    /**
     * Gets the list of fields which should be returned in the response.
     *
     * @param fields
     *         - A string whose value can be BASIC, DEFAULT, or FULL. The value of this string is mapped to the
     *         list of fields which must be returned in the response.
     * @return the list of fields which must be returned in the response.
     */
    protected List<String> getRequiredFields(final String fields)
    {
        return Arrays.stream(cmsComponentTypeDataMapping.getLevelMapping().getOrDefault(fields, "").split(","))
                .map(String::trim)
                .collect(Collectors.toList());
    }

    protected CMSComponentTypeListWsDTO buildReply(final SearchResult<de.hybris.platform.cmsfacades.data.ComponentTypeData> searchResult, final String fields)
    {
        final List<ComponentTypeData> resultList = getDataMapper().mapAsList(searchResult.getResult(), ComponentTypeData.class, fields);

        CMSComponentTypeListWsDTO componentTypeList = new CMSComponentTypeListWsDTO();
        componentTypeList.setComponentTypes(resultList);
        componentTypeList.setPagination(getWebPaginationUtils().buildPagination(searchResult));

        return componentTypeList;
    }

    public DataMapper getDataMapper()
    {
        return dataMapper;
    }

    public ComponentTypeFacade getComponentTypeFacade()
    {
        return componentTypeFacade;
    }

    public WebPaginationUtils getWebPaginationUtils()
    {
        return webPaginationUtils;
    }
}
