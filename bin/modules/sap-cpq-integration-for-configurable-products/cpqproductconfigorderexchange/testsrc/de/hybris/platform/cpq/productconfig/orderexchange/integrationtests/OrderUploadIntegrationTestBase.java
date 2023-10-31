/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.orderexchange.integrationtests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommercePlaceOrderStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.service.data.CommerceOrderResult;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.cpq.productconfig.orderexchange.action.SapCpiOmmOrderOutboundTestAction;
import de.hybris.platform.cpq.productconfig.services.CacheAccessService;
import de.hybris.platform.cpq.productconfig.services.CacheKeyService;
import de.hybris.platform.cpq.productconfig.services.cache.DefaultCacheKey;
import de.hybris.platform.cpq.productconfig.services.data.ConfigurationSummaryData;
import de.hybris.platform.cpq.productconfig.services.integrationtests.BaseIntegrationServiceLayerTest;
import de.hybris.platform.cpq.productconfig.services.integrationtests.config.DefaultB2BIntegrationTestConfiguration;
import de.hybris.platform.cpq.productconfig.services.integrationtests.config.IntegrationTestConfiguration;
import de.hybris.platform.cpq.productconfig.services.strategies.ConfigurationLifecycleStrategy;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.sap.sapcpiorderexchange.service.SapCpiOrderConversionService;
import de.hybris.platform.sap.sapcpiorderexchange.service.impl.SapCpiOmmOrderConversionService;
import de.hybris.platform.util.Config;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Test;


public abstract class OrderUploadIntegrationTestBase extends BaseIntegrationServiceLayerTest
{
	protected static final String NIKON_D850 = "NIKON_D850";

	protected static final String CANON_EOS_80D = "CANON_EOS_80D";

	protected static final String CANON_EOS_6D_MARK_II = "CANON_EOS_6D_MARK_II";

	protected static final String CONF_CAMERA_BUNDLE = "CONF_CAMERA_BUNDLE";

	protected static final String CONFIGORDEREXCHANGE_SPACING_ITEMS = "configorderexchange.spacing.items";

	protected static final String CPQPRODUCTCONFIGSERVICES_MOCK_CONF_LAPTOP_COMPLETE = "cpqproductconfigservices.mock.CONF_LAPTOP_COMPLETE";

	protected static final String CPQPRODUCTCONFIGSERVICES_MOCK_CONF_LAPTOP = "cpqproductconfigservices.mock.CONF_LAPTOP";

	protected static final String HIGHER_LEVEL_ENTRY_NUMBER = "higherLevelEntryNumber";

	protected static final String ENTRY_NUMBER = "entryNumber";

	protected static final String PRODUCT_CODE = "productCode";

	protected static final String ORDER_ID = "orderId";

	protected static final String CONF_POWER_SUPPLY_CPQ = "CONF_POWER_SUPPLY_CPQ";

	@Resource(name = "b2bCommercePlaceOrderStrategy")
	protected CommercePlaceOrderStrategy placeOrderStrategy;

	@Resource(name = "productService")
	protected ProductService productService;

	@Resource(name = "sapSendOrderAction")
	protected SapCpiOmmOrderOutboundTestAction outboundTestAction;

	@Resource(name = "cpqProductConfigConfigurationSummaryCacheAccessService")
	protected CacheAccessService<DefaultCacheKey, ConfigurationSummaryData> cacheAccessService;

	@Resource(name = "cpqProductConfigCacheKeyService")
	protected CacheKeyService cacheKeyService;

	@Resource(name = "cpqProductConfigMockConfigurationLifecycleStrategy")
	protected ConfigurationLifecycleStrategy mockConfigurationLifeCycleStrategy;

	@Resource(name = "sapCpiOrderConversionService")
	protected SapCpiOrderConversionService conversionService;

	private static final Logger LOG = Logger.getLogger(OrderUploadIntegrationTestBase.class);

