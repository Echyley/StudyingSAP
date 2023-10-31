/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.personalizationyprofile.mapper.impl;

import de.hybris.platform.personalizationyprofile.yaas.Affinities;
import de.hybris.platform.personalizationyprofile.yaas.Insights;
import de.hybris.platform.personalizationyprofile.yaas.Location;
import de.hybris.platform.personalizationyprofile.yaas.LocationsAffinity;
import de.hybris.platform.personalizationyprofile.yaas.Profile;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;


/**
 * @deprecated since 2011, Profile structure has changed and code is no longer valid. Recommended mapper to use is
 *             {@link CxSegmentMapper}
 */
@Deprecated(since = "2011", forRemoval = true)
public class CxLocationConsumptionLayerMapper extends AbstractAffinityMapper<LocationsAffinity>
{
	private static final String DIVIDER = "_";
	private static final String REQUIRED_FIELD = "insights.affinities.locations";
	private static final String ENABLED_PROPERTY = "personalizationyprofile.mapper.location.enabled";
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
	protected Optional<Map<String, LocationsAffinity>> getAffinityMap(final Profile profile)
	{
		return Optional.of(profile)//
				.map(Profile::getInsights)//
				.map(Insights::getAffinities)//
				.map(Affinities::getLocations)//
				.map(Map::values) //
				.map(this::getMappedLocations);
	}

	protected Map<String, LocationsAffinity> getMappedLocations(final Collection<LocationsAffinity> original)
	{
		final Map<String, LocationsAffinity> mapped = new HashMap<>();

		original.stream().filter(Objects::nonNull).forEach(value -> {
			final Location location = value.getLocation();
			if (location == null)
			{
				return;
			}
			addCountry(mapped, value, location);
			addRegion(mapped, value, location);
			addCity(mapped, value, location);
		});

		return mapped;
	}

	protected void addCountry(final Map<String, LocationsAffinity> map, final LocationsAffinity locationAffiinity,
			final Location location)
	{
		final String countryCode = location.getCountryCode();
		if (countryCode != null)
		{
			map.merge(countryCode, locationAffiinity, this::sumLocation);
		}
	}

	protected void addRegion(final Map<String, LocationsAffinity> map, final LocationsAffinity locationAffiinity,
			final Location location)
	{
		final String countryCode = location.getCountryCode();
		final String regionCode = location.getRegionCode();

		if (countryCode != null && regionCode != null)
		{
			map.merge(String.join(DIVIDER, countryCode, regionCode), locationAffiinity, this::sumLocation);
		}
	}

	protected void addCity(final Map<String, LocationsAffinity> map, final LocationsAffinity locationAffiinity,
			final Location location)
	{
		final String countryCode = location.getCountryCode();
		final String regionCode = location.getRegionCode();
		final String city = location.getCity();
		if (countryCode != null && regionCode != null && city != null)
		{
			map.merge(String.join(DIVIDER, countryCode, regionCode, city), locationAffiinity, this::sumLocation);
		}
	}

	protected LocationsAffinity sumLocation(final LocationsAffinity a, final LocationsAffinity b)
	{
		final LocationsAffinity result = new LocationsAffinity();
		result.setRecentCount(
				Optional.ofNullable(a.getRecentCount()).orElse(0) + Optional.ofNullable(b.getRecentCount()).orElse(0));
		result.setScore(Optional.ofNullable(a.getScore()).orElse(BigDecimal.ZERO)
				.add(Optional.ofNullable(b.getScore()).orElse(BigDecimal.ZERO)));
		result.setRecentScore(Optional.ofNullable(a.getRecentScore()).orElse(BigDecimal.ZERO)
				.add(Optional.ofNullable(b.getRecentScore()).orElse(BigDecimal.ZERO)));
		return result;
	}

}
