/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omservices.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.sap.core.module.ModuleConfigurationAccess;
import de.hybris.platform.sap.core.saps4omservices.cache.exceptions.SAPS4OMHybrisCacheException;
import de.hybris.platform.sap.core.saps4omservices.cache.service.CacheAccess;
import de.hybris.platform.sap.saps4omservices.exceptions.OutboundServiceException;
import de.hybris.platform.sap.saps4omservices.services.SapS4OMPricingService;
import de.hybris.platform.sap.saps4omservices.services.SapS4SalesOrderSimulationService;
import de.hybris.platform.sap.saps4omservices.utils.SapS4OrderUtil;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.services.BaseStoreService;

/**
 * Fetch price related to product with configuration of cache.
 * if cache is enabled , the pricing would be fetched from cache otherwise API call would fetch the pricing from backend
 */

public class DefaultPricingService implements SapS4OMPricingService {
	private CommonI18NService commonI18NService;
	private UserService userService;
	private BaseStoreService baseStoreService;
	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;
	private CacheAccess sapS4OMSalesPricingCacheRegion;
	private SapS4SalesOrderSimulationService sapS4SalesOrderSimulationService;
	private ModuleConfigurationAccess moduleConfigurationAccess;
	private SapS4OrderUtil sapS4OrderUtil;
	
	public static final String CONF_PROP_IS_CACHED_CATALOG_PRICE = "saps4omcacheprice";
	public static final String CACHEKEY_SAP_PRICING = "SAP_PRICING";
	
	private static final Logger LOG = LoggerFactory.getLogger(DefaultPricingService.class);
	
	@Override
	public List<PriceInformation> getPriceForProduct(ProductModel product) throws OutboundServiceException {
		LOG.debug("Method getPriceForProduct called for S4OM.");
		List<PriceInformation> priceInformation =  new ArrayList<>();
		final boolean isPriceCacheEnabled = getModuleConfigurationAccess().getProperty(CONF_PROP_IS_CACHED_CATALOG_PRICE);
		if (isPriceCacheEnabled) {
			LOG.debug("Cache enabled for pricing");
			priceInformation = (List<PriceInformation>) getSapS4OMSalesPricingCacheRegion().get(getPriceCacheKey(product));
			LOG.debug("Price Info from cache: {}", priceInformation);
		}
				
		if(priceInformation==null || priceInformation.isEmpty() ) {
			priceInformation = getSapS4SalesOrderSimulationService().getPriceDetailsForProduct(product);
			LOG.debug("Price Info from backend system: {} ", priceInformation);
			try {
				if (isPriceCacheEnabled) {
					LOG.debug("Add price info to cache");
					getSapS4OMSalesPricingCacheRegion().put(getPriceCacheKey(product), priceInformation);
				}
			} catch (SAPS4OMHybrisCacheException e) {
				LOG.error("Unable to add the price information to SapPricinigCacheRegion...");
			}
		}
			
		return priceInformation;
	}
	
	protected String getPriceCacheKey(final ProductModel productModel)
	{
		final StringBuilder sapPricingCacheKey = new StringBuilder();
		final String currentCustomerId = getSapS4OrderUtil().getSoldToParty(null);
		sapPricingCacheKey.append(CACHEKEY_SAP_PRICING);
		sapPricingCacheKey.append(productModel.getCode());
		sapPricingCacheKey.append(currentCustomerId);
		sapPricingCacheKey.append(getCommonI18NService().getCurrentCurrency().getIsocode().toUpperCase(Locale.ENGLISH));
		sapPricingCacheKey.append(getCommonI18NService().getCurrentLanguage().getIsocode().toUpperCase(Locale.ENGLISH));
		return sapPricingCacheKey.toString();
	}
	
	protected String getCurrentCustomerID() {

		final UserModel userModel= getUserService().getCurrentUser();

		final B2BCustomerModel b2bCustomer = (userModel instanceof B2BCustomerModel) ? (B2BCustomerModel) userModel : null;
		// if b2bcustomer is null then go for reference customer
		if (b2bCustomer == null) {
			return getBaseStoreService().getCurrentBaseStore().getSAPConfiguration().getSapcommon_referenceCustomer();
		}
		final B2BUnitModel parent = getB2bUnitService().getParent(b2bCustomer);

		return parent.getUid();

	}
	

	public CommonI18NService getCommonI18NService() {
		return commonI18NService;
	}

	public void setCommonI18NService(CommonI18NService commonI18NService) {
		this.commonI18NService = commonI18NService;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public BaseStoreService getBaseStoreService() {
		return baseStoreService;
	}

	public void setBaseStoreService(BaseStoreService baseStoreService) {
		this.baseStoreService = baseStoreService;
	}

	protected B2BUnitService<B2BUnitModel, B2BCustomerModel> getB2bUnitService()
	{
		return b2bUnitService;
	}

	public void setB2bUnitService(final B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
	}

	


	public ModuleConfigurationAccess getModuleConfigurationAccess() {
		return moduleConfigurationAccess;
	}


	public void setModuleConfigurationAccess(ModuleConfigurationAccess moduleConfigurationAccess) {
		this.moduleConfigurationAccess = moduleConfigurationAccess;
	}

	protected SapS4OrderUtil getSapS4OrderUtil() {
		return sapS4OrderUtil;
	}

	public void setSapS4OrderUtil(SapS4OrderUtil sapS4OrderUtil) {
		this.sapS4OrderUtil = sapS4OrderUtil;
	}

	public SapS4SalesOrderSimulationService getSapS4SalesOrderSimulationService() {
		return sapS4SalesOrderSimulationService;
	}

	public void setSapS4SalesOrderSimulationService(SapS4SalesOrderSimulationService sapS4SalesOrderSimulationService) {
		this.sapS4SalesOrderSimulationService = sapS4SalesOrderSimulationService;
	}

	public CacheAccess getSapS4OMSalesPricingCacheRegion() {
		return sapS4OMSalesPricingCacheRegion;
	}

	public void setSapS4OMSalesPricingCacheRegion(CacheAccess sapS4OMSalesPricingCacheRegion) {
		this.sapS4OMSalesPricingCacheRegion = sapS4OMSalesPricingCacheRegion;
	}
	
	

}
