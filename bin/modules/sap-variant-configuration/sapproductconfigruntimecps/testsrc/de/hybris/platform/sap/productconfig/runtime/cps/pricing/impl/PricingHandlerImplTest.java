/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.pricing.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.sap.productconfig.runtime.cps.CharonFacade;
import de.hybris.platform.sap.productconfig.runtime.cps.CharonPricingFacade;
import de.hybris.platform.sap.productconfig.runtime.cps.constants.SapproductconfigruntimecpsConstants;
import de.hybris.platform.sap.productconfig.runtime.cps.enums.SAPProductConfigPricingDetailsMode;
import de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service.MasterDataContainerResolver;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataCharacteristicContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataClassContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataKnowledgeBaseContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataProductContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.common.CPSMasterDataKBHeaderInfo;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.pricing.CPSMasterDataVariantPriceKey;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.ConditionPurpose;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.ConditionResult;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingDocumentInput;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingDocumentResult;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.Subtotal;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSCharacteristicGroup;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSItem;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.pricing.CPSValuePrice;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.pricing.CPSValuePriceInfo;
import de.hybris.platform.sap.productconfig.runtime.cps.populator.impl.MasterDataContext;
import de.hybris.platform.sap.productconfig.runtime.cps.pricing.PricingConfigurationParameterCPS;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigModelFactory;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.ContextualConverter;
import de.hybris.platform.sap.productconfig.runtime.interf.CsticQualifier;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ConfigModelFactoryImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ConfigurationRetrievalOptions;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ProductConfigurationDiscount;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceSummaryModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceValueUpdateModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticValueModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.PriceModelImpl;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.Test.None;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.context.ApplicationContext;


/**
 * Unit test for {@link PricingHandlerImpl}
 */
@UnitTest
public class PricingHandlerImplTest
{
	private static final String CAM200 = "CAM200";
	private static final String JPY = "JPY";
	private static final BigDecimal VALUE_100 = new BigDecimal("100.00");
	private static final String EUR = "EUR";
	private static final String VALUE_10 = "VALUE_10";
	private static final String VALUE_12 = "VALUE_12";
	private static final String VALUE_14 = "VALUE_14";
	private static final String CSTIC_07 = "cstic_07";
	private static final String CSTIC_06 = "cstic_06";
	private static final String CSTIC_01 = "cstic_01";
	private static final String CSTIC_ID = "cstic id";
	private static final String KB_ID = "100";
	private static final String TARGET_FOR_SELECTED_OPTIONS = "TARGET_FOR_SELECTED_OPTIONS";
	private static final String TARGET_FOR_BASE_PRICE = "TARGET_FOR_BASE_PRICE";
	private static final String ID = "id";
	private static final String PRODUCT_ID = "productId";
	private static final String CURRENCY = EUR;
	private static final Double NET_VALUE = Double.valueOf(123.45);
	private static final Double ORIGINAL_NET_VALUE = Double.valueOf(173.45);
	private static final Double SELECTED_OPTIONS_PRICE = Double.valueOf(23.45);
	private static final Double BASE_PRICE = Double.valueOf(67.89);
	private static final BigDecimal VALUE_PRICE = BigDecimal.ONE;
	private static final String CLASS_ID = "Class";
	private static final String PRICE_KEY_NOT_KNOWN = "CAM_XXX";

	private static final String CURRENCY_USD = "USD";
	private static final String SUBTOTAL_FOR_BASE_PRICE_FLAG = "1";
	private static final String SUBTOTAL_FOR_SELECTED_OPTIONS_FLAG = "2";
	private static final String ANOTHER_SUBTOTAL_FLAG = "X";
	private static final String SUBTOTAL_BASE_PRICE = "1111.11";
	private static final String SUBTOTAL_SELECTED_OPTIONS = "11.11";

	private static final Collection<String> CONDITIONTYPES_FOR_BASEPRICE_COLLECTION = Arrays.asList("PR00", "PR01", "PR02",
			"PR03");
	private static final Collection<String> CONDITIONTYPES_FOR_SELECTEDOPTIONS_COLLECTION = Arrays.asList("VA00", "VA01");
	private static final Collection<String> ANOTHER_CONDITIONTYPES_COLLECTION = Arrays.asList("XX00", "YY00");

	private static final double PR00_VALUE = 111.11;
	private static final double PR01_VALUE = 222.22;
	private static final double PR02_VALUE = 999.99;
	private static final double PR03_VALUE = 999.99;
	private static final double VA00_VALUE = 3.33;
	private static final double VA01_VALUE = 4.44;


	private static final String CONDITIONTYPE_BASE_PRICE = "333.33";
	private static final String CONDITIONTYPE_SELECTED_OPTIONS = "7.77";

	private Map<CPSMasterDataVariantPriceKey, CPSValuePrice> pricesMap;
	private PricingHandlerImpl classUnderTest;
	private PricingDocumentResult pricingResult;
	private PricingDocumentResult pricingResultForSubtotalsAndConditionTypes;
	private MasterDataContext ctxt;
	private PriceValueUpdateModel cstic;
	private ProductConfigurationDiscount discount;

	@Mock
	private PricingConfigurationParameterCPS pricingConfigurationParameter;
	@Mock
	private ContextualConverter<CPSConfiguration, PricingDocumentInput, ConfigurationRetrievalOptions> pricingDocumentInputConverter;
	@Mock
	private CharonPricingFacade charonPricingFacade;
	@Mock
	private ConfigModelFactory configModelFactory;
	@Mock
	private CPSCharacteristicGroup group;
	@Mock
	private CPSItem item;
	@Mock
	private CPSConfiguration config;
	@Mock
	private ApplicationContext mockApplicationContext;
	@Mock
	private CPSMasterDataCharacteristicContainer masterDataCharacteristic;
	@Mock
	private MasterDataContainerResolver resolver;
	@Mock
	private CommonI18NService commonI18NService;

	@Mock
	private CharonFacade charonFacade;



	Answer<PriceModel> priceModelAnswer = new Answer<PriceModel>()
	{
		@Override
		public PriceModel answer(final InvocationOnMock invocation) throws Throwable
		{
			return new PriceModelImpl();
		}
	};

	Answer<PriceSummaryModel> priceSummaryModelAnswer = new Answer<PriceSummaryModel>()
	{
		@Override
		public PriceSummaryModel answer(final InvocationOnMock invocation) throws Throwable
		{
			return new PriceSummaryModel();
		}
	};
	final private CPSMasterDataKnowledgeBaseContainer masterDataContainer = new CPSMasterDataKnowledgeBaseContainer();
	private final Map<String, CPSMasterDataProductContainer> productMap = new HashMap<>();
	private final Map<String, CPSMasterDataClassContainer> classesMap = new HashMap<>();
	private CPSValuePrice cpsValuePrice;
	private final CPSValuePriceInfo priceInfo = new CPSValuePriceInfo();


	@Before
	public void setup() throws PricingEngineException
	{
		MockitoAnnotations.initMocks(this);
		pricesMap = new HashMap<>();
		Mockito.when(pricingConfigurationParameter.getTargetForSelectedOptions()).thenReturn(TARGET_FOR_SELECTED_OPTIONS);
		Mockito.when(pricingConfigurationParameter.getTargetForBasePrice()).thenReturn(TARGET_FOR_BASE_PRICE);
		Mockito.when(pricingConfigurationParameter.getPricingDetailsMode())
				.thenReturn(SAPProductConfigPricingDetailsMode.CONDITIONFUNCTION);
		final PricingDocumentInput pricingDocumentInput = new PricingDocumentInput();
		pricingResult = fillPricingDocumentResult();
		pricingResultForSubtotalsAndConditionTypes = fillPricingDocumentResultForSubtotalsAndConditionTypes();
		Mockito.when(charonPricingFacade.createPricingDocument(pricingDocumentInput)).thenReturn(pricingResult);
		masterDataContainer.setProducts(productMap);
		productMap.put(PRODUCT_ID, new CPSMasterDataProductContainer());
		masterDataContainer.setClasses(classesMap);
		classesMap.put(CLASS_ID, new CPSMasterDataClassContainer());
		Mockito.when(resolver.getCharacteristic(Mockito.same(masterDataContainer), Mockito.anyString()))
				.thenReturn(masterDataCharacteristic);
		final Set<String> possibleValues = fillPossibleValues();
		Mockito.when(resolver.getSpecificPossibleValueIds(Mockito.same(masterDataContainer), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString())).thenReturn(possibleValues);
		Mockito.when(resolver.getPossibleValueIds(Mockito.same(masterDataContainer), Mockito.anyString()))
				.thenReturn(possibleValues);
		classUnderTest = new PricingHandlerImpl();
		classUnderTest.setPricingConfigurationParameter(pricingConfigurationParameter);
		classUnderTest.setPricingDocumentInputConverter(pricingDocumentInputConverter);
		classUnderTest.setCharonPricingFacade(charonPricingFacade);
		classUnderTest.setConfigModelFactory(configModelFactory);
		classUnderTest.setCommonI18NService(commonI18NService);
		classUnderTest.setCharonFacade(charonFacade);

		Mockito.when(configModelFactory.createInstanceOfPriceModel()).thenAnswer(priceModelAnswer);
		Mockito.when(configModelFactory.getZeroPriceModel()).thenReturn(PriceModel.NO_PRICE);
		Mockito.when(configModelFactory.createInstanceOfPriceSummaryModel()).thenAnswer(priceSummaryModelAnswer);

		cstic = new PriceValueUpdateModel();
		cstic.setCsticQualifier(new CsticQualifier());
		cstic.getCsticQualifier().setCsticName(CSTIC_ID);
		cstic.setSelectedValues(new ArrayList<>());

		ctxt = new MasterDataContext();
		ctxt.setPricingProduct(PRODUCT_ID);
		ctxt.setKbCacheContainer(masterDataContainer);
		classUnderTest.setMasterDataResolver(resolver);
		final CPSMasterDataKBHeaderInfo headerInfo = new CPSMasterDataKBHeaderInfo();
		headerInfo.setId(Integer.valueOf(KB_ID));
		masterDataContainer.setHeaderInfo(headerInfo);

		cpsValuePrice = new CPSValuePrice();
		cpsValuePrice.setCurrency(EUR);
		cpsValuePrice.setValuePrice(VALUE_100);

		discount = new ProductConfigurationDiscount();
		discount.setCsticName(CSTIC_01);
		discount.setCsticValueName(VALUE_10);
		discount.setDiscount(BigDecimal.valueOf(40));

		final CurrencyModel eurCur = new CurrencyModel();
		eurCur.setDigits(2);
		given(commonI18NService.getCurrency(EUR)).willReturn(eurCur);

		final CurrencyModel jpyCur = new CurrencyModel();
		jpyCur.setDigits(0);
		given(commonI18NService.getCurrency(JPY)).willReturn(jpyCur);
	}

