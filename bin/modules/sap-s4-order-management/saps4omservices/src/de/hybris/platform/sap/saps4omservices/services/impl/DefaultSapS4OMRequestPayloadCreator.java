/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omservices.services.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commerceservices.delivery.DeliveryService;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.sapmodel.enums.SAPProductType;
import de.hybris.platform.sap.sapmodel.model.SAPDeliveryModeModel;
import de.hybris.platform.sap.saps4omservices.constants.Saps4omservicesConstants;
import de.hybris.platform.sap.saps4omservices.services.RequestPayloadModifierHook;
import de.hybris.platform.sap.saps4omservices.services.SapS4OMRequestPayloadCreator;
import de.hybris.platform.sap.saps4omservices.services.SapS4OrderManagementConfigService;
import de.hybris.platform.sap.saps4omservices.utils.SapS4OrderUtil;
import de.hybris.platform.saps4omservices.dto.CreditData;
import de.hybris.platform.saps4omservices.dto.PartnerData;
import de.hybris.platform.saps4omservices.dto.PricingData;
import de.hybris.platform.saps4omservices.dto.PricingElementData;
import de.hybris.platform.saps4omservices.dto.SAPS4OMItemRequestData;
import de.hybris.platform.saps4omservices.dto.SAPS4OMRequestData;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.util.PriceValue;

public class DefaultSapS4OMRequestPayloadCreator implements SapS4OMRequestPayloadCreator {

	private static final String EMPTY_STRING = "";
	private static final int ORDER_ENTRY_MULTIPLIER = 10;
	private static final String ORDER = "OR";
	private static final String QUANTITY = "1";
	private static final String BULK_QUANTITY = "9999999";
	private static final String DEFUALT_CURRENCY = "USD";
	private static final Integer BULK_QUANTITY_ITEM_NUMBER = 100;
	private SapS4OrderManagementConfigService sapS4OrderManagementConfigService;
	private SapS4OrderUtil sapS4OrderUtil;
	private UserService userService;
	private BaseStoreService baseStoreService;
	private CommonI18NService commonI18NService;
	private DeliveryService deliveryService;
	private List<RequestPayloadModifierHook> requestPayloadModifierHooks;

	private static final Logger LOG = LoggerFactory.getLogger(DefaultSapS4OMRequestPayloadCreator.class);

	@Override
	public SAPS4OMRequestData getPayloadForOrderCreation(OrderModel order) {

		SAPS4OMRequestData requestData = getRequestPayloadHeaderLevel(order);
		requestData.setItems(getRequestPayloadItemLevel(order));

		callModifierHookForOrder(order, requestData);
		return requestData;

	}

	@Override
	public SAPS4OMRequestData getPayloadForOrderSimulation(AbstractOrderModel cart) {

		SAPS4OMRequestData requestData = getRequestPayloadHeaderLevel(cart);
		List<SAPS4OMItemRequestData> requestPayloadItemLevel = getRequestPayloadItemLevel(cart);
		requestData.setItems(requestPayloadItemLevel);
		requestData.setPricing(new PricingData());

		callModifierHookForOrderSimulation(cart, requestData);
		return requestData;
	}

	@Override
	public SAPS4OMRequestData getPayloadForOrderSimulation(List<ProductModel> products, boolean checkAvailability) {

		SAPS4OMRequestData requestData = getRequestPayloadHeaderLevel(products.iterator().next());
		List<SAPS4OMItemRequestData> requestPayloadItemLevel = getRequestPayloadItemLevel(products, checkAvailability);
		requestData.setItems(requestPayloadItemLevel);
		requestData.setPricing(new PricingData());

		callModifierHookForOrderSimulation(products, requestData, checkAvailability);
		return requestData;

	}

	protected SAPS4OMRequestData getRequestPayloadHeaderLevel(ItemModel item) {

		SAPS4OMRequestData requestData = new SAPS4OMRequestData();
		final String soldToParty = getSapS4OrderUtil().getSoldToParty(getUserModel(item));

		requestData.setSoldToParty(soldToParty);

		SAPConfigurationModel sapConfiguration = getBaseStore(item).getSAPConfiguration();
		final String orderType = sapConfiguration.getSapcommon_transactionType();
		requestData.setSalesOrderType((orderType != null) ? orderType : ORDER);

		requestData.setShippingCondition(getShippingCondition(item));
		requestData.setSalesOrganization(sapConfiguration.getSapcommon_salesOrganization());
		requestData.setDistributionChannel(sapConfiguration.getSapcommon_distributionChannel());
		requestData.setDivision(sapConfiguration.getSapcommon_division());
		requestData.setPurchaseOrderByCustomer(getPOByCustomer(item));
		requestData.setCurrency(getCurrency(item));
		requestData.setPricingElements(getHeaderPricingElements(item));
		requestData.setPartner(getHeaderPartners(item));
		if (item instanceof CartModel cartModel) {
			requestData.setCreditDetails(new CreditData());
		}
		requestData.setRequestedDeliveryDate(getRequestedDeliveryDate(item));
		return requestData;

	}

