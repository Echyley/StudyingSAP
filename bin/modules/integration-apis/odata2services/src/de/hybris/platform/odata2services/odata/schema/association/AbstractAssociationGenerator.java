/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.schema.association;

import static de.hybris.platform.odata2services.odata.schema.utils.SchemaUtils.toFullQualifiedName;

import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;
import de.hybris.platform.integrationservices.model.TypeDescriptor;

import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.odata2.api.edm.EdmMultiplicity;
import org.apache.olingo.odata2.api.edm.provider.Association;
import org.apache.olingo.odata2.api.edm.provider.AssociationEnd;

public abstract class AbstractAssociationGenerator implements AssociationGenerator
{
	private static final String ASSOCIATION_NAME_PATTERN = "FK_%s_%s";

	@Override
	public Association generate(final TypeAttributeDescriptor attrDescriptor)
	{
		final String sourceTypeCode = getSourceRole(attrDescriptor);
		final String targetTypeCode = getTargetRole(attrDescriptor);
		return new Association()
				.setName(deriveAssociationName(attrDescriptor))
				.setEnd1(new AssociationEnd()
						.setType(toFullQualifiedName(sourceTypeCode))
						.setRole(sourceTypeCode)
						.setMultiplicity(getSourceCardinality(attrDescriptor)))
				.setEnd2(new AssociationEnd()
						.setType(toFullQualifiedName(getTargetType(attrDescriptor)))
						.setRole(targetTypeCode)
						.setMultiplicity(getTargetCardinality(attrDescriptor)));
	}

	private String deriveAssociationName(final TypeAttributeDescriptor descriptor)
	{
		return String.format(ASSOCIATION_NAME_PATTERN,
				descriptor.getTypeDescriptor().getItemCode(),
				descriptor.getAttributeName());
	}

	/**
	 * Generates an association source role.
	 * @param descriptor an attribute descriptor to generate the source role for.
	 * @return type code of the {@link TypeDescriptor} containing the attribute descriptor.
	 * @see TypeAttributeDescriptor#getTypeDescriptor()
	 * @see TypeDescriptor#getItemCode()
	 */
	private String getSourceRole(final TypeAttributeDescriptor descriptor)
	{
		return descriptor.getTypeDescriptor().getItemCode();
	}

	/**
	 * Generates an association target role.
	 * @param descriptor an attribute descriptor to generate the target role from.
	 * @return type code of the {@link TypeDescriptor} referenced by the attribute descriptor (type of the attribute values) or
	 * the attribute name if the source type is the same as the target type in the association.
	 * @see TypeAttributeDescriptor#getAttributeType()
	 * @see TypeDescriptor#getItemCode()
	 * @see TypeAttributeDescriptor#getAttributeName()
	 */
	private String getTargetRole(final TypeAttributeDescriptor descriptor)
	{
		final String targetTypeName = getTargetType(descriptor);
		return targetTypeName.equals(getSourceRole(descriptor)) ?
				StringUtils.capitalize(descriptor.getAttributeName()) :
				targetTypeName;
	}

	/**
	 * Generates an association target type.
	 * @param descriptor an attribute descriptor to generate the target type from.
	 * @return type code of the {@link TypeDescriptor} referenced by the attribute descriptor (type of the attribute values).
	 * @see TypeAttributeDescriptor#getAttributeType()
	 * @see TypeDescriptor#getItemCode()
	 */
	protected String getTargetType(final TypeAttributeDescriptor descriptor)
	{
		return descriptor.getAttributeType().getItemCode();
	}

	/**
	 * Determines cardinality of the source type presented in the specified attribute descriptor. Normally the source type is
	 * the item type containing the attribute.
	 * @param descriptor descriptor to derive the cardinality from
	 * @return multiplicity
	 */
	protected EdmMultiplicity getSourceCardinality(final TypeAttributeDescriptor descriptor)
	{
		return descriptor.reverse()
				.map(AbstractAssociationGenerator::toMultiplicity)
				.orElse(EdmMultiplicity.ZERO_TO_ONE);
	}

	/**
	 * Determines cardinality of the target type presented in the specified attribute descriptor. Normally the target type is
	 * the item type returned by the attribute.
	 * @param descriptor descriptor to derive the cardinality from
	 * @return multiplicity
	 */
	protected EdmMultiplicity getTargetCardinality(final TypeAttributeDescriptor descriptor)
	{
		return toMultiplicity(descriptor);
	}

	private static EdmMultiplicity toMultiplicity(final TypeAttributeDescriptor d)
	{
		if (d.isCollection() || (d.isMap() && !d.isLocalized()))
		{
			return EdmMultiplicity.MANY;
		}
		return d.isNullable()
				? EdmMultiplicity.ZERO_TO_ONE
				: EdmMultiplicity.ONE;
	}

	protected static boolean falseIfNull(final Object attribute)
	{
		return attribute != null;
	}
}
