/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.ysaprcomsfulfillment.actions.consignment;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.sap.sapmodel.enums.ConsignmentEntryStatus;
import de.hybris.platform.sap.saporderexchangeoms.model.SapConsignmentProcessModel;
import com.sap.hybris.ysaprcomsfulfillment.actions.order.SapOmsAbstractAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Checks the state of the consignment to perform changes (e.g., setting the
 * consignment status) according to the consignment's state changes resulted
 * from the actions performed on it.
 */
public class SapVerifyConsignmentCompletionAction extends SapOmsAbstractAction<SapConsignmentProcessModel> {

	private static Logger LOGGER = LoggerFactory.getLogger(SapVerifyConsignmentCompletionAction.class);

	@Override
	public String execute(final SapConsignmentProcessModel sapConsignmentProcessModel) throws Exception {

		if (isConsignmentShipped(sapConsignmentProcessModel.getConsignment())) {

			LOGGER.info("All the consignment entries are shipped.");

			final ConsignmentModel consignment = sapConsignmentProcessModel.getConsignment();
			consignment.setStatus(ConsignmentStatus.SHIPPED);
			getModelService().save(consignment);

			return Transition.OK.toString();
		}

		LOGGER.info("Waiting for all the consignment entries to be shipped.");

		return Transition.WAIT.toString();
	}

	protected boolean isConsignmentShipped(ConsignmentModel consignment) {

		return consignment
				.getConsignmentEntries()
				.stream()
				.allMatch(
						consignmentEntry -> consignmentEntry.getStatus().equals(
								ConsignmentEntryStatus.SHIPPED));

	}

}
