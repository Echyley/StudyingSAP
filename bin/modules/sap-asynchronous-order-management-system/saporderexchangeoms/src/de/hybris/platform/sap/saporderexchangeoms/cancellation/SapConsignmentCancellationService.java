/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saporderexchangeoms.cancellation;

import de.hybris.platform.ordercancel.OrderCancelResponse;
import de.hybris.platform.warehousing.cancellation.ConsignmentCancellationService;

/**
 * Interface to provide Integration processing for consignment cancellation
 */
public interface SapConsignmentCancellationService extends ConsignmentCancellationService
{

	/**
	 * processSapConsignmentCancellation
	 *
	 * @param orderCancelResponse
	 * 			Order cancel response
	 */
	void processSapConsignmentCancellation(OrderCancelResponse orderCancelResponse);


}
