/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.schema;

import de.hybris.platform.integrationservices.exception.IntegrationAttributeException;
import de.hybris.platform.integrationservices.exception.IntegrationObjectItemAndClassConflictException;
import de.hybris.platform.integrationservices.model.DescriptorFactory;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.TypeDescriptor;
import de.hybris.platform.integrationservices.model.impl.ClassTypeDescriptor;
import de.hybris.platform.integrationservices.model.impl.ItemTypeDescriptor;
import de.hybris.platform.integrationservices.util.ApplicationBeans;
import de.hybris.platform.odata2services.odata.InvalidODataSchemaException;
import de.hybris.platform.odata2services.odata.OData2ServicesException;
import de.hybris.platform.odata2services.odata.schema.association.AssociationListGeneratorRegistry;
import de.hybris.platform.odata2services.odata.schema.entity.EntityContainerGenerator;
import de.hybris.platform.odata2services.odata.schema.utils.SchemaUtils;

import java.util.Collection;
import java.util.List;

import org.apache.olingo.odata2.api.edm.provider.Association;
import org.apache.olingo.odata2.api.edm.provider.EntityType;
import org.apache.olingo.odata2.api.edm.provider.Schema;

import com.google.common.base.Preconditions;

/**
 * A default implementation of {@link SchemaGenerator}.
 */
public class DefaultSchemaGenerator implements SchemaGenerator
{
	private static final String CANNOT_GENERATE_FOR_NULL = "Unable to generate schema for null";
	private AssociationListGeneratorRegistry associationListGeneratorRegistry;
	private SchemaElementGenerator<List<EntityType>, Collection<IntegrationObjectItemModel>> entityTypesGenerator;
	private SchemaElementGenerator<List<EntityType>, Collection<TypeDescriptor>> entityTypeListElementGenerator;
	private EntityContainerGenerator entityContainerGenerator;
	private DescriptorFactory descriptorFactory;

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated use the alternative {@link #generate(Collection)}
	 */
	@Override
	@Deprecated(since = "2211.FP1", forRemoval = true)
	public Schema generateSchema(final Collection<IntegrationObjectItemModel> allModelsForType)
	{
		try
		{
			Preconditions.checkArgument(allModelsForType != null, CANNOT_GENERATE_FOR_NULL);
			final Collection<TypeDescriptor> descriptors = toDescriptors(allModelsForType);
			final List<EntityType> entityTypes =
					entityTypesGenerator == null
							? getEntityTypeListElementGenerator().generate(descriptors)
							: entityTypesGenerator.generate(allModelsForType);
			return generateInternal(descriptors, entityTypes);
		}
		catch (final OData2ServicesException | IntegrationAttributeException | IntegrationObjectItemAndClassConflictException e)
		{
			throw e;
		}
		catch (final RuntimeException e)
		{
			throw new InvalidODataSchemaException(e);
		}
	}

	@Override
	public Schema generate(final Collection<TypeDescriptor> descriptors)
	{
		try
		{
			Preconditions.checkArgument(descriptors != null, CANNOT_GENERATE_FOR_NULL);
			validateDescriptorTypes(descriptors);
			final List<EntityType> entityTypes = getEntityTypeListElementGenerator().generate(descriptors);
			return generateInternal(descriptors, entityTypes);
		}
		catch (final OData2ServicesException | IntegrationAttributeException | IntegrationObjectItemAndClassConflictException e)
		{
			throw e;
		}
		catch (final RuntimeException e)
		{
			throw new InvalidODataSchemaException(e);
		}
	}

	private Schema generateInternal(final Collection<TypeDescriptor> descriptors, final List<EntityType> entityTypes)
	{
		final List<Association> associations = getAssociationListGeneratorRegistry().generateFor(descriptors);
		return new Schema()
				.setNamespace(SchemaUtils.NAMESPACE)
				.setAnnotationAttributes(SchemaUtils.createNamespaceAnnotations())
				.setEntityTypes(entityTypes)
				.setAssociations(associations)
				.setEntityContainers(getEntityContainerGenerator().generate(entityTypes, associations));
	}

	private void validateDescriptorTypes(final Collection<TypeDescriptor> descriptors)
	{
		if (descriptors.stream().anyMatch(ItemTypeDescriptor.class::isInstance) &&
				descriptors.stream().anyMatch(ClassTypeDescriptor.class::isInstance))
		{
			final String io = descriptors
					.stream()
					.map(TypeDescriptor::getIntegrationObjectCode)
					.findFirst()
					.orElseThrow(() -> new IllegalArgumentException(CANNOT_GENERATE_FOR_NULL));
			throw new IntegrationObjectItemAndClassConflictException(
					io);
		}
	}

	private Collection<TypeDescriptor> toDescriptors(final Collection<IntegrationObjectItemModel> models)
	{
		return models.stream()
		             .map(getDescriptorFactory()::createItemTypeDescriptor)
		             .toList();
	}

	/**
	 * @deprecated use {@code setEntityTypeListElementGenerator} method
	 */
	@Deprecated(since = "2211.FP1", forRemoval = true)
	public void setEntityTypesGenerator(
			final SchemaElementGenerator<List<EntityType>, Collection<IntegrationObjectItemModel>> entityTypesGenerator)
	{
		this.entityTypesGenerator = entityTypesGenerator;
	}

	public void setEntityTypeListElementGenerator(
			final SchemaElementGenerator<List<EntityType>, Collection<TypeDescriptor>> generator)
	{
		entityTypeListElementGenerator = generator;
	}

	private SchemaElementGenerator<List<EntityType>, Collection<TypeDescriptor>> getEntityTypeListElementGenerator()
	{
		if (entityTypeListElementGenerator == null)
		{
			entityTypeListElementGenerator = ApplicationBeans.getBean("oDataEntityTypeListElementGenerator",
					SchemaElementGenerator.class);
		}
		return entityTypeListElementGenerator;
	}

	public void setAssociationListGeneratorRegistry(final AssociationListGeneratorRegistry associationListGeneratorRegistry)
	{
		this.associationListGeneratorRegistry = associationListGeneratorRegistry;
	}


	private AssociationListGeneratorRegistry getAssociationListGeneratorRegistry()
	{
		if (associationListGeneratorRegistry == null)
		{
			associationListGeneratorRegistry = ApplicationBeans.getBean("oDataAssociationListGeneratorRegistry",
					AssociationListGeneratorRegistry.class);
		}
		return associationListGeneratorRegistry;
	}

	public void setEntityContainerGenerator(final EntityContainerGenerator generator)
	{
		this.entityContainerGenerator = generator;
	}

	private EntityContainerGenerator getEntityContainerGenerator()
	{
		if (entityContainerGenerator == null)
		{
			entityContainerGenerator = ApplicationBeans.getBean("oDataEntityContainerGenerator", EntityContainerGenerator.class);
		}
		return entityContainerGenerator;
	}

	public void setDescriptorFactory(final DescriptorFactory factory)
	{
		descriptorFactory = factory;
	}

	private DescriptorFactory getDescriptorFactory()
	{
		if (descriptorFactory == null)
		{
			descriptorFactory = ApplicationBeans.getBean("integrationServicesDescriptorFactory", DescriptorFactory.class);
		}
		return descriptorFactory;
	}
}
