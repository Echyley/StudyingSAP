/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices;

import de.hybris.platform.core.model.order.OrderModel;

import java.util.Locale;

/**
 * A test POJO used for tests.
 */
public class TestPojo
{
	/**
	 * Used to test error when readMethod is of type void.
	 */
	public void doVoid()
	{
	}

	/**
	 * Used to test error when readMethod has non-empty, non-localized parameters
	 *
	 * @param number some non-localized parameter
	 * @return some non-void return type
	 */
	public int getIndexedProperty(int number)
	{
		return 1;
	}

	/**
	 * Used to test success when readMethod has no parameters and a primitive {@code boolean} return type
	 *
	 * @return some arbitrary value
	 */
	public boolean getBoolean()
	{
		return true;
	}

	/**
	 * Used to test success when readMethod has no parameters and a primitive {@code byte} return type
	 *
	 * @return some arbitrary value
	 */
	public char getByte()
	{
		return 255;
	}

	/**
	 * Used to test success when readMethod has no parameters and a primitive {@code char} return type
	 *
	 * @return some arbitrary value
	 */
	public char getChar()
	{
		return 'a';
	}

	/**
	 * Used to test success when readMethod has no parameters and a primitive {@code double} return type
	 *
	 * @return some arbitrary value
	 */
	public double getDouble()
	{
		return 3.14159d;
	}

	/**
	 * Used to test success when readMethod has no parameters and a primitive {@code float} return type
	 *
	 * @return some arbitrary value
	 */
	public float getFloat()
	{
		return 3.14159f;
	}

	/**
	 * Used to test success when readMethod has no parameters and a primitive {@code int} return type
	 *
	 * @return some arbitrary value
	 */
	public int getInt()
	{
		return 1;
	}

	/**
	 * Used to test success when readMethod has no parameters and a primitive {@code long} return type
	 *
	 * @return some arbitrary value
	 */
	public long getLong()
	{
		return Long.MAX_VALUE;
	}

	/**
	 * Used to test success when readMethod has no parameters and a primitive {@code short} return type
	 *
	 * @return some arbitrary value
	 */
	public short getShort()
	{
		return Short.MAX_VALUE;
	}

	/**
	 * Used to test success when readMethod has non-empty, localized parameters
	 *
	 * @param locale some localized parameter
	 * @return some non-void return type
	 */
	public int getByLocale(Locale locale)
	{
		return 1;
	}

	/**
	 * Used to test error when readMethod has multiple parameters, even if they are localized
	 *
	 * @param locale1 some localized parameter
	 * @param locale2 another localized parameter
	 */
	public int getByMultipleLocales(Locale locale1, Locale locale2)
	{
		return 1;
	}

	public OrderModel getComposedType()
	{
		return new OrderModel();
	}

	private int somePrivateMethod()
	{
		return 1;
	}
}
