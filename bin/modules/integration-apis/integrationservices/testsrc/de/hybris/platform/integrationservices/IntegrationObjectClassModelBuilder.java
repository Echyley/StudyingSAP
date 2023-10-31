/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices;

import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.integrationservices.model.IntegrationObjectClassModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.integrationservices.util.IntegrationTestUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.util.CollectionUtils;

import com.google.common.base.Preconditions;

/**
 * Integration object item builder to build an {@link IntegrationObjectClassModel}.
 */
public final class IntegrationObjectClassModelBuilder
{
	private final static String IMPEX_HEADER = "INSERT_UPDATE IntegrationObjectClass ; integrationObject(code)[unique = true]; code[unique = true]; type(code)     ; root[default = false]";
	private final static String IMPEX_WITH_EXCLUSIONS_HEADER = "INSERT_UPDATE IntegrationObjectClass[disable.interceptor.beans='%s'] ; integrationObject(code)[unique = true]; code[unique = true]; type(code)     ; root[default = false]";
	private static final String IMPEX_EXCLUSION_DELIMITER = ",";

	private final List<IntegrationObjectClassAttributeModelBuilder> attributes = new ArrayList<>();
	private String code;
	private Class<?> type;
	private boolean root = false;
	private String ioCode;
	private final Collection<String> excludedValidators = new ArrayList<>();

	/**
	 * Instantiates this IntegrationObjectClassModelBuilder.
	 *
	 * @return an instance of IntegrationObjectClassModelBuilder.
	 */
	public static IntegrationObjectClassModelBuilder integrationObjectClass()
	{
		return new IntegrationObjectClassModelBuilder();
	}

	/**
	 * Delegates to {@link #withCode(String)} to instantiate an IntegrationObjectClassModelBuilder with a specified class code.
	 *
	 * @param code the class code.
	 * @return an instance of IntegrationObjectClassModelBuilder with a class code defined.
	 */
	public static IntegrationObjectClassModelBuilder integrationObjectClass(final String code)
	{
		return integrationObjectClass().withCode(code);
	}

	/**
	 * Sets the class code for this IntegrationObjectClassModelBuilder.
	 *
	 * @param code the class code.
	 * @return an instance of IntegrationObjectClassModelBuilder with a class code defined.
	 */
	public IntegrationObjectClassModelBuilder withCode(final String code)
	{
		this.code = code;
		return this;
	}

	/**
	 * Sets the class type for this IntegrationObjectClassModelBuilder.
	 *
	 * @param type the class type to be included into an integration object.
	 * @return an instance of IntegrationObjectClassModelBuilder with a class type defined.
	 */
	public IntegrationObjectClassModelBuilder withType(final Class<?> type)
	{
		this.type = type;
		return this;
	}

	/**
	 * Sets isRoot for this class to true.
	 *
	 * @return an instance of IntegrationObjectClassModelBuilder with isRoot defined as true.
	 */
	public IntegrationObjectClassModelBuilder root()
	{
		return withRoot(true);
	}

	/**
	 * Sets isRoot for this class to a desired boolean value.
	 *
	 * @param value a boolean value for the class' getRoot property.
	 * @return an instance of IntegrationObjectClassModelBuilder with getRoot set to provided boolean.
	 * @see IntegrationObjectClassModel#getRoot()
	 */
	public IntegrationObjectClassModelBuilder withRoot(final boolean value)
	{
		root = value;
		return this;
	}

	/**
	 * Adds an IntegrationObjectClassAttribute to this class model.
	 *
	 * @return an instance of IntegrationObjectClassModelBuilder containing the provided attribute.
	 */
	public IntegrationObjectClassModelBuilder withAttribute(final IntegrationObjectClassAttributeModelBuilder attribute)
	{
		attributes.add(attribute);
		return this;
	}

	/**
	 * Delegates to {@link #withIntegrationObjectCode(String)} to find the IntegrationObject code from the provided model.
	 *
	 * @param model an IntegrationObjectModel to obtain the IntegrationObject code from.
	 * @return an instance of IntegrationObjectClassModelBuilder with the IntegrationObject code defined using the provided model.
	 */
	public IntegrationObjectClassModelBuilder withIntegrationObject(final IntegrationObjectModel model)
	{
		return withIntegrationObjectCode(model.getCode());
	}

