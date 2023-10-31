/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.schema.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.integrationservices.model.DescriptorFactory;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;
import de.hybris.platform.integrationservices.model.TypeDescriptor;
import de.hybris.platform.integrationservices.util.TestApplicationContext;
import de.hybris.platform.odata2services.odata.schema.SchemaElementGenerator;

import java.util.Collections;
import java.util.Set;

import org.apache.olingo.odata2.api.edm.provider.EntityType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PrimitiveEntityTypeGeneratorUnitTest {
	private static final String JAVA_STRING_CLASS_PATH = "java.lang.String";
	private static final IntegrationObjectItemModel IO_ITEM = new IntegrationObjectItemModel();

	@Rule
	public TestApplicationContext applicationContext = new TestApplicationContext();
	@Mock(lenient = true)
	private SchemaElementGenerator<EntityType, String> primitiveCollectionMemberEntityTypeGenerator;
	@Mock(lenient = true)
	private TypeAttributeDescriptor typeAttributeDescriptor;
	@Mock(lenient = true)
	private TypeDescriptor elementTypeDescriptor;
	@Mock(lenient = true)
	private EntityType entityType;
	@Mock(lenient = true)
	private DescriptorFactory descriptorFactory;
	@InjectMocks
	private PrimitiveEntityTypeGenerator primitiveEntityTypeGenerator;

	@Before
	public void setUp() {
		final TypeDescriptor integrationObjectItemTypeDescriptor = mock(TypeDescriptor.class);
		lenient().when(integrationObjectItemTypeDescriptor.getAttributes()).thenReturn(Collections.singleton(typeAttributeDescriptor));

		doReturn(integrationObjectItemTypeDescriptor).when(descriptorFactory).createItemTypeDescriptor(IO_ITEM);

		lenient().when(typeAttributeDescriptor.getAttributeType()).thenReturn(elementTypeDescriptor);
		lenient().when(elementTypeDescriptor.getItemCode()).thenReturn(JAVA_STRING_CLASS_PATH);
		lenient().when(primitiveCollectionMemberEntityTypeGenerator.generate(any())).thenReturn(entityType);
	}

	@Test
	public void testGenerate() {
		givenIsCollectionReturns(true);
		givenHasPrimitiveElementsReturns(true);

		final Set<EntityType> generatedEntityTypes = primitiveEntityTypeGenerator.generate(IO_ITEM);

		assertThat(generatedEntityTypes).hasSameElementsAs(Collections.singletonList(entityType));
	}

	@Test
	public void testGenerateWhenIsCollectionReturnsFalse() {
		givenIsCollectionReturns(false);
		givenHasPrimitiveElementsReturns(true);

		final Set<EntityType> generatedEntityTypes = primitiveEntityTypeGenerator.generate(IO_ITEM);

		assertThat(generatedEntityTypes).isEmpty();
	}

	@Test
	public void testGenerateWhenIsPrimitiveReturnsFalse() {
		givenIsCollectionReturns(true);
		givenHasPrimitiveElementsReturns(false);

		final Set<EntityType> generatedEntityTypes = primitiveEntityTypeGenerator.generate(IO_ITEM);

		assertThat(generatedEntityTypes).isEmpty();
	}

	@Test
	public void testCreateItemTypeDescriptorWhenDescriptorFactoryIsNotInjected() {
		primitiveEntityTypeGenerator.setDescriptorFactory(null);
		final DescriptorFactory df = mock(DescriptorFactory.class);
		applicationContext.addBean("integrationServicesDescriptorFactory", df);

		assertThat(primitiveEntityTypeGenerator.getDescriptorFactory()).isSameAs(df);
	}

	private void givenHasPrimitiveElementsReturns(final boolean b) {
		lenient().when(elementTypeDescriptor.isPrimitive()).thenReturn(b);
	}

	private void givenIsCollectionReturns(final boolean b) {
		lenient().when(typeAttributeDescriptor.isCollection()).thenReturn(b);
	}
}