	private PricingDocumentResult fillPricingDocumentResult()
	{
		final PricingDocumentResult pricingResult = new PricingDocumentResult();
		pricingResult.setNetValue(NET_VALUE);
		pricingResult.setDocumentCurrencyUnit(CURRENCY);
		pricingResult.setConditionsWithPurpose(new ArrayList<>());
		final ConditionPurpose condPurposeBase = new ConditionPurpose();
		condPurposeBase.setPurpose(pricingConfigurationParameter.getTargetForBasePrice());
		condPurposeBase.setValue(BASE_PRICE);
		pricingResult.getConditionsWithPurpose().add(condPurposeBase);
		final ConditionPurpose condPurposeOptions = new ConditionPurpose();
		condPurposeOptions.setPurpose(pricingConfigurationParameter.getTargetForSelectedOptions());
		condPurposeOptions.setValue(SELECTED_OPTIONS_PRICE);
		pricingResult.getConditionsWithPurpose().add(condPurposeOptions);
		return pricingResult;
	}

	private PricingDocumentResult createOrginalPricingResult()
	{
		final PricingDocumentResult originalPricingResult = new PricingDocumentResult();
		originalPricingResult.setNetValue(ORIGINAL_NET_VALUE);
		originalPricingResult.setDocumentCurrencyUnit(CURRENCY);
		originalPricingResult.setConditionsWithPurpose(new ArrayList<>());
		final ConditionPurpose condPurposeBase = new ConditionPurpose();
		condPurposeBase.setPurpose(pricingConfigurationParameter.getTargetForBasePrice());
		condPurposeBase.setValue(BASE_PRICE);
		originalPricingResult.getConditionsWithPurpose().add(condPurposeBase);
		final ConditionPurpose condPurposeOptions = new ConditionPurpose();
		condPurposeOptions.setPurpose(pricingConfigurationParameter.getTargetForSelectedOptions());
		condPurposeOptions.setValue(SELECTED_OPTIONS_PRICE);
		originalPricingResult.getConditionsWithPurpose().add(condPurposeOptions);
		return originalPricingResult;
	}

	private void fillPricesMap()
	{
		pricesMap.put(createPriceKey(PRODUCT_ID, "CAM500"), createCPSValuePrice(100));
		pricesMap.put(createPriceKey(PRODUCT_ID, CAM200), createCPSValuePrice(50));
		pricesMap.put(createPriceKey(PRODUCT_ID, "CAM700"), createCPSValuePrice(700));
		ctxt.setValuePricesMap(pricesMap);
	}

	protected CPSValuePrice createCPSValuePrice(final double valuePrice)
	{
		final CPSValuePrice valuePriceObject = new CPSValuePrice();
		valuePriceObject.setValuePrice(BigDecimal.valueOf(valuePrice));
		valuePriceObject.setCurrency(CURRENCY);
		return valuePriceObject;
	}

	protected CPSMasterDataVariantPriceKey createPriceKey(final String productId, final String pricingKey)
	{
		final CPSMasterDataVariantPriceKey priceKey = new CPSMasterDataVariantPriceKey();
		priceKey.setProductId(productId);
		priceKey.setVariantConditionKey(pricingKey);
		return priceKey;
	}

	@Test
	public void testGetValuePrice() throws PricingEngineException
	{
		Mockito.when(resolver.getValuePricingKey(Mockito.same(masterDataContainer), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString())).thenReturn("CAM700");
		fillPricesMap();
		final CPSValuePrice price = classUnderTest.getValuePrice(ctxt, PRODUCT_ID, "characteristicId", "valueId");
		final double valuePrice = 700;
		assertEquals(BigDecimal.valueOf(valuePrice), price.getValuePrice());
	}

	@Test
	public void testGetValuePrice_pricingKeyNull() throws PricingEngineException
	{
		final CPSValuePrice price = classUnderTest.getValuePrice(ctxt, PRODUCT_ID, "characteristicId", "valueId");
		assertNull(price);
	}


	@Test
	public void testIsValueSelectedWithNullListOfValuesReturnsFalse()
	{
		final List<CsticValueModel> cpsValues = null;
		final String value = CSTIC_01;
		assertFalse(classUnderTest.isValueSelected(value, cpsValues));
	}

	@Test
	public void testIsValueSelectedWithEmptyListOfValuesReturnsFalse()
	{
		final List<CsticValueModel> cpsValues = new ArrayList<>();
		final String value = CSTIC_01;
		assertFalse(classUnderTest.isValueSelected(value, cpsValues));
	}

	@Test
	public void testIsValueSelectedWithListOfValuesReturnsFalse()
	{
		final List<CsticValueModel> cpsValues = new ArrayList<>();
		cpsValues.add(createCsticValueModel("cstic_04"));

		final String value = CSTIC_01;
		assertFalse(classUnderTest.isValueSelected(value, cpsValues));
	}

	@Test
	public void testIsValueSelectedWithListOfNullValueReturnsFalse()
	{
		final List<CsticValueModel> cpsValues = new ArrayList<>();
		cpsValues.add(createCsticValueModel(null));

		final String value = CSTIC_01;
		assertFalse(classUnderTest.isValueSelected(value, cpsValues));
	}

	private CsticValueModel createCsticValueModel(final String csticValueId)
	{
		final CsticValueModel csticValue = new CsticValueModelImpl();
		csticValue.setName(csticValueId);
		return csticValue;
	}

	@Test
	public void testIsValueSelectedWithListOfWrongValuesReturnsFalse()
	{
		final List<CsticValueModel> cpsValues = new ArrayList<>();
		cpsValues.add(createCsticValueModel("cstic_02"));
		cpsValues.add(createCsticValueModel("cstic_04"));
		cpsValues.add(createCsticValueModel(CSTIC_06));

		final String value = CSTIC_01;
		assertFalse(classUnderTest.isValueSelected(value, cpsValues));
	}

	@Test
	public void testIsValueSelectedWithListOfValuesReturnsTrue()
	{
		final List<CsticValueModel> cpsValues = new ArrayList<>();
		cpsValues.add(createCsticValueModel(CSTIC_01));
		cpsValues.add(createCsticValueModel("cstic_03"));
		cpsValues.add(createCsticValueModel("cstic_04"));
		cpsValues.add(createCsticValueModel(CSTIC_06));

		final String value = "cstic_04";
		assertTrue(classUnderTest.isValueSelected(value, cpsValues));
	}

	@Test
	public void testHandleSelectedValuePriceForSingleValueReturnNull()
	{
		final boolean isMultiValued = false;
		final String possibleValue = CSTIC_01;
		final BigDecimal valuePrice = BigDecimal.valueOf(300);
		final List<String> cpsValues = new ArrayList<>();
		final BigDecimal oldSelectedValuePrice = null;

		final BigDecimal price = classUnderTest.updateSelectedValuePrice(isMultiValued, possibleValue, valuePrice, cpsValues,
				oldSelectedValuePrice);
		assertNull(price);
	}

	@Test
	public void testHandleSelectedValuePriceForSingleValueReturnPrice()
	{
		final boolean isMultiValued = false;
		final String possibleValue = CSTIC_01;
		final BigDecimal valuePrice = BigDecimal.valueOf(300);
		final List<String> cpsValues = new ArrayList<>();
		final BigDecimal oldSelectedValuePrice = null;
		cpsValues.add(CSTIC_01);

		final BigDecimal price = classUnderTest.updateSelectedValuePrice(isMultiValued, possibleValue, valuePrice, cpsValues,
				oldSelectedValuePrice);
		assertNotNull(price);
		assertEquals(valuePrice, price);
	}

	@Test
	public void testHandleSelectedValuePriceForMultiValueReturnNull()
	{
		final boolean isMultiValued = true;
		final String possibleValue = CSTIC_01;
		final BigDecimal valuePrice = BigDecimal.valueOf(300);
		final List<String> cpsValues = new ArrayList<>();
		final BigDecimal oldSelectedValuePrice = null;
		cpsValues.add(CSTIC_01);

		final BigDecimal price = classUnderTest.updateSelectedValuePrice(isMultiValued, possibleValue, valuePrice, cpsValues,
				oldSelectedValuePrice);
		assertNull(price);
	}


