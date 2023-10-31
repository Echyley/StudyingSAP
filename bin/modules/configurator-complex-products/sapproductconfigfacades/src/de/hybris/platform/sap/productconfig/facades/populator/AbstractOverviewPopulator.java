/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.facades.populator;

import de.hybris.platform.sap.productconfig.facades.overview.ValuePositionTypeEnum;


/**
 * Abstract base class for populators suitable to populate the product configuration overview page.
 */
public abstract class AbstractOverviewPopulator
{

	/**
	 * Determines value for valuePositionType depending on position of value and size of value list.
	 *
	 * @param sizeOfValueList
	 * @param index
	 * @return valuePositionType
	 */
	protected ValuePositionTypeEnum determineValuePositionType(final int sizeOfValueList, final int index)
	{
		ValuePositionTypeEnum valuePositionType = ValuePositionTypeEnum.INTERJACENT;
		if (index == 0 && sizeOfValueList == 1)
		{
			valuePositionType = ValuePositionTypeEnum.ONLY_VALUE;
		}
		else if (index == 0 && sizeOfValueList > 1)
		{
			valuePositionType = ValuePositionTypeEnum.FIRST;
		}
		else if (index == sizeOfValueList - 1)
		{
			valuePositionType = ValuePositionTypeEnum.LAST;
		}
		return valuePositionType;
	}

}
