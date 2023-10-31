/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.facades.integrationtests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import de.hybris.platform.apiregistryservices.exceptions.CredentialException;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.catalog.enums.ProductInfoStatus;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.order.SaveCartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.CommerceSaveCartParameterData;
import de.hybris.platform.commercefacades.order.data.CommerceSaveCartResultData;
import de.hybris.platform.commercefacades.order.data.ConfigurationInfoData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.order.impl.DefaultQuoteFacade;
import de.hybris.platform.commercefacades.quote.data.QuoteData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.order.CommerceSaveCartException;
import de.hybris.platform.commerceservices.order.exceptions.IllegalQuoteStateException;
import de.hybris.platform.commerceservices.order.strategies.QuoteActionValidationStrategy;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.cpq.productconfig.facades.OrderIntegrationFacade;
import de.hybris.platform.cpq.productconfig.facades.QuoteIntegrationFacade;
import de.hybris.platform.cpq.productconfig.facades.SavedCartIntegrationFacade;
import de.hybris.platform.cpq.productconfig.services.data.ConfigurationSummaryData;
import de.hybris.platform.cpq.productconfig.services.integrationtests.config.DefaultIntegrationTestConfiguration;
import de.hybris.platform.cpq.productconfig.services.integrationtests.config.IntegrationTestConfiguration;
import de.hybris.platform.cpq.productconfig.services.model.CloudCPQOrderEntryProductInfoModel;
import de.hybris.platform.cpq.productconfig.services.strategies.impl.DefaultConfigurationCartValidationHook;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.order.model.AbstractOrderEntryProductInfoModel;
import de.hybris.platform.order.strategies.ordercloning.CloneAbstractOrderStrategy;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import com.hybris.charon.exp.NotFoundException;
import com.sap.security.core.server.csi.XSSEncoder;


/**
 * Base test class, there are two subclasses, one for Mock engine and one for real API.
 */
public abstract class AbstractOrderIntegrationTestBase extends BaseIntegrationTest
{
	private static final Logger LOG = Logger.getLogger(AbstractOrderIntegrationTestBase.class);

	protected static final String PRODUCT_NAME_CONF_TECH_PRODUCT = "CONF_TECH_PRODUCT";
	protected static final String PRODUCT_NAME_CONF_CAMERA_BUNDLE = "CONF_CAMERA_BUNDLE";
	protected static final String PRICE_VALUE = "€30.00";
	protected static final String PRICE = "Price";
	protected static final String NAME = "Name";
	protected static final String PRODUCT_NAME_CONF_LAPTOP = P_CODE_CONF_LAPTOP;
	protected static final String PRODUCT_NAME_COFFEE_MACHINE = "CONF_COFFEEMACHINE_3000";
	protected static final String SUB_ENTRY_PRODUCT_NAME = "CONF_POWER_SUPPLY";
	protected static final String SUB_ENTRY_QUANTITY_LABEL = "Quantity";
	protected static final String SUB_ENTRY_QUANTITY = "1.00";
	private static final String MATCH_MSG = "Expected configId's to MATCH, but they DON'T";
	private static final String NOT_MATCH_MSG = "Expected configId's NOT to MATCH, but they DO!";

	protected static final String PRODUCT_NAME_CONF_LAPTOP_COMPLETE = "CONF_LAPTOP_COMPLETE";

	@Resource(name = "b2bCheckoutFacade")
	protected CheckoutFacade checkoutFacade;

	@Resource(name = "baseStoreService")
	protected BaseStoreService baseStoreService;

	@Resource(name = "customerAccountService")
	protected CustomerAccountService customerAccountService;

	@Resource(name = "saveCartFacade")
	protected SaveCartFacade saveCartFacade;

	@Resource(name = "commerceCartService")
	protected CommerceCartService commerceCartService;

	@Resource(name = "defaultQuoteFacade")
	protected DefaultQuoteFacade defaultQuoteFacade;

	@Resource(name = "quoteService")
	protected QuoteService quoteService;

	@Resource(name = "cpqProductConfigSavedCartIntegrationFacade")
	protected SavedCartIntegrationFacade savedCartIntegrationFacade;

	@Resource(name = "cpqProductConfigOrderIntegrationFacade")
	protected OrderIntegrationFacade orderIntegrationFacade;