	@Override
	protected IntegrationTestConfiguration createTestConfig()
	{
		final IntegrationTestConfiguration config = new DefaultB2BIntegrationTestConfiguration();
		config.addAdditionalImpex("/cpqproductconfigservices/test/testDataOrderManagement.impex");
		config.addAdditionalImpex("/cpqproductconfigorderexchange/test/testDataOutboundOMMOrderOMSOrder.impex");
		config.addAdditionalImpex("/cpqproductconfigorderexchange/test/process.impex");
		return config;
	}

	protected void assumeOMM()
	{
		if (Config.getBoolean("cpqproductconfigservices.tests.ignoreCoDeployemntIssues", false))
		{
			assumeTrueAndLog("cpqproductconfig can only run with OMM and not with OMS, cancelling test",
					conversionService instanceof SapCpiOmmOrderConversionService
							&& !conversionService.getClass().getSimpleName().contains("Oms"),
					LOG);
		}
	}

	@Test
	public void testSendOrderWithOneBundle()
	{
		assumeOMM();
		testOrder(this::createOrderWithOneBundle, this::checkPayloadOneBundle, P_CODE_CONF_LAPTOP);
	}

	@Test
	public void testSendOrderWithTwoBundles()
	{
		assumeOMM();
		testOrder(this::createOrderWithTwoBundles, this::checkPayloadTwoBundles, P_CODE_CONF_LAPTOP);
	}

	@Test
	public void testSendOrderSpacingTooSmall()
	{
		assumeOMM();


		final OrderModel orderModel = createOrderWithOneBundle(CONF_CAMERA_BUNDLE);
		enrichOrder(orderModel);

		final OrderProcessModel orderProcessModel = new OrderProcessModel();
		orderProcessModel.setOrder(orderModel);

		try
		{
			//hierarchyMapperService.
			outboundTestAction.executeAction(orderProcessModel);
			fail("outboundTestAction should fail due to to item spacing being too small");
		}
		catch (final IllegalStateException e)
		{
			assertTrue(e.getMessage().contains("exceeds the item spacing"));
		}
	}

	@Test
	public void testSendOrderWithBundlesSimpleProducts()
	{
		assumeOMM();
		testOrder(this::createMixedOrder, this::checkMixedPayload, P_CODE_CONF_LAPTOP);
	}


	protected void testOrder(final Function<String, OrderModel> createOrder, final Consumer<Map<String, Object>> checkPayload,
			final String productCode)
	{
		final OrderModel orderModel = createOrder.apply(productCode);
		enrichOrder(orderModel);

		final OrderProcessModel orderProcessModel = new OrderProcessModel();
		orderProcessModel.setOrder(orderModel);

		outboundTestAction.executeAction(orderProcessModel);

		assertTrue(outboundTestAction.isSuccess());
		final Map<String, Object> payload = outboundTestAction.getPayLoad();

		assertNotNull(payload);
		if (LOG.isDebugEnabled())
		{
			LOG.debug("order payload: " + payload);
		}
		checkPayload.accept(payload);
	}

	protected void checkPayloadOneBundle(final Map<String, Object> payload)
	{
		final List<Map<String, Object>> items = checkPayloadGeneric(payload);
		assertEquals(2, items.size());
		final Map<String, Object> firstItem = items.get(0);
		checkRootItem(firstItem, "10", P_CODE_CONF_LAPTOP);

		final Map<String, Object> secondItem = items.get(1);
		checkLineItem(secondItem, "14", "10", getPowerSupplyProductCode());
	}

	protected void checkPayloadTwoBundles(final Map<String, Object> payload)
	{
		final List<Map<String, Object>> items = checkPayloadGeneric(payload);
		assertEquals(4, items.size());
		final Map<String, Object> firstItem = items.get(0);
		checkRootItem(firstItem, "10", P_CODE_CONF_LAPTOP);

		final Map<String, Object> secondItem = items.get(1);
		checkLineItem(secondItem, "14", "10", getPowerSupplyProductCode());

		final Map<String, Object> thirdItem = items.get(2);
		checkRootItem(thirdItem, "20", P_CODE_CONF_LAPTOP);

		final Map<String, Object> fourthItem = items.get(3);
		checkLineItem(fourthItem, "24", "20", getPowerSupplyProductCode());
	}

