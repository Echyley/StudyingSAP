/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.export.impl;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.integrationservices.model.DescriptorFactory;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.integrationservices.model.TypeDescriptor;
import de.hybris.platform.integrationservices.service.IntegrationObjectService;
import de.hybris.platform.integrationservices.util.Log;
import de.hybris.platform.odata2services.config.ODataServicesConfiguration;
import de.hybris.platform.odata2services.dto.ConfigurationBundleEntity;
import de.hybris.platform.odata2services.dto.IntegrationObjectBundleEntity;
import de.hybris.platform.odata2services.export.ExportConfigurationSearchService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;

import com.google.common.base.Preconditions;


/**
 * The default implementation for the interface {@link ExportConfigurationSearchService}.
 */
public class DefaultExportConfigurationSearchService implements ExportConfigurationSearchService
{
	private static final Logger LOG = Log.getLogger(DefaultExportConfigurationSearchService.class);
	private static final String QUERY = "SELECT {pk} FROM {%s} WHERE {pk}=?pk";
	private static final String STREAM_CONFIG_QUERY = "SELECT DISTINCT {sc.pk} FROM {OutboundSyncStreamConfiguration AS sc " +
			"JOIN OutboundChannelConfiguration AS occ ON {sc:outboundChannelConfiguration} = {occ:pk}} " +
			"WHERE {occ.code}=?property";
	private static final String JOB_QUERY = "SELECT DISTINCT {sj.pk} FROM {OutboundSyncJob AS sj " +
			"JOIN OutboundSyncStreamConfigurationContainer AS scc ON {sj:streamConfigurationContainer} = {scc:pk}} " +
			"WHERE {scc.id}=?property";
	private static final String CRON_JOB_QUERY = "SELECT DISTINCT {scj.pk} FROM {OutboundSyncCronJob AS scj " +
			"JOIN OutboundSyncJob AS sj ON {scj:job} = {sj:pk}} " +
			"WHERE {sj.code}=?property";
	private static final String TRIGGER_QUERY = "SELECT DISTINCT {tr.pk} FROM {Trigger AS tr " +
			"JOIN OutboundSyncCronJob AS cj ON {tr:cronjob}={cj:pk}} " +
			"WHERE {cj.code}=?property";

	private static final String INTEGRATION_OBJECT = "integrationObject";
	private static final String PROPERTY = "property";

	private final IntegrationObjectService integrationObjectService;
	private final FlexibleSearchService flexibleSearchService;
	private final ODataServicesConfiguration configurationService;
	private final TypeService typeService;
	private final DescriptorFactory descriptorFactory;


	/**
	 * Instantiates a new export configuration search service.
	 *
	 * @param integrationObjectService the integration object search service
	 * @param flexibleSearchService    the flexible search service integration objects
	 * @param configurationService     the configuration service to read the configurations
	 * @param typeService              the type descriptor service to get the runtime attributes
	 * @param descriptorFactory        the descriptor factory to create an integration object descriptor
	 */
	public DefaultExportConfigurationSearchService(
			@NotNull final IntegrationObjectService integrationObjectService,
			@NotNull final FlexibleSearchService flexibleSearchService,
			@NotNull final ODataServicesConfiguration configurationService,
			@NotNull final TypeService typeService,
			@NotNull final DescriptorFactory descriptorFactory)
	{
		Preconditions.checkArgument(integrationObjectService != null, "integrationObjectService must not be null");
		Preconditions.checkArgument(flexibleSearchService != null, "flexibleSearchService must not be null");
		Preconditions.checkArgument(configurationService != null, "configurationService must not be null");
		Preconditions.checkArgument(typeService != null, "typeService must not be null");
		Preconditions.checkArgument(descriptorFactory != null, "descriptorFactory must not be null");

		this.integrationObjectService = integrationObjectService;
		this.flexibleSearchService = flexibleSearchService;
		this.configurationService = configurationService;
		this.typeService = typeService;
		this.descriptorFactory = descriptorFactory;
	}

	@Override
	public Set<ItemModel> findRootItemInstances(final IntegrationObjectBundleEntity integrationObjectBundleEntity)
	{
		final IntegrationObjectModel io = findExportableIntegrationObjectByCode(
				integrationObjectBundleEntity.getIntegrationObjectCode());
		return integrationObjectBundleEntity.getRootItemInstancePks()
		                                    .stream()
		                                    .map(rootItemInstancePk -> findRootItemInstance(io, rootItemInstancePk))
		                                    .collect(Collectors.toCollection(LinkedHashSet::new));
	}

	@Override
	public Set<IntegrationObjectModel> getExportableIntegrationObjects()
	{
		return configurationService.getExportableIntegrationObjects().stream()
		                           .filter(Strings::isNotBlank)
		                           .map(this::findIntegrationObjectIfExist)
		                           .filter(Optional::isPresent)
		                           .map(Optional::get)
		                           .collect(Collectors.toCollection(LinkedHashSet::new));
	}

	@Override
	public IntegrationObjectModel findExportableIntegrationObjectByCode(final String ioCode)
	{
		final IntegrationObjectModel integrationObject = integrationObjectService.findIntegrationObject(ioCode);
		throwExceptionIfNotExportableIO(integrationObject);
		return integrationObject;
	}

