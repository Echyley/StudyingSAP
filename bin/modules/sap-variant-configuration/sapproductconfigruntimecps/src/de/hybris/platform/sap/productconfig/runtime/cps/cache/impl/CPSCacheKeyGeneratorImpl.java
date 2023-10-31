/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.cache.impl;

import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.apiregistryservices.strategies.ConsumedDestinationLocatorStrategy;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.regioncache.key.CacheUnitValueType;
import de.hybris.platform.sap.productconfig.runtime.cps.cache.CPSCacheKeyGenerator;
import de.hybris.platform.sap.productconfig.runtime.cps.client.ConfigurationClient;
import de.hybris.platform.sap.productconfig.runtime.cps.client.KbDeterminationClient;
import de.hybris.platform.sap.productconfig.runtime.cps.client.MasterDataClient;
import de.hybris.platform.sap.productconfig.runtime.cps.client.PricingClient;
import de.hybris.platform.sap.productconfig.runtime.interf.cache.ProductConfigurationUserIdProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.cache.impl.ProductConfigurationCacheKey;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.site.impl.DefaultBaseSiteConsumedDestinationLocatorStrategy;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;


/**
 * Default implementation of {@link CPSCacheKeyGenerator}
 */
public class CPSCacheKeyGeneratorImpl implements CPSCacheKeyGenerator
{
	/**
	 * Key for accessing the pricing document data type
	 */
	static final String PRICING_DOCUMENT_DATA_TYPE = "PRICING_DOCUMENT_DATA_TYPE";

	/**
	 * Key for accessing the language
	 */
	static final String KEY_PRODUCT = "PRODUCT";
	/**
	 * Key for accessing the language
	 */
	public static final String KEY_LANGUAGE = "LANGUAGE";
	/**
	 * Key for accessing the currency
	 */
	public static final String KEY_CURRENCY = "CURRENCY";
	/**
	 * Key for accessing the knowledgebase id
	 */
	public static final String KEY_KB_ID = "KB_ID";
	/**
	 * Key for accessing the user id
	 */
	static final String KEY_USER_ID = "USER_ID";
	/**
	 * Key for accessing the configuration id
	 */
	static final String KEY_CONFIG_ID = "CONFIG_ID";
	/**
	 * Key for accessing the cps service tenant
	 */
	static final String KEY_CPS_SERVICE_TENANT = "CPS_SERVICE_TENANT";
	/**
	 * Key for accessing the cps service url
	 */
	static final String KEY_CPS_SERVICE_URL = "CPS_SERVICE_URL";
	static final String TENANT_UNIT = "TENANT";
	protected static final String KEY_TENANT_ID = "TENANT_ID";

	protected static final String CONFIGRATION_SERVICE_ID = ConfigurationClient.class.getSimpleName();
	protected static final String KB_DETERMINATION_SERVICE_ID = KbDeterminationClient.class.getSimpleName();
	protected static final String MASTER_DATA_SERVICE_ID = MasterDataClient.class.getSimpleName();
	protected static final String PRICING_SERVICE_ID = PricingClient.class.getSimpleName();
	protected static final String TYPECODE_COOKIE = "__COOKIE__";
	protected static final String TYPECODE_MASTER_DATA = "__MASTER_DATA__";
	protected static final String TYPECODE_KNOWLEDGEBASES = "__KNOWLEDGEBASES__";
	protected static final String TYPECODE_PRICING_DOCUMENT_DATA = "__PRICING_DOCUMENT_DATA__";
	protected static final String TYPECODE_VALUE_PRICES = "__VALUE_PRICES__";
	protected static final String TYPECODE_RUNTIME_CONFIGURATION = "__RUNTIME_CONFIGURATION__";

	private final BaseSiteService baseSiteService;
	private final ProductConfigurationUserIdProvider userIdProvider;
	private final ConsumedDestinationLocatorStrategy consumedDestinationLocatorStrategy;
	private final CommonI18NService i18NService;

	public CPSCacheKeyGeneratorImpl(final BaseSiteService baseSiteService, final ProductConfigurationUserIdProvider userIdProvider,
			final ConsumedDestinationLocatorStrategy consumedDestinationLocatorStrategy, final CommonI18NService i18nService)
	{
		this.baseSiteService = baseSiteService;
		this.userIdProvider = userIdProvider;
		this.consumedDestinationLocatorStrategy = consumedDestinationLocatorStrategy;
		this.i18NService = i18nService;
	}



	@Override
	public ProductConfigurationCacheKey createMasterDataCacheKey(final String kbId, final String lang)
	{
		final Map<String, String> keyMap = retrieveBasicCPSParameters(MASTER_DATA_SERVICE_ID);
		keyMap.put(CPSCacheKeyGeneratorImpl.KEY_KB_ID, kbId);
		keyMap.put(CPSCacheKeyGeneratorImpl.KEY_LANGUAGE, lang);
		return new ProductConfigurationCacheKey(keyMap, CacheUnitValueType.SERIALIZABLE, TYPECODE_MASTER_DATA, getTenantId());
	}

