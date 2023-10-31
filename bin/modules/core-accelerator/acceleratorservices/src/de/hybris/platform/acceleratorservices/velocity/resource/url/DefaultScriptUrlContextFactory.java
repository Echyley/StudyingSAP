/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.velocity.resource.url;

import de.hybris.platform.acceleratorservices.velocity.ant.SourcePath;
import de.hybris.platform.acceleratorservices.velocity.ant.TargetPath;
import de.hybris.platform.acceleratorservices.velocity.resource.name.DefaultPropertyNameBuilder;
import de.hybris.platform.acceleratorservices.velocity.resource.name.DefaultTargetScriptNameBuilder;

import java.io.File;
import java.util.Locale;


public class DefaultScriptUrlContextFactory implements ScriptUrlContextFactory<SourcePath, TargetPath>
{
	@Override
	public ScriptUrlContext create(final Locale locale, final File scriptFile, final SourcePath propertiesPath,
			final TargetPath targetPath)
	{
		DefaultScriptUrlContext context = null;
		if (targetPath.isRelative())
		{
			context = new DefaultScriptUrlContext(locale, scriptFile, propertiesPath.getPropertyFilePath(),
					targetPath.getTargetFilePath());
		}
		else
		{
			context = new MirroredStructureScriptUrlContext(locale, scriptFile, propertiesPath.getPropertyFilePath(),
					targetPath.getTargetFilePath());
		}

		context.setPropertyNameBuilder(new DefaultPropertyNameBuilder());
		context.setTargetScriptNameBuilder(new DefaultTargetScriptNameBuilder());
		return context;
	}
}