	@Resource(name = "cpqProductConfigQuoteIntegrationFacade")
	protected QuoteIntegrationFacade quoteIntegrationFacade;

	@Override
	protected IntegrationTestConfiguration createTestConfig()
	{
		final IntegrationTestConfiguration config = new DefaultIntegrationTestConfiguration();
		config.addAdditionalImpex("/cpqproductconfigservices/test/testDataOrderManagement.impex");
		config.addAdditionalImpex("/cpqproductconfigservices/test/testDataQuoteManagement.impex");
		return config;
	}

	//<!-- CAN BE DELETED AFTER PR TO PLATFORM 20.11 HAS BEEN CONSUMED -->

	@Resource(name = "cloneAbstractOrderStrategy")
	private CloneAbstractOrderStrategy currentCloneStrategy;

	@Resource(name = "quoteActionValidationStrategy")
	private QuoteActionValidationStrategy currentQuoteValidationStrategy;

	//<!-- CAN BE DELETED AFTER PR TO PLATFORM 20.11 HAS BEEN CONSUMED -->

	@Test
	public void testAddToCartAfterInteractiveUi() throws CommerceCartModificationException
	{
		final String configId = addConfigurationToCart();

		//Check: Configuration ID has been persisted along with cart entry
		final CartModel cartModel = cartService.getSessionCart();
		assertNotNull("We expect to see a session cart", cartModel);
		final List<AbstractOrderEntryModel> cartEntries = cartModel.getEntries();
		final CloudCPQOrderEntryProductInfoModel cloudCPQOrderEntryProductInfoModel = getConfigurationModelAttachedToFirstAbstractOrderEntry(
				cartEntries);
		assertEquals("Configuration ID must be persisted at cart entry level", configId,
				cloudCPQOrderEntryProductInfoModel.getConfigurationId());
	}

	@Test
	public void testUpdateCart() throws CommerceCartModificationException
	{
		final String configId = addConfigurationToCart();

		//set price for first cart entry to 0
		final AbstractOrderEntryModel abstractOrderEntryModel = getCart().getEntries().get(0);
		assertNotNull(abstractOrderEntryModel);
		abstractOrderEntryModel.setBasePrice(Double.valueOf(0));
		modelService.save(abstractOrderEntryModel);

		//update cart
		cartIntegrationFacade.updateCartEntryFromConfiguration(configId, 0);
		final AbstractOrderEntryModel abstractOrderEntryModelAfterUpdate = getCart().getEntries().get(0);
		assertNotNull(abstractOrderEntryModelAfterUpdate);
		assertEquals("We expect the price to be taken from configuration", Double.valueOf(1080),
				abstractOrderEntryModelAfterUpdate.getBasePrice());

	}

	@Test
	public void testDeleteConfigOnDirectCartEntryDeletion() throws CommerceCartModificationException
	{
		final String configId = addConfigurationToCart();
		final AbstractOrderEntryModel entryModel = cartService.getSessionCart().getEntries().get(0);
		modelService.remove(entryModel);
		checkConfigDeleted(configId);
	}

	@Test
	public void testDeleteConfigOnCartEntryQuantityZero() throws CommerceCartModificationException
	{
		final String configId = addConfigurationToCart();
		cartFacade.updateCartEntry(cartFacade.getSessionCart().getEntries().get(0).getEntryNumber(), 0l);
		checkConfigDeleted(configId);
	}

	@Test
	public void testDeleteConfigOnCartEntryQuantityZeroWithoutCurrentBaseSite() throws CommerceCartModificationException
	{
		assumeFalseAndLog(
				"Test runs into NPE when sap order management is deployed and base site/store is null, as sap order mangement always assumess a base store context",
				isExtensionInSetup("sapordermgmtb2bfacades"), LOG);
		final String configId = addConfigurationToCart();
		final Integer entryNumber = cartFacade.getSessionCart().getEntries().get(0).getEntryNumber();

		final BaseSiteModel siteInCart = cartService.getSessionCart().getSite();
		assertEquals("cart should contain current site as base site", baseSiteService.getCurrentBaseSite().getPk().toString(),
				siteInCart.getPk().toString());

		// unset current base site
		baseSiteService.setCurrentBaseSite("", false);
		assertNull(baseSiteService.getCurrentBaseSite());

		// trigger model remove
		cartFacade.updateCartEntry(entryNumber, 0l);
		assertNull("current base site should not be overriden by remove interceptor", baseSiteService.getCurrentBaseSite());

		// reset current base site
		baseSiteService.setCurrentBaseSite(siteInCart, false);
		checkConfigDeleted(configId);
	}

