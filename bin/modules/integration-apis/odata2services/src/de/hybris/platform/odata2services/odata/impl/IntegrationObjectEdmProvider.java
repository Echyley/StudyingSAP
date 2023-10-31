/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.impl;

import static de.hybris.platform.odata2services.constants.Odata2servicesConstants.ENTITY_TYPE;
import static de.hybris.platform.odata2services.constants.Odata2servicesConstants.SERVICE;

import de.hybris.platform.integrationservices.exception.IntegrationAttributeException;
import de.hybris.platform.integrationservices.exception.IntegrationObjectItemAndClassConflictException;
import de.hybris.platform.integrationservices.model.GraphOperationsFactory;
import de.hybris.platform.integrationservices.model.IntegrationObjectDescriptor;
import de.hybris.platform.integrationservices.model.IntegrationObjectGraphOperations;
import de.hybris.platform.integrationservices.model.TypeDescriptor;
import de.hybris.platform.integrationservices.service.IntegrationObjectDescriptorService;
import de.hybris.platform.integrationservices.service.IntegrationObjectService;
import de.hybris.platform.integrationservices.util.Log;
import de.hybris.platform.odata2services.odata.OData2ServicesException;
import de.hybris.platform.odata2services.odata.schema.SchemaGenerator;
import de.hybris.platform.odata2services.odata.schema.utils.SchemaUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.odata2.api.edm.FullQualifiedName;
import org.apache.olingo.odata2.api.edm.provider.Association;
import org.apache.olingo.odata2.api.edm.provider.AssociationSet;
import org.apache.olingo.odata2.api.edm.provider.EdmProvider;
import org.apache.olingo.odata2.api.edm.provider.EntityContainer;
import org.apache.olingo.odata2.api.edm.provider.EntityContainerInfo;
import org.apache.olingo.odata2.api.edm.provider.EntitySet;
import org.apache.olingo.odata2.api.edm.provider.EntityType;
import org.apache.olingo.odata2.api.edm.provider.Schema;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.slf4j.Logger;

import com.google.common.base.Preconditions;

public class IntegrationObjectEdmProvider extends EdmProvider
{
	private static final Logger LOG = Log.getLogger(IntegrationObjectEdmProvider.class);

	private final SchemaGenerator schemaGenerator;
	private final String type;
	private final String serviceName;
	private IntegrationObjectDescriptorService descriptorService;
	private GraphOperationsFactory graphOperationsFactory;
	private Schema schema;

	/**
	 * @deprecated use {@code #IntegrationObjectEdmProvider(SchemaGenerator, ODataContext)} instead.
	 */
	@Deprecated(since = "2211.FP1", forRemoval = true)
	IntegrationObjectEdmProvider(final IntegrationObjectService integrationObjectService,
	                             final SchemaGenerator schemaGenerator, final ODataContext context)
	{
		this(schemaGenerator, context);
	}

	IntegrationObjectEdmProvider(@NotNull final SchemaGenerator schemaGenerator,
	                             @NotNull final ODataContext context)
	{
		super();
		this.schemaGenerator = schemaGenerator;
		type = getParameter(ENTITY_TYPE, context);
		serviceName = getParameter(SERVICE, context);
	}

	@Override
	public List<Schema> getSchemas() throws ODataException
	{
		Preconditions.checkArgument(StringUtils.isNotBlank(serviceName), "Service must be provided when generating schemas");

		try
		{
			final Schema schemaForType = StringUtils.isEmpty(type)
					? generateSchemaForAllTypes()
					: generateSchemaForType();
			return Collections.singletonList(schemaForType);
		}
		catch (final OData2ServicesException | IntegrationAttributeException | IntegrationObjectItemAndClassConflictException e)
		{
			throw e;
		}
		catch (final RuntimeException e)
		{
			throw new ODataException(e);
		}
	}

	private Schema generateSchemaForAllTypes()
	{
		LOG.debug("Reading schema for all types for service '{}'.", serviceName);

		if (schema == null)
		{
			final Optional<IntegrationObjectDescriptor> ioDescriptor = getDescriptor(serviceName);
			final Collection<TypeDescriptor> types = ioDescriptor.map(IntegrationObjectDescriptor::getItemTypeDescriptors)
			                                                     .orElse(Collections.emptySet());
			schema = schemaGenerator.generate(types);
		}
		return schema;
	}

