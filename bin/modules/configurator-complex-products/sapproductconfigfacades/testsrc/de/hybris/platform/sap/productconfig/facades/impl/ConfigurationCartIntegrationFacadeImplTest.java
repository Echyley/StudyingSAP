/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.facades.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.model.AbstractOrderEntryProductInfoModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.sap.productconfig.facades.ConfigConsistenceChecker;
import de.hybris.platform.sap.productconfig.facades.ConfigPricing;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.KBKeyData;
import de.hybris.platform.sap.productconfig.facades.PricingData;
import de.hybris.platform.sap.productconfig.facades.SessionAccessFacade;
import de.hybris.platform.sap.productconfig.facades.populator.ConfigurationOrderEntryProductInfoModelPopulator;
import de.hybris.platform.sap.productconfig.facades.populator.SolvableConflictPopulator;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingConfigurationParameter;
import de.hybris.platform.sap.productconfig.runtime.interf.ProviderFactory;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticGroupModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.InstanceModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.ClassificationSystemCPQAttributesContainer;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.UniqueKeyGeneratorImpl;
import de.hybris.platform.sap.productconfig.services.analytics.intf.AnalyticsService;
import de.hybris.platform.sap.productconfig.services.impl.SimpleConfigurationVariantUtilImpl;
import de.hybris.platform.sap.productconfig.services.intf.PricingService;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationOrderIntegrationService;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationPricingStrategy;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.productconfig.services.model.CPQOrderEntryProductInfoModel;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderEntryLinkStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderIntegrationStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationClassificationCacheStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationCopyStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationLifecycleStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationProductLinkStrategy;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


/**
 * Unit test for {@link ConfigurationCartIntegrationFacadeImpl}
 */
@UnitTest
public class ConfigurationCartIntegrationFacadeImplTest
{
	private static final String OTHER_CART_ITEM_KEY = "1234567890";
	private static final String CONFIG_ID = "configId";
	private static final String CART_ENTRY_KEY = "123";
	private static final String PRODUCT_CODE = "SAP_SIMPLE_POC";
	private static final String BASE_PRODUCT_CODE = "SAP_SIMPLE_POC_BASE";
	private static final String DUMMY_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><SOLUTION><CONFIGURATION CFGINFO=\"\" CLIENT=\"000\" COMPLETE=\"F\" CONSISTENT=\"T\" KBBUILD=\"3\" KBNAME=\"DUMMY_KB\" KBPROFILE=\"DUMMY_KB\" KBVERSION=\"3800\" LANGUAGE=\"E\" LANGUAGE_ISO=\"EN\" NAME=\"SCE 5.0\" ROOT_NR=\"1\" SCEVERSION=\" \"><INST AUTHOR=\"5\" CLASS_TYPE=\"300\" COMPLETE=\"F\" CONSISTENT=\"T\" INSTANCE_GUID=\"\" INSTANCE_ID=\"01\" NR=\"1\" OBJ_KEY=\"DUMMY_KB\" OBJ_TXT=\"Dummy KB\" OBJ_TYPE=\"MARA\" QTY=\"1.0\" UNIT=\"ST\"><CSTICS><CSTIC AUTHOR=\"8\" CHARC=\"DUMMY_CSTIC\" CHARC_TXT=\"Dummy CStic\" VALUE=\"8\" VALUE_TXT=\"Value 8\"/></CSTICS></INST><PARTS/><NON_PARTS/></CONFIGURATION><SALES_STRUCTURE><ITEM INSTANCE_GUID=\"\" INSTANCE_ID=\"1\" INSTANCE_NR=\"1\" LINE_ITEM_GUID=\"\" PARENT_INSTANCE_NR=\"\"/></SALES_STRUCTURE></SOLUTION>";
	private static final String CONFIG_ID_OLD = "oldConfigId";
	private static final String NEW_CONFIG_ID = "newConfigId";
	private static final String PRODUCT_CODE_CHANGEABLE_VARIANT = "CONF_M_PIPE_30";
	private static final String PRODUCT_CODE_NON_CHANGEABLE_VARIANT = "WEC_DRAGON_CAR_90";

	@InjectMocks
	private ConfigurationCartIntegrationFacadeImpl classUnderTest;
	private ConfigurationMessageMapperImpl configurationMessageMapper;
	private KBKeyData kbKey;
	private CartModel shoppingCart;
	private ProductModel product;
	private UnitModel unit;
	private ConfigurationData configData;
	private final List<AbstractOrderEntryModel> itemsInCart = new ArrayList<>();
	private CommerceCartModification modification;
	private ConfigModel configModel;
	private ConfigModel newConfigModel;
	private final GroupStatusCheckerImpl groupStatusChecker = new GroupStatusCheckerImpl();
	private final SolvableConflictPopulator solvableConflictPopulator = new SolvableConflictPopulator();
	private final Long quantity = 7l;