	@Test
	public void testDarkAddToCart() throws CommerceCartModificationException, CredentialException
	{
		darkAddToCart();
	}

	protected CartModificationData darkAddToCart() throws CommerceCartModificationException
	{
		final CartModificationData cartModificationData = cartFacade.addToCart(PRODUCT_NAME_CONF_LAPTOP, 1);
		final OrderEntryData orderEntryData = cartModificationData.getEntry();

		assertEquals(PRODUCT_NAME_CONF_LAPTOP, orderEntryData.getProduct().getCode());
		final List<ConfigurationInfoData> configurationInfoDataList = orderEntryData.getConfigurationInfos();
		assertNotNull(configurationInfoDataList);
		assertEquals(7, configurationInfoDataList.size());

		final ConfigurationInfoData configurationInfoData = configurationInfoDataList.get(2);

		validateInfoDataForDarkAddToCart(configurationInfoData);

		return cartModificationData;
	}

	protected void validateInfoDataForDarkAddToCart(final ConfigurationInfoData configurationInfoData)
	{
		validateInfoData(configurationInfoData, ProductInfoStatus.ERROR, "LI#0#KEY", "CONF_POWER_SUPPLY");
	}

	@Test
	public void testChangeConfiguration() throws CommerceCartModificationException
	{
		// add a config to cart and check that configId returned on change does not match the added config id
		final String configId = addConfigurationToCart();
		final String configIdToChange = cartIntegrationFacade
				.getConfigIdForSessionCartEntry(cartFacade.getSessionCart().getEntries().get(0).getEntryNumber()).getConfigId();
		assertNotEquals(configId, configIdToChange);

		// modify cached summary and check that is replaced by actual values from config on update cart
		final ConfigurationSummaryData cachedSummary = configurationService.getConfigurationSummary(configId);
		final int numOfLineItems = cachedSummary.getConfiguration().getLineItems().size();
		cachedSummary.getConfiguration().setLineItems(Collections.emptyList());
		cartIntegrationFacade.updateCartEntryFromConfiguration(configIdToChange, 0);
		assertEquals(numOfLineItems,
				configurationService.getConfigurationSummary(configIdToChange).getConfiguration().getLineItems().size());
	}

	@Test
	public void testOrderSubmit()
	{
		checkConfigurationOnLifecycleStep(this::createCart, this::orderSubmit, true);
	}

	@Test
	public void testCreateSavedCart()
	{
		checkConfigurationOnLifecycleStep(this::createCart, this::saveCart, true);
	}

	@Test
	public void testRestoreSavedCart()
	{
		checkConfigurationOnLifecycleStep(this::createCart, this::restoreSavedCart, true);
	}

	@Test
	public void testKeepSavedCartWhenRestoring()
	{
		checkConfigurationOnLifecycleStep(this::createCart, this::cloneSavedCart, false);
	}

	@Test
	public void testInitiateQuote()
	{
		checkConfigurationOnLifecycleStep(this::createCart, this::initiateQuote, true);
	}

	@Test
	public void testInitiateQuoteAndEdit()
	{
		checkConfigurationOnLifecycleStep(this::createCart, this::initiateQuoteAndEdit, false);
	}

	@Test
	public void testAddToCartNoStock() throws CommerceCartModificationException
	{
		final CartModificationData modificationData = cartFacade.addToCart(PRODUCT_NAME_CONF_LAPTOP, 9999L);
		assertEquals(CommerceCartModificationStatus.SUCCESS, modificationData.getStatusCode());
		assertEquals(9999L, modificationData.getQuantityAdded());

		final CartModificationData modificationData2 = cartFacade.addToCart(PRODUCT_NAME_CONF_LAPTOP, 9999L);
		assertEquals(CommerceCartModificationStatus.NO_STOCK, modificationData2.getStatusCode());
		assertEquals(0L, modificationData2.getQuantityAdded());
	}

