/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omservices.services.impl;


import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.hybris.platform.basecommerce.enums.InStockStatus;
import de.hybris.platform.commerceservices.model.FutureStockModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.sap.sapmodel.model.SAPPlantLogSysOrgModel;
import de.hybris.platform.sap.sapmodel.model.SAPPricingConditionModel;
import de.hybris.platform.sap.sapmodel.services.impl.SAPDefaultUnitService;
import de.hybris.platform.sap.saps4omservices.constants.Saps4omservicesConstants;
import de.hybris.platform.sap.saps4omservices.enums.S4ProceduresSubtotal;
import de.hybris.platform.sap.saps4omservices.services.ResponsePayloadModifierHook;
import de.hybris.platform.sap.saps4omservices.services.SapS4OMProductAvailability;
import de.hybris.platform.sap.saps4omservices.services.SapS4OMResponseProcessor;
import de.hybris.platform.sap.saps4omservices.services.SapS4OrderManagementConfigService;
import de.hybris.platform.saps4omservices.dto.PricingElementData;
import de.hybris.platform.saps4omservices.dto.PricingElementsData;
import de.hybris.platform.saps4omservices.dto.SAPS4OMData;
import de.hybris.platform.saps4omservices.dto.SAPS4OMItemData;
import de.hybris.platform.saps4omservices.dto.SAPS4OMItemsData;
import de.hybris.platform.saps4omservices.dto.ScheduleLineData;
import de.hybris.platform.saps4omservices.dto.ScheduleLinesData;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.util.DiscountValue;
import de.hybris.platform.util.PriceValue;
import de.hybris.platform.util.TaxValue;

public class DefaultSapS4OMResponseProcessor implements SapS4OMResponseProcessor{
	
	private static final int ORDER_ENTRY_MULTIPLIER = 10;
	private static final String BULK_QUANTITY = "9999999";
	private static final String PRICE_INFO_MAP = "priceInfoMap";
	private static final String SAP_PRICING_CONDTIONS_MAP = "sapPricingCondtionsMap";
	private static final String TAX_MAP = "taxMap";
	private static final String DISCOUNT_MAP = "discountMap";
	private static final String STOCK_INFO_MAP = "stockInfoMap";
	private static final String SCHEDULE_LINE_MAP = "scheduleLineMap";
	private static final String DEFAULT_PLANT = "defaultPlant";
	public static final String NETAMOUNT = "NetAmount";
	public static final String TAXAMOUNT = "TaxAmount";
	public static final String COSTAMOUNT = "CostAmount";
	public static final String SUBTOTALAMOUNT1 = "Subtotal1Amount";
	public static final String SUBTOTALAMOUNT2 = "Subtotal2Amount";
	public static final String SUBTOTALAMOUNT3 = "Subtotal3Amount";
	public static final String SUBTOTALAMOUNT4 = "Subtotal4Amount";
	public static final String SUBTOTALAMOUNT5 = "Subtotal5Amount";
	public static final String SUBTOTALAMOUNT6 = "Subtotal6Amount";
	public static final String CONF_PROP_PRICE_SUBTOTAL = "salesordersimulate_pricesub";
	public static final String CONF_PROP_DISCOUNTS_SUBTOTAL = "salesordersimulate_discountsub";
	public static final String CONF_PROP_TAXES_SUBTOTAL = "salesordersimulate_taxessub";
	public static final String CONF_PROP_DELIVERY_SUBTOTAL = "salesordersimulate_deliverysub";
	public static final String CONF_PROP_PAYMENT_COST_SUBTOTAL = "salesordersimulate_paymentsub";
	public static final String STOCK_AVAILABILITY_MAP = "sapStockAvailabilityMap";
	
	
	private SAPDefaultUnitService sapUnitService;
	private BaseStoreService baseStoreService;
	private List<ResponsePayloadModifierHook> responsePayloadModifierHooks;
	private SessionService sessionService;
	private SapS4OrderManagementConfigService sapS4OrderManagementConfigService;
			
	private static final Logger LOG = LoggerFactory.getLogger(DefaultSapS4OMResponseProcessor.class);

