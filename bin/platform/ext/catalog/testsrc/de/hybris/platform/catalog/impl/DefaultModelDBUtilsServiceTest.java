/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.catalog.impl;


import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.ModelDBUtilsService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.ProductFeatureModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.UUID;

import javax.annotation.Resource;

import org.junit.Test;

@IntegrationTest
public class DefaultModelDBUtilsServiceTest extends ServicelayerTest
{
	@Resource
	private ModelService modelService;
	@Resource
	private ModelDBUtilsService modelDBUtilsService;

	@Test
	public void shouldReturnColumnSizeForStringColumnType()
	{
		final Long size = modelDBUtilsService.findColumnSize(ProductModel._TYPECODE, ProductModel.CODE);

		assertThat(size).isNotNull().isPositive();
	}

	@Test
	public void shouldReturnColumnSizeForIntegerColumnType()
	{
		final Long size = modelDBUtilsService.findColumnSize(ProductModel._TYPECODE, ProductModel.ENDLINENUMBER);

		assertThat(size).isNotNull().isPositive();
	}

	@Test
	public void shouldReturnColumnSizeForDateColumnType()
	{
		final Long size = modelDBUtilsService.findColumnSize(ProductModel._TYPECODE, ProductModel.ONLINEDATE);

		assertThat(size).isNotNull().isPositive();
	}

	@Test
	public void shouldReturnColumnSizeForDoubleColumnType()
	{
		final Long size = modelDBUtilsService.findColumnSize(ProductModel._TYPECODE, ProductModel.DELIVERYTIME);

		assertThat(size).isNotNull().isPositive();
	}

	@Test
	public void shouldReturnColumnSizeForBigDecimalColumnType()
	{
		final Long size = modelDBUtilsService.findColumnSize(CartModel._TYPECODE, CartModel.DELIVERYCOST);

		assertThat(size).isNotNull().isPositive();
	}

	@Test(expected = UnknownIdentifierException.class)
	public void shouldThrowExceptionForInvalidTypeCode()
	{
		final Long size = modelDBUtilsService.findColumnSize("foo", CartModel.DELIVERYCOST);

		assertThat(size).isNotNull().isPositive();
	}

	@Test
	public void shouldNotCorruptConnectionPool()
	{
		Long lastSize = 1L;
		for (int i = 0; i < 1000; i++)
		{
			lastSize = modelDBUtilsService.findColumnSize(CartModel._TYPECODE, CartModel.DELIVERYCOST);
		}
		final ProductModel product = createProduct();

		assertThat(product).isNotNull();
		assertThat(lastSize).isNotNull().isPositive();
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionForListReference()
	{
		final Long size = modelDBUtilsService.findColumnSize(ProductModel._TYPECODE, ProductModel.FEATURES);

		assertThat(size).isNull();
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionForCollection()
	{
		final Long size = modelDBUtilsService.findColumnSize(ProductModel._TYPECODE, ProductModel.VARIANTS);

		assertThat(size).isNull();
	}

	ProductModel createProduct()
	{
		final CatalogModel catalog = modelService.create(CatalogModel.class);
		catalog.setId(UUID.randomUUID().toString());

		final CatalogVersionModel catalogVersion = modelService.create(CatalogVersionModel.class);
		catalogVersion.setVersion(UUID.randomUUID().toString());
		catalogVersion.setCatalog(catalog);


		final ProductModel product = modelService.create(ProductModel.class);
		product.setCode("1234");
		product.setName("1234");
		product.setCatalogVersion(catalogVersion);

		final ProductFeatureModel productFeature = modelService.create(ProductFeatureModel.class);
		productFeature.setProduct(product);

		modelService.save(product);

		return product;
	}
}