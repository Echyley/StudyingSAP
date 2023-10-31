/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy;

import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.strategy.GetAllStrategy;

/**
 * Implementation for ERP 6.04 of StrategyFactoryERP. <br>
 * 
 * @see StrategyFactoryERP
 * @version 1.0
 */
public class StrategyFactoryERP604 extends StrategyFactoryERP {

    @Override
    public GetAllStrategy createGetAllStrategy() {
        return new GetAllStrategyERP604();
    }

}
