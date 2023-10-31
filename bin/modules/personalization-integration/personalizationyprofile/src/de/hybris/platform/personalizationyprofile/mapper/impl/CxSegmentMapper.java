/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.personalizationyprofile.mapper.impl;

import de.hybris.platform.personalizationintegration.mapping.MappingData;
import de.hybris.platform.personalizationintegration.mapping.SegmentMappingData;
import de.hybris.platform.personalizationservices.configuration.CxConfigurationService;
import de.hybris.platform.personalizationyprofile.yaas.Profile;
import de.hybris.platform.personalizationyprofile.yaas.Segment;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public class CxSegmentMapper extends AbstractBaseMapper
{
	protected static final BigDecimal AFFINITY = BigDecimal.TEN;
	private static final String REQUIRED_FIELD = "segments";
	private static final String ENABLED_PROPERTY = "personalizationyprofile.mapper.segment.enabled";
	private static final Set<String> REQUIRED_FIELDS = Collections.singleton(REQUIRED_FIELD);

	private CxConfigurationService cxConfigurationService;

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
	public void populateAfterValidation(final Profile source, final MappingData target)
	{
		final List<SegmentMappingData> segmentMappings = source.getSegments() //
				.entrySet().stream() //
				.map(this::buildSegment) //
				.collect(Collectors.toList());
		target.getSegments().addAll(segmentMappings);
	}

	@Override
	protected boolean isSourceValid(final Profile source)
	{
		return source != null && source.getSegments() != null;
	}

	protected SegmentMappingData buildSegment(final Map.Entry<String, Segment> entry)
	{
		final SegmentMappingData result = new SegmentMappingData();

		final String code = getSegmentCode(entry.getKey());
		result.setCode(code);
		result.setAffinity(getAffinity());

		return result;
	}

	protected BigDecimal getAffinity()
	{
		return cxConfigurationService.getMinAffinity().add(AFFINITY);
	}


	protected CxConfigurationService getCxConfigurationService()
	{
		return cxConfigurationService;
	}

	public void setCxConfigurationService(final CxConfigurationService cxConfigurationService)
	{
		this.cxConfigurationService = cxConfigurationService;
	}

}
