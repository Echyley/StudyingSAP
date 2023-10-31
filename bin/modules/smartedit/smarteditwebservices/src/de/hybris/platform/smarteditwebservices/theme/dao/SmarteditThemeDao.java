/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.smarteditwebservices.theme.dao;

import com.hybris.backoffice.model.ThemeModel;

import java.util.List;

/**
 * Interface for SmartEdit Theme DAO
 */
public interface SmarteditThemeDao {
    /**
     * Retrieve all themes that active for smartedit
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
