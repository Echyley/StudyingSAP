/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services.integrationtests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeFalse;
import static org.junit.Assume.assumeTrue;

import de.hybris.bootstrap.config.ConfigUtil;
import de.hybris.bootstrap.config.ExtensionInfo;
import de.hybris.bootstrap.config.PlatformConfig;
import de.hybris.platform.commerceservices.event.AbstractCommerceUserEvent;
import de.hybris.platform.commerceservices.event.LoginSuccessEvent;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.commerceservices.storesession.StoreSessionService;
import de.hybris.platform.commerceservices.strategies.CartCleanStrategy;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.cpq.productconfig.services.BusinessContextService;
import de.hybris.platform.cpq.productconfig.services.ConfigurationService;
import de.hybris.platform.cpq.productconfig.services.client.CpqClientUtil;
import de.hybris.platform.cpq.productconfig.services.hooks.SwitchableEntryNumberHook;
import de.hybris.platform.cpq.productconfig.services.impl.DefaultEngineDeterminationService;
import de.hybris.platform.cpq.productconfig.services.integrationtests.config.DefaultIntegrationTestConfiguration;
import de.hybris.platform.cpq.productconfig.services.integrationtests.config.IntegrationTestConfiguration;
import de.hybris.platform.cpq.productconfig.services.model.CloudCPQOrderEntryProductInfoModel;
import de.hybris.platform.cpq.productconfig.services.strategies.CPQInteractionStrategy;
import de.hybris.platform.cpq.productconfig.services.strategies.impl.mock.MockConfigurationLifecycleStrategy;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.order.strategies.calculation.FindPriceStrategy;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.security.auth.AuthenticationService;
import de.hybris.platform.servicelayer.security.auth.InvalidCredentialsException;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.util.Utilities;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;


public abstract class BaseIntegrationServiceLayerTest extends ServicelayerTest
{
	protected static final String PDT_CALCULATION_MODE = "pdt.calculation.mode";
	protected static final String PDT_CALCULATION_MODE_SERVICE_LAYER = "sl";
	protected static String STRICT_UUID_PATTERN = "\\p{XDigit}{8}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{12}";
	protected static final String UTF_8 = "utf-8";
	private static final Logger LOG = Logger.getLogger(BaseIntegrationServiceLayerTest.class);

	protected static final String P_CODE_CONF_LAPTOP = "CONF_LAPTOP";

	protected static String pdtCalculationMode = "";

	@Resource(name = "baseSiteService")
	protected BaseSiteService baseSiteService;

	@Resource(name = "baseStoreService")
	protected BaseStoreService baseStoreService;

	@Resource(name = "authenticationService")
	protected AuthenticationService authenticationService;

	@Resource(name = "i18NService")
	protected I18NService i18NService;

	@Resource(name = "commonI18NService")
	protected CommonI18NService commonI18NService;

	@Resource(name = "commerceCommonI18NService")
	protected CommerceCommonI18NService commerceCommonI18NService;

	@Resource(name = "cpqProductConfigConfigurationService")
	protected ConfigurationService configurationService;

	@Resource(name = "modelService")
	protected ModelService modelService;

	@Resource(name = "cartService")
	protected CartService cartService;

	@Resource(name = "commerceCartService")
	protected CommerceCartService commerceCartService;

	@Resource(name = "cartValidationStrategy")
	protected CartCleanStrategy cartCleanStrategy;

	@Resource(name = "eventService")
	protected EventService eventService;

	@Resource(name = "userService")
	protected UserService userService;

	@Resource(name = "storeSessionService")
	protected StoreSessionService storeSessionService;

	@Resource(name = "cpqProductConfigEngineDeterminationService")
	protected DefaultEngineDeterminationService engineDeterminationService;

	@Resource(name = "flexibleSearchService")
	protected FlexibleSearchService flexibleSearchService;

	@Resource(name = "cpqProductConfigCpqClientUtil")
	protected CpqClientUtil clientUtil;

	@Resource(name = "cpqProductConfigCPQInteractionStrategy")
	protected CPQInteractionStrategy cpqInteractionStrategy;

	@Resource(name = "slFindPriceStrategy")
	private FindPriceStrategy slFindPriceStrategy;

	@Resource(name = "findPriceStrategy")
	private FindPriceStrategy findPriceStrategy;

	@Resource(name = "cpqProductConfigCloneAbstractOrderHookForTest")
	protected SwitchableEntryNumberHook entryNumberSpacingHook;

	@Resource(name = "cpqProductConfigBusinessContextService")
	protected BusinessContextService businessContextService;

