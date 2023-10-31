/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices;

import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.util.IntegrationTestUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.rules.ExternalResource;

import com.google.common.base.Preconditions;

/**
 * Integration object item attribute builder to build an {@link IntegrationObjectItemAttributeModel}.
 */
public class IntegrationObjectItemAttributeModelBuilder extends ExternalResource
{
	private String name;
	private String objectCode;
	private String itemCode;
	private String itemType;
	private String returnItemCode;
	private String qualifier;
	private boolean unique = false;
	private boolean autoCreate = false;


	private IntegrationObjectItemAttributeModelBuilder()
	{
		//Empty private constructor that cannot be called externally.
	}

	public static IntegrationObjectItemAttributeModelBuilder integrationObjectItemAttribute(final String name)
	{
		return integrationObjectItemAttribute().withName(name);
	}

	public static IntegrationObjectItemAttributeModelBuilder integrationObjectItemAttribute()
	{
		return new IntegrationObjectItemAttributeModelBuilder();
	}

	public IntegrationObjectItemAttributeModelBuilder withItem(final IntegrationObjectItemModel item)
	{
		objectCode = item.getIntegrationObject().getCode();
		itemType = item.getType().getCode();
		itemCode = item.getCode();
		return this;
	}

	public IntegrationObjectItemAttributeModelBuilder withIntegrationObjectCode(final String code)
	{
		objectCode = code;
		return this;
	}

	public IntegrationObjectItemAttributeModelBuilder withItemCode(final String code)
	{
		itemCode = code;
		return this;
	}

	public IntegrationObjectItemAttributeModelBuilder withItemType(final String type)
	{
		itemType = type;
		return this;
	}

	public IntegrationObjectItemAttributeModelBuilder withName(final String name)
	{
		this.name = name;
		return this;
	}

	public IntegrationObjectItemAttributeModelBuilder withQualifier(final String q)
	{
		qualifier = q;
		return this;
	}

	public IntegrationObjectItemAttributeModelBuilder withReturnItem(final String itemCode)
	{
		returnItemCode = itemCode;
		return this;
	}

	public IntegrationObjectItemAttributeModelBuilder unique()
	{
		unique = true;
		return this;
	}

	public IntegrationObjectItemAttributeModelBuilder autoCreate()
	{
		autoCreate = true;
		return this;
	}

	public static List<String> buildAttributeImpexHeader()
	{
		return new ArrayList<>(
				Arrays.asList("$integrationItem = integrationObjectItem(integrationObject(code), code)",
						"$attributeName = attributeName",
						"$attributeDescriptor = attributeDescriptor(enclosingType(code), qualifier)",
						"$returnItem = returnIntegrationObjectItem(integrationObject(code), code)",
						"INSERT_UPDATE IntegrationObjectItemAttribute; $integrationItem[unique = true]; $attributeName[unique = true]; $attributeDescriptor; $returnItem; unique ; autoCreate"));
	}

	public String buildImpexLine()
	{
		Preconditions.checkArgument(name != null, "name cannot be null");
		Preconditions.checkArgument(objectCode != null, "integrationObject cannot be null");
		Preconditions.checkArgument(itemCode != null, "integrationObjectItem cannot be null");

		final String integrationItem = objectCode + ":" + itemCode;
		final String descriptor = deriveTypeCode() + ":" + deriveQualifier();
		final String returnItem = StringUtils.isNotEmpty(returnItemCode) ? objectCode + ":" + returnItemCode : "";
		return "                                            ; "
				+ integrationItem + "; " + name + "         ; " + descriptor + "          ;" + returnItem + "; " + unique + "; " + autoCreate;
	}

	private String deriveTypeCode()
	{
		return StringUtils.isNotBlank(itemType) ? itemType : itemCode;
	}

	private String deriveQualifier()
	{
		return StringUtils.isNotBlank(qualifier) ? qualifier : name;
	}

	public IntegrationObjectItemAttributeModel build()
	{
		final List<String> impex = buildAttributeImpexHeader();
		impex.add(buildImpexLine());

		try
		{
			IntegrationTestUtil.importImpEx(impex);
		}
		catch (final ImpExException ex)
		{
			throw new RuntimeException(ex);
		}

		return IntegrationTestUtil.findAny(IntegrationObjectItemAttributeModel.class, itemAttributeModel ->
				itemAttributeModel.getAttributeName().equals(name)
		).orElse(null);
	}

	public void cleanup()
	{
		IntegrationTestUtil.findAny(IntegrationObjectItemAttributeModel.class, itemAttributeModel ->
				itemAttributeModel.getAttributeName().equals(name)
		).ifPresent(IntegrationTestUtil::remove);
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
}
