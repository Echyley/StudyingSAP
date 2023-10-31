/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.services.impl.CPQConfigurableChecker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ProductConfigAddToCartValidatorTest
{

	private static final String P_CODE = "pCode";

	@InjectMocks
	private ProductConfigAddToCartValidator classUnderTest;

	@Mock
	private CPQConfigurableChecker cpqChecker;

	private final CommerceCartParameter parameter = new CommerceCartParameter();
	private final ProductModel configurableProduct = new ProductModel();
	private final ProductModel defaultProduct = new ProductModel();

	@Before
	public void setUp()
	{
		configurableProduct.setCode(P_CODE);
		given(cpqChecker.isCPQConfigurableProduct(configurableProduct)).willReturn(true);
		given(cpqChecker.isCPQConfigurableProduct(defaultProduct)).willReturn(false);
	}

	@Test
	public void testSupportsConfigurableProduct()
	{
		parameter.setProduct(configurableProduct);
		assertTrue("validator shall support configurable products", classUnderTest.supports(parameter));
	}
	
	@Test
	public void testSupportsNotDefaultProduct()
	{
		parameter.setProduct(defaultProduct);
		assertFalse("validator shall only support configurable products", classUnderTest.supports(parameter));
	}

}
