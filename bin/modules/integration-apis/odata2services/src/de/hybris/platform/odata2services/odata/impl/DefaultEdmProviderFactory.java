/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.impl;

import de.hybris.platform.integrationservices.model.GraphOperationsFactory;
import de.hybris.platform.integrationservices.service.IntegrationObjectDescriptorService;
import de.hybris.platform.integrationservices.service.IntegrationObjectService;
import de.hybris.platform.integrationservices.util.ApplicationBeans;
import de.hybris.platform.odata2services.odata.EdmProviderFactory;
import de.hybris.platform.odata2services.odata.schema.SchemaGenerator;

import javax.validation.constraints.NotNull;

import org.apache.olingo.odata2.api.edm.provider.EdmProvider;
import org.apache.olingo.odata2.api.processor.ODataContext;

import com.google.common.base.Preconditions;

public class DefaultEdmProviderFactory implements EdmProviderFactory
{
	private IntegrationObjectDescriptorService integrationObjectDescriptorService;
	private GraphOperationsFactory graphOperationsFactory;
	private SchemaGenerator schemaGenerator;

	/**
	 * Instantiates a new DefaultEdmProviderFactory.
	 *
	 * @deprecated use #DefaultEdmProviderFactory(SchemaGenerator) instead.
	 */
	@Deprecated(since = "2211.FP1", forRemoval = true)
	public DefaultEdmProviderFactory()
	{
		this(ApplicationBeans.getBean("oDataSchemaGenerator", SchemaGenerator.class));
	}

	/**
	 * Instantiates a new EdmProviderFactory.
	 *
	 * @param generator a schema generator
	 */
	protected DefaultEdmProviderFactory(@NotNull final SchemaGenerator generator)
	{
		Preconditions.checkArgument(generator != null, "schemaGenerator must not be null");

		schemaGenerator = generator;
	}

	@Override
	public EdmProvider createInstance(final ODataContext context)
	{
		final IntegrationObjectEdmProvider provider = new IntegrationObjectEdmProvider(schemaGenerator, context);
		provider.setDescriptorService(integrationObjectDescriptorService);
		provider.setGraphOperationsFactory(graphOperationsFactory);
		return provider;
	}

	public void setIntegrationObjectDescriptorService(final IntegrationObjectDescriptorService service)
	{
		integrationObjectDescriptorService = service;
	}

	public void setGraphOperationsFactory(final GraphOperationsFactory factory)
	{
		this.graphOperationsFactory = factory;
	}

	/**
	 * @deprecated use {@link #setIntegrationObjectDescriptorService(IntegrationObjectDescriptorService)} instead.
	 */
	@Deprecated(since = "2211.FP1", forRemoval = true)
	public void setIntegrationObjectService(final IntegrationObjectService integrationObjectService)
	{
		// this method does nothing.
	}

	/**
	 * @deprecated use new constructor to set schema generator.
	 */
	@Deprecated(since = "2211.FP1", forRemoval = true)
	public void setSchemaGenerator(final SchemaGenerator schemaGenerator)
	{
		this.schemaGenerator = schemaGenerator;
	}
}
