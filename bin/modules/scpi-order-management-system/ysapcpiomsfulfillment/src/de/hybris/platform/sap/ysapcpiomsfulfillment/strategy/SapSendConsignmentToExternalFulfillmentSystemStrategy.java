/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.ysapcpiomsfulfillment.strategy;

import static de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService.RESPONSE_MESSAGE;
import static de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService.getPropertyValue;
import static de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService.isSentSuccessfully;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.OrderService;
import de.hybris.platform.orderhistory.OrderHistoryService;
import de.hybris.platform.orderhistory.model.OrderHistoryEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.BusinessProcessEvent;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService;
import de.hybris.platform.sap.sapcpiorderexchange.service.SapCpiOrderOutboundConversionService;
import de.hybris.platform.sap.sapmodel.enums.SAPOrderStatus;
import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;
import de.hybris.platform.sap.sapmodel.model.SAPPlantLogSysOrgModel;
import de.hybris.platform.sap.sapmodel.services.SapPlantLogSysOrgService;
import de.hybris.platform.sap.ysapcpiomsfulfillment.SapCpiOmsFulfillmentUtil;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.warehousing.externalfulfillment.strategy.SendConsignmentToExternalFulfillmentSystemStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.ResponseEntity;

import com.google.common.collect.Sets;

import rx.Single;
import rx.SingleSubscriber;

public class SapSendConsignmentToExternalFulfillmentSystemStrategy implements SendConsignmentToExternalFulfillmentSystemStrategy
{

	private static final Logger LOG = LoggerFactory.getLogger(SapSendConsignmentToExternalFulfillmentSystemStrategy.class);

	private SapPlantLogSysOrgService sapPlantLogSysOrgService;
	private OrderHistoryService orderHistoryService;
	private ModelService modelService;
	private SapCpiOutboundService sapCpiOutboundService;
	private SapCpiOrderOutboundConversionService sapCpiOrderOutboundConversionService;
	private OrderService orderService;
	private TimeService timeService;
	private BusinessProcessService businessProcessService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendConsignment(final ConsignmentModel consignment)
	{
		if (isReplicationNotRequired(consignment))
		{
			if (consignment.getSapOrder() != null)
			{
				LOG.info(
						"The consignment [{}] has already been sent to SAP backend through SAP order [{}] and will not be replicated again!",
						consignment.getCode(),
						consignment.getSapOrder().getCode());
			}
			else
			{
				LOG.info(
						"The newly generated external consignment [{}] will not be replicated to SAP backend since its original consignment has already been replicated!",
						consignment.getCode());
				terminateRedundantConsignmentProcess(consignment);
			}
			return;
		}

		consignment.getConsignmentEntries().forEach(consignmentEntry ->
		{
			consignmentEntry.setSapOrderEntryRowNumber(consignmentEntry.getOrderEntry().getEntryNumber() + 1);
			getModelService().save(consignmentEntry);
		});

		final OrderModel order = (OrderModel) consignment.getOrder();

		// Read customizing data from the base store configuration
		final SAPPlantLogSysOrgModel sapPlantLogSysOrg = getSapPlantLogSysOrgService().getSapPlantLogSysOrgForPlant(
				order.getStore(), consignment.getWarehouse().getCode());

		// Initialize order history entry
		final OrderHistoryEntryModel orderHistoryEntry = initializeOrderHistory(order,
				sapPlantLogSysOrg.getLogSys().getSapLogicalSystemName());

		// Initialize SAPOrder
		final SAPOrderModel sapOrder = initializeSapOrder(orderHistoryEntry, consignment);

		sendOrderToScpi(consignment, orderHistoryEntry).subscribe(
				new SingleSubscriber<ResponseEntity<Map>>()
				{
					@Override
					public void onSuccess(final ResponseEntity<Map> mapResponseEntity)
					{
						if (isSentSuccessfully(mapResponseEntity))
						{
							// Save order history entry
							orderHistoryEntry.setTimestamp(getTimeService().getCurrentTime());
							getOrderHistoryService().saveHistorySnapshot(orderHistoryEntry.getPreviousOrderVersion());
							getModelService().save(orderHistoryEntry);

							// Save SAPOrder
							sapOrder.setSapOrderStatus(SAPOrderStatus.SENT_TO_ERP);
							sapOrder.setOrder(order);
							getModelService().save(sapOrder);

							LOG.info(
									"SAP Order [{}] that is related to Hybris order [{}] has been sent successfully to the SAP backend through SCPI! {}",
									sapOrder.getCode(), order.getCode(), getPropertyValue(mapResponseEntity, RESPONSE_MESSAGE));
						}
						else
						{
							LOG.error("SAP Order [{}] that is related to Hybris order [{}] has not been sent to the SAP backend!",
									sapOrder.getCode(), order.getCode());
						}
					}

					@Override
					public void onError(final Throwable error)
					{
						LOG.error(
								"SAP Order [{}] that is related to Hybris order [{}] has not been sent to the SAP backend through SCPI!",
								sapOrder.getCode(), order.getCode(), error);
					}
				});
	}

