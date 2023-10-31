/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.velocity.eval.key;

public interface KeyEvaluator<T>
{
	void setKeySeparator(String separator);

	String[] eval(T key);
}