	@Override
	public Set<AttributeDescriptorModel> findRuntimeAttributeDescriptors(final ConfigurationBundleEntity configBundle)
	{
		return configBundle.getIntegrationObjectBundles()
		                   .stream()
		                   .map(this::findRootItemInstanceDescriptors)
		                   .filter(Objects::nonNull)
		                   .flatMap(Collection::stream)
		                   .collect(Collectors.toSet());
	}

	@Override
	public Set<ItemModel> findSteamConfigurations(final String occCode)
	{
		return findItems(STREAM_CONFIG_QUERY, occCode);
	}

	@Override
	public Set<ItemModel> findJobs(final String containerId)
	{
		return findItems(JOB_QUERY, containerId);
	}

	@Override
	public Set<ItemModel> findCronJobs(final String jobCode)
	{
		return findItems(CRON_JOB_QUERY, jobCode);
	}

	@Override
	public Set<ItemModel> findTriggers(final String cronJobCode)
	{
		return findItems(TRIGGER_QUERY, cronJobCode);
	}

	private Set<ItemModel> findItems(final String query, final String property)
	{
		final var flexibleSearchQuery = new FlexibleSearchQuery(query);
		flexibleSearchQuery.setResultClassList(Collections.singletonList(ItemModel.class));
		flexibleSearchQuery.addQueryParameter(PROPERTY, property);
		final SearchResult<ItemModel> queryResult = flexibleSearchService.search(flexibleSearchQuery);
		return new HashSet<>(queryResult.getResult());
	}

	private ItemModel findRootItemInstance(final IntegrationObjectModel integrationObject, final String pk)
	{
		final String rootItemTypeCode = integrationObject.getRootItem().getType().getCode();
		final var query = new FlexibleSearchQuery(String.format(QUERY, rootItemTypeCode));
		query.addQueryParameter(ItemModel.PK, pk);
		try
		{
			final ItemModel itemModel = flexibleSearchService.searchUnique(query);
			LOG.debug("Integration object root instance with pk [{}] has been found.", pk);
			return itemModel;

		}
		catch (final ModelNotFoundException ex)
		{
			LOG.error("Integration object root instance with pk [{}] has not been found.", pk);
			throw ex;
		}
	}

	private Optional<IntegrationObjectModel> findIntegrationObjectIfExist(final String ioCode)
	{
		try
		{
			final IntegrationObjectModel ioModel = integrationObjectService.findIntegrationObject(ioCode);
			return Optional.of(ioModel);
		}
		catch (final ModelNotFoundException ex)
		{
			LOG.warn("Integration object with code [{}] has not been found.", ioCode);
			return Optional.empty();
		}
	}

	private void throwExceptionIfNotExportableIO(final IntegrationObjectModel io)
	{

		if (isIntegrationObjectNotExportable(io))
		{
			throw new NonExportableIntegrationObjectException(io.getCode());
		}

		if (hasNoRootItem(io))
		{
			throw new NonExportableIntegrationObjectNoRootItemException(io.getCode());
		}

	}

	private boolean isIntegrationObjectNotExportable(final IntegrationObjectModel io)
	{
		return getExportableIntegrationObjects().stream()
		                                        .map(IntegrationObjectModel::getCode)
		                                        .noneMatch(it -> it.equals(io.getCode()));
	}

	private boolean hasNoRootItem(final IntegrationObjectModel integrationObjectModel)
	{
		return integrationObjectModel.getRootItem() == null;
	}

	private Set<AttributeDescriptorModel> findRootItemInstanceDescriptors(final IntegrationObjectBundleEntity ioBundle)
	{
		return findRootItemInstances(ioBundle).stream()
		                                      .map(this::findTypeDescriptors)
		                                      .flatMap(Collection::stream)
		                                      .map(this::findAttributeDescriptors)
		                                      .flatMap(Collection::stream)
		                                      .filter(attrDesc -> isConfiguredAttribute(attrDesc, ioBundle))
		                                      .collect(Collectors.toSet());
	}

	private Set<TypeDescriptor> findTypeDescriptors(final ItemModel rootItemInstance)
	{
		return descriptorFactory.createIntegrationObjectDescriptor(rootItemInstance.getProperty(INTEGRATION_OBJECT))
		                        .getItemTypeDescriptors();
	}

	private Collection<AttributeDescriptorModel> findAttributeDescriptors(final TypeDescriptor typeDescriptor)
	{
		final ComposedTypeModel composedType = typeService.getComposedTypeForCode(typeDescriptor.getTypeCode());
		return typeService.getRuntimeAttributeDescriptorsForType(composedType);
	}

	private boolean isConfiguredAttribute(final AttributeDescriptorModel attrDesc,
	                                      final IntegrationObjectBundleEntity ioBundle)
	{
		return findRootItemInstances(ioBundle).stream()
		                                      .map(this::findTypeDescriptors)
		                                      .flatMap(Collection::stream)
		                                      .map(TypeDescriptor::getAttributes)
		                                      .flatMap(Collection::stream)
		                                      .anyMatch(typeAttrDesc -> typeAttrDesc.getQualifier()
		                                                                            .equals(attrDesc.getQualifier()));
	}

}