	@Test
	public void testHandleSelectedValuePriceFor_SingleValue_selectedValuePriceAlreadySet()
	{
		final boolean isMultiValued = false;
		final String possibleValue = CSTIC_01;
		final BigDecimal valuePrice = BigDecimal.valueOf(300);
		final List<String> cpsValues = new ArrayList<>();
		final BigDecimal oldSelectedValuePrice = BigDecimal.valueOf(400);
		cpsValues.add(CSTIC_01);

		final BigDecimal price = classUnderTest.updateSelectedValuePrice(isMultiValued, possibleValue, valuePrice, cpsValues,
				oldSelectedValuePrice);
		assertNotNull(price);
		assertEquals(oldSelectedValuePrice, price);

	}

	@Test
	public void testCalculateDeltaPriceWithSavings()
	{
		final BigDecimal selectedValuePrice = BigDecimal.valueOf(100);

		final Map<String, CPSValuePriceInfo> mapValuePriceInfo = new HashMap<>();
		mapValuePriceInfo.put(CSTIC_07, createValuePriceInfo(BigDecimal.valueOf(250), BigDecimal.valueOf(300)));

		classUnderTest.calculateDeltaPrices(selectedValuePrice, mapValuePriceInfo);
		assertEquals(BigDecimal.valueOf(150), mapValuePriceInfo.get(CSTIC_07).getDeltaPrice().getValuePrice());
		assertEquals(BigDecimal.valueOf(50), mapValuePriceInfo.get(CSTIC_07).getDeltaPrice().getObsoleteValuePrice());
	}

	@Test
	public void testCalculateSavings()
	{
		final BigDecimal selectedValuePrice = null;
		final Map<String, CPSValuePriceInfo> mapValuePriceInfo = new HashMap<>();
		mapValuePriceInfo.put(CSTIC_07, createValuePriceInfo(BigDecimal.valueOf(250), BigDecimal.valueOf(300)));

		classUnderTest.calculateDeltaPrices(selectedValuePrice, mapValuePriceInfo);
		assertEquals(BigDecimal.valueOf(250), mapValuePriceInfo.get(CSTIC_07).getDeltaPrice().getValuePrice());
		assertEquals(BigDecimal.valueOf(50), mapValuePriceInfo.get(CSTIC_07).getDeltaPrice().getObsoleteValuePrice());
	}

	@Test
	public void testCalculateDeltaPriceWithSelectedValuePriceSet_300()
	{
		final BigDecimal selectedValuePrice = BigDecimal.valueOf(300);

		final Map<String, CPSValuePriceInfo> mapValuePriceInfo = new HashMap<>();
		mapValuePriceInfo.put(CSTIC_01, createValuePriceInfo(BigDecimal.valueOf(300)));
		mapValuePriceInfo.put(CSTIC_06, createValuePriceInfo(null));
		mapValuePriceInfo.put(CSTIC_07, createValuePriceInfo(null));

		classUnderTest.calculateDeltaPrices(selectedValuePrice, mapValuePriceInfo);
		assertNull(mapValuePriceInfo.get(CSTIC_07).getDeltaPrice());
	}

	@Test
	public void testCalculateDeltaPriceWithSelectedValuePriceSet_100()
	{
		final BigDecimal selectedValuePrice = BigDecimal.valueOf(100);

		final Map<String, CPSValuePriceInfo> mapValuePriceInfo = new HashMap<>();
		mapValuePriceInfo.put(CSTIC_01, createValuePriceInfo(null));
		mapValuePriceInfo.put(CSTIC_06, createValuePriceInfo(null));
		mapValuePriceInfo.put(CSTIC_07, createValuePriceInfo(BigDecimal.valueOf(400)));

		classUnderTest.calculateDeltaPrices(selectedValuePrice, mapValuePriceInfo);
		assertEquals(BigDecimal.valueOf(300), mapValuePriceInfo.get(CSTIC_07).getDeltaPrice().getValuePrice());
	}

	protected Set<String> fillPossibleValues()
	{
		final Set<String> possibleValues = new HashSet<>();
		possibleValues.add(CSTIC_01);
		possibleValues.add(CSTIC_06);
		possibleValues.add(CSTIC_07);
		return possibleValues;
	}

	@Test
	public void testCalculateDeltaPriceWithSelectedValuePriceNull()
	{
		final BigDecimal selectedValuePrice = null;

		final Map<String, CPSValuePriceInfo> mapValuePriceInfo = new HashMap<>();
		mapValuePriceInfo.put(CSTIC_01, createValuePriceInfo(null));
		mapValuePriceInfo.put(CSTIC_06, createValuePriceInfo(null));
		mapValuePriceInfo.put(CSTIC_07, createValuePriceInfo(BigDecimal.valueOf(400)));

		classUnderTest.calculateDeltaPrices(selectedValuePrice, mapValuePriceInfo);
		assertEquals(BigDecimal.valueOf(400), mapValuePriceInfo.get(CSTIC_07).getDeltaPrice().getValuePrice());
	}

	@Test
	public void testCalculateDeltaPriceWith_valuePriceNull()
	{
		final BigDecimal selectedValuePrice = null;

		final Map<String, CPSValuePriceInfo> mapValuePriceInfo = new HashMap<>();
		final CPSValuePriceInfo priceInfo = createValuePriceInfo(null);
		priceInfo.setValuePrice(null);
		mapValuePriceInfo.put(CSTIC_01, priceInfo);
		mapValuePriceInfo.put(CSTIC_06, createValuePriceInfo(null));
		mapValuePriceInfo.put(CSTIC_07, createValuePriceInfo(BigDecimal.valueOf(400)));

		classUnderTest.calculateDeltaPrices(selectedValuePrice, mapValuePriceInfo);
		assertEquals(BigDecimal.valueOf(400), mapValuePriceInfo.get(CSTIC_07).getDeltaPrice().getValuePrice());
	}

	protected CPSValuePriceInfo createValuePriceInfo(final BigDecimal price)
	{
		return createValuePriceInfo(price, null);
	}


	protected CPSValuePriceInfo createValuePriceInfo(final BigDecimal price, final BigDecimal obsoletePrice)
	{
		final CPSValuePriceInfo valuePriceInfo = new CPSValuePriceInfo();
		final CPSValuePrice valuePrice = new CPSValuePrice();
		valuePrice.setValuePrice(price);
		valuePrice.setObsoleteValuePrice(obsoletePrice);
		valuePrice.setCurrency(CURRENCY);
		valuePriceInfo.setValuePrice(valuePrice);
		return valuePriceInfo;
	}

	protected CPSValuePriceInfo createValuePriceInfoWithDelta(final BigDecimal price, final BigDecimal delta)
	{
		final CPSValuePriceInfo valuePriceInfo = new CPSValuePriceInfo();
		final CPSValuePrice valuePrice = new CPSValuePrice();
		valuePrice.setValuePrice(price);
		valuePrice.setCurrency(CURRENCY);
		valuePriceInfo.setValuePrice(valuePrice);

		final CPSValuePrice deltaPrice = new CPSValuePrice();
		deltaPrice.setValuePrice(delta);
		deltaPrice.setCurrency(CURRENCY);
		valuePriceInfo.setDeltaPrice(deltaPrice);

		return valuePriceInfo;
	}

	protected CsticValueModel createPossibleValue(final String possibleValueId)
	{
		final CsticValueModel possibleValue = new CsticValueModelImpl();
		possibleValue.setName(possibleValueId);
		return possibleValue;
	}

	@Test
	public void testCreatePriceModelFromCPSValue()
	{
		final CPSValuePrice input = new CPSValuePrice();
		input.setValuePrice(VALUE_PRICE);
		input.setCurrency(CURRENCY);
		final PriceModel priceModel = classUnderTest.createPriceModelFromCPSValue(input);
		assertNotNull(priceModel);
		assertEquals(VALUE_PRICE, priceModel.getPriceValue());
	}

	@Test
	public void testCreatePriceModelNull()
	{
		final PriceModel priceModel = classUnderTest.createPriceModelFromCPSValue(null);
		assertNotNull(priceModel);
		assertEquals(PriceModel.NO_PRICE, priceModel);
	}

	@Test
	public void testGetSelectedValuePriceAndValuePricesMap() throws PricingEngineException
	{
		Mockito.when(resolver.getValuePricingKey(Mockito.same(masterDataContainer), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString())).thenReturn("CAM700");
		fillPricesMap();
		Mockito.when(Boolean.valueOf(masterDataCharacteristic.isMultiValued())).thenReturn(Boolean.FALSE);
		cstic.getCsticQualifier().setInstanceName(PRODUCT_ID);
		final Pair<BigDecimal, Map<String, CPSValuePriceInfo>> pair = classUnderTest.getSelectedValuePriceAndValuePricesMap(ctxt,
				cstic);
		final Map<String, CPSValuePriceInfo> mapValuePriceInfo = pair.getRight();
		assertNull(pair.getLeft());
		assertEquals(3, mapValuePriceInfo.size());
		assertEquals(BigDecimal.valueOf(700.00), mapValuePriceInfo.get(CSTIC_01).getValuePrice().getValuePrice());
		assertEquals(CURRENCY, mapValuePriceInfo.get(CSTIC_01).getValuePrice().getCurrency());
	}