	private String getRequestedDeliveryDate(ItemModel item) {
		
		if(item instanceof AbstractOrderModel abstractOrder && sapS4OrderManagementConfigService.isRequestedRetrievalDateFeatureEnabled(abstractOrder.getStore()) && abstractOrder.getRequestedRetrievalDate() != null) {
			
			final DateFormat formatter = new SimpleDateFormat(Saps4omservicesConstants.DATE_FORMAT);
			formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
			Date date;
			try {
				date = formatter.parse(abstractOrder.getRequestedRetrievalDate());
				return "/Date(" +date.getTime()+ ")/";
			} catch (ParseException e) {
				LOG.debug("Date is not in expected format {} : {} ", Saps4omservicesConstants.DATE_FORMAT, abstractOrder.getRequestedRetrievalDate());
			}
		}
		return null;
	}

	private String getShippingCondition(ItemModel item) {

		if (item instanceof OrderModel orderModel && orderModel.getDeliveryMode() != null && orderModel.getStore().getSAPConfiguration().getSapDeliveryModes() != null) {
			DeliveryModeModel deliveryMode = orderModel.getDeliveryMode();
			Optional<SAPDeliveryModeModel> sapDeliveryModeModel = orderModel
																.getStore()
																.getSAPConfiguration()
																.getSapDeliveryModes()
																.stream()
																.filter(sapDeliveryMode -> sapDeliveryMode.getDeliveryMode().getCode().equals(deliveryMode.getCode()))
																.findFirst();
			return sapDeliveryModeModel.isPresent() ? sapDeliveryModeModel.get().getDeliveryValue() : EMPTY_STRING;
		}
		return EMPTY_STRING;
	}

	protected List<SAPS4OMItemRequestData> getRequestPayloadItemLevel(List<ProductModel> products,
			boolean checkAvailability) {
		List<SAPS4OMItemRequestData> items = new ArrayList<>();
		int number = 1;
		for (ProductModel product : products) {
			setItemLevelData(items, number, product, QUANTITY, false);
			setItemLevelData(items, number+BULK_QUANTITY_ITEM_NUMBER, product, BULK_QUANTITY, false);
			number++;
		}
		return items;
	}

	protected List<SAPS4OMItemRequestData> getRequestPayloadItemLevel(AbstractOrderModel abstractOrderModel) {
		List<SAPS4OMItemRequestData> items = new ArrayList<>();
		boolean isProductionPlant = getSapS4OrderManagementConfigService().isS4SynchronousOrderEnabled()
				&& abstractOrderModel instanceof OrderModel;
		for (AbstractOrderEntryModel abstractOderEntryModel : abstractOrderModel.getEntries()) {
			if (isAllowedProduct(abstractOderEntryModel)) {
				setItemLevelData(items, (abstractOderEntryModel.getEntryNumber() + 1),
						abstractOderEntryModel.getProduct(), abstractOderEntryModel.getQuantity() + EMPTY_STRING,
						isProductionPlant);
			}
		}
		return items;
	}

