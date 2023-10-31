/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.facades;

import java.util.Map;


/**
 * Mapping for CPQ image formats.
 *
 * There are two image formats for characteristics (192Wx96H) and characteristic values (96Wx96H).
 */
public interface CPQImageFormatMapping
{
	/**
	 *
	 * @return mapping with CPQ image formats
	 */
	Map<String, CPQImageType> getCPQMediaFormatQualifiers();
}
