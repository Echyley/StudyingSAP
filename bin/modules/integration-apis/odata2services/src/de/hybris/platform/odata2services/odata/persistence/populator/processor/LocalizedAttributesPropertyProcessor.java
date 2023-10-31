/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.persistence.populator.processor;

import static de.hybris.platform.odata2services.constants.Odata2servicesConstants.LANGUAGE_KEY_PROPERTY_NAME;
import static de.hybris.platform.odata2services.constants.Odata2servicesConstants.LOCALIZED_ATTRIBUTE_NAME;

import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;
import de.hybris.platform.integrationservices.model.TypeDescriptor;
import de.hybris.platform.integrationservices.service.IntegrationLocalizationService;
import de.hybris.platform.odata2services.odata.persistence.AbstractRequest;
import de.hybris.platform.odata2services.odata.persistence.ConversionOptions;
import de.hybris.platform.odata2services.odata.persistence.ItemConversionRequest;
import de.hybris.platform.integrationservices.util.ApplicationBeans;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.edm.EdmNavigationProperty;
import org.apache.olingo.odata2.api.edm.EdmTyped;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.core.ep.entry.EntryMetadataImpl;
import org.apache.olingo.odata2.core.ep.entry.MediaMetadataImpl;
import org.apache.olingo.odata2.core.ep.entry.ODataEntryImpl;
import org.apache.olingo.odata2.core.uri.ExpandSelectTreeNodeImpl;
import org.springframework.beans.factory.annotation.Required;

public class LocalizedAttributesPropertyProcessor extends AbstractCollectionPropertyProcessor
{
	private IntegrationLocalizationService localizationService;

	@Override
	protected boolean shouldPropertyBeConverted(final ItemConversionRequest conversionRequest, final String propertyName)
	{
		return false;
	}

	@Override
	protected boolean isApplicable(final TypeAttributeDescriptor typeAttributeDescriptor)
	{
		return false;
	}

	@Override
	public void processEntity(final ODataEntry oDataEntry, final ItemConversionRequest conversionRequest) throws EdmException
	{
		final EdmTyped localizedAttributes = conversionRequest.getEntityType().getProperty(LOCALIZED_ATTRIBUTE_NAME);

		if (localizedAttributes instanceof EdmNavigationProperty)
		{
			final TypeDescriptor typeDescriptor = getTypeDescriptor(conversionRequest);
			if (typeDescriptor != null)
			{
				processEntityInternal(oDataEntry, LOCALIZED_ATTRIBUTE_NAME, getSimpleLocalizedAttributes(typeDescriptor),
						conversionRequest);
			}
		}
	}

	private List<TypeAttributeDescriptor> getSimpleLocalizedAttributes(final TypeDescriptor typeDescriptor)
	{
		return typeDescriptor
				.getAttributes()
				.stream()
				.filter(TypeAttributeDescriptor::isPrimitive)
				.filter(TypeAttributeDescriptor::isLocalized)
				.collect(Collectors.toList());
	}

	@Override
	protected boolean canHandleEntityValue(final Object value)
	{
		return value != null;
	}

	/**
	 * Derives the Entries for the localizedAttributes property.
	 *
	 * @param request           - item conversion request
	 * @param localizedProperty - should be equivalent to the LOCALIZED_ATTRIBUTE_NAME constant
	 * @param value             - all localizable & primitive {@link TypeAttributeDescriptor}s
	 *                          that belong to the parent item derived from the entityType in the ItemConversionRequest
	 * @return a list of translations for the localizedSimpleAttributeDescriptors
	 */
	@Override
	protected List<ODataEntry> deriveDataFeedEntries(final ItemConversionRequest request, final String localizedProperty,
	                                                 final Object value)
	{
		final Map<Locale, Map<String, Object>> itemTranslations = new HashMap<>();
		final ConversionOptions options = request.getOptions();
		if ((options.isExpandPresent() || options.isNavigationSegmentPresent()) &&
				value instanceof List)
		{
			final Locale[] supportedLocales = getIntegrationLocalizationService().getAllSupportedLocales().toArray(new Locale[0]);
			final List<?> descriptors = (List<?>) value;
			descriptors.stream()
			           .filter(TypeAttributeDescriptor.class::isInstance)
			           .map(TypeAttributeDescriptor.class::cast)
			           .forEach(descriptor -> addPropertyLocalizationsToMap(itemTranslations, descriptor, request,
					           supportedLocales));
		}
		return getEntries(itemTranslations);
	}

	private TypeDescriptor getTypeDescriptor(final AbstractRequest conversionRequest) throws EdmException
	{
		final String itemTypeCode = conversionRequest.getEntityType().getName();
		final String integrationObjectCode = conversionRequest.getIntegrationObjectCode();
		return getItemTypeDescriptorService().getTypeDescriptorByTypeCode(integrationObjectCode, itemTypeCode).orElse(null);
	}

	private void addPropertyLocalizationsToMap(final Map<Locale, Map<String, Object>> itemTranslations,
	                                           final TypeAttributeDescriptor descriptor,
	                                           final ItemConversionRequest request,
	                                           final Locale[] supportedLocales)
	{
		final Map<Locale, Object> attrLocalizations = descriptor.accessor().getValues(request.getValue(), supportedLocales);
		if (attrLocalizations != null)
		{
			attrLocalizations.forEach((locale, localizedPropertyValue) -> {
				Map<String, Object> existingLocaleEntry = itemTranslations.get(locale);
				if (existingLocaleEntry == null)
				{
					existingLocaleEntry = createNewLocalizedEntry(locale);
				}
				addToEntry(itemTranslations, descriptor.getAttributeName(), localizedPropertyValue, locale, existingLocaleEntry);
			});
		}
	}

	private void addToEntry(final Map<Locale, Map<String, Object>> itemTranslations, final String localizedPropertyName,
	                        final Object localizedPropertyValue, final Locale locale,
	                        final Map<String, Object> existingLocaleEntry)
	{
		existingLocaleEntry.put(localizedPropertyName, localizedPropertyValue);
		itemTranslations.put(locale, existingLocaleEntry);
	}

	private Map<String, Object> createNewLocalizedEntry(final Locale locale)
	{
		final Map<String, Object> newEntry = new HashMap<>();
		newEntry.put(LANGUAGE_KEY_PROPERTY_NAME, locale.toString());
		return newEntry;
	}

	private List<ODataEntry> getEntries(final Map<Locale, Map<String, Object>> itemTranslations)
	{
		return itemTranslations.entrySet().stream().map(this::createLocalizedODataEntry).collect(Collectors.toList());
	}

	private ODataEntry createLocalizedODataEntry(final Map.Entry<Locale, Map<String, Object>> translation)
	{
		final Locale key = translation.getKey();

		final ODataEntry entry = new ODataEntryImpl(com.google.common.collect.Maps.newHashMap(), new MediaMetadataImpl(),
				new EntryMetadataImpl(), new ExpandSelectTreeNodeImpl());
		entry.getProperties().put(LANGUAGE_KEY_PROPERTY_NAME, key.getLanguage());
		entry.getProperties().putAll(translation.getValue());
		return entry;
	}

	@Required
	public void setLocalizationService(final IntegrationLocalizationService service)
	{
		this.localizationService = service;
	}

	IntegrationLocalizationService getIntegrationLocalizationService()
	{
		if (localizationService == null)
		{
			localizationService = ApplicationBeans.getBean("integrationLocalizationService",
					IntegrationLocalizationService.class);
		}
		return localizationService;
	}
}
