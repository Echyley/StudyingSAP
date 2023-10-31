/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.velocity.eval;

import java.util.Map;


/**
 * Abstraction containing evaluated data where the key is to be unique (it might contain various kind of data) for
 * target impex script and value is {@link Map} of type attribute and it value.
 *
 * @param <K> KEY
 * @param <V> VALUE
 */
public interface EvaluatedRow<K, V extends Map>
{
	K getKey();

	V getValues();
}