	/**
	 * Checks if the consignment has already been sent to an external SAP system
	 *
	 * @param consignment
	 * @return true if the consignment has already been replicated
	 */

	protected boolean isReplicationNotRequired(final ConsignmentModel consignment)
	{
		return consignment.getSapOrder() != null || consignment.getOrder()
		                                                       .getConsignments()
		                                                       .stream()
		                                                       .anyMatch(processedConsignment ->
				                                                       processedConsignment.getSapOrder() != null && processedConsignment
						                                                       .getSapOrder()
						                                                       .getConsignments()
						                                                       .stream()
						                                                       .anyMatch(
								                                                       replicatedConsignment -> findSapPlantLogSysOrg(
										                                                       replicatedConsignment).equals(
										                                                       findSapPlantLogSysOrg(
												                                                       consignment)))
		                                                       );
	}

	/**
	 * Send the consignment to the external SAP system wrapped in an order
	 *
	 * @param consignment
	 * @return
	 */
	protected Single<ResponseEntity<Map>> sendOrderToScpi(final ConsignmentModel consignment,
	                                                      final OrderHistoryEntryModel orderHistoryEntry)
	{
		// Read customizing data from the base store configuration
		final SAPPlantLogSysOrgModel sapPlantLogSysOrg = findSapPlantLogSysOrg(consignment);

		// Clone hybris parent order
		final OrderModel clonedOrder = getOrderService().clone(null, null, consignment.getOrder(),
				orderHistoryEntry.getPreviousOrderVersion().getVersionID());

		final List<AbstractOrderEntryModel> orderEntries = new ArrayList<>();

		// Copy order entries
		consignment.getConsignmentEntries().stream()
		           .forEach(entry -> orderEntries.add(entry.getOrderEntry()));

		// Set cloned order attributes
		clonedOrder.setConsignments(Sets.newHashSet(consignment));
		clonedOrder.setSapLogicalSystem(sapPlantLogSysOrg.getLogSys().getSapLogicalSystemName());
		clonedOrder.setSapSalesOrganization(sapPlantLogSysOrg.getSalesOrg());
		clonedOrder.setEntries(orderEntries);
		clonedOrder.setSapSystemType(sapPlantLogSysOrg.getLogSys().getSapSystemType());
		clonedOrder.setPaymentTransactions(consignment.getOrder().getPaymentTransactions());

		// Send cloned order to SCPI
		return getSapCpiOutboundService().sendOrder(
				getSapCpiOrderOutboundConversionService().convertOrderToSapCpiOrder(clonedOrder)).toSingle();
	}

	/**
	 * Initialize an entry in the order history for the SAP order before sending it to the external system
	 *
	 * @param order
	 * @param logicalSystem
	 * @return
	 */
	protected OrderHistoryEntryModel initializeOrderHistory(final OrderModel order, final String logicalSystem)
	{

		final OrderModel snapshot = getOrderHistoryService().createHistorySnapshot(order);
		final OrderHistoryEntryModel historyEntry = getModelService().create(OrderHistoryEntryModel.class);

		historyEntry.setOrder(order);
		historyEntry.setPreviousOrderVersion(snapshot);

		historyEntry.setDescription(String.format("SAP sales document [%s] has been sent to the logical system [%s]!",
				snapshot.getVersionID(), logicalSystem));

		return historyEntry;

	}

