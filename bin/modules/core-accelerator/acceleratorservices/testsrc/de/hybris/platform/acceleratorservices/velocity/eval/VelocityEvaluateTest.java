/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.velocity.eval;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.velocity.BaseFileSandBoxTest;
import de.hybris.platform.acceleratorservices.velocity.ant.SourcePath;
import de.hybris.platform.acceleratorservices.velocity.ant.TargetPath;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/acceleratorservices/test/impexgenerator/test-generate-impex-spring.xml"})
@UnitTest
public class VelocityEvaluateTest extends BaseFileSandBoxTest
{
	public static final Logger LOG = Logger.getLogger(VelocityEvaluateTest.class);

	@javax.annotation.Resource
	private GenerateLocalizedImpexesBean impexGenerator;

	@Value("classpath:/acceleratorservices/test/impexgenerator/test-encoding")
	private Resource rootDir;

	@Value("classpath:/acceleratorservices/test/impexgenerator/test-encoding/key-data-lexcial-error-chars.test")
	private Resource resourceLexical;

	private static final String GENERATED_IMPEXES_DIR = "generated";

	@Override
	protected Resource getRootDir()
	{
		return rootDir;
	}


	@Test
	public void testLoadLexicalErrorData() throws Exception
	{

		final TargetPath target = new TargetPath();
		target.setTargetFilePath("./" + GENERATED_IMPEXES_DIR);

		final SourcePath source = new SourcePath(Collections.EMPTY_LIST);
		source.setPropertyFilePath(".");

		final File file = resourceLexical.getFile().getAbsoluteFile();
		final Map<String, List<String>> templates = Collections.singletonMap(file.getParentFile().getAbsolutePath(),
				Collections.singletonList(file.getName()));

		impexGenerator.setTemplateFiles(templates);
		impexGenerator.setSourcePath(source);
		impexGenerator.setTargetPath(target);
		//impexGenerator.setPropertyEvaluatorFactory(removeRowEvaluator);


		impexGenerator.run();

		assertGenerated(GENERATED_IMPEXES_DIR, new String[]
		{ "key-data-lexcial-error-chars_de.impex", "key-data-lexcial-error-chars_en.impex" });
		assertFileContains(GENERATED_IMPEXES_DIR, new String[]
		{ "key-data-lexcial-error-chars_de.impex", "key-data-lexcial-error-chars_en.impex" },
				"INSERT_UPDATE Media;$contentCV[unique=true];");
		assertFileContains(GENERATED_IMPEXES_DIR, new String[]
		{ "key-data-lexcial-error-chars_en.impex" }, "INSERT_UPDATE Media;$contentCV[lang=en];");

		assertFileContains(GENERATED_IMPEXES_DIR, new String[]
		{ "key-data-lexcial-error-chars_de.impex" }, "INSERT_UPDATE Media;$contentCV[lang=de];");
		assertFileContains(GENERATED_IMPEXES_DIR, new String[]
		{ "key-data-lexcial-error-chars_en.impex" }, "INSERT_UPDATE Media;$contentCV[lang=en];");



	}


	private void assertFileContains(final String rootgenerated, final String[] fileNames, final String containsString)
			throws IOException
	{
		final File generatedDir = new File(rootDir.getFile(), rootgenerated);
		for (final String expectedFileName : fileNames)
		{
			final File file = new File(generatedDir, expectedFileName);
			boolean contains = false;
			final List<String> lines = FileUtils.readLines(file);
			for (final String line : lines)
			{
				if (line.equals(containsString))
				{
					contains = true;
					break;
				}
			}
			Assert.assertTrue("File " + file + " should contain line [" + containsString + "]", contains);
		}

	}


}
