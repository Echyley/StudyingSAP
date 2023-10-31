/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.velocity.eval;

import java.util.Map;
import java.util.Set;

public interface RowsEvaluator<T extends EvaluatedRow<?, ? extends Map>, K>
{
	Set<T> evaluateRows(String keyDescriminator, Map<K, ?> allProperties);

	Set<T> evaluateRows(Map<K, ?> allProperties);
}
