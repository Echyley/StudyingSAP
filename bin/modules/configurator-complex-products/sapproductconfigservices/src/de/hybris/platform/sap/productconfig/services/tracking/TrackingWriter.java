/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.tracking;

/**
 * Will persist or process {@link TrackingItem}s recorded by the {@link TrackingRecorder}.
 */
public interface TrackingWriter
{

	/**
	 * Callback to notify the write that a new history item was created.
	 *
	 * @param item
	 */
	void trackingItemCreated(TrackingItem item);

}