	protected List<PartnerData> getHeaderPartners(ItemModel item) {
		List<PartnerData> partners = new ArrayList<>();
		SAPConfigurationModel sapConfig = getBaseStore(item).getSAPConfiguration();
		Locale locale = getCommonI18NService()
				.getLocaleForIsoCode(getCommonI18NService().getCurrentLanguage().getIsocode());
		UserModel user = getUserModel(item);
		PartnerData contact = new PartnerData();
		if (user instanceof B2BCustomerModel userModel) {
			LOG.debug("Contact Id of the b2b customer: {}", userModel.getCustomerID());
			contact.setContactPerson(userModel.getCustomerID());
		}
		contact.setPartnerFunction(sapConfig.getContactPersonPartnerFunctionCode(locale));

		partners.add(contact);

		String soldToCustomer = getSapS4OrderUtil().getSoldToParty(user);

		if (item instanceof AbstractOrderModel abstractOrder) {

			String shipToCustomer = abstractOrder.getDeliveryAddress() != null
					&& !StringUtils.isEmpty(abstractOrder.getDeliveryAddress().getSapCustomerID())
							? abstractOrder.getDeliveryAddress().getSapCustomerID()
							: soldToCustomer;
			PartnerData shipTo = new PartnerData();
			shipTo.setCustomer(shipToCustomer);
			shipTo.setPartnerFunction(sapConfig.getShipToPartnerFunctionCode(locale));
			String billToCustomer = abstractOrder.getPaymentAddress() != null
					&& !StringUtils.isEmpty(abstractOrder.getPaymentAddress().getSapCustomerID())
							? abstractOrder.getPaymentAddress().getSapCustomerID()
							: soldToCustomer;
			PartnerData billTo = new PartnerData();
			billTo.setCustomer(billToCustomer);
			billTo.setPartnerFunction(sapConfig.getBillToPartnerFunctionCode(locale));
			partners.add(shipTo);
			partners.add(billTo);
		} else {
			PartnerData shipTo = new PartnerData();
			shipTo.setCustomer(soldToCustomer);
			shipTo.setPartnerFunction(sapConfig.getShipToPartnerFunctionCode(locale));
			PartnerData billTo = new PartnerData();
			billTo.setCustomer(soldToCustomer);
			billTo.setPartnerFunction(sapConfig.getBillToPartnerFunctionCode(locale));
			partners.add(shipTo);
			partners.add(billTo);
		}
		return partners;
	}

	protected List<PricingElementData> getHeaderPricingElements(ItemModel itemModel) {
		List<PricingElementData> pricingElements = null;
		if (itemModel instanceof AbstractOrderModel abstractOrderModel
				&& abstractOrderModel.getDeliveryMode() != null) {
			pricingElements = new ArrayList<>();
			PricingElementData pricingElementData = deliveryPricingCondition(abstractOrderModel);
			if (pricingElementData != null) {
				pricingElements.add(pricingElementData);
			}

		}
		return pricingElements;
	}

	protected PricingElementData deliveryPricingCondition(AbstractOrderModel abstractOrderModel) {

		String shippingConditionCode = abstractOrderModel.getStore().getSAPConfiguration()
				.getSaps4deliverycostconditiontype();
		if (StringUtils.isNotEmpty(shippingConditionCode)) {
			PricingElementData pricingElementData = new PricingElementData();
			DeliveryModeModel deliveryModel = abstractOrderModel.getDeliveryMode();
			Double deliveryCost = null;
			if (abstractOrderModel instanceof CartModel cartModel) {
				PriceValue deliveryCostCart = getDeliveryService()
						.getDeliveryCostForDeliveryModeAndAbstractOrder(deliveryModel, abstractOrderModel);
				deliveryCost = deliveryCostCart != null ? deliveryCostCart.getValue() : null;
				LOG.debug("Delivery cost from CartModel : {}", deliveryCost);
			} else if (abstractOrderModel instanceof OrderModel orderModel) {
				deliveryCost = orderModel.getDeliveryCost();
				LOG.debug("Delivery cost from OrderModel : {}", deliveryCost);
			}

			if (deliveryCost != null) {
				pricingElementData.setConditionType(shippingConditionCode);
				pricingElementData.setConditionRateValue(deliveryCost + EMPTY_STRING);
			}
			return pricingElementData;
		}
		return new PricingElementData();

	}

	private String getPlant(ProductModel product) {
		return (product != null && product.getSapPlant() != null) ? product.getSapPlant().getCode() : EMPTY_STRING;
	}

	protected boolean isAllowedProduct(ItemModel item) {

		ProductModel product;
		if (item instanceof AbstractOrderEntryModel abstractOrderEntry) {
			product = abstractOrderEntry.getProduct();
		} else {
			product = (ProductModel) item;
		}

		return product != null && product.getSapProductTypes() != null
				&& product.getSapProductTypes().contains(SAPProductType.PHYSICAL);
	}

	private String getPOByCustomer(ItemModel item) {

		if (item instanceof AbstractOrderModel abstractOrder) {
			return abstractOrder.getProperty("purchaseOrderNumber") != null
					? abstractOrder.getProperty("purchaseOrderNumber")
					: EMPTY_STRING;
		} else {
			return EMPTY_STRING;
		}
	}