	@Mock
	private SimpleConfigurationVariantUtilImpl configurationVariantUtilMock;
	@Mock
	private CommerceCartService commerceCartServiceMock;
	@Mock
	private ProductConfigurationService configServiceMock;
	@Mock
	private ProductConfigurationOrderIntegrationService configurationPricingOrderIntegrationServiceMock;
	@Mock
	private ProductConfigurationPricingStrategy productConfigurationPricingStrategyMock;
	@Mock
	private CartService cartServiceMock;
	@Mock
	private ConfigurationClassificationCacheStrategy configurationClassificationCacheStrategyMock;
	@Mock
	private ModelService modelServiceMock;
	@Mock
	private ProductService productServiceMock;
	@Mock
	private ConfigurationOrderEntryProductInfoModelPopulator configInfoPopulatorMock;
	@Mock
	private CartEntryModel otherCartItemMock;
	@Mock
	private ConfigPricing configPricingMock;
	@Mock
	private AbstractOrderEntryModel cartItemMock;
	@Mock
	private ProviderFactory providerFactoryMock;
	@Mock
	private PricingConfigurationParameter pricingConfigurationParametersMock;
	@Mock
	private Converter<CommerceCartModification, CartModificationData> cartModificationConverterMock;
	@Mock
	private BaseStoreService baseStoreServiceMock;
	@Mock
	public SessionService sessionServiceMock;
	@Mock
	public PricingService pricingServiceMock;
	@Mock
	private Session sessionMock;
	@Mock
	private AnalyticsService analyticsServiceMock;
	@Mock
	private ConfigurationAbstractOrderEntryLinkStrategy configurationAbstractOrderEntryLinkStrategyMock;
	@Mock
	private ConfigurationAbstractOrderIntegrationStrategy configurationAbstractOrderIntegrationStrategyMock;
	@Mock
	private ConfigurationCopyStrategy copyStrategyMock;
	@Mock
	private ConfigurationLifecycleStrategy configLifecycleStrategyMock;
	@Mock
	private ConfigurationProductLinkStrategy configurationProductLinkStrategyMock;
	@Mock
	private ConfigConsistenceChecker configConsistenceCheckerMock;
	@Mock
	private ConfigurationAbstractOrderEntryLinkStrategy mockConfigurationAbstractOrderEntryLinkStrategy;
	@Mock
	private ConfigurationExpertModeFacadeImpl configurationExpertModeFacade;
	@Mock
	private SessionAccessFacade sessionAccessFacade;
	@Mock
	private ProductModel productChangeableVariant;
	@Mock
	private ProductModel productNonChangeableVariant;

