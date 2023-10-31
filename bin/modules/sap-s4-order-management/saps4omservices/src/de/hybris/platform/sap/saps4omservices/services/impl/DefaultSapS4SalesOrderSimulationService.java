/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omservices.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.sap.core.saps4omservices.cache.service.impl.SapS4OMProductAvailabilityCache;
import de.hybris.platform.sap.saps4omservices.exceptions.OutboundServiceException;
import de.hybris.platform.sap.saps4omservices.services.SapS4OMOutboundService;
import de.hybris.platform.sap.saps4omservices.services.SapS4OMProductAvailability;
import de.hybris.platform.sap.saps4omservices.services.SapS4OMRequestPayloadCreator;
import de.hybris.platform.sap.saps4omservices.services.SapS4OMResponseProcessor;
import de.hybris.platform.sap.saps4omservices.services.SapS4SalesOrderSimulationService;
import de.hybris.platform.sap.saps4omservices.utils.SapS4OrderUtil;
import de.hybris.platform.saps4omservices.dto.SAPS4OMData;
import de.hybris.platform.saps4omservices.dto.SAPS4OMRequestData;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;

public class DefaultSapS4SalesOrderSimulationService implements SapS4SalesOrderSimulationService {

	private static final String DEFAULT_PLANT = "defaultPlant";
	private static final String PRICE_INFO_MAP = "priceInfoMap";

	public static final String STOCK_AVAILABILITY_MAP = "sapStockAvailabilityMap";
	
	private static final Logger LOG = LoggerFactory.getLogger(DefaultSapS4SalesOrderSimulationService.class);
	private SapS4OMOutboundService sapS4OMOutboundService;
	private SapS4OMRequestPayloadCreator sapS4OMRequestPayloadCreator;
	private SapS4OMResponseProcessor sapS4OMResponseProcessor;
	private SapS4OMProductAvailabilityCache sapS4OMProductAvailabilityCache;
	private B2BUnitService b2bUnitService;
	private BaseStoreService baseStoreService;
	private UserService userService;

	@Override
	public List<PriceInformation> getPriceDetailsForProduct(ProductModel productModel) throws OutboundServiceException {
		LOG.debug("Method call getPriceDetailsForProduct");
		List<ProductModel> productModelList = new ArrayList<>();
		productModelList.add(productModel);
		final String plant = (productModel.getSapPlant()!=null)?productModel.getSapPlant().getCode():DEFAULT_PLANT;
		final String  currentCustomerId = getCurrentCustomerID();
		Map<String, SapS4OMProductAvailability> stockLevelMap;
		SAPS4OMRequestData requestData = getSapS4OMRequestPayloadCreator().getPayloadForOrderSimulation(productModelList, false);
	
			SAPS4OMData salesOrderSimulationResponse = getSapS4OMOutboundService()
					.simulateOrder(SapS4OrderUtil.S4_ORDER_SIMULATE_DESTINATION,
							SapS4OrderUtil.S4_ORDER_SIMULATE_DESTINATION_TARGET, requestData);
			final Map<String, Object> simulationDetailsMap = getSapS4OMResponseProcessor().processOrderSimulationResponse(salesOrderSimulationResponse, productModel);
			LOG.debug("Price info map from backed {}", simulationDetailsMap.get(PRICE_INFO_MAP));
			stockLevelMap = (Map<String, SapS4OMProductAvailability>) simulationDetailsMap.get(STOCK_AVAILABILITY_MAP);
			SapS4OMProductAvailability sapS4OMProductAvailability = stockLevelMap.get((productModel.getSapPlant() != null) ? productModel.getSapPlant().getCode()
					: DEFAULT_PLANT);
			getSapS4OMProductAvailabilityCache().cacheProductAvailability(sapS4OMProductAvailability, productModel, currentCustomerId, plant);
			return ((Map<String, List<PriceInformation>>) simulationDetailsMap.get(PRICE_INFO_MAP)).get(productModel.getCode());
		
	}

	@Override
	public Map<String, List<PriceInformation>> getPriceDetailsForProducts(List<ProductModel> productModels) throws OutboundServiceException {
		LOG.debug("Method call getPriceDetailsForProducts");
		SAPS4OMRequestData requestData = getSapS4OMRequestPayloadCreator().getPayloadForOrderSimulation(productModels, false);
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		SAPS4OMData salesOrderSimulationResponse = null;
		
			salesOrderSimulationResponse = getSapS4OMOutboundService().simulateOrder(
					SapS4OrderUtil.S4_ORDER_SIMULATE_DESTINATION, SapS4OrderUtil.S4_ORDER_SIMULATE_DESTINATION_TARGET,
					requestData);
		final Map<String, Object> stockPriceMap = getSapS4OMResponseProcessor().processOrderSimulationResponse(salesOrderSimulationResponse, null);
		return (Map<String, List<PriceInformation>>) stockPriceMap.get(PRICE_INFO_MAP);
	}

