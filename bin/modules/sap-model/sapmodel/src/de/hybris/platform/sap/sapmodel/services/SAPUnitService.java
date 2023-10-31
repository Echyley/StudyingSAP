/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapmodel.services;

import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.product.UnitService;


/**
 * Interface to provide access to UnitModel information for a given product SAPCode
 */
public interface SAPUnitService extends UnitService
{


	/**
	 * Get UnitModel for a given product SAPCode
	 * @param code String representation of SAPCode
	 * @return Returns UnitModel object associated with provided SAPCode
	 */
	public UnitModel getUnitForSAPCode(final String code);

}
