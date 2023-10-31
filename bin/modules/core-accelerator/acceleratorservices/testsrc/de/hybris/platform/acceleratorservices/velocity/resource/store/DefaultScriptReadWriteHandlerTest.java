/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.velocity.resource.store;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.velocity.BaseFileSandBoxTest;
import de.hybris.platform.acceleratorservices.velocity.resource.url.ScriptUrlContext;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * Test for the feature of lazy target file/script creation unless all referenced properties from script can be
 * processed
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/acceleratorservices/test/impexgenerator/test-generate-impex-spring.xml"})
@UnitTest
@Ignore
public class DefaultScriptReadWriteHandlerTest extends BaseFileSandBoxTest
{
	@Value("classpath:acceleratorservices/test/impexgenerator")
	private Resource rootDir;

	private final String sandBoxDir = "sndBox";

	@Mock
	private ScriptUrlContext ctx;

	private ScriptReadWriteHandler handler;


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
		Assert.assertTrue("should create a sand box dir ", new File(getRootDir().getFile(), sandBoxDir).mkdir());
		MockitoAnnotations.initMocks(this);
		handler = new DefaultScriptReadWriteHandler();


	}



	@Test
	public void testPrepareTargetWriterForNestedNotExistingDirs() throws IOException
	{
		BDDMockito.given(ctx.getTargetScriptEffectivePath()).willReturn(
				sandBoxDir + "/some-dir/other-dir/and one more/test-file.txt");

		Writer writer = null;
		try
		{
			writer = handler.prepareTemporaryTargetWriter(ctx);

			assureFileNotExists(ctx);

			writer.append("some blah");

			assureFileNotExists(ctx);

			writer.append("some other blah");

			assureFileNotExists(ctx);

			handler.confirmTarget(ctx);

			assureFileExists(ctx); //ok
		}
		finally
		{
			IOUtils.closeQuietly(writer);
		}
	}

	@Test
	public void testLazyFileCreation() throws IOException
	{
		BDDMockito.given(ctx.getTargetScriptEffectivePath()).willReturn(sandBoxDir + "/test-file.txt");
		Writer writer = null;
		try
		{
			writer = handler.prepareTemporaryTargetWriter(ctx);

			assureFileNotExists(ctx);

			writer.append("some blah");

			assureFileNotExists(ctx);

			writer.append("some other blah");

			assureFileNotExists(ctx);

			handler.confirmTarget(ctx);

			assureFileExists(ctx); //ok
		}
		finally
		{
			IOUtils.closeQuietly(writer);
		}
	}


	private void assureFileNotExists(final ScriptUrlContext urlContext)
	{
		final String fileFullPath = urlContext.getTargetScriptEffectivePath();
		final File targetfile = new File(fileFullPath);
		Assert.assertTrue(!targetfile.exists());
	}

	private void assureFileExists(final ScriptUrlContext urlContext)
	{
		final String fileFullPath = urlContext.getTargetScriptEffectivePath();
		final File targetfile = new File(fileFullPath);
		Assert.assertTrue(targetfile.exists() && targetfile.isFile() && targetfile.canRead());
	}
}
