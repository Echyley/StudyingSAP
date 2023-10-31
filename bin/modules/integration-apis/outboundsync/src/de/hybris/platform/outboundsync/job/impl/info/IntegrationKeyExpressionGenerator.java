/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundsync.job.impl.info;

import de.hybris.platform.integrationservices.exception.CircularKeyReferenceException;
import de.hybris.platform.integrationservices.model.KeyAttribute;
import de.hybris.platform.integrationservices.model.ReferencePath;
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;
import de.hybris.platform.integrationservices.model.TypeDescriptor;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Generates SpEL expression for deriving an item model integration key.
 */
public class IntegrationKeyExpressionGenerator implements ExpressionGenerator
{
	private static final Comparator<TypeAttributeDescriptor> COMPARATOR = new AttributeComparator();
	private static final char KEY_DELIMITER = '|';
	private static final String DATE = "java.util.Date";

	/**
	 * {@inheritDoc}
	 *
	 * @return a SpEL expression to calculate an {@code ItemModel} integration key based on the {@code itemType} provided;
	 * or {@code null}, if the {@code itemType} is {@code null} or the {@code itemType} has no key attributes.
	 */
	@Override
	public String generateExpression(final TypeDescriptor itemType)
	{
		if (itemType == null)
		{
			return null;
		}
		final StringBuilder exprBuilder = getKeyAttributes(itemType)
		                                          .stream()
		                                          .map(KeyAttribute::getAttributeDescriptor)
		                                          .sorted(COMPARATOR)
		                                          .map(attr -> toExpression(itemType, attr))
		                                          .filter(Objects::nonNull)
		                                          .map(StringBuilder::new)
		                                          .reduce(new StringBuilder(), (p, n) -> p.append(KEY_DELIMITER).append(n));
		return exprBuilder.length() > 0
				? exprBuilder.substring(1)
				: null;
	}

	private String toExpression(final TypeDescriptor itemType,
	                            final TypeAttributeDescriptor attrDesc)
	{
		final List<ReferencePath> paths = attrDesc.getTypeDescriptor().pathFrom(itemType);
		return paths.isEmpty()
				? null
				: ("#{" + toPropertyPath(paths.get(0), attrDesc) +"}");
	}

	private String toPropertyPath(final ReferencePath path, final TypeAttributeDescriptor attr)
	{
		final String pathStr = path.length() > 0
				? (path.toPropertyPath() + "." + attr.getQualifier())
				: attr.getQualifier();
		return applyTypeConditions(pathStr, attr).replace(".", "?.");
	}

	private String applyTypeConditions(final String path, final TypeAttributeDescriptor attr)
	{
		return DATE.equals(attr.getAttributeType().getTypeCode()) ? (path + ".time") : path;
	}

	private List<KeyAttribute> getKeyAttributes(final TypeDescriptor type)
	{
		try
		{
			return type.getKeyDescriptor().getKeyAttributes();
		}
		catch (final CircularKeyReferenceException e) {
			return Collections.emptyList();
		}
	}

	private static class AttributeComparator implements Comparator<TypeAttributeDescriptor>, Serializable
	{
		private static final long serialVersionUID = -228496578192831690L;
		private static final String SEPARATOR = "_";

		@Override
		public int compare(final TypeAttributeDescriptor o1, final TypeAttributeDescriptor o2)
		{
			final String key1 = asComparableString(o1);
			final String key2 = asComparableString(o2);
			return key1.compareTo(key2);
		}

		private static String asComparableString(final TypeAttributeDescriptor descriptor)
		{
			return descriptor.getTypeDescriptor().getItemCode() + SEPARATOR + descriptor.getAttributeName();
		}
	}
}
