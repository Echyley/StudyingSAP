/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.smarteditwebservices.theme.facade;

import de.hybris.platform.smarteditwebservices.theme.SmarteditThemeNotFoundException;
import de.hybris.platform.smarteditwebservices.data.Theme;

import java.util.List;

/**
 * Interface methods for SmarteditThemeFacade.
 * The implementing class will provide methods for persisting theme models.
 */
public interface SmarteditThemeFacade {

    /**
     * Retrieve all themes that active for smartedit.
     *
     * @param langIsoCode The language iso code to be used on theme search.
     * @return a list of {@link Theme}
     */
    List<Theme> getThemes(String langIsoCode);

    /**
     * Retrieve a theme by code
     *
     * @param themeCode   the theme's unique identifier
     * @param langIsoCode The language iso code to be used on theme search.
     * @return the {@link Theme} represented by this code
     * @throws SmarteditThemeNotFoundException {@link SmarteditThemeNotFoundException}
     */
    Theme getThemeByCode(String themeCode, String langIsoCode) throws SmarteditThemeNotFoundException;

    /**
     * Retrieve current login user theme
     *
     * @param langIsoCode The language iso code to be used on theme search.
     * @return a {@link Theme} instance, or null if it does not exist in the database
     */
    Theme getCurrentUserTheme(String langIsoCode);

    /**
     * setting current login user theme
     *
     * @param themeCode the theme's unique identifier
     * @throws SmarteditThemeNotFoundException {@link SmarteditThemeNotFoundException}
     */
    void setCurrentUserTheme(String themeCode) throws SmarteditThemeNotFoundException;
}
