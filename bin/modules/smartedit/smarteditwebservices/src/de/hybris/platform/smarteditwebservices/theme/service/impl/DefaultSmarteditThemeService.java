/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.smarteditwebservices.theme.service.impl;

import com.hybris.backoffice.model.ThemeModel;
import de.hybris.platform.smarteditwebservices.theme.dao.SmarteditThemeDao;
import de.hybris.platform.smarteditwebservices.theme.service.SmarteditThemeService;

import java.util.List;

/**
 * Default implementation of the Smartedit Theme Service.
 * It has dependencies on the {@link SmarteditThemeDao}
 */
public class DefaultSmarteditThemeService implements SmarteditThemeService {

    private SmarteditThemeDao smarteditThemeDao;

    @Override
    public List<ThemeModel> getThemes() {
        return getSmarteditThemeDao().getThemes();
    }

    @Override
    public ThemeModel getThemeByCode(String code) {
        return getSmarteditThemeDao().getThemeByCode(code);
    }

    protected SmarteditThemeDao getSmarteditThemeDao() {
        return smarteditThemeDao;
    }

    public void setSmarteditThemeDao(SmarteditThemeDao smarteditThemeDao) {
        this.smarteditThemeDao = smarteditThemeDao;
    }
}
