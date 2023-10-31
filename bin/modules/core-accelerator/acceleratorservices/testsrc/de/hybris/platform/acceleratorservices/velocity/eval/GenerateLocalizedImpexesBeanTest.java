/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.velocity.eval;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.acceleratorservices.velocity.BaseFileSandBoxTest;
import de.hybris.platform.acceleratorservices.velocity.ant.SourcePath;
import de.hybris.platform.acceleratorservices.velocity.ant.TargetPath;
import de.hybris.platform.acceleratorservices.velocity.core.CoreVelocityConfigurer;
import de.hybris.platform.acceleratorservices.velocity.locale.DefaultLocaleResolver;
import de.hybris.platform.acceleratorservices.velocity.locale.LocaleResolver;
import de.hybris.platform.acceleratorservices.velocity.resource.url.ScriptUrlContext;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/acceleratorservices/test/impexgenerator/test-generate-impex-spring.xml"})
@IntegrationTest
public class GenerateLocalizedImpexesBeanTest extends BaseFileSandBoxTest
{
	private static final String PRODUCTS_IMPEX_PL_IMPEX = "products_pl.impex";
	private static final String PRODUCTS_IMPEX_EN_IMPEX = "products_en.impex";

	private static final String GENERATED_IMPEXES_DIR = "generated";

	private static final String TEST_CASE_ONE_FOLDER = "test-case-one";
	private static final String TEST_CASE_TWO_FOLDER = "test-case-two";
	private static final String TEST_CASE_THREE_FOLDER = "test-case-three";

	public static final Logger LOG = Logger.getLogger(GenerateLocalizedImpexesBeanTest.class);

	@javax.annotation.Resource
	private GenerateLocalizedImpexesBean impexGenerator;

	@Value("classpath:/acceleratorservices/test/impexgenerator")
	private Resource rootDir;

	@javax.annotation.Resource(name = "velocityContextConfigurerRemove")
	private CoreVelocityConfigurer<VelocityContext, ScriptUrlContext> removeRowConfigurer;

	@javax.annotation.Resource(name = "velocityContextConfigurerAbort")
	private CoreVelocityConfigurer<VelocityContext, ScriptUrlContext> abortRowConfigurer;

	@Spy
	private final LocaleResolver resolverMock = new DefaultLocaleResolver();

	@Mock
	private SourcePath source;

	@Override
	protected Resource getRootDir()
	{
		return rootDir;
	}

	@Override
	@Before
	public void prepare() throws IOException
	{
		super.prepare();

		MockitoAnnotations.initMocks(this);
		impexGenerator.setLocaleResolverInstance(resolverMock);
		BDDMockito.given(resolverMock.getAllLocales()).willReturn(Arrays.asList(Locale.ENGLISH, new Locale("pl")));
	}

	@DirtiesContext
	@Test
	public void testCallNoSourceAndTargetFileSet()
	{
		try
		{
			impexGenerator.run();
			Assert.fail("should not continue");
		}
		catch (final NullPointerException npe)
		{
			Assert.assertEquals("Source path should be not null", npe.getMessage());
		}
	}


	@Test
	public void testCallNoTargetFileSet()
	{
		setSourcePath("some path");
		assertThatThrownBy( () -> impexGenerator.run() )
				.isInstanceOf(NullPointerException.class)
				.hasMessage("Target path should be not null");
	}

	@Test
	public void testNotExistingPropertiesInAbortMode() throws Exception
	{
		final TargetPath target = new TargetPath();
		target.setTargetFilePath("./" + GENERATED_IMPEXES_DIR);

		impexGenerator.setTemplateFiles(getAllMatchingTemplates(rootDir.getFile()));
		setSourcePath("./not-existing-localized");
		impexGenerator.setTargetPath(target);
		impexGenerator.setVelocityContextConfigurer(abortRowConfigurer);

		try
		{
			impexGenerator.run();
			Assert.fail("Should abort script if properties does not exists");
		}
		catch (final ResourceNotFoundException mie)
		{
			//mie.printStackTrace();
			//
		}
		assertNothingGenerated(GENERATED_IMPEXES_DIR);
	}

