/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omservices.order.populator;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.sqlserver.jdbc.StringUtils;

import de.hybris.platform.b2b.enums.CheckoutPaymentType;
import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BCostCenterService;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.customer.CustomerService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.enums.DeliveryStatus;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.sap.sapmodel.enums.ConsignmentEntryStatus;
import de.hybris.platform.sap.sapmodel.enums.SAPOrderType;
import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;
import de.hybris.platform.sap.sapmodel.services.impl.SAPDefaultUnitService;
import de.hybris.platform.sap.saps4omservices.constants.Saps4omservicesConstants;
import de.hybris.platform.sap.saps4omservices.services.SapS4OrderManagementConfigService;
import de.hybris.platform.saps4omservices.dto.PartnerData;
import de.hybris.platform.saps4omservices.dto.PricingElementData;
import de.hybris.platform.saps4omservices.dto.SAPS4OMItemData;
import de.hybris.platform.saps4omservices.dto.SAPS4OMResponseData;
import de.hybris.platform.saps4omservices.dto.ScheduleLineData;
import de.hybris.platform.saps4omservices.dto.ScheduleLinesData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

public class DefaultSapS4OMOrderPopulator implements Populator<SAPS4OMResponseData, OrderModel> {

	private static final Logger LOG = LoggerFactory.getLogger(DefaultSapS4OMOrderPopulator.class);

	private static final String PARTNER_FUNCTION_CONTACT_PERSON = "CP";
	private static final String OVERALL_TOTAL_DELIVERY_STATUS_B = "B";
	private static final String OVERALL_TOTAL_DELIVERY_STATUS_C = "C";
	private static final String DATE_FORMAT = "E MMM dd HH:mm:ss Z yyyy";
	private static final String CONFIRMED_DATE = "confirmedDate";
	private static final String CONFIRMED_QUANTITY = "confirmedQuantity";
    private static final String PAYMENTTYPE = "paymentType";

	private UserService userService;
	private CustomerService customerService;
	private ProductService productService;
	private BaseStoreService baseStoreService;
	private CommonI18NService commonI18NService;
	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;
	private B2BCostCenterService<B2BCostCenterModel, B2BCustomerModel> b2bCostCenterService;
	private SAPDefaultUnitService sapUnitService;
	private BaseSiteService baseSiteService;
	private KeyGenerator guidKeyGenerator;
	private ModelService modelService;
	private Map<String, OrderStatus> sapS4OMOrderStatusMap;
	private Map<String, ConsignmentEntryStatus> sapS4OMConsignmentEntryStatusMap;
	private SapS4OrderManagementConfigService sapS4OrderManagementConfigService;

	@Override
	public void populate(SAPS4OMResponseData source, OrderModel target) throws ConversionException {

		populateCommon(source, target);
		populateOrderStatus(source, target);
		populateDeliveryStatus(source, target);
		populateDeliveryAddress(source, target);
		populateDeliveryMode(source, target);
		populateCurrency(source, target);
		populateOrderEntry(source, target);
		populatePrice(source, target);
		populateSAPOrder(target);

		populateConsignments(source, target);

	}

	protected void populateConsignments(SAPS4OMResponseData source, OrderModel target) {

		ConsignmentModel consignment = getModelService().create(ConsignmentModel.class);
		consignment.setDeliveryMode(target.getDeliveryMode());
		consignment.setCode(target.getCode() + "_1");
		consignment.setOrder(target);
		Set<ConsignmentEntryModel> consignmentEntries = new HashSet<>();
		boolean allShipped = target.getEntries().stream().allMatch(entry -> entry.getConsignmentEntries().stream()
				.allMatch(consignmentEntry -> ConsignmentEntryStatus.SHIPPED.equals(consignmentEntry.getStatus())));

		if(allShipped) {
			consignment.setStatus(ConsignmentStatus.SHIPPED);
		} else {
			consignment.setStatus(ConsignmentStatus.IN_PROCESS);
		}
		target.getEntries().stream().forEach(entry -> consignmentEntries.addAll(entry.getConsignmentEntries()));
		consignment.setConsignmentEntries(consignmentEntries);
		target.setConsignments(Collections.singleton(consignment));
		target.getSapOrders().forEach(sapOrder -> {
			sapOrder.setConsignments(target.getConsignments());
			consignment.setSapOrder(sapOrder);
		});

	}

