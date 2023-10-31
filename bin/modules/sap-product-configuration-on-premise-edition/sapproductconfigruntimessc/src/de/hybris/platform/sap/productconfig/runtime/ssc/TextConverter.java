/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.ssc;

import de.hybris.platform.sap.productconfig.runtime.interf.TextConverterBase;

import com.sap.custdev.projects.fbs.slc.cfg.client.ITextDescription;


/**
 * Text Converter class for converting SSC/backend texts from ITF format into plain text format to be used within
 * hybris.
 */
public interface TextConverter extends TextConverterBase
{
	/**
	 * Converts the text array we got from SSC into a text. Also takes care of different sections of the text in the
	 * modeling environment. Removes all meta text elements (bold, underline, etc.)
	 *
	 * @param textDescriptionArray
	 * @return Dependency text
	 */
	String convertDependencyText(ITextDescription[] textDescriptionArray);

}
