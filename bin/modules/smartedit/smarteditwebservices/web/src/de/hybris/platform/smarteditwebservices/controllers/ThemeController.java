/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.smarteditwebservices.controllers;

import de.hybris.platform.smarteditwebservices.theme.facade.SmarteditThemeFacade;
import de.hybris.platform.smarteditwebservices.data.Theme;
import de.hybris.platform.smarteditwebservices.dto.ThemeListWsDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;


import static de.hybris.platform.smarteditwebservices.constants.SmarteditwebservicesConstants.API_VERSION;

/**
 * Controller to manage cms themes data
 */
@Controller
@RequestMapping(API_VERSION + "/themes")
@Tag(name = "themes")
public class ThemeController {
    @Resource
    private SmarteditThemeFacade smarteditThemeFacade;

    @GetMapping
    @ResponseBody
    @Operation(operationId = "getThemes", summary = "Get Themes", description = "Endpoint to retrieve themes")
    public ThemeListWsDto getThemes(
            @Parameter(description = "The language iso code is used to return the Themes in the desired language")
            @RequestParam(required = false) final String langIsoCode) {
        var themes = new ThemeListWsDto();
        themes.setThemes(getSmarteditThemeFacade().getThemes(langIsoCode));
        return themes;
    }

    @GetMapping(value = "/{themeCode}")
    @ResponseBody
    @Operation(operationId = "getThemeByCode", summary = "Get Theme by code", description = "Endpoint to get theme by code")
    public Theme getThemeByCode(@Parameter(description = "Theme data identifier", required = true) @PathVariable("themeCode") final String themeCode,
                                @Parameter(description = "The language iso code is used to return the Themes in the desired language")
                                @RequestParam(required = false) final String langIsoCode) {
        return getSmarteditThemeFacade().getThemeByCode(themeCode, langIsoCode);
    }

    @GetMapping(value = "/currentUser/theme")
    @ResponseBody
    @Operation(operationId = "getCurrentUserTheme", summary = "Get Current User Theme", description = "Endpoint to retrieve current login user theme")
    public Theme getCurrentUserTheme(@Parameter(description = "The language iso code is used to return the Themes in the desired language")
                                     @RequestParam(required = false) final String langIsoCode) {
        return getSmarteditThemeFacade().getCurrentUserTheme(langIsoCode);
    }

    @PutMapping(value = "/currentUser/theme/{themeCode}")
    @ResponseBody
    @Operation(operationId = "updateCurrentUserTheme", summary = "Update Current User Theme", description = "Endpoint to update current login user theme")
    public void updateCurrentUserTheme(@Parameter(description = "Theme data identifier", required = true) @PathVariable("themeCode") final String themeCode) {
        getSmarteditThemeFacade().setCurrentUserTheme(themeCode);
    }

    public SmarteditThemeFacade getSmarteditThemeFacade() {
        return smarteditThemeFacade;
    }

    public void setSmarteditThemeFacade(final SmarteditThemeFacade smarteditThemeFacade) {
        this.smarteditThemeFacade = smarteditThemeFacade;
    }
}
