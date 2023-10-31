/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.interf;

/**
 * Text Converter class for converting backend texts from ITF format into plain text format to be used within hybris.
 */
public interface TextConverterBase
{
	/**
	 * Removes all meta text elements (bold, underline, etc.) out of the long text value, provided by the knowledge base.
	 *
	 * @param formattedText
	 *           The text to be cleansed
	 * @return The converted text, which will be shown in the web frontend
	 */
	String convertLongText(String formattedText);
}