	@Test
	public void testGetSelectedValuePriceAndValuePricesMapKeyNotKnown() throws PricingEngineException
	{
		fillPricesMap();
		Mockito.when(Boolean.valueOf(masterDataCharacteristic.isMultiValued())).thenReturn(Boolean.FALSE);
		cstic.getCsticQualifier().setInstanceName(PRODUCT_ID);
		final Pair<BigDecimal, Map<String, CPSValuePriceInfo>> pair = classUnderTest.getSelectedValuePriceAndValuePricesMap(ctxt,
				cstic);
		final Map<String, CPSValuePriceInfo> mapValuePriceInfo = pair.getRight();
		assertNull(pair.getLeft());
		assertEquals(0, mapValuePriceInfo.size());
	}

	@Test
	public void testGetSelectedValuePriceAndValuePricesMap_onlySomeValuesHavePrices() throws PricingEngineException
	{

		Mockito.when(Boolean.valueOf(masterDataCharacteristic.isMultiValued())).thenReturn(Boolean.FALSE);
		Mockito.when(resolver.getSpecificPossibleValueIds(Mockito.same(masterDataContainer), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString())).thenReturn(new HashSet<String>(Arrays.asList(CSTIC_01)));
		Mockito.when(resolver.getValuePricingKey(Mockito.same(masterDataContainer), Mockito.anyString(), Mockito.anyString(),
				Mockito.eq(CSTIC_01))).thenReturn("CAM700");
		fillPricesMap();
		cstic.getCsticQualifier().setInstanceName(PRODUCT_ID);
		final Pair<BigDecimal, Map<String, CPSValuePriceInfo>> pair = classUnderTest.getSelectedValuePriceAndValuePricesMap(ctxt,
				cstic);
		final Map<String, CPSValuePriceInfo> mapValuePriceInfo = pair.getRight();
		assertNull(pair.getLeft());
		assertEquals(3, mapValuePriceInfo.size());
		assertEquals(BigDecimal.valueOf(700.00), mapValuePriceInfo.get(CSTIC_01).getValuePrice().getValuePrice());
		assertEquals(CURRENCY, mapValuePriceInfo.get(CSTIC_01).getValuePrice().getCurrency());
		assertEquals(BigDecimal.ZERO, mapValuePriceInfo.get(CSTIC_06).getValuePrice().getValuePrice());
		assertEquals(CURRENCY, mapValuePriceInfo.get(CSTIC_06).getValuePrice().getCurrency());
		assertEquals(BigDecimal.ZERO, mapValuePriceInfo.get(CSTIC_07).getValuePrice().getValuePrice());
		assertEquals(CURRENCY, mapValuePriceInfo.get(CSTIC_07).getValuePrice().getCurrency());
	}

	@Test
	public void testGetSelectedValuePriceAndValuePricesMap_NoValuePrices() throws PricingEngineException
	{
		Mockito.when(Boolean.valueOf(masterDataCharacteristic.isMultiValued())).thenReturn(Boolean.FALSE);
		cstic.getCsticQualifier().setInstanceName(PRODUCT_ID);
		final Pair<BigDecimal, Map<String, CPSValuePriceInfo>> pair = classUnderTest.getSelectedValuePriceAndValuePricesMap(ctxt,
				cstic);
		final Map<String, CPSValuePriceInfo> mapValuePriceInfo = pair.getRight();
		assertNull(pair.getLeft());
		assertEquals(0, mapValuePriceInfo.size());
	}

	@Test
	public void testGetSelectedValuePriceAndValuePricesMap_checkMap() throws PricingEngineException
	{
		fillPricesMap();

		Mockito.when(resolver.getValuePricingKey(Mockito.same(masterDataContainer), Mockito.anyString(), Mockito.anyString(),
				Mockito.eq(VALUE_10))).thenReturn(CAM200);
		Mockito.when(resolver.getValuePricingKey(Mockito.same(masterDataContainer), Mockito.anyString(), Mockito.anyString(),
				Mockito.eq(VALUE_12))).thenReturn("CAM700");
		Mockito.when(resolver.getValuePricingKey(Mockito.same(masterDataContainer), Mockito.anyString(), Mockito.anyString(),
				Mockito.eq(VALUE_14))).thenReturn("CAM500");
		final Set<String> possibleValues = new HashSet<>();
		possibleValues.add(VALUE_10);
		possibleValues.add(VALUE_12);
		possibleValues.add(VALUE_14);

		Mockito.when(resolver.getPossibleValueIds(Mockito.same(masterDataContainer), Mockito.anyString()))
				.thenReturn(possibleValues);

		Mockito.when(resolver.getSpecificPossibleValueIds(Mockito.same(masterDataContainer), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString())).thenReturn(possibleValues);

		Mockito.when(Boolean.valueOf(masterDataCharacteristic.isMultiValued())).thenReturn(Boolean.FALSE);
		cstic.getCsticQualifier().setInstanceName(PRODUCT_ID);
		final Pair<BigDecimal, Map<String, CPSValuePriceInfo>> pair = classUnderTest.getSelectedValuePriceAndValuePricesMap(ctxt,
				cstic);
		final Map<String, CPSValuePriceInfo> mapValuePriceInfo = pair.getRight();

		assertEquals(3, mapValuePriceInfo.size());
		assertEquals(BigDecimal.valueOf(50.00), mapValuePriceInfo.get(VALUE_10).getValuePrice().getValuePrice());
		assertEquals(BigDecimal.valueOf(700.00), mapValuePriceInfo.get(VALUE_12).getValuePrice().getValuePrice());
		assertEquals(BigDecimal.valueOf(100.00), mapValuePriceInfo.get(VALUE_14).getValuePrice().getValuePrice());
	}



	@Test
	public void testGetSelectedValuePriceAndValuePricesMapNoPriceForFirstSpecificPossibleValue() throws PricingEngineException
	{
		fillPricesMap();
		pricesMap.remove(createPriceKey(PRODUCT_ID, CAM200));
		Mockito.when(resolver.getValuePricingKey(Mockito.same(masterDataContainer), Mockito.anyString(), Mockito.anyString(),
				Mockito.eq(VALUE_10))).thenReturn(CAM200);
		Mockito.when(resolver.getValuePricingKey(Mockito.same(masterDataContainer), Mockito.anyString(), Mockito.anyString(),
				Mockito.eq(VALUE_12))).thenReturn("CAM700");
		Mockito.when(resolver.getValuePricingKey(Mockito.same(masterDataContainer), Mockito.anyString(), Mockito.anyString(),
				Mockito.eq(VALUE_14))).thenReturn("CAM500");
		final Set<String> possibleValues = new HashSet<>();
		possibleValues.add(VALUE_10);
		possibleValues.add(VALUE_12);
		possibleValues.add(VALUE_14);

		Mockito.when(resolver.getPossibleValueIds(Mockito.same(masterDataContainer), Mockito.anyString()))
				.thenReturn(possibleValues);

		Mockito.when(resolver.getSpecificPossibleValueIds(Mockito.same(masterDataContainer), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString())).thenReturn(possibleValues);

		Mockito.when(Boolean.valueOf(masterDataCharacteristic.isMultiValued())).thenReturn(Boolean.FALSE);
		cstic.getCsticQualifier().setInstanceName(PRODUCT_ID);
		final Pair<BigDecimal, Map<String, CPSValuePriceInfo>> pair = classUnderTest.getSelectedValuePriceAndValuePricesMap(ctxt,
				cstic);
		final Map<String, CPSValuePriceInfo> mapValuePriceInfo = pair.getRight();

		assertEquals(3, mapValuePriceInfo.size());
		assertEquals(BigDecimal.ZERO, mapValuePriceInfo.get(VALUE_10).getValuePrice().getValuePrice());
		assertEquals(BigDecimal.valueOf(700.00), mapValuePriceInfo.get(VALUE_12).getValuePrice().getValuePrice());
		assertEquals(BigDecimal.valueOf(100.00), mapValuePriceInfo.get(VALUE_14).getValuePrice().getValuePrice());
	}

	@Test
	public void testGetCurrencyForValuePrice()
	{

		Mockito.when(resolver.getValuePricingKey(Mockito.same(masterDataContainer), Mockito.anyString(), Mockito.anyString(),
				Mockito.eq(CSTIC_01))).thenReturn("CAM700");

		fillPricesMap();

		cstic.getCsticQualifier().setInstanceName(PRODUCT_ID);

		final String result = classUnderTest.getCurrencyForValuePrice(ctxt, PRODUCT_ID, CSTIC_ID, CSTIC_01);

		assertNotNull(result);
		assertEquals(CURRENCY, result);
	}

	@Test
	public void testGetCurrencyForValuePriceNoPriceSet()
	{

		Mockito.when(resolver.getValuePricingKey(Mockito.same(masterDataContainer), Mockito.anyString(), Mockito.anyString(),
				Mockito.eq(CSTIC_01))).thenReturn("CAM700");

		fillPricesMap();
		pricesMap.remove(createPriceKey(PRODUCT_ID, "CAM700"));
		cstic.getCsticQualifier().setInstanceName(PRODUCT_ID);

		final String result = classUnderTest.getCurrencyForValuePrice(ctxt, PRODUCT_ID, CSTIC_ID, CSTIC_01);

		assertNull(result);

	}


	@Test
	public void testCheckSpecificPossibleValuesForCurrency()
	{
		Mockito.when(resolver.getValuePricingKey(Mockito.same(masterDataContainer), Mockito.anyString(), Mockito.anyString(),
				Mockito.eq(CSTIC_01))).thenReturn("CAM700");

		fillPricesMap();

		cstic.getCsticQualifier().setInstanceName(PRODUCT_ID);

		final Set<String> possibleValues = classUnderTest.getMasterDataResolver().getPossibleValueIds(masterDataContainer,
				CSTIC_ID);
		final String result = classUnderTest.checkSpecificPossibleValuesForCurrency(possibleValues, ctxt, PRODUCT_ID, CSTIC_ID);

		assertNotNull(result);
		assertEquals(CURRENCY, result);
	}

