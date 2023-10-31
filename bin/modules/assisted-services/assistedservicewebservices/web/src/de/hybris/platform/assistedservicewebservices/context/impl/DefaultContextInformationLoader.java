/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.context.impl;

import de.hybris.platform.assistedservicewebservices.context.ContextInformationLoader;
import de.hybris.platform.assistedservicewebservices.exceptions.UnsupportedCurrencyException;
import de.hybris.platform.assistedservicewebservices.exceptions.UnsupportedLanguageException;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.webservicescommons.util.YSanitizer;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DefaultContextInformationLoader implements ContextInformationLoader {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultContextInformationLoader.class);
    private static final String HTTP_REQUEST_PARAM_CURRENCY = "curr";
    private static final String HTTP_REQUEST_PARAM_LANGUAGE = "lang";
    private CommonI18NService commonI18NService;
    private CommerceCommonI18NService commerceCommonI18NService;
    private BaseStoreService baseStoreService;

    @Override
    public LanguageModel setLanguageFromRequest(final HttpServletRequest request) throws UnsupportedLanguageException {
        final String languageString = request.getParameter(HTTP_REQUEST_PARAM_LANGUAGE);
        LanguageModel languageToSet = null;

        if (StringUtils.isNotBlank(languageString)) {
            try {
                languageToSet = getCommonI18NService().getLanguage(languageString);
            } catch (final UnknownIdentifierException e) {
                throw new UnsupportedLanguageException("Language  " + YSanitizer.sanitize(languageString) + " is not supported", e);
            }
        }

        if (languageToSet == null) {
            languageToSet = getCommerceCommonI18NService().getDefaultLanguage();
        }

        final BaseStoreModel currentBaseStore = getBaseStoreService().getCurrentBaseStore();

        if (currentBaseStore != null) {
            final Collection<LanguageModel> storeLanguages = getStoreLanguages(currentBaseStore);

            if (storeLanguages.isEmpty()) {
                throw new UnsupportedLanguageException("Current base store supports no languages!");
            }

            if (!storeLanguages.contains(languageToSet)) {
                throw new UnsupportedLanguageException("The language is not supported by the current base store");
            }
        }


        if (languageToSet != null && !languageToSet.equals(getCommerceCommonI18NService().getCurrentLanguage())) {
            getCommerceCommonI18NService().setCurrentLanguage(languageToSet);

            if (LOG.isDebugEnabled()) {
                LOG.debug(String.format("%s set as current language", languageToSet));
            }
        }
        return languageToSet;
    }


    protected Collection<LanguageModel> getStoreLanguages(final BaseStoreModel currentBaseStore) {
        if (currentBaseStore == null) {
            throw new IllegalStateException("No current base store was set!");
        }
        return currentBaseStore.getLanguages() == null ? Collections.<LanguageModel>emptySet() : currentBaseStore.getLanguages();
    }

    @Override
    public CurrencyModel setCurrencyFromRequest(final HttpServletRequest request) throws UnsupportedCurrencyException {
        final String currencyString = request.getParameter(HTTP_REQUEST_PARAM_CURRENCY);
        CurrencyModel currencyToSet = null;

        if (StringUtils.isNotBlank(currencyString)) {
            try {
                currencyToSet = getCommonI18NService().getCurrency(currencyString);
            } catch (final UnknownIdentifierException e) {
                throw new UnsupportedCurrencyException("Currency " + YSanitizer.sanitize(currencyString) + " is not supported", e);
            }
        }

        if (currencyToSet == null) {
            currencyToSet = getCommerceCommonI18NService().getDefaultCurrency();
        }

        final BaseStoreModel currentBaseStore = getBaseStoreService().getCurrentBaseStore();

        if (currentBaseStore != null) {
            final List<CurrencyModel> storeCurrencies = getCommerceCommonI18NService().getAllCurrencies();

            if (storeCurrencies.isEmpty()) {
                throw new UnsupportedCurrencyException("Current base store supports no currencies!");
            }

            if (!storeCurrencies.contains(currencyToSet)) {
                throw new UnsupportedCurrencyException("The currency is not supported by the current base store");
            }
        }

        if (currencyToSet != null && !currencyToSet.equals(getCommerceCommonI18NService().getCurrentCurrency())) {
            getCommerceCommonI18NService().setCurrentCurrency(currencyToSet);
            if (LOG.isDebugEnabled()) {
                LOG.debug(String.format("%s set as current currency", currencyToSet));
            }
        }

        return currencyToSet;
    }

    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    public void setCommonI18NService(final CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

    public CommerceCommonI18NService getCommerceCommonI18NService() {
        return commerceCommonI18NService;
    }

    public void setCommerceCommonI18NService(final CommerceCommonI18NService commerceCommonI18NService) {
        this.commerceCommonI18NService = commerceCommonI18NService;
    }

    public BaseStoreService getBaseStoreService() {
        return baseStoreService;
    }

    public void setBaseStoreService(final BaseStoreService baseStoreService) {
        this.baseStoreService = baseStoreService;
    }
}
