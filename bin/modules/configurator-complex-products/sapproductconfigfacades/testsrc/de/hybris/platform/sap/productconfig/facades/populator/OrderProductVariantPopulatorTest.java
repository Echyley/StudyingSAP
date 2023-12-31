/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.facades.populator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.sap.productconfig.facades.VariantConfigurationInfoProvider;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 * Unit test for {@link OrderProductVariantPopulator}
 */
@UnitTest
public class OrderProductVariantPopulatorTest
{
	private static final String ITEM_PK = "1";
	private OrderProductVariantPopulator classUnderTest;
	@Mock
	private VariantConfigurationInfoProvider variantConfigurationInfoProvider;
	@Mock
	private AbstractOrderEntryModel sourceEntry;
	private List<OrderEntryData> targetList;
	private final OrderEntryData entry0 = new OrderEntryData();
	private final OrderEntryData entry1 = new OrderEntryData();
	private ProductData productData;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new OrderProductVariantPopulator();
		classUnderTest.setVariantConfigurationInfoProvider(variantConfigurationInfoProvider);
		targetList = new ArrayList<>();
		productData = new ProductData();
		productData.setBaseOptions(new ArrayList<>());
		Mockito.when(sourceEntry.getEntryNumber()).thenReturn(Integer.valueOf(0));
		Mockito.when(sourceEntry.getPk()).thenReturn(PK.parse(ITEM_PK));
		entry0.setEntryNumber(Integer.valueOf(0));
		entry1.setEntryNumber(Integer.valueOf(1));
		entry0.setProduct(productData);
	}

	@Test
	public void testWriteToTargetEntryEmptyList()
	{
		classUnderTest.writeToTargetEntry(targetList, sourceEntry);
		assertTrue(targetList.isEmpty());
	}

	@Test
	public void testWriteToTargetEntryNoMatchingEntry()
	{
		targetList.add(entry1);
		classUnderTest.writeToTargetEntry(targetList, sourceEntry);
		assertEquals(1, targetList.size());
		assertNull(entry1.getItemPK());
	}

	@Test
	public void testWriteToTargetEntryMatchingEntry()
	{
		targetList.add(entry0);
		classUnderTest.writeToTargetEntry(targetList, sourceEntry);
		assertEquals(1, targetList.size());
		assertEquals(ITEM_PK, entry0.getItemPK());
	}
}
