/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.personalizationyprofile.mapper.impl;

import de.hybris.platform.personalizationyprofile.yaas.Affinities;
import de.hybris.platform.personalizationyprofile.yaas.Affinity;
import de.hybris.platform.personalizationyprofile.yaas.Insights;
import de.hybris.platform.personalizationyprofile.yaas.Profile;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;


/**
 * @deprecated since 2011, Profile structure has changed and code is no longer valid. Recommended mapper to use is
 *             {@link CxSegmentMapper}
 */
@Deprecated(since = "2011", forRemoval = true)
public class CxProductConsumptionLayerMapper extends AbstractAffinityMapper<Affinity>
{
	private static final String REQUIRED_FIELD = "insights.affinities.products";
	private static final String ENABLED_PROPERTY = "personalizationyprofile.mapper.product.enabled";
	private static final Set<String> REQUIRED_FIELDS = Collections.singleton(REQUIRED_FIELD);

	@Override
	protected Set<String> getDefaultRequiredFields()
	{
		return REQUIRED_FIELDS;
	}

	@Override
	protected String getEnabledProperty()
	{
		return ENABLED_PROPERTY;
	}

	@Override
	protected Optional<Map<String, Affinity>> getAffinityMap(final Profile profile)
	{
		return Optional.of(profile)//
				.map(Profile::getInsights)//
				.map(Insights::getAffinities)//
				.map(Affinities::getProducts);
	}

}
