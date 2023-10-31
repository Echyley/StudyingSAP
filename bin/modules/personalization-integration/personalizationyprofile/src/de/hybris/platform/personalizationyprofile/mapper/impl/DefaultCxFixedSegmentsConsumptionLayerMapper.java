/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.personalizationyprofile.mapper.impl;

import de.hybris.platform.personalizationintegration.mapping.mapper.impl.CxFixedSegmentsProfileMapper;
import de.hybris.platform.personalizationyprofile.mapper.CxConsumptionLayerProfileMapper;
import de.hybris.platform.personalizationyprofile.yaas.Profile;

import java.util.Collections;
import java.util.Set;


public class DefaultCxFixedSegmentsConsumptionLayerMapper extends CxFixedSegmentsProfileMapper<Profile> implements
		CxConsumptionLayerProfileMapper
{
	@Override
	public Set<String> getRequiredFields()
	{
		return Collections.emptySet();
	}
}
