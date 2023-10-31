/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.velocity.resource.url;

import java.io.File;
import java.util.Locale;



public interface ScriptUrlContext
{
	String getScriptFullPath();

	String getScriptRootFolder();

	String getScriptFileName();

	String getPropertyEffectivePath(String propertyIdentifier);

	String getTargetScriptEffectivePath();

	File getTargetScriptTemporaryFile();

	Locale getLocale();

}
