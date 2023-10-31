/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.smarteditwebservices.theme.facade.impl;

import com.hybris.backoffice.model.ThemeModel;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.smarteditwebservices.theme.SmarteditThemeNotFoundException;
import de.hybris.platform.smarteditwebservices.theme.facade.SmarteditThemeFacade;
import de.hybris.platform.smarteditwebservices.theme.service.SmarteditThemeService;
import de.hybris.platform.smarteditwebservices.data.Theme;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Default implementation of the {@link SmarteditThemeFacade}
 */
public class DefaultSmarteditThemeFacade implements SmarteditThemeFacade {
    private static final Logger LOGGER = getLogger(DefaultSmarteditThemeFacade.class);
    private static final String DEFAULT_SMARTEDIT_THEME = "sap_fiori_3";
    private SmarteditThemeService smarteditThemeService;
    private ModelService modelService;
    private UserService userService;
    private SessionService sessionService;
    private SearchRestrictionService searchRestrictionService;
    private I18NService i18NService;
    private CommonI18NService commonI18NService;
    private AbstractPopulatingConverter<ThemeModel, Theme> themeModelToDataConverter;

    @Override
    public List<Theme> getThemes(String langIsoCode) {
        setCurrentLocale(langIsoCode);
        final List<ThemeModel> smartEditThemeModels = getSmarteditThemeService().getThemes();
        if (smartEditThemeModels == null) {
            return Collections.emptyList();
        }

        return smartEditThemeModels.stream().map(getThemeModelToDataConverter()::convert)
                .collect(Collectors.toList());
    }

    @Override
    public Theme getThemeByCode(String themeCode, String langIsoCode) throws SmarteditThemeNotFoundException {
        setCurrentLocale(langIsoCode);
        final ThemeModel theme = getSmarteditThemeService().getThemeByCode(themeCode);
        if (theme == null) {
            LOGGER.debug("No theme found using this code: {}", themeCode);
            throw new SmarteditThemeNotFoundException("No theme found with " + themeCode);
        } else {
            return getThemeModelToDataConverter().convert(theme);
        }
    }

    @Override
    public Theme getCurrentUserTheme(String langIsoCode) {
        setCurrentLocale(langIsoCode);
        final UserModel currentUser = userService.getCurrentUser();

        final ThemeModel currentUserTheme = currentUser.getThemeForSmartedit();
        if (currentUserTheme == null) {
            LOGGER.debug("No user theme found, use default theme");
            return getThemeModelToDataConverter().convert(getSmarteditThemeService().getThemeByCode(DEFAULT_SMARTEDIT_THEME));
        } else {
            return getThemeModelToDataConverter().convert(currentUserTheme);
        }
    }

    @Override
    public void setCurrentUserTheme(final String themeCode) throws SmarteditThemeNotFoundException {
        getSessionService().executeInLocalView(new SessionExecutionBody() {
            @Override
            public Object execute() {
                try {
                    getSearchRestrictionService().disableSearchRestrictions();
                    UserModel currentUser = getUserService().getCurrentUser();
                    final ThemeModel theme = getSmarteditThemeService().getThemeByCode(themeCode);
                    if (theme == null) {
                        LOGGER.debug("No theme found using this code: {}", themeCode);
                        throw new SmarteditThemeNotFoundException("Failed to set theme as the theme is not found for" + themeCode);
                    } else {
                        currentUser.setThemeForSmartedit(theme);
                        modelService.save(currentUser);
                    }
                } finally {
                    getSearchRestrictionService().enableSearchRestrictions();
                }
                return null;
            }
        });
    }

    /**
     * Sets current locale by language iso code.
     *
     * @param langIsoCode the language iso code.
     */
    protected void setCurrentLocale(final String langIsoCode) {
        if (langIsoCode != null) {
            final var locale = getCommonI18NService().getLocaleForIsoCode(langIsoCode);
            getI18NService().setCurrentLocale(locale);
        }
    }

    protected SmarteditThemeService getSmarteditThemeService() {
        return smarteditThemeService;
    }

    public void setSmarteditThemeService(SmarteditThemeService smarteditThemeService) {
        this.smarteditThemeService = smarteditThemeService;
    }

    protected UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    protected AbstractPopulatingConverter<ThemeModel, Theme> getThemeModelToDataConverter() {
        return themeModelToDataConverter;
    }

    public void setThemeModelToDataConverter(AbstractPopulatingConverter<ThemeModel, Theme> themeModelToDataConverter) {
        this.themeModelToDataConverter = themeModelToDataConverter;
    }

    protected ModelService getModelService() {
        return modelService;
    }

    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    protected SessionService getSessionService() {
        return sessionService;
    }

    public void setSessionService(final SessionService sessionService) {
        this.sessionService = sessionService;
    }

    protected SearchRestrictionService getSearchRestrictionService() {
        return searchRestrictionService;
    }

    public void setSearchRestrictionService(final SearchRestrictionService searchRestrictionService) {
        this.searchRestrictionService = searchRestrictionService;
    }

    protected I18NService getI18NService() {
        return i18NService;
    }

    public void setI18NService(final I18NService i18NService) {
        this.i18NService = i18NService;
    }

    protected CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    public void setCommonI18NService(final CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }
}
