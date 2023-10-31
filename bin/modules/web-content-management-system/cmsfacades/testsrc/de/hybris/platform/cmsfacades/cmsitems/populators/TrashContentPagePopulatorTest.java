/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.cmsitems.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.enums.CmsPageStatus;
import de.hybris.platform.cms2.model.pages.ContentPageModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.junit.MockitoRule;

import static org.mockito.Mockito.*;


@UnitTest
@RunWith(Parameterized.class)
public class TrashContentPagePopulatorTest
{
	private Map<String, Object> source;

	@Mock
	private ContentPageModel contentPageModel;

	@InjectMocks
	private TrashContentPagePopulator trashContentPagePopulator;

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule().silent();


	@Before
	public void setUp()
	{
		source = new HashMap<>();
	}

	@Parameters
	public static Collection<Object[]> data()
	{
		return Arrays.asList(new Object[][] {
				{ false, true, false }, { false, false, false }, { true, false, false }, { true, true, true }
		});
	}

	@Parameter
	public boolean isHomePage;

	@Parameter(1)
	public boolean isPageTrashed;

	@Parameter(2)
	public boolean isSetHomePage;

	@Test
	public void getOnlyWhenProvidedPageIsHomePageAndIsTrashedWillSetHomePage()
	{
		// GIVEN
		isHomePage(isHomePage);
		isPageTrashed(isPageTrashed);

		// WHEN
		trashContentPagePopulator.populate(source, contentPageModel);

		// THEN
		if (isSetHomePage) {
			verify(contentPageModel, times(1)).setHomepage(false);
		} else {
			verify(contentPageModel, never()).setHomepage(anyBoolean());
		}

	}

	protected void isHomePage(final boolean isHomePage)
	{
		when(contentPageModel.isHomepage()).thenReturn(isHomePage);
	}

	protected void isPageTrashed(final boolean isPageTrashed)
	{
		when(contentPageModel.getPageStatus()).thenReturn((isPageTrashed) ? CmsPageStatus.DELETED : CmsPageStatus.ACTIVE);
	}
}
