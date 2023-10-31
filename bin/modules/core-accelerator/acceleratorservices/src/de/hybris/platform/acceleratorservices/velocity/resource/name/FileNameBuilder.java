/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.velocity.resource.name;

import java.util.Locale;


public interface FileNameBuilder
{
	String buildFileName(Locale locale, String resourceIdentifier);

}
