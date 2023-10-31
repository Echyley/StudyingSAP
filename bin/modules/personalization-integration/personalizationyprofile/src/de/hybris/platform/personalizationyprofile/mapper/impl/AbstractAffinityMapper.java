/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.personalizationyprofile.mapper.impl;

import de.hybris.platform.personalizationintegration.mapping.MappingData;
import de.hybris.platform.personalizationintegration.mapping.SegmentMappingData;
import de.hybris.platform.personalizationyprofile.mapper.affinity.CxConsumptionLayerAffinityStrategy;
import de.hybris.platform.personalizationyprofile.yaas.Affinity;
import de.hybris.platform.personalizationyprofile.yaas.Profile;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * @deprecated since 2011, Profile structure has changed and code is no longer valid.
 */
@Deprecated(since = "2011", forRemoval = true)
public abstract class AbstractAffinityMapper<T extends Affinity> extends AbstractBaseMapper
{
	private CxConsumptionLayerAffinityStrategy affinityStrategy;

	@Override
	public void populateAfterValidation(final Profile source, final MappingData target)
	{
		final List<SegmentMappingData> segmentMappings = getAffinityMap(source)//
				.map(this::createSegmentMappingList)//
				.orElse(Collections.emptyList());
		normalizeAffinity(segmentMappings);
		target.getSegments().addAll(segmentMappings);
	}

	protected abstract Optional<Map<String, T>> getAffinityMap(final Profile profile);

	protected List<SegmentMappingData> createSegmentMappingList(final Map<String, T> affinityMap)
	{
		return affinityMap.entrySet().stream()//
				.map(entry -> createSegmentMapping(entry.getKey(), entry.getValue()))//
				.collect(Collectors.toList());
	}

	protected SegmentMappingData createSegmentMapping(final String affinityKey, final T affinity)
	{
		final SegmentMappingData segmentMappingData = new SegmentMappingData();
		segmentMappingData.setCode(getSegmentCode(affinityKey));
		segmentMappingData.setAffinity(affinityStrategy.extract(affinity));
		return segmentMappingData;
	}

	protected void normalizeAffinity(final List<SegmentMappingData> data)
	{
		//by default nothing to do
	}

	//Getters and Setters
	public void setAffinityStrategy(final CxConsumptionLayerAffinityStrategy affinityStrategy)
	{
		this.affinityStrategy = affinityStrategy;
	}

	protected CxConsumptionLayerAffinityStrategy getAffinityStrategy()
	{
		return affinityStrategy;
	}

}
