/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices;

import de.hybris.platform.core.PK;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.integrationservices.util.IntegrationObjectTestUtil;
import de.hybris.platform.integrationservices.util.IntegrationTestUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.fest.util.Collections;
import org.junit.rules.ExternalResource;

import com.google.common.base.Preconditions;

/**
 * Integration object builder to build an {@link IntegrationObjectModel}.
 */
public class IntegrationObjectModelBuilder extends ExternalResource
{
	private final Set<PK> createdIntegrationObjectPKs = new HashSet<>();
	private final List<IntegrationObjectItemModelBuilder> itemBuilders = new ArrayList<>();
	private final List<IntegrationObjectClassModelBuilder> classBuilders = new ArrayList<>();
	private final Set<String> integrationObjectCodes = new HashSet<>();
	private String code;

	private IntegrationObjectModelBuilder()
	{
		//Empty private constructor that cannot be called externally.
	}

	/**
	 * Get a new instance of the builder
	 *
	 * @return IntegrationObjectModelBuilder
	 */
	public static IntegrationObjectModelBuilder integrationObject()
	{
		return new IntegrationObjectModelBuilder();
	}

	/**
	 * Specifies to run cleanup/after methods of this rule even if {@link #build()} method was not called.
	 * If {@code cleanAlways( )} is not called, then the after/cleanup method of this rule will clean only integration
	 * objects, which were explicitly created by calling the {@link #build()} method. With this option, this builder will
	 * remove all objects specified by calling {@link #withCode(String)} method, even if {@code build( )} was
	 * not called later. This is useful, if instead of calling the {@code build()} method, production code executed by
	 * the test creates the same integration object as specified in this builder.
	 *
	 * @return a builder specified to cleanup even not created objects
	 */
	public IntegrationObjectModelBuilder cleanAlways()
	{
		integrationObjectCodes.clear();
		return this;
	}

	public IntegrationObjectModelBuilder withCode(final String code)
	{
		integrationObjectCodes.add(code);
		this.code = code;
		return this;
	}

	public IntegrationObjectModelBuilder withItem(final IntegrationObjectItemModelBuilder builder)
	{
		itemBuilders.add(builder);
		return this;
	}

	public IntegrationObjectModelBuilder withClass(final IntegrationObjectClassModelBuilder builder)
	{
		classBuilders.add(builder);
		return this;
	}

	/**
	 * Each time build() is called, a new instance of the the {@link IntegrationObjectModel} is returned
	 * with the same properties that were set.
	 *
	 * @return IntegrationObjectModel
	 */
	public IntegrationObjectModel build()
	{
		Preconditions.checkArgument(code != null, "code cannot be null");
		itemBuilders.forEach(builder -> builder.withIntegrationObjectCode(code));
		classBuilders.forEach(builder -> builder.withIntegrationObjectCode(code));

		try
		{
			IntegrationTestUtil.importImpEx(generateImpex());
		}
		catch (final ImpExException ex)
		{
			throw new RuntimeException(ex);
		}

		final IntegrationObjectModel io = IntegrationObjectTestUtil.findIntegrationObjectByCode(code);
		createdIntegrationObjectPKs.add(io.getPk());
		return io;
	}

	public List<String> generateImpex()
	{
		final List<String> impex = new ArrayList<>(List.of("INSERT_UPDATE IntegrationObject; code[unique = true]",
				"                               ; " + code));
		if (!Collections.isEmpty(itemBuilders))
		{
			impex.addAll(generateItemImpexHeader());
			impex.addAll(generateItemImpexLines());
		}

		if (!Collections.isEmpty(classBuilders))
		{
			impex.addAll(generateClassImpexHeader());
			impex.addAll(generateClassImpexLines());
		}

		final List<String> itemAttributeLines = generateItemAttributeImpexLines();
		if (!Collections.isEmpty(itemAttributeLines))
		{
			impex.addAll(IntegrationObjectItemAttributeModelBuilder.buildAttributeImpexHeader());
			impex.addAll(itemAttributeLines);
		}

		final List<String> classAttributeLines = generateClassAttributeImpexLines();
		if (!Collections.isEmpty(classAttributeLines))
		{
			impex.addAll(IntegrationObjectClassAttributeModelBuilder.buildAttributeImpexHeader());
			impex.addAll(classAttributeLines);
		}

		final List<String> classificationAttributeLines = generateClassificationAttributeImpexLines();
		if (!Collections.isEmpty(classificationAttributeLines))
		{
			impex.addAll(IntegrationObjectItemClassificationAttributeBuilder.buildClassificationAttributeImpexHeader());
			impex.addAll(classificationAttributeLines);
		}

		return impex;
	}

	private List<String> generateItemImpexHeader()
	{
		return IntegrationObjectItemModelBuilder.buildItemImpexHeader(
				itemBuilders.stream()
				            .flatMap(b -> b.getExcludedValidators().stream())
				            .toList());
	}

	private List<String> generateClassImpexHeader()
	{
		return IntegrationObjectClassModelBuilder.buildClassImpexHeader(
				classBuilders.stream()
				             .flatMap(b -> b.getExcludedValidators().stream())
				             .toList());
	}


	private List<String> generateItemImpexLines()
	{
		return itemBuilders.stream().map(IntegrationObjectItemModelBuilder::buildImpexLine).collect(Collectors.toList());
	}

	private List<String> generateClassImpexLines()
	{
		return classBuilders.stream().map(IntegrationObjectClassModelBuilder::buildImpexLine).collect(Collectors.toList());
	}

	private List<String> generateItemAttributeImpexLines()
	{
		return itemBuilders.stream()
		                   .map(IntegrationObjectItemModelBuilder::buildAttributeLines)
		                   .flatMap(Collection::stream)
		                   .collect(Collectors.toList());
	}

	private List<String> generateClassAttributeImpexLines()
	{
		return classBuilders.stream()
		                    .map(IntegrationObjectClassModelBuilder::buildAttributeLines)
		                    .flatMap(Collection::stream)
		                    .collect(Collectors.toList());
	}

	private List<String> generateClassificationAttributeImpexLines()
	{

		return itemBuilders.stream()
		                   .map(IntegrationObjectItemModelBuilder::buildClassificationAttributeLines)
		                   .flatMap(Collection::stream)
		                   .collect(Collectors.toList());
	}

	/**
	 * Deletes all integration objects created from this builder
	 */
	public void cleanup()
	{
		IntegrationTestUtil.remove(IntegrationObjectModel.class,
				integrationObjectModel -> createdIntegrationObjectPKs.contains(integrationObjectModel.getPk()));
		createdIntegrationObjectPKs.clear();
		if (!Collections.isEmpty(integrationObjectCodes))
		{
			IntegrationTestUtil.remove(IntegrationObjectModel.class,
					integrationObject -> integrationObjectCodes.contains(integrationObject.getCode()));
			integrationObjectCodes.clear();
		}
	}

	@Override
	protected void before()
	{
		build();
	}

	@Override
	protected void after()
	{
		cleanup();
	}

}