	@Before
	public void setup() throws ConfigurationEngineException, CommerceCartModificationException
	{
		MockitoAnnotations.openMocks(this);

		final CsticTypeMapperImpl typeMapper = new CsticTypeMapperImpl();
		typeMapper.setUiTypeFinder(new UiTypeFinderImpl());
		typeMapper.setValueFormatTranslator(new ValueFormatTranslatorImpl());
		final UniqueUIKeyGeneratorImpl uiKeyGenerator = new UniqueUIKeyGeneratorImpl();
		uiKeyGenerator.setKeyGenerator(new UniqueKeyGeneratorImpl());
		typeMapper.setUiKeyGenerator(uiKeyGenerator);
		final IntervalInDomainHelperImpl intervalHandler = new IntervalInDomainHelperImpl();
		typeMapper.setIntervalHandler(intervalHandler);
		typeMapper.setProviderFactory(providerFactoryMock);
		typeMapper.setMessagesMapper(new ConfigurationMessageMapperImpl());
		solvableConflictPopulator.setGroupStatusChecker(groupStatusChecker);

		classUnderTest.setConflictPopulator(solvableConflictPopulator);
		classUnderTest.setUiKeyGenerator(uiKeyGenerator);
		classUnderTest.setAbstractOrderEntryLinkStrategy(configurationAbstractOrderEntryLinkStrategyMock);

		configurationMessageMapper = new ConfigurationMessageMapperImpl();
		configurationMessageMapper.setConfigurationAbstractOrderEntryLinkStrategy(mockConfigurationAbstractOrderEntryLinkStrategy);
		classUnderTest.setMessagesMapper(configurationMessageMapper);
		classUnderTest.setConfigurationExpertModeFacade(configurationExpertModeFacade);
		classUnderTest.setSessionAccessFacade(sessionAccessFacade);
		classUnderTest.setGroupStatusChecker(groupStatusChecker);


		given(mockConfigurationAbstractOrderEntryLinkStrategy.getCartEntryForDraftConfigId(anyString())).willReturn(null);
		given(configurationExpertModeFacade.isExpertModeActive()).willReturn(false);
		given(providerFactoryMock.getPricingParameter()).willReturn(pricingConfigurationParametersMock);
		given(pricingConfigurationParametersMock.showDeltaPrices()).willReturn(true);
		given(sessionServiceMock.getAttribute(ClassificationSystemCPQAttributesContainer.class.getName())).willReturn(null);
		given(sessionServiceMock.getCurrentSession()).willReturn(sessionMock);
		given(Boolean.valueOf(pricingServiceMock.isActive())).willReturn(Boolean.FALSE);

		kbKey = new KBKeyData();
		kbKey.setProductCode(PRODUCT_CODE);
		kbKey.setKbName("YSAP_SIMPLE_POC");
		kbKey.setKbLogsys("ABC");
		kbKey.setKbVersion("123");

		shoppingCart = new CartModel();
		shoppingCart.setEntries(itemsInCart);
		product = new ProductModel();
		unit = new UnitModel();

		product.setCode(PRODUCT_CODE);
		product.setUnit(unit);

		configData = new ConfigurationData();
		configData.setKbKey(kbKey);

		final PricingData pricingData = new PricingData();
		pricingData.setBasePrice(ConfigPricing.NO_PRICE);
		pricingData.setSelectedOptions(ConfigPricing.NO_PRICE);
		pricingData.setCurrentTotal(ConfigPricing.NO_PRICE);

		given(cartServiceMock.getSessionCart()).willReturn(shoppingCart);
		given(otherCartItemMock.getPk()).willReturn(PK.parse(OTHER_CART_ITEM_KEY));
		given(configPricingMock.getPricingData(any(ConfigModel.class))).willReturn(pricingData);

		final InstanceModel rootInstance = new InstanceModelImpl();
		final List<CsticGroupModel> csticGroups = new ArrayList<>();
		rootInstance.setCsticGroups(csticGroups);
		configModel = createConfigModel(CONFIG_ID, rootInstance);
		configModel.setKbKey(new KBKeyImpl(PRODUCT_CODE));
		newConfigModel = createConfigModel(NEW_CONFIG_ID, rootInstance);

		given(configServiceMock.retrieveConfigurationModel(NEW_CONFIG_ID, null)).willReturn(newConfigModel);
		given(configServiceMock.retrieveConfigurationModel(CONFIG_ID, null)).willReturn(configModel);

		cartItemMock.setProduct(product);
		modification = new CommerceCartModification();
		modification.setEntry(cartItemMock);


		given(cartItemMock.getPk()).willReturn(PK.parse("123"));
		given(cartItemMock.getProduct()).willReturn(product);
		given(configurationAbstractOrderIntegrationStrategyMock.getConfigurationForAbstractOrderEntry(cartItemMock))
				.willReturn(configModel);

		/*
		 * we use mock instead of real class, because calling baseStore.getName() would lead to illegalArgument Exception,
		 * as no LocalProvider is injected into the item Context. baseStore.getName() is used in some debug statements, so
		 * when running tests with log level debug this would lead to an exception
		 */
		final BaseStoreModel baseStore = Mockito.mock(BaseStoreModel.class);
		given(baseStore.getCatalogs()).willReturn(Collections.EMPTY_LIST);
		given(baseStoreServiceMock.getCurrentBaseStore()).willReturn(baseStore);

		final ClassificationSystemCPQAttributesProviderImpl nameProvider = new ClassificationSystemCPQAttributesProviderImpl();
		nameProvider.setBaseStoreService(baseStoreServiceMock);
		typeMapper.setNameProvider(nameProvider);

		given(productServiceMock.getProductForCode(PRODUCT_CODE)).willReturn(product);
		given(productServiceMock.getProductForCode(PRODUCT_CODE_CHANGEABLE_VARIANT)).willReturn(productChangeableVariant);
		given(productServiceMock.getProductForCode(PRODUCT_CODE_NON_CHANGEABLE_VARIANT)).willReturn(productNonChangeableVariant);
		given(productChangeableVariant.getCode()).willReturn(PRODUCT_CODE_CHANGEABLE_VARIANT);
		given(productNonChangeableVariant.getCode()).willReturn(PRODUCT_CODE_NON_CHANGEABLE_VARIANT);

		given(configurationVariantUtilMock.getBaseProductCode(product)).willReturn(BASE_PRODUCT_CODE);
		given(configurationVariantUtilMock.isCPQChangeableVariantProduct(productChangeableVariant)).willReturn(true);
		given(configurationVariantUtilMock.isCPQChangeableVariantProduct(productNonChangeableVariant)).willReturn(false);
		given(configLifecycleStrategyMock.retrieveConfigurationModel(CONFIG_ID)).willReturn(configModel);
		given(commerceCartServiceMock.addToCart(Mockito.any(CommerceCartParameter.class))).willReturn(modification);
		given(cartModificationConverterMock.convert(modification)).willReturn(new CartModificationData());

	}


	protected ConfigModel createConfigModel(final String configId, final InstanceModel rootInstance)
	{
		final ConfigModel configModel = new ConfigModelImpl();
		configModel.setRootInstance(rootInstance);
		configModel.setId(configId);
		configModel.setKbKey(new KBKeyImpl("productCode", "kbName", "kbLogsys", "kbVersion"));
		return configModel;
	}


	private ConfigurationData initializeFirstCall()
	{
		final ConfigurationData configContent = new ConfigurationData();
		configContent.setConfigId("123");
		configContent.setKbKey(kbKey);
		return configContent;
	}

