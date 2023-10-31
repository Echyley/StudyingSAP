/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.velocity.resource.url;

import de.hybris.platform.acceleratorservices.velocity.resource.name.FileNameBuilder;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import org.apache.commons.io.FilenameUtils;


/**
 * Provides a context information about expected localized property files {@link #getPropertyEffectivePath(String)} and
 * target script path/ file name {@link #getTargetScriptEffectiveRootPath()}/{@link #getTargetScriptEffectivePath()}
 */
public class DefaultScriptUrlContext implements ScriptUrlContext
{
	private final String scriptRootFolder;
	private final Locale locale;
	private final String scriptFileName;

	private final String propertiesRelativePath;
	private final String targetScriptRelativePath;

	private final File targetScriptTemporaryFile;

	private FileNameBuilder propertyNameBuilder;
	private FileNameBuilder targetScriptNameBuilder;

	public DefaultScriptUrlContext(final Locale locale, final File scriptFile, final String propertiesRelativePath,
			final String targetRelativePath)
	{
		this.locale = locale;
		this.scriptRootFolder = scriptFile.getParent();
		this.scriptFileName = scriptFile.getName();

		this.targetScriptRelativePath = targetRelativePath;
		this.propertiesRelativePath = propertiesRelativePath;
		try
		{
			this.targetScriptTemporaryFile = File.createTempFile(scriptFileName + "_" + locale.getLanguage(), null);
		}
		catch (final IOException e)
		{
			throw new IllegalStateException(e);
		}
	}

	protected String getTargetScriptRelativePath()
	{
		return targetScriptRelativePath;
	}

	@Override
	public String getScriptRootFolder()
	{
		return scriptRootFolder;
	}

	@Override
	public String getScriptFileName()
	{
		return scriptFileName;
	}

	@Override
	public String getScriptFullPath()
	{
		return new File(getScriptRootFolder(), getScriptFileName()).getAbsolutePath();
	}

	@Override
	public File getTargetScriptTemporaryFile()
	{
		return targetScriptTemporaryFile;
	}

	@Override
	public String getPropertyEffectivePath(final String propertyIdentifer)
	{
		return FilenameUtils.concat(getPropertiesEffectiveRootPath(), propertyNameBuilder.buildFileName(locale, propertyIdentifer));
	}

	protected String getPropertiesEffectiveRootPath()
	{
		return FilenameUtils.concat(scriptRootFolder, propertiesRelativePath);
	}

	protected String getTargetScriptEffectiveRootPath()
	{
		return FilenameUtils.concat(scriptRootFolder, targetScriptRelativePath);
	}

	@Override
	public String getTargetScriptEffectivePath()
	{
		final String targetIdentifier = FilenameUtils.getBaseName(getScriptFileName());

		final String effectiveFileName = getTargetScriptEffectiveRootPath();
		return FilenameUtils.concat(effectiveFileName, targetScriptNameBuilder.buildFileName(locale, targetIdentifier));
	}

	@Override
	public Locale getLocale()
	{
		return locale;
	}

	public void setPropertyNameBuilder(final FileNameBuilder nameBuilder)
	{
		this.propertyNameBuilder = nameBuilder;
	}

	protected FileNameBuilder getTargetScriptNameBuilder()
	{
		return targetScriptNameBuilder;
	}

	public void setTargetScriptNameBuilder(final FileNameBuilder targetScriptNameBuilder)
	{
		this.targetScriptNameBuilder = targetScriptNameBuilder;
	}
}
