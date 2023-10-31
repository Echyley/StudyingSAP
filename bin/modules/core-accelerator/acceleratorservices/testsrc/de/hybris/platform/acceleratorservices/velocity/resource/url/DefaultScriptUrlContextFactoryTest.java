/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.velocity.resource.url;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.velocity.ant.SourcePath;
import de.hybris.platform.acceleratorservices.velocity.ant.TargetPath;

import java.io.File;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


/**
 *
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultScriptUrlContextFactoryTest
{
	@Mock
	private File scriptFile;

	@Mock
	private SourcePath sourcePath;

	@Mock
	private TargetPath targetPath;

	private final Locale locale = Locale.CANADA;

	private final DefaultScriptUrlContextFactory factory = new DefaultScriptUrlContextFactory();

	@Test
	public void testFactoryWhenTargetRelative()
	{
		BDDMockito.given(Boolean.valueOf(targetPath.isRelative())).willReturn(Boolean.TRUE);

		final ScriptUrlContext ctx = factory.create(locale, scriptFile, sourcePath, targetPath);

		Assert.assertTrue(ctx instanceof DefaultScriptUrlContext);
	}


	@Test
	public void testFactoryWhenTargetAbsolute()
	{
		BDDMockito.given(Boolean.valueOf(targetPath.isRelative())).willReturn(Boolean.FALSE);

		final ScriptUrlContext ctx = factory.create(locale, scriptFile, sourcePath, targetPath);

		Assert.assertTrue(ctx instanceof MirroredStructureScriptUrlContext);
	}
}
