/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.cache.impl;

import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.regioncache.key.CacheUnitValueType;
import de.hybris.platform.sap.productconfig.runtime.interf.cache.ProductConfigurationUserIdProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.cache.impl.ProductConfigurationCacheKey;
import de.hybris.platform.sap.productconfig.services.cache.CacheKeyGenerator;
import de.hybris.platform.sap.productconfig.services.constants.SapproductconfigservicesConstants;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class CacheKeyGeneratorImpl implements CacheKeyGenerator
{

	protected static final String NO_ACTIVE_TENANT = "NO_ACTIVE_TENANT";
	protected static final String KEY_CONFIG_ID = "CONFIG_ID";
	protected static final String KEY_PRODUCT_CODE = "PRODUCT_CODE";
	protected static final String KEY_IS_SESSION_BOUND_TO_CONFIG = "SESSION_BOUND";
	protected static final String KEY_USER_ID = "USER_ID";
	protected static final String KEY_CURRENCY = "CURRENCY";
	protected static final String TYPECODE_ANALYTICS_DATA = "__ANALYTICS_DATA__";
	protected static final String TYPECODE_PRICE_SUMMARY = "__PRICE_SUMMARY__";
	protected static final String TYPECODE_CONFIG = "__CONFIG__";
	protected static final String TYPECODE_CLASSIFICATION_SYSTEM_CPQ_ATTRIBUTES = "__CLASSIFICATION_SYSTEM_CPQ_ATTRIBUTES__";
	protected static final String KEY_TENANT_ID = "TENANT_ID";
	protected static final String KEY_IS_ANONYMOUS = "IS_ANONYMOUS";

	private final ProductConfigurationUserIdProvider userIdProvider;
	private final SessionService sessionService;
	private final CommonI18NService i18NService;

	public CacheKeyGeneratorImpl(final ProductConfigurationUserIdProvider userIdProvider, final SessionService sessionService,
			final CommonI18NService i18nService)
	{
		this.userIdProvider = userIdProvider;
		this.sessionService = sessionService;
		this.i18NService = i18nService;
	}

	@Override
	public void populateCacheKeyContextAttributes(final Map<String, String> ctxtAttributes)
	{
		ctxtAttributes.put(KEY_CURRENCY, getCurrencyIso());
		ctxtAttributes.put(KEY_USER_ID, getUserIdProvider().getCurrentUserId());
		ctxtAttributes.put(KEY_IS_SESSION_BOUND_TO_CONFIG, String.valueOf(isSessionBoundToConfiguration()));
		ctxtAttributes.put(KEY_IS_ANONYMOUS, String.valueOf(getUserIdProvider().isAnonymousUser()));
		ctxtAttributes.put(KEY_TENANT_ID, getTenantId());
	}

	/**
	 * Creates cache key for price summary and uses the currency code from the commerce currency model to distinguish
	 * different currencies
	 */
	@Override
	public ProductConfigurationCacheKey createPriceSummaryCacheKey(final String configId)
	{
		final Map<String, String> keys = prepareBasicCacheKeyMap(configId);
		keys.put(CacheKeyGeneratorImpl.KEY_CURRENCY, getCurrencyIso());
		return new ProductConfigurationCacheKey(keys, CacheUnitValueType.SERIALIZABLE, TYPECODE_PRICE_SUMMARY, getTenantId());
	}

	@Override
	public ProductConfigurationCacheKey createPriceSummaryCacheKey(final String configId, final Map<String, String> ctxtAttributes)
	{
		final Map<String, String> keys = prepareBasicCacheKeyMap(configId, ctxtAttributes);
		keys.put(KEY_CURRENCY, ctxtAttributes.get(KEY_CURRENCY));
		return new ProductConfigurationCacheKey(keys, CacheUnitValueType.SERIALIZABLE, TYPECODE_PRICE_SUMMARY,
				ctxtAttributes.get(KEY_TENANT_ID));
	}

	@Override
	public ProductConfigurationCacheKey createAnalyticsDataCacheKey(final String configId)
	{
		return createCacheKey(configId, TYPECODE_ANALYTICS_DATA);
	}


	@Override
	public ProductConfigurationCacheKey createAnalyticsDataCacheKey(final String configId,
			final Map<String, String> ctxtAttributes)
	{
		return createCacheKey(configId, TYPECODE_ANALYTICS_DATA, ctxtAttributes);
	}

	@Override
	public ProductConfigurationCacheKey createConfigCacheKey(final String configId)
	{
		return createCacheKey(configId, TYPECODE_CONFIG);
	}

	@Override
	public ProductConfigurationCacheKey createConfigCacheKey(final String configId, final Map<String, String> ctxtAttributes)
	{
		return createCacheKey(configId, TYPECODE_CONFIG, ctxtAttributes);
	}

	@Override
	public ProductConfigurationCacheKey createClassificationSystemCPQAttributesCacheKey(final String productCode)
	{
		return createCacheKeyWithoutUserKey(productCode, TYPECODE_CLASSIFICATION_SYSTEM_CPQ_ATTRIBUTES);
	}

	protected ProductConfigurationCacheKey createCacheKey(final String configId, final String typeCode)
	{
		final Map<String, String> keys = prepareBasicCacheKeyMap(configId);
		return new ProductConfigurationCacheKey(keys, CacheUnitValueType.SERIALIZABLE, typeCode, getTenantId());
	}

	protected ProductConfigurationCacheKey createCacheKey(final String configId, final String typeCode,
			final Map<String, String> ctxtAttributes)
	{
		final Map<String, String> keys = prepareBasicCacheKeyMap(configId, ctxtAttributes);
		return new ProductConfigurationCacheKey(keys, CacheUnitValueType.SERIALIZABLE, typeCode, ctxtAttributes.get(KEY_TENANT_ID));
	}

	protected Map<String, String> prepareBasicCacheKeyMap(final String configId)
	{
		final Map<String, String> keys = new HashMap<>();
		keys.put(KEY_CONFIG_ID, configId);
		if (isSessionBoundToConfiguration() || !getUserIdProvider().isAnonymousUser())
		{
			keys.put(KEY_USER_ID, getUserIdProvider().getCurrentUserId());
		}
		return keys;
	}

	protected Map<String, String> prepareBasicCacheKeyMap(final String configId, final Map<String, String> ctxtAttributes)
	{
		final Map<String, String> keys = new HashMap<>();
		keys.put(KEY_CONFIG_ID, configId);
		if (Boolean.parseBoolean(ctxtAttributes.get(KEY_IS_SESSION_BOUND_TO_CONFIG))
				|| !Boolean.parseBoolean(ctxtAttributes.get(KEY_IS_ANONYMOUS)))
		{
			/**
			 * in case of an anonymous session this will be in fact the session id
			 *
			 * @see ProductConfigurationUserIdProvider#getCurrentUserId()
			 **/
			keys.put(KEY_USER_ID, ctxtAttributes.get(KEY_USER_ID));
		}
		return keys;
	}

	protected String getCurrencyIso()
	{
		final CurrencyModel currencyModel = getI18NService().getCurrentCurrency();
		return Optional.ofNullable(currencyModel).map(CurrencyModel::getIsocode).orElse("null");
	}

	protected ProductConfigurationCacheKey createCacheKeyWithoutUserKey(final String productCode, final String typeCode)
	{
		final Map<String, String> keys = new HashMap<>();
		keys.put(KEY_PRODUCT_CODE, productCode);
		return new ProductConfigurationCacheKey(keys, CacheUnitValueType.SERIALIZABLE, typeCode, getTenantId());
	}

	protected String getTenantId()
	{
		if (Registry.hasCurrentTenant())
		{
			return Registry.getCurrentTenant().getTenantID();
		}
		return NO_ACTIVE_TENANT;
	}

	protected ProductConfigurationUserIdProvider getUserIdProvider()
	{
		return userIdProvider;
	}

	protected SessionService getSessionService()
	{
		return this.sessionService;
	}

	protected boolean isSessionBoundToConfiguration()
	{
		boolean sessionBoundToConfiguration = true;
		final Object attribute = getSessionService()
				.getAttribute(SapproductconfigservicesConstants.SESSION_NOT_BOUND_TO_CONFIGURATIONS);
		if (attribute instanceof Boolean)
		{
			sessionBoundToConfiguration = !((Boolean) attribute).booleanValue();
		}
		return sessionBoundToConfiguration;
	}

	protected CommonI18NService getI18NService()
	{
		return i18NService;
	}

}