	@Test
	public void testCartConfigurationStatusInCaseOfIssues() throws CommerceCartModificationException
	{
		addConfigurationToCart();
		final ConfigurationInfoData configurationInfoData = getConfigurationInfoForFirstCartEntry();
		assertEquals("We expect configuration to be incomplete, thus showing error", ProductInfoStatus.ERROR,
				configurationInfoData.getStatus());
	}

	@Test
	public void testAddConfigurationToCartWithQtyThree() throws CommerceCartModificationException
	{
		addConfigurationToCartWithProduct(PRODUCT_NAME_COFFEE_MACHINE, 3L);
		final OrderEntryData cartEntry = getFirstCartEntry();
		assertEquals(Long.valueOf(3L), cartEntry.getQuantity());
	}

	@Test
	public void testCartConfigurationStatusNumberOfIssues() throws CommerceCartModificationException
	{
		addConfigurationToCart();
		final OrderEntryData cartEntry = getFirstCartEntry();
		final Map<ProductInfoStatus, Integer> statusSummaryMap = cartEntry.getStatusSummaryMap();
		assertNotNull(statusSummaryMap);
		assertNull(statusSummaryMap.get(ProductInfoStatus.SUCCESS));
		assertEquals("We expect one issue (due to incompleteness)", Integer.valueOf(1),
				statusSummaryMap.get(ProductInfoStatus.ERROR));
	}

	@Test
	public void testCartConfigurationStatusInCaseNoIssues() throws CommerceCartModificationException, CredentialException
	{
		addConfigurationToCartWithProduct(PRODUCT_NAME_CONF_LAPTOP_COMPLETE, 1L);
		final ConfigurationInfoData configurationInfoData = getConfigurationInfoForFirstCartEntry();
		assertEquals("We expect configuration to be complete and consistent, thus showing success",
				getExpectedStatusForCoffeeMachine(), configurationInfoData.getStatus());
	}

	@Test
	public void testGetConfigurationIdForSavedCartEntry() throws CommerceSaveCartException
	{
		final String configurationId = createCartForProductAndRetrieveConfigurationId(PRODUCT_NAME_CONF_LAPTOP);
		final AbstractOrderModel savedCart = saveCart();
		assertEquals("Expected Configuration ID which was retrieved during create configuration", configurationId,
				savedCartIntegrationFacade.getConfigurationId(savedCart.getCode(), 0));
	}

	@Test
	public void testGetProductCodeForSavedCartEntry() throws CommerceSaveCartException
	{
		createCartForProductAndRetrieveConfigurationId(PRODUCT_NAME_CONF_LAPTOP);
		final AbstractOrderModel savedCart = saveCart();
		assertEquals("Expected Product Code which was used during create configuration", PRODUCT_NAME_CONF_LAPTOP,
				savedCartIntegrationFacade.getProductCode(savedCart.getCode(), 0));
	}

	@Test
	public void testGetConfigurationIdForOrderEntry() throws CommerceSaveCartException
	{
		final String configurationId = createCartForProductAndRetrieveConfigurationId(PRODUCT_NAME_CONF_LAPTOP);
		final AbstractOrderModel order = orderSubmit();
		assertEquals("Expected Configuration ID which was retrieved during create configuration", configurationId,
				orderIntegrationFacade.getConfigurationId(order.getCode(), 10).getConfigId());
	}

	@Test
	public void testGetProductCodeForOrderEntry()
	{
		createCartForProductAndRetrieveConfigurationId(PRODUCT_NAME_CONF_LAPTOP);
		final AbstractOrderModel order = orderSubmit();
		assertEquals("Expected Product Code which was used during create configuration", PRODUCT_NAME_CONF_LAPTOP,
				orderIntegrationFacade.getProductCode(order.getCode(), 10));
	}

	@Test
	public void testGetConfigurationIdForQuoteEntry() throws CommerceSaveCartException
	{
		final String configurationId = createCartForProductAndRetrieveConfigurationId(PRODUCT_NAME_CONF_LAPTOP);
		final AbstractOrderModel quote = initiateQuote();
		assertEquals("Expected Configuration ID which was retrieved during create configuration", configurationId,
				quoteIntegrationFacade.getConfigurationId(quote.getCode(), 10));
	}

