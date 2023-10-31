/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saporderexchangeoms.cancellation;

import de.hybris.platform.ordercancel.OrderCancelEntry;
import de.hybris.platform.ordercancel.OrderCancelException;
import de.hybris.platform.ordercancel.OrderCancelResponse;
import de.hybris.platform.ordercancel.impl.executors.WarehouseResponseExecutor;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.warehousing.cancellation.ConsignmentCancellationService;
import de.hybris.platform.warehousing.cancellation.OmsOrderCancelService;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Concrete implementation to provide OMS executor for {@link WarehouseResponseExecutor}
 */
public class SapOmsWarehouseResponseExecutor extends WarehouseResponseExecutor
{
	private OmsOrderCancelService omsOrderCancelService;
	private ConsignmentCancellationService consignmentCancellationService;
	private SapConsignmentCancellationService sapConsignmentCancellationService;

	@Override
	public void processCancelResponse(final OrderCancelResponse orderCancelResponse,
			final OrderCancelRecordEntryModel cancelRequestRecordEntry) throws OrderCancelException
	{
		// Cancel first the unallocated quantities if existing
		final List<OrderCancelEntry> allocatedEntries = getOmsOrderCancelService().processOrderCancel(cancelRequestRecordEntry);

		if (!CollectionUtils.isEmpty(allocatedEntries))
		{
			final OrderCancelResponse updatedOrderCancelResponse = new OrderCancelResponse(orderCancelResponse.getOrder(),
					allocatedEntries);

			// Cancel order entries
			super.processCancelResponse(updatedOrderCancelResponse, cancelRequestRecordEntry);

			// Then process the cancellation of the consignments
			getSapConsignmentCancellationService().processSapConsignmentCancellation(orderCancelResponse);
		}
		else
		{
			super.processCancelResponse(createOrderCancelResponse(orderCancelResponse), cancelRequestRecordEntry);
		}
	}

	protected SapConsignmentCancellationService getSapConsignmentCancellationService()
	{
		return sapConsignmentCancellationService;
	}

	private OrderCancelResponse createOrderCancelResponse(final OrderCancelResponse orderCancelResponse)
	{
		final List<OrderCancelEntry> orderCancelEntries = new ArrayList<>();
		orderCancelResponse.getEntriesToCancel().forEach(cancelEntry -> orderCancelEntries.add(createOrderCancelEntryWithZeroQuantity(cancelEntry)));
		return new OrderCancelResponse(orderCancelResponse.getOrder(), orderCancelEntries, orderCancelResponse.getResponseStatus(),
				orderCancelResponse.getNotes());
	}

	private OrderCancelEntry createOrderCancelEntryWithZeroQuantity(final OrderCancelEntry cancelEntry)
	{
		return new OrderCancelEntry(cancelEntry.getOrderEntry(), Long.valueOf(0), cancelEntry.getNotes(),
				cancelEntry.getCancelReason());
	}

	@Required
	public void setSapConsignmentCancellationService(final SapConsignmentCancellationService sapConsignmentCancellationService)
	{
		this.sapConsignmentCancellationService = sapConsignmentCancellationService;
	}

	protected OmsOrderCancelService getOmsOrderCancelService()
	{
		return omsOrderCancelService;
	}

	@Required
	public void setOmsOrderCancelService(final OmsOrderCancelService omsOrderCancelService)
	{
		this.omsOrderCancelService = omsOrderCancelService;
	}

	protected ConsignmentCancellationService getConsignmentCancellationService()
	{
		return consignmentCancellationService;
	}

	@Required
	public void setConsignmentCancellationService(final ConsignmentCancellationService consignmentCancellationService)
	{
		this.consignmentCancellationService = consignmentCancellationService;
	}

}