	@Test
	public void testNotExistingPropertiesInAbortModeForNonRelative() throws Exception
	{
		final TargetPath target = new TargetPath();
		target.setTargetFilePath(new File(rootDir.getFile(), GENERATED_IMPEXES_DIR).getAbsolutePath());
		target.setRelative(false);

		impexGenerator.setTemplateFiles(getAllMatchingTemplates(new File(rootDir.getFile(), "/test-lang/products/")));
		setSourcePath("./not-existing-localized");
		impexGenerator.setTargetPath(target);
		impexGenerator.setVelocityContextConfigurer(abortRowConfigurer);

		try
		{
			impexGenerator.run();
			Assert.fail("Should abort script if properties does not exists");
		}
		catch (final ResourceNotFoundException mie)
		{
			//mie.printStackTrace();
		}

		assertNothingGenerated(GENERATED_IMPEXES_DIR);
	}

	@Test
	public void testPartiallyExistingPropertiesInAbortMode() throws Exception
	{
		final TargetPath target = new TargetPath();
		target.setTargetFilePath("./" + GENERATED_IMPEXES_DIR);

		impexGenerator.setTemplateFiles(getAllMatchingTemplates(rootDir.getFile()));
		impexGenerator.setTargetPath(target);
		setSourcePath("./" + TEST_CASE_ONE_FOLDER);
		impexGenerator.setVelocityContextConfigurer(abortRowConfigurer);
		//impexGenerator.setPropertyEvaluatorFactory(abortRowEvaluator);

		try
		{
			impexGenerator.run();
			Assert.fail("Should abort script if any properties does not exists");
		}
		catch (final ResourceNotFoundException mie)
		{
			//
		}

		assertGenerated(GENERATED_IMPEXES_DIR, new String[] { PRODUCTS_IMPEX_EN_IMPEX });
	}

	@Test
	public void testNotExistingPropertiesInWarnModeForNonRelative() throws Exception
	{

		final TargetPath target = new TargetPath();
		target.setTargetFilePath(new File(rootDir.getFile(), GENERATED_IMPEXES_DIR).getAbsolutePath());
		target.setRelative(false);


		impexGenerator.setTemplateFiles(getAllMatchingTemplates(new File(rootDir.getFile(), "/test-lang/products/")));
		impexGenerator.setTargetPath(target);
		setSourcePath("./not-existing-localized");
		impexGenerator.setVelocityContextConfigurer(removeRowConfigurer);

		impexGenerator.run();

		assertNothingGenerated(GENERATED_IMPEXES_DIR);
	}

	@Test
	public void testNotExistingPropertiesInWarnMode() throws Exception
	{
		final TargetPath target = new TargetPath();
		target.setTargetFilePath("./" + GENERATED_IMPEXES_DIR);

		impexGenerator.setTemplateFiles(getAllMatchingTemplates(rootDir.getFile()));
		impexGenerator.setTargetPath(target);
		setSourcePath("./not-existing-localized");
		impexGenerator.setVelocityContextConfigurer(removeRowConfigurer);

		impexGenerator.run();

		assertNothingGenerated(GENERATED_IMPEXES_DIR);
	}

	@Test
	public void testPartiallyExistingPropertiesInWarnMode() throws Exception
	{
		final TargetPath target = new TargetPath();
		target.setTargetFilePath("./" + GENERATED_IMPEXES_DIR);
		//

		impexGenerator.setTemplateFiles(getAllMatchingTemplates(rootDir.getFile()));
		impexGenerator.setTargetPath(target);
		setSourcePath("./" + TEST_CASE_ONE_FOLDER);
		impexGenerator.setVelocityContextConfigurer(removeRowConfigurer);

		impexGenerator.run();

		assertGenerated(GENERATED_IMPEXES_DIR, new String[] { PRODUCTS_IMPEX_EN_IMPEX });
	}

