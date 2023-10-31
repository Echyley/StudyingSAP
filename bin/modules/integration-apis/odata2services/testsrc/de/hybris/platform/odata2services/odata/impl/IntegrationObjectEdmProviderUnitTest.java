/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.impl;

import static de.hybris.platform.odata2services.odata.schema.utils.SchemaUtils.toFullQualifiedName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.integrationservices.model.GraphOperationsFactory;
import de.hybris.platform.integrationservices.model.IntegrationObjectDescriptor;
import de.hybris.platform.integrationservices.model.TypeDescriptor;
import de.hybris.platform.integrationservices.model.impl.IntegrationObjectGraphSearch;
import de.hybris.platform.integrationservices.service.IntegrationObjectDescriptorService;
import de.hybris.platform.integrationservices.service.IntegrationObjectService;
import de.hybris.platform.odata2services.odata.schema.SchemaGenerator;
import de.hybris.platform.odata2services.odata.schema.utils.SchemaUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.olingo.odata2.api.edm.FullQualifiedName;
import org.apache.olingo.odata2.api.edm.provider.Association;
import org.apache.olingo.odata2.api.edm.provider.AssociationSet;
import org.apache.olingo.odata2.api.edm.provider.EntityContainer;
import org.apache.olingo.odata2.api.edm.provider.EntityContainerInfo;
import org.apache.olingo.odata2.api.edm.provider.EntitySet;
import org.apache.olingo.odata2.api.edm.provider.EntityType;
import org.apache.olingo.odata2.api.edm.provider.Schema;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class IntegrationObjectEdmProviderUnitTest
{
	private static final String ENTITY_TYPE_VALUE = "MyType";
	private static final String ENTITYSET_NAME = "Products";
	private static final String ENTITY_NAME = "TestProduct";
	private static final String NAMESPACE = "TestOData";
	private static final String INVALID = "Invalid";
	private static final String ENTITY_TYPE = "entityType";
	private static final String SERVICE = "service";
	private static final String SERVICE_VALUE = "MyService";
	private static final Schema EMPTY_SCHEMA = new Schema();
	private static final Schema SOME_SCHEMA = new Schema();
	private static final FullQualifiedName EMPTY_FQNAME = new FullQualifiedName("Any Namespace", "");

	@InjectMocks
	private IntegrationObjectEdmProvider provider;
	@Mock
	private IntegrationObjectDescriptorService descriptorService;
	@Mock
	private IntegrationObjectDescriptor descriptor;
	@Mock
	private GraphOperationsFactory graphOperationsFactory;
	@Mock
	private SchemaGenerator schemaGenerator;
	@Mock
	private ODataContext context;

	@Before
	public void setUp()
	{
		integrationObjectDescriptorFound();
		contextContainsServiceAndTypeParameter();
		doReturn(EMPTY_SCHEMA).when(schemaGenerator).generate(Collections.emptySet());

		provider = new IntegrationObjectEdmProvider(schemaGenerator, context);
		provider.setDescriptorService(descriptorService);
		provider.setGraphOperationsFactory(graphOperationsFactory);
	}

	@Test
	public void testGetSchemas() throws ODataException
	{
		schemaGeneratorProducesSchemaForType(SOME_SCHEMA);

		final List<Schema> schemas = whenGetSchemasIsCalled();

		assertThat(schemas)
				.containsExactly(SOME_SCHEMA);
	}

	@Test
	public void testGetSchemas_NoTypeSpecified() throws ODataException
	{
		contextContainsNoTypeParameter();
		schemaGeneratorProducesSchemaForAllIntegrationObjectTypes(SOME_SCHEMA);
		provider = new IntegrationObjectEdmProvider(mock(IntegrationObjectService.class), schemaGenerator, context);
		provider.setDescriptorService(descriptorService);
		provider.setGraphOperationsFactory(graphOperationsFactory);

		final List<Schema> schemas = whenGetSchemasIsCalled();

		assertThat(schemas)
				.containsExactly(SOME_SCHEMA);
	}

	@Test
	public void testGetSchemas_NoEntityTypeSpecified() throws ODataException
	{
		contextContainsNoTypeParameter();
		schemaGeneratorProducesSchemaForAllIntegrationObjectTypes(SOME_SCHEMA);
		provider = new IntegrationObjectEdmProvider(schemaGenerator, context);
		provider.setDescriptorService(descriptorService);
		provider.setGraphOperationsFactory(graphOperationsFactory);

		final List<Schema> schemas = whenGetSchemasIsCalled();

		assertThat(schemas)
				.containsExactly(SOME_SCHEMA);
	}

	@Test
	public void testGetSchemas_NoEntityTypeSpecified_IsCachedAfterFirstCall() throws ODataException
	{
		contextContainsNoTypeParameter();
		schemaGeneratorProducesSchemaForAllIntegrationObjectTypes(SOME_SCHEMA);
		provider = new IntegrationObjectEdmProvider(schemaGenerator, context);
		provider.setDescriptorService(descriptorService);
		provider.setGraphOperationsFactory(graphOperationsFactory);

		whenGetSchemasIsCalled();
		whenGetSchemasIsCalled();

		verify(schemaGenerator, times(1)).generate(any());
	}

	@Test
	public void testSomethingBadHappens()
	{
		final IntegrationObjectGraphSearch graphSearch = mock(IntegrationObjectGraphSearch.class);
		doReturn(graphSearch).when(graphOperationsFactory).create(descriptor);
		doThrow(new RuntimeException("Test expected exception")).when(graphSearch).findTypesRelatedTo(ENTITY_TYPE_VALUE);

		assertThatThrownBy(() -> provider.getSchemas()).isInstanceOf(ODataException.class);
	}

	@Test
	public void testGetSchemas_NoServiceSpecified()
	{
		contextContainsNoServiceParameter();
		provider = new IntegrationObjectEdmProvider(schemaGenerator, context);

		assertThatThrownBy(() -> provider.getSchemas()).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void testGetEntitySet()
	{
		schemaGeneratorProducesSchemaForAllIntegrationObjectTypes(
				getSchemaWithContainerAndEntityAssociations(SchemaUtils.CONTAINER_NAME));

		final EntitySet entitySet = provider.getEntitySet(SchemaUtils.CONTAINER_NAME, ENTITYSET_NAME);

		assertThat(entitySet)
				.isNotNull();
	}

	@Test
	public void testGetEntitySet_WithDifferentContainerName()
	{
		schemaGeneratorProducesSchemaForAllIntegrationObjectTypes(getSchemaWithContainerAndEntityAssociations(INVALID));

		final EntitySet entitySet = provider.getEntitySet(SchemaUtils.CONTAINER_NAME, ENTITYSET_NAME);

		assertThat(entitySet)
				.isNull();
	}

	@Test
	public void testGetEntitySet_WithOutEntityContainerName()
	{
		assertThatThrownBy(() -> provider.getEntitySet(StringUtils.EMPTY, ENTITYSET_NAME)).isInstanceOf(
				IllegalArgumentException.class);
	}

	@Test
	public void testGetEntitySet_Null()
	{
		schemaGeneratorProducesSchemaForAllIntegrationObjectTypes(
				getSchemaWithContainerAndEntityAssociations(SchemaUtils.CONTAINER_NAME));

		final EntitySet entitySet = provider.getEntitySet(SchemaUtils.CONTAINER_NAME, INVALID);

		assertThat(entitySet)
				.isNull();
	}

	@Test
	public void testGetEntitySet_WithOutEntitySetName()
	{
		assertThatThrownBy(() -> provider.getEntitySet(SchemaUtils.CONTAINER_NAME, StringUtils.EMPTY)).isInstanceOf(
				IllegalArgumentException.class);
	}

	@Test
	public void testGetAssociationSet()
	{
		schemaGeneratorProducesSchemaForAllIntegrationObjectTypes(
				getSchemaWithContainerAndEntityAssociations(SchemaUtils.CONTAINER_NAME));

		final AssociationSet associationSet = provider.getAssociationSet(SchemaUtils.CONTAINER_NAME,
				toFullQualifiedName("Product"),
				StringUtils.EMPTY, StringUtils.EMPTY);

		assertThat(associationSet)
				.isNotNull();
	}

	@Test
	public void testGetAssociationSet_WithOutAssociationName()
	{
		assertThatThrownBy(
				() -> provider.getAssociationSet(StringUtils.EMPTY, EMPTY_FQNAME, StringUtils.EMPTY, StringUtils.EMPTY))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void testGetAssociationSet_Null()
	{
		schemaGeneratorProducesSchemaForAllIntegrationObjectTypes(
				getSchemaWithContainerAndEntityAssociations(SchemaUtils.CONTAINER_NAME));

		final AssociationSet associationSet = provider.getAssociationSet(SchemaUtils.CONTAINER_NAME,
				toFullQualifiedName(INVALID), StringUtils.EMPTY, StringUtils.EMPTY);

		assertThat(associationSet)
				.isNull();
	}

	@Test
	public void testGetAssociationSet_InvalidContainer()
	{
		schemaGeneratorProducesSchemaForAllIntegrationObjectTypes(getSchemaWithContainerAndEntityAssociations(INVALID));

		final AssociationSet associationSet = provider.getAssociationSet(SchemaUtils.CONTAINER_NAME,
				toFullQualifiedName("ProductInvalid"), StringUtils.EMPTY, StringUtils.EMPTY);

		assertThat(associationSet)
				.isNull();
	}


	@Test
	public void testGetEntityType()
	{
		schemaGeneratorProducesSchemaForAllIntegrationObjectTypes(getSchemaWithEntityType());
		final EntityType entityType = provider.getEntityType(toFullQualifiedName(ENTITY_NAME));

		assertThat(entityType)
				.isNotNull();
	}

	@Test
	public void testGetEntityType_WithOutEntityName()
	{
		assertThatThrownBy(() -> provider.getEntityType(EMPTY_FQNAME))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void testGetEntityType_Null()
	{
		schemaGeneratorProducesSchemaForAllIntegrationObjectTypes(getSchemaWithEntityType());
		final EntityType entityType = provider.getEntityType(toFullQualifiedName(INVALID));

		assertThat(entityType)
				.isNull();
	}

	@Test
	public void testGetAssociation()
	{
		schemaGeneratorProducesSchemaForAllIntegrationObjectTypes(getSchemaWithEntityAssociations());

		final Association association = provider.getAssociation(toFullQualifiedName(NAMESPACE));

		assertThat(association)
				.isNotNull();
	}

	@Test
	public void testGetAssociation_WithOutAssociation()
	{
		assertThatThrownBy(() -> provider.getAssociation(EMPTY_FQNAME))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void testGetAssociation_Null()
	{
		schemaGeneratorProducesSchemaForAllIntegrationObjectTypes(getSchemaWithEntityAssociations());

		final Association association = provider.getAssociation(toFullQualifiedName(INVALID));

		assertThat(association)
				.isNull();
	}

	@Test
	public void testGetEntityContainerInfo_WithOutContainerName()
	{
		final EntityContainerInfo containerInfo = provider.getEntityContainerInfo(null);

		assertThat(containerInfo)
				.isNotNull();
		assertThat(containerInfo.isDefaultEntityContainer())
				.isTrue();
	}

	@Test
	public void testGetEntityContainerInfo()
	{
		final EntityContainerInfo containerInfo = provider.getEntityContainerInfo(SchemaUtils.CONTAINER_NAME);

		assertThat(containerInfo)
				.isNotNull();
		assertThat(containerInfo.isDefaultEntityContainer())
				.isTrue();
	}

	@Test
	public void tesDescriptorServiceNotProvided_EntityTypeSpecified() throws ODataException
	{
		provider.setDescriptorService(null);

		final List<Schema> schemas = whenGetSchemasIsCalled();

		assertThat(schemas).containsExactly(EMPTY_SCHEMA);
	}

	@Test
	public void tesDescriptorServiceNotProvided_NoEntityTypeSpecified() throws ODataException
	{
		contextContainsNoTypeParameter();

		final List<Schema> schemas = whenGetSchemasIsCalled();

		assertThat(schemas).containsExactly(EMPTY_SCHEMA);
	}

	@Test
	public void testDescriptorServiceReturnNoDescriptor_EntityTypeSpecified() throws ODataException
	{
		integrationObjectDescriptorNotFound();

		final List<Schema> schemas = whenGetSchemasIsCalled();

		assertThat(schemas).containsExactly(EMPTY_SCHEMA);
	}

	@Test
	public void testDescriptorServiceReturnNoDescriptor_NoEntityTypeSpecified() throws ODataException
	{
		integrationObjectDescriptorNotFound();
		contextContainsNoTypeParameter();

		final List<Schema> schemas = whenGetSchemasIsCalled();

		assertThat(schemas).containsExactly(EMPTY_SCHEMA);
	}

	@Test
	public void testGraphOperationsFactoryNotProvided() throws ODataException
	{
		provider.setGraphOperationsFactory(null);

		final List<Schema> schemas = whenGetSchemasIsCalled();

		assertThat(schemas).containsExactly(EMPTY_SCHEMA);
	}

	@Test
	public void testGraphOperationsFactoryReturnNoGraphSearch() throws ODataException
	{
		integrationObjectGraphSearchNotFound();

		final List<Schema> schemas = whenGetSchemasIsCalled();

		assertThat(schemas).containsExactly(EMPTY_SCHEMA);
	}

	private void schemaGeneratorProducesSchemaForType(final Schema schema)
	{
		final var typeDescriptors = Set.of(mock(TypeDescriptor.class));
		final var integrationObjectGraphSearch = mock(IntegrationObjectGraphSearch.class);
		doReturn(integrationObjectGraphSearch).when(graphOperationsFactory).create(descriptor);
		doReturn(typeDescriptors).when(integrationObjectGraphSearch).findTypesRelatedTo(ENTITY_TYPE_VALUE);
		doReturn(schema).when(schemaGenerator).generate(typeDescriptors);
	}

	private void schemaGeneratorProducesSchemaForAllIntegrationObjectTypes(final Schema schema)
	{
		final var typeDescriptor1 = mock(TypeDescriptor.class);
		final var typeDescriptor2 = mock(TypeDescriptor.class);
		final var typeDescriptors = Set.of(typeDescriptor1, typeDescriptor2);
		doReturn(typeDescriptors).when(descriptor).getItemTypeDescriptors();
		doReturn(schema).when(schemaGenerator).generate(typeDescriptors);
	}

	private Schema getSchemaWithEntityType()
	{
		final EntityType entityType = mock(EntityType.class);
		final List<EntityType> ENTITY_TYPES = Collections.singletonList(entityType);
		doReturn(ENTITY_NAME).when(entityType).getName();

		final Schema schema = new Schema();
		schema.setEntityTypes(ENTITY_TYPES);
		return schema;
	}

	private Schema getSchemaWithContainerAndEntityAssociations(final String containerName)
	{
		final EntityContainer container = mock(EntityContainer.class);
		final EntitySet entitySet = mock(EntitySet.class);
		final AssociationSet associationSet = mock(AssociationSet.class);
		final List<AssociationSet> ASSOCIATION_SET = Collections.singletonList(associationSet);
		final List<EntityContainer> ENTITY_CONTAINERS = Collections.singletonList(container);
		final List<EntitySet> ENTITY_SETS = Collections.singletonList(entitySet);
		doReturn(ENTITY_SETS).when(container).getEntitySets();
		doReturn(containerName).when(container).getName();
		doReturn(ASSOCIATION_SET).when(container).getAssociationSets();
		doReturn(ENTITYSET_NAME).when(entitySet).getName();
		doReturn(toFullQualifiedName("Product")).when(associationSet).getAssociation();

		final Schema schema = new Schema();
		schema.setEntityContainers(ENTITY_CONTAINERS);
		return schema;
	}

	private Schema getSchemaWithEntityAssociations()
	{
		final Association association = mock(Association.class);
		doReturn(NAMESPACE).when(association).getName();
		final List<Association> associationList = Collections.singletonList(association);

		final Schema schema = new Schema();
		schema.setAssociations(associationList);
		return schema;
	}

	private void contextContainsServiceAndTypeParameter()
	{
		doReturn(ENTITY_TYPE_VALUE).when(context).getParameter(ENTITY_TYPE);
		doReturn(SERVICE_VALUE).when(context).getParameter(SERVICE);
	}

	private void contextContainsNoTypeParameter()
	{
		doReturn("").when(context).getParameter(ENTITY_TYPE);
	}

	private void contextContainsNoServiceParameter()
	{
		doReturn("").when(context).getParameter(SERVICE);
	}

	private void integrationObjectDescriptorFound()
	{
		doReturn(descriptor).when(descriptorService).retrieve(SERVICE_VALUE);
	}

	private void integrationObjectDescriptorNotFound()
	{
		doReturn(null).when(descriptorService).retrieve(SERVICE_VALUE);
	}

	private void integrationObjectGraphSearchNotFound()
	{
		doReturn(null).when(graphOperationsFactory).create(descriptor);
	}

	private List<Schema> whenGetSchemasIsCalled() throws ODataException
	{
		return provider.getSchemas();
	}
}
