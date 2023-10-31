/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.model.impl;

import de.hybris.platform.integrationservices.exception.CircularKeyReferenceException;
import de.hybris.platform.integrationservices.integrationkey.KeyAttributeValue;
import de.hybris.platform.integrationservices.integrationkey.KeyValue;
import de.hybris.platform.integrationservices.model.KeyAttribute;
import de.hybris.platform.integrationservices.model.KeyDescriptor;
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;
import de.hybris.platform.integrationservices.model.TypeDescriptor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.base.Preconditions;

/**
 * A primary key descriptor for a complex item type.
 */
public final class ItemKeyDescriptor implements KeyDescriptor
{
	private final Collection<KeyAttribute> simpleKeyAttributes;
	private final Collection<ReferenceKeyAttribute> referencedKeyAttributes;
	private final List<TypeDescriptor> referencePath;


	private ItemKeyDescriptor(final TypeDescriptor itemType, final List<TypeDescriptor> refPath)
	{
		Preconditions.checkArgument(itemType != null, "TypeDescriptor is required for ItemKeyDescriptor");
		Preconditions.checkArgument(!itemType.isPrimitive(), "TypeDescriptor cannot be for a primitive type");
		referencePath = expand(itemType, refPath);
		final Collection<TypeAttributeDescriptor> keyAttributes = deriveKeyAttributes(itemType);
		simpleKeyAttributes = extractSimpleKeyAttributes(keyAttributes);
		referencedKeyAttributes = extractReferencedKeyAttributes(keyAttributes);
	}

	private static List<TypeDescriptor> expand(final TypeDescriptor item, final List<TypeDescriptor> basePath)
	{
		final List<TypeDescriptor> path = new ArrayList<>(basePath);
		path.add(item);
		return path;
	}

	private static Set<TypeAttributeDescriptor> deriveKeyAttributes(final TypeDescriptor itemType)
	{
		return itemType.getAttributes()
		               .stream()
		               .filter(TypeAttributeDescriptor::isKeyAttribute)
		               .collect(Collectors.toSet());
	}

	/**
	 * Creates new instance of this descriptor.
	 * @param item an item model, for which a key descriptor should be created.
	 * @return a key descriptor for the specified item
	 */
	static ItemKeyDescriptor create(final TypeDescriptor item)
	{
		return new ItemKeyDescriptor(item, new ArrayList<>());
	}

	@Override
	public KeyValue calculateKey(final Map<String, Object> item)
	{
		final Collection<KeyAttributeValue> thisItemValues = simpleKeyAttributes.stream()
				.map(attr -> getValue(attr, item))
				.collect(Collectors.toSet());
		final Collection<KeyAttributeValue> referencedValues = referencedKeyAttributes.stream()
				.map(attr -> attr.calculateKey(item))
				.map(KeyValue::getKeyAttributeValues)
				.flatMap(Collection::stream)
				.collect(Collectors.toSet());

		return new KeyValue.Builder()
				.withValues(thisItemValues)
				.withValues(referencedValues)
				.build();
	}

	@Override
	public List<KeyAttribute> getKeyAttributes()
	{
		final List<KeyAttribute> allRefKeyAttributes = this.referencedKeyAttributes
				.stream()
				.map(ReferenceKeyAttribute::getKeyAttributes)
				.flatMap(Collection::stream)
				.collect(Collectors.toList());
		return Stream.concat(simpleKeyAttributes.stream(), allRefKeyAttributes.stream())
		             .collect(Collectors.toList());
	}

	@Override
	public boolean isKeyAttribute(final String attr)
	{
		return simpleKeyAttributes.stream().anyMatch(key -> key.getName().equals(attr))
				|| referencedKeyAttributes.stream().anyMatch(key -> key.getName().equals(attr));
	}

	private KeyAttributeValue getValue(final KeyAttribute attribute, final Map<String, Object> item)
	{
		final Object value = item == null ? null : item.get(attribute.getName());
		return new KeyAttributeValue(attribute, value);
	}

	private static Collection<KeyAttribute> extractSimpleKeyAttributes(final Collection<TypeAttributeDescriptor> attributes)
	{
		return attributes.stream()
				.filter(TypeAttributeDescriptor::isPrimitive)
				.map(KeyAttribute::new)
				.collect(Collectors.toSet());
	}

	private Collection<ReferenceKeyAttribute> extractReferencedKeyAttributes(final Collection<TypeAttributeDescriptor> attributes)
	{
		return attributes.stream()
				.map(attr -> ReferenceKeyAttribute.create(attr, referencePath))
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());
	}

	private static final class ReferenceKeyAttribute
	{
		private final TypeAttributeDescriptor itemAttribute;
		private final ItemKeyDescriptor keyDescriptor;

		private ReferenceKeyAttribute(final TypeAttributeDescriptor attr, final List<TypeDescriptor> refPath)
		{
			itemAttribute = attr;
			keyDescriptor = deriveKeyDescriptor(attr.getAttributeType(), refPath);
		}

		static ReferenceKeyAttribute create(final TypeAttributeDescriptor attribute, final List<TypeDescriptor> refPath)
		{
			return attribute.isPrimitive()
					? null
					: new ReferenceKeyAttribute(attribute, refPath);
		}

		private ItemKeyDescriptor deriveKeyDescriptor(final TypeDescriptor item, final List<TypeDescriptor> refPath)
		{
			final boolean referencesItemOnThePath = item != null
					&& refPath.stream().anyMatch(it -> Objects.equals(it.getItemCode(), item.getItemCode()));
			if (referencesItemOnThePath)
			{
				throw new CircularKeyReferenceException(itemAttribute);
			}
			assert item != null;
			return new ItemKeyDescriptor(item, refPath);
		}

		String getName()
		{
			return itemAttribute.getAttributeName();
		}

		KeyValue calculateKey(final Map<String, Object> item)
		{
			final Object attributeValue = item == null ? null : item.get(getName());
			return keyDescriptor.calculateKey((Map<String, Object>) attributeValue);
		}

		List<KeyAttribute> getKeyAttributes()
		{
			return keyDescriptor.getKeyAttributes();
		}
	}
}
