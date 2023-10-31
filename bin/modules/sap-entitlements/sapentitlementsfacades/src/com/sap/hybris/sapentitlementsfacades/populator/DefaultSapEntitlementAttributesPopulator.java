/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapentitlementsfacades.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;

import com.sap.hybris.sapentitlementsfacades.data.EntitlementAttributeData;
import com.sap.hybris.sapentitlementsfacades.data.EntitlementData;
import com.sap.hybris.sapentitlementsintegration.pojo.Entitlement;


/**
 * Populate entitlement attributes
 */
public class DefaultSapEntitlementAttributesPopulator implements Populator<Entitlement, EntitlementData>
{
	private Set<String> ignoredAttributes;

	@Override
	public void populate(final Entitlement entitlement, final EntitlementData entitlementData) throws ConversionException
	{
		final List<EntitlementAttributeData> attributes = new ArrayList<EntitlementAttributeData>();
		for (final Map.Entry<String, Object> customAttribute : entitlement.getCustomAttributes().entrySet())
		{
			if (!getIgnoredAttributes().contains(customAttribute.getKey()))
			{
				final EntitlementAttributeData entitlementAttributeData = new EntitlementAttributeData();
				entitlementAttributeData.setAttributeCode(customAttribute.getKey());
				entitlementAttributeData.setAttributeValue(customAttribute.getValue());
				attributes.add(entitlementAttributeData);
			}
		}
		entitlementData.setAttributes(attributes);
	}

	/**
	 * @return the ignoredAttributes
	 */
	public Set<String> getIgnoredAttributes()
	{
		return ignoredAttributes;
	}

	/**
	 * @param ignoredAttributes
	 *           the ignoredAttributes to set
	 */
	@Required
	public void setIgnoredAttributes(final Set<String> ignoredAttributes)
	{
		this.ignoredAttributes = ignoredAttributes;
	}

}
