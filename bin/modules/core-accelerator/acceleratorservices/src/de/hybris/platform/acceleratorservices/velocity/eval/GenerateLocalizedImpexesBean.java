/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.velocity.eval;

import de.hybris.platform.acceleratorservices.velocity.VelocityExecutor;
import de.hybris.platform.acceleratorservices.velocity.ant.SourcePath;
import de.hybris.platform.acceleratorservices.velocity.ant.TargetPath;
import de.hybris.platform.acceleratorservices.velocity.core.CoreVelocityConfigurer;
import de.hybris.platform.acceleratorservices.velocity.locale.LocaleResolver;
import de.hybris.platform.acceleratorservices.velocity.resource.url.ScriptUrlContext;
import de.hybris.platform.acceleratorservices.velocity.resource.url.ScriptUrlContextFactory;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;


public class GenerateLocalizedImpexesBean implements Runnable
{
	private static final Logger LOG = LoggerFactory.getLogger(GenerateLocalizedImpexesBean.class);

	private SourcePath sourcePath;
	private TargetPath targetPath;

	private LocaleResolver localeResolverInstance;
	private ScriptUrlContextFactory scriptUrlContextFactory;
	private Map<String, List<String>> templateFiles;
	private CoreVelocityConfigurer<VelocityContext, ScriptUrlContext> velocityContextConfigurer;

	@Resource
	public void setLocaleResolverInstance(final LocaleResolver localeResolverInstance)
	{
		this.localeResolverInstance = localeResolverInstance;
	}

	@Resource
	public void setScriptUrlContextFactory(final ScriptUrlContextFactory scriptUrlContextFactory)
	{
		this.scriptUrlContextFactory = scriptUrlContextFactory;
	}


	protected CoreVelocityConfigurer<VelocityContext, ScriptUrlContext> getVelocityContextConfigurer()
	{
		return velocityContextConfigurer;
	}

	@Resource
	public void setVelocityContextConfigurer(
			final CoreVelocityConfigurer<VelocityContext, ScriptUrlContext> velocityContextConfigurer)
	{
		this.velocityContextConfigurer = velocityContextConfigurer;
	}

	public void setSourcePath(final SourcePath sourcePath)
	{
		this.sourcePath = sourcePath;
	}

	public void setTargetPath(final TargetPath targetPath)
	{
		this.targetPath = targetPath;
	}

	public void setTemplateFiles(final Map<String, List<String>> templateFiles)
	{
		this.templateFiles = templateFiles;
	}

	@Override
	public void run()
	{
		Preconditions.checkNotNull(sourcePath, "Source path should be not null");
		Preconditions.checkNotNull(targetPath, "Target path should be not null");
		for (final Locale supported : localeResolverInstance.getAllLocales())
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Starting processing for a {}", supported);
			}

			processFileSet(supported);

			if (LOG.isDebugEnabled())
			{
				LOG.debug("Finished processing for a {}", supported);
			}
		}
	}

	protected void processFileSet(final Locale locale)
	{
		for (final String rootFile : templateFiles.keySet())
		{
			for (final String fileName : templateFiles.get(rootFile))
			{
				final File templateFile = getTemplateFile(new File(rootFile), fileName);
				final ScriptUrlContext urlContext = scriptUrlContextFactory.create(locale, templateFile, sourcePath, targetPath); //build script context
				final VelocityExecutor velocityExecutor = getVelocityExecutor();
				final VelocityContext context = getVelocityContextConfigurer().configure(urlContext);
				velocityExecutor.execute(urlContext, context);
			}
		}
	}

	private File getTemplateFile(final File rootDir, final String fileName)
	{
		return new File(rootDir, fileName);
	}

	/**
	 * Inject here appropriate {@link VelocityExecutor} instance.
	 */
	public VelocityExecutor getVelocityExecutor()
	{
		throw new UnsupportedOperationException("Override it or inject via lookup in spring");
	}
}
