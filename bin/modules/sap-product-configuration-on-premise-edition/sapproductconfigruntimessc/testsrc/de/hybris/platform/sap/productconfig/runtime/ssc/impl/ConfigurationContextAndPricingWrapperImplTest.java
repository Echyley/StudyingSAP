/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.ssc.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationParameterB2B;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ConfigModelFactoryImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.PriceModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ZeroPriceModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.services.ConfigurationProductUtil;
import de.hybris.platform.sap.productconfig.runtime.ssc.PricingConfigurationParameterSSC;
import de.hybris.platform.sap.productconfig.runtime.ssc.constants.SapproductconfigruntimesscConstants;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.sap.custdev.projects.fbs.slc.cfg.IConfigSession;
import com.sap.custdev.projects.fbs.slc.cfg.client.IDocument;
import com.sap.custdev.projects.fbs.slc.cfg.client.IItemInfo;
import com.sap.custdev.projects.fbs.slc.cfg.client.IPricingAttribute;
import com.sap.custdev.projects.fbs.slc.cfg.ipintegration.InteractivePricingException;
import com.sap.custdev.projects.fbs.slc.cfg.ipintegration.InteractivePricingIntegration;
import com.sap.custdev.projects.fbs.slc.helper.ConfigSessionManager;
import com.sap.custdev.projects.fbs.slc.pricing.ip.api.InteractivePricingMgr;
import com.sap.custdev.projects.fbs.slc.pricing.slc.api.ISLCDocument;
import com.sap.spe.conversion.ICurrencyValue;
import com.sap.spe.pricing.transactiondata.IPricingCondition;
import com.sap.spe.pricing.transactiondata.IPricingDocument;


@UnitTest
public class ConfigurationContextAndPricingWrapperImplTest
{

	private static final String SALES_ORG = "sales org";
	private static final String DISTRIBUTION_CHANNEL = "distribution channel";
	private static final String DIVISION = "division";
	private static final String EMPTY = "";
	private static final String CUSTOMER_NUMBER = "customer number";
	private static final String SAP_COUNTRY_CODE = "sap country code";
	private static final String ISO_CURRENCY_CODE = "ISO CURRENCY CODE";
	private static final String SAP_CURRENCY_CODE = "SAP CURRENCY CODE";
	private static final String SAP_UNIT_CODE = "sapUnitCode";
	private static final String PRODUCT_CODE = "productCode";
	private static final String CONFIG_ID = "1234";

	private final ConfigurationContextAndPricingWrapperImpl classUnderTest = new ConfigurationContextAndPricingWrapperImpl();
	@Mock
	private ConfigurationProductUtil mockConfigProductUtil;
	@Mock
	private PricingConfigurationParameterSSC mockPricingConfigParamater;
	@Mock
	private ProductModel mockProduct;
	@Mock
	private UnitModel mockUnitModel;
	@Mock
	private IConfigSession session;

	@Mock
	private CommonI18NService mockI18NService;

	@Mock
	private ConfigModel mockConfigModel;

	@Mock
	private ConfigModelFactoryImpl mockConfigModelFactory;

	@Mock
	private PriceModel mockPriceModel;

	@Mock
	private ConfigSessionManager mockConfigSessionMgr;

	@Mock
	private InteractivePricingIntegration mockInteractivePricingIntegration;

	@Mock
	private InteractivePricingMgr mockPricingMgr;

	@Mock
	private ISLCDocument mockDoucment;

	@Mock
	private IPricingDocument mockPricingDocument;

	@Mock
	private ICurrencyValue mockCurrencyValue;

	@Mock
	private ConfigurationParameterB2B mockConfigurationParameterB2B;

	@Mock
	private InstanceModel mockInstance;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest.setConfigurationProductUtil(mockConfigProductUtil);
		classUnderTest.setPricingConfigurationParameter(mockPricingConfigParamater);
		classUnderTest.setConfigModelFactory(mockConfigModelFactory);
		classUnderTest.setI18NService(mockI18NService);


