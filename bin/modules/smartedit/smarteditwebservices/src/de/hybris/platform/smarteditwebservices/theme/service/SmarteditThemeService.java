/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.smarteditwebservices.theme.service;

import com.hybris.backoffice.model.ThemeModel;

import java.util.List;

/**
 * Provide methods for managing SmartEdit theme information.
 */
public interface SmarteditThemeService {
    /**
     * Retrieve all Themes active for smartedit.
     *
     * @return a list of {@link ThemeModel}
     */
    List<ThemeModel> getThemes();

    /**
     * Retrieve the theme by code
     *
     * @param themeCode the unique identifier for Theme
     * @return a {@link ThemeModel}
     */
    ThemeModel getThemeByCode(String themeCode);
}
