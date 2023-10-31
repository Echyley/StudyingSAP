/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.interf;

import de.hybris.platform.converters.Populator;


/**
 * Compared to ordinary {@link Populator}s, a ContextualPopulator is context aware, meaning that a context can be
 * provided for the populating process.
 *
 * @param <SOURCE>
 *           type of source object
 * @param <TARGET>
 *           type of target object
 * @param <CONTEXT>
 *           type of context object
 */
public interface ContextualPopulator<SOURCE, TARGET, CONTEXT>
{
	/**
	 * Populate the target instance from the source instance. The collection of options is used to control what data is
	 * populated.
	 *
	 * @param source
	 *           the source object
	 * @param target
	 *           the target to fill
	 * @param context
	 *           populating context
	 * @param options
	 *           options used to control what data is populated
	 */
	void populate(SOURCE source, TARGET target, CONTEXT context);
}