		when(mockConfigProductUtil.getProductForCurrentCatalog(PRODUCT_CODE)).thenReturn(mockProduct);
		when(mockProduct.getUnit()).thenReturn(mockUnitModel);
		when(mockPricingConfigParamater.retrieveUnitSapCode(mockUnitModel)).thenReturn(SAP_UNIT_CODE);

		when(mockPricingConfigParamater.isPricingSupported()).thenReturn(true);
		when(session.getConfigSessionManager()).thenReturn(mockConfigSessionMgr);
		when(mockConfigSessionMgr.getInteractivePricingIntegration(CONFIG_ID)).thenReturn(mockInteractivePricingIntegration);
		when(mockInteractivePricingIntegration.getInteractivePricingManager()).thenReturn(mockPricingMgr);
		when(mockPricingMgr.getDocument()).thenReturn(mockDoucment);
		when(mockDoucment.getPricingDocument()).thenReturn(mockPricingDocument);
		when(mockPricingDocument.getNetValueWithoutFreight()).thenReturn(mockCurrencyValue);
		when(mockPricingDocument.getConditions()).thenReturn(new IPricingCondition[] {});
		when(mockConfigModelFactory.createInstanceOfPriceModel()).thenReturn(mockPriceModel);

		when(mockConfigModel.getRootInstance()).thenReturn(mockInstance);
		when(mockInstance.getName()).thenReturn("ROOT");
	}

	@Test
	public void testGetItemPricingContext()
	{
		final KBKey kbKey = new KBKeyImpl(PRODUCT_CODE);
		final IItemInfo result = classUnderTest.getItemPricingContext(kbKey);
		assertNotNull(result);

		assertEquals(PRODUCT_CODE,
				result.getAttributes().get(SapproductconfigruntimesscConstants.PRICING_ATTRIBUTE_PMATN).getValues().get(0));
		assertEquals(PRODUCT_CODE, result.getProductId());
		assertEquals("X",
				result.getAttributes().get(SapproductconfigruntimesscConstants.PRICING_ATTRIBUTE_PRSFD).getValues().get(0));
		assertNotNull(result.getTimestamps().get(SapproductconfigruntimesscConstants.DET_DEFAULT_TIMESTAMP));
		assertEquals(0, BigDecimal.ONE.compareTo(result.getQuantity()));
		assertEquals(SAP_UNIT_CODE, result.getQuantityUnit());
		assertTrue(result.getPricingRelevantFlag());
	}

	@Test
	public void testProcessPriceWithoutParameters() throws Exception
	{
		classUnderTest.setPricingConfigurationParameter(null);
		classUnderTest.processPrice(session, CONFIG_ID, null);
		verify(session, times(0)).getConfigSessionManager();
	}

	@Test
	public void testProcessPricePricingUnsupported() throws Exception
	{
		when(mockPricingConfigParamater.isPricingSupported()).thenReturn(false);
		classUnderTest.processPrice(session, CONFIG_ID, null);
		verify(session, times(0)).getConfigSessionManager();
	}

	@Test
	public void testProcessPriceTargetBasePriceIsNullAndOptionPriceIsNull() throws Exception
	{
		when(mockPricingConfigParamater.getTargetForBasePrice()).thenReturn(null);
		when(mockPricingConfigParamater.getTargetForSelectedOptions()).thenReturn(null);

		classUnderTest.processPrice(session, CONFIG_ID, mockConfigModel);
		verify(session, times(1)).getConfigSessionManager();
		verify(mockPricingDocument, times(0)).getAccumulatedValuesForConditionsWithPurpose();
	}

	@Test
	public void testProcessPriceTargetBasePriceIsNullAndOptionPriceIsEmpty() throws Exception
	{
		when(mockPricingConfigParamater.getTargetForBasePrice()).thenReturn(null);
		when(mockPricingConfigParamater.getTargetForSelectedOptions()).thenReturn(EMPTY);

		classUnderTest.processPrice(session, CONFIG_ID, mockConfigModel);
		verify(session, times(1)).getConfigSessionManager();
		verify(mockPricingDocument, times(0)).getAccumulatedValuesForConditionsWithPurpose();
	}

	@Test
	public void testProcessPriceTargetBasePriceIsEmptyAndOptionPriceIsNull() throws Exception
	{
		when(mockPricingConfigParamater.getTargetForBasePrice()).thenReturn(EMPTY);
		when(mockPricingConfigParamater.getTargetForSelectedOptions()).thenReturn(null);

		classUnderTest.processPrice(session, CONFIG_ID, mockConfigModel);
		verify(session, times(1)).getConfigSessionManager();
		verify(mockPricingDocument, times(0)).getAccumulatedValuesForConditionsWithPurpose();
	}

	@Test
	public void testProcessPriceTargetBasePriceIsEmptyAndOptionPriceIsEmpty() throws Exception
	{
		when(mockPricingConfigParamater.getTargetForBasePrice()).thenReturn(EMPTY);
		when(mockPricingConfigParamater.getTargetForSelectedOptions()).thenReturn(null);

		classUnderTest.processPrice(session, CONFIG_ID, mockConfigModel);
		verify(session, times(1)).getConfigSessionManager();
		verify(mockPricingDocument, times(0)).getAccumulatedValuesForConditionsWithPurpose();
	}

	@Test
	public void testProcessPriceBasePriceIsSetAndOptionPriceIsNull() throws Exception
	{
		when(mockPricingConfigParamater.getTargetForBasePrice()).thenReturn("47");
		when(mockPricingConfigParamater.getTargetForSelectedOptions()).thenReturn(null);

		classUnderTest.processPrice(session, CONFIG_ID, mockConfigModel);
		verify(session, times(1)).getConfigSessionManager();
		verify(mockPricingDocument, times(1)).getAccumulatedValuesForConditionsWithPurpose();
	}

	@Test
	public void testProcessPriceBasePriceIsNullAndOptionPriceIsSet() throws Exception
	{
		when(mockPricingConfigParamater.getTargetForBasePrice()).thenReturn(null);
		when(mockPricingConfigParamater.getTargetForSelectedOptions()).thenReturn("47");

		classUnderTest.processPrice(session, CONFIG_ID, mockConfigModel);
		verify(session, times(1)).getConfigSessionManager();
		verify(mockPricingDocument, times(1)).getAccumulatedValuesForConditionsWithPurpose();
	}

	@Test
	public void testRetrivePrice()
	{
		when(mockConfigModelFactory.getZeroPriceModel()).thenCallRealMethod();
		when(mockConfigModelFactory.createInstanceOfPriceModel()).thenReturn(mockPriceModel);
		when(mockCurrencyValue.getValue()).thenReturn(new BigDecimal(12));
		when(mockCurrencyValue.getUnitName()).thenReturn("EUR");

		final Map<String, ICurrencyValue> condFuncValuesMap = new HashMap<>();
		condFuncValuesMap.put("47", mockCurrencyValue);


		PriceModel priceModel = classUnderTest.retrievePrice(null, null, "base price");
		assertEquals(ZeroPriceModelImpl.NO_PRICE, priceModel);

		priceModel = classUnderTest.retrievePrice(EMPTY, null, "base price");
		assertEquals(ZeroPriceModelImpl.NO_PRICE, priceModel);

		priceModel = classUnderTest.retrievePrice("47", null, "base price");
		assertEquals(ZeroPriceModelImpl.NO_PRICE, priceModel);

		priceModel = classUnderTest.retrievePrice(null, condFuncValuesMap, "base price");
		assertEquals(ZeroPriceModelImpl.NO_PRICE, priceModel);

		priceModel = classUnderTest.retrievePrice(EMPTY, condFuncValuesMap, "base price");
		assertEquals(ZeroPriceModelImpl.NO_PRICE, priceModel);

		priceModel = classUnderTest.retrievePrice("NONE", condFuncValuesMap, "base price");
		assertEquals(ZeroPriceModelImpl.NO_PRICE, priceModel);

		priceModel = classUnderTest.retrievePrice("47", condFuncValuesMap, "base price");
		assertEquals(mockPriceModel, priceModel);
	}

	@Test
	public void testRetrivePriceCurrency()
	{
		when(mockConfigModelFactory.getZeroPriceModel()).thenCallRealMethod();
		when(mockConfigModelFactory.createInstanceOfPriceModel()).thenReturn(new PriceModelImpl());
		when(mockCurrencyValue.getValue()).thenReturn(new BigDecimal(12));
		when(mockCurrencyValue.getUnitName()).thenReturn(SAP_CURRENCY_CODE);
		when(mockPricingConfigParamater.convertSapToIsoCode(SAP_CURRENCY_CODE)).thenReturn(ISO_CURRENCY_CODE);

		final Map<String, ICurrencyValue> condFuncValuesMap = new HashMap<>();
		condFuncValuesMap.put("47", mockCurrencyValue);

		final PriceModel result = classUnderTest.retrievePrice("47", condFuncValuesMap, "base price");
		assertEquals(ISO_CURRENCY_CODE, result.getCurrency());
	}

	@Test
	public void testGetDocumentPricingContextB2BContext()
	{
		when(mockPricingConfigParamater.getSalesOrganization()).thenReturn("4711");

		classUnderTest.setConfigurationParameterB2B(null);
		IDocument documentPricingContext = classUnderTest.getDocumentPricingContext();
		Map<String, IPricingAttribute> attributes = documentPricingContext.getAttributes();
		assertFalse(attributes.containsKey(SapproductconfigruntimesscConstants.PRICING_ATTRIBUTE_KUNNR));

		when(mockConfigurationParameterB2B.isSupported()).thenReturn(false);
		documentPricingContext = classUnderTest.getDocumentPricingContext();
		attributes = documentPricingContext.getAttributes();
		assertFalse(attributes.containsKey(SapproductconfigruntimesscConstants.PRICING_ATTRIBUTE_KUNNR));

		when(mockConfigurationParameterB2B.isSupported()).thenReturn(true);
		classUnderTest.setConfigurationParameterB2B(mockConfigurationParameterB2B);
		when(mockConfigurationParameterB2B.getCustomerNumber()).thenReturn("0815");
		documentPricingContext = classUnderTest.getDocumentPricingContext();
		attributes = documentPricingContext.getAttributes();
		assertTrue(attributes.containsKey(SapproductconfigruntimesscConstants.PRICING_ATTRIBUTE_KUNNR));
	}

	@Test
	public void testProvideCurrentTotalPriceModel()
	{
		final BigDecimal priceValue = new BigDecimal(12.46);
		when(mockCurrencyValue.getValue()).thenReturn(priceValue);
		when(mockCurrencyValue.getUnitName()).thenReturn(SAP_CURRENCY_CODE);
		when(mockConfigModelFactory.createInstanceOfPriceModel()).thenReturn(new PriceModelImpl());
		when(mockPricingConfigParamater.convertSapToIsoCode(SAP_CURRENCY_CODE)).thenReturn(ISO_CURRENCY_CODE);
		final ConfigModel configModel = new ConfigModelImpl();
		classUnderTest.provideCurrentTotalPriceModel(configModel, mockCurrencyValue);
		final PriceModel result = configModel.getCurrentTotalPrice();
		assertNotNull(result);
		assertEquals(ISO_CURRENCY_CODE, result.getCurrency());
		assertEquals(0, result.getPriceValue().compareTo(priceValue));
	}

	@Test
	public void testAddCountrySapCode()
	{
		when(mockConfigurationParameterB2B.isSupported()).thenReturn(true);
		when(mockConfigurationParameterB2B.getCountrySapCode()).thenReturn(SAP_COUNTRY_CODE);
		classUnderTest.setConfigurationParameterB2B(mockConfigurationParameterB2B);
		final Map<String, String> configContext = new HashMap();
		classUnderTest.addCountrySapCodeToContext(configContext);
		assertEquals(SAP_COUNTRY_CODE, configContext.get(SapproductconfigruntimesscConstants.CONTEXT_ATTRIBUTE_VBPA_AG_LAND1));
		assertEquals(SAP_COUNTRY_CODE, configContext.get(SapproductconfigruntimesscConstants.CONTEXT_ATTRIBUTE_VBPA_RG_LAND1));
	}

	@Test
	public void testAddCountrySapCodeNotB2BInContext()
	{

		final Map<String, String> configContext = new HashMap();
		classUnderTest.addCountrySapCodeToContext(configContext);
		assertTrue(configContext.isEmpty());
	}

	@Test
	public void testAddCountrySapCodeUnsupported()
	{
		when(mockConfigurationParameterB2B.isSupported()).thenReturn(false);
		when(mockConfigurationParameterB2B.getCountrySapCode()).thenReturn(EMPTY);
		final Map<String, String> configContext = new HashMap();
		classUnderTest.addCountrySapCodeToContext(configContext);
		assertTrue(configContext.isEmpty());
	}

	@Test
	public void testAddCustomerNumberToContext()
	{
		when(mockConfigurationParameterB2B.isSupported()).thenReturn(true);
		when(mockConfigurationParameterB2B.getCustomerNumber()).thenReturn(CUSTOMER_NUMBER);
		classUnderTest.setConfigurationParameterB2B(mockConfigurationParameterB2B);
		final Map<String, String> configContext = new HashMap();
		classUnderTest.addCustomerNumberToContext(configContext);
		assertEquals(CUSTOMER_NUMBER, configContext.get(SapproductconfigruntimesscConstants.CONTEXT_ATTRIBUTE_VBAK_KUNNR));
		assertEquals(CUSTOMER_NUMBER, configContext.get(SapproductconfigruntimesscConstants.CONTEXT_ATTRIBUTE_VBPA_AG_KUNNR));
		assertEquals(CUSTOMER_NUMBER, configContext.get(SapproductconfigruntimesscConstants.CONTEXT_ATTRIBUTE_VBPA_RG_KUNNR));
	}

	@Test
	public void testAddCustomerNotB2BInContext()
	{

		final Map<String, String> configContext = new HashMap();
		classUnderTest.addCustomerNumberToContext(configContext);
		assertTrue(configContext.isEmpty());
	}

	@Test
	public void testAddCustonerUnsupported()
	{
		when(mockConfigurationParameterB2B.isSupported()).thenReturn(false);
		when(mockConfigurationParameterB2B.getCustomerNumber()).thenReturn(EMPTY);
		final Map<String, String> configContext = new HashMap();
		classUnderTest.addCustomerNumberToContext(configContext);
		assertTrue(configContext.isEmpty());
	}

	@Test
	public void testAddSalesorganizationToContext()
	{
		when(mockPricingConfigParamater.getSalesOrganization()).thenReturn(SALES_ORG);
		final Map<String, String> configContext = new HashMap();
		classUnderTest.addSalesOrganisationToContext(configContext);
		assertEquals(SALES_ORG, configContext.get(SapproductconfigruntimesscConstants.CONTEXT_ATTRIBUTE_VBAK_VKORG));
	}

	@Test
	public void testAddSalesOrganizationToContextEmpty()
	{
		when(mockPricingConfigParamater.getSalesOrganization()).thenReturn(EMPTY);
		final Map<String, String> configContext = new HashMap();
		classUnderTest.addSalesOrganisationToContext(configContext);
		assertTrue(configContext.isEmpty());
	}

	@Test
	public void testAddDistributionChannelForConditionsToContext()
	{
		when(mockPricingConfigParamater.getDistributionChannelForConditions()).thenReturn(DISTRIBUTION_CHANNEL);
		final Map<String, String> configContext = new HashMap();
		classUnderTest.addDistributionChannelToContext(configContext);
		assertEquals(DISTRIBUTION_CHANNEL, configContext.get(SapproductconfigruntimesscConstants.CONTEXT_ATTRIBUTE_VBAK_VTWEG));
	}

	@Test
	public void testAddDistributionChannelForConditionsToContextEmpty()
	{
		when(mockPricingConfigParamater.getDistributionChannelForConditions()).thenReturn(EMPTY);
		final Map<String, String> configContext = new HashMap();
		classUnderTest.addDistributionChannelToContext(configContext);
		assertTrue(configContext.isEmpty());
	}

	@Test
	public void testAddDivisionsForConditionsToContext()
	{
		when(mockPricingConfigParamater.getDivisionForConditions()).thenReturn(DIVISION);
		final Map<String, String> configContext = new HashMap();
		classUnderTest.addDivisionsForConditionsToContext(configContext);
		assertEquals(DIVISION, configContext.get(SapproductconfigruntimesscConstants.CONTEXT_ATTRIBUTE_VBAK_SPART));
	}

	@Test
	public void testAddDivisionsForConditionsToContextEmpty()
	{
		when(mockPricingConfigParamater.getDivisionForConditions()).thenReturn(EMPTY);
		final Map<String, String> configContext = new HashMap();
		classUnderTest.addDivisionsForConditionsToContext(configContext);
		assertTrue(configContext.isEmpty());
	}

	@Test
	public void testPreparePricingContext() throws InteractivePricingException
	{
		final KBKey kbKey = new KBKeyImpl(PRODUCT_CODE);
		classUnderTest.preparePricingContext(session, CONFIG_ID, kbKey);
		verify(session).getConfigSessionManager();
		verify(mockConfigSessionMgr).setPricingContext(eq(CONFIG_ID), any(IDocument.class), any(IItemInfo.class), any());
		verify(mockConfigSessionMgr).setInteractivePricingEnabled(CONFIG_ID, true);
	}

	@Test
	public void testPreparePricingContextPricingInActive() throws InteractivePricingException
	{
		final KBKey kbKey = new KBKeyImpl(PRODUCT_CODE);
		classUnderTest.setPricingConfigurationParameter(null);
		classUnderTest.preparePricingContext(session, CONFIG_ID, kbKey);
		verify(session, times(0)).getConfigSessionManager();
		verify(mockConfigSessionMgr, times(0)).setPricingContext(eq(CONFIG_ID), any(IDocument.class), any(IItemInfo.class), any());
		verify(mockConfigSessionMgr, times(0)).setInteractivePricingEnabled(CONFIG_ID, true);

	}

	@Test
	public void testRetrieveConfigurationContext()
	{
		final KBKey kbKey = new KBKeyImpl(PRODUCT_CODE);
		final Hashtable<String, String> result = classUnderTest.retrieveConfigurationContext(kbKey);
		assertNotNull(result);
		assertEquals(new SimpleDateFormat("yyyyMMdd").format(new Date()),
				result.get(SapproductconfigruntimesscConstants.CONTEXT_ATTRIBUTE_VBAK_ERDAT));
		assertEquals("1", result.get(SapproductconfigruntimesscConstants.CONTEXT_ATTRIBUTE_VBAP_KWMENG));
		assertEquals(PRODUCT_CODE, result.get(SapproductconfigruntimesscConstants.CONTEXT_ATTRIBUTE_VBAP_MATNR));
	}

	@Test
	public void testRetrieveConfigurationContextNoPricing()
	{
		classUnderTest.setPricingConfigurationParameter(null);
		final KBKey kbKey = new KBKeyImpl(PRODUCT_CODE);
		final Hashtable<String, String> result = classUnderTest.retrieveConfigurationContext(kbKey);
		assertNotNull(result);
		assertEquals(new SimpleDateFormat("yyyyMMdd").format(new Date()),
				result.get(SapproductconfigruntimesscConstants.CONTEXT_ATTRIBUTE_VBAK_ERDAT));
		assertEquals("1", result.get(SapproductconfigruntimesscConstants.CONTEXT_ATTRIBUTE_VBAP_KWMENG));
		assertEquals(PRODUCT_CODE, result.get(SapproductconfigruntimesscConstants.CONTEXT_ATTRIBUTE_VBAP_MATNR));
		assertEquals(3, result.size());
	}

	@Test
	public void testIsPricingConfigurationActive()
	{
		assertTrue(classUnderTest.isPricingConfigurationActive());
	}

	@Test
	public void testIsPricingConfigurationActiveParametersReturnFalse()
	{
		when(mockPricingConfigParamater.isPricingSupported()).thenReturn(false);
		assertFalse(classUnderTest.isPricingConfigurationActive());
	}

	@Test
	public void testIsPricingConfigurationActiveNoParameters()
	{
		classUnderTest.setPricingConfigurationParameter(null);
		assertFalse(classUnderTest.isPricingConfigurationActive());
	}


}
