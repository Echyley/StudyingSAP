/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.velocity.eval;

import java.util.Collection;



public interface PropertyEvaluator
{
	/**
	 *
	 * Method which loads a given resource. The resource is a identifier used to build a physical path to property file
	 * containing localized attribute values. Method returns collection of evaluated rows. See {@link EvaluatedRow} for
	 * more details.
	 *
	 */
	Collection<? extends EvaluatedRow<?, ?>> load(final String resourceId);

	/**
	 *
	 * Method which loads a given resource. The resource is a identifier used to build a physical path to property file
	 * containing localized attribute values. Adds additional key for filtering out localized properties not matching
	 * given type.
	 *
	 * Method returns collection of evaluated rows. See {@link EvaluatedRow} for more details.
	 *
	 */
	Collection<? extends EvaluatedRow<?, ?>> load(final String resourceId, final String key);

	void setRowEvaluator(final RowsEvaluator<? extends EvaluatedRow<?, ?>, ?> rowEvaluator);

}
