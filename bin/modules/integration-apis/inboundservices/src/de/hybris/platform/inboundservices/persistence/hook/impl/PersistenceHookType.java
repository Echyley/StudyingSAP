/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.inboundservices.persistence.hook.impl;

/**
 * Enumerated type to classify hook types for context population
 */
public enum PersistenceHookType
{
	/**
	 * Indicates a {@link de.hybris.platform.inboundservices.persistence.hook.PrePersistHook}
	 */
	PRE,
	/**
	 * Indicates a {@link de.hybris.platform.inboundservices.persistence.hook.PostPersistHook}
	 */
	POST
}
