/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.persistence.populator.processor;

import static de.hybris.platform.odata2services.constants.Odata2servicesConstants.PRIMITIVE_ENTITY_PROPERTY_NAME;

import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;
import de.hybris.platform.odata2services.odata.persistence.ItemConversionRequest;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.core.ep.entry.EntryMetadataImpl;
import org.apache.olingo.odata2.core.ep.entry.MediaMetadataImpl;
import org.apache.olingo.odata2.core.ep.entry.ODataEntryImpl;
import org.apache.olingo.odata2.core.uri.ExpandSelectTreeNodeImpl;

import com.google.common.collect.Maps;

public class PrimitiveCollectionPropertyProcessor extends AbstractCollectionPropertyProcessor
{
	@Override
	protected boolean isApplicable(final TypeAttributeDescriptor typeAttributeDescriptor)
	{
		return typeAttributeDescriptor.isCollection() && typeAttributeDescriptor.isPrimitive();
	}

	@Override
	protected List<ODataEntry> deriveDataFeedEntries(final ItemConversionRequest request, final String propertyName, final Object value)
	{
		return ((Collection<?>) value).stream()
			.map(this::createPrimitiveValueEntry)
			.collect(Collectors.toList());
	}

	private ODataEntry createPrimitiveValueEntry(final Object value)
	{
		final ODataEntry entry = new ODataEntryImpl(Maps.newHashMap(), new MediaMetadataImpl(),
				new EntryMetadataImpl(), new ExpandSelectTreeNodeImpl());
		entry.getProperties().putIfAbsent(PRIMITIVE_ENTITY_PROPERTY_NAME, value);
		return entry;
	}
}
