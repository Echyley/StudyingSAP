/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.impl;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.integrationservices.util.TestApplicationContext;
import de.hybris.platform.odata2services.odata.EdmProviderFactory;
import de.hybris.platform.odata2services.odata.schema.SchemaGenerator;

import org.apache.olingo.odata2.api.processor.ODataContext;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

@UnitTest
public class DefaultEdmProviderFactoryUnitTest
{
	private static final String ENTITY_TYPE = "entityType";
	private static final String SERVICE = "service";

	private final EdmProviderFactory edmProviderFactory = createEdmProviderFactory();
	private final EdmProviderFactory defaultFactory = new DefaultEdmProviderFactory();

	@ClassRule
	public static final TestApplicationContext applicationContext = new TestApplicationContext();

	@BeforeClass
	public static void setup()
	{
		applicationContext.addBean("oDataSchemaGenerator", mock(SchemaGenerator.class));
	}

	@Test
	public void testConstructorWithNullSchemaGenerator()
	{
		assertThatThrownBy(() -> new DefaultEdmProviderFactory(null))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("schemaGenerator must not be null");
	}

	@Test
	public void testDefaultConstructorCreateInstance()
	{
		assertNotNull(defaultFactory.createInstance(givenContext()));
	}

	@Test
	public void testCreateInstance()
	{
		assertNotNull(edmProviderFactory.createInstance(givenContext()));
	}

	@Test
	public void testDefaultConstructorCreateInstanceWithNullContext()
	{
		assertThatThrownBy(() -> defaultFactory.createInstance(null))
				.isInstanceOf(NullPointerException.class);
	}

	@Test
	public void testCreateInstanceWithNullContext()
	{
		assertThatThrownBy(() -> edmProviderFactory.createInstance(null))
				.isInstanceOf(NullPointerException.class);
	}

	private static ODataContext givenContext()
	{
		final ODataContext context = mock(ODataContext.class);
		lenient().when(context.getParameter(ENTITY_TYPE)).thenReturn("Product");
		lenient().when(context.getParameter(SERVICE)).thenReturn("InboundProduct");
		lenient().when(context.getHttpMethod()).thenReturn("GET");
		return context;
	}

	private static EdmProviderFactory createEdmProviderFactory()
	{
		return new DefaultEdmProviderFactory(mock(SchemaGenerator.class));
	}
}