	@Test
	public void testGetProductCodeForQuoteEntry()
	{
		createCartForProductAndRetrieveConfigurationId(PRODUCT_NAME_CONF_LAPTOP);
		final AbstractOrderModel quote = initiateQuote();
		assertEquals("Expected Product Code which was used during create configuration", PRODUCT_NAME_CONF_LAPTOP,
				quoteIntegrationFacade.getProductCode(quote.getCode(), 10));
	}

	protected ProductInfoStatus getExpectedStatusForCoffeeMachine()
	{
		return ProductInfoStatus.SUCCESS;
	}

	protected ConfigurationInfoData getConfigurationInfoForFirstCartEntry()
	{
		final OrderEntryData entryData = getFirstCartEntry();
		final List<ConfigurationInfoData> configurationInfos = entryData.getConfigurationInfos();
		assertNotNull(configurationInfos);
		assertEquals(7, configurationInfos.size());
		final ConfigurationInfoData configurationInfoData = configurationInfos.get(2);
		return configurationInfoData;
	}

	protected OrderEntryData getFirstCartEntry()
	{
		final CartData sessionCart = cartFacade.getSessionCart();
		final List<OrderEntryData> entries = sessionCart.getEntries();
		assertNotNull(entries);
		assertEquals(1, entries.size());
		final OrderEntryData entryData = entries.get(0);
		return entryData;
	}

	protected void checkConfigDeleted(final String configId)
	{
		try
		{
			configurationService.getConfigurationSummary(configId);
			fail("Configuration should be deleted.");
		}
		catch (final NotFoundException e)
		{
			// expected
		}
	}

	protected String addConfigurationToCart() throws CommerceCartModificationException
	{
		return addConfigurationToCartWithProduct(PRODUCT_NAME_CONF_LAPTOP, 1L);
	}

	protected String addConfigurationToCartWithProduct(final String productCode, final long quantity)
			throws CommerceCartModificationException
	{
		final String configId = configurationService.createConfiguration(productCode);
		assertNotNull("We expect a configuration ID to be available after we created a default configuration", configId);
		final CartModificationData modData = cartIntegrationFacade.addConfigurationToCart(configId, quantity);
		assertNotNull("After addToCart, we expect to see the entry number of the new cart entry",
				modData.getEntry().getEntryNumber());
		return configId;
	}

	protected AbstractOrderModel createCart()
	{
		try
		{
			addConfigurationToCart();
		}
		catch (final CommerceCartModificationException ex)
		{
			throw new AssertionError("create cart failed with an exception", ex);
		}
		return cartService.getSessionCart();
	}

	protected AbstractOrderModel orderSubmit()
	{
		OrderData order;
		try
		{
			order = checkoutFacade.placeOrder();
			assertNotNull("We expect to see an order", order);
			final BaseStoreModel store = baseStoreService.getCurrentBaseStore();

			return customerAccountService.getOrderForCode(order.getCode(), store);

		}
		catch (final InvalidCartException ex)
		{
			throw new AssertionError("order submit failed with an exception", ex);
		}
	}

	protected AbstractOrderModel saveCart()
	{
		modelService.save(cartService.getSessionCart());
		return commerceCartService.getCartForCodeAndUser(cartService.getSessionCart().getCode(), userService.getCurrentUser());
	}

	protected void checkConfigurationOnLifecycleStep(final Supplier<AbstractOrderModel> initialLifecycleStep,
			final Supplier<AbstractOrderModel> lifecycleTransition, final boolean expectedMatch)
	{
		final AbstractOrderModel initialAbstractOrder = initialLifecycleStep.get();
		final CloudCPQOrderEntryProductInfoModel productInfoModel = getConfigurationModelAttachedToFirstAbstractOrderEntry(
				initialAbstractOrder.getEntries());

		final AbstractOrderModel abstractOrderAfterTransition = lifecycleTransition.get();
		final CloudCPQOrderEntryProductInfoModel configurationModelAttachedToFirstCartEntry = getConfigurationModelAttachedToFirstAbstractOrderEntry(
				abstractOrderAfterTransition.getEntries());
		final String configIdAfterTransition = configurationModelAttachedToFirstCartEntry.getConfigurationId();

		assertEquals(expectedMatch ? MATCH_MSG : NOT_MATCH_MSG, expectedMatch,
				productInfoModel.getConfigurationId().equals(configIdAfterTransition));
	}

