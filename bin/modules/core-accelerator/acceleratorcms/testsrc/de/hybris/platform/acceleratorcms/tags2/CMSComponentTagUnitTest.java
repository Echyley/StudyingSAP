/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorcms.tags2;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorcms.data.CmsPageRequestContextData;
import de.hybris.platform.acceleratorservices.util.HtmlElementHelper;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.PageContext;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


@UnitTest
public class CMSComponentTagUnitTest
{
	static final String CSS_CLASS = "ASDF_CLASS";
	static final String DYNAMIC_CSS_CLASS = "DYNAMIC_CLASS";
	static final String ATTR_KEY = "attrkey";
	static final String ATTR_VAL = "attrvalue";
	static final String UID = "ASDF_UID";
	static final String CSS_CLASS_SELECTOR = "class";
	final CMSComponentTag t = new CMSComponentTag();
	final CMSComponentTag spy = Mockito.spy(t);
	final CmsPageRequestContextData contextData = Mockito.mock(CmsPageRequestContextData.class);
	final PageContext pageContext = Mockito.mock(PageContext.class);
	final AbstractCMSComponentModel currentComponent = Mockito.mock(AbstractCMSComponentModel.class);

	@Before
	public void setUp()
	{
		spy.htmlElementHelper = new HtmlElementHelper();
		spy.setPageContext(pageContext);
		spy.currentComponent = currentComponent;
		spy.cmsDynamicAttributeServices = Collections.emptyList();
		Mockito.when(currentComponent.getUid()).thenReturn(UID);
		Mockito.when(pageContext.getAttribute("contentSlot", PageContext.REQUEST_SCOPE)).thenReturn(null);
		Mockito.when(spy.getElementCssClass()).thenReturn(CSS_CLASS);
		spy.currentCmsPageRequestContextData = contextData;
	}

	@Test
	public void testGetElementAttributeWithLiveEdit()
	{
		spy.dynamicAttributes = null;
		Mockito.when(Boolean.valueOf(contextData.isLiveEdit())).thenReturn(Boolean.TRUE);
		Assert.assertEquals(CSS_CLASS, spy.getElementAttributes().get(CSS_CLASS_SELECTOR));
	}

	@Test
	public void testGetElementAttributeWithoutLiveEdit()
	{
		spy.dynamicAttributes = null;
		Mockito.when(Boolean.valueOf(contextData.isLiveEdit())).thenReturn(Boolean.FALSE);
		Assert.assertEquals(CSS_CLASS, spy.getElementAttributes().get(CSS_CLASS_SELECTOR));
	}

	@Test
	public void testGetElementAttributeWithDynamicAttrsNoLiveEdit()
	{
		final HashMap<String, String> attrs = new HashMap<>();
		attrs.put(CSS_CLASS_SELECTOR, DYNAMIC_CSS_CLASS);
		attrs.put(ATTR_KEY, ATTR_VAL);
		spy.dynamicAttributes = attrs;

		Mockito.when(Boolean.valueOf(contextData.isLiveEdit())).thenReturn(Boolean.FALSE);

		final Map<String, String> mergedAttributes = spy.getElementAttributes();
		final List<String> classes = Arrays.asList(mergedAttributes.get(CSS_CLASS_SELECTOR).split(" "));

		Assert.assertTrue(classes.contains(CSS_CLASS));
		Assert.assertTrue(classes.contains(DYNAMIC_CSS_CLASS));
		Assert.assertEquals(ATTR_VAL, mergedAttributes.get(ATTR_KEY));
	}

	@Test
	public void testGetElementAttributeWithDynamicAttrsYesLiveEdit()
	{
		final HashMap<String, String> attrs = new HashMap<>();
		attrs.put(CSS_CLASS_SELECTOR, DYNAMIC_CSS_CLASS);
		attrs.put(ATTR_KEY, ATTR_VAL);
		spy.dynamicAttributes = attrs;

		Mockito.when(Boolean.valueOf(contextData.isLiveEdit())).thenReturn(Boolean.TRUE);

		final Map<String, String> mergedAttributes = spy.getElementAttributes();
		final List<String> classes = Arrays.asList(mergedAttributes.get(CSS_CLASS_SELECTOR).split(" "));

		Assert.assertTrue(classes.contains(CSS_CLASS));
		Assert.assertTrue(classes.contains(DYNAMIC_CSS_CLASS));
		Assert.assertEquals(ATTR_VAL, mergedAttributes.get(ATTR_KEY));
	}
}
