/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.facades.populator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ClassificationData;
import de.hybris.platform.commercefacades.product.data.FeatureData;
import de.hybris.platform.commercefacades.product.data.ProductData;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;


/**
 * Unit test for {@link FeatureProvider}
 */
@UnitTest
public class FeatureProviderTest
{
	private final FeatureProvider classUnderTest = new FeatureProvider();
	private final ProductData productData = new ProductData();

	@Test
	public void testEmptyFeatureList()
	{
		productData.setClassifications(new ArrayList<>());
		final List<FeatureData> result = classUnderTest.getListOfFeatures(productData);

		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testFeatureList()
	{
		final List<ClassificationData> classificationDataList = new ArrayList<>();
		final ClassificationData classificationData = new ClassificationData();
		final List<FeatureData> features = new ArrayList<>();
		features.add(new FeatureData());
		classificationData.setFeatures(features);
		classificationDataList.add(classificationData);
		productData.setClassifications(classificationDataList);
		final List<FeatureData> result = classUnderTest.getListOfFeatures(productData);

		assertNotNull(result);
		assertEquals(1, result.size());
	}
}
