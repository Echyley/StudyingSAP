/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.carrotsearch.sizeof;


/**
 * This class is used to replace RamUsageEstimator in java-sizeof as java-sizeof is end of life
 * will need to be removed if orika release a newer version which is not include java-sizeof
 */
public final class RamUsageEstimator
{
	private static final long NUMBER_ZERO = 0;

	private RamUsageEstimator()
	{
	}

	/**
	 * @param objects to keep the function parameter not changed
	 *                0 is returned in orika's master branch
	 */
	public static long sizeOfAll(Object... objects)
	{
		return NUMBER_ZERO;
	}
}
