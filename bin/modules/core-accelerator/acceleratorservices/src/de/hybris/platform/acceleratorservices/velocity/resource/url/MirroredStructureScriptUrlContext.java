/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.velocity.resource.url;

import java.io.File;
import java.util.Locale;

import org.apache.commons.io.FilenameUtils;


/**
 * Provides a context information about expected localized property files {@link #getPropertyEffectivePath(String)} and
 * target script path/ file name {@link #getTargetScriptEffectiveRootPath()}/{@link #getTargetScriptEffectivePath()}
 */
public class MirroredStructureScriptUrlContext extends DefaultScriptUrlContext
{
	public MirroredStructureScriptUrlContext(final Locale locale, final File scriptFile, final String propertiesRelativePath,
			final String targetRelativePath)
	{
		super(locale, scriptFile, propertiesRelativePath, targetRelativePath);
	}

	@Override
	public String getTargetScriptEffectivePath()
	{
		final File sourceFolder = new File(getScriptRootFolder());
		final File targetPath = new File(getTargetScriptRelativePath());

		final File commonRoot = findCommonRoot(sourceFolder, targetPath);

		final String sourceRelativePath = sourceFolder.getAbsolutePath().substring(commonRoot.getAbsolutePath().length());

		final String targetIdentifier = FilenameUtils.getBaseName(getScriptFileName());
		final String targetFileName = getTargetScriptNameBuilder().buildFileName(getLocale(), targetIdentifier);

		final File targetFullPath = new File(targetPath.getAbsolutePath(), sourceRelativePath);
		return FilenameUtils.concat(targetFullPath.getAbsolutePath(), targetFileName);
	}

	private File findCommonRoot(File sourceFolder, final File targetPath)
	{
		File commonRoot = null;
		File commonRootAbove = sourceFolder;
		do
		{
			File localTargetPath = new File(targetPath.getAbsolutePath());
			do
			{
				if (sourceFolder.equals(localTargetPath))
				{
					commonRoot = commonRootAbove;
					return commonRoot;
				}
			}
			while ((localTargetPath = localTargetPath.getParentFile()) != null);
			commonRootAbove = sourceFolder;
		}
		while ((sourceFolder = sourceFolder.getParentFile()) != null);
		return targetPath;
	}

}