	@Override
	public void processOrderCreationResponse(SAPS4OMData responseData, OrderModel order) {

		order.setCode(responseData.getResult().getSalesOrder());
		order.setConsignments(Collections.emptySet());
		order.setSapOrders(Collections.emptySet());
		order.getEntries().stream().forEach(entry -> entry.setProductInfos(Optional.ofNullable(entry.getProductInfos()).orElse(Collections.emptyList())));
		callModifierHookForOrder(responseData, order);	
	}

	@Override
	public Map<String, Object> processOrderSimulationResponse(SAPS4OMData salesOrderSimulationResponse, ItemModel productModel) {
		final Map<String, Object> simulationDetailsMap = processResponseData(salesOrderSimulationResponse, productModel);
		return simulationDetailsMap;
	}
	
	protected Map<String, Object> processResponseData(SAPS4OMData salesOrderSimulationData,
			ItemModel itemModel) {
		if (salesOrderSimulationData == null || salesOrderSimulationData.getResult() == null) {
			return new HashMap<>();

		}
		Map<String, Object> itemInfoMap = new HashMap<>();
		Map<String, List<PriceInformation>> productPriceMap = new HashMap<>();
		Map<String, List<StockLevelModel>> stockLevelMap = new HashMap<>();
		Map<String, SapS4OMProductAvailability> stockAvailabilityMap = new HashMap<>();
		Map<String, DiscountValue> discountMap = new HashMap<>();
		Map<String, TaxValue> taxMap = new HashMap<>();
		Map<String, List<SAPPricingConditionModel>> sapPricingCondtionsMap = new HashMap<>();
        Map<String, Collection<String>> scheduleLineMap = new HashMap<>();
  	    itemInfoMap.put(SCHEDULE_LINE_MAP, scheduleLineMap);
		itemInfoMap.put(PRICE_INFO_MAP, productPriceMap);
		itemInfoMap.put(STOCK_INFO_MAP, stockLevelMap);
		itemInfoMap.put(DISCOUNT_MAP, discountMap);
		itemInfoMap.put(TAX_MAP, taxMap);
		itemInfoMap.put(SAP_PRICING_CONDTIONS_MAP, sapPricingCondtionsMap);
		itemInfoMap.put(STOCK_AVAILABILITY_MAP, stockAvailabilityMap);
		final String salesOrg = salesOrderSimulationData.getResult().getSalesOrganization();
		final String distrChannel = salesOrderSimulationData.getResult().getDistributionChannel();
		final String division = salesOrderSimulationData.getResult().getDivision();
		final String plant = getSAPS4OMPlant(itemModel);

		try {
			double deliveryCost = 0;
			double paymentCost = 0;
			List<SAPS4OMItemData> items = null;
			SAPS4OMItemsData itemsData = salesOrderSimulationData.getResult().getItems();
			if (itemsData != null) {
				items = itemsData.getSalesOrderItems();
				for (int i = 0; i < items.size(); i++) {
					SAPS4OMItemData itemData = items.get(i);
					setUnitForProduct(itemModel, itemData);
					setItemDetails(itemInfoMap, itemData, salesOrg, distrChannel, division, itemModel, stockAvailabilityMap, plant);
					deliveryCost += Double.parseDouble(getDeliverySubtotal(itemData, itemModel));
					paymentCost += Double.parseDouble(getPaymentCost(itemData, itemModel));
				}
			}
			if (itemModel instanceof AbstractOrderModel) {
				setOrderModel(itemModel, itemInfoMap, deliveryCost, paymentCost);
				setEarliestRequestedDateInSession(itemModel, salesOrderSimulationData);
			}
			callModifierHookForOrderSimulation(salesOrderSimulationData, itemModel, itemInfoMap);
		} catch (RestClientException e) {
			LOG.error("Unable to get the Stock and price details inside response processor...");
			LOG.error("Unable to get the Stock and price details...");
			return itemInfoMap;
		}
		return itemInfoMap;

	}