	@Test
	public void testConfigurationAbstractOrderIntegrationStrategy()
	{
		assertEquals(configurationAbstractOrderIntegrationStrategyMock,
				classUnderTest.getConfigurationAbstractOrderIntegrationStrategy());
	}

	@Test
	public void testAddConfigurationToCart() throws CommerceCartModificationException
	{
		final ConfigurationData configContent = initializeFirstCall();
		given(configServiceMock.retrieveExternalConfiguration(configContent.getConfigId())).willReturn(DUMMY_XML);
		given(productServiceMock.getProductForCode(kbKey.getProductCode())).willReturn(product);
		given(commerceCartServiceMock.addToCart(Mockito.any(CommerceCartParameter.class))).willReturn(modification);

		classUnderTest.addConfigurationToCart(configContent);

		verify(cartItemMock).setProduct(product);
	}

	@Test
	public void testAddConfigurationToCartWithQty() throws CommerceCartModificationException
	{
		final ConfigurationData configContent = initializeFirstCall();

		given(configServiceMock.retrieveExternalConfiguration(configContent.getConfigId())).willReturn(DUMMY_XML);
		given(productServiceMock.getProductForCode(kbKey.getProductCode())).willReturn(product);
		when(commerceCartServiceMock.addToCart(Mockito.any(CommerceCartParameter.class))).then(createAddToCartAnswer());

		configContent.setQuantity(quantity);
		classUnderTest.addConfigurationToCart(configContent);
		assertEquals(Long.valueOf(quantity), cartItemMock.getQuantity());
	}

	@Test
	public void testAddProductConfigurationToCart() throws CommerceCartModificationException
	{
		initializeFirstCall();

		given(productServiceMock.getProductForCode(kbKey.getProductCode())).willReturn(product);

		classUnderTest.addProductConfigurationToCart(PRODUCT_CODE, quantity, CONFIG_ID);

		verify(cartItemMock).setProduct(product);
		verify(configInfoPopulatorMock, times(1)).populate(any(), any());
	}

	@Test
	public void testAddProductConfigurationToCartEmptyConfigId() throws CommerceCartModificationException
	{
		initializeFirstCall();

		classUnderTest.addProductConfigurationToCart(PRODUCT_CODE, quantity, "");

		verify(cartItemMock).setProduct(product);
		verify(configInfoPopulatorMock, times(1)).populate(any(), any());
	}

	@Test(expected = CommerceCartModificationException.class)
	public void testAddProductConfigurationToCartNoConfigForId()
			throws CommerceCartModificationException, ConfigurationEngineException
	{
		given(configLifecycleStrategyMock.retrieveConfigurationModel(CONFIG_ID_OLD)).willThrow(ConfigurationEngineException.class);

		classUnderTest.addProductConfigurationToCart(PRODUCT_CODE, quantity, CONFIG_ID_OLD);

	}

	@Test
	public void testAddProductConfigurationToCartChangeableVariant() throws CommerceCartModificationException
	{
		initializeFirstCall();

		classUnderTest.addProductConfigurationToCart(PRODUCT_CODE_CHANGEABLE_VARIANT, quantity, CONFIG_ID);
		//As we continue with the changeable variant, we expect to have it used 2 times when requesting the product model
		verify(productServiceMock, times(2)).getProductForCode(PRODUCT_CODE_CHANGEABLE_VARIANT);
	}

	@Test
	public void testAddProductConfigurationToCartNonChangeableVariant() throws CommerceCartModificationException
	{
		initializeFirstCall();

		classUnderTest.addProductConfigurationToCart(PRODUCT_CODE_NON_CHANGEABLE_VARIANT, quantity, CONFIG_ID);
		//As we continue with the base product, we expect to have the variant code only used 1 times when requesting the product model
		verify(productServiceMock, times(1)).getProductForCode(PRODUCT_CODE_NON_CHANGEABLE_VARIANT);
		verify(productServiceMock, times(1)).getProductForCode(PRODUCT_CODE);
	}

	@Test
	public void testAddProductConfigurationToCartNoProductCode() throws CommerceCartModificationException
	{
		initializeFirstCall();

		classUnderTest.addProductConfigurationToCart(null, quantity, CONFIG_ID);
		//As we don't provide a product code, the code will be taken from the KBKey
		verify(productServiceMock, times(1)).getProductForCode(PRODUCT_CODE);
	}

	@Test
	public void testAddProductConfigurationToCartBlankCode() throws CommerceCartModificationException
	{
		initializeFirstCall();

		classUnderTest.addProductConfigurationToCart("", quantity, CONFIG_ID);
		//As we don't provide a product code, the code will be taken from the KBKey
		verify(productServiceMock, times(1)).getProductForCode(PRODUCT_CODE);
	}

	@Test
	public void testAddConfigAttributesToCartEntry() throws CommerceCartModificationException
	{
		initializeFirstCall();
		given(configurationAbstractOrderEntryLinkStrategyMock.getConfigIdForCartEntry(cartItemMock.getPk().toString()))
				.willReturn(CONFIG_ID);

		classUnderTest.addConfigAttributesToCartEntry(cartItemMock);

		verify(configServiceMock, times(1)).retrieveConfigurationOverview(CONFIG_ID);
	}

