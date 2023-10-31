/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.ysapcpiomsfulfillment.cancellation;

import static de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService.RESPONSE_MESSAGE;
import static de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService.getPropertyValue;
import static de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService.isSentSuccessfully;

import de.hybris.platform.basecommerce.enums.CancelReason;
import de.hybris.platform.ordercancel.impl.orderstatechangingstrategies.EnterCancellingStrategy;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderCancellationModel;
import de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService;
import de.hybris.platform.sap.sapcpiorderexchange.service.SapCpiOrderOutboundConversionService;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.ResponseEntity;

import rx.SingleSubscriber;

public class SapCpiOmsEnterCancellingStrategy extends EnterCancellingStrategy
{

	private static final Logger LOG = LoggerFactory.getLogger(SapCpiOmsEnterCancellingStrategy.class);
	private SapCpiOutboundService sapCpiOutboundService;
	private SapCpiOrderOutboundConversionService sapCpiOrderOutboundConversionService;

	/**
	 * Change OrderStatus of an order after cancel operation
	 *
	 * @param orderCancelRecordEntry
	 * @param saveOrderModel
	 */
	@Override
	public void changeOrderStatusAfterCancelOperation(final OrderCancelRecordEntryModel orderCancelRecordEntry,
	                                                  final boolean saveOrderModel)
	{
		super.changeOrderStatusAfterCancelOperation(orderCancelRecordEntry, saveOrderModel);
		processConsignmentsCancellations(orderCancelRecordEntry);
	}

	/**
	 * Process external consignments cancellations
	 *
	 * @param orderCancelRecordEntry
	 */
	protected void processConsignmentsCancellations(final OrderCancelRecordEntryModel orderCancelRecordEntry)
	{
		if (orderCancelRecordEntry.getCancelReason() == null)
		{
			orderCancelRecordEntry.setCancelReason(CancelReason.OTHER);
		}

		final List<SAPCpiOutboundOrderCancellationModel> sapCpiOrderCancellations = getSapCpiOrderOutboundConversionService()
				.convertCancelOrderToSapCpiCancelOrder(orderCancelRecordEntry);

		if (sapCpiOrderCancellations.isEmpty())
		{
			LOG.info(
					"There are no SAP orders attached to the order [{}] so there is no cancellation requests to be sent to SCPI!",
					orderCancelRecordEntry.getModificationRecord().getOrder().getCode());
			return;
		}

		sapCpiOrderCancellations.stream()
		                        .filter(sapCpiOrderCancellation -> isCancellationReplicationRequired(sapCpiOrderCancellation,
				                        orderCancelRecordEntry))
		                        .forEach(sapCpiOrderCancellation -> sendConsignmentCancellationToScpi(sapCpiOrderCancellation,
				                        orderCancelRecordEntry));

	}

	/**
	 * Checks if the SAP order cancellation needs to be replicated to SCPI
	 *
	 * @param sapCpiOutboundOrderCancellation
	 * @param orderCancelRecordEntry
	 * @return true SAP order cancellation is required
	 */
	protected boolean isCancellationReplicationRequired(
			final SAPCpiOutboundOrderCancellationModel sapCpiOutboundOrderCancellation,
			final OrderCancelRecordEntryModel orderCancelRecordEntry)
	{
		return orderCancelRecordEntry.getOrderEntriesModificationEntries()
		                             .stream()
		                             .anyMatch(entry -> entry.getOrderEntry()
		                                                     .getConsignmentEntries()
		                                                     .stream()
		                                                     .anyMatch(consignmentEntry -> consignmentEntry.getConsignment()
		                                                                                                   .getSapOrder() != null && consignmentEntry
				                                                     .getConsignment()
				                                                     .getSapOrder()
				                                                     .getCode()
				                                                     .contentEquals(
						                                                     sapCpiOutboundOrderCancellation.getOrderId())));
	}

	/**
	 * Send consignment cancellation to the SAP backend through SCPI
	 *
	 * @param sapCpiOutboundOrderCancellation
	 * @param orderCancelRecordEntry
	 */
	protected void sendConsignmentCancellationToScpi(final SAPCpiOutboundOrderCancellationModel sapCpiOutboundOrderCancellation,
	                                                 final OrderCancelRecordEntryModel orderCancelRecordEntry)
	{
		getSapCpiOutboundService().sendOrderCancellation(sapCpiOutboundOrderCancellation)
		                          .toSingle()
		                          .subscribe(new SingleSubscriber<ResponseEntity<Map>>()
		                          {
			                          @Override
			                          public void onSuccess(final ResponseEntity<Map> mapResponseEntity)
			                          {
				                          if (isSentSuccessfully(mapResponseEntity))
				                          {
					                          LOG.info(
							                          "The cancellation request for SAP order [{}] linked to hybris order [{}] has been sent successfully to the SAP backend through SCPI! {}",
							                          sapCpiOutboundOrderCancellation.getOrderId(),
							                          orderCancelRecordEntry.getModificationRecord().getOrder().getCode(),
							                          getPropertyValue(mapResponseEntity, RESPONSE_MESSAGE));
				                          }
				                          else
				                          {
					                          LOG.error(
							                          "The cancellation request for SAP order [{}] linked to hybris order [{}] has not been sent to the SAP backend! {}",
							                          sapCpiOutboundOrderCancellation.getOrderId(),
							                          orderCancelRecordEntry.getModificationRecord().getOrder().getCode(),
							                          getPropertyValue(mapResponseEntity, RESPONSE_MESSAGE));
				                          }
			                          }

			                          @Override
			                          public void onError(final Throwable throwable)
			                          {
				                          LOG.error(
						                          "The cancellation request for SAP order [{}] linked to hybris order [{}] has not been sent to the SAP backend through SCPI!",
						                          sapCpiOutboundOrderCancellation.getOrderId(),
						                          orderCancelRecordEntry.getModificationRecord().getOrder().getCode(), throwable);
			                          }
		                          });
	}

	protected SapCpiOutboundService getSapCpiOutboundService()
	{
		return sapCpiOutboundService;
	}

	@Required
	public void setSapCpiOutboundService(final SapCpiOutboundService sapCpiOutboundService)
	{
		this.sapCpiOutboundService = sapCpiOutboundService;
	}

	protected SapCpiOrderOutboundConversionService getSapCpiOrderOutboundConversionService()
	{
		return sapCpiOrderOutboundConversionService;
	}

	@Required
	public void setSapCpiOrderOutboundConversionService(
			final SapCpiOrderOutboundConversionService sapCpiOrderOutboundConversionService)
	{
		this.sapCpiOrderOutboundConversionService = sapCpiOrderOutboundConversionService;
	}

}
