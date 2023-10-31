/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.smarteditwebservices.theme.dao.impl;

import com.hybris.backoffice.model.ThemeModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.smarteditwebservices.theme.dao.SmarteditThemeDao;

import java.util.List;

import static de.hybris.platform.core.model.ItemModel.PK;

public class DefaultSmarteditThemeDao implements SmarteditThemeDao {
    private FlexibleSearchService flexibleSearchService;

    @Override
    public List<ThemeModel> getThemes() {
        final var queryString = "SELECT {" + PK + "} FROM {" + ThemeModel._TYPECODE + " as t} where {t."
                + ThemeModel.ACTIVEFORSMARTEDIT + "} = ?activeForSmartedit ORDER BY {t." + ThemeModel.SEQUENCE + "} ASC";
        final var query = new FlexibleSearchQuery(queryString);
        // see: https://help.sap.com/docs/SAP_COMMERCE/d0224eca81e249cb821f2cdf45a82ace/8bc36ba986691014b48be171221d1f4f.html?version=2005&locale=en-US#boolean-parameters-in-queries
        query.addQueryParameter("activeForSmartedit", 1);
        final SearchResult<ThemeModel> search = getFlexibleSearchService().search(query);
        return search.getResult();
    }

    @Override
    public ThemeModel getThemeByCode(String themeCode) {
        final var queryThemeByCodeString = "SELECT {" + PK + "} FROM {" + ThemeModel._TYPECODE + " as t} where {t."
                + ThemeModel.CODE + "} = ?code";
        final var query = new FlexibleSearchQuery(queryThemeByCodeString);
        query.addQueryParameter("code", themeCode);
        final SearchResult<ThemeModel> search = getFlexibleSearchService().search(query);
        if (search.getCount() == 0) {
            return null;
        } else {
            return search.getResult().get(0);
        }
    }

    protected FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }
}
