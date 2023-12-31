/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.ProductCsticAndValueParameterProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.ProviderFactory;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.CsticParameter;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.CsticParameterWithValues;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Unit test for {@link ProductCsticAndValueParameterProviderServiceImpl}
 */
@UnitTest
public class ProductCsticAndValueParameterProviderServiceImplTest
{

	private static final String PRODUCT = "TEST_PRODUCT";
	private static final String CSTIC_NAME = "CSTIC_NAME";

	@Mock
	private ProviderFactory providerFactory;

	@Mock
	private ProductCsticAndValueParameterProvider productCsticAndValueParameterProvider;

	private ProductCsticAndValueParameterProviderServiceImpl classUnderTest;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);

		classUnderTest = new ProductCsticAndValueParameterProviderServiceImpl();
		classUnderTest.setProviderFactory(providerFactory);

		when(providerFactory.getProductCsticAndValueParameterProvider()).thenReturn(productCsticAndValueParameterProvider);
		when(productCsticAndValueParameterProvider.retrieveProductCsticsAndValuesParameters(PRODUCT)).thenReturn(createData());
	}

	protected Map<String, CsticParameterWithValues> createData()
	{
		final Map<String, CsticParameterWithValues> map = new HashMap<>();

		final CsticParameterWithValues csticWithValues = new CsticParameterWithValues();
		final CsticParameter cstic = new CsticParameter();
		cstic.setCsticName(CSTIC_NAME);
		csticWithValues.setCstic(cstic);

		map.put(CSTIC_NAME, csticWithValues);

		return map;
	}

	@Test
	public void testRetrieveProductCsticsAndValuesParameters()
	{
		final Map<String, CsticParameterWithValues> result = classUnderTest.retrieveProductCsticsAndValuesParameters(PRODUCT);

		assertNotNull(result);
		assertEquals(1, result.size());
		assertTrue(result.containsKey(CSTIC_NAME));
		assertEquals(CSTIC_NAME, result.get(CSTIC_NAME).getCstic().getCsticName());
	}

}