	protected void checkMixedPayload(final Map<String, Object> payload)
	{
		final List<Map<String, Object>> items = checkPayloadGeneric(payload);
		assertEquals(9, items.size());
		final Map<String, Object> firstItem = items.get(0);
		checkRootItem(firstItem, "10", P_CODE_CONF_LAPTOP);

		final Map<String, Object> secondItem = items.get(1);
		checkLineItem(secondItem, "14", "10", getPowerSupplyProductCode());

		final Map<String, Object> thirdItem = items.get(2);
		checkRootItem(thirdItem, "20", CANON_EOS_6D_MARK_II);

		final Map<String, Object> fourthItem = items.get(3);
		checkRootItem(fourthItem, "30", P_CODE_CONF_LAPTOP);

		final Map<String, Object> fifthItem = items.get(4);
		checkLineItem(fifthItem, "34", "30", getPowerSupplyProductCode());

		final Map<String, Object> sixthItem = items.get(5);
		checkRootItem(sixthItem, "40", CANON_EOS_80D);

		final Map<String, Object> seventhItem = items.get(6);
		checkRootItem(seventhItem, "50", NIKON_D850);

		final Map<String, Object> eighthItem = items.get(7);
		checkRootItem(eighthItem, "60", P_CODE_CONF_LAPTOP);

		final Map<String, Object> ninthItem = items.get(8);
		checkLineItem(ninthItem, "64", "60", getPowerSupplyProductCode());
	}

	protected List<Map<String, Object>> checkPayloadGeneric(final Map<String, Object> payload)
	{
		assertNotNull(payload);
		assertNotNull(payload.get(ORDER_ID));
		final List<Map<String, Object>> items = (List<Map<String, Object>>) payload.get("sapCpiOutboundOrderItems");
		assertNotNull(items);
		return items;
	}

	protected void checkLineItem(final Map<String, Object> item, final String entryNumber, final String higherLevelEntryNumber,
			final String productCode)
	{
		assertEquals(productCode, item.get(PRODUCT_CODE));
		assertEquals(entryNumber, item.get(ENTRY_NUMBER));
		assertEquals(higherLevelEntryNumber, item.get(HIGHER_LEVEL_ENTRY_NUMBER));
	}

	protected void checkRootItem(final Map<String, Object> item, final String entryNumber, final String productCode)
	{
		assertEquals(productCode, item.get(PRODUCT_CODE));
		assertEquals(entryNumber, item.get(ENTRY_NUMBER));
		assertNull(item.get(HIGHER_LEVEL_ENTRY_NUMBER));
	}

	protected String getPowerSupplyProductCode()
	{
		return CONF_POWER_SUPPLY_CPQ;
	}

	protected OrderModel createOrderWithOneBundle(final String productCode)
	{
		addBundleToCart(productCode);

		final OrderModel orderModel = placeOrder();
		assertNotNull(orderModel);
		LOG.info("Order code: " + orderModel.getCode());
		assertEquals(1, orderModel.getEntries().size());

		return orderModel;
	}

	protected OrderModel createOrderWithTwoBundles(final String productCode)
	{
		addBundleToCart(productCode);
		addBundleToCart(productCode);

		final OrderModel orderModel = placeOrder();
		assertNotNull(orderModel);
		LOG.info("Order code: " + orderModel.getCode());
		assertEquals(2, orderModel.getEntries().size());

		return orderModel;
	}

	protected OrderModel createMixedOrder(final String productCode)
	{
		addBundleToCart(productCode);
		addToCart(CANON_EOS_6D_MARK_II);
		addBundleToCart(productCode);
		addToCart(CANON_EOS_80D);
		addToCart(NIKON_D850);
		addBundleToCart(productCode);

		final OrderModel orderModel = placeOrder();
		assertNotNull(orderModel);
		LOG.info("Order code: " + orderModel.getCode());
		assertEquals(6, orderModel.getEntries().size());

		return orderModel;
	}

