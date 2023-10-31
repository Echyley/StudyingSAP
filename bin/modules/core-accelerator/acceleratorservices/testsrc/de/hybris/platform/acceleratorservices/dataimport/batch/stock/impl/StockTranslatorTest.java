/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.dataimport.batch.stock.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.dataimport.batch.stock.StockImportAdapter;
import de.hybris.platform.acceleratorservices.dataimport.batch.stock.StockTranslator;
import de.hybris.platform.jalo.Item;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Test for {@link StockTranslator}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class StockTranslatorTest
{
	private static final String TEST_STOCK = "1234";

	private StockTranslator translator;

	@Mock
	private StockImportAdapter stockImportAdapter;

	@Mock
	private Item item;

	@Before
	public void setUp()
	{
		translator = new StockTranslator();
		translator.setStockImportAdapter(stockImportAdapter);
	}

	@Test
	public void test()
	{
		translator.performImport(TEST_STOCK, item);
		BDDMockito.verify(stockImportAdapter, BDDMockito.times(1)).performImport(TEST_STOCK, item);
	}
}
