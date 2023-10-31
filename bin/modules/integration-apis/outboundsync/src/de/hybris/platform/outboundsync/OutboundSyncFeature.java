/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundsync;

import de.hybris.platform.integrationservices.util.featuretoggle.Feature;

/**
 * Enums defining the outboundsync features that can be toggled
 */
public enum OutboundSyncFeature implements Feature
{
	// This property is defined in the outboundsync extension's project.properties
	DELETE("outboundsync.delete.enabled");

	private final String property;

	OutboundSyncFeature(final String prop)
	{
		property = prop;
	}

	@Override
	public String getProperty()
	{
		return property;
	}
}