	@Test
	public void testConvertNullToNullPK()
	{
		final PK pk = classUnderTest.convertStringToPK(null);
		assertEquals("null value should be mapped to NULL PK", PK.NULL_PK, pk);
	}

	@Test
	public void testConvertEmptyStringToNullPK()
	{
		final PK pk = classUnderTest.convertStringToPK("");
		assertEquals("empty value should be mapped to NULL PK", PK.NULL_PK, pk);
	}

	@Test
	public void testConvertStringToPK()
	{
		final PK pk = classUnderTest.convertStringToPK("123");
		assertEquals("string pk conversion failed", PK.parse("123"), pk);
	}

	@Test
	public void testGetOrCreateCartItem_newItemWithQty() throws CommerceCartModificationException
	{
		itemsInCart.add(otherCartItemMock);

		when(commerceCartServiceMock.addToCart(Mockito.any(CommerceCartParameter.class))).then(createAddToCartAnswer());

		configData.setQuantity(quantity);
		final AbstractOrderEntryModel cartItem = classUnderTest.getOrCreateCartItem(product, configData);
		assertNotNull(cartItem);
		assertEquals(Long.valueOf(quantity), cartItem.getQuantity());
		verify(configurationProductLinkStrategyMock).removeConfigIdForProduct(PRODUCT_CODE);
	}

	protected Answer createAddToCartAnswer()
	{
		final Answer answer = new Answer<CommerceCartModification>()
		{
			@Override
			public CommerceCartModification answer(final InvocationOnMock invocation) throws Throwable
			{
				final Object[] args = invocation.getArguments();
				final CommerceCartParameter ccp = (CommerceCartParameter) args[0];
				modification.setQuantity(ccp.getQuantity());
				given(cartItemMock.getQuantity()).willReturn(Long.valueOf(ccp.getQuantity()));
				return modification;
			}
		};
		return answer;
	}

	@Test
	public void testGetOrCreateCartItem_newItem() throws CommerceCartModificationException
	{
		itemsInCart.add(otherCartItemMock);
		configData.setConfigId(CONFIG_ID);
		given(commerceCartServiceMock.addToCart(Mockito.any(CommerceCartParameter.class))).willReturn(modification);
		final AbstractOrderEntryModel cartItem = classUnderTest.getOrCreateCartItem(product, configData);
		assertNotNull(cartItem);
		verify(configurationProductLinkStrategyMock).removeConfigIdForProduct(PRODUCT_CODE);
	}

	@Test
	public void testGetOrCreateCartItem_updateItemAndNotRemoveProductLink() throws CommerceCartModificationException
	{
		itemsInCart.add(otherCartItemMock);
		configData.setConfigId(CONFIG_ID);
		given(configurationAbstractOrderEntryLinkStrategyMock.getCartEntryForConfigId(CONFIG_ID)).willReturn(OTHER_CART_ITEM_KEY);

		given(Boolean.valueOf(modelServiceMock.isRemoved(otherCartItemMock))).willReturn(Boolean.FALSE);
		final AbstractOrderEntryModel cartItem = classUnderTest.getOrCreateCartItem(product, configData);
		assertNotNull(cartItem);
		verify(configurationProductLinkStrategyMock, times(0)).removeConfigIdForProduct(PRODUCT_CODE);
	}

	@Test
	public void testItemInCart_false() throws CommerceCartModificationException
	{
		itemsInCart.add(otherCartItemMock);

		final boolean itemInCart = classUnderTest.isItemInCartByKey(cartItemMock.getPk().toString());

		assertFalse("Item should not be in cart", itemInCart);
	}

	@Test
	public void testItemInCart_true() throws CommerceCartModificationException
	{
		itemsInCart.add(otherCartItemMock);
		itemsInCart.add(cartItemMock);

		final boolean itemInCart = classUnderTest.isItemInCartByKey(cartItemMock.getPk().toString());
		assertTrue("Item should be in cart", itemInCart);
	}

	@Test
	public void testGetOrCreateCartItem_updateRemovedItem() throws CommerceCartModificationException
	{
		itemsInCart.add(otherCartItemMock);
		given(configurationAbstractOrderEntryLinkStrategyMock.getCartEntryForConfigId(CONFIG_ID)).willReturn(OTHER_CART_ITEM_KEY);

		given(Boolean.valueOf(modelServiceMock.isRemoved(otherCartItemMock))).willReturn(Boolean.TRUE);

		given(commerceCartServiceMock.addToCart(Mockito.any(CommerceCartParameter.class))).willReturn(modification);
		final AbstractOrderEntryModel cartItem = classUnderTest.getOrCreateCartItem(product, configData);

		assertNotNull(cartItem);
		assertNotSame("New item expected", otherCartItemMock, cartItem);
	}

	@Test
	public void testRest()
	{
		classUnderTest.resetConfiguration("123");
		verify(configServiceMock).releaseSession("123");
	}