	/**
	 * Initialize an SAP order from the parent Hybris order
	 *
	 * @param orderHistoryEntry
	 * @param consignment
	 * @return SAPOrderModel
	 */
	protected SAPOrderModel initializeSapOrder(final OrderHistoryEntryModel orderHistoryEntry,
	                                           final ConsignmentModel consignment)
	{
		final SAPOrderModel sapOrder = getModelService().create(SAPOrderModel.class);
		sapOrder.setCode(orderHistoryEntry.getPreviousOrderVersion().getVersionID());
		sapOrder.setConsignments(Sets.newHashSet(consignment));
		return sapOrder;
	}

	/**
	 * Find SAP plant configuration information
	 *
	 * @param consignment
	 * @return SAPPlantLogSysOrgModel
	 */
	protected SAPPlantLogSysOrgModel findSapPlantLogSysOrg(final ConsignmentModel consignment)
	{
		return getSapPlantLogSysOrgService().getSapPlantLogSysOrgForPlant(
				consignment.getOrder().getStore(), consignment.getWarehouse().getCode());
	}

	/**
	 * Terminate the redundant consignment process which contains the consignment that has already been replicated to SAP backend
	 *
	 * @param consignment
	 */
	protected void terminateRedundantConsignmentProcess(final ConsignmentModel consignment)
	{
		consignment.getConsignmentProcesses()
		           .stream()
		           .filter(process -> process.getConsignment().getCode().contentEquals(consignment.getCode()))
		           .findFirst()
		           .ifPresent(consignmentProcess -> {
			           triggerConfirmConsignmentEvent(consignment, consignmentProcess);
			           triggerCancelConsignmentEvent(consignment, consignmentProcess);
		           });
	}

	/**
	 * Trigger consignment cancellation event
	 *
	 * @param consignment
	 * @param consignmentProcess
	 */
	protected void triggerCancelConsignmentEvent(final ConsignmentModel consignment,
	                                             final ConsignmentProcessModel consignmentProcess)
	{
		final BusinessProcessEvent event = BusinessProcessEvent.builder(
				new StringBuilder()
						.append(consignmentProcess.getCode())
						.append(SapCpiOmsFulfillmentUtil.CONSIGNMENT_ACTION_EVENT)
						.toString()
		).withChoice(SapCpiOmsFulfillmentUtil.CANCEL_CONSIGNMENT).build();

		LOG.info(
				"Consignment cancellation event [{}] has been triggered for the redundant external consignment [{}]!",
				event, consignment.getCode());
		getBusinessProcessService().triggerEvent(event);
	}

	/**
	 * Trigger consignment confirmation event
	 *
	 * @param consignment
	 * @param consignmentProcess
	 */
	protected void triggerConfirmConsignmentEvent(final ConsignmentModel consignment,
	                                              final ConsignmentProcessModel consignmentProcess)
	{
		final BusinessProcessEvent event = BusinessProcessEvent.builder(
				new StringBuilder()
						.append(consignmentProcess.getCode())
						.append(SapCpiOmsFulfillmentUtil.CONSIGNMENT_SUBMISSION_CONFIRMATION_EVENT)
						.toString()).build();
		LOG.info(
				"Consignment confirmation event [{}] has been triggered for the redundant external consignment [{}]!",
				event, consignment.getCode());
		getBusinessProcessService().triggerEvent(event);
	}

	protected SapPlantLogSysOrgService getSapPlantLogSysOrgService()
	{
		return sapPlantLogSysOrgService;
	}

	@Required
	public void setSapPlantLogSysOrgService(final SapPlantLogSysOrgService sapPlantLogSysOrgService)
	{
		this.sapPlantLogSysOrgService = sapPlantLogSysOrgService;
	}

	protected OrderHistoryService getOrderHistoryService()
	{
		return orderHistoryService;
	}

	@Required
	public void setOrderHistoryService(final OrderHistoryService orderHistoryService)
	{
		this.orderHistoryService = orderHistoryService;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
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

	protected OrderService getOrderService()
	{
		return orderService;
	}

	@Required
	public void setOrderService(final OrderService orderService)
	{
		this.orderService = orderService;
	}

	protected TimeService getTimeService()
	{
		return timeService;
	}

	@Required
	public void setTimeService(final TimeService timeService)
	{
		this.timeService = timeService;
	}

	protected BusinessProcessService getBusinessProcessService()
	{
		return businessProcessService;
	}

	@Required
	public void setBusinessProcessService(final BusinessProcessService businessProcessService)
	{
		this.businessProcessService = businessProcessService;
	}

}
