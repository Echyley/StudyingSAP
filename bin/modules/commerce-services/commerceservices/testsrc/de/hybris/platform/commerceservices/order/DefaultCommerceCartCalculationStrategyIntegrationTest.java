/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.order;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commerceservices.constants.CommerceServicesConstants;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceCartCalculationStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.DeliveryModeService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.promotions.PromotionsService;
import de.hybris.platform.promotions.impl.DefaultPromotionsService;
import de.hybris.platform.promotions.jalo.PromotionsManager;
import de.hybris.platform.promotions.model.PromotionGroupModel;
import de.hybris.platform.promotions.result.PromotionOrderResults;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.AddressService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.util.DiscountValue;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Integration test suite(product discount and delivery cost threshold settings ) for {@link DefaultCommerceCartCalculationStrategy}
 */
@IntegrationTest
public class DefaultCommerceCartCalculationStrategyIntegrationTest extends ServicelayerTest
{
	private static final Logger LOG = Logger.getLogger(DefaultCommerceCartCalculationStrategyIntegrationTest.class);
	private static final String TEST_BASESITE_UID = "testSite";

	private static final double RESET_TOTAL_PRICE = 548.39d;
	private static final double RESET_DELIVERY_COST = 14.99d;
	private static final double RESET_TOTAL_TAX = 49.85d;
	private static final double RESET_SUB_TOTAL = 533.4d;

	private static final double NO_RESET_TOTAL_PRICE = 542.39d;
	private static final double NO_RESET_DELIVERY_COST = 8.99d;
	private static final double NO_RESET_TOTAL_TAX = 49.31d;

	@Resource
	private DefaultCommerceCartCalculationStrategy defaultCommerceCartCalculationStrategy;

	@Resource
	private UserService userService;

	@Resource
	private PromotionsService promotionsService;

	@Resource
	private ConfigurationService configurationService;

	@Resource
	private AddressService addressService;

	@Resource
	private CalculationService calculationService;

	@Resource
	private CartService cartService;

	@Resource
	private BaseSiteService baseSiteService;

	@Resource
	private DeliveryModeService deliveryModeService;

	@Resource
	private ModelService modelService;

	private DeliveryModeModel standardGrossDeliveryMode;
	private PromotionsService mockPromotionsService;
	private UserModel testCustomer;
	private Collection<CartModel> cartModels;
	private CartModel targetCart;
	private CommerceCartParameter parameter;

	@Before
	public void setUp() throws Exception
	{
		// importing test csv
		importCsv("/commerceservices/test/testCommerceReCalculateCart.csv", "utf-8");
		standardGrossDeliveryMode = deliveryModeService.getDeliveryModeForCode("standard-gross");
		baseSiteService.setCurrentBaseSite(baseSiteService.getBaseSiteForUID(TEST_BASESITE_UID), false);
		mockPromotionsService = new MockPromotionService();
		defaultCommerceCartCalculationStrategy.setPromotionsService(mockPromotionsService);
	}

	@After
	public void reset() throws Exception
	{
		defaultCommerceCartCalculationStrategy.setPromotionsService(promotionsService);
		configurationService.getConfiguration()
				.setProperty(CommerceServicesConstants.CART_CALCULATION_RESET_DELIVERY_COST_ENABLED, Boolean.FALSE.toString());
	}

	@Test
	public void shouldNotChangeOrderFeesWhenCallCalculateCartAgain() throws CalculationException
	{
		prepareAndCalculateCartWithDeliveryCostWorking();

		defaultCommerceCartCalculationStrategy.calculateCart(parameter);
		modelService.refresh(targetCart);

		Assert.assertEquals(Boolean.TRUE, targetCart.getCalculated());
		Assert.assertEquals(RESET_TOTAL_PRICE, targetCart.getTotalPrice(), 0.0d);
		Assert.assertEquals(RESET_DELIVERY_COST, targetCart.getDeliveryCost(), 0.0d);
		Assert.assertEquals(RESET_TOTAL_TAX, targetCart.getTotalTax(), 0.0d);
		Assert.assertEquals(RESET_SUB_TOTAL, targetCart.getSubtotal(), 0.0d);
	}

