/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps;

/**
 * Parses conflict texts from configuration engine into its display ready form
 */
public interface CPSConflictTextParser
{
	/**
	 * Parses conflict texts from CPS service
	 *
	 * @param rawText
	 *           Text from CPS service
	 * @return Parsed text. Is returned as empty string in case input is null
	 */
	String parseConflictText(final String rawText);
}
