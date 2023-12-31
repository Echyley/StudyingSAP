/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.integrationtests;

import de.hybris.bootstrap.config.ConfigUtil;
import de.hybris.bootstrap.config.ExtensionInfo;
import de.hybris.bootstrap.config.PlatformConfig;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.jalo.CoreBasicDataCreator;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.c2l.C2LManager;
import de.hybris.platform.jalo.c2l.Currency;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.order.CartService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.SwitchableProviderFactory;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.services.ConfigurationProductUtil;
import de.hybris.platform.sap.productconfig.services.cache.ProductConfigurationCacheAccessService;
import de.hybris.platform.sap.productconfig.services.impl.ServiceConfigurationValueHelperImpl;
import de.hybris.platform.sap.productconfig.services.intf.ExternalConfigurationAccess;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.LifecycleStrategiesTestChecker;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderEntryLinkStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationLifecycleStrategy;
import de.hybris.platform.servicelayer.ServicelayerTest;
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

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Locale;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;


public abstract class CPQServiceLayerTest extends ServicelayerTest
{
	private static final String UTF_8 = "utf-8";

	private static final Logger LOG = Logger.getLogger(CPQServiceLayerTest.class);

	private static long overallSetUpTime = 0;

	protected static final String PDT_CALCULATION_MODE = "pdt.calculation.mode";
	protected static final String PDT_CALCULATION_MODE_SERVICE_LAYER = "sl";
	protected static final String PRODUCT_CODE_YSAP_NOCFG = "YSAP_NOCFG";
	protected static final String PRODUCT_CODE_CPQ_HOME_THEATER = "CPQ_HOME_THEATER";
	protected static final String PRODUCT_CODE_CONF_HOME_THEATER = "CONF_HOME_THEATER_ML";
	protected static final String PRODUCT_CODE_CPQ_LAPTOP = "CPQ_LAPTOP";
	protected static final String PRODUCT_CODE_CPQ_LAPTOP_MUSIC = "CPQ_LAPTOP_MUSIC";
	protected static final String PRODUCT_CODE_CPQ_LAPTOP_MUZAC = "CPQ_LAPTOP_MUZAC";
	protected static final String PRODUCT_CODE_YSAP_SIMPLE_POC = "YSAP_SIMPLE_POC";
	protected static final String PRODUCT_CODE_CONF_PIPE = "CONF_PIPE";
	protected static final String TEST_CONFIGURE_SITE = "testConfigureSite";

	@Resource(name = "cartService")
	protected CartService cartService;
	@Resource(name = "commerceCartService")
	protected CommerceCartService commerceCartService;
	@Resource(name = "productService")
	protected ProductService productService;
	@Resource(name = "sapProductConfigProductUtil")
	protected ConfigurationProductUtil configurationProductUtil;
	@Resource(name = "catalogVersionService")
	protected CatalogVersionService catalogVersionService;
	@Resource(name = "modelService")
	protected ModelService modelService;
	@Resource(name = "userService")
	protected UserService realUserService;
	@Resource(name = "sapProductConfigProviderFactory")
	protected SwitchableProviderFactory providerFactory;
	@Resource(name = "sapProductConfigConfigurationService")
	protected ProductConfigurationService cpqService;
	@Resource(name = "sapProductConfigConfigurationLifecycleStrategy")
	protected ConfigurationLifecycleStrategy configurationLifecycleStrategy;
	@Resource(name = "sapProductConfigAbstractOrderEntryLinkStrategy")
	protected ConfigurationAbstractOrderEntryLinkStrategy cpqAbstractOrderEntryLinkStrategy;
	@Resource(name = "authenticationService")
	protected AuthenticationService authenticationService;
	@Resource(name = "sapProductConfigProductConfigurationCacheAccessService")
	protected ProductConfigurationCacheAccessService productConfigurationCacheAccessService;
	@Resource(name = "i18NService")
	protected I18NService i18NService;
	@Resource(name = "commonI18NService")
	protected CommonI18NService commonI18NService;
	@Resource(name = "flexibleSearchService")
	protected FlexibleSearchService flexibleSearchService;
	@Resource(name = "baseStoreService")
	protected BaseStoreService baseStoreService;
	@Resource(name = "baseSiteService")
	protected BaseSiteService baseSiteService;
	@Resource(name = "sapProductConfigDefaultConfigurationService")
	protected ProductConfigurationService cpqServiceNoRules;
	@Resource(name = "sapProductConfigExternalConfigurationAccess")
	protected ExternalConfigurationAccess externalConfigurationAccess;