	protected void populateConsignmentEntry(OrderEntryModel orderEntry, SAPS4OMItemData item) {

		ConsignmentEntryModel consignmentEntry = new ConsignmentEntryModel();
		consignmentEntry.setOrderEntry(orderEntry);
		consignmentEntry.setQuantity(orderEntry.getQuantity());
		consignmentEntry.setStatus(getConsignmentEntryStatus(item.getSdProcessStatus()));
		orderEntry.setConsignmentEntries(Collections.singleton(consignmentEntry));
	}

	protected ConsignmentEntryStatus getConsignmentEntryStatus(String sdProcessStatus) {

		ConsignmentEntryStatus status = null;
		if (sdProcessStatus != null && !sdProcessStatus.isEmpty()) {
			status = getSapS4OMConsignmentEntryStatusMap().get(sdProcessStatus);
		}

		return status;
	}

	protected void populateSAPOrder(OrderModel order) {
		SAPOrderModel sapOrder = new SAPOrderModel();
		sapOrder.setSapOrderType(SAPOrderType.SALES_SYNCHRONOUS);
		sapOrder.setCode(order.getCode());
		sapOrder.setOrder(order);
		sapOrder.setCreationtime(order.getCreationtime());
		order.setSapOrders(Collections.singleton(sapOrder));

	}

