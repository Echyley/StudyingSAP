/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.core.service;

import java.io.Closeable;


/**
 * {@link SnProgressTracker} that extends {@link Closeable} in order to be able to release resources.
 */
public interface SnCloseableProgressTracker extends SnProgressTracker, Closeable
{
	// empty
}
