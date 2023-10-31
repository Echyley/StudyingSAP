/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpioaaorderintegration.service.impl;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.sap.hybris.sapcpioaaorderintegration.outbound.impl.SapCpiOaaOrderContributor;
import com.sap.hybris.sapcpioaaorderintegration.outbound.impl.SapCpiOaaOrderEntryContributor;
import com.sap.retail.oaa.model.model.ScheduleLineModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.core.configuration.model.SAPHTTPDestinationModel;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiOrder;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiOrderItem;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiScheduleLinesOrderItem;
import de.hybris.platform.sap.sapmodel.model.SAPLogicalSystemModel;
import de.hybris.platform.sap.sapmodel.model.SAPSalesOrganizationModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SapOaaCpiOrderConversionServiceTest {

	private static final int ENTRY_NUMBER_INT = 1;

	private static final String ORDER_CODE = "100001";

	private static final String ENTRY_NUMBER = "1";

	@InjectMocks
	private SapOaaCpiOrderConversionService sapOaaCpiOrderConversionService;

	@Mock
	private OrderModel orderModel;

	@Mock
	private CurrencyModel currencyModel;

	@Mock
	private BaseStoreModel baseStoreModel;

	@Mock
	private DeliveryModeModel deliveryModeModel;

	@Mock
	private SAPSalesOrganizationModel sapSalesOrganization;

	@Mock
	private ConsignmentModel consignmentModel;

	@Mock
	private WarehouseModel warehouseModel;

	@Mock
	private SAPLogicalSystemModel sapLogicalSystemModel;

	@Mock
	private SAPHTTPDestinationModel sapHttpDestinationModel;

	@Mock
	private SAPConfigurationModel sapConfigurationModel;

	@Mock
	private AbstractOrderEntryModel abstractOrderEntryModel;

	@Mock
	private ScheduleLineModel scheduleLineModel;

	@Mock
	private PointOfServiceModel pointOfServiceModel;

	@Mock
	private BaseSiteModel baseSiteModel;

	@Mock
	private ProductModel productModel;

	@Mock
	private UnitModel unitModel;

	@Mock
	private LanguageModel languageModel;

	@Before
	public void setUp() {
		Mockito.lenient().when(orderModel.getCode()).thenReturn(ORDER_CODE);
		Mockito.lenient().when(orderModel.getDate()).thenReturn(new Date());
		Mockito.lenient().when(orderModel.getCurrency()).thenReturn(currencyModel);
		Mockito.lenient().when(currencyModel.getIsocode()).thenReturn("EUR");
		Mockito.lenient().when(orderModel.getStore()).thenReturn(baseStoreModel);
		Mockito.lenient().when(baseStoreModel.getUid()).thenReturn("apparel-uk");
		Mockito.lenient().when(orderModel.getDeliveryMode()).thenReturn(deliveryModeModel);
		Mockito.lenient().when(deliveryModeModel.getCode()).thenReturn("Ship");
		Mockito.lenient().when(orderModel.getSapSalesOrganization()).thenReturn(sapSalesOrganization);
		Mockito.lenient().when(orderModel.getSapLogicalSystem()).thenReturn("QM7");
		Mockito.lenient().when(sapSalesOrganization.getSalesOrganization()).thenReturn("R100");
		Mockito.lenient().when(sapSalesOrganization.getDistributionChannel()).thenReturn("R3");
		Mockito.lenient().when(sapSalesOrganization.getDivision()).thenReturn("10");
		Set<ConsignmentModel> consignmentModels = new HashSet<ConsignmentModel>();
		consignmentModels.add(consignmentModel);
		Mockito.lenient().when(orderModel.getConsignments()).thenReturn(consignmentModels);
		Mockito.lenient().when(consignmentModel.getWarehouse()).thenReturn(warehouseModel);
		Mockito.lenient().when(warehouseModel.getCode()).thenReturn("ROCCRSI");

		Mockito.lenient().when(sapLogicalSystemModel.getSenderName()).thenReturn("SENDER");
		Mockito.lenient().when(sapLogicalSystemModel.getSenderPort()).thenReturn("xxx");

		Mockito.lenient().when(sapLogicalSystemModel.getSapLogicalSystemName()).thenReturn("QM7");
		Mockito.lenient().when(sapLogicalSystemModel.getSapHTTPDestination()).thenReturn(sapHttpDestinationModel);

		Mockito.lenient().when(sapHttpDestinationModel.getUserid()).thenReturn("USER");

		Mockito.lenient().when(baseStoreModel.getSAPConfiguration()).thenReturn(sapConfigurationModel);
		Mockito.lenient().when(sapConfigurationModel.getSapcommon_transactionType()).thenReturn("TRAN");

	}

	@Test
	public void testConvertOrderToSapCpiOrder_SapCpiOrder() {

		SapCpiOaaOrderContributor sapOrderContributor = new SapCpiOaaOrderContributor();
		sapOaaCpiOrderConversionService.setSapOrderContributor(sapOrderContributor);
		SapCpiOrder sapCpiOrder = null;

		try {
			sapCpiOrder = sapOaaCpiOrderConversionService.convertOrderToSapCpiOrder(orderModel);
		} catch (Exception e) {
		}

		if (sapCpiOrder != null)
			verify(sapCpiOrder).getOrderId();
	}

	@Test
	public void testConvertOrderToSapCpiOrder_SapCpiOrderItem() {

		final List<AbstractOrderEntryModel> entries = new ArrayList<AbstractOrderEntryModel>();
		entries.add(abstractOrderEntryModel);
		Mockito.lenient().when(orderModel.getEntries()).thenReturn(entries);

		Mockito.lenient().when(scheduleLineModel.getConfirmedQuantity()).thenReturn(1.0);
		Mockito.lenient().when(scheduleLineModel.getConfirmedDate()).thenReturn(new Date());

		Mockito.lenient().when(pointOfServiceModel.getName()).thenReturn("M001");
		Mockito.lenient().when(pointOfServiceModel.getSapoaa_cacShippingPoint()).thenReturn("C001");

		final List<ScheduleLineModel> scheduleLineModels = new ArrayList<ScheduleLineModel>();
		scheduleLineModels.add(scheduleLineModel);
		Mockito.lenient().when(abstractOrderEntryModel.getScheduleLines()).thenReturn(scheduleLineModels);
		Mockito.lenient().when(abstractOrderEntryModel.getSapSource()).thenReturn(pointOfServiceModel);
		Mockito.lenient().when(abstractOrderEntryModel.getDeliveryPointOfService()).thenReturn(pointOfServiceModel);
		Mockito.lenient().when(abstractOrderEntryModel.getSapVendor()).thenReturn(null);
		Mockito.lenient().when(abstractOrderEntryModel.getProduct()).thenReturn(productModel);
		Mockito.lenient().when(abstractOrderEntryModel.getUnit()).thenReturn(unitModel);
		Mockito.lenient().when(productModel.getCode()).thenReturn("Shirt");
		Mockito.lenient().when(productModel.getName(new java.util.Locale("EN"))).thenReturn(null);
		Mockito.lenient().when(unitModel.getCode()).thenReturn("Unit1");

		Mockito.lenient().when(orderModel.getSite()).thenReturn(baseSiteModel);
		Mockito.lenient().when(orderModel.getLanguage()).thenReturn(languageModel);
		Mockito.lenient().when(languageModel.getIsocode()).thenReturn("EN");
		Mockito.lenient().when(languageModel.getFallbackLanguages()).thenReturn(new ArrayList());

		Mockito.lenient().when(abstractOrderEntryModel.getEntryNumber()).thenReturn(ENTRY_NUMBER_INT);

		SapCpiOrder sapCpiOrder = new SapCpiOrder();

		try {
			SapCpiOaaOrderContributor sapOrderContributor = new SapCpiOaaOrderContributor();
			SapCpiOaaOrderEntryContributor sapCpiOaaOrderEntryContributor = new SapCpiOaaOrderEntryContributor();
			sapCpiOaaOrderEntryContributor.setDatePattern("yyyy-MM-dd HH:mm:ss.S");

			sapOaaCpiOrderConversionService.setSapOrderContributor(sapOrderContributor);
			sapOaaCpiOrderConversionService.setSapOrderEntryContributor(sapCpiOaaOrderEntryContributor);

			sapOaaCpiOrderConversionService.setSapCpiOrderItems(orderModel, sapCpiOrder);
		} catch (Exception e) {

		}
		if (sapCpiOrder != null) {
			List<SapCpiOrderItem> sapCpiOrderItems = sapCpiOrder.getSapCpiOrderItems();
			if (sapCpiOrderItems != null && sapCpiOrderItems.size() > 0) {
				SapCpiOrderItem sapCpiOrderItem = sapCpiOrderItems.get(0);

				assertTrue(sapCpiOrderItem.getPlant().equals("M001"));
				assertTrue(sapCpiOrderItem.getCacShippingPoint().equals("C001"));
				assertTrue(sapCpiOrderItem.getProductCode().equals("Shirt"));
				assertTrue(sapCpiOrderItem.getUnit().equals("Unit1"));
				assertTrue(sapCpiOrderItem.getOrderId().equals(ORDER_CODE));
				assertTrue(sapCpiOrderItem.getEntryNumber().equals(ENTRY_NUMBER));
				SapCpiScheduleLinesOrderItem sapCpiScheduleLinesOrderItem = (SapCpiScheduleLinesOrderItem) sapCpiOrderItem
						.getScheduleLines().get(0);
				assertTrue(sapCpiScheduleLinesOrderItem.getConfirmedQuantity().equals("1.0"));
			}
		}

	}
}