	private void setEarliestRequestedDateInSession(ItemModel itemModel, SAPS4OMData salesOrderSimulationData) {
		String requestedDeliveryDate = salesOrderSimulationData.getResult().getRequestedDeliveryDate();
		if(!StringUtils.isEmpty(requestedDeliveryDate) && itemModel instanceof CartModel cart && sapS4OrderManagementConfigService.isRequestedRetrievalDateFeatureEnabled(cart.getStore())) {
				LocalDate earliestDeliveryDate = LocalDate.ofInstant(Instant.ofEpochMilli(Long.parseLong(Saps4omservicesConstants.DATE_REGEX_PATTERN.matcher(requestedDeliveryDate).replaceAll("$1"))), ZoneId.of("UTC"));
				final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Saps4omservicesConstants.DATE_FORMAT);
				final String earliestDate =formatter.format(earliestDeliveryDate);
				sessionService.getCurrentSession().setAttribute(Saps4omservicesConstants.SAPS4OMCARTEARLIESTRETRIEVALDATE_PREFIX + cart.getCode(), earliestDate);
				cart.setEarliestRetrievalDate(earliestDate);
				cart.setRequestedRetrievalDate(earliestDate);
		}
	}

	private void setUnitForProduct(ItemModel itemModel, SAPS4OMItemData item) {
		if (itemModel instanceof ProductModel) {
			((ProductModel) itemModel).setUnit(getSapUnitService().getUnitForSAPCode(item.getRequestedQuantitySapUnit()));
		}
	}

	private void setItemDetails(Map<String, Object> itemInfoMap, SAPS4OMItemData item, String salesOrg,
			String distrChannel, String division, ItemModel itemModel, Map<String, SapS4OMProductAvailability> stockAvalabiltyMap, String plant) {
		Map<String, List<PriceInformation>> productPriceMap = (Map<String, List<PriceInformation>>) itemInfoMap
				.get(PRICE_INFO_MAP);
		Map<String, List<StockLevelModel>> stockLevelMap = (Map<String, List<StockLevelModel>>) itemInfoMap
				.get(STOCK_INFO_MAP);
		Map<String, DiscountValue> discountMap = (Map<String, DiscountValue>) itemInfoMap.get(DISCOUNT_MAP);
		Map<String, TaxValue> taxMap = (Map<String, TaxValue>) itemInfoMap.get(TAX_MAP);
		Map<String, List<SAPPricingConditionModel>> sapPricingCondtionsMap = (Map<String, List<SAPPricingConditionModel>>) itemInfoMap
				.get(SAP_PRICING_CONDTIONS_MAP);
		Map<String, SapS4OMProductAvailability> stockAvalabiltyLevelMap = (Map<String, SapS4OMProductAvailability>) itemInfoMap.get(STOCK_AVAILABILITY_MAP);
        Map<String, Collection<String>> scheduleLineList = (Map<String, Collection<String>>) itemInfoMap.get(SCHEDULE_LINE_MAP);
		
 		
		List<PriceInformation> priceList = new ArrayList<>();
		String material = item.getMaterial();
		String salesOrderItemNumber = item.getSalesOrderItem();
		String currency = item.getTransactionCurrency();
		String requestedQuantity = item.getRequestedQuantity();
		Double quantity = (requestedQuantity != null && !requestedQuantity.isEmpty()) ? Double.valueOf(requestedQuantity):0;	
		String taxAmount = getTaxAmount(item, itemModel);
		
		if (BULK_QUANTITY.equals(requestedQuantity)) {
			setSchedLinesData(item, scheduleLineList, item.getScheduleLines());
			setStockDetails(stockLevelMap, item.getScheduleLines(), material, salesOrg, distrChannel, division, stockAvalabiltyLevelMap, plant);
		} else {
			setPriceDetails(productPriceMap, priceList, material, currency, quantity, item, itemModel);
			setDiscounts(discountMap, salesOrderItemNumber, currency, item, quantity, itemModel);
			setTaxValues(taxMap, salesOrderItemNumber, currency, quantity, taxAmount);		
		}
		
		if(itemModel instanceof AbstractOrderModel) {
			setSchedLinesData(item, scheduleLineList, item.getScheduleLines());
			setStockDetails(stockLevelMap, item.getScheduleLines(), material, salesOrg, distrChannel, division, stockAvalabiltyLevelMap, plant);
			setPricingConditions(sapPricingCondtionsMap, item.getPricingElements(), salesOrderItemNumber, itemModel);
		}
	}

	private void setSchedLinesData(SAPS4OMItemData item, Map<String, Collection<String>> scheduleLineMap,
			ScheduleLinesData shceduleLinesData) {

		if (shceduleLinesData.getSalesOrderScheduleLines() != null) {
			Collection<String> scheduleLineString = new ArrayList<>();
			for (ScheduleLineData scheduleLineData : shceduleLinesData.getSalesOrderScheduleLines()) {
				if (scheduleLineData.getConfirmedDeliveryDate() != null) {
					String confirmedQuantity = "confirmedQuantity";
					String dateString = "confirmedDate";
					Map<String, String> payload = new HashMap<>();
					String deliveryDateString = scheduleLineData.getConfirmedDeliveryDate();
					Date deliveryDate = new Date(Long.parseLong(deliveryDateString.replaceAll(".*?(\\d+).*", "$1")));
					DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
					
					payload.put(dateString, formatter.format(deliveryDate));
					payload.put(confirmedQuantity, scheduleLineData.getConfdOrderQtyByMatlAvailCheck());
					try {
						String json = new ObjectMapper().writeValueAsString(payload);
						scheduleLineString.add(json);
					} catch (IOException e) {
						LOG.error("Error while processing JSON {}", e.getMessage());
					}
				}
			}

			scheduleLineMap.put(item.getSalesOrderItem(), scheduleLineString);
		}
	}

	private void setTaxValues(Map<String, TaxValue> taxMap, String salesOrderItemNumber, String currency,
			Double quantity, String taxAmount) {
		Double tax = Double.parseDouble(taxAmount) / quantity;
		final TaxValue taxValue = new TaxValue(generateCode("DISC", salesOrderItemNumber, ""), tax, true, tax,
				currency);
		taxMap.put(salesOrderItemNumber, taxValue);
	}

	private void setPriceDetails(Map<String, List<PriceInformation>> productPriceMap, List<PriceInformation> priceList,
			String material, String currency, Double quantity, SAPS4OMItemData item, ItemModel itemModel) {
		String subTotal1 = getSubTotal(item, itemModel);
		priceList.add(new PriceInformation(new PriceValue(currency, Double.valueOf(subTotal1) / quantity, true)));
		productPriceMap.put(material, priceList);
	}

	private void setStockDetails(Map<String, List<StockLevelModel>> stockLevelMap, ScheduleLinesData shceduleLinesData,
			String material, String salesOrg, String distrChannel, String division, Map<String, SapS4OMProductAvailability> stockAvalabiltyMap, String plant ) {
		if (shceduleLinesData == null || shceduleLinesData.getSalesOrderScheduleLines() == null) {
			return;
		}
		List<StockLevelModel> stockLevelModels = new ArrayList<>();
		int totalStock = 0;
		List<FutureStockModel> futureStockAvailability = new ArrayList<>();
		totalStock = setFutureStockAvailability(shceduleLinesData, material, totalStock, futureStockAvailability);
		StockLevelModel stockModel = new StockLevelModel();
		stockModel.setProductCode(material);

		if ((getCurrentAvailableStock(stockLevelMap.get(material)) + totalStock) > 0) {
			stockModel.setInStockStatus(InStockStatus.NOTSPECIFIED);
		} else {
			stockModel.setInStockStatus(InStockStatus.FORCEOUTOFSTOCK);
		}
		stockModel.setAvailable(totalStock);
		stockModel.setWarehouse(getSapPlant(salesOrg, distrChannel, division));

		if (stockLevelMap.get(material) != null) {
			if (totalStock > 0) {
				stockLevelMap.get(material).add(stockModel);
			}
		} else {
			stockLevelModels.add(stockModel);
			stockLevelMap.put(material, stockLevelModels);
		}
		final SapS4OMProductAvailability sapProductAvaialbility = new SapS4OMProductAvailabilityImpl((long) totalStock,futureStockAvailability,stockModel);
		stockAvalabiltyMap.put(plant, sapProductAvaialbility);

	}

	private int setFutureStockAvailability(ScheduleLinesData shceduleLinesData, String material, int totalStock,
			List<FutureStockModel> futureStockAvailability) {
		for (ScheduleLineData scheduleLineData : shceduleLinesData.getSalesOrderScheduleLines()) {

			try {
				totalStock += (int) Math.round(Double.parseDouble(scheduleLineData.getConfdOrderQtyByMatlAvailCheck()));
			} catch (NumberFormatException e) {
				LOG.error("Error while processing schedule Line Data {}", e.getMessage());
			}
			FutureStockModel futureStockModel = new FutureStockModel();
			futureStockModel.setProductCode(material);
			String confirmedDeliveryDate = scheduleLineData.getConfirmedDeliveryDate();
			String requestedDeliveryDate = scheduleLineData.getRequestedDeliveryDate();
			String deliveryDateString = (confirmedDeliveryDate != null) ? confirmedDeliveryDate : requestedDeliveryDate;
			String availability = scheduleLineData.getConfdOrderQtyByMatlAvailCheck();
			Date deliveryDate = new Date(Long.parseLong(deliveryDateString.replaceAll(".*?(\\d+).*", "$1")));
			Date todaysDate = new Date();
			if (deliveryDate.after(todaysDate)) {
				futureStockModel.setDate(deliveryDate);
				futureStockModel.setQuantity(Integer.valueOf((int) Double.parseDouble(availability)));
				futureStockAvailability.add(futureStockModel);
				
			}

		}
		return totalStock;
	}

	private int getCurrentAvailableStock(List<StockLevelModel> stockList) {
		int availableStocks = 0;
		if (stockList != null) {
			for (StockLevelModel stock : stockList) {
				availableStocks += stock.getAvailable();
			}
		}
		return availableStocks;
	}

	private WarehouseModel getSapPlant(String salesOrg, String distrChannel, String division) {
		Set<SAPPlantLogSysOrgModel> sapPlantLogSysOrgs = getBaseStoreService().getCurrentBaseStore().getSAPConfiguration()
				.getSapPlantLogSysOrg();

		SAPPlantLogSysOrgModel sapPlant = sapPlantLogSysOrgs.stream()
				.filter(sapPlantDetail -> (salesOrg.equals(sapPlantDetail.getSalesOrg().getSalesOrganization())
						&& distrChannel.equals(sapPlantDetail.getSalesOrg().getDistributionChannel())
						&& division.equals(sapPlantDetail.getSalesOrg().getDivision())))
				.findAny().orElse(null);
		if (sapPlant != null) {
			return sapPlant.getPlant();
		}
		return null;
	}

	private void setDiscounts(Map<String, DiscountValue> discountMap, String salesOrderItemNumber, String currency,
			SAPS4OMItemData item, Double quantity, ItemModel itemModel) {
		Double totalDiscounts = getDiscounts(item, itemModel);
		Double discount = totalDiscounts / quantity;
		final DiscountValue discountValue = new DiscountValue(generateCode("DISC", salesOrderItemNumber, ""), discount,
				true, discount, currency);
		discountMap.put(salesOrderItemNumber, discountValue);
	}

	protected String generateCode(String prefix, String entryNumber, String code) {
		return prefix + entryNumber + code;

	}

	protected void setPricingConditions(Map<String, List<SAPPricingConditionModel>> sapPricingCondtionsMap,
			PricingElementsData pricingElementsData, String salesOrderItemNumber, ItemModel itemModel) {
		List<PricingElementData> pricingElements = pricingElementsData.getSalesOrderPricingElements();
		List<SAPPricingConditionModel> sapPricingConditions = new ArrayList<>();

		for (PricingElementData pricingElementData : pricingElements) {
			NumberFormat formatter = new DecimalFormat("#0.00");

			if (isPricingCoditionToBeConsidered(pricingElementData, itemModel)) {
				SAPPricingConditionModel sapPricingConditionModel = new SAPPricingConditionModel();
				sapPricingConditionModel.setStepNumber(pricingElementData.getPricingProcedureStep());
				sapPricingConditionModel.setConditionCounter(pricingElementData.getPricingProcedureCounter());
				sapPricingConditionModel.setConditionType(pricingElementData.getConditionType());
				sapPricingConditionModel.setCurrencyKey(pricingElementData.getTransactionCurrency());
				sapPricingConditionModel.setConditionPricingUnit(pricingElementData.getConditionQuantity());
				sapPricingConditionModel.setConditionUnit(pricingElementData.getConditionQuantityUnit());
				sapPricingConditionModel.setConditionRate(
						formatter.format(Double.parseDouble(pricingElementData.getConditionRateValue())));
				sapPricingConditionModel.setConditionValue(
						formatter.format(Double.parseDouble(pricingElementData.getConditionAmount())));
				sapPricingConditionModel.setConditionCalculationType(pricingElementData.getConditionCalculationType());
				sapPricingConditions.add(sapPricingConditionModel);

			}
		}

		sapPricingCondtionsMap.put(salesOrderItemNumber, sapPricingConditions);
	}

	protected boolean isPricingCoditionToBeConsidered(PricingElementData pricingElement, ItemModel itemModel) {
		String pricingConditionCode = ((AbstractOrderModel) itemModel).getStore().getSAPConfiguration().getSaps4itempriceconditiontype();
		String conditionType = pricingElement.getConditionType();
		double conditionAmount = Double.parseDouble(pricingElement.getConditionAmount());
		return StringUtils.isNotEmpty(conditionType)
				&& (pricingConditionCode.equals(conditionType) || conditionAmount < 0);
	}

	protected void setOrderModel(ItemModel itemModel, Map<String, Object> itemInfoMap, double deliveryCost,
			double paymentCost) {

		Map<String, List<PriceInformation>> productPriceMap = (Map<String, List<PriceInformation>>) itemInfoMap
				.get(PRICE_INFO_MAP);
		Map<String, DiscountValue> discountMap = (Map<String, DiscountValue>) itemInfoMap.get(DISCOUNT_MAP);
		Map<String, TaxValue> taxMap = (Map<String, TaxValue>) itemInfoMap.get(TAX_MAP);
		Map<String, List<SAPPricingConditionModel>> sapPricingCondtionsMap = (Map<String, List<SAPPricingConditionModel>>) itemInfoMap
				.get(SAP_PRICING_CONDTIONS_MAP);
		Map<String, List<StockLevelModel>> stockLevelMap = (Map<String, List<StockLevelModel>>) itemInfoMap
				.get(STOCK_INFO_MAP);
		Map<String,Collection<String>> scheduleMap = (Map<String, Collection<String>>) itemInfoMap.get(SCHEDULE_LINE_MAP);
		double tax = 0;
		double discount = 0;
		if (((AbstractOrderModel) itemModel).getDeliveryMode() != null) {
			((AbstractOrderModel) itemModel).setDeliveryCost(deliveryCost);
		}
		double totalOrderPrice = 0;
		for (AbstractOrderEntryModel orderEntry : ((AbstractOrderModel) itemModel).getEntries()) {
			final int orderEntryNumber = (orderEntry.getEntryNumber() + 1) * ORDER_ENTRY_MULTIPLIER;
			totalOrderPrice = setPriceInfoToOrderModel(productPriceMap, totalOrderPrice, orderEntry);
			tax = setTaxValueToOrderModel(taxMap, tax, orderEntry, orderEntryNumber);

			discount = setDiscountValueToOrderModel(discountMap, discount, orderEntry, orderEntryNumber);

			setPricingConditionToOrderModel(itemModel, sapPricingCondtionsMap, orderEntry, orderEntryNumber);

			if (stockLevelMap.get(orderEntry.getProduct().getCode()) != null) {
				orderEntry.getProduct().setStockLevels(
						new HashSet<StockLevelModel>(stockLevelMap.get(orderEntry.getProduct().getCode())));
			}
			Collection<String> model;
			model = scheduleMap.get(String.valueOf(orderEntryNumber));
			orderEntry.setDeliveryScheduleLines(model);

		}

		((AbstractOrderModel) itemModel).setDeliveryCost(deliveryCost);
		((AbstractOrderModel) itemModel).setPaymentCost(paymentCost);
		((AbstractOrderModel) itemModel).setTotalDiscounts(discount);
		((AbstractOrderModel) itemModel).setTotalTax(tax);
		((AbstractOrderModel) itemModel).setTotalPrice(totalOrderPrice + deliveryCost - discount);
		((AbstractOrderModel) itemModel).setSubtotal(totalOrderPrice);		
	}

	private double setPriceInfoToOrderModel(Map<String, List<PriceInformation>> productPriceMap, double totalOrderPrice,
			AbstractOrderEntryModel orderEntry) {
		List<PriceInformation> priceInfoList = productPriceMap.get(orderEntry.getProduct().getCode());
		if (priceInfoList != null && !priceInfoList.isEmpty()) {
			PriceInformation priceInformation = priceInfoList.get(0);
			double price = priceInformation.getValue().getValue();
			orderEntry.setBasePrice(price);
			orderEntry.setTotalPrice(price * orderEntry.getQuantity());
			totalOrderPrice += price * orderEntry.getQuantity();
		}
		return totalOrderPrice;
	}

	private void setPricingConditionToOrderModel(ItemModel itemModel,
			Map<String, List<SAPPricingConditionModel>> sapPricingCondtionsMap, AbstractOrderEntryModel orderEntry,
			final int orderEntryNumber) {
		List<SAPPricingConditionModel> sapPricingConditions = sapPricingCondtionsMap.get(orderEntryNumber + "");
		if (itemModel instanceof OrderModel && orderEntry.getSapPricingConditions().isEmpty()
				&& (sapPricingConditions != null)) {
			for (SAPPricingConditionModel sapPricingCodition : sapPricingConditions) {
				sapPricingCodition.setOrderEntry(orderEntry);
				sapPricingCodition.setOrder(((AbstractOrderModel) itemModel).getCode());
				sapPricingCodition.setConditionUnit(orderEntry.getProduct().getUnit().getCode());
			}
			orderEntry.setSapPricingConditions(new HashSet<SAPPricingConditionModel>(sapPricingConditions));
		}
	}

	private double setTaxValueToOrderModel(Map<String, TaxValue> taxMap, double tax, AbstractOrderEntryModel orderEntry,
			final int orderEntryNumber) {
		TaxValue taxValue = taxMap.get(orderEntryNumber + "");
		if (taxValue != null) {
			tax += taxValue.getValue();
			orderEntry.setTaxValues(Arrays.asList(taxValue));
		}
		return tax;
	}

	private double setDiscountValueToOrderModel(Map<String, DiscountValue> discountMap, double discount,
			AbstractOrderEntryModel orderEntry, final int orderEntryNumber) {
		DiscountValue discountValue = discountMap.get(orderEntryNumber + "");
		if (discountValue != null) {
			discount += discountValue.getValue();
			orderEntry.setDiscountValues(Arrays.asList(discountValue));
		}
		return discount;
	}

	protected Double getDiscounts(SAPS4OMItemData item, ItemModel itemModel) {

		S4ProceduresSubtotal priceSubtotal = getBaseStore(itemModel).getSAPConfiguration().getSaps4om_pricesub();
		S4ProceduresSubtotal discountSubTotal = getBaseStore(itemModel).getSAPConfiguration().getSaps4om_discountsub();
		String subTotal1 = getAmount(item, priceSubtotal);
		String subTotal2 = getAmount(item, discountSubTotal);

		Double discount = 0.0;
		if (!subTotal1.isEmpty() && !subTotal2.isEmpty()) {
			discount = Double.parseDouble(subTotal1) - Double.parseDouble(subTotal2);
		}
		return discount;
	}

	

	private String getTaxAmount(SAPS4OMItemData item, ItemModel itemModel) {
		S4ProceduresSubtotal taxSubtotal = getBaseStore(itemModel).getSAPConfiguration().getSaps4om_taxessub();
		return getAmount(item, taxSubtotal);

	}

	private String getSubTotal(SAPS4OMItemData item, ItemModel itemModel) {
		S4ProceduresSubtotal priceSubtotal = getBaseStore(itemModel).getSAPConfiguration().getSaps4om_pricesub();
		return getAmount(item, priceSubtotal);
	}

	private String getPaymentCost(SAPS4OMItemData item, ItemModel itemModel) {
		S4ProceduresSubtotal paymentSubtotal = getBaseStore(itemModel).getSAPConfiguration().getSaps4om_paymentsub();
		return getAmount(item, paymentSubtotal);
	}

	private String getDeliverySubtotal(SAPS4OMItemData item, ItemModel itemModel) {
		S4ProceduresSubtotal deliverySubtotal = getBaseStore(itemModel).getSAPConfiguration().getSaps4om_deliverysub();
		return getAmount(item, deliverySubtotal);		
		
	}

	private String getAmount(SAPS4OMItemData item, S4ProceduresSubtotal subtotal) {
		if (subtotal == null) {
			return "0";
		}
		switch (subtotal.getCode()) {
		case NETAMOUNT:
			return getSubTotalAmount(item.getNetAmount());
		case TAXAMOUNT:
			return getSubTotalAmount(item.getTaxAmount());
		case COSTAMOUNT:
			return getSubTotalAmount(item.getCostAmount());
		case SUBTOTALAMOUNT1:
			return getSubTotalAmount(item.getSubtotal1Amount());
		case SUBTOTALAMOUNT2:
			return getSubTotalAmount(item.getSubtotal2Amount());
		case SUBTOTALAMOUNT3:
			return getSubTotalAmount(item.getSubtotal3Amount());
		case SUBTOTALAMOUNT4:
			return getSubTotalAmount(item.getSubtotal4Amount());
		case SUBTOTALAMOUNT5:
			return getSubTotalAmount(item.getSubtotal5Amount());
		case SUBTOTALAMOUNT6:
			return getSubTotalAmount(item.getSubtotal6Amount());
		default:
			return "";
		}
	}

	private String getSubTotalAmount(String item) {
		return item != null ? item : "0";
	}

		private String getSAPS4OMPlant(ItemModel itemModel) {
		String plant = null;
		if(itemModel instanceof ProductModel) {
			 plant = (((ProductModel) itemModel).getSapPlant()!=null)?((ProductModel) itemModel).getSapPlant().getCode():DEFAULT_PLANT;
		}
		return plant;
	}

	
	private BaseStoreModel baseStore(ItemModel itemModel) {
		BaseStoreModel baseStore = null;
		if (itemModel instanceof AbstractOrderModel) {
			baseStore = ((AbstractOrderModel) itemModel).getStore();
		} else {
			baseStore = getBaseStoreService().getCurrentBaseStore();
		}
		return baseStore;
	}
	
	protected void callModifierHookForOrder(SAPS4OMData responseData, OrderModel order) {
		for (ResponsePayloadModifierHook hook : Optional.ofNullable(getResponsePayloadModifierHooks()).orElse(Collections.emptyList())) {
			hook.modifyPayloadForOrder(order, responseData);
		}
	}

	protected void callModifierHookForOrderSimulation(SAPS4OMData salesOrderSimulationData, ItemModel itemModel, Map<String, Object> itemInfoMap) {
		for (ResponsePayloadModifierHook hook : Optional.ofNullable(getResponsePayloadModifierHooks()).orElse(Collections.emptyList())) {
			hook.modifyPayloadForOrderSimulation(itemModel, salesOrderSimulationData, itemInfoMap);
		}
	}

	private BaseStoreModel getBaseStore(ItemModel itemModel) {
		return baseStore(itemModel);
	}

	protected SAPDefaultUnitService getSapUnitService() {
		return sapUnitService;
	}

	public void setSapUnitService(SAPDefaultUnitService sapUnitService) {
		this.sapUnitService = sapUnitService;
	}
	
	public BaseStoreService getBaseStoreService() {
		return baseStoreService;
	}

	public void setBaseStoreService(BaseStoreService baseStoreService) {
		this.baseStoreService = baseStoreService;
	}

	public List<ResponsePayloadModifierHook> getResponsePayloadModifierHooks() {
		return responsePayloadModifierHooks;
	}

	public void setResponsePayloadModifierHooks(List<ResponsePayloadModifierHook> responsePayloadModifierHooks) {
		this.responsePayloadModifierHooks = responsePayloadModifierHooks;
	}

	public SessionService getSessionService() {
		return sessionService;
	}

	public void setSessionService(SessionService sessionService) {
		this.sessionService = sessionService;
	}

	public SapS4OrderManagementConfigService getSapS4OrderManagementConfigService() {
		return sapS4OrderManagementConfigService;
	}

	public void setSapS4OrderManagementConfigService(SapS4OrderManagementConfigService sapS4OrderManagementConfigService) {
		this.sapS4OrderManagementConfigService = sapS4OrderManagementConfigService;
	}


}
