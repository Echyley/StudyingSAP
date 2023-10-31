/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.populators.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.util.TaxValue;

import java.util.Collection;
import java.util.Collections;

import org.cxml.ItemDetail;
import org.cxml.ItemID;
import org.cxml.ItemOut;
import org.cxml.Money;
import org.cxml.SupplierPartID;
import org.cxml.Tax;
import org.cxml.UnitPrice;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultOrderEntryOverridingPopulatorTest
{
	private static final String PRICE_VALUE = "10.00";
	private final static String ITEM_QTY = "1";
	private final static String SUPPLIER_PART_ID = "123456";

	@InjectMocks
	private DefaultOrderEntryOverridingPopulator defaultOrderEntryPopulator;

	private final AbstractOrderEntryModel orderEntry = new AbstractOrderEntryModel();

	@Mock
	private ProductService productService;
	@Mock
	private ItemOut itemOut;
	@Mock
	private ItemID itemId;
	@Mock
	private ItemDetail itemDetail;
	@Mock
	private UnitPrice unitPrice;
	@Mock
	private Money unitPriceMoney;
	@Mock
	private SupplierPartID supplierPartId;

	@Mock
	private Populator<Tax, Collection<TaxValue>> taxValuePopulator;

	@Before
	public void setUp()
	{
		when(itemOut.getQuantity()).thenReturn(ITEM_QTY);
		when(itemOut.getItemDetailOrBlanketItemDetail()).thenReturn(Collections.singletonList(itemDetail));
		when(itemDetail.getUnitPrice()).thenReturn(unitPrice);
		when(unitPrice.getMoney()).thenReturn(unitPriceMoney);
		when(unitPriceMoney.getvalue()).thenReturn(PRICE_VALUE);
	}

	@Test
	public void testPopulate()
	{
		defaultOrderEntryPopulator.populate(itemOut, orderEntry);
		assertThat(orderEntry.getBasePrice()).isEqualTo(Double.valueOf(PRICE_VALUE));
	}
}