	@Test(expected = RuntimeException.class)
	public void testRestoreConfigurationWrongKey()
	{
		final KBKeyData kbKey = new KBKeyData();
		final String cartEntryKey = "X";
		classUnderTest.restoreConfiguration(kbKey, cartEntryKey);
	}

	@Test
	public void testRestoreConfiguration()
	{
		final KBKeyData kbKey = new KBKeyData();
		final String cartEntryKey = cartItemMock.getPk().toString();
		itemsInCart.add(cartItemMock);

		given(configServiceMock.createConfigurationFromExternal(any(KBKey.class), any(String.class))).willReturn(configModel);
		given(productServiceMock.getProductForCode(any(String.class))).willReturn(product);
		assertNotNull(classUnderTest.restoreConfiguration(kbKey, cartEntryKey));
	}

	@Test
	public void testLinkEntryWithConfigInfos()
	{
		final List<AbstractOrderEntryProductInfoModel> configInlineModels = new ArrayList<>();
		final CPQOrderEntryProductInfoModel inlineInfo = new CPQOrderEntryProductInfoModel();
		final CPQOrderEntryProductInfoModel anotherInlineInfo = new CPQOrderEntryProductInfoModel();
		configInlineModels.add(inlineInfo);
		configInlineModels.add(anotherInlineInfo);
		final ArgumentCaptor<List> arg = ArgumentCaptor.forClass(List.class);
		classUnderTest.linkEntryWithConfigInfos(cartItemMock, configInlineModels);
		verify(cartItemMock, times(1)).setProductInfos(arg.capture());
		assertNotNull(arg.getValue());
		assertEquals(configInlineModels, arg.getValue());
		assertEquals(cartItemMock, inlineInfo.getOrderEntry());
		assertEquals(cartItemMock, anotherInlineInfo.getOrderEntry());
	}

	@Test
	public void testUpdateLinkToCartItem()
	{
		classUnderTest.updateLinkToCartItem(CONFIG_ID, CART_ENTRY_KEY);
		verify(configurationAbstractOrderEntryLinkStrategyMock).setConfigIdForCartEntry(CART_ENTRY_KEY, CONFIG_ID);
		verify(configLifecycleStrategyMock, times(0)).releaseSession(anyString());
	}

	@Test
	public void testUpdateLinkToCartItemWithDraft()
	{
		given(configurationAbstractOrderEntryLinkStrategyMock.getCartEntryForDraftConfigId(CONFIG_ID)).willReturn(CART_ENTRY_KEY);
		given(configurationAbstractOrderEntryLinkStrategyMock.getConfigIdForCartEntry(CART_ENTRY_KEY)).willReturn(CONFIG_ID_OLD);
		classUnderTest.updateLinkToCartItem(CONFIG_ID, CART_ENTRY_KEY);
		verify(configurationAbstractOrderEntryLinkStrategyMock).setConfigIdForCartEntry(CART_ENTRY_KEY, CONFIG_ID);
		verify(configurationAbstractOrderEntryLinkStrategyMock).removeDraftConfigIdForCartEntry(CART_ENTRY_KEY);
		verify(configServiceMock).releaseSession(CONFIG_ID_OLD);
	}

	@Test
	public void testUpdateLinkToCartItemWithDraftAndOldConfigSame()
	{
		given(configurationAbstractOrderEntryLinkStrategyMock.getCartEntryForDraftConfigId(CONFIG_ID)).willReturn(CART_ENTRY_KEY);
		given(configurationAbstractOrderEntryLinkStrategyMock.getConfigIdForCartEntry(CART_ENTRY_KEY)).willReturn(CONFIG_ID);
		classUnderTest.updateLinkToCartItem(CONFIG_ID, CART_ENTRY_KEY);
		verify(configurationAbstractOrderEntryLinkStrategyMock).setConfigIdForCartEntry(CART_ENTRY_KEY, CONFIG_ID);
		verify(configurationAbstractOrderEntryLinkStrategyMock).removeDraftConfigIdForCartEntry(CART_ENTRY_KEY);
		verify(configLifecycleStrategyMock, times(0)).releaseSession(anyString());
	}

	@Test
	public void testUpdateLinkToCartItemWithDraftOnly()
	{
		given(configurationAbstractOrderEntryLinkStrategyMock.getCartEntryForDraftConfigId(CONFIG_ID)).willReturn(CART_ENTRY_KEY);
		given(configurationAbstractOrderEntryLinkStrategyMock.getConfigIdForCartEntry(CART_ENTRY_KEY)).willReturn(null);
		classUnderTest.updateLinkToCartItem(CONFIG_ID, CART_ENTRY_KEY);
		verify(configurationAbstractOrderEntryLinkStrategyMock).setConfigIdForCartEntry(CART_ENTRY_KEY, CONFIG_ID);
		verify(configurationAbstractOrderEntryLinkStrategyMock).removeDraftConfigIdForCartEntry(CART_ENTRY_KEY);
		verify(configLifecycleStrategyMock, times(0)).releaseSession(anyString());
	}

