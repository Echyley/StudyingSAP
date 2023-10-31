/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapsalesordersimulation.contributor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.orderexchange.constants.OrderCsvColumns;
import de.hybris.platform.sap.orderexchange.constants.SalesConditionCsvColumns;
import de.hybris.platform.sap.sapmodel.model.SAPPricingConditionModel;
import de.hybris.platform.sap.saporderexchangeoms.outbound.impl.SapOmsSalesConditionsContributor;

public class SalesOrderSimulaitonSalesConditionsContributor extends SapOmsSalesConditionsContributor {
	private static final Logger LOG = Logger.getLogger(SalesOrderSimulaitonSalesConditionsContributor.class);
	private static final String ABSOLUTE_CONDITION_TYPE = "C";

	@Override
	protected List<Map<String, Object>> createRowsSyncPricing(final OrderModel order) {
		LOG.info("SalesOrderSimulaitonSalesConditionsContributor : inside createRowsForSyncPricing method...");
		int discountConditionCounter = getConditionCounterStartProductDiscount();
		final List<Map<String, Object>> result = new ArrayList<>();
		setConditionTypes(order);
		
		for (final AbstractOrderEntryModel entry : order.getEntries())
		{
			final Iterator<SAPPricingConditionModel> it = entry.getSapPricingConditions().iterator();
			while (it.hasNext())
			{
				final SAPPricingConditionModel condition = it.next();
				final Map<String, Object> row = new HashMap<>();

				row.put(OrderCsvColumns.ORDER_ID, order.getCode());
				row.put(SalesConditionCsvColumns.CONDITION_ENTRY_NUMBER, entry.getEntryNumber());
				row.put(SalesConditionCsvColumns.CONDITION_CODE, condition.getConditionType());
				row.put(SalesConditionCsvColumns.CONDITION_VALUE, condition.getConditionRate());
				row.put(SalesConditionCsvColumns.CONDITION_PRICE_QUANTITY, condition.getConditionPricingUnit());
				int conditionCounter = (Double.parseDouble(condition.getConditionRate())<0) ? discountConditionCounter++ : getConditionCounterGrossPrice();
				row.put(SalesConditionCsvColumns.CONDITION_COUNTER, conditionCounter);
				
				
				if (isAbsolute(condition.getConditionCalculationType()))
				{
					row.put(SalesConditionCsvColumns.ABSOLUTE, Boolean.TRUE);
					row.put(SalesConditionCsvColumns.CONDITION_CURRENCY_ISO_CODE, condition.getCurrencyKey());
					row.put(SalesConditionCsvColumns.CONDITION_UNIT_CODE, condition.getConditionUnit());
				}
				else
				{
					row.put(SalesConditionCsvColumns.ABSOLUTE, Boolean.FALSE);
				}
				
				getBatchIdAttributes().forEach(row::putIfAbsent);
				row.put("dh_batchId", order.getCode());
				result.add(row);
			}
		}
		
		createDeliveryCostRow(order, result);
		return result;
	}
	
	protected boolean isAbsolute(String conditionCalType) {
		return conditionCalType.equals(ABSOLUTE_CONDITION_TYPE)? Boolean.TRUE: Boolean.FALSE;
	}

	
}