	private UserModel getUserModel(ItemModel itemModel) {

		if (itemModel instanceof AbstractOrderModel abstractOrder) {
			return abstractOrder.getUser();

		} else {
			return getUserService().getCurrentUser();
		}

	}

	private BaseStoreModel getBaseStore(ItemModel itemModel) {
		BaseStoreModel baseStore = null;
		if (itemModel instanceof AbstractOrderModel abstractOrder) {
			baseStore = abstractOrder.getStore();
		} else {
			baseStore = getBaseStoreService().getCurrentBaseStore();
		}
		return baseStore;
	}

	private String getCurrency(ItemModel itemModel) {
		String currency = null;
		if (itemModel instanceof AbstractOrderModel abstractOrder) {
			currency = abstractOrder.getCurrency().getSapCode();
		} else {
			currency = getCommonI18NService().getCurrentCurrency().getSapCode();
		}
		currency = (currency != null) ? currency : DEFUALT_CURRENCY;
		return currency;
	}

	private void setItemLevelData(List<SAPS4OMItemRequestData> items, int number, ProductModel product, String quantity,
			boolean isProductionPlant) {

		SAPS4OMItemRequestData itemRequestData = new SAPS4OMItemRequestData();
		itemRequestData.setSalesOrder(EMPTY_STRING);
		itemRequestData.setSalesOrderItem((number) * ORDER_ENTRY_MULTIPLIER + EMPTY_STRING);
		itemRequestData.setMaterial(product.getCode());
		itemRequestData.setRequestedQuantity(quantity + EMPTY_STRING);
		if (isProductionPlant) {
			LOG.debug("Set production plant");
			itemRequestData.setProductionPlant(getPlant(product));
		} else {
			LOG.debug("Set plant");
			itemRequestData.setPlant(getPlant(product));
		}
		itemRequestData.setScheduleLines(new ArrayList<>());
		itemRequestData.setPricingElements(new ArrayList<>());
		items.add(itemRequestData);
	}

	protected void callModifierHookForOrder(OrderModel order, SAPS4OMRequestData requestData) {
		for (RequestPayloadModifierHook hook : Optional.ofNullable(getRequestPayloadModifierHooks())
				.orElse(Collections.emptyList())) {
			hook.modifyPayloadForOrder(order, requestData);
		}
	}

	protected void callModifierHookForOrderSimulation(AbstractOrderModel order, SAPS4OMRequestData requestData) {
		for (RequestPayloadModifierHook hook : Optional.ofNullable(getRequestPayloadModifierHooks())
				.orElse(Collections.emptyList())) {
			hook.modifyPayloadForOrderSimulation(order, requestData);
		}
	}

	protected void callModifierHookForOrderSimulation(List<ProductModel> products, SAPS4OMRequestData requestData,
			boolean checkAvailability) {
		for (RequestPayloadModifierHook hook : Optional.ofNullable(getRequestPayloadModifierHooks())
				.orElse(Collections.emptyList())) {
			hook.modifyPayloadForOrderSimulation(products, requestData);
		}
	}

	public SapS4OrderUtil getSapS4OrderUtil() {
		return sapS4OrderUtil;
	}

	public void setSapS4OrderUtil(SapS4OrderUtil sapS4OrderUtil) {
		this.sapS4OrderUtil = sapS4OrderUtil;
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

	public CommonI18NService getCommonI18NService() {
		return commonI18NService;
	}

	public void setCommonI18NService(CommonI18NService commonI18NService) {
		this.commonI18NService = commonI18NService;
	}

	public DeliveryService getDeliveryService() {
		return deliveryService;
	}

	public void setDeliveryService(DeliveryService deliveryService) {
		this.deliveryService = deliveryService;
	}

	public List<RequestPayloadModifierHook> getRequestPayloadModifierHooks() {
		return requestPayloadModifierHooks;
	}

	public void setRequestPayloadModifierHooks(List<RequestPayloadModifierHook> requestPayloadModifierHooks) {
		this.requestPayloadModifierHooks = requestPayloadModifierHooks;
	}

	public SapS4OrderManagementConfigService getSapS4OrderManagementConfigService() {
		return sapS4OrderManagementConfigService;
	}

	public void setSapS4OrderManagementConfigService(
			SapS4OrderManagementConfigService sapS4OrderManagementConfigService) {
		this.sapS4OrderManagementConfigService = sapS4OrderManagementConfigService;
	}
}