	protected void addBundleToCart(final String productCode)
	{
		final String configId = configurationService.createConfiguration(productCode);
		assertNotNull(configId, productCode);
		updateConfiguration(configId, productCode);
		final ProductModel product = productService.getProductForCode(productCode);
		final CommerceCartParameter parameterAddToCart = new CommerceCartParameter();
		parameterAddToCart.setEnableHooks(true);
		parameterAddToCart.setCart(cartService.getSessionCart());
		parameterAddToCart.setProduct(product);
		parameterAddToCart.setQuantity(1l);
		parameterAddToCart.setUnit(product.getUnit());
		parameterAddToCart.setCreateNewEntry(true);
		parameterAddToCart.setCpqConfigId(configId);
		try
		{
			commerceCartService.addToCart(parameterAddToCart);
		}
		catch (final CommerceCartModificationException e)
		{
			throw new IllegalStateException("Add to cart failed", e);
		}
	}

	protected void addToCart(final String productCode)
	{
		final ProductModel product = productService.getProductForCode(productCode);
		final CommerceCartParameter parameterAddToCart = new CommerceCartParameter();
		parameterAddToCart.setEnableHooks(true);
		parameterAddToCart.setCart(cartService.getSessionCart());
		parameterAddToCart.setProduct(product);
		parameterAddToCart.setQuantity(1l);
		parameterAddToCart.setUnit(product.getUnit());
		parameterAddToCart.setCreateNewEntry(true);
		try
		{
			commerceCartService.addToCart(parameterAddToCart);
		}
		catch (final CommerceCartModificationException e)
		{
			throw new IllegalStateException("Add to cart failed", e);
		}
	}

	protected void updateConfiguration(final String configId, final String productCode)
	{
		if (P_CODE_CONF_LAPTOP.equals(productCode))
		{
			//temporarily change the default configuration to a complete configuration and force a configuration summary cache reload
			final String defaultConfigSummary = Config.getParameter(CPQPRODUCTCONFIGSERVICES_MOCK_CONF_LAPTOP);
			final String completeConfigSummary = Config.getParameter(CPQPRODUCTCONFIGSERVICES_MOCK_CONF_LAPTOP_COMPLETE);
			Config.setParameter(CPQPRODUCTCONFIGSERVICES_MOCK_CONF_LAPTOP, completeConfigSummary);
			cacheAccessService.remove(cacheKeyService.createConfigurationSummaryCacheKey(configId));
			mockConfigurationLifeCycleStrategy.deleteConfiguration(configId);
			configurationService.getConfigurationSummary(configId);
			Config.setParameter(CPQPRODUCTCONFIGSERVICES_MOCK_CONF_LAPTOP, defaultConfigSummary);
		}
	}

	protected OrderModel placeOrder()
	{
		final CartModel sessionCart = cartService.getSessionCart();
		validate(sessionCart);
		sessionCart.setStatus(OrderStatus.CREATED);
		modelService.save(sessionCart);
		final CommerceCheckoutParameter parameter = new CommerceCheckoutParameter();
		parameter.setCart(cartService.getSessionCart());
		parameter.setEnableHooks(true);
		CommerceOrderResult commerceOrderResult;
		try
		{
			commerceOrderResult = placeOrderStrategy.placeOrder(parameter);
		}
		catch (final InvalidCartException e)
		{
			throw new IllegalStateException("Place order failed", e);
		}
		final OrderModel orderModel = commerceOrderResult.getOrder();
		return orderModel;
	}

	protected void validate(final CartModel cart)
	{
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(cart);
		List<CommerceCartModification> cartValidationFailures;
		try
		{
			cartValidationFailures = commerceCartService.validateCart(parameter);
		}
		catch (final CommerceCartModificationException e)
		{
			throw new IllegalStateException("cart could not be validated", e);
		}
		assertNotNull(cartValidationFailures);
		assertEquals(0, cartValidationFailures.size());
	}

	protected void enrichOrder(final OrderModel orderModel)
	{
		//enrich order to make it pass SCPI standard conversion steps
		final LanguageModel languageModel = getFromPersistence("select {pk} from {language} where {isocode}='en'");
		orderModel.setLanguage(languageModel);
		final AddressModel deliveryAddressModel = new AddressModel();
		deliveryAddressModel.setOwner(orderModel);
		orderModel.setDeliveryAddress(deliveryAddressModel);
		orderModel.setPaymentTransactions(Collections.emptyList());
		modelService.save(orderModel);
	}

}
