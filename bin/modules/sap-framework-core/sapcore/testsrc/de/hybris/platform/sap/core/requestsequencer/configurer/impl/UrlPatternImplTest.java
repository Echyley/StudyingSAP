/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.requestsequencer.configurer.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.Test;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.common.configurer.impl.ConfigurerEntitiesListImpl;



/**
 * Test class for {@link UrlPatternImpl}.
 */
@UnitTest
public class UrlPatternImplTest
{
	private final UrlPatternImpl classUnderTest = new UrlPatternImpl();

	private static String URLPATTERN1 = "/airlines/*";
	private static String URLPATTERN2 = "/connections/*";


	/**
	 * Test testInit method.
	 */
	@Test
	public void testIncludePatternListInit()
	{

		final List<String> includeRegExList = new ArrayList<String>();
		includeRegExList.add(URLPATTERN1);
		includeRegExList.add(URLPATTERN2);

		final ConfigurerEntitiesListImpl<Pattern> includeUrlPatterns = new ConfigurerEntitiesListImpl<Pattern>();
		classUnderTest.setIncludeUrlPatternList(includeUrlPatterns);
		classUnderTest.setIncludeUrlRegExList(includeRegExList);

		classUnderTest.init();

		final ConfigurerEntitiesListImpl<Pattern> urlPatternsTmp = classUnderTest.getIncludeUrlPatternList();

		assertTrue(urlPatternsTmp.getEntities().size() == 2);

		final List<Pattern> entities = urlPatternsTmp.getEntities();

		assertNotNull(entities);

	}

	/**
	 * Test testInit method.
	 */
	@Test
	public void testExcludePatternListInit()
	{

		final List<String> excludeRegExList = new ArrayList<String>();
		excludeRegExList.add(URLPATTERN1);

		classUnderTest.setExcludeUrlRegExList(excludeRegExList);

		final ConfigurerEntitiesListImpl<Pattern> excludeUrlPatterns = new ConfigurerEntitiesListImpl<Pattern>();
		classUnderTest.setExcludeUrlPatternList(excludeUrlPatterns);

		classUnderTest.init();

		final ConfigurerEntitiesListImpl<Pattern> urlPatternsTmp = classUnderTest.getExcludeUrlPatternList();

		assertTrue(urlPatternsTmp.getEntities().size() == 1);

		final List<Pattern> entities = urlPatternsTmp.getEntities();

		assertNotNull(entities);

	}


	/**
	 * Test testInit method.
	 */
	@Test
	public void testIncludeAndExcludePatternListInit()
	{

		final List<String> excludeRegExList = new ArrayList<String>();
		excludeRegExList.add(URLPATTERN1);
		classUnderTest.setExcludeUrlRegExList(excludeRegExList);

		final List<String> includeRegExList = new ArrayList<String>();
		includeRegExList.add(URLPATTERN1);
		includeRegExList.add(URLPATTERN2);
		classUnderTest.setIncludeUrlRegExList(includeRegExList);

		final ConfigurerEntitiesListImpl<Pattern> excludeUrlPatterns = new ConfigurerEntitiesListImpl<Pattern>();
		classUnderTest.setExcludeUrlPatternList(excludeUrlPatterns);

		final ConfigurerEntitiesListImpl<Pattern> includeUrlPatterns = new ConfigurerEntitiesListImpl<Pattern>();
		classUnderTest.setIncludeUrlPatternList(includeUrlPatterns);

		classUnderTest.init();

		final ConfigurerEntitiesListImpl<Pattern> includeUrlPatternsTmp = classUnderTest.getIncludeUrlPatternList();
		assertTrue(includeUrlPatternsTmp.getEntities().size() == 2);
		final List<Pattern> includeEntities = includeUrlPatternsTmp.getEntities();
		assertNotNull(includeEntities);


		final ConfigurerEntitiesListImpl<Pattern> excludeUrlatternsTmp = classUnderTest.getExcludeUrlPatternList();
		assertTrue(excludeUrlatternsTmp.getEntities().size() == 1);
		final List<Pattern> excludeEntities = excludeUrlatternsTmp.getEntities();
		assertNotNull(excludeEntities);

	}
}