	@Test
	public void testUpdateProductConfigurationInCart() throws CommerceCartModificationException
	{
		final long entryNumber = 0;
		itemsInCart.add(otherCartItemMock);
		configData.setConfigId(CONFIG_ID);
		given(configurationAbstractOrderEntryLinkStrategyMock.getCartEntryForDraftConfigId(CONFIG_ID)).willReturn(CART_ENTRY_KEY);
		given(Boolean.valueOf(modelServiceMock.isRemoved(otherCartItemMock))).willReturn(Boolean.FALSE);
		itemsInCart.add(cartItemMock);
		given(cartModificationConverterMock.convert(any(CommerceCartModification.class))).willReturn(new CartModificationData());

		final CartModificationData cartModificationData = classUnderTest.updateProductConfigurationInCart(PRODUCT_CODE, CONFIG_ID);
		assertNotNull(cartModificationData);
		verify(configurationAbstractOrderEntryLinkStrategyMock).removeDraftConfigIdForCartEntry(CART_ENTRY_KEY);
		verify(configurationAbstractOrderEntryLinkStrategyMock).setConfigIdForCartEntry(CART_ENTRY_KEY, CONFIG_ID);
		verify(configInfoPopulatorMock, times(1)).populate(any(), any());
	}

	@Test
	public void testConfigureCartItemAlreadyInSession()
	{
		itemsInCart.add(cartItemMock);
		given(configurationAbstractOrderEntryLinkStrategyMock.getConfigIdForCartEntry(CART_ENTRY_KEY)).willReturn(CONFIG_ID);
		given(copyStrategyMock.deepCopyConfiguration(CONFIG_ID, PRODUCT_CODE, null, false)).willReturn(NEW_CONFIG_ID);

		final ConfigurationData configData = classUnderTest.configureCartItem(CART_ENTRY_KEY);
		assertNotNull(configData);
		assertEquals(NEW_CONFIG_ID, configData.getConfigId());
		verify(configurationAbstractOrderEntryLinkStrategyMock).setDraftConfigIdForCartEntry(CART_ENTRY_KEY, NEW_CONFIG_ID);
		verify(configurationProductLinkStrategyMock, times(0)).setConfigIdForProduct(PRODUCT_CODE, NEW_CONFIG_ID);
	}

	@Test
	public void testConfigureCartItemNotInSessionFromExternal()
	{
		itemsInCart.add(cartItemMock);
		given(configServiceMock.createConfigurationFromExternal(any(KBKey.class), eq(DUMMY_XML))).willReturn(configModel);

		final ConfigurationData configData = classUnderTest.configureCartItem(CART_ENTRY_KEY);

		assertNotNull(configData);
		assertEquals(CONFIG_ID, configData.getConfigId());
		verify(configurationAbstractOrderEntryLinkStrategyMock).setDraftConfigIdForCartEntry(CART_ENTRY_KEY, CONFIG_ID);
		verify(configurationProductLinkStrategyMock, times(0)).setConfigIdForProduct(PRODUCT_CODE, CONFIG_ID);
	}

	@Test
	public void testConfigureCartItemNotInSessionCreateDefault()
	{
		itemsInCart.add(cartItemMock);
		given(configServiceMock.createDefaultConfiguration(any(KBKey.class))).willReturn(configModel);

		final ConfigurationData configData = classUnderTest.configureCartItem(CART_ENTRY_KEY);
		assertNotNull(configData);
		assertEquals(CONFIG_ID, configData.getConfigId());
		verify(configurationAbstractOrderEntryLinkStrategyMock).setDraftConfigIdForCartEntry(CART_ENTRY_KEY, CONFIG_ID);
		verify(configurationProductLinkStrategyMock, times(0)).setConfigIdForProduct(PRODUCT_CODE, CONFIG_ID);
	}

	@Test
	public void testConfigureCartItemNotFound()
	{
		final ConfigurationData configData = classUnderTest.configureCartItem(CART_ENTRY_KEY);
		assertNull(configData);
	}

	@Test
	public void testDraftConfigCopyNotRequired()
	{
		final ConfigurationData configData = classUnderTest.draftConfig(CART_ENTRY_KEY, kbKey, CONFIG_ID, false, null);

		assertNotNull(configData);
		assertEquals(CONFIG_ID, configData.getConfigId());
		verify(configurationAbstractOrderEntryLinkStrategyMock).setDraftConfigIdForCartEntry(CART_ENTRY_KEY, CONFIG_ID);
		verifyNoMoreInteractions(configLifecycleStrategyMock);
	}

	@Test
	public void testDraftConfigCopyReleaseOldSession()
	{
		given(configurationAbstractOrderEntryLinkStrategyMock.getDraftConfigIdForCartEntry(CART_ENTRY_KEY))
				.willReturn(CONFIG_ID_OLD);
		final ConfigurationData configData = classUnderTest.draftConfig(CART_ENTRY_KEY, kbKey, CONFIG_ID, false, null);

		assertNotNull(configData);
		assertEquals(CONFIG_ID, configData.getConfigId());
		verify(configurationAbstractOrderEntryLinkStrategyMock).setDraftConfigIdForCartEntry(CART_ENTRY_KEY, CONFIG_ID);
		verify(configServiceMock).releaseSession(CONFIG_ID_OLD);
	}