	@Override
	public void setCartDetails(AbstractOrderModel cartModel) throws OutboundServiceException {
		LOG.debug("Method call setCartDetails");
		if (cartModel.getEntries().isEmpty()) {
			LOG.debug("No entries found in the cart");
			return;
		}
		SAPS4OMRequestData requestData = getSapS4OMRequestPayloadCreator().getPayloadForOrderSimulation(cartModel);

			SAPS4OMData salesOrderSimulationResponse = getSapS4OMOutboundService()
					.simulateOrder(SapS4OrderUtil.S4_ORDER_SIMULATE_DESTINATION,
							SapS4OrderUtil.S4_ORDER_SIMULATE_DESTINATION_TARGET, requestData);
			getSapS4OMResponseProcessor().processOrderSimulationResponse(salesOrderSimulationResponse, cartModel);
		
	}

	@Override
	public Boolean checkCreditLimitExceeded(ItemModel cartModel, UserModel user) throws OutboundServiceException {
		LOG.debug("Method call checkCreditLimitExceeded");
		SAPS4OMRequestData requestData = getSapS4OMRequestPayloadCreator().getPayloadForOrderSimulation((AbstractOrderModel) cartModel);
			SAPS4OMData salesOrderSimulationResponse = getSapS4OMOutboundService()
					.simulateOrder(SapS4OrderUtil.S4_ORDER_SIMULATE_DESTINATION,
							SapS4OrderUtil.S4_ORDER_SIMULATE_DESTINATION_TARGET, requestData);
			if (salesOrderSimulationResponse.getResult().getCreditDetails() != null) {
				String creditStatus = salesOrderSimulationResponse.getResult().getCreditDetails()
						.getCreditCheckStatus();
				return "B".equalsIgnoreCase(creditStatus);

			}
			LOG.debug("Credit check limit is disabled");
		return false;
	}
	
	@Override
	public SapS4OMProductAvailability getStockAvailability(ProductModel productModel, BaseStoreModel baseStore) throws OutboundServiceException {
		LOG.debug("Method call getStockAvailability");
		List<ProductModel> productModelList = new ArrayList<>();
		productModelList.add(productModel);
		Map<String, SapS4OMProductAvailability> stockLevelMap;
			Map<String, Object> responseMap;
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			SAPS4OMRequestData requestData = getSapS4OMRequestPayloadCreator().getPayloadForOrderSimulation(productModelList, true);
				SAPS4OMData responseData = getSapS4OMOutboundService().simulateOrder(
						SapS4OrderUtil.S4_ORDER_SIMULATE_DESTINATION, SapS4OrderUtil.S4_ORDER_SIMULATE_DESTINATION_TARGET,
						requestData);
				responseMap = getSapS4OMResponseProcessor().processOrderSimulationResponse(responseData, productModel);
				stockLevelMap = (Map<String, SapS4OMProductAvailability>) responseMap.get(STOCK_AVAILABILITY_MAP);
		return stockLevelMap.get((productModel.getSapPlant() != null) ? productModel.getSapPlant().getCode()
				: DEFAULT_PLANT);

	}
	
	protected String getCurrentCustomerID() {

		UserModel userModel= getUserService().getCurrentUser();

		B2BCustomerModel b2bCustomer = (userModel instanceof B2BCustomerModel) ? (B2BCustomerModel) userModel : null;
		// if b2bcustomer is null then go for reference customer
		if (b2bCustomer == null) {
			LOG.debug("Not a B2B customer so using the reference customer");
			return getBaseStoreService().getCurrentBaseStore().getSAPConfiguration().getSapcommon_referenceCustomer();
		}
		final B2BUnitModel parent = (B2BUnitModel) getB2bUnitService().getParent(b2bCustomer);

		return parent.getUid();

	}

	protected SapS4OMOutboundService getSapS4OMOutboundService() {
		return sapS4OMOutboundService;
	}

	public void setSapS4OMOutboundService(SapS4OMOutboundService sapS4OMOutboundService) {
		this.sapS4OMOutboundService = sapS4OMOutboundService;
	}
	
	public SapS4OMResponseProcessor getSapS4OMResponseProcessor() {
		return sapS4OMResponseProcessor;
	}

	public void setSapS4OMResponseProcessor(SapS4OMResponseProcessor sapS4OMResponseProcessor) {
		this.sapS4OMResponseProcessor = sapS4OMResponseProcessor;
	}

	public SapS4OMRequestPayloadCreator getSapS4OMRequestPayloadCreator() {
		return sapS4OMRequestPayloadCreator;
	}

	public void setSapS4OMRequestPayloadCreator(SapS4OMRequestPayloadCreator sapS4OMRequestPayloadCreator) {
		this.sapS4OMRequestPayloadCreator = sapS4OMRequestPayloadCreator;
	}

	public SapS4OMProductAvailabilityCache getSapS4OMProductAvailabilityCache() {
		return sapS4OMProductAvailabilityCache;
	}

	public void setSapS4OMProductAvailabilityCache(SapS4OMProductAvailabilityCache sapS4OMProductAvailabilityCache) {
		this.sapS4OMProductAvailabilityCache = sapS4OMProductAvailabilityCache;
	}
	protected BaseStoreService getBaseStoreService() {
		return baseStoreService;
	}

	public void setBaseStoreService(BaseStoreService baseStoreService) {
		this.baseStoreService = baseStoreService;
	}

	protected UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	protected B2BUnitService getB2bUnitService() {
		return b2bUnitService;
	}

	public void setB2bUnitService(B2BUnitService b2bUnitService) {
		this.b2bUnitService = b2bUnitService;
	}

}