	@Test
	public void testPartiallyExistingProprtiesInWarnModeForNonRelative() throws Exception
	{
		final TargetPath target = new TargetPath();
		target.setTargetFilePath(new File(rootDir.getFile(), GENERATED_IMPEXES_DIR).getAbsolutePath());
		target.setRelative(false);
		//
		impexGenerator.setTemplateFiles(getAllMatchingTemplates(new File(rootDir.getFile(), "/test-lang/products/")));
		impexGenerator.setTargetPath(target);
		setSourcePath("./" + TEST_CASE_ONE_FOLDER);
		impexGenerator.setVelocityContextConfigurer(removeRowConfigurer);

		impexGenerator.run();

		assertGenerated(GENERATED_IMPEXES_DIR + "/products", new String[] { PRODUCTS_IMPEX_EN_IMPEX });
	}

	@Test
	public void testAllExistingProprtiesInWarnMode() throws Exception
	{
		final TargetPath target = new TargetPath();
		target.setTargetFilePath("./" + GENERATED_IMPEXES_DIR);

		impexGenerator.setTemplateFiles(getAllMatchingTemplates(rootDir.getFile()));
		impexGenerator.setTargetPath(target);
		setSourcePath("./" + TEST_CASE_TWO_FOLDER);
		impexGenerator.setVelocityContextConfigurer(removeRowConfigurer);

		impexGenerator.run();

		assertGenerated(GENERATED_IMPEXES_DIR, new String[] { PRODUCTS_IMPEX_EN_IMPEX, PRODUCTS_IMPEX_PL_IMPEX });
	}

	@Test
	public void testAllExistingProprtiesInWarnModeForNonRelative() throws Exception
	{

		final TargetPath target = new TargetPath();
		target.setTargetFilePath(new File(rootDir.getFile(), GENERATED_IMPEXES_DIR).getAbsolutePath());
		target.setRelative(false);

		impexGenerator.setTemplateFiles(getAllMatchingTemplates(new File(rootDir.getFile(), "/test-lang/products/")));
		impexGenerator.setTargetPath(target);
		setSourcePath("./" + TEST_CASE_TWO_FOLDER);
		impexGenerator.setVelocityContextConfigurer(removeRowConfigurer);

		impexGenerator.run();

		assertGenerated(GENERATED_IMPEXES_DIR + "/products", new String[] { PRODUCTS_IMPEX_EN_IMPEX, PRODUCTS_IMPEX_PL_IMPEX });
	}

	@Test
	public void testAllExistingProprtiesInAbortMode() throws Exception
	{

		final TargetPath target = new TargetPath();
		target.setTargetFilePath("./" + GENERATED_IMPEXES_DIR);
		//
		impexGenerator.setTemplateFiles(getAllMatchingTemplates(rootDir.getFile()));
		impexGenerator.setTargetPath(target);
		setSourcePath("./" + TEST_CASE_TWO_FOLDER);
		impexGenerator.setVelocityContextConfigurer(abortRowConfigurer);

		impexGenerator.run();

		assertGenerated(GENERATED_IMPEXES_DIR, new String[] { PRODUCTS_IMPEX_EN_IMPEX, PRODUCTS_IMPEX_PL_IMPEX });
	}

	@Test
	public void testAllExistingProprtiesInAbortModeForNonRelative() throws Exception
	{
		final TargetPath target = new TargetPath();
		target.setTargetFilePath(new File(rootDir.getFile(), GENERATED_IMPEXES_DIR).getAbsolutePath());
		target.setRelative(false);
		//
		impexGenerator.setTemplateFiles(getAllMatchingTemplates(new File(rootDir.getFile(), "/test-lang/products/")));
		impexGenerator.setTargetPath(target);
		setSourcePath("./" + TEST_CASE_TWO_FOLDER);
		impexGenerator.setVelocityContextConfigurer(abortRowConfigurer);

		impexGenerator.run();

		assertGenerated(GENERATED_IMPEXES_DIR + "/products", new String[] { PRODUCTS_IMPEX_EN_IMPEX, PRODUCTS_IMPEX_PL_IMPEX });
	}

