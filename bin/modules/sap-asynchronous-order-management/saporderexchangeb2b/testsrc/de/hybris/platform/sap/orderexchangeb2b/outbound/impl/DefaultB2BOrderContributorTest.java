/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.orderexchangeb2b.outbound.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.orderexchange.constants.OrderCsvColumns;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;


@SuppressWarnings("javadoc")
@UnitTest
public class DefaultB2BOrderContributorTest
{
	private DefaultB2BOrderContributor cut;

	@Before
	public void setUp()
	{
		cut = new DefaultB2BOrderContributor();
		
		Map<String, String> batchIdAttributes = new HashMap<String, String>();
		batchIdAttributes.put("dh_batchId", "000001");
		batchIdAttributes.put("dh_sourceId", "HYBRIS");
		batchIdAttributes.put("dh_type", "SALESORDER_CREATE");
		
		cut.setBatchIdAttributes(batchIdAttributes);
	}

	@Test
	public void testGetColumns() throws Exception
	{
		final Set<String> columns = cut.getColumns();
		assertTrue(columns.contains(OrderCsvColumns.CHANNEL));
		assertTrue(columns.contains(OrderCsvColumns.PURCHASE_ORDER_NUMBER));
	}

	@Test
	public void testEnhanceColumns() throws Exception
	{
		final OrderModel model = new OrderModel();
		model.setPurchaseOrderNumber("PO123");
		final BaseSiteModel site = new BaseSiteModel();
		model.setSite(site);
		site.setChannel(SiteChannel.B2B);

		final Map<String, Object> row = new HashMap<>();
		final List<Map<String, Object>> rows = Arrays.asList(row);
		final List<Map<String, Object>> enhancedRows = cut.enhanceRowsByB2BFields(model, rows);
		assertEquals("PO123", enhancedRows.get(0).get(OrderCsvColumns.PURCHASE_ORDER_NUMBER));
		assertEquals(SiteChannel.B2B, enhancedRows.get(0).get(OrderCsvColumns.CHANNEL));
	}
}