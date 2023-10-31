/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy;

import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.strategy.GetAllStrategy;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.strategy.LrdActionsStrategy;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.strategy.SetStrategy;


/**
 * Holder of access (interaction procedure) for a specific release of ERP. <br>
 * 
 * @version 1.0
 */
public abstract class StrategyFactoryERP
{

	/**
	 * Access (interaction procedure) for reading a sales document from ERP with all aspects..<br>
	 * 
	 * @return get all strategy
	 */
	public abstract GetAllStrategy createGetAllStrategy();


	/**
	 * Access (interaction procedure) for LORD API.<br>
	 * 
	 * @return LORD API strategy
	 */
	public LrdActionsStrategy createLrdActionsStrategy()
	{
		return new LrdActionsStrategyERP();
	}

	/**
	 * Access (interaction procedure) for ERP_LORD_SET.<br>
	 * 
	 * @return ERP_LORD_SET strategy
	 */
	public SetStrategy createSetStrategy()
	{
		return new SetStrategyERP();
	}


}
