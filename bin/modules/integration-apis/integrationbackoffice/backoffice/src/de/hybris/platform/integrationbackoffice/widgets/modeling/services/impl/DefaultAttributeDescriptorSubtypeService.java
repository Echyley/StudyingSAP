/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.services.impl;

import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.integrationbackoffice.widgets.modeling.builders.DataStructureBuilder;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.SubtypeData;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.IntegrationMapKeyDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.AttributeDescriptorSubtypeService;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.ReadService;

import javax.validation.constraints.NotNull;

import java.util.Set;

import com.google.common.base.Preconditions;

/**
 * Default implementation of {@link AttributeDescriptorSubtypeService}.
 */
public class DefaultAttributeDescriptorSubtypeService implements AttributeDescriptorSubtypeService
{
	private final ReadService readService;
	private final DataStructureBuilder dataStructureBuilder;

	/**
	 * Default constructor of {@link DefaultAttributeDescriptorSubtypeService}.
	 *
	 * @param readService          the service used to perform database queries.
	 * @param dataStructureBuilder the service used to perform data structure operations.
	 */
	public DefaultAttributeDescriptorSubtypeService(@NotNull final ReadService readService,
	                                                @NotNull final DataStructureBuilder dataStructureBuilder)
	{
		Preconditions.checkNotNull(readService, "ReadService can't be null.");
		Preconditions.checkNotNull(dataStructureBuilder, "DataStructureBuilder can't be null.");
		this.readService = readService;
		this.dataStructureBuilder = dataStructureBuilder;
	}

	@Override
	public ComposedTypeModel findSubtype(final AttributeDescriptorModel attributeDescriptor,
	                                     final Set<SubtypeData> subtypeDataSet,
	                                     final IntegrationMapKeyDTO parentKeyDTO)
	{
		final String attributeDescriptorQualifier = attributeDescriptor.getQualifier();
		final ComposedTypeModel attributeType = readService.getComplexTypeForAttributeDescriptor(attributeDescriptor);
		final ComposedTypeModel attributeSubtype = dataStructureBuilder.findSubtypeMatch(
				parentKeyDTO,
				attributeDescriptorQualifier,
				attributeType,
				subtypeDataSet);
		return attributeSubtype != null ? attributeSubtype : attributeType;
	}
}
