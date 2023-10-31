/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.pcmbackoffice.renderers;

import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.pcmbackoffice.services.ShortcutsService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Div;

import com.hybris.backoffice.enums.BackofficeSpecialCollectionType;
import com.hybris.backoffice.model.BackofficeObjectSpecialCollectionModel;
import com.hybris.cockpitng.core.config.impl.jaxb.gridview.GridView;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.testing.util.CockpitTestUtil;


@RunWith(MockitoJUnitRunner.class)
public class ShortcutsTypeRendererTest
{
	@Spy
	@InjectMocks
	private final ShortcutsTypeRenderer shortcutsTypeRenderer = new ShortcutsTypeRenderer();
	@Mock
	protected ShortcutsService shortcutsService;
	@Mock
	private DataType dataType;
	@Mock
	private ProductModel productModel;
	@Mock
	private WidgetInstanceManager widgetInstanceManager;
	@Mock
	private final Component parentComponent = mock(Div.class);
	@Mock
	private final GridView configuration = mock(GridView.class);

	private static final String BLOCKED_LIST = "blockedlist";
	private static final String QUICK_LIST = "quicklist";
	private final BackofficeObjectSpecialCollectionModel quickLiskCollectionModel = new BackofficeObjectSpecialCollectionModel();
	private final BackofficeObjectSpecialCollectionModel blockedLiskCollectionModel = new BackofficeObjectSpecialCollectionModel();


	@Before
	public void setUp()
	{
		CockpitTestUtil.mockZkEnvironment();
		widgetInstanceManager = CockpitTestUtil.mockWidgetInstanceManager();
		quickLiskCollectionModel.setCollectionType(BackofficeSpecialCollectionType.QUICKLIST);
		blockedLiskCollectionModel.setCollectionType(BackofficeSpecialCollectionType.BLOCKEDLIST);
	}

	@Test
	public void shouldNotRenderAnyIconWhenCollectionContainsNeitherBlockedOrQuickItems()
	{
		// given
		given(shortcutsService.initCollection(BLOCKED_LIST)).willReturn(blockedLiskCollectionModel);
		given(shortcutsService.initCollection(QUICK_LIST)).willReturn(quickLiskCollectionModel);
		given(shortcutsService.collectionContainsItem(productModel, blockedLiskCollectionModel)).willReturn(false);
		given(shortcutsService.collectionContainsItem(productModel, quickLiskCollectionModel)).willReturn(false);

		// when
		shortcutsTypeRenderer.render(parentComponent, configuration, productModel, dataType, widgetInstanceManager);

		// then
		verify(parentComponent, never()).appendChild(any());
		verify(shortcutsTypeRenderer, never()).createIcon(any(), any());
		verify(shortcutsTypeRenderer).fireComponentRendered(parentComponent, configuration, productModel);
	}

	@Test
	public void shouldRenderBlockedListIconWhenBelongsToBlockedList()
	{
		// given
		given(shortcutsService.initCollection(BLOCKED_LIST)).willReturn(blockedLiskCollectionModel);
		given(shortcutsService.initCollection(QUICK_LIST)).willReturn(quickLiskCollectionModel);
		given(shortcutsService.collectionContainsItem(productModel, blockedLiskCollectionModel)).willReturn(true);

		// when
		shortcutsTypeRenderer.render(parentComponent, configuration, productModel, dataType, widgetInstanceManager);

		// then
		verify(shortcutsTypeRenderer).createIcon(eq(BLOCKED_LIST), nullable(String.class));
		verify(shortcutsTypeRenderer).fireComponentRendered(parentComponent, configuration, productModel);
	}

	@Test
	public void shouldRenderQuickListIconWhenBelongsToQuickList()
	{
		// given
		given(shortcutsService.initCollection(BLOCKED_LIST)).willReturn(blockedLiskCollectionModel);
		given(shortcutsService.initCollection(QUICK_LIST)).willReturn(quickLiskCollectionModel);
		given(shortcutsService.collectionContainsItem(productModel, blockedLiskCollectionModel)).willReturn(false);
		given(shortcutsService.collectionContainsItem(productModel, quickLiskCollectionModel)).willReturn(true);

		// when
		shortcutsTypeRenderer.render(parentComponent, configuration, productModel, dataType, widgetInstanceManager);

		// then
		verify(shortcutsTypeRenderer).createIcon(eq(QUICK_LIST), nullable(String.class));
		verify(shortcutsTypeRenderer).fireComponentRendered(parentComponent, configuration, productModel);
	}
}