	protected IntegrationTestConfiguration testConfig;

	@BeforeClass
	public static void adjustEnvironment()
	{
		pdtCalculationMode = Registry.getCurrentTenant().getConfig().getParameter(PDT_CALCULATION_MODE);
		Registry.getCurrentTenant().getConfig().setParameter(PDT_CALCULATION_MODE, PDT_CALCULATION_MODE_SERVICE_LAYER);
	}

	@AfterClass
	public static void cleanUpEnvironment()
	{
		Registry.getCurrentTenant().getConfig().setParameter(PDT_CALCULATION_MODE, pdtCalculationMode);
	}

	@Before
	public void setUp() throws Exception
	{
		testConfig = createTestConfig();
		chooseEngine();
		initializeTestData();
		initShop();
		login();
		checkFindPricingStrategy();
		enableOrderEntrySpacing();
	}


	public void enableOrderEntrySpacing()
	{
		entryNumberSpacingHook.activateEntryNumberSpacing();
	}

	public void disableOrderEntrySpacing()
	{
		entryNumberSpacingHook.deActivateEntryNumberSpacing();

	}

	protected void checkFindPricingStrategy()
	{
		LOG.info("slFindPriceStrategy=" + slFindPriceStrategy.getClass().getName());
		LOG.info("findPriceStrategy=" + findPriceStrategy.getClass().getName());
	}

	protected IntegrationTestConfiguration createTestConfig()
	{
		return new DefaultIntegrationTestConfiguration();
	}

	protected void login() throws InvalidCredentialsException
	{
		final LoginData loginData = testConfig.getLoginData();
		if (loginData.executeLogin)
		{
			login(loginData.userName, loginData.password);
		}
	}

	public void login(final String userName, final String password) throws InvalidCredentialsException
	{
		authenticationService.login(userName, password);
		loginSuccessServiceLayer();
	}

	public Collection<CurrencyModel> getAllCurrencies()
	{
		List<CurrencyModel> currencyModelList = commerceCommonI18NService.getAllCurrencies();
		if (currencyModelList.isEmpty())
		{
			currencyModelList = commonI18NService.getAllCurrencies();
		}

		return currencyModelList;
	}

	protected boolean updateSessionCurrency(final CurrencyModel preferredCurrency, final CurrencyModel defaultCurrency)
	{
		if (preferredCurrency != null)
		{
			final String currencyIsoCode = preferredCurrency.getIsocode();

			// Get the available currencies and check if the currency iso code is supported
			final Collection<CurrencyModel> currencies = getAllCurrencies();
			for (final CurrencyModel currency : currencies)
			{
				if (StringUtils.equals(currency.getIsocode(), currencyIsoCode))
				{
					// Set the current currency
					storeSessionService.setCurrentCurrency(currencyIsoCode);
					return true;
				}
			}
		}
		// Fallback to the default
		storeSessionService.setCurrentCurrency(defaultCurrency.getIsocode());
		return false;
	}

	protected void loginSuccessServiceLayer()
	{
		final UserModel currentUser = userService.getCurrentUser();

		// First thing to do is to try to change the user on the session cart
		if (cartService.hasSessionCart())
		{
			cartService.changeCurrentCartUser(currentUser);
		}

		// Update the session currency (which might change the cart currency)
		if (!updateSessionCurrency(currentUser.getSessionCurrency(), commerceCommonI18NService.getDefaultCurrency()))
		{
			// Update the user
			currentUser.setSessionCurrency(commonI18NService.getCurrentCurrency());
			modelService.save(currentUser);
		}

		// Update the user
		currentUser.setSessionLanguage(commonI18NService.getCurrentLanguage());
		modelService.save(currentUser);

		// Calculate the cart after setting everything up
		if (cartService.hasSessionCart())
		{
			final CartModel sessionCart = cartService.getSessionCart();

			// Clean the existing info on the cart if it does not belong to the current user
			cartCleanStrategy.cleanCart(sessionCart);
			try
			{
				final CommerceCartParameter parameter = new CommerceCartParameter();
				parameter.setEnableHooks(true);
				parameter.setCart(sessionCart);
				commerceCartService.recalculateCart(parameter);
			}
			catch (final CalculationException ex)
			{
				LOG.error("Failed to recalculate order [" + sessionCart.getCode() + "]", ex);
			}
		}
		eventService.publishEvent(initializeCommerceEvent(new LoginSuccessEvent(), (CustomerModel) currentUser));
	}

