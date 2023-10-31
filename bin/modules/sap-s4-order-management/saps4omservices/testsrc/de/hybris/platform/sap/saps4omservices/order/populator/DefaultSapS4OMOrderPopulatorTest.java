/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omservices.order.populator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BCostCenterService;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.commerceservices.customer.CustomerService;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.sapmodel.enums.ConsignmentEntryStatus;
import de.hybris.platform.sap.sapmodel.model.SAPDeliveryModeModel;
import de.hybris.platform.sap.sapmodel.services.impl.SAPDefaultUnitService;
import de.hybris.platform.sap.saps4omservices.services.SapS4OrderManagementConfigService;
import de.hybris.platform.saps4omservices.dto.PartnerData;
import de.hybris.platform.saps4omservices.dto.PartnersData;
import de.hybris.platform.saps4omservices.dto.PricingElementData;
import de.hybris.platform.saps4omservices.dto.PricingElementsData;
import de.hybris.platform.saps4omservices.dto.SAPS4OMItemData;
import de.hybris.platform.saps4omservices.dto.SAPS4OMItemsData;
import de.hybris.platform.saps4omservices.dto.SAPS4OMResponseData;
import de.hybris.platform.saps4omservices.dto.ScheduleLineData;
import de.hybris.platform.saps4omservices.dto.ScheduleLinesData;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSapS4OMOrderPopulatorTest 
{
	private static final String CURRENCY_USD = "USD";
	private static final String TOTAL_NET_AMOUNT = "64.65";
	private static final String SHIPPING_CONDITION_AMOUNT = "12.00";
	private static final String SHIPPING_PRICE_CONDITION_TYPE = "YBHD";
	private static final String SHIPPING_COND = "01";
	private static final String DELIVERY_MODE_VALUE = "standard-gross";
	private static final String SALES_ORDER_DATE = "/Date(1646611200000)/";
	private static final String SALES_REQUESTED_DELIVERY_DATE = "/Date(1646611200000)/";
	private static final String PO_NUMBER = "789";
	private static final String ITEM_CONDITION_AMOUNT = "17.55";
	private static final String ITEM_PRICE_CONDITION_TYPE = "PPR0";
	private static final String SALES_ORDER_ITEM = "10";
	private static final String REQUESTED_QTY = "1";
	private static final String MATERIAL_CODE = "TG11";
	private static final String SD_PROCESS_STATUS_B = "B";
	private static final String SD_PROCESS_STATUS_C = "C";
	private static final String TOTAL_DELIVERY_STATUS_A = "A";
	private static final String TOTAL_DELIVERY_STATUS_C = "C";
	private static final String OVERALL_SD_PROCESS_STATUS_B = "B";
	private static final String PARTNER_FUNCTION_CONTACT_PERSON = "CP";
	private static final String CONTACT_PERSON = "123";
	private static final String ORDER_STATUS_IN_PROCESS = "IN_PROCESS";
	private static final String ORDER_STATUS_COMPLETED = "COMPLETED";
	private static final String DELIVERY_STATUS_NOTSHIPPED = "NOTSHIPPED";
	private static final String DELIVERY_STATUS_SHIPPED = "SHIPPED";
	private static final String RANDOM_KEY = "TEST";
	private static final String ITEM_TAX_AMOUNT = "0.28";
	private static final String ITEM_NET_AMOUNT = "18.34";
	private static final String ITEM_SUBTOTAL_AMOUNT = "6.34";
	
	@Spy
	@InjectMocks
	private DefaultSapS4OMOrderPopulator sapS4OMOrderPopulator;
	
	@Mock
	private UserService userService;
	
	@Mock
	private CustomerService customerService;
	
	@Mock
	private ProductService productService;
	
	@Mock
	private BaseStoreService baseStoreService;
	
	@Mock
	private CommonI18NService commonI18NService;
	
	@Mock
	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;
	
	@Mock
	private B2BCostCenterService<B2BCostCenterModel, B2BCustomerModel> b2bCostCenterService;
	
	@Mock
	private BaseSiteService baseSiteService;
	
	@Mock
	private KeyGenerator guidKeyGenerator;
	
	@Mock
	private ModelService modelService;
	
	@Mock
	private SAPDefaultUnitService sapDefaultUnitService;
	
	@Mock
	private Map<String, OrderStatus> sapS4OMOrderStatusMap;
	
	@Mock
	private Map<String, ConsignmentEntryStatus> sapS4OMConsignmentEntryStatusMap;
	
	@Mock
	private SapS4OrderManagementConfigService sapS4OrderManagementConfigService; 
	
	private SAPS4OMResponseData source;
	private OrderModel target;
	
	@Before
	public void setUp()
	{
		source = mock(SAPS4OMResponseData.class);
		target = new OrderModel();
		sapS4OMOrderPopulator.setUserService(userService);
		sapS4OMOrderPopulator.setCustomerService(customerService);
		sapS4OMOrderPopulator.setProductService(productService);
		sapS4OMOrderPopulator.setBaseStoreService(baseStoreService);
		sapS4OMOrderPopulator.setCommonI18NService(commonI18NService);
		sapS4OMOrderPopulator.setB2bUnitService(b2bUnitService);
		sapS4OMOrderPopulator.setB2bCostCenterService(b2bCostCenterService);
	}
	
	@Test
	public void testShouldPopulate() {
		
		/* populateCommon */
		final LanguageModel language = mock(LanguageModel.class);
		language.setIsocode("de");
		final Locale locale = mock(Locale.class);
		final CustomerModel customerModel = mock(CustomerModel.class);
		given(source.getSalesOrder()).willReturn(MATERIAL_CODE);
		final PartnersData partnersData = new PartnersData();
		final List<PartnerData> partnerDataList = new ArrayList<>();
		PartnerData partnerData = new PartnerData();
		partnerData.setPartnerFunction(PARTNER_FUNCTION_CONTACT_PERSON);
		partnerData.setContactPerson(CONTACT_PERSON);
		partnerDataList.add(partnerData);
		partnersData.setSalesOrderPartners(partnerDataList);
		given(commonI18NService.getCurrentLanguage()).willReturn(language);
		given(commonI18NService.getLocaleForIsoCode(language.getIsocode())).willReturn(locale);
		given(source.getPartner()).willReturn(partnersData);
		given(customerService.getCustomerByCustomerId(CONTACT_PERSON)).willReturn(customerModel);
		given(source.getPurchaseOrderByCustomer()).willReturn(PO_NUMBER);
		given(source.getSalesOrderDate()).willReturn(SALES_ORDER_DATE);
		given(guidKeyGenerator.generate()).willReturn(RANDOM_KEY);
		given(modelService.create(ConsignmentModel.class)).willReturn(new ConsignmentModel());
		given(source.getRequestedDeliveryDate()).willReturn(SALES_REQUESTED_DELIVERY_DATE);
		given(sapS4OrderManagementConfigService.isRequestedRetrievalDateFeatureEnabled(any(BaseStoreModel.class))).willReturn(true);
		
		/* populateOrderEntry */
		final SAPS4OMItemsData itemsData = new SAPS4OMItemsData(); 
		final SAPS4OMItemData itemData = new SAPS4OMItemData(); 
		itemData.setMaterial(MATERIAL_CODE);
		itemData.setRequestedQuantity(REQUESTED_QTY);
		itemData.setSalesOrderItem(SALES_ORDER_ITEM);
		itemData.setTaxAmount(ITEM_TAX_AMOUNT);
		itemData.setNetAmount(ITEM_NET_AMOUNT);
		final List<SAPS4OMItemData> itemDataList = new ArrayList<>();
		itemDataList.add(itemData);
		itemsData.setSalesOrderItems(itemDataList);
		final PricingElementsData pricingElements = new PricingElementsData();
		final PricingElementData itemPricingElement = new PricingElementData();
		itemPricingElement.setConditionType(ITEM_PRICE_CONDITION_TYPE);
		itemPricingElement.setConditionRateValue(ITEM_CONDITION_AMOUNT);
		itemPricingElement.setConditionAmount(ITEM_CONDITION_AMOUNT);
		final PricingElementData itemPricingElementShippingPrice = new PricingElementData();
		itemPricingElementShippingPrice.setConditionType(SHIPPING_PRICE_CONDITION_TYPE);
		itemPricingElementShippingPrice.setConditionAmount(SHIPPING_CONDITION_AMOUNT);
		
		final List<PricingElementData> pricingElementList = new ArrayList<>();
		pricingElementList.add(itemPricingElement);
		pricingElementList.add(itemPricingElementShippingPrice);
		pricingElements.setSalesOrderPricingElements(pricingElementList);
		itemData.setPricingElements(pricingElements);
		
		ScheduleLinesData scheduleLinesData = spy(ScheduleLinesData.class);
		ScheduleLineData scheduleLineData = spy(ScheduleLineData.class);
		itemData.setScheduleLines(scheduleLinesData);
		when(scheduleLinesData.getSalesOrderScheduleLines()).thenReturn(Collections.singletonList(scheduleLineData));
		when(scheduleLineData.getConfirmedDeliveryDate()).thenReturn("30/05/2050 02:34:56");
		when(scheduleLineData.getConfdOrderQtyByMatlAvailCheck()).thenReturn("1");
		
		final ProductModel productModel = mock(ProductModel.class);
		final BaseStoreModel baseStoreModel = mock(BaseStoreModel.class);
		final SAPConfigurationModel sapConfigurationModel = mock(SAPConfigurationModel.class);
		final List<B2BCostCenterModel> b2bCostCenters = new ArrayList<>();
		b2bCostCenters.add(new B2BCostCenterModel());
		given(source.getItems()).willReturn(itemsData);
		given(productService.getProductForCode(MATERIAL_CODE)).willReturn(productModel);
		given(baseStoreService.getCurrentBaseStore()).willReturn(baseStoreModel);
		given(baseStoreModel.getSAPConfiguration()).willReturn(sapConfigurationModel);
		given(sapConfigurationModel.getSaps4itempriceconditiontype()).willReturn(ITEM_PRICE_CONDITION_TYPE);
		given(b2bCostCenterService.getCostCentersForUnitBranch(any(B2BUnitModel.class), any(CurrencyModel.class))).willReturn(b2bCostCenters);
		
		/* populateOrderStatus */
		given(source.getOverallSDProcessStatus()).willReturn(OVERALL_SD_PROCESS_STATUS_B);
		given(source.getOverallTotalDeliveryStatus()).willReturn(OVERALL_SD_PROCESS_STATUS_B);
		given(sapS4OMOrderStatusMap.get(SD_PROCESS_STATUS_B)).willReturn(OrderStatus.IN_PROCESS);
		given(sapS4OMOrderStatusMap.containsKey(Mockito.any())).willReturn(true);
		
		/* populateDeliveryAddress */
		final B2BUnitModel parentB2BUnit = new B2BUnitModel();
		final Set<B2BUnitModel> subB2BUnits = new HashSet<>();
		final B2BUnitModel subB2BUnit1 = new B2BUnitModel();
		final AddressModel subB2BUnit1Address = new AddressModel();
		subB2BUnit1Address.setShippingAddress(true);
		subB2BUnit1.setAddresses(Collections.singletonList(subB2BUnit1Address));
		
		final B2BUnitModel subB2BUnit2 = new B2BUnitModel();
		final AddressModel subB2BUnit2Address = new AddressModel();
		subB2BUnit2Address.setShippingAddress(false);
		subB2BUnit2.setAddresses(Collections.singletonList(subB2BUnit2Address));
		subB2BUnits.add(subB2BUnit1);
		subB2BUnits.add(subB2BUnit2);
		
		given(b2bUnitService.getUnitForUid(null)).willReturn(parentB2BUnit);
		given(b2bUnitService.getBranch(parentB2BUnit)).willReturn(subB2BUnits);
		
		/* populateDeliveryMode */
		final SAPDeliveryModeModel sapDeliveryMode = new SAPDeliveryModeModel();
		sapDeliveryMode.setDeliveryValue(DELIVERY_MODE_VALUE);
		final Set<SAPDeliveryModeModel> sapDeliveryModeSet = new HashSet<>();
		final DeliveryModeModel deliveryMode = new DeliveryModeModel();
		sapDeliveryMode.setDeliveryMode(deliveryMode);
		sapDeliveryModeSet.add(sapDeliveryMode);
		given(source.getShippingCondition()).willReturn(SHIPPING_COND);
		given(sapConfigurationModel.getSapDeliveryModes()).willReturn(sapDeliveryModeSet);
		
		/* populateDeliveryMode */
		final PricingElementsData headerPricingElements = new PricingElementsData();
		final PricingElementData headerPricingElement = new PricingElementData();
		headerPricingElement.setConditionType(SHIPPING_PRICE_CONDITION_TYPE);
		headerPricingElement.setConditionRateValue(SHIPPING_CONDITION_AMOUNT);
		final List<PricingElementData> headerPricingElementList = new ArrayList<>();
		headerPricingElementList.add(headerPricingElement);
		headerPricingElements.setSalesOrderPricingElements(headerPricingElementList);
		given(source.getTotalNetAmount()).willReturn(TOTAL_NET_AMOUNT);
		given(source.getPricingElements()).willReturn(headerPricingElements);
		given(sapConfigurationModel.getSaps4deliverycostconditiontype()).willReturn(SHIPPING_PRICE_CONDITION_TYPE);
		
		/* populateCurrency */
		final CurrencyModel currencyModel = spy(CurrencyModel.class);
		given(source.getTransactionCurrency()).willReturn(CURRENCY_USD);
		given(commonI18NService.getCurrency(CURRENCY_USD)).willReturn(currencyModel);
		
		sapS4OMOrderPopulator.populate(source, target);
		assertEquals(source.getSalesOrder(),target.getCode());
		assertEquals("2022-03-07", target.getRequestedRetrievalDate());
		assertFalse(source.getItems().getSalesOrderItems().isEmpty());
		assertFalse(target.getEntries().get(0).getDeliveryScheduleLines().isEmpty());
		assertEquals(1, target.getEntries().get(0).getDeliveryScheduleLines().size());
		final Date salesOrderDateTest = new Date(Long.parseLong(source.getSalesOrderDate().replaceAll(".*?(\\d+).*", "$1")));
		assertEquals(salesOrderDateTest,target.getDate());
		Double itemSubtotalAmount = Double.parseDouble(ITEM_SUBTOTAL_AMOUNT);
		assertEquals(itemSubtotalAmount, target.getSubtotal());
		
	}
	
	@Test
	public void testPopulateCurrency() {
		given(source.getTotalNetAmount()).willReturn(TOTAL_NET_AMOUNT);
		given(source.getTransactionCurrency()).willReturn(CURRENCY_USD);
		
		try
		{
			sapS4OMOrderPopulator.populateCurrency(source, target);
		}
		catch (final IllegalArgumentException e)
		{
			Assert.assertEquals("Unexpected reason of fail",
					"Order currency must not be null", e.getMessage());
		}
	}
	
	@Test
	public void testPopulateStatusNotShipped() {
		given(source.getOverallSDProcessStatus()).willReturn(SD_PROCESS_STATUS_B);
		given(source.getOverallTotalDeliveryStatus()).willReturn(null);
		given(sapS4OMOrderStatusMap.get(SD_PROCESS_STATUS_B)).willReturn(OrderStatus.IN_PROCESS);
		given(sapS4OMOrderStatusMap.containsKey(Mockito.any())).willReturn(true);
		
		sapS4OMOrderPopulator.populateOrderStatus(source, target);
		sapS4OMOrderPopulator.populateDeliveryStatus(source, target);
		assertEquals(ORDER_STATUS_IN_PROCESS,target.getStatus().getCode());
		assertEquals(DELIVERY_STATUS_NOTSHIPPED,target.getDeliveryStatus().getCode());
	}
	
	@Test
	public void testPopulateStatusShipped() {
		given(source.getOverallSDProcessStatus()).willReturn(SD_PROCESS_STATUS_C);
		given(sapS4OMOrderStatusMap.get(SD_PROCESS_STATUS_C)).willReturn(OrderStatus.COMPLETED);
		given(sapS4OMOrderStatusMap.containsKey(Mockito.any())).willReturn(true);
		
		sapS4OMOrderPopulator.populateOrderStatus(source, target);
		sapS4OMOrderPopulator.populateDeliveryStatus(source, target);
		assertEquals(ORDER_STATUS_COMPLETED,target.getStatus().getCode());
		assertEquals(DELIVERY_STATUS_SHIPPED,target.getDeliveryStatus().getCode());
	}
	
	@Test
	public void testPopulateStatusShippedWhenDeliveryStatusNotNull() {
		given(source.getOverallSDProcessStatus()).willReturn(SD_PROCESS_STATUS_B);
		given(source.getOverallTotalDeliveryStatus()).willReturn(TOTAL_DELIVERY_STATUS_C);
		given(sapS4OMOrderStatusMap.get(SD_PROCESS_STATUS_B)).willReturn(OrderStatus.IN_PROCESS);
		given(sapS4OMOrderStatusMap.containsKey(Mockito.any())).willReturn(true);
		
		sapS4OMOrderPopulator.populateOrderStatus(source, target);
		sapS4OMOrderPopulator.populateDeliveryStatus(source, target);
		assertEquals(ORDER_STATUS_IN_PROCESS,target.getStatus().getCode());
		assertEquals(DELIVERY_STATUS_SHIPPED,target.getDeliveryStatus().getCode());
	}
	
	@Test
	public void testPopulateDefaultStatus() {
		given(source.getOverallSDProcessStatus()).willReturn(SD_PROCESS_STATUS_B);
		given(source.getOverallTotalDeliveryStatus()).willReturn(TOTAL_DELIVERY_STATUS_A);
		given(sapS4OMOrderStatusMap.get(SD_PROCESS_STATUS_B)).willReturn(OrderStatus.IN_PROCESS);
		given(sapS4OMOrderStatusMap.containsKey(Mockito.any())).willReturn(true);
		
		sapS4OMOrderPopulator.populateOrderStatus(source, target);
		sapS4OMOrderPopulator.populateDeliveryStatus(source, target);
		assertEquals(ORDER_STATUS_IN_PROCESS,target.getStatus().getCode());
		assertEquals(DELIVERY_STATUS_NOTSHIPPED,target.getDeliveryStatus().getCode());
	}

}
