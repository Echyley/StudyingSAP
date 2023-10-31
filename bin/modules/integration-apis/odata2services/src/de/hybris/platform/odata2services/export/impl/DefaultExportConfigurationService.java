/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.export.impl;

import de.hybris.platform.odata2services.dto.ConfigurationBundleEntity;
import de.hybris.platform.odata2services.dto.ExportEntity;
import de.hybris.platform.odata2services.export.ExportConfigurationDecorator;
import de.hybris.platform.odata2services.export.ExportConfigurationService;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.convert.converter.Converter;

/**
 * The default implementation for the interface {@link ExportConfigurationService}.
 */
public class DefaultExportConfigurationService implements ExportConfigurationService
{

	private List<Converter<ConfigurationBundleEntity, Set<ExportEntity>>> converters;
	private List<ExportConfigurationDecorator> decorators;

	@Override
	public Set<ExportEntity> generateExportConfiguration(final ConfigurationBundleEntity configurationBundle)
	{
		final List<Set<ExportEntity>> convertedEntities = converters.stream()
		                                                            .map(converter -> converter.convert(configurationBundle))
		                                                            .collect(Collectors.toList());

		final Set<ExportEntity> baseEntities = convertedEntities.get(0);
		final Set<ExportEntity> runtimeEntities = convertedEntities.get(1);
		return decorateRuntimeEntities(runtimeEntities, decorateEntities(baseEntities));
	}

	private Set<ExportEntity> decorateEntities(final Set<ExportEntity> baseEntities)
	{
		return CollectionUtils.isNotEmpty(baseEntities) ? decorateExportEntities(baseEntities) : baseEntities;
	}

	private Set<ExportEntity> decorateRuntimeEntities(final Set<ExportEntity> runtimeEntities,
	                                                  final Set<ExportEntity> decoratedEntities)
	{
		final Set<ExportEntity> exportedEntities = new LinkedHashSet<>();
		Stream.of(runtimeEntities, decoratedEntities).filter(CollectionUtils::isNotEmpty).forEach(exportedEntities::addAll);
		return exportedEntities;
	}

	private Set<ExportEntity> decorateExportEntities(final Set<ExportEntity> baseEntities)
	{
		Set<ExportEntity> decoratedEntities = Collections.unmodifiableSet(baseEntities);
		for (final ExportConfigurationDecorator decorator : decorators)
		{
			decoratedEntities = decorator.decorate(decoratedEntities);
		}
		return decoratedEntities;
	}

	public void setDecorators(final List<ExportConfigurationDecorator> decorators)
	{
		this.decorators = Collections.unmodifiableList(decorators);
	}

	List<ExportConfigurationDecorator> getDecorators()
	{
		return Collections.unmodifiableList(decorators);
	}

	public void setConverters(
			final List<Converter<ConfigurationBundleEntity, Set<ExportEntity>>> converters)
	{
		this.converters = Collections.unmodifiableList(converters);
	}

	List<Converter<ConfigurationBundleEntity, Set<ExportEntity>>> getConverters()
	{
		return Collections.unmodifiableList(converters);
	}

}
