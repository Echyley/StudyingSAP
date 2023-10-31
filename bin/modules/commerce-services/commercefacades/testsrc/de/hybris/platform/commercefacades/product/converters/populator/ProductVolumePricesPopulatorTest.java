/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercefacades.product.converters.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.product.PriceService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.PriceValue;
import de.hybris.platform.variants.model.VariantProductModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static de.hybris.platform.europe1.jalo.GeneratedPriceRow.MINQTD;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;



/**
 * Test suite for {@link ProductVolumePricesPopulator}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ProductVolumePricesPopulatorTest
{
	private static final String CURRENCY_ISO = "EUR";
	private static final Double PRICE_VALUE1 = 222.45D;
	private static final Double PRICE_VALUE2 = 333.45D;

	@Mock
	private Map<String ,Long> qualifiersMap;

	@Mock
	private PriceService priceService;

	@Mock
	private PriceDataFactory priceDataFactory;

	@Mock
	private ModelService modelService;

	private ProductVolumePricesPopulator<ProductModel, ProductData> productVolumePricesPopulator;

	@Before
	public void setUp()
	{
		productVolumePricesPopulator = new ProductVolumePricesPopulator<>();
		productVolumePricesPopulator.setPriceService(priceService);
		productVolumePricesPopulator.setPriceDataFactory(priceDataFactory);
		productVolumePricesPopulator.setModelService(modelService);
		given(qualifiersMap.get(MINQTD)).willReturn(1L);
	}

	@Test
	public void testPopulate()
	{
		final ProductModel source = mock(ProductModel.class);
		final PriceValue priceValue1 = mock(PriceValue.class);
		final PriceValue priceValue2 = mock(PriceValue.class);
		final VariantProductModel variantProductModel = mock(VariantProductModel.class);

		final PriceData priceData1 = mock(PriceData.class);
		final PriceData priceData2 = mock(PriceData.class);
		final List<PriceData> priceDataList = new ArrayList<>();
		priceDataList.add(priceData1);
		priceDataList.add(priceData2);

		final List<PriceInformation> mockPriceInformationList = new ArrayList<>();
		final PriceInformation priceInformation1 = mock(PriceInformation.class);
		final PriceInformation priceInformation2 = mock(PriceInformation.class);
		mockPriceInformationList.add(priceInformation1);
		mockPriceInformationList.add(priceInformation2);

		given(source.getVariants()).willReturn(Collections.singleton(variantProductModel));
		given(priceValue1.getCurrencyIso()).willReturn(CURRENCY_ISO);
		given(priceValue1.getValue()).willReturn(PRICE_VALUE1);
		given(priceValue2.getCurrencyIso()).willReturn(CURRENCY_ISO);
		given(priceValue2.getValue()).willReturn(PRICE_VALUE2);
		given(priceInformation1.getPriceValue()).willReturn(priceValue1);
		given(priceInformation2.getPriceValue()).willReturn(priceValue2);
		given(priceInformation1.getQualifiers()).willReturn(qualifiersMap);
		given(priceInformation2.getQualifiers()).willReturn(qualifiersMap);
		given(priceService.getPriceInformationsForProduct(source)).willReturn(mockPriceInformationList);
		given(priceDataFactory.create(PriceDataType.FROM, BigDecimal.valueOf(PRICE_VALUE1), CURRENCY_ISO)).willReturn(priceData1);
		given(priceDataFactory.create(PriceDataType.FROM, BigDecimal.valueOf(PRICE_VALUE2), CURRENCY_ISO)).willReturn(priceData2);

		final ProductData result = new ProductData();
		productVolumePricesPopulator.populate(source, result);

		Assert.assertEquals(priceDataList, result.getVolumePrices());
	}

	@Test
	public void testPopulateVolumePricesEmptyWhenSinglePrice()
	{
		final ProductModel source = mock(ProductModel.class, withSettings().lenient());
		final PriceValue priceValue1 = mock(PriceValue.class, withSettings().lenient());
		final VariantProductModel variantProductModel = mock(VariantProductModel.class);

		final List<PriceInformation> mockPriceInformationList = new ArrayList<>();
		final PriceInformation priceInformation1 = mock(PriceInformation.class, withSettings().lenient());
		mockPriceInformationList.add(priceInformation1);

		given(source.getVariants()).willReturn(Collections.singleton(variantProductModel));
		given(priceValue1.getCurrencyIso()).willReturn(CURRENCY_ISO);
		given(priceValue1.getValue()).willReturn(PRICE_VALUE1);
		given(priceInformation1.getPriceValue()).willReturn(priceValue1);
		given(priceInformation1.getQualifiers()).willReturn(qualifiersMap);
		given(priceService.getPriceInformationsForProduct(source)).willReturn(mockPriceInformationList);

		final ProductData result = new ProductData();
		productVolumePricesPopulator.populate(source, result);

		Assert.assertTrue(result.getVolumePrices().isEmpty());
	}
}
