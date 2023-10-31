/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.product.interceptors;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.variants.model.VariantProductModel;
import de.hybris.platform.variants.model.VariantTypeModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class VariantProductValidateInterceptorUnitTest
{
	@Mock
	private TypeService typeService;

	private final VariantProductValidateInterceptor interceptor = new VariantProductValidateInterceptor();


	@Before
	public void prepare()
	{
		interceptor.setTypeService(typeService);
	}

	@Test
	public void testValidateForNull()
	{
		try
		{
			interceptor.onValidate(null, mock(InterceptorContext.class));
		}
		catch (InterceptorException e)
		{
			fail("Should not throw exception for null value", e);
		}
	}

	@Test
	public void testValidateForNotAVariantProduct() throws InterceptorException
	{
		final ProductModel model = mock(ProductModel.class);
		interceptor.onValidate(model, mock(InterceptorContext.class));

		verifyNoInteractions(model);
	}


	@Test
	public void testValidateForAVariantProduct() throws InterceptorException
	{
		final VariantProductModel model = mock(VariantProductModel.class);
		final InterceptorContext ctx = mock(InterceptorContext.class);

		interceptor.onValidate(model, ctx);

		verifyNoInteractions(model);
	}

	@Test
	public void testValidateForVariantProductWhenNew() throws InterceptorException
	{
		final VariantProductModel model = mock(VariantProductModel.class);
		final InterceptorContext ctx = mock(InterceptorContext.class);
		when(ctx.isNew(model)).thenReturn(Boolean.TRUE);
		interceptor.onValidate(model, ctx);

		verify(model, Mockito.times(1)).getBaseProduct();
	}

	@Test
	public void testValidateForVariantProductWhenBaseProductChanged() throws InterceptorException
	{
		final VariantProductModel model = mock(VariantProductModel.class);
		final InterceptorContext ctx = mock(InterceptorContext.class);
		when(ctx.isModified(model, VariantProductModel.BASEPRODUCT)).thenReturn(Boolean.TRUE);
		interceptor.onValidate(model, ctx);

		verify(model, Mockito.times(1)).getBaseProduct();
	}


	@Test
	public void testValidateForVariantProductWithNullBaseProductType()
	{
		final VariantProductModel model = mock(VariantProductModel.class);

		final ProductModel baseProduct = mock(ProductModel.class);

		final InterceptorContext ctx = mock(InterceptorContext.class);
		when(ctx.isModified(model, VariantProductModel.BASEPRODUCT)).thenReturn(Boolean.TRUE);
		when(model.getBaseProduct()).thenReturn(baseProduct);
		try
		{
			interceptor.onValidate(model, ctx);
			Assert.fail("When base product's variant type is null validation should fail ");
		}
		catch (final InterceptorException e)
		{
			//ok here
		}
		verify(model, Mockito.times(1)).getBaseProduct();
	}

	/**
	 * case
	 * <p>
	 * base product variantType -> Shoe
	 * <p>
	 * variant product type -> Shirt
	 * <p>
	 */
	@Test
	public void testValidateForVariantProductWithIncompatibleBaseProductType()
	{
		when(
				typeService.isAssignableFrom(any(ShirtVariantTypeModel.class),
						any(ShoeVariantTypeModel.class))).thenReturn(Boolean.FALSE);

		final VariantProductModel model = mock(VariantProductModel.class);

		final ProductModel baseProduct = mock(ProductModel.class);
		when(baseProduct.getVariantType()).thenReturn(new ShoeVariantTypeModel());//shoe variant for base product

		final InterceptorContext ctx = mock(InterceptorContext.class);
		when(ctx.isModified(model, VariantProductModel.BASEPRODUCT)).thenReturn(Boolean.TRUE);
		when(typeService.getComposedTypeForCode(anyString()))
				.thenReturn(new ShirtVariantTypeModel()); //shirt for VP itself
		when(model.getBaseProduct()).thenReturn(baseProduct);
		try
		{
			interceptor.onValidate(model, ctx);
			Assert.fail("When base product's variant type is not compatible with variant product Shoe <-> Shirt");
		}
		catch (final InterceptorException e)
		{
			//ok here
		}
		verify(model, Mockito.times(1)).getBaseProduct();
	}

	/**
	 * case
	 * <p>
	 * base product variantType -> Shirt
	 * <p>
	 * variant product type -> FancyShirt (more specific)
	 * <p>
	 */
	@Test
	public void testValidateForVariantProductWithCompatibleBaseProductTypeMoreSpecific()
	{
		when(
				typeService.isAssignableFrom(any(FancyShirtVariantTypeModel.class),
						any(ShirtVariantTypeModel.class)))
				.thenReturn(Boolean.FALSE);


		final VariantProductModel model = mock(VariantProductModel.class);

		final ProductModel baseProduct = mock(ProductModel.class);
		when(baseProduct.getVariantType()).thenReturn(new ShirtVariantTypeModel());//shoe variant for base product

		final InterceptorContext ctx = mock(InterceptorContext.class);
		when(ctx.isModified(model, VariantProductModel.BASEPRODUCT)).thenReturn(Boolean.TRUE);
		when(typeService.getComposedTypeForCode(anyString()))
				.thenReturn(new FancyShirtVariantTypeModel()); //shirt for VP itself
		when(model.getBaseProduct()).thenReturn(baseProduct);

		assertThatThrownBy(() -> interceptor.onValidate(model, ctx)).isInstanceOf(InterceptorException.class);

		verify(model).getBaseProduct();
	}

	/**
	 * case
	 * <p>
	 * base product variantType -> Shirt
	 * <p>
	 * variant product type -> Shirt
	 * <p>
	 */
	@Test
	public void testValidateForVariantProductWithCompatibleBaseProductType() throws InterceptorException
	{
		when(
				typeService.isAssignableFrom(any(ShirtVariantTypeModel.class),
						any(ShirtVariantTypeModel.class))).thenReturn(Boolean.TRUE);


		final VariantProductModel model = mock(VariantProductModel.class);

		final ProductModel baseProduct = mock(ProductModel.class);
		when(baseProduct.getVariantType()).thenReturn(new ShirtVariantTypeModel());//shoe variant for base product

		final InterceptorContext ctx = mock(InterceptorContext.class);
		when(ctx.isModified(model, VariantProductModel.BASEPRODUCT)).thenReturn(Boolean.TRUE);
		when(typeService.getComposedTypeForCode(anyString()))
				.thenReturn(new ShirtVariantTypeModel()); //shirt for VP itself
		when(model.getBaseProduct()).thenReturn(baseProduct);

		interceptor.onValidate(model, ctx);

		verify(model, Mockito.times(1)).getBaseProduct();
	}

	/**
	 * case
	 * <p>
	 * base product variantType -> FancyShirt (more specific)
	 * <p>
	 * variant product type -> Shirt
	 * <p>
	 */
	@Test
	public void testValidateForVariantProductWithIncompatibleBaseProductTypeTooSpecific()
	{

		final VariantProductModel model = mock(VariantProductModel.class);

		final ProductModel baseProduct = mock(ProductModel.class);
		when(baseProduct.getVariantType()).thenReturn(new ShirtVariantTypeModel());//shoe variant for base product

		final InterceptorContext ctx = mock(InterceptorContext.class);
		when(ctx.isModified(model, VariantProductModel.BASEPRODUCT)).thenReturn(Boolean.TRUE);
		when(typeService.getComposedTypeForCode(anyString()))
				.thenReturn(new FancyShirtVariantTypeModel()); //shirt for VP itself
		when(model.getBaseProduct()).thenReturn(baseProduct);

		try
		{
			interceptor.onValidate(model, ctx);
			//Assert.fail("When base product's variant type is not compatible with variant product Shoe <-> Shirt");
		}
		catch (final InterceptorException e)
		{
			//ok here
		}
		verify(model, Mockito.times(1)).getBaseProduct();
	}

	static class ShoeVariantTypeModel extends VariantTypeModel
	{
		@Override
		public String toString()
		{
			return "shoe";
		}
	}

	static class ShirtVariantTypeModel extends VariantTypeModel
	{
		@Override
		public String toString()
		{
			return "shirt";
		}
	}

	static class FancyShirtVariantTypeModel extends ShirtVariantTypeModel
	{
		@Override
		public String toString()
		{
			return "fancy-shirt";
		}
	}

}