	@Test
	public void testCheckSpecificPossibleValuesForCurrencyNoPriceForFirstEntry()
	{
		Mockito.when(resolver.getValuePricingKey(Mockito.same(masterDataContainer), Mockito.anyString(), Mockito.anyString(),
				Mockito.eq(CSTIC_01))).thenReturn("CAM700");

		fillPricesMap();

		pricesMap.remove(createPriceKey(PRODUCT_ID, CAM200));
		cstic.getCsticQualifier().setInstanceName(PRODUCT_ID);

		final Set<String> possibleValues = classUnderTest.getMasterDataResolver().getPossibleValueIds(masterDataContainer,
				CSTIC_ID);
		final String result = classUnderTest.checkSpecificPossibleValuesForCurrency(possibleValues, ctxt, PRODUCT_ID, CSTIC_ID);

		assertNotNull(result);
		assertEquals(CURRENCY, result);
	}

	@Test
	public void testCheckSpecificPossibleValuesForCurrencyNoPriceFound()
	{
		Mockito.when(resolver.getValuePricingKey(Mockito.same(masterDataContainer), Mockito.anyString(), Mockito.anyString(),
				Mockito.eq(CSTIC_01))).thenReturn("CAM700");

		fillPricesMap();

		pricesMap.remove(createPriceKey(PRODUCT_ID, CAM200));
		pricesMap.remove(createPriceKey(PRODUCT_ID, "CAM500"));
		pricesMap.remove(createPriceKey(PRODUCT_ID, "CAM700"));
		cstic.getCsticQualifier().setInstanceName(PRODUCT_ID);

		final Set<String> possibleValues = classUnderTest.getMasterDataResolver().getPossibleValueIds(masterDataContainer,
				CSTIC_ID);
		final String result = classUnderTest.checkSpecificPossibleValuesForCurrency(possibleValues, ctxt, PRODUCT_ID, CSTIC_ID);

		assertNull(result);
	}


	@Test
	public void testGetConfigModelFactoryNotNull()
	{
		final PricingHandlerImpl pricingHandler = Mockito.spy(new PricingHandlerImpl());
		pricingHandler.setConfigModelFactory(new ConfigModelFactoryImpl());
		final ConfigModelFactory configModelFactory = pricingHandler.getConfigModelFactory();
		assertNotNull(configModelFactory);
		assertTrue(configModelFactory instanceof ConfigModelFactoryImpl);
	}

	@Test
	public void testCreatePriceModel()
	{
		final PriceModel result = classUnderTest.createPriceModel(CURRENCY, NET_VALUE);
		assertNotNull(result);
		assertEquals(CURRENCY, result.getCurrency());
		assertEquals(BigDecimal.valueOf(NET_VALUE.doubleValue()), result.getPriceValue());

	}

	@Test
	public void testFillValuePrices() throws PricingEngineException
	{
		cstic.getCsticQualifier().setInstanceName(PRODUCT_ID);
		Mockito.when(resolver.getValuePricingKey(Mockito.same(masterDataContainer), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString())).thenReturn("CAM700");
		fillPricesMap();
		cstic.setSelectedValues(new ArrayList<>());
		cstic.getSelectedValues().add(CSTIC_01);
		classUnderTest.fillValuePrices(ctxt, cstic);
		assertNotNull(cstic.getValuePrices());
		assertEquals(3, cstic.getValuePrices().size());
		for (final PriceModel deltaPrice : cstic.getValuePrices().values())
		{
			assertNotNull(deltaPrice);
		}
	}


	@Test(expected = None.class)
	public void testFillValueAndDeltaPricesCsticNull() throws PricingEngineException
	{
		//Test: No exception happens
		classUnderTest.fillValuePrices(ctxt, (PriceValueUpdateModel) null);
	}

	@Test
	public void testGetValuePrices() throws PricingEngineException
	{
		cstic.getCsticQualifier().setInstanceName(PRODUCT_ID);
		fillPricesMap();
		final Map<String, CPSValuePriceInfo> result = classUnderTest.getValuePrices(ctxt, cstic);
		assertNotNull(result);
	}

	@Test
	public void testFillPriceInfos_ShowDelta()
	{
		final Map<String, CPSValuePriceInfo> mapValuePriceInfo = new HashMap<>();
		mapValuePriceInfo.put(VALUE_10, createValuePriceInfoWithDelta(BigDecimal.valueOf(400), BigDecimal.valueOf(300)));
		mapValuePriceInfo.put(VALUE_12, createValuePriceInfoWithDelta(BigDecimal.valueOf(600), BigDecimal.valueOf(500)));

		final PriceValueUpdateModel updateModel = new PriceValueUpdateModel();
		classUnderTest.fillPriceInfos(mapValuePriceInfo, updateModel, true);
		assertNotNull(updateModel.getValuePrices());
		assertEquals(BigDecimal.valueOf(300), updateModel.getValuePrices().get(VALUE_10).getPriceValue());
		assertEquals(BigDecimal.valueOf(500), updateModel.getValuePrices().get(VALUE_12).getPriceValue());
	}

	@Test
	public void testFillPriceInfos_ShowAbsolute()
	{
		final Map<String, CPSValuePriceInfo> mapValuePriceInfo = new HashMap<>();
		mapValuePriceInfo.put(VALUE_10, createValuePriceInfoWithDelta(BigDecimal.valueOf(400), BigDecimal.valueOf(300)));
		mapValuePriceInfo.put(VALUE_12, createValuePriceInfoWithDelta(BigDecimal.valueOf(600), BigDecimal.valueOf(500)));

		final PriceValueUpdateModel updateModel = new PriceValueUpdateModel();
		classUnderTest.fillPriceInfos(mapValuePriceInfo, updateModel, false);
		assertNotNull(updateModel.getValuePrices());
		assertEquals(BigDecimal.valueOf(400), updateModel.getValuePrices().get(VALUE_10).getPriceValue());
		assertEquals(BigDecimal.valueOf(600), updateModel.getValuePrices().get(VALUE_12).getPriceValue());
		assertFalse(updateModel.isShowDeltaPrices());
	}

	@Test
	public void testFillPriceInfosPriceNull()
	{
		final Map<String, CPSValuePriceInfo> mapValuePriceInfo = new HashMap<>();
		mapValuePriceInfo.put(VALUE_10, null);

		final PriceValueUpdateModel updateModel = new PriceValueUpdateModel();
		classUnderTest.fillPriceInfos(mapValuePriceInfo, updateModel, true);
		assertNotNull(updateModel.getValuePrices());
		assertEquals(PriceModel.NO_PRICE, updateModel.getValuePrices().get(VALUE_10));
		assertTrue(updateModel.isShowDeltaPrices());
	}

	@Test
	public void testFillPriceInfosNotFound()
	{
		final Map<String, CPSValuePriceInfo> mapValuePriceInfo = new HashMap<>();

		final PriceValueUpdateModel updateModel = new PriceValueUpdateModel();
		classUnderTest.fillPriceInfos(mapValuePriceInfo, updateModel, true);
		assertNotNull(updateModel.getValuePrices());
		assertNull(updateModel.getValuePrices().get(VALUE_10));
		assertTrue(updateModel.isShowDeltaPrices());
	}

	@Test
	public void testCreateUpdateModelWithoutSelectedValues()
	{
		final CsticModel csticModel = new CsticModelImpl();
		csticModel.setInstanceName("instance name");
		csticModel.setName("cstic name");
		final PriceValueUpdateModel result = classUnderTest.createUpdateModel(csticModel, false);
		assertNotNull(result);
		assertNotNull(result.getCsticQualifier());
		assertNotNull(result.getSelectedValues());
		assertNull(result.getValuePrices());
		assertEquals("instance name", result.getCsticQualifier().getInstanceName());
		assertEquals("cstic name", result.getCsticQualifier().getCsticName());
		assertTrue(result.getSelectedValues().isEmpty());
	}

	@Test
	public void testCreateUpdateModelWithSelectedValues()
	{
		final CsticModel csticModel = new CsticModelImpl();
		csticModel.setInstanceName("instance name");
		csticModel.setName("cstic name");
		final ArrayList<CsticValueModel> assignedValues = new ArrayList<>();
		final CsticValueModelImpl value1 = new CsticValueModelImpl();
		value1.setName("value1");
		assignedValues.add(value1);
		final CsticValueModelImpl value2 = new CsticValueModelImpl();
		value2.setName("value2");
		assignedValues.add(value2);
		csticModel.setAssignedValues(assignedValues);
		final PriceValueUpdateModel result = classUnderTest.createUpdateModel(csticModel, true);
		assertNotNull(result);
		assertNotNull(result.getCsticQualifier());
		assertNotNull(result.getSelectedValues());
		assertNull(result.getValuePrices());
		assertEquals("instance name", result.getCsticQualifier().getInstanceName());
		assertEquals("cstic name", result.getCsticQualifier().getCsticName());
		assertEquals(2, result.getSelectedValues().size());
		assertTrue(result.getSelectedValues().contains("value1"));
		assertTrue(result.getSelectedValues().contains("value2"));
	}


	protected CsticModel createCsticModel(final String cstic, final String value1, final String value2, final String value3)
	{
		final CsticModel cstic1 = new CsticModelImpl();
		final List<CsticValueModel> assignedValues = new ArrayList<>();
		assignedValues.add(createCsticValueModel(value1));
		assignedValues.add(createCsticValueModel(value2));
		assignedValues.add(createCsticValueModel(value3));
		cstic1.setAssignedValuesWithoutCheckForChange(assignedValues);
		cstic1.setName(cstic);
		return cstic1;
	}