	@Test
	public void testKeyOrderIssue() throws Exception
	{
		final TargetPath target = new TargetPath();
		target.setTargetFilePath(new File(rootDir.getFile(), GENERATED_IMPEXES_DIR).getAbsolutePath());
		target.setRelative(false);
		//
		impexGenerator.setTemplateFiles(getAllMatchingTemplates(rootDir.getFile(), "ordered-categories.*"));
		impexGenerator.setTargetPath(target);
		setSourcePath("./" + TEST_CASE_THREE_FOLDER);
		impexGenerator.setVelocityContextConfigurer(removeRowConfigurer);

		impexGenerator.run();

		assertGenerated(GENERATED_IMPEXES_DIR, new String[] { "ordered-categories_en.impex" });

		final Iterator<String> lines = FileUtils.readLines(
				new File(new File(rootDir.getFile(), GENERATED_IMPEXES_DIR), "ordered-categories_en.impex")).iterator();
		assertHasLine(lines, " ;32;32; ");
		assertHasLine(lines, " ;7S;7S; ");
		assertHasLine(lines, " ;Absinthe;Absinthe; ");
		assertHasLine(lines, " ;Adio;Adio; ");
		assertHasLine(lines, " ;Blowfish;Blowfish; ");
		assertHasLine(lines, " ;Blue Tomato;Blue Tomato; ");
		assertHasLine(lines, " ;Body Glove;Body Glove; ");
		assertHasLine(lines, " ;Coal;Coal; ");
		assertHasLine(lines, " ;Creatures of Leisure;Creatures of Leisure; ");
		assertHasLine(lines, " ;Elan;Elan; ");
		assertHasLine(lines, " ;Horsefeathers;Horsefeathers; ");
		assertHasLine(lines, " ;Lightning Bolt;Lightning Bolt; ");
		assertHasLine(lines, " ;Line;Line; ");
		assertHasLine(lines, " ;Liquid Force;Liquid Force; ");
		assertHasLine(lines, " ;MBM;MBM; ");
		assertHasLine(lines, " ;Matix;Matix; ");
		assertHasLine(lines, " ;Mr. Lacy;Mr. Lacy; ");
		assertHasLine(lines, " ;New Era;New Era; ");
		assertHasLine(lines, " ;New Stormrider Guide;New Stormrider Guide; ");
		assertHasLine(lines, " ;Nike;Nike; ");
		assertHasLine(lines, " ;Nike 6.0;Nike 6.0; ");
		assertHasLine(lines, " ;Nike ACG;Nike ACG; ");
		assertHasLine(lines, " ;Odlo;Odlo; ");
		assertHasLine(lines, " ;Sykum;Sykum; ");
		assertHasLine(lines, " ;Technine;Technine; ");
		assertHasLine(lines, " ;The Hundreds;The Hundreds; ");
		assertHasLine(lines, " ;Vox;Vox; ");
		assertHasLine(lines, " ;Wave-Finder Surf Guide;Wave-Finder Surf Guide; ");
		assertHasLine(lines, " ;WeSC;WeSC; ");
		assertHasLine(lines, " ;adidas;adidas; ");
		assertHasLine(lines, " ;adidas eyewear;adidas eyewear; ");
		assertHasLine(lines, " ;brands;Brands; ");
		assertHasLine(lines, " ;categories;Categories; ");
		assertHasLine(lines, " ;clothes;Clothes; ");
		assertHasLine(lines, " ;goggles;Goggles; ");
		assertHasLine(lines, " ;guides;Guides; ");
		assertHasLine(lines, " ;others;Others; ");
		assertHasLine(lines, " ;skigear;Ski Gear; ");
		assertHasLine(lines, " ;tools;Tools; ");
	}

	private void setSourcePath(final String path)
	{
		BDDMockito.given(source.getPropertyFilePath()).willReturn(path);
		impexGenerator.setSourcePath(source);
	}
}
