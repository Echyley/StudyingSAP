/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.inboundservices.persistence;

/**
 * Service to persist and save platform items
 */
public interface ItemPersistenceService
{
	/**
	 * Persists items obtained from a request into the platform
	 * @param context a context describing what data need to be persisted.
	 */
	void persist(PersistenceContext context);
}
