/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices;

import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.integrationservices.search.ItemTypeMatch;
import de.hybris.platform.integrationservices.util.IntegrationTestUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.junit.rules.ExternalResource;
import org.springframework.util.CollectionUtils;

import com.google.common.base.Preconditions;


/**
 * Integration object item builder to build an {@link IntegrationObjectItemModel}.
 */
public class IntegrationObjectItemModelBuilder extends ExternalResource
{
	private static final String EMPTY_TYPE = "";
	private static final String IMPEX_HEADER = "INSERT_UPDATE IntegrationObjectItem ; integrationObject(code)[unique = true]; code[unique = true]; type(code)     ; root[default = false] ; itemTypeMatch(code)";
	private static final String IMPEX_WITH_EXCLUSIONS_HEADER = "INSERT_UPDATE IntegrationObjectItem[disable.interceptor.beans='%s'] ; integrationObject(code)[unique = true]; code[unique = true]; type(code)     ; root[default = false] ; itemTypeMatch(code)";
	private static final String IMPEX_EXCLUSION_DELIMITER = ",";
	private final List<IntegrationObjectItemAttributeModelBuilder> attributes = new ArrayList<>();
	private final List<IntegrationObjectItemClassificationAttributeBuilder> classificationAttributes = new ArrayList<>();
	private String code;
	private String type;
	private boolean root = false;
	private String integrationObjectCode;
	private ItemTypeMatch itemTypeMatch;
	private final Collection<String> excludedValidators = new ArrayList<>();

	private IntegrationObjectItemModelBuilder()
	{
		//Empty private constructor that cannot be called externally.
	}

	public static IntegrationObjectItemModelBuilder integrationObjectItem(final String code)
	{
		return integrationObjectItem().withCode(code);
	}

	public static IntegrationObjectItemModelBuilder integrationObjectItem()
	{
		return new IntegrationObjectItemModelBuilder();
	}

	public IntegrationObjectItemModelBuilder withCode(final String code)
	{
		this.code = code;
		return this;
	}

	public IntegrationObjectItemModelBuilder withIntegrationObject(final IntegrationObjectModel model)
	{
		return withIntegrationObjectCode(model.getCode());
	}

	public IntegrationObjectItemModelBuilder withIntegrationObjectCode(final String ioCode)
	{
		integrationObjectCode = ioCode;
		return this;
	}

	public IntegrationObjectItemModelBuilder withType(final String type)
	{
		this.type = type;
		return this;
	}

	public IntegrationObjectItemModelBuilder withTypeMatch(final ItemTypeMatch match)
	{
		itemTypeMatch = match;
		return this;
	}

	public IntegrationObjectItemModelBuilder root()
	{
		return withRoot(true);
	}

	public IntegrationObjectItemModelBuilder withRoot(final boolean value)
	{
		root = value;
		return this;
	}

	public IntegrationObjectItemModelBuilder withAttribute(final IntegrationObjectItemAttributeModelBuilder attr)
	{
		attributes.add(attr);
		return this;
	}

	public IntegrationObjectItemModelBuilder withAttribute(final IntegrationObjectItemClassificationAttributeBuilder attr)
	{
		classificationAttributes.add(attr);
		return this;
	}

	/**
	 * Sets the list of excluded validators for the impex execution
	 *
	 * @param excludedValidators a collection of strings with the bean names of the validators that we want to exclude from the impex execution
	 * @return an instance of IntegrationObjectItemModelBuilder with the excluded validators list defined
	 */
	public IntegrationObjectItemModelBuilder withExcludedValidators(final Collection<String> excludedValidators)
	{
		this.excludedValidators.addAll(excludedValidators);
		return this;
	}

	public IntegrationObjectItemModel build()
	{
		Preconditions.checkArgument(code != null, "code cannot be null");
		Preconditions.checkArgument(integrationObjectCode != null, "integrationObject cannot be null");

		final List<String> impex = buildItemImpexHeader(excludedValidators);
		impex.add(buildImpexLine());
		if (!CollectionUtils.isEmpty(attributes))
		{
			impex.addAll(IntegrationObjectItemAttributeModelBuilder.buildAttributeImpexHeader());
			impex.addAll(buildAttributeLines());
		}

		if (!CollectionUtils.isEmpty(classificationAttributes))
		{
			impex.addAll(
					IntegrationObjectItemClassificationAttributeBuilder.buildClassificationAttributeImpexHeader());
			impex.addAll(buildClassificationAttributeLines());
		}

		try
		{
			IntegrationTestUtil.importImpEx(impex);
		}
		catch (final ImpExException ex)
		{
			throw new RuntimeException(ex);
		}

		return IntegrationTestUtil.findAny(IntegrationObjectItemModel.class, integrationObjectItem ->
				integrationObjectItem.getCode().equals(code) && integrationObjectItem.getIntegrationObject()
				                                                                     .getCode()
				                                                                     .equals(integrationObjectCode)
		).orElse(null);
	}

	/**
	 * Builds the Impex header for this builder's IntegrationObjectItem.
	 *
	 * @return the Impex header for this builder's IntegrationObjectItem.
	 */
	public static List<String> buildClassImpexHeader()
	{
		return buildItemImpexHeader(null);
	}

	/**
	 * Builds the Impex header for this builder's IntegrationObjectItem.
	 *
	 * @param excludedValidators containing a collection of validator bean names that we want to exclude in the impex execution
	 * @return the Impex header for this builder's IntegrationObjectItem.
	 */
	public static List<String> buildItemImpexHeader(final Collection<String> excludedValidators)
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

	public String buildImpexLine()
	{
		return "                                    ; " + integrationObjectCode + "                ; " + code + "              ; " + deriveType() + "; " + root + "; " + deriveTypeMatch();
	}

	public List<String> buildAttributeLines()
	{
		return attributes.stream().map(attribute -> attribute.withIntegrationObjectCode(integrationObjectCode)
		                                                     .withItemCode(code)
		                                                     .withItemType(type).buildImpexLine())
		                 .collect(Collectors.toList());
	}

	public List<String> buildClassificationAttributeLines()
	{

		return classificationAttributes.stream().map(attribute -> attribute.withIntegrationObjectCode(integrationObjectCode)
		                                                                   .withItemCode(code).buildImpexLine())
		                               .collect(Collectors.toList());
	}

	private String deriveType()
	{
		return StringUtils.isNotBlank(type) ? type : code;
	}

	private String deriveTypeMatch()
	{
		return Objects.nonNull(itemTypeMatch) ? itemTypeMatch.name() : EMPTY_TYPE;
	}

	public void cleanup()
	{
		IntegrationTestUtil.findAny(IntegrationObjectItemModel.class, itemModel -> itemModel.getCode().equals(code))
		                   .ifPresent(IntegrationTestUtil::remove);
		attributes.forEach(IntegrationObjectItemAttributeModelBuilder::cleanup);
		attributes.clear();
		classificationAttributes.forEach(IntegrationObjectItemClassificationAttributeBuilder::cleanup);
		classificationAttributes.clear();
	}

	@Override
	protected void before() throws ImpExException
	{
		build();
	}

	@Override
	protected void after()
	{
		cleanup();
	}

	Collection<String> getExcludedValidators()
	{
		return List.copyOf(excludedValidators);
	}
}
