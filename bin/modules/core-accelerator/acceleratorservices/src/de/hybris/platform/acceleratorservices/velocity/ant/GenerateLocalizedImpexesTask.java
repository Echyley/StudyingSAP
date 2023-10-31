/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.velocity.ant;

import de.hybris.bootstrap.config.ConfigUtil;
import de.hybris.bootstrap.config.PlatformConfig;
import de.hybris.bootstrap.config.SystemConfig;
import de.hybris.platform.acceleratorservices.velocity.eval.GenerateLocalizedImpexesBean;
import de.hybris.platform.acceleratorservices.velocity.locale.DefaultLocaleResolver;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.common.base.Preconditions;


public class GenerateLocalizedImpexesTask extends Task
{
	private static final String META_INF_SPRING_SPRING_IMPEX_XML = "classpath:/acceleratorservices/impexgenerator/generate-impex-spring.xml";

	private static final String PROPERTIES_SUPPORTED_LOCALES = "impex.generation.supported.locales";

	private GenerateLocalizedImpexesBean impexGeneratorBean;

	private SourcePath sourcePath;
	private TargetPath targetPath;

	public SourcePath createSourcePath()
	{
		sourcePath = new SourcePath(new ArrayList<>());
		return sourcePath;
	}

	public void addSourcePath(final SourcePath sourcePath)
	{
		this.sourcePath = sourcePath;
	}

	public TargetPath createTargetPath()
	{
		targetPath = new TargetPath();
		return targetPath;
	}

	public void addTargetPath(final TargetPath targetPath)
	{
		this.targetPath = targetPath;
	}

	protected Properties getSystemProperties()
	{
		PlatformConfig platformConfig;
		try
		{
			final SystemConfig systemConfig = SystemConfig.getInstanceByProps(getProject().getProperties());

			platformConfig = PlatformConfig.getInstance(systemConfig);
		}
		catch (final RuntimeException e)
		{
			throw new BuildException(e.getMessage(), e);
		}

		final Properties props = new Properties();
		ConfigUtil.loadRuntimeProperties(props, platformConfig);
		return props;
	}

	protected Map<String, List<String>> createMatchingTemplates()
	{
		final Map<String, List<String>> resultMap = new LinkedHashMap<>();
		for (final FileSet singleFileSet : sourcePath.getTemplateFileSet())
		{
			final File rootDir = singleFileSet.getDir();
			final String[] fileNames = singleFileSet.getDirectoryScanner(getProject()).getIncludedFiles();
			resultMap.put(rootDir.getAbsolutePath(), Arrays.asList(fileNames));
		}
		return resultMap;
	}

	@Override
	public void init()
	{
		final ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext();
		applicationContext.setClassLoader(this.getClass().getClassLoader());
		applicationContext.setConfigLocation(META_INF_SPRING_SPRING_IMPEX_XML);
		applicationContext.refresh();

		impexGeneratorBean = applicationContext.getBean(GenerateLocalizedImpexesBean.class);
		Preconditions.checkNotNull(applicationContext);
		Preconditions.checkNotNull(impexGeneratorBean, "Given task bean should be not null");
		super.init();
	}

	@Override
	public void execute()
	{
		impexGeneratorBean.setLocaleResolverInstance(new DefaultLocaleResolver(getSystemProperties().getProperty(
				PROPERTIES_SUPPORTED_LOCALES)));

		impexGeneratorBean.setSourcePath(sourcePath);
		impexGeneratorBean.setTargetPath(targetPath);

		impexGeneratorBean.setTemplateFiles(createMatchingTemplates());

		impexGeneratorBean.run();
	}
}
