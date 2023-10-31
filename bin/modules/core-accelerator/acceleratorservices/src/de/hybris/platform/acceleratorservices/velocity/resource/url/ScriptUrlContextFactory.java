/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.velocity.resource.url;

import java.io.File;
import java.util.Locale;


public interface ScriptUrlContextFactory<S, T>
{
	ScriptUrlContext create(final Locale locale, final File scriptFile, final S propertiesRelativePath, final T targetRelativePath);
}
