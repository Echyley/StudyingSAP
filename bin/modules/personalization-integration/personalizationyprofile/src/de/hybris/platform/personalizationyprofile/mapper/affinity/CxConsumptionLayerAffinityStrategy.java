/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.personalizationyprofile.mapper.affinity;

import de.hybris.platform.personalizationyprofile.yaas.Affinity;

import java.math.BigDecimal;


/**
 * Strategy for calculating affinity.
 * 
 * @deprecated since 2011, Profile structure has changed and mappers which have used that strategy are deprecated
 */
@Deprecated(since = "2011", forRemoval = true)
public interface CxConsumptionLayerAffinityStrategy
{
	/**
	 * Extracts value of affinity from provided structure.
	 *
	 * @param affinity
	 *           structure from which affinity is extracted
	 * @return single affinity value
	 */
	BigDecimal extract(Affinity affinity);
}