	private Schema generateSchemaForType()
	{
		LOG.debug("Reading schema for service '{}' and type '{}'.", serviceName, type);

		Collection<TypeDescriptor> types = Collections.emptySet();
		final Optional<IntegrationObjectDescriptor> ioDescriptor = getDescriptor(serviceName);

		if (ioDescriptor.isPresent())
		{
			final Optional<IntegrationObjectGraphOperations> graphSearch = getGraphSearch(ioDescriptor.get());
			types = graphSearch.map(search -> search.findTypesRelatedTo(type))
			                   .orElse(Collections.emptySet());
		}
		return schemaGenerator.generate(types);
	}

	private static String getParameter(final String param, final ODataContext context)
	{
		final Object value = context.getParameter(param);
		return value != null ? value.toString() : "";
	}

	private Optional<IntegrationObjectDescriptor> getDescriptor(final String ioCode)
	{
		return Optional.ofNullable(descriptorService)
		               .map(s -> s.retrieve(ioCode));
	}

	private Optional<IntegrationObjectGraphOperations> getGraphSearch(final IntegrationObjectDescriptor descriptor)
	{
		return Optional.ofNullable(graphOperationsFactory)
		               .map(f -> f.create(descriptor));
	}

	@Override
	public EntitySet getEntitySet(final String entityContainer, final String entitySetName)
	{
		Preconditions.checkArgument(StringUtils.isNotEmpty(entityContainer), "Requested container name should not be null");
		Preconditions.checkArgument(StringUtils.isNotEmpty(entitySetName), "Requested entity name should not be null");

		final EntityContainer container = generateSchemaForAllTypes().getEntityContainers()
		                                                             .stream()
		                                                             .filter(con -> entityContainer.equals(con.getName()))
		                                                             .findFirst()
		                                                             .orElse(null);

		if (container != null)
		{
			return container.getEntitySets()
			                .stream()
			                .filter(entitySet -> entitySetName.equals(entitySet.getName()))
			                .findFirst()
			                .orElse(null);
		}
		return null;
	}

	@Override
	public EntityType getEntityType(final FullQualifiedName entityTypeName)
	{
		Preconditions.checkArgument(StringUtils.isNotEmpty(entityTypeName.getName()), "Requested entity type should not be null");

		return generateSchemaForAllTypes().getEntityTypes()
		                                  .stream()
		                                  .filter(entityType -> entityType.getName().equals(entityTypeName.getName()))
		                                  .findFirst()
		                                  .orElse(null);
	}

	@Override
	public Association getAssociation(final FullQualifiedName associationName)
	{
		Preconditions.checkArgument(StringUtils.isNotEmpty(associationName.getName()),
				"Requested entity type should not be null");

		if (SchemaUtils.NAMESPACE.equals(associationName.getNamespace()))
		{
			return generateSchemaForAllTypes().getAssociations()
			                                  .stream()
			                                  .filter(association -> association.getName().equals(associationName.getName()))
			                                  .findFirst()
			                                  .orElse(null);
		}
		return null;
	}

	@Override
	public AssociationSet getAssociationSet(final String entityContainer, final FullQualifiedName associationName,
	                                        final String sourceEntitySetName, final String sourceEntitySetRole)
	{
		Preconditions.checkArgument(StringUtils.isNotEmpty(associationName.getName()),
				"Requested entity association name should not be null");

		if (SchemaUtils.CONTAINER_NAME.equals(entityContainer))
		{
			return generateSchemaForAllTypes().getEntityContainers().get(0).getAssociationSets()
			                                  .stream()
			                                  .filter(associationSet -> associationName.equals(associationSet.getAssociation()))
			                                  .findFirst()
			                                  .orElse(null);
		}
		return null;
	}

	@Override
	public EntityContainerInfo getEntityContainerInfo(final String entityContainerName)
	{
		if (SchemaUtils.CONTAINER_NAME.equals(entityContainerName) || StringUtils.isEmpty(entityContainerName))
		{
			final EntityContainerInfo entityContainerInfo = new EntityContainerInfo();
			entityContainerInfo.setName(SchemaUtils.CONTAINER_NAME);
			entityContainerInfo.setDefaultEntityContainer(true);
			return entityContainerInfo;
		}
		return null;
	}

	/**
	 * Setter of the descriptorService attribute used to get the Integration Object Descriptor from an Integration Object
	 *
	 * @param descriptorService the IntegrationObjectDescriptorService
	 */
	public void setDescriptorService(final IntegrationObjectDescriptorService descriptorService)
	{
		this.descriptorService = descriptorService;
	}

	/**
	 * Setter of the graphOperationsFactory attribute used to get the IntegrationObjectGraphOperations service from an
	 * Integration Object Descriptor
	 *
	 * @param graphOperationsFactory the GraphOperationsFactory
	 */
	public void setGraphOperationsFactory(final GraphOperationsFactory graphOperationsFactory)
	{
		this.graphOperationsFactory = graphOperationsFactory;
	}
}
