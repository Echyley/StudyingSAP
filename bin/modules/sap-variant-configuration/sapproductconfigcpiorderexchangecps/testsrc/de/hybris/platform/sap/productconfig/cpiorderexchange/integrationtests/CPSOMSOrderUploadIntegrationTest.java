/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.cpiorderexchange.integrationtests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.ManualTest;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommercePlaceOrderStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.service.data.CommerceOrderResult;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.DebitPaymentInfoModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.order.DeliveryModeService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.sap.productconfig.integration.service.impl.SwitchableProductConfigIntegrationCloneAbstractOrderHook;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.services.integrationtests.CPQServiceLayerTest;
import de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService;
import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;
import de.hybris.platform.servicelayer.security.auth.InvalidCredentialsException;
import de.hybris.platform.servicelayer.user.AddressService;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


@ManualTest
public class CPSOMSOrderUploadIntegrationTest extends CPQServiceLayerTest
{

	private static final Logger LOG = Logger.getLogger(CPSOMSOrderUploadIntegrationTest.class);
	private static final String ORDER_PROCESS_DEFINITION_NAME = "order-process";
	private static final int timeOut = 15; //seconds

	@Resource(name = "commercePlaceOrderStrategy")
	protected CommercePlaceOrderStrategy placeOrderStrategy;

	@Resource(name = "deliveryModeService")
	protected DeliveryModeService deliveryModeService;

	@Resource(name = "addressService")
	protected AddressService addressService;

	@Resource(name = "testDefaultOutboundService")
	protected TestSapCpiOutboundServiceImpl testDefaultOutboundService;

	@Resource(name = "sapProductConfigIntegrationCloneAbstractOrderHookForTest")
	protected SwitchableProductConfigIntegrationCloneAbstractOrderHook switchableProductConfigIntegrationCloneAbstractOrderHook;


	protected Object sapSendConsignmentToSapCpiStrategy = null;

	protected Object originalOutboundService;
	protected Class<?> classSapSendConsignmentToSapCpiStrategy;
	protected Method methodSetService;

	@Before
	public void setUp() throws Exception
	{
		if (Registry.getApplicationContext().containsBean("sapSendConsignmentToSapCpiStrategy"))
		{
			sapSendConsignmentToSapCpiStrategy = Registry.getApplicationContext().getBean("sapSendConsignmentToSapCpiStrategy");
			classSapSendConsignmentToSapCpiStrategy = sapSendConsignmentToSapCpiStrategy.getClass();

			final Class[] parameterType = null;
			final Method methodGetService = classSapSendConsignmentToSapCpiStrategy.getDeclaredMethod("getSapCpiOutboundService",
					parameterType);
			methodGetService.setAccessible(true);
			originalOutboundService = methodGetService.invoke(sapSendConsignmentToSapCpiStrategy, parameterType);
			methodSetService = classSapSendConsignmentToSapCpiStrategy.getDeclaredMethod("setSapCpiOutboundService",
					SapCpiOutboundService.class);
			methodSetService.invoke(sapSendConsignmentToSapCpiStrategy, testDefaultOutboundService);
			LOG.info("Have set test SCPI outbound service");
		}
		switchableProductConfigIntegrationCloneAbstractOrderHook.activateEntryNumberSpacing();
	}

	@After
	public void cleanUp() throws Exception
	{
		if (sapSendConsignmentToSapCpiStrategy != null)
		{
			methodSetService.invoke(sapSendConsignmentToSapCpiStrategy, originalOutboundService);
			LOG.info("Restored SCPI outbound service to: " + originalOutboundService.getClass());
		}
		switchableProductConfigIntegrationCloneAbstractOrderHook.deActivateEntryNumberSpacing();
	}


	@Test
	public void testOrderSplitWithConfigurator() throws Exception
	{
		if (isExtensionInSetup("ysapcpiomsfulfillment"))
		{
			ensureMockProvider();
			prepareCPQData();
			importCPQStockData();
			importCPQUserData();
			importSCPIData();
			importOMSData();

			login();

			addConfigToCart(KB_CONF_HOME_THEATER);
			addConfigToCart(KB_CPQ_LAPTOP);

			addPaymentInfo();
			addDeliveryInfo();

			final CommerceOrderResult commerceOrderResult = placeOrder();
			final OrderModel orderModel = commerceOrderResult.getOrder();
			assertNotNull(orderModel);

			final Set<SAPOrderModel> sapOrders = waitForSAPOrders(orderModel, timeOut);
			assertNotNull(sapOrders);
			assertEquals("We expect 2 SAP orders as 2 consignments (one for each warehouse) should have been sent", 2,
					sapOrders.size());
			sapOrders.forEach(sapOrder -> checkSapOrder(sapOrder));

		}
		else
		{
			LOG.info("Test will not be executed as the OMS prerequisites are not present");
		}
	}

