/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.personalizationyprofile.mapper.impl;

import de.hybris.platform.personalizationintegration.mapping.MappingData;
import de.hybris.platform.personalizationintegration.mapping.SegmentMappingData;
import de.hybris.platform.personalizationservices.configuration.CxConfigurationService;
import de.hybris.platform.personalizationservices.model.config.CxConfigModel;
import de.hybris.platform.personalizationyprofile.yaas.Insights;
import de.hybris.platform.personalizationyprofile.yaas.Metrics;
import de.hybris.platform.personalizationyprofile.yaas.OrderMetrics;
import de.hybris.platform.personalizationyprofile.yaas.Profile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


/**
 * @deprecated since 2011 Recommended mapper to use is {@link CxSegmentMapper}
 */
@Deprecated(since = "2011", forRemoval = false)
public class CxOrderMapper extends AbstractBaseMapper
{
	private static final Logger LOG = LoggerFactory.getLogger(CxOrderMapper.class.getName());
	private static final String REQUIRED_FIELD = "insights.metrics.orders";
	private static final String ENABLED_PROPERTY = "personalizationyprofile.mapper.order.enabled";
	private static final Set<String> REQUIRED_FIELDS = Collections.singleton(REQUIRED_FIELD);
	private static final int AFFINITY_BIGDECIMAL_SCALE = 5;

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
		final Optional<BigDecimal> ordersValue = Optional.of(source)//
				.map(Profile::getInsights)//
				.map(Insights::getMetrics)//
				.map(Metrics::getOrders)//
				.map(OrderMetrics::getAllOrdersValuesSum);

		cxConfigurationService.getConfiguration()//
				.flatMap(configModel -> getSegment(configModel, ordersValue.orElse(null)))//
				.ifPresent(target.getSegments()::add);

	}

	protected Optional<SegmentMappingData> getSegment(final CxConfigModel configModel, final BigDecimal ordersValue)
	{
		return configModel.getOrderMapperSegmentMap()//
				.entrySet()//
				.stream()//
				.filter(entry -> isAboveThreshold(ordersValue, entry.getValue()))//
				.max(Map.Entry.comparingByValue())//
				.map(segment -> createSegment(//
						segment.getKey(), //
						calculateAffinity(ordersValue, segment.getValue())//
				));
	}

	protected boolean isAboveThreshold(final BigDecimal ordersValue, final BigDecimal configuredOrdersValue)
	{
		if (ordersValue != null)
		{
			return ordersValue.compareTo(configuredOrdersValue) > 0;
		}
		return false;
	}

	protected BigDecimal calculateAffinity(final BigDecimal ordersValue, final BigDecimal configuredOrdersValue)
	{
		try
		{
			return (ordersValue == null) ? BigDecimal.ZERO
					: ordersValue.divide(configuredOrdersValue, AFFINITY_BIGDECIMAL_SCALE, RoundingMode.HALF_UP);
		}
		catch (final ArithmeticException e)
		{
			LOG.debug("Exception during affinity calculation. Using default value.", e);
			return BigDecimal.ZERO;
		}
	}

	protected SegmentMappingData createSegment(final String name, final BigDecimal affinity)
	{
		final SegmentMappingData segment = new SegmentMappingData();
		segment.setCode(name);
		segment.setAffinity(affinity);

		return segment;
	}

	public CxConfigurationService getCxConfigurationService()
	{
		return cxConfigurationService;
	}

	@Required
	public void setCxConfigurationService(final CxConfigurationService cxConfigurationService)
	{
		this.cxConfigurationService = cxConfigurationService;
	}

}