	protected CustomerModel customerModel;
	protected ServiceConfigurationValueHelperImpl serviceConfigValueHelper = new ServiceConfigurationValueHelperImpl();

	protected static String pdtCalculationMode = "";

	protected static final KBKey KB_CPQ_HOME_THEATER;
	protected static final KBKey KB_CONF_HOME_THEATER;
	protected static final KBKey KB_CPQ_LAPTOP;
	protected static final KBKey KB_Y_SAP_SIMPLE_POC;
	protected static final KBKey KB_CPQ_LAPTOP_MUSIC;

	static
	{
		KB_CPQ_HOME_THEATER = new KBKeyImpl(PRODUCT_CODE_CPQ_HOME_THEATER);
		KB_CONF_HOME_THEATER = new KBKeyImpl(PRODUCT_CODE_CONF_HOME_THEATER);
		KB_CPQ_LAPTOP = new KBKeyImpl(PRODUCT_CODE_CPQ_LAPTOP);
		KB_Y_SAP_SIMPLE_POC = new KBKeyImpl(PRODUCT_CODE_YSAP_SIMPLE_POC);
		KB_CPQ_LAPTOP_MUSIC = new KBKeyImpl(PRODUCT_CODE_CPQ_LAPTOP_MUSIC);
	}

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

	protected void importCsvIfExist(final String csvFile, final String encoding) throws Exception
	{
		final InputStream inStream = CPQServiceLayerTest.class.getResourceAsStream(csvFile);

		if (inStream != null)
		{
			inStream.close();
			importCsv(csvFile, encoding);
		}
		else
		{
			LOG.info("file not found: " + csvFile);
		}
	}

	public static void createCoreData() throws Exception
	{
		// copied from ServicelayerTestLogic.createCoredata()
		// we only need this, but do not want to import the impex file (to save testruntime)
		JaloSession.getCurrentSession().setUser(UserManager.getInstance().getAdminEmployee());
		new CoreBasicDataCreator().createEssentialData(Collections.EMPTY_MAP, null);
		//ServicelayerTestLogic.createCoreData();
	}

	protected void prepareCPQData() throws Exception
	{
		final long startTime = System.currentTimeMillis();
		LOG.info("CREATING CORE DATA FOR CPQ-TEST....");

		createCoreData();
		LOG.info("CORE DATA FOR CPQ-TEST CREATED (" + (System.currentTimeMillis() - startTime) + "ms )");

		importCPQTestData();
		importSapConfigurationTestDataIfExisting();

		// normally the base site is derived from the request URL via pattern macthing - in integration test mode we set it active manually
		baseSiteService.setCurrentBaseSite(TEST_CONFIGURE_SITE, false);

		//product catalog needs be set
		makeProductCatalogVersionAvailableInSession();

		// during ECP pipeline build all extensions are active, which leads to an inconsistent setup
		ensureNoRulesCPSWithDefaultLifecyclce();

		importPromotionDummyDataIfRequired();

		// default in hybris is DE/EUR
		LOG.info("Tests running with locale: " + i18NService.getCurrentLocale().toString());
		final CurrencyModel cur = i18NService.getCurrentCurrency();
		LOG.info("Tests running with Currency: isoCode=" + cur.getIsocode());

		final long duration = System.currentTimeMillis() - startTime;
		overallSetUpTime += duration;
		LOG.info("CPQ DATA READY FOR TEST! (" + duration + "ms / " + overallSetUpTime / 1000 + "s)");
	}