	protected void addConfigToCart(final KBKey kbKey) throws CommerceCartModificationException
	{
		final ConfigModel configModel = cpqService.createDefaultConfiguration(kbKey);
		assertNotNull(configModel);

		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setConfigId(configModel.getId());
		final ProductModel product = productService.getProductForCode((kbKey.getProductCode()));
		parameter.setProduct(product);
		parameter.setCart(cartService.getSessionCart());
		parameter.setQuantity(1L);
		parameter.setEnableHooks(true);

		final CommerceCartModification commerceCartModification = commerceCartService.addToCart(parameter);
		assertNotNull(commerceCartModification);
		assertEquals("success", commerceCartModification.getStatusCode());
		LOG.info(String.format("Added %s to cart", kbKey.getProductCode()));
	}

	protected void login() throws InvalidCredentialsException
	{
		login("cpq01", "welcome");
	}

	protected void checkSapOrder(final SAPOrderModel sapOrderModel)
	{
		LOG.info("Checking SAP order " + sapOrderModel.getCode());
		final Set<ConsignmentModel> consignments = sapOrderModel.getConsignments();
		assertNotNull(consignments);
		assertEquals("We expect one consignment as base of the SCPI message", 1, consignments.size());
		final ConsignmentModel consignmentModel = consignments.iterator().next();
		final Set<ConsignmentEntryModel> consignmentEntries = consignmentModel.getConsignmentEntries();
		assertNotNull(consignmentEntries);
		assertEquals("We expect one consignment entry corresponding to one of our order entries", 1, consignmentEntries.size());
		final ConsignmentEntryModel consignmentEntryModel = consignmentEntries.iterator().next();
		assertEquals("We expect that sap entry row numbers are entry numbers increased by 1", 1,
				consignmentEntryModel.getSapOrderEntryRowNumber() % 10);

		final AbstractOrderEntryModel consignmentOrderEntry = consignmentEntryModel.getOrderEntry();
		assertNotNull("We expect a configuration to be present in the consignment entry",
				consignmentOrderEntry.getProductConfiguration());
		LOG.info("Product configuration found in consignment order entry: "
				+ consignmentOrderEntry.getProductConfiguration().getConfigurationId());
		LOG.info("Entry number found in consignment order entry: " + consignmentOrderEntry.getEntryNumber());
		assertEquals("We expect that entry numbers have been kept stable", 0, consignmentOrderEntry.getEntryNumber() % 10);
	}

	protected CommerceOrderResult placeOrder() throws InvalidCartException
	{
		final CartModel cart = cartService.getSessionCart();
		final CommerceCheckoutParameter checkoutParameter = new CommerceCheckoutParameter();
		checkoutParameter.setCart(cart);
		checkoutParameter.setEnableHooks(true);

		final CommerceOrderResult commerceOrderResult = placeOrderStrategy.placeOrder(checkoutParameter);
		return commerceOrderResult;
	}

	protected void addDeliveryInfo()
	{
		final CartModel cart = cartService.getSessionCart();
		cart.setDeliveryMode(deliveryModeService.getDeliveryModeForCode("premium-gross"));
		cart.setDeliveryAddress(addressService.getAddressesForOwner(realUserService.getCurrentUser()).iterator().next());
	}

	protected void addPaymentInfo()
	{
		final CartModel cart = cartService.getSessionCart();
		final DebitPaymentInfoModel paymentInfo = new DebitPaymentInfoModel();

		paymentInfo.setOwner(cart);
		paymentInfo.setCode(UUID.randomUUID().toString());
		paymentInfo.setBank("MyBank");
		paymentInfo.setUser(realUserService.getCurrentUser());
		paymentInfo.setAccountNumber("34434");
		paymentInfo.setBankIDNumber("1111112");
		paymentInfo.setBaOwner("Tiger");
		cart.setPaymentInfo(paymentInfo);
	}

	protected Set<SAPOrderModel> waitForSAPOrders(final OrderModel order, final int timeOut) throws InterruptedException
	{
		int timeCount = 0;
		Set<SAPOrderModel> sapOrders = null;
		do
		{
			Thread.sleep(1000);
			modelService.refresh(order);
			sapOrders = order.getSapOrders();
		}
		while ((sapOrders == null || sapOrders.size() != 2) && timeCount++ < timeOut);
		modelService.refresh(order);
		return sapOrders;
	}

	protected void importSCPIData() throws ImpExException
	{
		importCsv("/impex/essentialdata-OutboundOMMOrderOMSOrder.impex", "utf-8");
		importCsv("/impex/essentialdata-sapproductconfigcpiorderexchange-outboundOrder.impex", "utf-8");
		importCsv("/impex/projectdata-OutboundDestinations.impex", "utf-8");
	}

	protected void importOMSData() throws ImpExException
	{
		importCsv("/impex/projectdata-dynamic-business-process-order.impex", "utf-8");
		importCsv("/impex/projectdata-dynamic-business-process-consignment.impex", "utf-8");
		importCsv("/test/sapproductconfigcpiorderexchangecps.impex", "utf-8");
	}
}