	protected void populateCommon(SAPS4OMResponseData source, OrderModel target) {
		target.setCode(source.getSalesOrder());
                target.setProperty(PAYMENTTYPE, CheckoutPaymentType.ACCOUNT);
		Locale locale = getCommonI18NService()
				.getLocaleForIsoCode(getCommonI18NService().getCurrentLanguage().getIsocode());
		PartnerData partnerData = null;
		if (source.getPartner() != null && source.getPartner().getSalesOrderPartners() != null) {
			LOG.debug("Populate user info for order with locale {}", locale);
			BaseStoreModel baseStore = getBaseStoreService().getCurrentBaseStore();
			String contactPersonPartnerFunction = baseStore.getSAPConfiguration()
					.getContactPersonPartnerFunctionCode(locale);
			target.setStore(baseStore);
			BaseSiteModel baseSiteModel = getBaseSiteService().getCurrentBaseSite();
			target.setSite(baseSiteModel);
			target.setGuid(getGuidKeyGenerator().generate().toString());
			partnerData = source.getPartner().getSalesOrderPartners().stream()
					.filter(partner -> Objects.equals(
							Optional.ofNullable(contactPersonPartnerFunction).orElse(PARTNER_FUNCTION_CONTACT_PERSON),
							partner.getPartnerFunction()))
					.findAny().orElse(null);
		}
		if (partnerData != null) {
			LOG.debug("Partner data found for partner function {}", partnerData.getPartnerFunction());
			target.setUser(getCustomerService().getCustomerByCustomerId(partnerData.getContactPerson()));
		} else {
			LOG.debug("Partner data not found. Falling back to current user");
			target.setUser(getUserService().getCurrentUser());
		}
		if (!StringUtils.isEmpty(source.getPurchaseOrderByCustomer())) {
			target.setProperty("purchaseOrderNumber", source.getPurchaseOrderByCustomer());
		}
		if (!StringUtils.isEmpty(source.getSalesOrderDate())) {
			LOG.debug("Populate date for order");
			Date salesOrderDate = new Date(Long.parseLong(source.getSalesOrderDate().replaceAll(".*?(\\d+).*", "$1")));
			target.setDate(salesOrderDate);
		}
		if (!StringUtils.isEmpty(source.getRequestedDeliveryDate()) && sapS4OrderManagementConfigService.isRequestedRetrievalDateFeatureEnabled(target.getStore())) {
			LOG.debug("Populate date for order requested delivery date");
			LocalDate requestedDeliveryDate = LocalDate.ofInstant(Instant.ofEpochMilli(Long.parseLong(Saps4omservicesConstants.DATE_REGEX_PATTERN.matcher(source.getRequestedDeliveryDate()).replaceAll("$1"))), ZoneId.of("UTC"));
			final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Saps4omservicesConstants.DATE_FORMAT);
			target.setRequestedRetrievalDate(formatter.format(requestedDeliveryDate));
		}
	}

	protected void populateOrderEntry(SAPS4OMResponseData source, OrderModel target) {
		List<AbstractOrderEntryModel> orderEntryList = new ArrayList<>();
		Double itemsPriceTotal = 0.0;
		Double taxAmount = 0.0;
		Double shippingAmount = 0.0;
		PricingElementData pricingElement = null;
		PricingElementData pricingElementShippingPrice = null;
		List<SAPS4OMItemData> salesOrderItems = source.getItems() != null
				&& source.getItems().getSalesOrderItems() != null ? source.getItems().getSalesOrderItems()
						: Collections.emptyList();
		B2BCostCenterModel costCenter = fetchCostCenter(target);
		LOG.debug("Cost center code {}", costCenter.getCode());
		for (SAPS4OMItemData item : salesOrderItems) {
			OrderEntryModel orderEntry = new OrderEntryModel();
			orderEntry.setProduct(getProductService().getProductForCode(item.getMaterial()));

			if (!StringUtils.isEmpty(item.getRequestedQuantity())) {
				LOG.debug("Populate quantity {} for order entry with product {}", item.getRequestedQuantity(),
						orderEntry.getProduct().getCode());
				orderEntry.setQuantity(Long.valueOf(item.getRequestedQuantity()));
				orderEntry.setUnit(getSapUnitService().getUnitForSAPCode(item.getRequestedQuantitySapUnit()));
			}
			pricingElementShippingPrice = fetchShippingPricingElement(item);
			pricingElement = fetchItemPricingElement(item);
			taxAmount = taxAmount + fetchItemTaxAmount(item); 
			shippingAmount = fetchConditionAmount(pricingElementShippingPrice);
			orderEntry.setBasePrice(fetchConditionRateValue(pricingElement));
			orderEntry.setTotalPrice((item.getNetAmount() != null ? Double.parseDouble(item.getNetAmount()) : 0.0 ) - shippingAmount);
			orderEntry.setCostCenter(costCenter);
			itemsPriceTotal = itemsPriceTotal + orderEntry.getTotalPrice();

			if (!StringUtils.isEmpty(item.getSalesOrderItem())) {
				LOG.debug("Populate entry number {}", item.getSalesOrderItem());
				orderEntry.setEntryNumber(Integer.valueOf(item.getSalesOrderItem()));
			}
			populateOrderEntryScheduleData(orderEntry, item.getScheduleLines());
			populateConsignmentEntry(orderEntry, item);
			orderEntry.setOrder(target);
			orderEntryList.add(orderEntry);
		}
		
		target.setEntries(orderEntryList);
		target.setTotalTax(taxAmount);
		target.setSubtotal(itemsPriceTotal);
	}

	protected void populateOrderEntryScheduleData(final OrderEntryModel orderEntry,
			final ScheduleLinesData scheduleLines) {

		if (scheduleLines.getSalesOrderScheduleLines() != null) {
			Collection<String> scheduleLineString = new ArrayList<>();
			for (ScheduleLineData scheduleLineData : scheduleLines.getSalesOrderScheduleLines()) {
				if (scheduleLineData.getConfirmedDeliveryDate() != null) {
					Map<String, String> payload = new HashMap<>();
					String deliveryDateString = scheduleLineData.getConfirmedDeliveryDate();
					Date deliveryDate = new Date(Long.parseLong(deliveryDateString.replaceAll(".*?(\\d+).*", "$1")));
					DateFormat formatter = new SimpleDateFormat(DATE_FORMAT);

					payload.put(CONFIRMED_DATE, formatter.format(deliveryDate));
					payload.put(CONFIRMED_QUANTITY, scheduleLineData.getConfdOrderQtyByMatlAvailCheck());
					try {
						String json = new ObjectMapper().writeValueAsString(payload);
						scheduleLineString.add(json);
					} catch (IOException e) {
						LOG.error("Error while processing JSON {}", e.getMessage());
					}
				}
			}
			orderEntry.setDeliveryScheduleLines(scheduleLineString);
		}
	}

	protected void populateOrderStatus(final SAPS4OMResponseData source, final OrderModel target) {
		final String overallStatus = source.getOverallSDProcessStatus();
		LOG.debug("Overall status for order is {}", overallStatus);
		if (overallStatus != null && getSapS4OMOrderStatusMap().containsKey(overallStatus)) {
			target.setStatus(getSapS4OMOrderStatusMap().get(overallStatus));
		} else {
			target.setStatus(OrderStatus.CREATED);
		}
		LOG.debug("Populate order status as {}", target.getStatus());
	}

	protected void populateDeliveryStatus(SAPS4OMResponseData source, OrderModel target) {
		LOG.debug("Populate delivery status when Order status is {}", target.getStatus());
		if (target.getStatus() == OrderStatus.CREATED || target.getStatus() == OrderStatus.OPEN || target.getStatus() == OrderStatus.IN_PROCESS) {
			final DeliveryStatus status = getShippingstatus(source);
			target.setDeliveryStatus(status);
		} else if (target.getStatus() == OrderStatus.COMPLETED) {
			target.setDeliveryStatus(DeliveryStatus.SHIPPED);
		}
		LOG.debug("Delivery status is {}", target.getDeliveryStatus());
	}

	protected DeliveryStatus getShippingstatus(final SAPS4OMResponseData source) {
		final String shippingStatus = source.getOverallTotalDeliveryStatus();
		if (shippingStatus == null) {
			return DeliveryStatus.NOTSHIPPED;
		}
		switch (shippingStatus) {
		case OVERALL_TOTAL_DELIVERY_STATUS_C:
			return DeliveryStatus.SHIPPED;
		case OVERALL_TOTAL_DELIVERY_STATUS_B:
			return DeliveryStatus.PARTSHIPPED;
		default:
			return DeliveryStatus.NOTSHIPPED;
		}
	}

	private void populateDeliveryAddress(SAPS4OMResponseData source, OrderModel target) {
		String soldToParty = source.getSoldToParty();
		B2BUnitModel parentB2BUnit = getB2bUnitService().getUnitForUid(soldToParty);
		LOG.debug("Fetch shipping address from parent unit with uid {}", parentB2BUnit.getUid());
		target.setUnit(parentB2BUnit);
		Set<B2BUnitModel> subB2BUnits = getB2bUnitService().getBranch(parentB2BUnit);
		for (final B2BUnitModel unit : subB2BUnits) {
			unit.getAddresses().stream().filter(AddressModel::getShippingAddress).findFirst()
					.ifPresent(target::setDeliveryAddress);

		}
	}

	protected void populateDeliveryMode(SAPS4OMResponseData source, OrderModel target) {
		LOG.debug("Populate delivery mode for order from base store with shipping condition {}",
				source.getShippingCondition());
		BaseStoreModel baseStore = getBaseStoreService().getCurrentBaseStore();
		if (!StringUtils.isEmpty(source.getShippingCondition()) && baseStore.getSAPConfiguration() != null) {
			baseStore.getSAPConfiguration().getSapDeliveryModes().stream()
					.filter(deliveryMode -> source.getShippingCondition().equals(deliveryMode.getDeliveryValue()))
					.findFirst().ifPresent(deliveryMode -> target.setDeliveryMode(deliveryMode.getDeliveryMode()));
		}
	}

	protected void populatePrice(SAPS4OMResponseData source, OrderModel target) {
		if (!StringUtils.isEmpty(source.getTotalNetAmount())) {
			LOG.debug("Populate total price for order with order net amount as {}", source.getTotalNetAmount());
			target.setTotalPrice(Double.valueOf(source.getTotalNetAmount())+ target.getTotalTax());
		}
		target.setDeliveryCost(fetchShippingPrice(source));
	}

	protected Double fetchShippingPrice(SAPS4OMResponseData source) {
		Double shippingPrice = 0.0;
		PricingElementData pricingElement = null;
		BaseStoreModel baseStore = getBaseStoreService().getCurrentBaseStore();
		if (source.getPricingElements() != null && source.getPricingElements().getSalesOrderPricingElements() != null
				&& baseStore.getSAPConfiguration() != null) {
			LOG.debug("Populate delivery cost for order with delivery condition type as {}",
					baseStore.getSAPConfiguration().getSaps4deliverycostconditiontype());
			pricingElement = source
					.getPricingElements().getSalesOrderPricingElements().stream().filter(data -> baseStore
							.getSAPConfiguration().getSaps4deliverycostconditiontype().equals(data.getConditionType()))
					.findAny().orElse(null);

			shippingPrice = shippingPrice + fetchConditionRateValue(pricingElement);
		}
		return shippingPrice;
	}

	protected PricingElementData fetchItemPricingElement(SAPS4OMItemData source) {
		PricingElementData pricingElement = null;
		BaseStoreModel baseStore = getBaseStoreService().getCurrentBaseStore();
		if (baseStore.getSAPConfiguration() != null
				&& baseStore.getSAPConfiguration().getSaps4itempriceconditiontype() != null) {
			LOG.debug("Populate item price for order entry with item price condition type as {}",
					baseStore.getSAPConfiguration().getSaps4itempriceconditiontype());
			pricingElement = source
					.getPricingElements().getSalesOrderPricingElements().stream().filter(data -> baseStore
							.getSAPConfiguration().getSaps4itempriceconditiontype().equals(data.getConditionType()))
					.findAny().orElse(null);
		}
		return pricingElement;
	}

	protected B2BCostCenterModel fetchCostCenter(OrderModel target) {
		List<B2BCostCenterModel> b2bCostCenters = new ArrayList<>();
		if (target.getUnit() != null && target.getCurrency() != null) {
			b2bCostCenters = getB2bCostCenterService().getCostCentersForUnitBranch(target.getUnit(),
					target.getCurrency());
		}
		return Optional.ofNullable(b2bCostCenters).orElse(Collections.emptyList()).stream().findFirst().orElse(null);
	}

	protected void populateCurrency(SAPS4OMResponseData source, OrderModel target) {
		if (source.getTotalNetAmount() != null && source.getTransactionCurrency() != null) {
			LOG.debug("Populate curreny info for order with transaction curreny as {}",
					source.getTransactionCurrency());
			final CurrencyModel currency = getCommonI18NService().getCurrency(source.getTransactionCurrency());
			if (currency == null) {
				throw new IllegalArgumentException("Order currency must not be null");
			}
			target.setCurrency(currency);
		}
	}

	protected double fetchConditionRateValue(PricingElementData pricingElement) {
		if (pricingElement != null && pricingElement.getConditionRateValue() != null) {
			return Double.valueOf(pricingElement.getConditionRateValue());
		}
		return 0.0;
	}

	protected double fetchConditionAmount(PricingElementData pricingElement) {
		if (pricingElement != null && pricingElement.getConditionAmount() != null) {
			return Double.valueOf(pricingElement.getConditionAmount());
		}
		return 0.0;
	}
	
	protected double fetchItemTaxAmount(SAPS4OMItemData item) {
		if(item != null && item.getTaxAmount() != null) {
			
			return Double.parseDouble(item.getTaxAmount());
		}
		return 0.0;
	}
	
	protected PricingElementData fetchShippingPricingElement(SAPS4OMItemData source) {
		PricingElementData pricingElement = null;
		BaseStoreModel baseStore = getBaseStoreService().getCurrentBaseStore();
		if (baseStore.getSAPConfiguration() != null
				&& baseStore.getSAPConfiguration().getSaps4deliverycostconditiontype() != null) {
			LOG.debug("Populate item shipping price for order entry with item shipping price condition type as {}",
					baseStore.getSAPConfiguration().getSaps4deliverycostconditiontype());
			pricingElement = source
					.getPricingElements().getSalesOrderPricingElements().stream().filter(data -> baseStore
							.getSAPConfiguration().getSaps4deliverycostconditiontype().equals(data.getConditionType()))
					.findAny().orElse(null);
		}
		return pricingElement;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public CustomerService getCustomerService() {
		return customerService;
	}

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}

	public ProductService getProductService() {
		return productService;
	}

	public void setProductService(ProductService productService) {
		this.productService = productService;
	}

	public CommonI18NService getCommonI18NService() {
		return commonI18NService;
	}

	public void setCommonI18NService(CommonI18NService commonI18NService) {
		this.commonI18NService = commonI18NService;
	}

	public BaseStoreService getBaseStoreService() {
		return baseStoreService;
	}

	public void setBaseStoreService(BaseStoreService baseStoreService) {
		this.baseStoreService = baseStoreService;
	}

	public B2BUnitService<B2BUnitModel, B2BCustomerModel> getB2bUnitService() {
		return b2bUnitService;
	}

	public void setB2bUnitService(B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService) {
		this.b2bUnitService = b2bUnitService;
	}

	public B2BCostCenterService<B2BCostCenterModel, B2BCustomerModel> getB2bCostCenterService() {
		return b2bCostCenterService;
	}

	public void setB2bCostCenterService(
			B2BCostCenterService<B2BCostCenterModel, B2BCustomerModel> b2bCostCenterService) {
		this.b2bCostCenterService = b2bCostCenterService;
	}

	public SAPDefaultUnitService getSapUnitService() {
		return sapUnitService;
	}

	public void setSapUnitService(SAPDefaultUnitService sapUnitService) {
		this.sapUnitService = sapUnitService;
	}

	public BaseSiteService getBaseSiteService() {
		return baseSiteService;
	}

	public void setBaseSiteService(BaseSiteService baseSiteService) {
		this.baseSiteService = baseSiteService;
	}

	protected KeyGenerator getGuidKeyGenerator() {
		return guidKeyGenerator;
	}

	public void setGuidKeyGenerator(final KeyGenerator guidKeyGenerator) {
		this.guidKeyGenerator = guidKeyGenerator;
	}

	public ModelService getModelService() {
		return modelService;
	}

	public void setModelService(ModelService modelService) {
		this.modelService = modelService;
	}

	public Map<String, ConsignmentEntryStatus> getSapS4OMConsignmentEntryStatusMap() {
		return sapS4OMConsignmentEntryStatusMap;
	}

	public void setSapS4OMConsignmentEntryStatusMap(
			Map<String, ConsignmentEntryStatus> sapS4OMConsignmentEntryStatusMap) {
		this.sapS4OMConsignmentEntryStatusMap = sapS4OMConsignmentEntryStatusMap;
	}

	public Map<String, OrderStatus> getSapS4OMOrderStatusMap() {
		return sapS4OMOrderStatusMap;
	}

	public void setSapS4OMOrderStatusMap(Map<String, OrderStatus> sapS4OMOrderStatusMap) {
		this.sapS4OMOrderStatusMap = sapS4OMOrderStatusMap;
	}

	public SapS4OrderManagementConfigService getSapS4OrderManagementConfigService() {
		return sapS4OrderManagementConfigService;
	}

	public void setSapS4OrderManagementConfigService(SapS4OrderManagementConfigService sapS4OrderManagementConfigService) {
		this.sapS4OrderManagementConfigService = sapS4OrderManagementConfigService;
	}

}
