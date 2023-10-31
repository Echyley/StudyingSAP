/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy;

import java.util.List;


/**
 * In case of ECC 605, we also request payment service provider information from the EPR backend
 *
 *
 */
public class LrdActionsStrategyERP605 extends LrdActionsStrategyERP
{

	/**
	 * Default constructor
	 */
	public LrdActionsStrategyERP605()
	{
		super();
		setActiveFieldsListCreateChange605(activeFieldsListCreateChange);
	}


	private static void setActiveFieldsListCreateChange605(final List<SetActiveFieldsListEntry> activeFieldsListCreateChange)
	{
		// HEAD
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("HEAD", "SPPAYM"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("LPAYSP", "PS_PROVIDER"));
	}

}