	@Override
	public ProductConfigurationCacheKey createKnowledgeBaseHeadersCacheKey(final String product)
	{
		final Map<String, String> keyMap = retrieveBasicCPSParameters(KB_DETERMINATION_SERVICE_ID);
		keyMap.put(CPSCacheKeyGeneratorImpl.KEY_PRODUCT, product);
		return new ProductConfigurationCacheKey(keyMap, CacheUnitValueType.SERIALIZABLE, TYPECODE_KNOWLEDGEBASES, getTenantId());
	}

	@Override
	public ProductConfigurationCacheKey createCookieCacheKey(final String configId)
	{
		final Map<String, String> keyMap = retrieveBasicCPSParameters(CONFIGRATION_SERVICE_ID);
		keyMap.put(CPSCacheKeyGeneratorImpl.KEY_CONFIG_ID, configId);
		return new ProductConfigurationCacheKey(keyMap, CacheUnitValueType.SERIALIZABLE, TYPECODE_COOKIE, getTenantId());
	}

	@Override
	public ProductConfigurationCacheKey createValuePricesCacheKey(final String kbId, final String pricingProduct)
	{
		final Map<String, String> keyMap = retrieveBasicCPSParameters(PRICING_SERVICE_ID);
		keyMap.put(CPSCacheKeyGeneratorImpl.KEY_KB_ID, kbId);
		if (null != pricingProduct)
		{
			keyMap.put(CPSCacheKeyGeneratorImpl.KEY_PRODUCT, pricingProduct);
		}
		keyMap.put(CPSCacheKeyGeneratorImpl.KEY_USER_ID, getUserIdProvider().getCurrentUserId());
		keyMap.put(CPSCacheKeyGeneratorImpl.KEY_CURRENCY, getCurrencyIso());
		return new ProductConfigurationCacheKey(keyMap, CacheUnitValueType.SERIALIZABLE, TYPECODE_VALUE_PRICES, getTenantId());
	}

	@Override
	public ProductConfigurationCacheKey createConfigurationCacheKey(final String configId)
	{
		return createConfigurationCacheKey(configId, Collections.emptyMap());
	}

	@Override
	public ProductConfigurationCacheKey createConfigurationCacheKey(final String configId, final Map<String, String> contextAttr)
	{
		final Map<String, String> keyMap = new HashMap();
		keyMap.put(CPSCacheKeyGeneratorImpl.KEY_CONFIG_ID, configId);
		keyMap.put(CPSCacheKeyGeneratorImpl.KEY_USER_ID,
				contextAttr.containsKey(KEY_USER_ID) ? contextAttr.get(KEY_USER_ID) : getUserIdProvider().getCurrentUserId());
		return new ProductConfigurationCacheKey(keyMap, CacheUnitValueType.NON_SERIALIZABLE, TYPECODE_RUNTIME_CONFIGURATION,
				contextAttr.containsKey(KEY_TENANT_ID) ? contextAttr.get(KEY_TENANT_ID) : getTenantId());
	}

	protected String getCurrencyIso()
	{
		final CurrencyModel currencyModel = getI18NService().getCurrentCurrency();
		return Optional.ofNullable(currencyModel).map(CurrencyModel::getIsocode).orElse(null);
	}

	protected Map<String, String> retrieveBasicCPSParameters(final String serviceId)
	{
		final Pair<String, String> parameterPair = getCPSServiceParameter(serviceId);
		final Map<String, String> keyMap = new HashMap();
		keyMap.put(CPSCacheKeyGeneratorImpl.KEY_CPS_SERVICE_URL, parameterPair.getLeft());
		keyMap.put(CPSCacheKeyGeneratorImpl.KEY_CPS_SERVICE_TENANT, parameterPair.getRight());
		return keyMap;
	}

	protected String getTenantId()
	{

		return Registry.hasCurrentTenant() ? Registry.getCurrentTenant().getTenantID() : TENANT_UNIT;
	}

	protected String getCurrentBaseSite()
	{
		return getBaseSiteService().getCurrentBaseSite().getUid();
	}

	protected Pair<String, String> getCPSServiceParameter(final String serviceId)
	{
		final ConsumedDestinationModel lookup = getConsumedDestinationLocatorStrategy().lookup(serviceId);
		return Pair.of(lookup.getUrl(),
				lookup.getAdditionalProperties().get(DefaultBaseSiteConsumedDestinationLocatorStrategy.BASE_SITE));
	}

	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	protected ProductConfigurationUserIdProvider getUserIdProvider()
	{
		return userIdProvider;
	}

	protected ConsumedDestinationLocatorStrategy getConsumedDestinationLocatorStrategy()
	{

		return consumedDestinationLocatorStrategy;
	}

	protected CommonI18NService getI18NService()
	{
		return i18NService;
	}
}
