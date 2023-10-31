/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices;

import de.hybris.platform.integrationservices.model.IntegrationObjectClassAttributeModel;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;

/**
 * Integration object class attribute builder to build an {@link IntegrationObjectClassAttributeModel}.
 */
public final class IntegrationObjectClassAttributeModelBuilder
{
	private static final List<String> IMPEX_HEADER =
			List.of("$integrationClass = integrationObjectClass(integrationObject(code), code)",
					"$attributeName = attributeName",
					"$returnClass = returnIntegrationObjectClass(integrationObject(code), code)",
					"INSERT_UPDATE IntegrationObjectClassAttribute; $integrationClass[unique = true]      ; $attributeName[unique = true]; readMethod         ; $returnClass          ; unique[default=false]");

	private String name;
	private String readMethod;
	private String classCode;
	private String returnClassCode;
	private String ioCode;
	private Boolean unique;

	/**
	 * Instantiates this IntegrationObjectClassAttributeModelBuilder.
	 *
	 * @return an instance of IntegrationObjectClassAttributeModelBuilder.
	 */
	public static IntegrationObjectClassAttributeModelBuilder integrationObjectClassAttribute()
	{
		return new IntegrationObjectClassAttributeModelBuilder();
	}

	/**
	 * Delegates to {@link #withName(String)} to instantiate an IntegrationObjectClassAttributeModelBuilder with
	 * a specified attribute name.
	 *
	 * @param name a name for the attribute.
	 * @return an instance of IntegrationObjectClassAttributeModelBuilder with an attribute name defined.
	 */
	public static IntegrationObjectClassAttributeModelBuilder integrationObjectClassAttribute(final String name)
	{
		return integrationObjectClassAttribute().withName(name);
	}

	/**
	 * Sets the attribute name for this IntegrationObjectClassAttributeModelBuilder.
	 *
	 * @param name a name for the attribute.
	 * @return an instance of IntegrationObjectClassAttributeModelBuilder with an attribute name defined.
	 */
	public IntegrationObjectClassAttributeModelBuilder withName(final String name)
	{
		this.name = name;
		return this;
	}

	/**
	 * Sets the readMethod for this IntegrationObjectClassAttributeModelBuilder.
	 *
	 * @param readMethod the name of the readMethod.
	 * @return an instance of IntegrationObjectClassAttributeModelBuilder with the readMethod name defined.
	 */
	public IntegrationObjectClassAttributeModelBuilder withReadMethod(final String readMethod)
	{
		this.readMethod = readMethod;
		return this;
	}

	/**
	 * Sets the class code for this IntegrationObjectClassAttributeModelBuilder.
	 *
	 * @param code the code for the class containing the attribute to build.
	 * @return an instance of IntegrationObjectClassAttributeModelBuilder with the class code defined.
	 */
	public IntegrationObjectClassAttributeModelBuilder withClassCode(final String code)
	{
		classCode = code;
		return this;
	}

	/**
	 * Sets the reference class code within the IntegrationObject in this IntegrationObjectClassAttributeModelBuilder. If not
	 * called, it implies a primitive attribute.
	 *
	 * @param returnClassCode the code for the reference class within the same integration object.
	 * @return an instance of IntegrationObjectClassAttributeModelBuilder with the reference class code defined.
	 */
	public IntegrationObjectClassAttributeModelBuilder withReturnClassCode(final String returnClassCode)
	{
		this.returnClassCode = returnClassCode;
		return this;
	}

	/**
	 * Sets the IntegrationObject code for this IntegrationObjectClassAttributeModelBuilder.
	 *
	 * @param code the IO code for the integration object containing this class attribute.
	 * @return an instance of IntegrationObjectClassAttributeModelBuilder with IntegrationObject code defined.
	 */
	public IntegrationObjectClassAttributeModelBuilder withIntegrationObjectCode(final String code)
	{
		ioCode = code;
		return this;
	}

	/**
	 * Specifies this attribute being a key attribute in the integration object.
	 *
	 * @return an instance of IntegrationObjectClassAttributeModelBuilder with unique value defined.
	 */
	public IntegrationObjectClassAttributeModelBuilder unique()
	{
		return withUnique(true);
	}

	/**
	 * Sets the unique value for the IntegrationObjectClassAttributeModelBuilder.
	 *
	 * @param u the unique value for the integration object containing this class attribute.
	 * @return an instance of IntegrationObjectClassAttributeModelBuilder with unique value defined.
	 */
	public IntegrationObjectClassAttributeModelBuilder withUnique(final Boolean u)
	{
		unique = u;
		return this;
	}

	/**
	 * Builds the Impex header for this class attribute.
	 *
	 * @return the Impex header for this builder's class attribute.
	 */
	public static List<String> buildAttributeImpexHeader()
	{
		return IMPEX_HEADER;
	}

	/**
	 * Builds the Impex body for this class attribute.
	 *
	 * @return the Impex body for this builder's class attribute.
	 */
	public String buildImpexLine()
	{
		Preconditions.checkArgument(name != null, "name cannot be null");
		Preconditions.checkArgument(ioCode != null, "integrationObject cannot be null");
		Preconditions.checkArgument(classCode != null, "integrationObjectClass cannot be null");

		final String integrationClass = ioCode + ":" + classCode;
		final String readMethodName = StringUtils.isNotEmpty(readMethod) ? readMethod : "";
		final String returnClass = StringUtils.isNotEmpty(returnClassCode) ? ioCode + ":" + returnClassCode : "";
		final String isUnique = unique != null ? String.valueOf(unique) : "";
		return "                                             ; " + integrationClass + "; " + name + "  ; " + readMethodName + " ;" + returnClass + " ;" + isUnique;
	}
}
