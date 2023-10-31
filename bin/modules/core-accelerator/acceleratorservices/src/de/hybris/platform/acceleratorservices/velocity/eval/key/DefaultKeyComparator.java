/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.velocity.eval.key;

import de.hybris.platform.acceleratorservices.velocity.eval.EvaluatedRow;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Map;

import org.apache.commons.collections.ComparatorUtils;

/**
 * Natural comparator based on the keys
 */
public class DefaultKeyComparator implements Comparator<EvaluatedRow<String, Map<?, ?>>>, Serializable
{
	@Override
	public int compare(final EvaluatedRow<String, Map<?, ?>> o1, final EvaluatedRow<String, Map<?, ?>> o2)
	{
		return ComparatorUtils.NATURAL_COMPARATOR.compare(o1.getKey(), o2.getKey());
	}
}