	/**
	 * Sets the IntegrationObject code for this class.
	 *
	 * @param ioCode an IntegrationObject code.
	 * @return an instance of IntegrationObjectClassModelBuilder with the IntegrationObject code defined.
	 */
	public IntegrationObjectClassModelBuilder withIntegrationObjectCode(final String ioCode)
	{
		this.ioCode = ioCode;
		return this;
	}

	/**
	 * Sets the list of excluded validators for the impex execution
	 *
	 * @param excludedValidators a collection of strings with the bean names of the validators that we want to exclude from the impex execution
	 * @return an instance of IntegrationObjectClassModelBuilder with the excluded validators list defined
	 */
	public IntegrationObjectClassModelBuilder withExcludedValidators(final Collection<String> excludedValidators)
	{
		this.excludedValidators.addAll(excludedValidators);
		return this;
	}

	/**
	 * Builds the IntegrationObjectClassModel for this builder's IntegrationObjectClass.
	 *
	 * @return an IntegrationObjectClassModel produced by this builder.
	 */
	public IntegrationObjectClassModel build()
	{
		Preconditions.checkArgument(code != null, "code cannot be null");
		Preconditions.checkArgument(type != null, "type cannot be null");
		Preconditions.checkArgument(ioCode != null, "integrationObject cannot be null");

		final List<String> impex = buildClassImpexHeader(excludedValidators);
		impex.add(buildImpexLine());
		if (!CollectionUtils.isEmpty(attributes))
		{
			impex.addAll(IntegrationObjectClassAttributeModelBuilder.buildAttributeImpexHeader());
			impex.addAll(buildAttributeLines());
		}

		try
		{
			IntegrationTestUtil.importImpEx(impex);
		}
		catch (final ImpExException ex)
		{
			throw new RuntimeException(ex);
		}

		return IntegrationTestUtil.findAny(IntegrationObjectClassModel.class, integrationObjectClass ->
				integrationObjectClass.getCode().equals(code) && integrationObjectClass.getIntegrationObject()
				                                                                       .getCode()
				                                                                       .equals(ioCode)
		).orElse(null);
	}

	/**
	 * Builds the Impex header for this builder's IntegrationObjectClass.
	 *
	 * @return the Impex header for this builder's IntegrationObjectClass.
	 */
	public static List<String> buildClassImpexHeader()
	{
		return buildClassImpexHeader(null);
	}

	/**
	 * Builds the Impex header for this builder's IntegrationObjectClass.
	 *
	 * @param excludedValidators containing a collection of validator bean names that we want to exclude in the impex execution
	 * @return the Impex header for this builder's IntegrationObjectClass.
	 */
	public static List<String> buildClassImpexHeader(final Collection<String> excludedValidators)
	{
		return CollectionUtils.isEmpty(excludedValidators)
				? new ArrayList<>(Arrays.asList(IMPEX_HEADER))
				: headerWithDisabledInterceptors(excludedValidators);
	}

	private static List<String> headerWithDisabledInterceptors(final Collection<String> excludedValidators)
	{
		final String excludedString = String.join(IMPEX_EXCLUSION_DELIMITER, excludedValidators);
		return List.of(String.format(IMPEX_WITH_EXCLUSIONS_HEADER, excludedString));
	}

	/**
	 * Builds the Impex body for this builder's IntegrationObjectClass.
	 *
	 * @return the Impex body for this builder's IntegrationObjectClass.
	 */
	public String buildImpexLine()
	{
		return "                                     ; " + ioCode + "                        ; " + code + "       ; " + type.getName() + "   ; " + root;
	}

	/**
	 * Builds the Impex body for attributes in this builder's IntegrationObjectClass.
	 *
	 * @return the Impex body for attributes in this builder's IntegrationObjectClass.
	 */
	public List<String> buildAttributeLines()
	{
		return attributes.stream()
		                 .map(attribute -> attribute.withIntegrationObjectCode(ioCode)
		                                            .withClassCode(code).buildImpexLine())
		                 .collect(Collectors.toList());
	}

	Collection<String> getExcludedValidators()
	{
		return List.copyOf(excludedValidators);
	}
}
