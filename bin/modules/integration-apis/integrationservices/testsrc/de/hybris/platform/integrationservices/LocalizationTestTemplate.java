/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices;

import static de.hybris.platform.integrationservices.util.files.ItemsXmlBuilder.itemsXml;
import static de.hybris.platform.integrationservices.util.files.LocalizedPropertiesBuilder.localizedProperties;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.platform.integrationservices.util.files.ItemType;
import de.hybris.platform.integrationservices.util.files.ItemsXml;
import de.hybris.platform.integrationservices.util.files.LocalizedProperties;
import de.hybris.platform.integrationservices.util.files.PropertyKey;
import de.hybris.platform.servicelayer.ServicelayerTest;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.junit.Test;

/**
 * A <a href="https://sourcemaking.com/design_patterns/template_method">template</a> for localization testing in the commerce
 * platform extensions.
 */
public abstract class LocalizationTestTemplate extends ServicelayerTest
{
	private final LocalizedProperties localizedProperties = localizedProperties(getExtension()).build();
	private final ItemsXml itemsXml = itemsXml(getExtension()).build();

	@Test
	public void thereAreNoUnconventionalLocalizationProperties()
	{
		final Collection<String> nonConventionalProperties = localizedProperties.getPropertiesNotMatchingNamingConventions();

		assertThat(nonConventionalProperties)
				.as("These properties do not comply with 'type.<type_code>.[<attr_name>.]<name|description>' convention")
				.isEmpty();
	}

	@Test
	public void allTypesInLocalizationPropertiesAreInItemsXml()
	{
		final Collection<String> propertiesForTypesNotPresentInItemsXml = derivePropertiesForTypesNotPresentInItemsXml();

		assertThat(propertiesForTypesNotPresentInItemsXml)
				.as("These properties are not for items in the %s-items.xml", getExtension())
				.isEmpty();
	}

	@Test
	public void allAttributesInLocalizationPropertiesAreInItemsXml()
	{
		final Collection<String> attributes = deriveAttributePropertiesNotPresentInItemsXml();

		assertThat(attributes)
				.as("These properties are not for attributes in the %s-items.xml", getExtension())
				.isEmpty();
	}

	@Test
	public void allItemsInItemsXmlAreLocalized()
	{
		final Collection<String> nonLocalizedItems = deriveNonLocalizedItems();

		assertThat(nonLocalizedItems)
				.as("These localization properties are missing for items in %s-items.xml", getExtension())
				.isEmpty();
	}

	@Test
	public void allAttributesInItemsXmlAreLocalized()
	{
		final Collection<String> nonLocalizedAttributes = deriveNonLocalizedItemAttributes();

		assertThat(nonLocalizedAttributes)
				.as("These localization properties are missing for attributes in %s-items.xml", getExtension())
				.isEmpty();
	}

	@Test
	public void allValidLocalizationPropertiesHaveValues()
	{
		final Collection<String> propertiesWithoutValues = localizedProperties.getValidPropertiesWithoutValues();

		assertThat(propertiesWithoutValues)
				.as("These properties have no localization values")
				.isEmpty();
	}

	/**
	 * Retrieves name of the extension being tested.
	 *
	 * @return extension name
	 */
	protected abstract String getExtension();

	private Collection<String> deriveAttributePropertiesNotPresentInItemsXml()
	{
		return localizedProperties.getAttributeProperties()
		                          .stream()
		                          .filter(Predicate.not(this::isAttributePresentInItemsXml))
		                          .map(PropertyKey::getKeyString)
		                          .collect(Collectors.toSet());
	}

	private boolean isAttributePresentInItemsXml(final PropertyKey key)
	{
		return itemsXml.containsAttribute(key.getItemType(), key.getAttributeName());
	}

	private Collection<String> derivePropertiesForTypesNotPresentInItemsXml()
	{
		final Collection<String> allItemsXmlTypes = itemsXml.getContextTypeCodes();
		return localizedProperties.getItemProperties()
		                          .stream()
		                          .filter(Predicate.not(p -> allItemsXmlTypes.contains(p.getItemType())))
		                          .map(PropertyKey::getKeyString)
		                          .collect(Collectors.toSet());
	}

	private Collection<String> deriveNonLocalizedItems()
	{
		return itemsXml.getCreatedTypeCodes().stream()
		               .filter(Predicate.not(localizedProperties::keyExistsForItem))
		               .map(PropertyKey::asItemProperty)
		               .collect(Collectors.toSet());
	}

	private Collection<String> deriveNonLocalizedItemAttributes()
	{
		return itemsXml.getItemTypes().stream()
		               .map(this::deriveNonLocalizedAttributes)
		               .flatMap(Collection::stream)
		               .collect(Collectors.toSet());
	}

	private Collection<String> deriveNonLocalizedAttributes(final ItemType type)
	{
		return type.getAttributes().stream()
		           .filter(attr -> attributeIsNotLocalized(type, attr))
		           .map(attr -> PropertyKey.asAttributeProperty(type.getTypeCode(), attr))
		           .collect(Collectors.toSet());
	}

	private boolean attributeIsNotLocalized(final ItemType type, final String attr)
	{
		return !(localizedProperties.keyExistsForAttribute(type.getTypeCode(), attr)
				|| type.getInheritedItems().stream()
				       .filter(i -> i.containsAttribute(attr))
				       .anyMatch(i -> localizedProperties.keyExistsForAttribute(i.getTypeCode(), attr)));
	}
}
