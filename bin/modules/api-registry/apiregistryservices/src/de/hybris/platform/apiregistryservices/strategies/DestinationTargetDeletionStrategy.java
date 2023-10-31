/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.apiregistryservices.strategies;

import de.hybris.platform.apiregistryservices.model.DestinationTargetModel;


/**
 * Strategy for deleting destination target
 */
public interface DestinationTargetDeletionStrategy
{
	/**
	 * Delete destination target and all related objects (such as destinations, event configurations)
	 * are not consumed by other destinations targets, destinations or other possible entities
	 * @param destinationTarget the destinations target
	 */
	void deleteDestinationTarget(DestinationTargetModel destinationTarget);
}
