/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.populator.impl;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSChoice;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConflictingAssumptionModel;


public class ConflictAssumptionPopulator implements Populator<CPSChoice, ConflictingAssumptionModel>
{

	@Override
	public void populate(final CPSChoice source, final ConflictingAssumptionModel target)
	{
		target.setInstanceId(source.getItemId());
		target.setCsticName(source.getCharacteristicId());
		target.setValueName(source.getValue().getValue());
	}
}