	@Test
	public void testDraftConfigCopyRequired()
	{
		given(copyStrategyMock.deepCopyConfiguration(CONFIG_ID, PRODUCT_CODE, null, false)).willReturn(NEW_CONFIG_ID);

		final ConfigurationData configData = classUnderTest.draftConfig(CART_ENTRY_KEY, kbKey, CONFIG_ID, true, null);

		assertNotNull(configData);
		assertEquals(NEW_CONFIG_ID, configData.getConfigId());
		verify(configurationAbstractOrderEntryLinkStrategyMock).setDraftConfigIdForCartEntry(CART_ENTRY_KEY, NEW_CONFIG_ID);
		verifyNoMoreInteractions(configLifecycleStrategyMock);
	}

	@Test
	public void testProductLinkStrategy()
	{
		assertEquals(configurationProductLinkStrategyMock, classUnderTest.getProductLinkStrategy());
	}

	@Test
	public void testRemoveConfigurationLink()
	{
		classUnderTest.removeConfigurationLink(PRODUCT_CODE);
		verify(configurationProductLinkStrategyMock).removeConfigIdForProduct(PRODUCT_CODE);
	}

	@Test
	public void testUpdateKBKeyForVariants()
	{
		given(configurationVariantUtilMock.isCPQVariantProduct(product)).willReturn(true);
		given(configurationVariantUtilMock.isCPQNotChangeableVariantProduct(product)).willReturn(true);
		classUnderTest.updateKBKeyForVariants(configData);
		assertEquals(BASE_PRODUCT_CODE, configData.getKbKey().getProductCode());
	}

	@Test
	public void testUpdateKBKeyForVariants_NoVariant()
	{
		classUnderTest.updateKBKeyForVariants(configData);
		assertEquals(PRODUCT_CODE, configData.getKbKey().getProductCode());
	}

	@Test
	public void testUpdateKBKeyForVariants_changeableVariant()
	{
		given(configurationVariantUtilMock.isCPQVariantProduct(product)).willReturn(true);
		given(configurationVariantUtilMock.isCPQChangeableVariantProduct(product)).willReturn(true);
		classUnderTest.updateKBKeyForVariants(configData);
		assertEquals(PRODUCT_CODE, configData.getKbKey().getProductCode());
	}


	@Test
	public void testRemoveLinkToProduct()
	{
		classUnderTest.removeLinkToProduct(PRODUCT_CODE);
		verify(configurationProductLinkStrategyMock).removeConfigIdForProduct(PRODUCT_CODE);
		verify(sessionAccessFacade).removeUiStatusForProduct(PRODUCT_CODE);
	}

	@Test
	public void testConfigureCartItemOnExistingDraft()
	{
		itemsInCart.add(cartItemMock);
		Mockito.when(configurationAbstractOrderEntryLinkStrategyMock.getDraftConfigIdForCartEntry(CART_ENTRY_KEY))
				.thenReturn(CONFIG_ID);
		final ConfigurationData configurationData = classUnderTest.configureCartItemOnExistingDraft(CART_ENTRY_KEY);
		assertNotNull(configurationData);
		assertEquals(CONFIG_ID, configurationData.getConfigId());
	}

	@Test
	public void testConfigureCartItemOnExistingDraftSwitchesVariantToKMAT()
	{
		given(configurationVariantUtilMock.isCPQNotChangeableVariantProduct(product)).willReturn(true);
		itemsInCart.add(cartItemMock);
		given(configurationAbstractOrderEntryLinkStrategyMock.getDraftConfigIdForCartEntry(CART_ENTRY_KEY)).willReturn(CONFIG_ID);
		final ConfigurationData configurationData = classUnderTest.configureCartItemOnExistingDraft(CART_ENTRY_KEY);
		assertNotNull(configurationData);
		assertEquals(BASE_PRODUCT_CODE, configurationData.getKbKey().getProductCode());
	}


	@Test
	public void testConfigureCartItemOnExistingDraftNoDraftAvailable()
	{
		itemsInCart.add(cartItemMock);
		Mockito.when(configurationAbstractOrderEntryLinkStrategyMock.getDraftConfigIdForCartEntry(CART_ENTRY_KEY)).thenReturn(null);

		final ConfigurationData result = classUnderTest.configureCartItemOnExistingDraft(CART_ENTRY_KEY);
		assertNull(result);
	}

	@Test
	public void testGetOrderEntry()
	{
		itemsInCart.add(cartItemMock);
		final AbstractOrderEntryModel orderEntry = classUnderTest.getOrderEntry(CART_ENTRY_KEY);
		assertNotNull(orderEntry);
		assertEquals(CART_ENTRY_KEY, orderEntry.getPk().toString());
	}

}
