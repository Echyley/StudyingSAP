/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.velocity.resource.store;


import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.velocity.BaseFileSandBoxTest;
import de.hybris.platform.acceleratorservices.velocity.resource.name.DefaultPropertyNameBuilder;
import de.hybris.platform.acceleratorservices.velocity.resource.name.DefaultTargetScriptNameBuilder;
import de.hybris.platform.acceleratorservices.velocity.resource.url.DefaultScriptUrlContext;
import de.hybris.platform.acceleratorservices.velocity.resource.url.MirroredStructureScriptUrlContext;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * Test presenting other strategy for resolving target dir. target dir is calculated as: starting from first common
 * folder for target and source dir we build a mirror path relative in target dir as it was in source dir . Example :
 *
 * <pre>
 * source: /mainSource/subFolder/data/template.vt
 * target: /mainTarget/
 * </pre>
 *
 * So the out put folder for {@link MirroredStructureScriptUrlContext#getTargetScriptEffectivePath()} will be
 *
 * <pre>
 * /mainTarget/subFolder/data/template.vt
 * </pre>
 *
 * Consequently for a case :
 *
 * <pre>
 * source: /root/folderA/template.vt
 * target: /root/folderB/
 *
 * </pre>
 *
 * , will produce a :
 *
 *
 * <pre>
 * /root/folderB/template.vt
 * </pre>
 *
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/acceleratorservices/test/impexgenerator/test-generate-impex-spring.xml"})
@UnitTest
public class PathTemplateTest extends BaseFileSandBoxTest
{
	/**
	 *
	 */
	private static final String PROPERTIES_RELATIVE_PATH = ".";

	private DefaultScriptUrlContext ctx = null;

	private final String ROOT_SAND_BOX = "/ctxTest";

	@Value("classpath:acceleratorservices/test/impexgenerator")
	private Resource rootDir;

	private File source;

	@Override
	protected Resource getRootDir()
	{
		return rootDir;
	}

	@Test
	public void testRelativePaths() throws IOException
	{
		source = createSetupDirInSandBox("/resources-lang/impex/data/catalog/");
		final File file = new File(source, "somefile.txt");
		FileUtils.write(file, "some blah");


		ctx = new MirroredStructureScriptUrlContext(Locale.CHINESE, file, PROPERTIES_RELATIVE_PATH,
				createPathInSandBox("/resources"));

		ctx.setPropertyNameBuilder(new DefaultPropertyNameBuilder());
		ctx.setTargetScriptNameBuilder(new DefaultTargetScriptNameBuilder());

		Assert.assertEquals(createPathInSandBox("/resources/impex/data/catalog/somefile_zh.impex"),
				ctx.getTargetScriptEffectivePath());
	}


	@Test
	public void testRelativePathsOtherCase() throws IOException
	{
		source = createSetupDirInSandBox("/someResource/impex/data/catalog/x");
		final File file = new File(source, "somefile.txt");
		FileUtils.write(file, "some blah");

		ctx = new MirroredStructureScriptUrlContext(Locale.ITALIAN, file, PROPERTIES_RELATIVE_PATH,
				createPathInSandBox("/someResources/y"));

		ctx.setPropertyNameBuilder(new DefaultPropertyNameBuilder());
		ctx.setTargetScriptNameBuilder(new DefaultTargetScriptNameBuilder());

		Assert.assertEquals(createPathInSandBox("/someResources/y/impex/data/catalog/x/somefile_it.impex"),
				ctx.getTargetScriptEffectivePath());
	}

	@Test
	public void testRelativePathsOtherPathsConcat() throws IOException
	{
		source = createSetupDirInSandBox("/someResource/impex/data/catalog/x");
		final File file = new File(source, "somefile.txt");
		FileUtils.write(file, "some blah");

		ctx = new MirroredStructureScriptUrlContext(Locale.KOREA, file, PROPERTIES_RELATIVE_PATH,
				createPathInSandBox("/someResources/impex/data/catalog/z"));

		ctx.setPropertyNameBuilder(new DefaultPropertyNameBuilder());
		ctx.setTargetScriptNameBuilder(new DefaultTargetScriptNameBuilder());

		Assert.assertEquals(createPathInSandBox("/someResources/impex/data/catalog/z/impex/data/catalog/x/somefile_ko.impex"),
				ctx.getTargetScriptEffectivePath());
	}


	@Test
	public void testRelativePathsCornerCase() throws IOException
	{
		source = createSetupDirInSandBox("/test/products");
		final File file = new File(source, "somefile.txt");
		FileUtils.write(file, "some blah");

		ctx = new MirroredStructureScriptUrlContext(Locale.US, file, PROPERTIES_RELATIVE_PATH,
				createPathInSandBox("/test/generated/"));

		ctx.setPropertyNameBuilder(new DefaultPropertyNameBuilder());
		ctx.setTargetScriptNameBuilder(new DefaultTargetScriptNameBuilder());

		Assert.assertEquals(createPathInSandBox("/test/generated/somefile_en.impex"), ctx.getTargetScriptEffectivePath());
	}


	@Test
	public void testRelativePathsSampleDateCase() throws IOException
	{
		source = createSetupDirInSandBox("/resources-lang/acceleratorsampledata/import/productCatalogs/apparelProductCatalog");
		final File file = new File(source, "somefile.txt");
		FileUtils.write(file, "some blah");

		ctx = new MirroredStructureScriptUrlContext(Locale.US, file, PROPERTIES_RELATIVE_PATH, createPathInSandBox("/resources"));

		ctx.setPropertyNameBuilder(new DefaultPropertyNameBuilder());
		ctx.setTargetScriptNameBuilder(new DefaultTargetScriptNameBuilder());

		Assert.assertEquals(
				createPathInSandBox("/resources/acceleratorsampledata/import/productCatalogs/apparelProductCatalog/somefile_en.impex"),
				ctx.getTargetScriptEffectivePath());
	}

	protected String createPathInSandBox(final String path) throws IOException
	{
		final File file = new File(rootDir.getFile(), ROOT_SAND_BOX + path);
		return file.getCanonicalPath();
	}

	protected File createSetupDirInSandBox(final String path) throws IOException
	{
		final File file = new File(createPathInSandBox(path));
		if (!file.exists())
		{
			FileUtils.forceMkdir(file);
		}
		return file;
	}

	@After
	public void teardown() throws IOException {
		final File generatedDir = new File(getRootDir().getFile(), ROOT_SAND_BOX);

		if (generatedDir.exists())
		{
			FileUtils.deleteDirectory(generatedDir);
		}
	}

}
