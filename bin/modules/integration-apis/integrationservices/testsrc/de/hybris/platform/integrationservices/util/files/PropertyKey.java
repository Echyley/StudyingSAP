/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.util.files;

import java.util.Set;

import javax.validation.constraints.NotNull;

/**
 * Represents a property key in a localization bundle file.
 */
public class PropertyKey
{
	private static final String KEY_DELIM = ".";
	private static final String KEY_DELIM_EXPRESSION = "\\.";
	private static final String REQUIRED_SUFFIX = "name";
	private static final Set<String> VALID_SUFFIXES = Set.of(REQUIRED_SUFFIX, "description");
	private static final String VALID_KEY_PREFIX = "type";
	private static final int TYPE_SEGMENTS = 3;
	private static final int ATTRIBUTE_SEGMENTS = 4;

	private final String keyString;
	private final String[] keySegments;

	/**
	 * Instantiates this property key.
	 *
	 * @param key the full property key as it appears in the localization file, e.g. {@code "type.Product.name"}
	 */
	public PropertyKey(@NotNull final String key)
	{
		keyString = key;
		keySegments = key.split(KEY_DELIM_EXPRESSION);
	}

	private String getPropertyPrefix()
	{
		return keySegments[0];
	}

	/**
	 * Retrieves item type, for which this property key is.
	 *
	 * @return segment of the property key containing the item type name or {@code null}, if this property is not for an item type
	 * localization.
	 */
	public String getItemType()
	{
		return isValid() ? keySegments[1] : null;
	}

	/**
	 * Retrieves name of the item type attribute localized by this property key.
	 *
	 * @return segment of the property containing the attribute name or {@code null}, if this property is not for an attribute
	 * localization
	 */
	public String getAttributeName()
	{
		return isAttribute() ? keySegments[2] : null;
	}

	private String getPropertySuffix()
	{
		return keySegments.length > 0 ? keySegments[keySegments.length - 1] : null;
	}

	/**
	 * Retrieves the property key value as it appears in the localization properties file.
	 *
	 * @return the string value of the key, e.g. {@code "type.IntegrationObject.name"}
	 */
	@NotNull
	public String getKeyString()
	{
		return keyString;
	}

	/**
	 * Determines whether this property is for an item type localization.
	 *
	 * @return {@code true}, if this property is in one of the following formats:
	 * <ul>
	 * <li>{@code type.<type_code>.name}</li>
	 * <li>{@code type.<type_code>.description}</li>
	 * </ul>
	 * or {@code false} otherwise.
	 */
	public boolean isItemType()
	{
		return keySegments.length == TYPE_SEGMENTS && prefixSuffixAreValid();
	}

	/**
	 * Determines whether this property is for an item attribute localization.
	 *
	 * @return {@code true}, if this property is in one of the following formats:
	 * <ul>
	 * <li>{@code type.<type_code>.<attribute_name>.name}</li>
	 * <li>{@code type.<type_code>.<attribute_name>.description}</li>
	 * </ul>
	 * or {@code false} otherwise.
	 */
	public boolean isAttribute()
	{
		return keySegments.length == ATTRIBUTE_SEGMENTS && prefixSuffixAreValid();
	}

	/**
	 * Determines whether this key is valid in the context of an extension localization file.
	 *
	 * @return {@code true}, if the property key is either for an item type or for an item type attribute.
	 * @see #isAttribute()
	 * @see #isItemType()
	 */
	public boolean isValid()
	{
		return isItemType() || isAttribute();
	}

	/**
	 * Some localization properties for types and attributes are optional. Only properties ending with {@code ".name"} are
	 * required for localization purposes. This method determines whether this property key represents a required or an optional
	 * localization property.
	 *
	 * @return {@code true}, if this property is required to be present in a localization file; {@code false}, if it's optional.
	 */
	public boolean isRequired()
	{
		return REQUIRED_SUFFIX.equals(getPropertySuffix());
	}

	private boolean prefixSuffixAreValid()
	{
		return VALID_KEY_PREFIX.equals(getPropertyPrefix()) && VALID_SUFFIXES.contains(getPropertySuffix());
	}

	/**
	 * Retrieves required property presentation for the specified item type.
	 *
	 * @param typeCode a type code for an item to get the corresponding localization property for
	 * @return localization property key as it would be returned by {@link #getKeyString()} method, if there was a
	 * {@code PropertyKey} for the specified item
	 */
	public static String asItemProperty(final String typeCode)
	{
		return VALID_KEY_PREFIX + KEY_DELIM + typeCode + KEY_DELIM + REQUIRED_SUFFIX;
	}

	/**
	 * Retrieves required property presentation for the specified item attribute.
	 *
	 * @param typeCode      type code of the item containing the specified attribute.
	 * @param attributeName name of the attribute to get the corresponding localization property for
	 * @return localization property key as it would be returned by {@link #getKeyString()} method, if there was a
	 * {@code PropertyKey} for the specified attribute.
	 */
	public static String asAttributeProperty(final String typeCode, final String attributeName)
	{
		return VALID_KEY_PREFIX + KEY_DELIM + typeCode + KEY_DELIM + attributeName + KEY_DELIM + REQUIRED_SUFFIX;
	}

	@Override
	public int hashCode()
	{
		return keyString.hashCode();
	}

	@Override
	public boolean equals(final Object obj)
	{
		return obj.getClass() == getClass() && ((PropertyKey) obj).keyString.equals(keyString);
	}

	@Override
	public String toString()
	{
		return getKeyString();
	}
}