	protected void importPromotionDummyDataIfRequired() throws ImpExException
	{
		if (isExtensionInSetup("promotionengineservices"))
		{
			// to avoid log is flooded with
			// java.lang.IllegalStateException: No rule engine context could be derived for order/cart
			importCsv("/sapproductconfigservices/test/sapProductConfig_promotionDummyTestData.impex", UTF_8);
		}
	}

	protected void ensureNoRulesCPSWithDefaultLifecyclce() throws ClassNotFoundException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException
	{
		if (isDefaultLifecycle() && "ProductConfigurationRuleAwareServiceImpl".equals(cpqService.getClass().getSimpleName()))
		{
			// no compile dependency to rules extension possible/desired, hence we inject via reflection.
			final Class rulesResultUtil = Class
					.forName("de.hybris.platform.sap.productconfig.rules.service.ProductConfigRulesResultUtil");
			final Object defaultRulesUtil = Class
					.forName("de.hybris.platform.sap.productconfig.rules.service.impl.ProductConfigRulesResultUtilImpl").newInstance();
			cpqService.getClass().getMethod("setRulesResultUtil", rulesResultUtil).invoke(cpqService, defaultRulesUtil);
		}
	}

	protected void login(final String userName, final String password) throws InvalidCredentialsException
	{
		authenticationService.login(userName, password);
		makeProductCatalogVersionAvailableInSession();
	}

	protected void makeProductCatalogVersionAvailableInSession()
	{
		//Note that this does not mean that product searches take the session catalog version into account,
		//as the 'Frontend_Product' search restriction is not active. This means our test products being available
		//in _another_ catalog will cause RT exceptions.

		//In case integration tests persist test data (which is the case for sapproductconfigocctests, as a standalone server needs to be launched),
		//test products therefore should be based on the test catalog testConfigureCatalog.

		//If this is not possible at a later point: We need to enable restriction 'Frontend_Product' during test execution
		catalogVersionService.setSessionCatalogVersion("testConfigureCatalog", "Online");

	}

	protected void useCurrency_USD()
	{
		// force english locale for tests
		i18NService.setCurrentLocale(Locale.ENGLISH);
		// force USD currency for tests
		// do no inject via i18nService, as this causes a class cast exception in jalo layer in some scenarios
		// for example, Hybris price factories read currency directly from jalo session ==> class cast exception Currency <-> CurrencyModel
		// instead inject in jalo layer directly. as i18nservice will do the conversion from Currency to CurrencyModel on the fly
		final Currency usd = C2LManager.getInstance().getCurrencyByIsoCode("USD");
		JaloSession.getCurrentSession().getSessionContext().setCurrency(usd);
	}

	protected void useLocale_EN()
	{
		// force english locale for tests
		i18NService.setCurrentLocale(Locale.ENGLISH);
	}

	protected void importCPQUserData() throws ImpExException
	{
		importCsv("/sapproductconfigservices/test/sapProductConfig_basic_userTestData.impex", UTF_8);
		customerModel = getFromPersistence("Select {pk} from {Customer} where {uid}='cpq01@sap.com'");
	}

	protected void importCPQStockData() throws ImpExException
	{
		importCsv("/sapproductconfigservices/test/sapProductConfig_stock_testData.impex", UTF_8);
	}

	protected void importCPQTestData() throws ImpExException, Exception
	{
		LOG.info("CREATING CPQ DATA FOR CPQ-TEST....");
		importCsv("/sapproductconfigservices/test/sapProductConfig_basic_testData.impex", UTF_8);
	}

	protected void importSapConfigurationTestDataIfExisting() throws ImpExException, Exception
	{
		importCsvIfExist("/sapproductconfigintegration/test/sapProductConfig_sapconfiguration_testData.impex", UTF_8);
	}

	@Before
	public void initProviders()
	{
		ensureMockProvider();
	}

	public void ensureMockProvider()
	{
		//explicitly references sapProductConfigDefaultPricingParameters and sapProductConfigDefaultProductCsticAndValueParameterProviderMock
		providerFactory.switchProviderFactory("sapProductConfigMockProviderFactory");
	}

