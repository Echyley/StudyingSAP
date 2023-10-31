/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.facades.populator;

import de.hybris.platform.commercefacades.product.data.FeatureData;
import de.hybris.platform.commercefacades.product.data.FeatureValueData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.sap.productconfig.facades.overview.CharacteristicValue;

import java.util.List;



/**
 * Populates individual variant default values obtained from the hybris classification system as {@link FeatureData}
 * into product configuration {@link CharacteristicValue} as required by the configuration overview page.<br>
 * This populator is triggered by the {@link VariantOverviewPopulator}.
 */
public class VariantOverviewValuePopulator extends AbstractOverviewPopulator implements
		Populator<FeatureData, List<CharacteristicValue>>
{

	@Override
	public void populate(final FeatureData source, final List<CharacteristicValue> target)
	{
		final List<FeatureValueData> featureValues = (List) source.getFeatureValues();
		final int size = featureValues.size();
		for (int i = 0; i < size; i++)
		{
			final CharacteristicValue value = new CharacteristicValue();
			value.setCharacteristic(source.getName());
			value.setValue(featureValues.get(i).getValue());
			value.setValuePositionType(determineValuePositionType(size, i));
			target.add(value);
		}
	}

}
