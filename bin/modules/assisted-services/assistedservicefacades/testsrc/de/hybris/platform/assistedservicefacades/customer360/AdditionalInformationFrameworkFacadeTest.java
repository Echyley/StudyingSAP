/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicefacades.customer360;

import static org.mockito.ArgumentMatchers.any;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.assistedservicefacades.customer360.impl.DefaultAdditionalInformationFrameworkFacade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


@UnitTest
public class AdditionalInformationFrameworkFacadeTest
{
	private DefaultAdditionalInformationFrameworkFacade facade;

	private static final String SECTION1_TITLE = "s1t";
	private static final String SECTION1_FRAGMENT1_TITLE = "s1f1t";
	private static final String SECTION1_FRAGMENT2_TITLE = "s1f2t";
	private static final String SECTION2_TITLE = "s2t";
	private static final String SECTION2_FRAGMENT1_TITLE = "s2f1t";

	private final Date data = new Date(); // just data object for Fragment populator
	private final String jspPath = "/jsp/path"; // just jsp path Fragment populator

	@Before
	public void setup()
	{
		facade = new DefaultAdditionalInformationFrameworkFacade();

		final Section section1 = new Section();
		section1.setId("section1");
		section1.setTitle(SECTION1_TITLE);

		final Fragment fragment1 = new Fragment();
		fragment1.setTitle(SECTION1_FRAGMENT1_TITLE);
		fragment1.setId("fragment1");

		final Fragment fragment2 = new Fragment();
		fragment2.setTitle(SECTION1_FRAGMENT2_TITLE);
		fragment2.setId("fragment2");

		final Section section2 = new Section();
		section2.setId("section2");
		section2.setTitle(SECTION2_TITLE);

		final Fragment fragment3 = new Fragment();
		fragment3.setTitle(SECTION2_FRAGMENT1_TITLE);
		fragment3.setId("fragment3");

		section1.setFragments(Arrays.asList(fragment1, fragment2));
		section2.setFragments(Arrays.asList(fragment3));

		facade.setSections(Arrays.asList(section1, section2));

		// only 1 data provider is defined
		final FragmentModelProvider mock = Mockito.mock(FragmentModelProvider.class);
		Mockito.when(mock.getModel(any(Map.class))).thenReturn(data);
		final Map<String, FragmentModelProvider> modelProvidersMap = new HashMap<String, FragmentModelProvider>()
		{
			{
				put("fragment3", mock);
			}
		};
		facade.setModelProvidersMap(modelProvidersMap);

		// only 1 jsp path provider is defined
		final Map<String, String> jspPathes = new HashMap<String, String>()
		{
			{
				put("fragment2", jspPath);
			}
		};
		facade.setJspProvidersMap(jspPathes);
	}

	public void setup2()
	{
		final List<Section> sections = facade.getSections();

		final Section section3 = new Section();
		section3.setId("section3");
		section3.setPriority(Integer.valueOf(10));

		final Section section4 = new Section();
		section4.setId("section4");
		section4.setPriority(Integer.valueOf(5));

		final Fragment fragment4 = new Fragment();
		fragment4.setId("fragment4");
		fragment4.setPriority(Integer.valueOf(10));

		final Fragment fragment5 = new Fragment();
		fragment5.setId("fragment5");
		fragment5.setPriority(Integer.valueOf(5));

		final List<Fragment> fragments = new ArrayList<>(sections.get(0).getFragments());
		fragments.addAll(Arrays.asList(fragment4, fragment5)); // to section 1
		sections.get(0).setFragments(fragments);

		sections.addAll(Arrays.asList(section3, section4));
		facade.setSections(sections);
	}

	@Test
	public void shouldGetSectionById()
	{
		final Section section1 = facade.getSection("section1");

		Assert.assertEquals(SECTION1_TITLE, section1.getTitle());
		Assert.assertEquals(2, section1.getFragments().size());

		final Section section2 = facade.getSection("section2");

		Assert.assertEquals(SECTION2_TITLE, section2.getTitle());
		Assert.assertEquals(1, section2.getFragments().size());
	}

	@Test
	public void shouldGetFragments()
	{
		final HashMap<String, String> props = new HashMap();
		final Fragment fragment1 = facade.getFragment("section1", "fragment1", props);
		Assert.assertEquals(SECTION1_FRAGMENT1_TITLE, fragment1.getTitle());
		Assert.assertNull(fragment1.getData());

		final Fragment fragment3 = facade.getFragment("section2", "fragment3", props);
		Assert.assertEquals(SECTION2_FRAGMENT1_TITLE, fragment3.getTitle());
		Assert.assertEquals(fragment3.getData(), data);

		final Fragment fragment2 = facade.getFragment("section1", "fragment2", props);
		Assert.assertEquals(SECTION1_FRAGMENT2_TITLE, fragment2.getTitle());
		Assert.assertEquals(jspPath, fragment2.getJspPath());
		Assert.assertNull(fragment2.getData());
	}

	@Test
	public void shouldNotMixFragmentsOrSessionsWithoutPriority()
	{
		final List<Section> sections = facade.getSections();
		Assert.assertEquals(SECTION1_TITLE, sections.get(0).getTitle());
		Assert.assertEquals(SECTION2_TITLE, sections.get(1).getTitle());

		Assert.assertEquals(SECTION1_FRAGMENT1_TITLE, sections.get(0).getFragments().get(0).getTitle());
		Assert.assertEquals(SECTION1_FRAGMENT2_TITLE, sections.get(0).getFragments().get(1).getTitle());
		Assert.assertEquals(SECTION2_FRAGMENT1_TITLE, sections.get(1).getFragments().get(0).getTitle());
	}

	@Test
	public void shouldMixFragmentsOrSessionsWithPriority()
	{
		setup2();

		final List<Section> sections = facade.getSections();
		Assert.assertEquals("section with lower prio should be first", "section4", sections.get(0).getId());
		Assert.assertEquals("section with high prio should be second", "section3", sections.get(1).getId());
		Assert.assertEquals("section with no prio should be have same order", "section1", sections.get(2).getId());
		Assert.assertEquals("section with no prio should be have same order", "section2", sections.get(3).getId());

		Assert.assertEquals("fragment5 with 5 prio should be 1st", "fragment5", sections.get(2).getFragments().get(0).getId());
		Assert.assertEquals("fragment4 with 10 prio should be 2nd", "fragment4", sections.get(2).getFragments().get(1).getId());
		Assert.assertEquals("fragment with no prio should be have same order", "fragment1", sections.get(2).getFragments().get(2).getId());
		Assert.assertEquals("fragment with no prio should be have same order", "fragment2", sections.get(2).getFragments().get(3).getId());
	}
}