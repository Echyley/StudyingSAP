/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapomsreturnprocess.returns.strategy;

import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.returns.model.ReturnEntryModel;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;


public class ReturnSourcingContext
{

	private ReturnOrderStartegy returnOrderStrategy;


	public void splitConsignment(final Map<ReturnEntryModel, List<ConsignmentEntryModel>> returnEntryConsignmentListMap)
	{
		returnOrderStrategy.splitOrder(returnEntryConsignmentListMap);
	}

	@Required
	public void setReturnOrderStrategy(final ReturnOrderStartegy returnOrderStrategy)
	{
		this.returnOrderStrategy = returnOrderStrategy;
	}



}
