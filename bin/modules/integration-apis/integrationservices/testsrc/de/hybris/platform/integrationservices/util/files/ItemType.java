/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.util.files;

import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Representation of an item type in the type system. This representation is generic and does not distinguish between types of an
 * item, e.g. a collection item type, a composed item type, an enum type, etc.
 */
public class ItemType
{
	private static final String RELATION_NODE = "relation";
	private static final String AUTOCREATE_ATTRIBUTE = "autocreate";
	private static final String CODE_ATTRIBUTE = "code";
	private static final String TYPE_ATTRIBUTE = "type";

	private final String typeCode;
	private final boolean created;
	private final Collection<String> typeAttributes;
	private final TypeService typeService;
	private Collection<ItemType> inheritedTypes;

	private ItemType(@NotNull final String type, final boolean isNew, final TypeService service)
	{
		typeCode = type;
		created = isNew;
		typeAttributes = new HashSet<>();
		typeService = service;
	}

	/**
	 * Creates an item type other than {@code ComposedType}, such as an enum or a map.
	 *
	 * @param el XML element containing the item type configuration
	 */
	public static ItemType create(@NotNull final Element el)
	{
		return create(el, null);
	}

	/**
	 * Creates an instance of a {@code ComposedType} item.
	 *
	 * @param el      XML element containing the item type configuration
	 * @param service instance of the type service to query about the item type hierarchy.
	 */
	public static ItemType create(@NotNull final Element el, final TypeService service)
	{
		return isRelationItem(el)
				? new ItemType(getType(el), false, service)
				: new ItemType(getCode(el), isNew(el), service);
	}

	/**
	 * Retrieves type code of this item type.
	 *
	 * @return a code that identifies this item type.
	 */
	public String getTypeCode()
	{
		return typeCode;
	}

	/**
	 * Determines whether this item type is created by the context items.xml or it was created in a different file and simply
	 * acquired more attributes or relations in the context items.xml. The implementation assumes that, if the item type
	 * definition does not contain {@code "autocreate"} attribute then it's by default is {@code true} or, otherwise, it takes
	 * the attribute value to determine whether the file creates this type or not.
	 *
	 * @return {@code true} if this item type is created in the context items.xml; {@code false} otherwise.
	 */
	public boolean isCreated()
	{
		return created;
	}

	/**
	 * Adds attributes to this item type.
	 *
	 * @param attributes a collection of attribute names present in this item type
	 * @return an item type with the attributes added.
	 */
	public ItemType withAttributes(final Collection<String> attributes)
	{
		typeAttributes.addAll(attributes);
		return this;
	}

	/**
	 * Adds an attribute to this item type.
	 *
	 * @param attribute name of an attribute present in this item type
	 * @return an item type with the attribute added.
	 */
	public ItemType withAttribute(final String attribute)
	{
		typeAttributes.add(attribute);
		return this;
	}

	/**
	 * Retrieves attributes explicitly defined in this item type within the context items.xml file.
	 *
	 * @return attributes for this item type defined in items.xml file, from which this item type was derived. Inherited attributes
	 * defined in other items.xml files are excluded. If this item type does not contain attributes, then an empty collection is
	 * returned.
	 * @see #withAttribute(String)
	 * @see #withAttributes(Collection)
	 */
	public Collection<String> getAttributes()
	{
		return Set.copyOf(typeAttributes);
	}

	/**
	 * Retrieves a collection of all item types this type inherits from in the type system.
	 *
	 * @return a collection of all supertypes for this item type or an empty collection, if this item type does not extend any
	 * other type or, if this type does not represent the {@code ComposedType} in the type system.
	 */
	public Collection<ItemType> getInheritedItems()
	{
		if (inheritedTypes == null)
		{
			inheritedTypes = typeService == null
					? Collections.emptySet()
					: getComposedType().map(ComposedTypeModel::getAllSuperTypes)
					                   .orElse(Collections.emptyList())
					                   .stream()
					                   .map(ComposedTypeModel::getCode)
					                   .map(c -> new ItemType(c, false, typeService))
					                   .collect(Collectors.toSet());
		}
		return inheritedTypes;
	}

	private Optional<ComposedTypeModel> getComposedType()
	{
		try
		{
			return Optional.of(typeService.getComposedTypeForCode(getTypeCode()));
		}
		catch (final UnknownIdentifierException e)
		{
			return Optional.empty();
		}
	}

	/**
	 * Checks whether this item type contains an attribute with the specified name. The name is case-insensitive.
	 *
	 * @param name name of the attribute to check for presence in this item type.
	 * @return {@code true}, if this item type contains the specified attribute regardless of the letter case match; {@code false}
	 * otherwise.
	 */
	public boolean containsAttribute(final String name)
	{
		return typeAttributes.contains(name)
				|| typeAttributes.contains(name.toLowerCase())
				|| typeAttributes.contains(name.toUpperCase())
				|| typeService != null && getComposedType()
				.map(type -> typeService.hasAttribute(type, name))
				.orElse(false);
	}

	@Override
	public int hashCode()
	{
		return typeCode.hashCode();
	}

	@Override
	public boolean equals(final Object obj)
	{
		return obj != null && obj.getClass() == getClass() && typeCode.equals(((ItemType) obj).typeCode);
	}

	private static boolean isRelationItem(final Element el)
	{
		final Node parent = el.getParentNode();
		return parent != null && RELATION_NODE.equals(parent.getNodeName());
	}

	private static boolean isNew(final Element el)
	{
		final String autocreated = el.getAttribute(AUTOCREATE_ATTRIBUTE);
		return autocreated.isEmpty() || Boolean.parseBoolean(autocreated);
	}

	private static String getCode(final Element el)
	{
		return el.getAttribute(CODE_ATTRIBUTE);
	}

	private static String getType(final Element el)
	{
		return el.getAttribute(TYPE_ATTRIBUTE);
	}
}