	protected CloudCPQOrderEntryProductInfoModel getConfigurationModelAttachedToFirstAbstractOrderEntry(
			final List<AbstractOrderEntryModel> cartEntries)
	{
		assertEquals("We expect to see one cart entry", 1, cartEntries.size());
		final AbstractOrderEntryModel entryModel = cartEntries.get(0);
		final List<AbstractOrderEntryProductInfoModel> productInfos = entryModel.getProductInfos();
		assertEquals("We expect one instance of product infos related to the cart entry", 1, productInfos.size());
		final AbstractOrderEntryProductInfoModel productInfoModel = productInfos.get(0);
		assertTrue("Product info must be of CPQ specific type", productInfoModel instanceof CloudCPQOrderEntryProductInfoModel);
		return (CloudCPQOrderEntryProductInfoModel) productInfoModel;
	}


	protected List<OrderEntryData> getCartEntries()
	{
		//Check: Configuration summary is available
		final CartData cartData = cartFacade.getSessionCart();
		assertNotNull("We expect to see a session cart", cartData);
		return cartData.getEntries();
	}

	protected AbstractOrderModel getCart()
	{
		final CartModel sessionCart = cartService.getSessionCart();
		assertNotNull(sessionCart);
		return sessionCart;
	}

	protected AbstractOrderModel restoreSavedCart()
	{
		modelService.save(cartService.getSessionCart());
		final CommerceSaveCartParameterData parameters = new CommerceSaveCartParameterData();
		parameters.setCartId(cartService.getSessionCart().getCode());
		try
		{
			saveCartFacade.restoreSavedCart(parameters);
		}
		catch (final CommerceSaveCartException ex)
		{
			throw new AssertionError("restore saved cart failed with an exception", ex);
		}
		return cartService.getSessionCart();
	}

	protected AbstractOrderModel cloneSavedCart()
	{
		final CartModel sessionCart = cartService.getSessionCart();
		final String code = sessionCart.getCode();
		sessionCart.setSaveTime(Date.from(Instant.now()));

		modelService.save(sessionCart);

		final CommerceSaveCartParameterData parameters = new CommerceSaveCartParameterData();
		parameters.setCartId(code);
		try
		{
			final CommerceSaveCartResultData cloneSavedCart = saveCartFacade.cloneSavedCart(parameters);
			return commerceCartService.getCartForCodeAndUser(cloneSavedCart.getSavedCartData().getCode(),
					userService.getCurrentUser());
		}
		catch (final CommerceSaveCartException ex)
		{
			throw new AssertionError("clone saved cart failed with an exception", ex);
		}
	}

	protected AbstractOrderModel initiateQuote()
	{
		final QuoteData quoteData = defaultQuoteFacade.initiateQuote();
		return quoteService.getCurrentQuoteForCode(quoteData.getCode());
	}

	protected AbstractOrderModel initiateQuoteAndEdit()
	{
		final QuoteData quoteData = defaultQuoteFacade.initiateQuote();
		defaultQuoteFacade.enableQuoteEdit(quoteData.getCode());
		return cartService.getSessionCart();
	}

	protected String createCartForProductAndRetrieveConfigurationId(final String productCode)
	{
		String configurationId = null;
		try
		{
			configurationId = addConfigurationToCartWithProduct(productCode, 1L);
		}
		catch (final CommerceCartModificationException ex)
		{
			throw new AssertionError("createCartForProductAndRetrieveConfigurationId failed with an exception", ex);
		}
		return configurationId;
	}

	@Test
	public void testCartValidationDefaultConfigHasIssues() throws CommerceCartModificationException
	{
		addConfigurationToCartWithProduct(getProductWithIncompleteDefaultConfiguration(), 1L);
		final List<CartModificationData> validateCartData = cartFacade.validateCartData();
		assertNotNull(validateCartData);
		assertEquals(1, validateCartData.size());
		final CartModificationData cartModificationData = validateCartData.get(0);
		assertEquals(DefaultConfigurationCartValidationHook.REVIEW_CONFIGURATION, cartModificationData.getStatusCode());
	}