	@Test
	public void shouldGetNoReSetOrderFeesWhenCallCalculateCartAgainAfterReAddToCart() throws CalculationException
	{
		prepareAndCalculateCartWithDeliveryCostWorking();

		reAddProductToCartWithDeliveryModeInCart(parameter.getCart());
		defaultCommerceCartCalculationStrategy.calculateCart(parameter);
		modelService.refresh(targetCart);

		Assert.assertEquals(Boolean.TRUE, targetCart.getCalculated());
		Assert.assertEquals(NO_RESET_TOTAL_PRICE, targetCart.getTotalPrice(), 0.0d);
		Assert.assertEquals(NO_RESET_DELIVERY_COST, targetCart.getDeliveryCost(), 0.0d);
		Assert.assertEquals(NO_RESET_TOTAL_TAX, targetCart.getTotalTax(), 0.0d);
		Assert.assertEquals(RESET_SUB_TOTAL, targetCart.getSubtotal(), 0.0d);
	}

	@Test
	public void shouldGetReSetOrderFeesWhenCallCalculateCartAgainWithEnableReSetDeliveryCostAfterReAddToCart() throws CalculationException
	{
		prepareAndCalculateCartWithDeliveryCostWorking();

		reAddProductToCartWithDeliveryModeInCart(parameter.getCart());
		configurationService.getConfiguration()
				.setProperty(CommerceServicesConstants.CART_CALCULATION_RESET_DELIVERY_COST_ENABLED, Boolean.TRUE.toString());
		defaultCommerceCartCalculationStrategy.setConfigurationService(configurationService);
		defaultCommerceCartCalculationStrategy.calculateCart(parameter);
		modelService.refresh(targetCart);

		Assert.assertEquals(Boolean.TRUE, targetCart.getCalculated());
		Assert.assertEquals(RESET_TOTAL_PRICE, targetCart.getTotalPrice(), 0.0d);
		Assert.assertEquals(RESET_DELIVERY_COST, targetCart.getDeliveryCost(), 0.0d);
		Assert.assertEquals(RESET_TOTAL_TAX, targetCart.getTotalTax(), 0.0d);
		Assert.assertEquals(RESET_SUB_TOTAL, targetCart.getSubtotal(), 0.0d);
	}

	@Test
	public void shouldGetNoReSetOrderFeesWhenCallRecalculateCartInOccPlaceOrderApiWithOutEnableReSetDeliveryCost()
			throws CalculationException
	{
		prepareAndCalculateCartWithDeliveryCostWorking();

		defaultCommerceCartCalculationStrategy.recalculateCart(parameter);
		modelService.refresh(targetCart);

		Assert.assertEquals(Boolean.TRUE, targetCart.getCalculated());
		Assert.assertEquals(NO_RESET_TOTAL_PRICE, targetCart.getTotalPrice(), 0.0d);
		Assert.assertEquals(NO_RESET_DELIVERY_COST, targetCart.getDeliveryCost(), 0.0d);
		Assert.assertEquals(NO_RESET_TOTAL_TAX, targetCart.getTotalTax(), 0.0d);
		Assert.assertEquals(RESET_SUB_TOTAL, targetCart.getSubtotal(), 0.0d);
	}

	@Test
	public void shouldGetReSetOrderFeesWhenCallRecalculateCartInOccPlaceOrderApiWithEnableReSetDeliveryCost()
			throws CalculationException
	{
		prepareAndCalculateCartWithDeliveryCostWorking();

		configurationService.getConfiguration()
				.setProperty(CommerceServicesConstants.CART_CALCULATION_RESET_DELIVERY_COST_ENABLED, Boolean.TRUE.toString());
		defaultCommerceCartCalculationStrategy.setConfigurationService(configurationService);
		defaultCommerceCartCalculationStrategy.recalculateCart(parameter);
		modelService.refresh(targetCart);

		Assert.assertEquals(Boolean.TRUE, targetCart.getCalculated());
		Assert.assertEquals(RESET_TOTAL_PRICE, targetCart.getTotalPrice(), 0.0d);
		Assert.assertEquals(RESET_DELIVERY_COST, targetCart.getDeliveryCost(), 0.0d);
		Assert.assertEquals(RESET_TOTAL_TAX, targetCart.getTotalTax(), 0.0d);
		Assert.assertEquals(RESET_SUB_TOTAL, targetCart.getSubtotal(), 0.0d);
	}

