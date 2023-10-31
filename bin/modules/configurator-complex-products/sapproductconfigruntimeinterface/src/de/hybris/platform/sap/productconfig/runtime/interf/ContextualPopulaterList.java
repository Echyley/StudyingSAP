/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.interf;

import java.util.List;


/**
 * Interface for a list of populators.
 *
 * @param <SOURCE>
 *           the type of the source object
 * @param <TARGET>
 *           the type of the destination object
 * @param <OPTIONS>
 *           the type of the options/context object
 */
public interface ContextualPopulaterList<SOURCE, TARGET, OPTIONS>
{


	/**
	 * Get the list of populators.
	 *
	 * @return the populators.
	 */
	List<ContextualPopulator<SOURCE, TARGET, OPTIONS>> getContextualPopulators();

	/**
	 * Set the list of populators.
	 *
	 * @param populators
	 *           the populators
	 */
	void setContextualPopulators(List<ContextualPopulator<SOURCE, TARGET, OPTIONS>> populators);
}