	public void ensureSSCProvider()
	{
		providerFactory.switchProviderFactory("sapProductConfigSSCProviderFactory");
	}

	public void ensureCPSProvider()
	{
		//sapProductConfigCPSProviderFactory only references the alias for an analytics provider and may contain the default implementation
		providerFactory.switchProviderFactory("sapProductConfigCPSPCIProviderFactory");
	}

	/**
	 * Reads a model from persistence via flexible search
	 *
	 * @param flexibleSearchSelect
	 * @return Model
	 */
	protected <T> T getFromPersistence(final String flexibleSearchSelect)
	{
		LOG.info("ExcutingQuery: " + flexibleSearchSelect);
		final SearchResult<Object> searchResult = flexibleSearchService.search(flexibleSearchSelect);
		Assert.assertEquals("FlexSearch Query - " + flexibleSearchSelect + ":", 1, searchResult.getTotalCount());
		return (T) searchResult.getResult().get(0);
	}

	@After
	public void tearDown()
	{
		clearProductConfigurationItems();
	}

	protected void clearProductConfigurationItems()
	{
		final String selectAllProductConfigItems = "Select {pk} from {ProductConfiguration}";
		final SearchResult<ProductConfigurationModel> searchResult = flexibleSearchService.search(selectAllProductConfigItems);

		if (CollectionUtils.isNotEmpty(searchResult.getResult()))
		{
			LOG.info(searchResult.getTotalCount() + " product configuration entries found");
			for (final ProductConfigurationModel config : searchResult.getResult())
			{
				configurationLifecycleStrategy.releaseSession(config.getConfigurationId());
				modelService.remove(config);
			}
		}
	}

	protected String getLifecycleBeanName() throws AssertionError
	{
		String className = cpqAbstractOrderEntryLinkStrategy.getClass().getSimpleName();
		if (cpqAbstractOrderEntryLinkStrategy.getClass().getSimpleName().startsWith("Dynamic"))
		{
			try
			{
				className = cpqAbstractOrderEntryLinkStrategy.getClass().getMethod("getDelegateBean")
						.invoke(cpqAbstractOrderEntryLinkStrategy).getClass().getSimpleName();
			}
			catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
					| SecurityException ex)
			{
				throw new AssertionError("failed to get delegate bean name from dynamic lifecycle bean", ex);
			}
		}
		return className;
	}

	public boolean isPersistentLifecycle() throws AssertionError
	{
		return getLifecycleBeanName().startsWith("Persistence");
	}

	public boolean isDefaultLifecycle() throws AssertionError
	{
		return getLifecycleBeanName().startsWith("Default");
	}


	protected LifecycleStrategiesTestChecker selectStrategyTestChecker()
	{
		String beanName = null;
		if (isPersistentLifecycle())
		{
			beanName = "sapProductConfigPersistentLifecycleTestChecker";
		}
		else if (isDefaultLifecycle())
		{
			beanName = "sapProductConfigDefaultLifecycleTestChecker";
		}
		else
		{
			throw new AssertionError("Unknown lifecyclce implementation: " + getLifecycleBeanName());
		}
		LOG.info("Running " + this.getClass().getSimpleName() + " with checker " + beanName);
		return (LifecycleStrategiesTestChecker) Registry.getApplicationContext().getBean(beanName);
	}

	protected boolean isExtensionInSetup(final String extension)
	{
		final PlatformConfig platformConfig = ConfigUtil.getPlatformConfig(Utilities.class);
		final ExtensionInfo extensionInfo = platformConfig.getExtensionInfo(extension);
		return (extensionInfo != null);
	}

	protected void disableSOMIfPresent() throws Exception
	{
		if(isExtensionInSetup("sapordermgmtservices")) {
			importCsvIfExist("/sapproductconfigintegration/test/sapProductConfig_sapconfiguration_noSOM_testData.impex", UTF_8);
		}
	}

}
