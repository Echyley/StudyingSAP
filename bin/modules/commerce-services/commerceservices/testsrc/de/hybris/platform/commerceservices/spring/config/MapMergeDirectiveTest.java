/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.spring.config;

import de.hybris.bootstrap.annotations.UnitTest;

import javax.annotation.Resource;

import java.lang.reflect.Field;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/commerceservices/mergedirective-test-spring.xml")
@UnitTest
public class MapMergeDirectiveTest
{
	@Resource
	private Map<String, String> addToMapMergeBean;

	@Resource
	private MultipleMapMergeBean multipleMapMergeBean;

	@Test
	public void testAddToMap()
	{
		assertEquals(4, addToMapMergeBean.size());
		assertTrue(addToMapMergeBean.containsKey("black"));
		assertEquals("Was expecting \"white\", but got " + addToMapMergeBean.get("black"), "white", addToMapMergeBean.get("black"));
	}

	@Test
	public void testAddToPropertyMap()
	{
		assertEquals(4, multipleMapMergeBean.getPropertyMap().size());
		assertTrue(multipleMapMergeBean.getPropertyMap().containsKey("quark"));
		assertEquals("Was expecting \"antiquark\", but got " + multipleMapMergeBean.getPropertyMap().get("quark"), "antiquark",
				multipleMapMergeBean.getPropertyMap().get("quark"));
	}

	@Test
	public void testAddToFieldMap() throws NoSuchFieldException, IllegalAccessException
	{
		final Field fieldMap = MultipleMapMergeBean.class.getDeclaredField("fieldMap");
		fieldMap.setAccessible(true);
		final Map<String, String> map = (Map<String, String>) fieldMap.get(multipleMapMergeBean);

		assertEquals(4, map.size());
		assertTrue(map.containsKey("seven"));
		assertEquals("Was expecting \"fourteen\", but got " + map.get("seven"), "fourteen", map.get("seven"));
	}
}