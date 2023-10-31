/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.personalizationyprofile.mapper;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.personalizationintegration.mapping.MappingData;
import de.hybris.platform.personalizationyprofile.yaas.Profile;

import java.util.Set;


public interface CxConsumptionLayerProfileMapper extends Populator<Profile, MappingData>
{
	/**
	 * Method define which profile attributes are needed by mapper.<br/>
	 * They will be send to profile service.<br/>
	 *
	 * @return set of profile attributes needed by mapper.<br/>
	 *         Example : [insights.affinities.products, insights.affinities.categories]
	 */
	Set<String> getRequiredFields();
}
