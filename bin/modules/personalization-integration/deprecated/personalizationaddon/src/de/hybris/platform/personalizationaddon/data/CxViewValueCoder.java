/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.personalizationaddon.data;

/**
 * Helps to encode and decode values provided on storefront
 */
public interface CxViewValueCoder
{
	/**
	 * Encodes input
	 * 
	 * @param input
	 *           data to encode
	 * @return encded data
	 */
	String encode(final String input);

	/**
	 * Decodes input
	 * 
	 * @param input
	 *           data to decode
	 * @return decoded data
	 */
	String decode(final String input);
}
