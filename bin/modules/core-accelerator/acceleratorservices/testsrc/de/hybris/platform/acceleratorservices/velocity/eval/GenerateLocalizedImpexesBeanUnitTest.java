/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.velocity.eval;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.velocity.VelocityExecutor;
import de.hybris.platform.acceleratorservices.velocity.ant.SourcePath;
import de.hybris.platform.acceleratorservices.velocity.ant.TargetPath;
import de.hybris.platform.acceleratorservices.velocity.core.CoreVelocityConfigurer;
import de.hybris.platform.acceleratorservices.velocity.locale.LocaleResolver;
import de.hybris.platform.acceleratorservices.velocity.resource.url.ScriptUrlContext;
import de.hybris.platform.acceleratorservices.velocity.resource.url.ScriptUrlContextFactory;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.BDDMockito;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.internal.InOrderImpl;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class GenerateLocalizedImpexesBeanUnitTest
{

	@Mock
	private SourcePath source;

	@Mock
	private TargetPath target;

	@Mock
	private LocaleResolver localeResolver;

	@Mock
	private VelocityExecutor velocityExecutor;


	@Mock
	private ScriptUrlContextFactory<SourcePath, TargetPath> scriptUrlContextFactory;


	@Mock
	private CoreVelocityConfigurer<VelocityContext, ScriptUrlContext> velocityContextConfigurer;

	@Spy
	private final Map<String, List<String>> templateFiles = new LinkedHashMap<>();

	@Spy
	@InjectMocks
	private final Runnable bean = new GenerateLocalizedImpexesBean();

	@Before
	public void prepare()
	{
		BDDMockito.given(localeResolver.getAllLocales()).willReturn(Collections.singletonList(Locale.FRENCH));
		Mockito.doReturn(velocityExecutor).when((GenerateLocalizedImpexesBean) bean).getVelocityExecutor();

		templateFiles.put("one", Arrays.asList("ten", "eleven"));
		templateFiles.put("two", Arrays.asList("twenty", "twentyone"));
	}

	@Test
	public void testOrderCall()
	{
		final ScriptUrlContext urlCtx1 = Mockito.mock(ScriptUrlContext.class);
		final ScriptUrlContext urlCtx2 = Mockito.mock(ScriptUrlContext.class);
		final ScriptUrlContext urlCtx3 = Mockito.mock(ScriptUrlContext.class);
		final ScriptUrlContext urlCtx4 = Mockito.mock(ScriptUrlContext.class);


		final VelocityContext velocityCtx1 = Mockito.mock(VelocityContext.class);
		final VelocityContext velocityCtx2 = Mockito.mock(VelocityContext.class);
		final VelocityContext velocityCtx3 = Mockito.mock(VelocityContext.class);
		final VelocityContext velocityCtx4 = Mockito.mock(VelocityContext.class);

		BDDMockito.given(
				scriptUrlContextFactory.create(Mockito.eq(Locale.FRENCH), Mockito.any(File.class), Mockito.eq(source),
						Mockito.eq(target))).willReturn(urlCtx1, urlCtx2, urlCtx3, urlCtx4);

		BDDMockito.given(velocityContextConfigurer.configure(urlCtx1)).willReturn(velocityCtx1);
		BDDMockito.given(velocityContextConfigurer.configure(urlCtx2)).willReturn(velocityCtx2);
		BDDMockito.given(velocityContextConfigurer.configure(urlCtx3)).willReturn(velocityCtx3);
		BDDMockito.given(velocityContextConfigurer.configure(urlCtx4)).willReturn(velocityCtx4);

		final InOrder orderVerify = new InOrderImpl(Arrays.asList(localeResolver, templateFiles, scriptUrlContextFactory,
			velocityContextConfigurer, velocityExecutor, velocityCtx1, velocityCtx2, velocityCtx3, velocityCtx4, urlCtx1,
			urlCtx2, urlCtx3, urlCtx4));

		bean.run();

		orderVerify.verify(localeResolver).getAllLocales();
		orderVerify.verify(templateFiles).keySet();
		orderVerify.verify(templateFiles).get("one");

		orderVerify.verify(scriptUrlContextFactory).create(Mockito.eq(Locale.FRENCH),
				Mockito.argThat(new FileMatcher("one", "ten")), Mockito.eq(source), Mockito.eq(target));
		orderVerify.verify(velocityContextConfigurer).configure(urlCtx1);
		orderVerify.verify(velocityExecutor).execute(urlCtx1, velocityCtx1);

		orderVerify.verify(scriptUrlContextFactory).create(Mockito.eq(Locale.FRENCH),
				Mockito.argThat(new FileMatcher("one", "eleven")), Mockito.eq(source), Mockito.eq(target));
		orderVerify.verify(velocityContextConfigurer).configure(urlCtx2);
		orderVerify.verify(velocityExecutor).execute(urlCtx2, velocityCtx2);

		orderVerify.verify(templateFiles).get("two");

		orderVerify.verify(scriptUrlContextFactory).create(Mockito.eq(Locale.FRENCH),
				Mockito.argThat(new FileMatcher("two", "twenty")), Mockito.eq(source), Mockito.eq(target));
		orderVerify.verify(velocityContextConfigurer).configure(urlCtx3);
		orderVerify.verify(velocityExecutor).execute(urlCtx3, velocityCtx3);

		orderVerify.verify(scriptUrlContextFactory).create(Mockito.eq(Locale.FRENCH),
				Mockito.argThat(new FileMatcher("two", "twentyone")), Mockito.eq(source), Mockito.eq(target));
		orderVerify.verify(velocityContextConfigurer).configure(urlCtx4);
		orderVerify.verify(velocityExecutor).execute(urlCtx4, velocityCtx4);
	}

	class FileMatcher implements ArgumentMatcher<File>
	{
		private final String parent;
		private final String ownname;

		FileMatcher(final String parent, final String name)
		{
			this.parent = parent;
			this.ownname = name;
		}

		@Override
		public boolean matches(final File file)
		{
			return parent.equals(file.getParent()) && ownname.equals(file.getName());
		}

	}
}
