/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.util.files;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

/**
 * Represents localization properties in a {@code resource/localization/<extension_name>-locales_<language_code>.properties} file.
 * This implementation is not aware of the possible property keys in a backoffice extensions and can be used only for testing
 * item type and attribute localization.
 */
public class ExtensionLocalizationProperties implements LocalizedProperties
{
	private final ResourceBundle resourceBundle;
	private final Collection<PropertyKey> propertyKeys;

	/**
	 * Instantiates this localization properties.
	 *
	 * @param bundle resource bundle containing the localization properties.
	 */
	ExtensionLocalizationProperties(final ResourceBundle bundle)
	{
		resourceBundle = bundle;
		propertyKeys = loadPropertyKeys(bundle);
	}

	@Override
	public Collection<PropertyKey> getItemProperties()
	{
		return propertyKeys.stream()
		                   .filter(PropertyKey::isItemType)
		                   .collect(Collectors.toSet());
	}

	/**
	 * {@inheritDoc}
	 * <p>This method finds all invalid, i.e. not following the naming conventions, properties in this file.</p>
	 * @see PropertyKey#isValid()
	 */
	@Override
	public Collection<String> getPropertiesNotMatchingNamingConventions()
	{
		return propertyKeys
				.stream()
				.filter(key -> !key.isValid())
				.map(PropertyKey::getKeyString)
				.collect(Collectors.toList());
	}

	@Override
	public boolean keyExistsForItem(final String itemType)
	{
		return getItemProperties().stream()
		                          .filter(PropertyKey::isRequired)
		                          .map(PropertyKey::getItemType)
		                          .anyMatch(type -> Objects.equals(type, itemType));
	}

	@Override
	public boolean keyExistsForAttribute(final String itemType, final String attribute)
	{
		final Collection<String> requiredAttributes = propertyKeys
				.stream()
				.filter(PropertyKey::isAttribute)
				.filter(PropertyKey::isRequired)
				.filter(key -> Objects.equals(key.getItemType(), itemType))
				.map(PropertyKey::getAttributeName)
				.collect(Collectors.toSet());
		return requiredAttributes.contains(attribute)
				|| requiredAttributes.contains(attribute.toUpperCase())
				|| requiredAttributes.contains(attribute.toLowerCase());
	}

	@Override
	public Collection<PropertyKey> getAttributeProperties()
	{
		return propertyKeys.stream()
		                   .filter(PropertyKey::isAttribute)
		                   .collect(Collectors.toSet());
	}

	@Override
	public Collection<String> getValidPropertiesWithoutValues()
	{
		return propertyKeys.stream()
				.filter(PropertyKey::isValid)
				.filter(Predicate.not(this::hasLocalizedValue))
				.map(PropertyKey::getKeyString)
				.collect(Collectors.toSet());
	}

	private boolean hasLocalizedValue(final PropertyKey key)
	{
		final String value = resourceBundle.getString(key.getKeyString());
		return StringUtils.isNotBlank(value);
	}

	private static Collection<PropertyKey> loadPropertyKeys(final ResourceBundle rb)
	{
		return Collections.list(rb.getKeys())
		                  .stream()
		                  .map(PropertyKey::new)
		                  .collect(Collectors.toList());
	}
}
