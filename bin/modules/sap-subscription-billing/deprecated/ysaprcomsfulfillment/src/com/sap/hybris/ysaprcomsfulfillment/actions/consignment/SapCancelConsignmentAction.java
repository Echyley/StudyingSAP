/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.ysaprcomsfulfillment.actions.consignment;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.processengine.BusinessProcessEvent;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.sap.saporderexchangeoms.model.SapConsignmentProcessModel;
import com.sap.hybris.ysaprcomsfulfillment.actions.order.SapOmsAbstractAction;
import com.sap.hybris.ysaprcomsfulfillment.constants.YsaprcomsfulfillmentConstants;
import org.springframework.beans.factory.annotation.Required;

import java.util.Collection;

/**
 * Cancel SAP OMS consignment processes
 */
public class SapCancelConsignmentAction extends SapOmsAbstractAction<SapConsignmentProcessModel> {

	private BusinessProcessService businessProcessService;

	@Override
	public String execute(SapConsignmentProcessModel process) throws Exception {

		final ConsignmentModel consignment = process.getConsignment();
		consignment.setStatus(ConsignmentStatus.CANCELLED);
		getModelService().save(consignment);

		if (isCancellationComplete(process.getParentProcess().getSapConsignmentProcesses())) {

			process.setDone(true);
			getModelService().save(process);
		    
			triggerCancelOrderEvent(process);
			
			return Transition.OK.toString();

		} else {

			return Transition.WAIT.toString();
		}

	}

	protected boolean isCancellationComplete(Collection<SapConsignmentProcessModel> sapConsignmentProcesses) {

		return sapConsignmentProcesses.stream().allMatch(
				sapConsignmentProcess -> sapConsignmentProcess.getConsignment().getStatus()
						.equals(ConsignmentStatus.CANCELLED));

	}

	protected void triggerCancelOrderEvent(SapConsignmentProcessModel sapConsignmentProcess) {

		final String eventID = new StringBuilder()//
		        .append(sapConsignmentProcess.getParentProcess().getCode())//
		        .append(YsaprcomsfulfillmentConstants.UNDERSCORE)//
				.append(YsaprcomsfulfillmentConstants.ORDER_ACTION_EVENT_NAME)//
				.toString();

		final BusinessProcessEvent event = BusinessProcessEvent.builder(eventID)
				.withChoice(YsaprcomsfulfillmentConstants.CANCEL_ORDER).build();
  
		getBusinessProcessService().triggerEvent(event);

	}

	@SuppressWarnings("javadoc")
	public BusinessProcessService getBusinessProcessService() {
		return businessProcessService;
	}

	@SuppressWarnings("javadoc")
	@Required
	public void setBusinessProcessService(final BusinessProcessService businessProcessService) {
		this.businessProcessService = businessProcessService;
	}

}
