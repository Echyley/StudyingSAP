/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.persistence.populator.processor;


import static de.hybris.platform.integrationservices.constants.IntegrationservicesConstants.INTEGRATION_KEY_PROPERTY_NAME;
import static de.hybris.platform.odata2services.constants.Odata2servicesConstants.LOCALIZED_ATTRIBUTE_NAME;

import de.hybris.platform.core.HybrisEnumValue;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;
import de.hybris.platform.integrationservices.model.TypeDescriptor;
import de.hybris.platform.integrationservices.service.ItemTypeDescriptorService;
import de.hybris.platform.integrationservices.util.Log;
import de.hybris.platform.odata2services.odata.persistence.AbstractRequest;
import de.hybris.platform.odata2services.odata.persistence.ItemConversionRequest;

import java.util.Optional;

import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.springframework.beans.factory.annotation.Required;

public abstract class AbstractPropertyProcessor implements PropertyProcessor
{
	private static final Log LOG = Log.getLogger(AbstractPropertyProcessor.class);

	private ItemTypeDescriptorService itemTypeDescriptorService;

	private Optional<TypeAttributeDescriptor> getTypeAttributeDescriptor(final String integrationObjectCode,
	                                                                     final String integrationItemCode,
	                                                                     final String propertyName)
	{
		final Optional<TypeDescriptor> itemTypeDescriptor = itemTypeDescriptorService.getTypeDescriptorByTypeCode(
				integrationObjectCode, integrationItemCode);
		return itemTypeDescriptor.flatMap(descriptor -> descriptor.getAttribute(propertyName));
	}

	private TypeAttributeDescriptor getTypeAttributeDescriptor(final Object value, final AbstractRequest request,
	                                                           final String propertyName)
	{
		return getTypeAttributeDescriptor(request.getIntegrationObjectCode(), getItemTypeCode(value), propertyName)
				.orElseGet(() -> getTypeAttributeDescriptorForEntityType(request, propertyName));
	}

	private TypeAttributeDescriptor getTypeAttributeDescriptorForEntityType(final AbstractRequest request,
	                                                                        final String propertyName)
	{
		try
		{
			final String entityName = request.getEntityType().getName();
			final String integrationObjectCode = request.getIntegrationObjectCode();
			return getTypeAttributeDescriptor(integrationObjectCode, entityName, propertyName).orElse(null);
		}
		catch (final EdmException ex)
		{
			LOG.error("Cannot get the name of the entity type.", ex);
			return null;
		}
	}

	@Override
	public void processEntity(final ODataEntry oDataEntry, final ItemConversionRequest conversionRequest) throws EdmException
	{
		for (final String propertyName : conversionRequest.getAllPropertyNames())
		{
			if (isPropertySupported(propertyName))
			{
				final TypeAttributeDescriptor attributeDescriptor = getTypeAttributeDescriptor(conversionRequest.getValue(), conversionRequest, propertyName);
				if (isPropertySupported(attributeDescriptor)
						&& shouldPropertyBeConverted(conversionRequest, propertyName)
						&& attributeDescriptor.isReadable())
				{
					final Object propertyValue = readPropertyValue(attributeDescriptor, conversionRequest);
					processEntityInternal(oDataEntry, propertyName, propertyValue, conversionRequest);
				}
			}
		}
	}

	protected Object readPropertyValue(final TypeAttributeDescriptor descriptor, final ItemConversionRequest conversionRequest)
	{
		return descriptor.isLocalized() ?
				descriptor.accessor().getValue(conversionRequest.getValue(), conversionRequest.getAcceptLocale()) :
				descriptor.accessor().getValue(conversionRequest.getValue());
	}

	protected boolean shouldPropertyBeConverted(final ItemConversionRequest request, final String propertyName)
	{
		return request.isPropertyValueShouldBeConverted(propertyName);
	}

	private boolean isPropertySupported(final String propertyName)
	{
		return !INTEGRATION_KEY_PROPERTY_NAME.equals(propertyName) && !LOCALIZED_ATTRIBUTE_NAME.equals(propertyName);
	}

	private String getItemTypeCode(final Object value)
	{
		return value instanceof HybrisEnumValue ?
				((HybrisEnumValue) value).getType() :
				((ItemModel) value).getItemtype();
	}

	protected boolean isPropertySupported(final TypeAttributeDescriptor descriptor)
	{
		return descriptor != null && isApplicable(descriptor);
	}

	protected abstract boolean isApplicable(final TypeAttributeDescriptor typeAttributeDescriptor);

	protected ItemTypeDescriptorService getItemTypeDescriptorService()
	{
		return itemTypeDescriptorService;
	}

	@Required
	public void setItemTypeDescriptorService(final ItemTypeDescriptorService itemTypeDescriptorService)
	{
		this.itemTypeDescriptorService = itemTypeDescriptorService;
	}

	protected abstract void processEntityInternal(final ODataEntry oDataEntry, final String propertyName, final Object value,
	                                              final ItemConversionRequest request) throws EdmException;

}
