/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.velocity.core;

import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.velocity.app.VelocityEngine;

public class DefaultVelocityEngineConfigurer implements CoreVelocityConfigurer<VelocityEngine, Void>
{
	private Map properties;

	public void setProperties(final Map properties)
	{
		this.properties = properties;
	}

	@Override
	public VelocityEngine configure()
	{
		return new VelocityEngine(MapUtils.toProperties(properties));
	}

	@Override
	public VelocityEngine configure(final Void context)
	{
		throw new UnsupportedOperationException();
	}
}
