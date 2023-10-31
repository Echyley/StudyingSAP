/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.pricing.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.cps.CharonPricingFacade;
import de.hybris.platform.sap.productconfig.runtime.cps.cache.CPSCache;
import de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service.MasterDataContainerResolver;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataKnowledgeBaseContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.common.CPSMasterDataKBHeaderInfo;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.pricing.CPSMasterDataVariantPriceKey;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingDocumentInput;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingDocumentResult;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.pricing.CPSValuePrice;
import de.hybris.platform.sap.productconfig.runtime.cps.populator.impl.MasterDataContext;
import de.hybris.platform.sap.productconfig.runtime.interf.ContextualConverter;
import de.hybris.platform.sap.productconfig.runtime.interf.CsticQualifier;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceValueUpdateModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultVariantConditionHandlerTest
{
	private static final String KB_ID = "123";
	private static final String PRODUCT_ID = "pCode";
	private static final String CSTIC_1 = "cstic_1";
	private static final String CSTIC_2 = "cstic_2";
	private static final String CSTIC_3 = "cstic_3";
	private static final String INSTANCE_1 = "instance_1";
	private static final String INSTANCE_2 = "instance_2";
	private static final String VALUE_1 = "value_1";
	private static final String VALUE_2 = "value_2";
	private static final String VALUE_3 = "value_3";
	private static final String CONDITION_1_1_1 = "condition_1_1_1";
	private static final String CONDITION_2_2_1 = "condition_1_2_1";
	private static final String CONDITION_2_2_3 = "condition_1_3_3";
	private static final CPSMasterDataVariantPriceKey PRICING_KEY_1_1_1 = createPricingKey(INSTANCE_1, CONDITION_1_1_1);
	private static final CPSValuePrice VALUE_PRICE_1_1_1 = new CPSValuePrice();
	private static final CPSMasterDataVariantPriceKey PRICING_KEY_2_2_1 = createPricingKey(INSTANCE_2, CONDITION_2_2_1);
	private static final CPSValuePrice VALUE_PRICE_2_2_1 = new CPSValuePrice();

	@Mock
	private CharonPricingFacade charonPricingFacade;
	@Mock
	private CPSCache cache;
	@Mock
	private MasterDataContainerResolver masterDataResolver;
	@Mock
	private Converter<PricingDocumentResult, Map<CPSMasterDataVariantPriceKey, CPSValuePrice>> pricesMapConverter;
	@Mock
	private ContextualConverter<CPSMasterDataKnowledgeBaseContainer, PricingDocumentInput, MasterDataContext> pricingDocumentInputKBConverter;
	@Mock
	private Logger mockedLogger;

	private DefaultVariantConditionHandler classUnderTest;

	private MasterDataContext ctxt;
	private final Map<CPSMasterDataVariantPriceKey, CPSValuePrice> cachedValuePricesMap = new HashMap<>();
	private final Map<CPSMasterDataVariantPriceKey, CPSValuePrice> fetchedValuePricesMap = new HashMap<>();
	private final Map<String, List<CPSMasterDataVariantPriceKey>> requestedValuePricesMap = new HashMap<>();
	private final PricingDocumentResult pricingResult = new PricingDocumentResult();
	private final CPSMasterDataKnowledgeBaseContainer kbCacheContainer = new CPSMasterDataKnowledgeBaseContainer();
	private final PricingDocumentInput pricingDocumentInput = new PricingDocumentInput();


	@Before
	public void setUp() throws PricingEngineException
	{
		kbCacheContainer.setHeaderInfo(new CPSMasterDataKBHeaderInfo());
		kbCacheContainer.getHeaderInfo().setId(Integer.valueOf(KB_ID));
		ctxt = new MasterDataContext();
		ctxt.setKbCacheContainer(kbCacheContainer);
		ctxt.setPricingProduct(PRODUCT_ID);

		cachedValuePricesMap.clear();
		fetchedValuePricesMap.clear();
		requestedValuePricesMap.clear();

		// manual instantiation of class instead of us of @injectMocks: Mockito is not smart enough to assign constructor args proper, as
		// pricingDocumentInputKBConverter is a subType of pricesMapConverter  (mockito ignores the generics) leading to strange test errors
		classUnderTest = new DefaultVariantConditionHandler(charonPricingFacade, cache, masterDataResolver,
				pricingDocumentInputKBConverter, pricesMapConverter);

		given(masterDataResolver.getPossibleValueIds(kbCacheContainer, CSTIC_1)).willReturn(Collections.singleton(VALUE_1));
		given(masterDataResolver.getPossibleValueIds(kbCacheContainer, CSTIC_2))
				.willReturn(Stream.of(VALUE_1, VALUE_2, VALUE_3).collect(Collectors.toSet()));
		given(masterDataResolver.getPossibleValueIds(kbCacheContainer, CSTIC_3)).willReturn(Collections.singleton(VALUE_1));

		given(masterDataResolver.getValuePricingKey(kbCacheContainer, INSTANCE_1, CSTIC_1, VALUE_1)).willReturn(CONDITION_1_1_1);
		given(masterDataResolver.getValuePricingKey(kbCacheContainer, INSTANCE_2, CSTIC_2, VALUE_1)).willReturn(CONDITION_2_2_1);
		given(masterDataResolver.getValuePricingKey(kbCacheContainer, INSTANCE_2, CSTIC_2, VALUE_3)).willReturn(CONDITION_2_2_3);
	}

	private void mockVariantConditionFetch() throws PricingEngineException
	{
		given(pricingDocumentInputKBConverter.convertWithContext(kbCacheContainer, ctxt)).willReturn(pricingDocumentInput);
		given(charonPricingFacade.createPricingDocument(pricingDocumentInput)).willReturn(pricingResult);
		given(pricesMapConverter.convert(pricingResult)).willReturn(fetchedValuePricesMap);
	}

	@Test
	public void testPrefetchValuePricesNothingCached() throws PricingEngineException
	{
		mockVariantConditionFetch();
		fetchedValuePricesMap.put(PRICING_KEY_1_1_1, VALUE_PRICE_1_1_1);
		given(cache.getValuePricesMap(KB_ID, PRODUCT_ID)).willReturn(null);

		classUnderTest.preFetchValuePrices(ctxt, Collections.singletonList(createUpdateModel(INSTANCE_1, CSTIC_1)), false);

		assertEquals(fetchedValuePricesMap, ctxt.getValuePricesMap());
		verify(cache).setValuePricesMap(KB_ID, PRODUCT_ID, fetchedValuePricesMap);
	}

	@Test
	public void testPrefetchValuePricesAllCached() throws PricingEngineException
	{
		cachedValuePricesMap.put(PRICING_KEY_1_1_1, VALUE_PRICE_1_1_1);
		given(cache.getValuePricesMap(KB_ID, PRODUCT_ID)).willReturn(cachedValuePricesMap);

		classUnderTest.preFetchValuePrices(ctxt, Collections.singletonList(createUpdateModel(INSTANCE_1, CSTIC_1)), false);

		assertSame(cachedValuePricesMap, ctxt.getValuePricesMap());
		verify(cache, never()).setValuePricesMap(eq(KB_ID), eq(PRODUCT_ID), any());
	}

	@Test
	public void testPopulateValuePricesMapFromCache()
	{
		given(cache.getValuePricesMap(KB_ID, PRODUCT_ID)).willReturn(cachedValuePricesMap);
		classUnderTest.populateValuePricesMapFromCache(ctxt);
		assertSame(cachedValuePricesMap, ctxt.getValuePricesMap());
	}

	@Test
	public void testPopulateValuePricesMapFromCacheNotCached() throws PricingEngineException
	{
		given(cache.getValuePricesMap(KB_ID, PRODUCT_ID)).willReturn(null);
		classUnderTest.populateValuePricesMapFromCache(ctxt);
		assertTrue(ctxt.getValuePricesMap().isEmpty());
	}

	@Test
	public void testCachePricesMap()
	{
		ctxt.setValuePricesMap(fetchedValuePricesMap);
		classUnderTest.cachePricesMap(ctxt);
		verify(cache).setValuePricesMap(KB_ID, PRODUCT_ID, fetchedValuePricesMap);
	}

	@Test
	public void testRetrieveVariantConditions() throws PricingEngineException
	{
		mockVariantConditionFetch();
		assertSame(fetchedValuePricesMap, classUnderTest.retrieveVariantConditions(ctxt));
	}

	@Test
	public void testDetermineRequestedValuePricingKeysEmpty()
	{
		classUnderTest.determineRequestedValuePricingKeys(ctxt, Collections.emptyList(), false);
		assertTrue(ctxt.getRequestedPricesByProductMap().isEmpty());
	}

	@Test
	public void testDetermineRequestedValuePricingKeysNoConditionMapping()
	{
		ctxt.setValuePricesMap(Collections.emptyMap());
		final List<PriceValueUpdateModel> inputList = Collections.singletonList(createUpdateModel(INSTANCE_1, CSTIC_3));
		classUnderTest.determineRequestedValuePricingKeys(ctxt, inputList, false);
		assertTrue(ctxt.getRequestedPricesByProductMap().isEmpty());
	}

	@Test
	public void testDetermineRequestedValuePricingKeysAlreadyKnown()
	{
		ctxt.setValuePricesMap(Collections.singletonMap(PRICING_KEY_1_1_1, VALUE_PRICE_1_1_1));
		final List<PriceValueUpdateModel> inputList = Collections.singletonList(createUpdateModel(INSTANCE_1, CSTIC_1));
		classUnderTest.determineRequestedValuePricingKeys(ctxt, inputList, false);
		assertTrue(ctxt.getRequestedPricesByProductMap().isEmpty());
	}

	@Test
	public void testdDetermineRequestedValuePricingKeysSingleKey()
	{
		ctxt.setValuePricesMap(Collections.emptyMap());
		final List<PriceValueUpdateModel> inputList = Collections.singletonList(createUpdateModel(INSTANCE_1, CSTIC_1));
		classUnderTest.determineRequestedValuePricingKeys(ctxt, inputList, false);

		assertEquals(1, ctxt.getRequestedPricesByProductMap().size());
		assertTrue(ctxt.getRequestedPricesByProductMap().keySet().contains(INSTANCE_1));

		assertEquals(1, ctxt.getRequestedPricesByProductMap().get(INSTANCE_1).size());
		assertTrue(ctxt.getRequestedPricesByProductMap().get(INSTANCE_1).contains(createPricingKey(INSTANCE_1, CONDITION_1_1_1)));
	}

	@Test
	public void testDetermineRequestedValuePricingKeysMultiKeys()
	{
		ctxt.setValuePricesMap(Collections.emptyMap());
		final List<PriceValueUpdateModel> inputList = Stream
				.of(createUpdateModel(INSTANCE_1, CSTIC_1), createUpdateModel(INSTANCE_2, CSTIC_2, VALUE_1, VALUE_3))
				.collect(Collectors.toList());
		classUnderTest.determineRequestedValuePricingKeys(ctxt, inputList, false);

		assertEquals(2, ctxt.getRequestedPricesByProductMap().size());
		assertTrue(ctxt.getRequestedPricesByProductMap().keySet().contains(INSTANCE_1));
		assertTrue(ctxt.getRequestedPricesByProductMap().keySet().contains(INSTANCE_2));

		assertEquals(1, ctxt.getRequestedPricesByProductMap().get(INSTANCE_1).size());
		assertTrue(ctxt.getRequestedPricesByProductMap().get(INSTANCE_1).contains(createPricingKey(INSTANCE_1, CONDITION_1_1_1)));

		assertEquals(2, ctxt.getRequestedPricesByProductMap().get(INSTANCE_2).size());
		assertTrue(ctxt.getRequestedPricesByProductMap().get(INSTANCE_2).contains(createPricingKey(INSTANCE_2, CONDITION_2_2_1)));
		assertTrue(ctxt.getRequestedPricesByProductMap().get(INSTANCE_2).contains(createPricingKey(INSTANCE_2, CONDITION_2_2_3)));
	}

	@Test
	public void testDetermineRequestedValuePricingKeyOnlySelectedValues()
	{
		ctxt.setValuePricesMap(Collections.emptyMap());
		final List<PriceValueUpdateModel> inputList = Collections.singletonList(createUpdateModel(INSTANCE_2, CSTIC_2, VALUE_3));
		classUnderTest.determineRequestedValuePricingKeys(ctxt, inputList, true);

		assertEquals(1, ctxt.getRequestedPricesByProductMap().size());
		assertEquals(1, ctxt.getRequestedPricesByProductMap().get(INSTANCE_2).size());
		assertTrue(ctxt.getRequestedPricesByProductMap().get(INSTANCE_2).contains(createPricingKey(INSTANCE_2, CONDITION_2_2_3)));
	}


	@Test
	public void testMapPossibleValueToPricingKey()
	{
		final CPSMasterDataVariantPriceKey pricingKey = classUnderTest.mapPossibleValueToPricingKey(ctxt,
				createUpdateModel(INSTANCE_1, CSTIC_1).getCsticQualifier(), VALUE_1);
		assertEquals(createPricingKey(INSTANCE_1, CONDITION_1_1_1), pricingKey);
	}

	@Test
	public void testMergeFetchedConditionsToEmptyCache()
	{
		ctxt.setValuePricesMap(cachedValuePricesMap);
		fetchedValuePricesMap.put(PRICING_KEY_2_2_1, VALUE_PRICE_2_2_1);
		ctxt.setRequestedPricesByProductMap(Collections.singletonMap(INSTANCE_2, Collections.singletonList(PRICING_KEY_2_2_1)));

		classUnderTest.mergeFetchedConditions(ctxt, fetchedValuePricesMap);

		assertEquals(1, ctxt.getValuePricesMap().size());
		assertTrue(ctxt.getValuePricesMap().containsKey(PRICING_KEY_2_2_1));
	}

	@Test
	public void testMergeFetchedConditionsIntoExistingConditions()
	{
		cachedValuePricesMap.put(PRICING_KEY_1_1_1, VALUE_PRICE_1_1_1);
		fetchedValuePricesMap.put(PRICING_KEY_2_2_1, VALUE_PRICE_2_2_1);
		ctxt.setValuePricesMap(cachedValuePricesMap);
		ctxt.setRequestedPricesByProductMap(Collections.singletonMap(INSTANCE_2, Collections.singletonList(PRICING_KEY_2_2_1)));

		classUnderTest.mergeFetchedConditions(ctxt, fetchedValuePricesMap);

		assertEquals(2, ctxt.getValuePricesMap().size());
		assertTrue(ctxt.getValuePricesMap().containsKey(PRICING_KEY_1_1_1));
		assertTrue(ctxt.getValuePricesMap().containsKey(PRICING_KEY_2_2_1));
	}

	@Test
	public void testMergeFetchedConditionsConsiderRequestedButNoFound()
	{
		ctxt.setValuePricesMap(cachedValuePricesMap);
		ctxt.setRequestedPricesByProductMap(Collections.singletonMap(INSTANCE_1, Collections.singletonList(PRICING_KEY_1_1_1)));

		classUnderTest.mergeFetchedConditions(ctxt, fetchedValuePricesMap);

		assertEquals(1, ctxt.getValuePricesMap().size());
		assertTrue(ctxt.getValuePricesMap().containsKey(PRICING_KEY_1_1_1));
		assertSame(DefaultVariantConditionHandler.NO_PRICE, ctxt.getValuePricesMap().get(PRICING_KEY_1_1_1));
	}

	@Test
	public void logDebugIfEnabledFalse()
	{
		given(mockedLogger.isDebugEnabled()).willReturn(false);
		classUnderTest.logDebugIfEnabled(mockedLogger, () -> "test");
		verify(mockedLogger, never()).debug("test");
	}

	@Test
	public void logDebugIfEnabledTrue()
	{
		given(mockedLogger.isDebugEnabled()).willReturn(true);
		classUnderTest.logDebugIfEnabled(mockedLogger, () -> "test");
		verify(mockedLogger).debug("test");
	}

	private static PriceValueUpdateModel createUpdateModel(final String instanceName, final String csticName,
			final String... selectedValueNames)
	{
		final PriceValueUpdateModel updateModel = new PriceValueUpdateModel();
		updateModel.setCsticQualifier(new CsticQualifier());
		updateModel.getCsticQualifier().setCsticName(csticName);
		updateModel.getCsticQualifier().setInstanceName(instanceName);
		updateModel.setSelectedValues(Arrays.asList(selectedValueNames));
		return updateModel;
	}

	private static CPSMasterDataVariantPriceKey createPricingKey(final String productId, final String confitionKey)
	{
		final CPSMasterDataVariantPriceKey expectedPricingKey = new CPSMasterDataVariantPriceKey();
		expectedPricingKey.setProductId(productId);
		expectedPricingKey.setVariantConditionKey(confitionKey);
		return expectedPricingKey;
	}

}
