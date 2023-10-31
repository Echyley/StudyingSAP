/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundservices.event.impl;

import de.hybris.platform.outboundservices.event.EventType;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

/**
 * The EventType stores the operation value for CloudEvent type
 */
public class DefaultEventType implements EventType
{
	private static final long serialVersionUID = 4351394431203163550L;
	private static final String UNKNOWN_EVENT_TYPE = "Unknown";
	private static final String CREATED_EVENT_TYPE = "Created";
	private static final String UPDATED_EVENT_TYPE = "Updated";
	private static final String DELETED_EVENT_TYPE = "Deleted";
	public static final EventType UNKNOWN = new DefaultEventType(UNKNOWN_EVENT_TYPE);
	public static final EventType CREATED = new DefaultEventType(CREATED_EVENT_TYPE);
	public static final EventType UPDATED = new DefaultEventType(UPDATED_EVENT_TYPE);
	public static final EventType DELETED = new DefaultEventType(DELETED_EVENT_TYPE);

	private final String type;

	/**
	 * Constructor with specific type
	 *
	 * @param type specific operation value
	 */
	public DefaultEventType(final String type)
	{
		this.type = StringUtils.isNotBlank(type) ? type : UNKNOWN_EVENT_TYPE;
	}

	@Override
	public String getType()
	{
		return type;
	}

	@Override
	public String toString()
	{
		return "DefaultEventType{" +
				"type='" + type + '\'' +
				'}';
	}

	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}

		if (o == null || getClass() != o.getClass())
		{
			return false;
		}

		final DefaultEventType that = (DefaultEventType) o;
		return this.getType().equals(that.getType());
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(type);
	}
}