	/**
	 * here simulate the process of re-add products to Cart with already setting delivery mode in cart, by adjust the order entry quantity with the entry's initial
	 * quantity, resetting entry's quantity with same quantity can trigger cartModel.setCalculated(false). or we can just cartModel.setCalculated(false) directly.
	 * for order entry,setCalulacted(false) + modelService.save(entry) can not trigger cartModel.setCalculated(false)
	 * @param cartModel
	 */
	protected void reAddProductToCartWithDeliveryModeInCart(final CartModel cartModel)
	{
		//choose the discounted order entry(product)
		final AbstractOrderEntryModel discountedEntry = cartModel.getEntries().get(0);
		final long entryQuantity = discountedEntry.getQuantity();
		//set the same entry's quantity to simulate the re add products to cart.
		discountedEntry.setQuantity(entryQuantity);
		modelService.save(discountedEntry);
	}

	/**
	 * the OriginalCart was loaded from test impex data, the cart has already contained 2 products
	 * and every product has its' price, tax.
	 * and also simulate the process of addToCart and checkout
	 */
	protected void prepareAndCalculateCartWithDeliveryCostWorking()
	{
		LOG.info("Creating data for commerce cart ..");
		testCustomer = userService.getUserForUID("abrodetest");
		cartModels = testCustomer.getCarts();
		targetCart = cartModels.iterator().next();

		final Collection<AddressModel> addressList = addressService.getAddressesForOwner(testCustomer);
		parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(targetCart);

		Assert.assertEquals(1, cartModels.size());
		//simulate the process of addToCart,will trigger calculateCart
		defaultCommerceCartCalculationStrategy.calculateCart(parameter);
		//simulate the process of checkout,set the delivery mode
		targetCart.setDeliveryAddress(addressList.stream().toList().get(0));
		targetCart.setDeliveryMode(standardGrossDeliveryMode);
		modelService.save(targetCart);
		defaultCommerceCartCalculationStrategy.calculateCart(parameter);
		//Delivery cost is accidentally correct here, because cart entries were already calculated and
		// promotions were already applied when delivery mode and address was added.
		modelService.refresh(targetCart);

		Assert.assertEquals(Boolean.TRUE, targetCart.getCalculated());
		Assert.assertEquals(RESET_TOTAL_PRICE, targetCart.getTotalPrice(), 0.0d);
		Assert.assertEquals(RESET_DELIVERY_COST, targetCart.getDeliveryCost(), 0.0d);
		Assert.assertEquals(RESET_TOTAL_TAX, targetCart.getTotalTax(), 0.0d);
		Assert.assertEquals(RESET_SUB_TOTAL, targetCart.getSubtotal(), 0.0d);
	}

	private class MockPromotionService extends DefaultPromotionsService
	{
		private static final Logger LOG = Logger.getLogger(
				DefaultCommerceCartCalculationStrategyIntegrationTest.MockPromotionService.class);

		@Override
		public PromotionOrderResults updatePromotions(final Collection<PromotionGroupModel> promotionGroups,
				final AbstractOrderModel cartModel, final boolean evaluateRestrictions,
				final PromotionsManager.AutoApplyMode productPromotionMode, final PromotionsManager.AutoApplyMode orderPromotionMode,
				final Date date)
		{
			// mannually creating discount of updatepromotions did(in test cases env, no promotion context loaded,will be failed when calling getPromotionsService().updatePromotions, so discount will be created mannually)
			final DiscountValue discountValue = new DiscountValue(UUID.randomUUID().toString(), 50.0, true, "USD");

			final AbstractOrderEntryModel entryOne = cartModel.getEntries().get(0);

			final List<DiscountValue> entryDiscounts = new ArrayList<>();
			entryDiscounts.add(discountValue);
			entryOne.setDiscountValues(entryDiscounts);
			//mock the process of recall calculateTotals after apply discount in updatepromotions
			try
			{
				calculationService.calculateTotals(cartModel, true);
			}
			catch (CalculationException e)
			{
				LOG.error(" error happens when calculateTotals " + e.getMessage(), e);
			}

			return null;
		}

	}
}
