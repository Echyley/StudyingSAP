/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.returnsexchange.outbound.impl;


import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.sap.hybris.returnsexchange.constants.ReturnOrderEntryCsvColumns;

import de.hybris.platform.returns.model.ReturnEntryModel;




public class DefaultCancelReturnOrderEntryContributor extends DefaultReturnOrderEntryContributor {
	@Override
	public Set<String> getColumns()
	{
		Set<String> columns = new HashSet<>(super.getColumns());
		columns.add(ReturnOrderEntryCsvColumns.REASON_CODE_FOR_RETURN_CANCELLATION);
		return columns;
		
	}
	
	@Override
	protected Map<String, Object> createReturnEntryRow(final ReturnEntryModel returnEntry)
	{

		final Map<String, Object> row = super.createReturnEntryRow(returnEntry);
		row.put(ReturnOrderEntryCsvColumns.REASON_CODE_FOR_RETURN_CANCELLATION, returnEntry.getReturnRequest().getReasonCodeCancellation());
		return row;
	}
}