	@Test
	public void testCartValidationNoIssues() throws CommerceCartModificationException
	{
		addConfigurationToCartWithProduct(getProductWithCompleteDefaultConfiguration(), 1L);
		final List<CartModificationData> validateCartData = cartFacade.validateCartData();
		assertNotNull(validateCartData);
		assertEquals(0, validateCartData.size());

	}

	@Test
	public void testQuoteCheckoutConfigHasIssues() throws CommerceCartModificationException, UnsupportedEncodingException
	{
		addConfigurationToCartWithProduct(getProductWithIncompleteDefaultConfiguration(), 1L);
		final QuoteModel quote = (QuoteModel) initiateQuote();
		quote.setExpirationTime(new Date(System.currentTimeMillis() + ONE_WEEK));
		quote.setState(QuoteState.BUYER_OFFER);
		modelService.save(quote);
		final String code = quote.getCode();

		try
		{
			defaultQuoteFacade.acceptAndPrepareCheckout(code);
			Assert.fail();
		}
		catch (final IllegalQuoteStateException ex)
		{
			final String execptionMessage = XSSEncoder.encodeHTML("There are issues with the configuration of product");
			assertTrue("Configuration related message expected but was " + ex.getLocalizedMessage(),
					ex.getLocalizedMessage().contains(execptionMessage));
		}
	}

	private static final long ONE_WEEK = 1000 * 86400 * 7;

	@Test
	public void testQuoteCheckoutConfigNoIssues() throws CommerceCartModificationException
	{
		addConfigurationToCartWithProduct(getProductWithCompleteDefaultConfiguration(), 1L);
		final AbstractOrderModel quote = initiateQuote();
		quote.setExpirationTime(new Date(System.currentTimeMillis() + ONE_WEEK));
		final String code = quote.getCode();
		
		try
		{
			defaultQuoteFacade.acceptAndPrepareCheckout(code);
			Assert.fail();
		}
		catch (final IllegalQuoteStateException ex)
		{
			assertTrue("Standard message expected but was " + ex.getLocalizedMessage(),
					ex.getLocalizedMessage().contains("Action [CHECKOUT] is not allowed for quote"));
		}

	}

	public String getProductWithIncompleteDefaultConfiguration()
	{
		return PRODUCT_NAME_CONF_LAPTOP;
	}

	public String getProductWithCompleteDefaultConfiguration()
	{
		return PRODUCT_NAME_COFFEE_MACHINE;
	}

	protected CartModificationData addConfigurationToCartWithProductRetrivingCartModification(final String productCode,
			final long quantity) throws CommerceCartModificationException
	{
		final String configId = configurationService.createConfiguration(productCode);
		assertNotNull("We expect a configuration ID to be available after we created a default configuration", configId);
		final CartModificationData modData = cartIntegrationFacade.addConfigurationToCart(configId, quantity);
		assertNotNull("After addToCart, we expect to see the entry number of the new cart entry",
				modData.getEntry().getEntryNumber());
		return modData;
	}

	@Test
	public void testAddConfigurationWithMultipleLineItemsToCart() throws CommerceCartModificationException
	{
		final CartModificationData cartModificationData = addConfigurationToCartWithProductRetrivingCartModification(
				PRODUCT_NAME_CONF_CAMERA_BUNDLE, 1L);
		final List<ConfigurationInfoData> infoDataList = cartModificationData.getEntry().getConfigurationInfos();
		validateInfoDataList(infoDataList);
	}

	protected abstract void validateInfoDataList(final List<ConfigurationInfoData> infoDataList);

	protected void validateInfoData(final ConfigurationInfoData configurationInfoData,
			final ProductInfoStatus expectedStatus, final String expectedLabel,
			final String expectedValue)
	{
		assertEquals(ConfiguratorType.CLOUDCPQCONFIGURATOR, configurationInfoData.getConfiguratorType());
		assertEquals(expectedStatus, configurationInfoData.getStatus());
		assertEquals(expectedLabel, configurationInfoData.getConfigurationLabel());
		assertEquals(expectedValue, configurationInfoData.getConfigurationValue());
	};

	protected void validateInfoDataSuccess(final ConfigurationInfoData configurationInfoData, final String expectedLabel,
			final String expectedValue)
	{
		validateInfoData(configurationInfoData, ProductInfoStatus.SUCCESS, expectedLabel, expectedValue);
	}


}