/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.externaltax.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.externaltax.ProductTaxCodeModel;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commerceservices.externaltax.TaxAreaLookupStrategy;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.externaltax.ProductTaxCodeService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTaxCodeStrategyTest
{
	private final static String PRODUCT_CODE_WITHOUT_TAX = "product-code-without-tax";
	private final static String PRODUCT_CODE = "test-product";
	private final static String PRODUCT_TAX_CODE = "test-product-tax";
	private final static String PRODUCT_TAX_AREA = "US";
	private final static String BASE_PRODUCT_CODE = "base-product";
	private final static String BASE_PRODUCT_TAX_CODE = "base-product-tax";
	private final static String VARIANT_PRODUCT_CODE = "variant-product";
	private final static String VARIANT_PRODUCT_TAX_CODE = "variant-product-tax";
	private final static String VARIANT_PRODUCT_CODE_WIHOUT_TAX = "variant-product-without-tax";
	ProductTaxCodeModel productTaxCodeModel;
	ProductTaxCodeModel baseProductTaxCodeModel;
	private DefaultTaxCodeStrategy defaultTaxCodeStrategy;
	@Mock
	private ProductTaxCodeService productTaxCodeService;
	@Mock
	private TaxAreaLookupStrategy taxAreaLookupStrategy;
	@Mock
	private ProductService productService;
	@Mock
	private CatalogVersionService catalogVersionService;
	@Mock
	private BaseSiteService baseSiteService;
	@Mock
	private ConfigurationService configurationService;
	private AbstractOrderModel abstractOrder;
	private BaseStoreModel baseStoreModel;
	private ProductModel baseProduct;
	private VariantProductModel variantProduct;

	@Before
	public void setUp()
	{
		defaultTaxCodeStrategy = new DefaultTaxCodeStrategy();
		defaultTaxCodeStrategy.setProductTaxCodeService(productTaxCodeService);
		defaultTaxCodeStrategy.setTaxAreaLookupStrategy(taxAreaLookupStrategy);
		defaultTaxCodeStrategy.setProductService(productService);
		defaultTaxCodeStrategy.setCatalogVersionService(catalogVersionService);
		defaultTaxCodeStrategy.setBaseSiteService(baseSiteService);
		defaultTaxCodeStrategy.setConfigurationService(configurationService);

		abstractOrder = Mockito.mock(AbstractOrderModel.class);
		baseStoreModel = Mockito.mock(BaseStoreModel.class);
		baseProduct = mock(ProductModel.class);
		variantProduct = mock(VariantProductModel.class);
		productTaxCodeModel = Mockito.mock(ProductTaxCodeModel.class);
		baseProductTaxCodeModel = Mockito.mock(ProductTaxCodeModel.class);

		given(productTaxCodeModel.getTaxCode()).willReturn(PRODUCT_TAX_CODE);
		given(baseProductTaxCodeModel.getTaxCode()).willReturn(BASE_PRODUCT_TAX_CODE);
		given(abstractOrder.getStore()).willReturn(baseStoreModel);
		given(baseStoreModel.getExternalTaxEnabled()).willReturn(Boolean.TRUE);
		given(variantProduct.getCode()).willReturn(VARIANT_PRODUCT_CODE);
		given(variantProduct.getBaseProduct()).willReturn(baseProduct);
		given(baseProduct.getCode()).willReturn(BASE_PRODUCT_CODE);

		given(taxAreaLookupStrategy.getTaxAreaForOrder(abstractOrder)).willReturn(PRODUCT_TAX_AREA);
		given(catalogVersionService.getSessionCatalogVersions()).willReturn(new ArrayList<>());
	}

	@Test
	public void shouldReturnTaxCode()
	{
		given(productTaxCodeService.getTaxCodeForProductAndArea(PRODUCT_CODE, PRODUCT_TAX_AREA)).willReturn(productTaxCodeModel);

		final String taxCode = defaultTaxCodeStrategy.getTaxCodeForCodeAndOrder(PRODUCT_CODE, abstractOrder);
		Assert.assertNotNull(taxCode);
		Assert.assertEquals(PRODUCT_TAX_CODE, taxCode);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionNullTaxCode()
	{
		defaultTaxCodeStrategy.getTaxCodeForCodeAndOrder(PRODUCT_CODE_WITHOUT_TAX, abstractOrder);
	}

	@Test
	public void taxCodeForBaseProductTest()
	{
		given(productService.getProductForCode(nullable(CatalogVersionModel.class), eq(BASE_PRODUCT_CODE))).willReturn(baseProduct);
		given(productTaxCodeService.getTaxCodeForProductAndArea(BASE_PRODUCT_CODE, PRODUCT_TAX_AREA))
				.willReturn(baseProductTaxCodeModel);

		final String taxCode = defaultTaxCodeStrategy.getTaxCodeForCodeAndOrder(BASE_PRODUCT_CODE, abstractOrder);
		Assert.assertNotNull(taxCode);
		Assert.assertEquals(BASE_PRODUCT_TAX_CODE, taxCode);
	}

	@Test
	public void taxCodeForVariantProductTest()
	{
		final Map<String, String> taxMap = new HashMap<>();
		taxMap.put(BASE_PRODUCT_CODE, BASE_PRODUCT_TAX_CODE);
		taxMap.put(VARIANT_PRODUCT_CODE, VARIANT_PRODUCT_TAX_CODE);
		given(productService.getProductForCode(nullable(CatalogVersionModel.class), eq(VARIANT_PRODUCT_CODE)))
				.willReturn(variantProduct);
		given(productTaxCodeService.lookupTaxCodes(anyCollection(), eq(PRODUCT_TAX_AREA))).willReturn(taxMap);

		final String taxCode = defaultTaxCodeStrategy.getTaxCodeForCodeAndOrder(VARIANT_PRODUCT_CODE, abstractOrder);
		Assert.assertNotNull(taxCode);
		Assert.assertEquals(VARIANT_PRODUCT_TAX_CODE, taxCode);
	}

	@Test
	public void variantProductWithoutTaxValueTest()
	{
		final Map<String, String> taxMap = new HashMap<>();
		taxMap.put(BASE_PRODUCT_CODE, BASE_PRODUCT_TAX_CODE);

		given(productService.getProductForCode(nullable(CatalogVersionModel.class), eq(VARIANT_PRODUCT_CODE_WIHOUT_TAX)))
				.willReturn(variantProduct);
		given(productTaxCodeService.lookupTaxCodes(anyCollection(), eq(PRODUCT_TAX_AREA))).willReturn(taxMap);

		final String taxCode = defaultTaxCodeStrategy.getTaxCodeForCodeAndOrder(VARIANT_PRODUCT_CODE_WIHOUT_TAX, abstractOrder);
		Assert.assertNotNull(taxCode);
		Assert.assertEquals(BASE_PRODUCT_TAX_CODE, taxCode);
	}

	@Test(expected = IllegalArgumentException.class)
	public void variantProductWithoutAnyTaxValueTest()
	{
		final Map<String, String> taxMap = new HashMap<>();

		given(productService.getProductForCode(nullable(CatalogVersionModel.class), eq(VARIANT_PRODUCT_CODE_WIHOUT_TAX)))
				.willReturn(variantProduct);
		given(productTaxCodeService.lookupTaxCodes(anyCollection(), eq(PRODUCT_TAX_AREA))).willReturn(taxMap);

		defaultTaxCodeStrategy.getTaxCodeForCodeAndOrder(VARIANT_PRODUCT_CODE_WIHOUT_TAX, abstractOrder);
	}

}