	@Test
	public void testFillValuePricesFromContext() throws PricingEngineException
	{
		Mockito.when(resolver.getValuePricingKey(Mockito.same(masterDataContainer), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString())).thenReturn("CAM700");
		fillPricesMap();
		final CsticModel csticModel = createCsticModel(CSTIC_01, CSTIC_01, CSTIC_07, CSTIC_06);
		csticModel.setInstanceName(PRODUCT_ID);
		final CsticValueModel csticValueModel = csticModel.getAssignedValues().get(0);
		assertEquals(PriceModel.NO_PRICE, csticValueModel.getValuePrice());
		classUnderTest.fillValuePrices(ctxt, csticModel);
		assertNotEquals(PriceModel.NO_PRICE, csticValueModel.getValuePrice());
	}

	@Test
	public void testFillValuePriceInfos()
	{
		final Map<String, CPSValuePriceInfo> valuePrices = new HashMap<>();
		final BigDecimal fourHundred = BigDecimal.valueOf(400);
		final BigDecimal sixHundred = BigDecimal.valueOf(600);
		valuePrices.put(VALUE_10, createValuePriceInfo(fourHundred));
		valuePrices.put(VALUE_14, createValuePriceInfo(sixHundred));
		final CsticModel csticModel = createCsticModel(CSTIC_01, VALUE_10, VALUE_12, VALUE_14);
		assertNotNull(csticModel.getAssignedValues().get(0));
		classUnderTest.fillValuePriceInfos(valuePrices, csticModel);
		assertEquals(fourHundred, csticModel.getAssignedValues().get(0).getValuePrice().getPriceValue());
		assertEquals(PriceModel.NO_PRICE, csticModel.getAssignedValues().get(1).getValuePrice());
		assertEquals(sixHundred, csticModel.getAssignedValues().get(2).getValuePrice().getPriceValue());
	}

	@Test
	public void testFillValuePriceInfo_ignoreZeroPrices()
	{
		final Map<String, CPSValuePriceInfo> valuePrices = new HashMap<>();
		valuePrices.put(VALUE_10, createValuePriceInfo(BigDecimal.ZERO));
		final CsticModel csticModel = createCsticModel(CSTIC_01, VALUE_10, VALUE_12, VALUE_14);
		assertNotNull(csticModel.getAssignedValues().get(0));
		classUnderTest.fillValuePriceInfos(valuePrices, csticModel);
		assertEquals(PriceModel.NO_PRICE, csticModel.getAssignedValues().get(0).getValuePrice());
	}

	@Test
	public void testAddValueToValuePriceInfoMap() throws PricingEngineException
	{
		Mockito.when(resolver.getValuePricingKey(Mockito.same(masterDataContainer), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString())).thenReturn("CAM700");
		fillPricesMap();
		final Map<String, CPSValuePriceInfo> valuePrices = new HashMap<>();
		classUnderTest.addValueToValuePriceInfoMap(ctxt, valuePrices, PRODUCT_ID, CSTIC_01, "CAM700", null);
		assertEquals(BigDecimal.valueOf(700.0), valuePrices.get("CAM700").getValuePrice().getValuePrice());
		assertEquals(CURRENCY, valuePrices.get("CAM700").getValuePrice().getCurrency());
	}

	@Test
	public void testAddValueToValuePriceInfoMap_zeroPrice() throws PricingEngineException
	{
		final Map<String, CPSValuePriceInfo> valuePrices = new HashMap<>();
		classUnderTest.addValueToValuePriceInfoMap(ctxt, valuePrices, PRODUCT_ID, CSTIC_01, "CAM700", CURRENCY);
		assertEquals(BigDecimal.ZERO, valuePrices.get("CAM700").getValuePrice().getValuePrice());
		assertEquals(CURRENCY, valuePrices.get("CAM700").getValuePrice().getCurrency());
	}

	@Test
	public void testIsValuePriceZero()
	{
		final CPSValuePriceInfo zeroPrice = createValuePriceInfo(BigDecimal.ZERO);
		assertTrue(classUnderTest.isValuePriceZero(zeroPrice));
		final CPSValuePriceInfo nonZeroPrice = createValuePriceInfo(VALUE_100);
		assertFalse(classUnderTest.isValuePriceZero(nonZeroPrice));
	}

	@Test
	public void testIsValuePriceZeroOtherScale()
	{
		final CPSValuePriceInfo zeroPrice = createValuePriceInfo(new BigDecimal("0.00000"));
		assertTrue(classUnderTest.isValuePriceZero(zeroPrice));
	}


	@Test
	public void testDetermineInstanceType()
	{
		assertEquals(SapproductconfigruntimecpsConstants.ITEM_TYPE_MARA, classUnderTest.determineInstanceType(ctxt, PRODUCT_ID));
	}

	@Test
	public void testDetermineInstanceTypeClass()
	{
		assertEquals(SapproductconfigruntimecpsConstants.ITEM_TYPE_KLAH, classUnderTest.determineInstanceType(ctxt, CLASS_ID));
	}

	@Test(expected = IllegalStateException.class)
	public void testDetermineInstanceTypeNotFound()
	{
		classUnderTest.determineInstanceType(ctxt, "unknown");
	}