	protected AbstractCommerceUserEvent initializeCommerceEvent(final AbstractCommerceUserEvent event,
			final CustomerModel customerModel)
	{
		event.setBaseStore(baseStoreService.getCurrentBaseStore());
		event.setSite(baseSiteService.getCurrentBaseSite());
		event.setCustomer(customerModel);
		event.setLanguage(commonI18NService.getCurrentLanguage());
		event.setCurrency(commonI18NService.getCurrentCurrency());
		return event;
	}

	protected void initializeTestData() throws Exception
	{
		createCoreData();
		for (final String impex : testConfig.getImpexToImport())
		{
			importCsv(impex, UTF_8);
		}
		if (isExtensionInSetup("promotionengineservices"))
		{
			// to avoid log is flooded with
			// java.lang.IllegalStateException: No rule engine context could be derived for order/cart
			importCsv("/cpqproductconfigservices/test/testDataDummyPromotion.impex", UTF_8);
		}
	}

	protected void initShop() throws InvalidCredentialsException
	{
		i18NService.setCurrentLocale(Locale.US);
		baseSiteService.setCurrentBaseSite(testConfig.getBaseSite(), false);
		assertNotNull(baseSiteService.getCurrentBaseSite());
	}

	protected void chooseEngine()
	{
		switch (testConfig.getEngine())
		{
			case CPQ:
				useRealAPI();
				break;
			case MOCK:
			default:
				useMock();
		}
	}

	protected void useMock()
	{
		engineDeterminationService.setMockEngineActive(true);
	}

	protected void useRealAPI()
	{
		engineDeterminationService.setMockEngineActive(false);
	}

	protected String getClientTokenAndSendContext()
	{
		businessContextService.sendBusinessContextToCPQ();
		return cpqInteractionStrategy.getClientAuthorizationString(businessContextService.getOwnerId());
	}


	@After
	public void tearDown() throws Exception
	{
		clearCPQProductConfigurations();
		engineDeterminationService.reset();
		disableOrderEntrySpacing();
	}

	protected void clearCPQProductConfigurations()
	{
		final String selectCPQProductInfos = "GET {CloudCPQOrderEntryProductInfo} WHERE {configurationId} is not null";
		final SearchResult<CloudCPQOrderEntryProductInfoModel> searchResult = flexibleSearchService.search(selectCPQProductInfos);

		if (CollectionUtils.isNotEmpty(searchResult.getResult()))
		{
			LOG.info(searchResult.getTotalCount() + " cpq product configuration entries found; all will be deleted now.");
			for (final CloudCPQOrderEntryProductInfoModel productInfo : searchResult.getResult())
			{
				configurationService.deleteConfiguration(productInfo.getConfigurationId());
			}
		}
	}

	public boolean isExtensionInSetup(final String extension)
	{
		final PlatformConfig platformConfig = ConfigUtil.getPlatformConfig(Utilities.class);
		final ExtensionInfo extensionInfo = platformConfig.getExtensionInfo(extension);
		return (extensionInfo != null);
	}

	/**
	 * Reads a model from persistence via flexible search
	 *
	 * @param flexibleSearchSelect
	 * @return Model
	 */
	protected <T> T getFromPersistence(final String flexibleSearchSelect)
	{
		final SearchResult<Object> searchResult = flexibleSearchService.search(flexibleSearchSelect);
		Assert.assertEquals("FlexSearch Query - " + flexibleSearchSelect + ":", 1, searchResult.getTotalCount());
		return (T) searchResult.getResult().get(0);
	}


	protected boolean isStrictUUID(final String configId)
	{
		return null != configId && configId.matches(STRICT_UUID_PATTERN);
	}

	protected void checkIsValidConfigId(final String configId)
	{
		assertNotNull(configId);
		if (CPQEngineForTest.CPQ == testConfig.getEngine())
		{
			assertTrue("Given config id is not a vlid UUID: " + configId, isStrictUUID(configId));
		}
		else if (CPQEngineForTest.MOCK == testConfig.getEngine())
		{
			assertTrue(configId.contains(MockConfigurationLifecycleStrategy.PREFIX_MOCK));
		}
	}

	protected String getAdminToken()
	{
		return cpqInteractionStrategy.getAuthorizationString();
	}

	public static void assumeTrueAndLog(final String msg, final boolean condition, final Logger Log)
	{
		if (!condition)
		{
			LOG.info(msg);
			assumeTrue(msg, condition);
		}
	}

	public static void assumeFalseAndLog(final String msg, final boolean condition, final Logger Log)
	{
		if (condition)
		{
			LOG.info(msg);
			assumeFalse(msg, condition);
		}
	}


}

