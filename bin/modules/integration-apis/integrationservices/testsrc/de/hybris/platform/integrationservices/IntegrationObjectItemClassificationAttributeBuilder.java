/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices;

import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemClassificationAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.util.IntegrationTestUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.rules.ExternalResource;

import com.google.common.base.Preconditions;

/**
 * Integration object item classification attribute builder to build an {@link IntegrationObjectItemClassificationAttributeModel}.
 */
public class IntegrationObjectItemClassificationAttributeBuilder extends ExternalResource
{
	private String name;
	private String objectCode;
	private String itemCode;
	private String returnItemCode;
	private String classSystem;
	private String classSystemVersion;
	private String classificationClass;
	private String classAttribute;

	private IntegrationObjectItemClassificationAttributeBuilder()
	{
		//Empty private constructor that cannot be called externally.
	}

	public static IntegrationObjectItemClassificationAttributeBuilder classificationAttribute(final String name)
	{
		return classificationAttribute().withName(name);
	}

	public static IntegrationObjectItemClassificationAttributeBuilder classificationAttribute()
	{
		return new IntegrationObjectItemClassificationAttributeBuilder();
	}

	public IntegrationObjectItemClassificationAttributeBuilder withItem(final IntegrationObjectItemModel item)
	{
		withIntegrationObjectCode(item.getIntegrationObject().getCode());
		return withItemCode(item.getCode());
	}

	public IntegrationObjectItemClassificationAttributeBuilder withIntegrationObjectCode(final String code)
	{
		objectCode = code;
		return this;
	}

	public IntegrationObjectItemClassificationAttributeBuilder withItemCode(final String code)
	{
		itemCode = code;
		return this;
	}

	public IntegrationObjectItemClassificationAttributeBuilder withName(final String name)
	{
		this.name = name;
		return this;
	}

	public IntegrationObjectItemClassificationAttributeBuilder withClassificationSystem(final String sys, final String ver)
	{
		classSystem = sys;
		classSystemVersion = ver;
		return this;
	}

	public IntegrationObjectItemClassificationAttributeBuilder withClassAssignment(final String cl, final String attr)
	{
		classificationClass = cl;
		classAttribute = attr;
		return this;
	}

	public IntegrationObjectItemClassificationAttributeBuilder withReturnItem(final String itemCode)
	{
		returnItemCode = itemCode;
		return this;
	}

	public static List<String> buildClassificationAttributeImpexHeader()
	{
		return new ArrayList<>(Arrays.asList("$item=integrationObjectItem(integrationObject(code), code)",
				"$systemVersionHeader=systemVersion(catalog(id), version)",
				"$classificationClassHeader=classificationClass(catalogVersion(catalog(id), version), code)",
				"$classificationAttributeHeader=classificationAttribute($systemVersionHeader, code)",
				"$classificationAssignment=classAttributeAssignment($classificationClassHeader, $classificationAttributeHeader)",
				"INSERT_UPDATE IntegrationObjectItemClassificationAttribute; $item[unique = true]; attributeName[unique = true]; $classificationAssignment ; returnIntegrationObjectItem(integrationObject(code), code)"));
	}

	public String buildImpexLine()
	{
		Preconditions.checkArgument(this.name != null, "name cannot be null");
		Preconditions.checkArgument(this.itemCode != null, "integrationObjectItem cannot be null");

		final String integrationItem = objectCode + ":" + itemCode;
		final String returnItem = StringUtils.isNotBlank(returnItemCode) ? objectCode + ":" + returnItemCode : "";
		return "                                                          ; "
				+ integrationItem + "    ; " + name + "                       ; " + deriveClassAssignment() + "; " + returnItem;
	}

	private String deriveClassAssignment()
	{
		return classSystem + ":" + classSystemVersion + ":" + classificationClass + ":" + classSystem + ":" + classSystemVersion + ":" + classAttribute;
	}

	public IntegrationObjectItemClassificationAttributeModel build()
	{
		final List<String> impex = buildClassificationAttributeImpexHeader();
		impex.add(buildImpexLine());

		try
		{
			IntegrationTestUtil.importImpEx(impex);
		}
		catch (final ImpExException ex)
		{
			throw new RuntimeException(ex);
		}

		return IntegrationTestUtil.findAny(IntegrationObjectItemClassificationAttributeModel.class, itemAttributeModel ->
				itemAttributeModel.getAttributeName().equals(name)
		).orElse(null);
	}

	public void cleanup()
	{
		IntegrationTestUtil.findAny(IntegrationObjectItemClassificationAttributeModel.class, itemAttributeModel ->
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