	@Test
	public void testDetermineInstanceTypeUnambigous()
	{
		//If we find key in products and classes: We consider it as product!
		productMap.put(CLASS_ID, new CPSMasterDataProductContainer());
		assertEquals(SapproductconfigruntimecpsConstants.ITEM_TYPE_MARA, classUnderTest.determineInstanceType(ctxt, CLASS_ID));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFillValuePricesUnknown() throws PricingEngineException
	{
		classUnderTest.fillValuePrices(null, new PriceValueUpdateModel());
	}

	@Test
	public void testApplyDiscountNullDiscount()
	{
		final CPSValuePrice adjustedCpsValuePrice = classUnderTest.applyDiscount(cpsValuePrice, null);
		assertEquals(EUR, adjustedCpsValuePrice.getCurrency());
		final BigDecimal valuePrice = adjustedCpsValuePrice.getValuePrice();
		assertEquals("expected 100 EUR, but saw " + valuePrice + " EUR", 0, VALUE_100.compareTo(valuePrice));
		assertEquals("scale should be still 2", 2, valuePrice.scale());
	}

	@Test
	public void testApplyDiscountNullCPSValuePrice()
	{
		final CPSValuePrice adjustedCpsValuePrice = classUnderTest.applyDiscount(null, discount);
		assertNull(adjustedCpsValuePrice);
	}

	@Test
	public void testGetPrice_NO_PRICE()
	{
		fillPricesMap();
		final CPSMasterDataVariantPriceKey pricingKey = createPriceKey(PRODUCT_ID, CAM200);
		pricesMap.put(pricingKey, DefaultVariantConditionHandler.NO_PRICE);
		assertNull(classUnderTest.getPrice(ctxt, pricingKey));
	}

	@Test
	public void testGetPrice()
	{
		fillPricesMap();
		final CPSMasterDataVariantPriceKey pricingKey = createPriceKey(PRODUCT_ID, CAM200);
		assertNotNull(classUnderTest.getPrice(ctxt, pricingKey));
	}

	@Test
	public void testAdjustCPSValuePrice()
	{
		final CPSValuePrice adjustedCpsValuePrice = classUnderTest.applyDiscount(cpsValuePrice, discount);
		assertEquals(EUR, adjustedCpsValuePrice.getCurrency());
		final BigDecimal valuePrice = adjustedCpsValuePrice.getValuePrice();
		assertEquals("expected 60 EUR, but saw " + valuePrice + " EUR", 0, BigDecimal.valueOf(60).compareTo(valuePrice));
		assertEquals("scale should be still 2", 2, valuePrice.scale());
		assertEquals("original value price may not be modified", 0, VALUE_100.compareTo(cpsValuePrice.getValuePrice()));
	}

	@Test
	public void testAdjustCPSValuePriceWithRounding()
	{
		final BigDecimal basePrice = new BigDecimal("99.99");
		cpsValuePrice.setValuePrice(basePrice);
		final CPSValuePrice adjustedCpsValuePrice = classUnderTest.applyDiscount(cpsValuePrice, discount);
		assertEquals(EUR, adjustedCpsValuePrice.getCurrency());
		final BigDecimal valuePrice = adjustedCpsValuePrice.getValuePrice();
		assertEquals("expected 59.99 EUR, but saw " + valuePrice + " EUR", 0, BigDecimal.valueOf(59.99).compareTo(valuePrice));
		assertEquals("scale should be still 2", 2, valuePrice.scale());
		assertEquals("original value price may not be modified", 0, basePrice.compareTo(cpsValuePrice.getValuePrice()));
	}

	@Test
	public void testAdjustCPSValuePriceWithRoundingZeroDigitCurrency()
	{
		final BigDecimal basePrice = new BigDecimal("99");
		cpsValuePrice.setValuePrice(basePrice);
		cpsValuePrice.setCurrency(JPY);
		final CPSValuePrice adjustedCpsValuePrice = classUnderTest.applyDiscount(cpsValuePrice, discount);
		assertEquals(JPY, adjustedCpsValuePrice.getCurrency());
		final BigDecimal valuePrice = adjustedCpsValuePrice.getValuePrice();
		assertEquals("expected 59 JPY, but saw " + valuePrice + " JPY", 0, BigDecimal.valueOf(59).compareTo(valuePrice));
		assertEquals("scale should be still 0", 0, valuePrice.scale());
		assertEquals("original value price may not be modified", 0, basePrice.compareTo(cpsValuePrice.getValuePrice()));
	}

	@Test
	public void testFindDiscountNull()
	{
		assertNull(classUnderTest.findDiscount(CSTIC_01, VALUE_10, new ArrayList()));
	}

	@Test
	public void testFindDiscountNullForNullDiscount()
	{
		assertNull(classUnderTest.findDiscount(CSTIC_01, VALUE_10, null));
	}

	@Test
	public void testFindDiscount()
	{
		final List<ProductConfigurationDiscount> discountList = new ArrayList();
		final ProductConfigurationDiscount discount = new ProductConfigurationDiscount();
		discount.setCsticName(CSTIC_01);
		discount.setCsticValueName(VALUE_10);
		discountList.add(discount);
		final ProductConfigurationDiscount anotherDiscount = new ProductConfigurationDiscount();
		anotherDiscount.setCsticName(CSTIC_06);
		anotherDiscount.setCsticValueName(VALUE_10);
		discountList.add(anotherDiscount);
		final ProductConfigurationDiscount result = classUnderTest.findDiscount(CSTIC_01, VALUE_10, discountList);
		assertNotNull(result);
		assertEquals(CSTIC_01, result.getCsticName());
		assertEquals(VALUE_10, result.getCsticValueName());
	}

	@Test
	public void testFindDiscountValueNotMatching()
	{
		final List<ProductConfigurationDiscount> discountList = new ArrayList();
		final ProductConfigurationDiscount discount = new ProductConfigurationDiscount();
		discount.setCsticName(CSTIC_01);
		discount.setCsticValueName(VALUE_12);
		discountList.add(discount);
		assertNull(classUnderTest.findDiscount(CSTIC_01, VALUE_10, discountList));
	}

	@Test
	public void testFindDiscountCsticNotMatching()
	{
		final List<ProductConfigurationDiscount> discountList = new ArrayList();
		final ProductConfigurationDiscount discount = new ProductConfigurationDiscount();
		discount.setCsticName(CSTIC_06);
		discount.setCsticValueName(VALUE_10);
		discountList.add(discount);
		assertNull(classUnderTest.findDiscount(CSTIC_01, VALUE_10, discountList));
	}


	@Test
	public void testGetBasePrice()
	{
		final PriceModel base = classUnderTest.getBasePrice(pricingResult);
		assertEquals(BigDecimal.valueOf(BASE_PRICE.doubleValue()), base.getPriceValue());
	}

	@Test
	public void testGetSelectedOptionsPrice()
	{
		final PriceModel selected = classUnderTest.getSelectedOptionsPrice(pricingResult);
		assertEquals(BigDecimal.valueOf(SELECTED_OPTIONS_PRICE.doubleValue()), selected.getPriceValue());
	}

	@Test
	public void testGetSelectedOptionsPriceNoResult()
	{
		final PricingDocumentResult pricingDocumentResult = null;
		final PriceModel selected = classUnderTest.getSelectedOptionsPrice(pricingDocumentResult);
		assertEquals(PriceModel.NO_PRICE, selected);
	}

	@Test
	public void testGetSelectedOptionsPriceNoTargetForSelectedOptions()
	{
		Mockito.when(pricingConfigurationParameter.getTargetForSelectedOptions()).thenReturn(null);
		final PriceModel selected = classUnderTest.getSelectedOptionsPrice(pricingResult);
		assertEquals(PriceModel.NO_PRICE, selected);
	}

	@Test
	public void testGetSelectedOptionsPriceNoSelectedOptionsPresent()
	{
		final List<ConditionPurpose> conditionsWithPurpose = pricingResult.getConditionsWithPurpose();
		conditionsWithPurpose.remove(1);
		final PriceModel selected = classUnderTest.getSelectedOptionsPrice(pricingResult);
		assertEquals(PriceModel.NO_PRICE, selected);
	}

	@Test
	public void testGetCurrentTotalPrice()
	{
		final PriceModel total = classUnderTest.getCurrentTotalPrice(pricingResult);
		assertEquals(CURRENCY, total.getCurrency());
		assertEquals(BigDecimal.valueOf(NET_VALUE.doubleValue()), total.getPriceValue());

	}

	@Test
	public void testGetCurrentTotalPriceNoPriceDocument()
	{
		final PricingDocumentResult pricingDocumentResult = null;
		final PriceModel total = classUnderTest.getCurrentTotalPrice(pricingDocumentResult);
		assertEquals(PriceModel.NO_PRICE, total);
	}

	@Test
	public void testGetCurrentTotalSavings()
	{
		final PricingDocumentResult originalPricingResult = createOrginalPricingResult();
		final PriceModel savings = classUnderTest.getCurrentTotalSavings(pricingResult, originalPricingResult);
		assertNotNull(savings);
		assertEquals(0, BigDecimal.valueOf(50).compareTo(savings.getPriceValue()));
	}

	@Test
	public void testGetCurrentTotalSavingsNoOriginalPrice()
	{
		final PricingDocumentResult originalPricingResult = null;
		final PriceModel savings = classUnderTest.getCurrentTotalSavings(pricingResult, originalPricingResult);
		assertNotNull(savings);
		assertEquals(PriceModel.NO_PRICE, savings);
	}

	@Test
	public void testGetPriceSummary() throws PricingEngineException, ConfigurationEngineException
	{
		final PricingDocumentResult originalPrisingResult = createOrginalPricingResult();

		final ConfigurationRetrievalOptions options = new ConfigurationRetrievalOptions();
		final List<ProductConfigurationDiscount> discountList = new ArrayList<>();

		options.setDiscountList(discountList);
		discountList.add(new ProductConfigurationDiscount());
		final CPSConfiguration configuration = new CPSConfiguration();

		Mockito.when(charonFacade.getConfiguration(ID)).thenReturn(configuration);

		final PricingDocumentInput pricingDocumentInput = new PricingDocumentInput();
		final PricingDocumentInput originalPricingDocumentInput = new PricingDocumentInput();

		Mockito.when(pricingDocumentInputConverter.convertWithContext(Mockito.any(), Mockito.any()))
				.thenReturn(originalPricingDocumentInput, pricingDocumentInput);

		Mockito.when(charonPricingFacade.createPricingDocument(pricingDocumentInput)).thenReturn(pricingResult);
		Mockito.when(charonPricingFacade.createPricingDocument(originalPricingDocumentInput)).thenReturn(originalPrisingResult);

		final PriceSummaryModel result = classUnderTest.getPriceSummary(ID, options);

		assertNotNull(result);
		assertEquals(BigDecimal.valueOf(BASE_PRICE.doubleValue()), result.getBasePrice().getPriceValue());
		assertEquals(BigDecimal.valueOf(SELECTED_OPTIONS_PRICE.doubleValue()), result.getSelectedOptionsPrice().getPriceValue());
		assertEquals(CURRENCY, result.getCurrentTotalPrice().getCurrency());
		assertEquals(BigDecimal.valueOf(NET_VALUE.doubleValue()), result.getCurrentTotalPrice().getPriceValue());
		assertEquals(0, BigDecimal.valueOf(50).compareTo(result.getCurrentTotalSavings().getPriceValue()));
	}

	@Test
	public void testGetPriceSummaryNoDiscounts() throws PricingEngineException, ConfigurationEngineException
	{
		final ConfigurationRetrievalOptions options = new ConfigurationRetrievalOptions();
		final CPSConfiguration configuration = new CPSConfiguration();
		Mockito.when(charonFacade.getConfiguration(ID)).thenReturn(configuration);
		final PricingDocumentInput pricingDocumentInput = new PricingDocumentInput();
		Mockito.when(pricingDocumentInputConverter.convertWithContext(configuration, options)).thenReturn(pricingDocumentInput);
		Mockito.when(charonPricingFacade.createPricingDocument(pricingDocumentInput)).thenReturn(pricingResult);

		final PriceSummaryModel result = classUnderTest.getPriceSummary(ID, options);

		assertNotNull(result);
		assertEquals(BigDecimal.valueOf(BASE_PRICE.doubleValue()), result.getBasePrice().getPriceValue());
		assertEquals(BigDecimal.valueOf(SELECTED_OPTIONS_PRICE.doubleValue()), result.getSelectedOptionsPrice().getPriceValue());
		assertEquals(CURRENCY, result.getCurrentTotalPrice().getCurrency());
		assertEquals(BigDecimal.valueOf(NET_VALUE.doubleValue()), result.getCurrentTotalPrice().getPriceValue());
		assertEquals(0, BigDecimal.ZERO.compareTo(result.getCurrentTotalSavings().getPriceValue()));
	}

	@Test(expected = IllegalStateException.class)
	public void testGetPriceSummaryNoConfiguration() throws PricingEngineException, ConfigurationEngineException
	{
		final ConfigurationRetrievalOptions options = new ConfigurationRetrievalOptions();
		Mockito.when(charonFacade.getConfiguration(ID)).thenReturn(null);
		classUnderTest.getPriceSummary(ID, options);
	}

	@Test
	public void testGetPriceForSubTotal()
	{
		final PriceModel price = classUnderTest.getPriceForSubTotal(SUBTOTAL_FOR_BASE_PRICE_FLAG,
				pricingResultForSubtotalsAndConditionTypes);
		assertEquals(createPriceModel(SUBTOTAL_BASE_PRICE), price);
	}

	@Test
	public void testGetPriceForSubTotalRequiredSubtotalDoesNotExist()
	{
		final PriceModel price = classUnderTest.getPriceForSubTotal(ANOTHER_SUBTOTAL_FLAG,
				pricingResultForSubtotalsAndConditionTypes);
		assertEquals(PriceModel.NO_PRICE, price);
	}

	@Test
	public void testGetPriceForSubTotalSubtotalFlagEmpty()
	{
		final PriceModel price = classUnderTest.getPriceForSubTotal("", pricingResultForSubtotalsAndConditionTypes);
		assertEquals(PriceModel.NO_PRICE, price);
	}

	@Test
	public void testGetPriceForSubTotalSubtotalFlagNull()
	{
		final PriceModel price = classUnderTest.getPriceForSubTotal(null, pricingResultForSubtotalsAndConditionTypes);
		assertEquals(PriceModel.NO_PRICE, price);
	}

	@Test
	public void testGetPriceForConditionTypes()
	{
		final PriceModel price = classUnderTest.getPriceForConditionTypes(CONDITIONTYPES_FOR_BASEPRICE_COLLECTION,
				pricingResultForSubtotalsAndConditionTypes);
		assertEquals(createPriceModel(CONDITIONTYPE_BASE_PRICE), price);
	}

	@Test
	public void testGetPriceForConditionTypesRequiredCondTypesDoNotExist()
	{
		final PriceModel price = classUnderTest.getPriceForConditionTypes(ANOTHER_CONDITIONTYPES_COLLECTION,
				pricingResultForSubtotalsAndConditionTypes);
		assertEquals(PriceModel.NO_PRICE, price);
	}

	@Test
	public void testGetPriceForConditionTypesCollectionEmpty()
	{
		final PriceModel price = classUnderTest.getPriceForConditionTypes(new ArrayList<String>(),
				pricingResultForSubtotalsAndConditionTypes);
		assertEquals(PriceModel.NO_PRICE, price);
	}

	@Test
	public void testGetPriceForConditionTypesCollectionNull()
	{
		final PriceModel price = classUnderTest.getPriceForConditionTypes(null, pricingResultForSubtotalsAndConditionTypes);
		assertEquals(PriceModel.NO_PRICE, price);
	}

	@Test
	public void testCreatePriceModelFromBigDecimal()
	{
		final PriceModel price = classUnderTest.createPriceModel(CURRENCY_USD, new BigDecimal(SUBTOTAL_BASE_PRICE));
		assertEquals(CURRENCY_USD, price.getCurrency());
		assertEquals(SUBTOTAL_BASE_PRICE, price.getPriceValue().toString());
	}

	@Test
	public void testGetBasePriceConditonType()
	{
		Mockito.when(pricingConfigurationParameter.getPricingDetailsMode())
				.thenReturn(SAPProductConfigPricingDetailsMode.CONDITIONTYPE);
		Mockito.when(pricingConfigurationParameter.getConditionTypesForBasePrice())
				.thenReturn(CONDITIONTYPES_FOR_BASEPRICE_COLLECTION);
		final PriceModel price = classUnderTest.getBasePrice(pricingResultForSubtotalsAndConditionTypes);
		assertEquals(createPriceModel(CONDITIONTYPE_BASE_PRICE), price);
	}

	@Test
	public void testGetBasePriceSubTotal()
	{
		Mockito.when(pricingConfigurationParameter.getPricingDetailsMode())
				.thenReturn(SAPProductConfigPricingDetailsMode.PRICINGSUBTOTAL);
		Mockito.when(pricingConfigurationParameter.getSubTotalForBasePrice()).thenReturn(SUBTOTAL_FOR_BASE_PRICE_FLAG);
		final PriceModel price = classUnderTest.getBasePrice(pricingResultForSubtotalsAndConditionTypes);
		assertEquals(createPriceModel(SUBTOTAL_BASE_PRICE), price);
	}

	@Test
	public void testGetBasePriceConditionFunction()
	{
		Mockito.when(pricingConfigurationParameter.getPricingDetailsMode())
				.thenReturn(SAPProductConfigPricingDetailsMode.CONDITIONFUNCTION);
		Mockito.when(pricingConfigurationParameter.getTargetForBasePrice()).thenReturn(TARGET_FOR_BASE_PRICE);
		final PriceModel price = classUnderTest.getBasePrice(pricingResult);
		assertEquals(BigDecimal.valueOf(BASE_PRICE), price.getPriceValue());
	}

	@Test
	public void testGetBasePricePricingModeNull()
	{
		Mockito.when(pricingConfigurationParameter.getPricingDetailsMode()).thenReturn(null);
		Mockito.when(pricingConfigurationParameter.getTargetForBasePrice()).thenReturn(TARGET_FOR_BASE_PRICE);
		final PriceModel price = classUnderTest.getBasePrice(pricingResult);
		assertEquals(BigDecimal.valueOf(BASE_PRICE), price.getPriceValue());
	}

	@Test
	public void testGetSelectedOptionsConditonType()
	{
		Mockito.when(pricingConfigurationParameter.getPricingDetailsMode())
				.thenReturn(SAPProductConfigPricingDetailsMode.CONDITIONTYPE);
		Mockito.when(pricingConfigurationParameter.getConditionTypesForSelectedOptions())
				.thenReturn(CONDITIONTYPES_FOR_SELECTEDOPTIONS_COLLECTION);
		final PriceModel price = classUnderTest.getSelectedOptionsPrice(pricingResultForSubtotalsAndConditionTypes);
		assertEquals(createPriceModel(CONDITIONTYPE_SELECTED_OPTIONS), price);
	}

	@Test
	public void testGetSelectedOptionsSubTotal()
	{
		Mockito.when(pricingConfigurationParameter.getPricingDetailsMode())
				.thenReturn(SAPProductConfigPricingDetailsMode.PRICINGSUBTOTAL);
		Mockito.when(pricingConfigurationParameter.getSubTotalForSelectedOptions()).thenReturn(SUBTOTAL_FOR_SELECTED_OPTIONS_FLAG);
		final PriceModel price = classUnderTest.getSelectedOptionsPrice(pricingResultForSubtotalsAndConditionTypes);
		assertEquals(createPriceModel(SUBTOTAL_SELECTED_OPTIONS), price);
	}

	@Test
	public void testGetSelectedOptionsConditionFunction()
	{
		Mockito.when(pricingConfigurationParameter.getPricingDetailsMode())
				.thenReturn(SAPProductConfigPricingDetailsMode.CONDITIONFUNCTION);
		Mockito.when(pricingConfigurationParameter.getTargetForSelectedOptions()).thenReturn(TARGET_FOR_SELECTED_OPTIONS);
		final PriceModel price = classUnderTest.getSelectedOptionsPrice(pricingResult);
		assertEquals(BigDecimal.valueOf(SELECTED_OPTIONS_PRICE), price.getPriceValue());
	}

	@Test
	public void testGetSelectedOptionsPricingModeNull()
	{
		Mockito.when(pricingConfigurationParameter.getPricingDetailsMode()).thenReturn(null);
		Mockito.when(pricingConfigurationParameter.getTargetForSelectedOptions()).thenReturn(TARGET_FOR_SELECTED_OPTIONS);
		final PriceModel price = classUnderTest.getSelectedOptionsPrice(pricingResult);
		assertEquals(BigDecimal.valueOf(SELECTED_OPTIONS_PRICE), price.getPriceValue());
	}

	private PricingDocumentResult fillPricingDocumentResultForSubtotalsAndConditionTypes()
	{
		final PricingDocumentResult pricingResult = new PricingDocumentResult();
		pricingResult.setDocumentCurrencyUnit(CURRENCY_USD);
		// Subtotals
		pricingResult.setSubTotals(new ArrayList<>());
		final Subtotal subtotalBase = new Subtotal();
		subtotalBase.setFlag(SUBTOTAL_FOR_BASE_PRICE_FLAG);
		subtotalBase.setValue(SUBTOTAL_BASE_PRICE);
		pricingResult.getSubTotals().add(subtotalBase);
		final Subtotal subtotalOptions = new Subtotal();
		subtotalOptions.setFlag(SUBTOTAL_FOR_SELECTED_OPTIONS_FLAG);
		subtotalOptions.setValue(SUBTOTAL_SELECTED_OPTIONS);
		pricingResult.getSubTotals().add(subtotalOptions);
		// Conditions
		pricingResult.setConditions(new ArrayList<>());
		pricingResult.getConditions().add(createCondition("PR00", PR00_VALUE, false, " "));
		pricingResult.getConditions().add(createCondition("PR01", PR01_VALUE, false, " "));
		pricingResult.getConditions().add(createCondition("PR02", PR02_VALUE, true, " "));
		pricingResult.getConditions().add(createCondition("PR03", PR03_VALUE, false, "A"));
		pricingResult.getConditions().add(createCondition("VA00", VA00_VALUE, false, " "));
		pricingResult.getConditions().add(createCondition("VA00", VA01_VALUE, false, " "));
		return pricingResult;
	}

	private ConditionResult createCondition(final String condType, final double value, final boolean statistical,
			final String inactiveFlag)
	{
		final ConditionResult condition = new ConditionResult();
		condition.setConditionType(condType);
		condition.setConditionValue(value);
		condition.setStatistical(statistical);
		condition.setInactiveFlag(inactiveFlag);
		return condition;
	}

	protected PriceModel createPriceModel(final String valuePrice)
	{
		final PriceModel priceModel = new PriceModelImpl();
		priceModel.setPriceValue(new BigDecimal(valuePrice));
		priceModel.setCurrency(CURRENCY_USD);
		return priceModel;
	}

}
