/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.widgets.actions.locking;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.locking.ItemLockingService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.testing.AbstractCockpitngUnitTest;
import com.hybris.cockpitng.testing.annotation.ExtensibleWidget;

import jersey.repackaged.com.google.common.collect.Lists;


@RunWith(MockitoJUnitRunner.class)
@ExtensibleWidget(level = ExtensibleWidget.ALL)
public class ToggleItemLockActionRendererTest extends AbstractCockpitngUnitTest<ToggleItemLockActionRenderer>
{
	private static final String ICON_ACTION_UNLOCK_ITEM_DEFAULT_FONT_NAME = "unlocked";
	private static final String ICON_ACTION_LOCK_ITEM_DEFAULT_FONT_NAME = "locked";

	@Spy
	@InjectMocks
	private ToggleItemLockActionRendererStub renderer;

	@Mock
	private ItemLockingService itemLockingService;

	@Mock
	private ItemModel lockedModel;

	@Mock
	private ItemModel unlockedModel;

	@Before
	public void setUp()
	{
		when(itemLockingService.isLocked(lockedModel)).thenReturn(true);
		when(itemLockingService.isLocked(unlockedModel)).thenReturn(false);
	}

	@Test
	public void isLocked()
	{
		assertThat(renderer.isLocked(null)).isFalse();
		assertThat(renderer.isLocked(lockedModel)).isTrue();
		assertThat(renderer.isLocked(unlockedModel)).isFalse();
		assertThat(renderer.isLocked(Lists.newArrayList(lockedModel, unlockedModel))).isFalse();
		assertThat(renderer.isLocked(Lists.newArrayList(unlockedModel, unlockedModel))).isFalse();
		assertThat(renderer.isLocked(Lists.newArrayList(lockedModel, lockedModel))).isTrue();
		assertThat(renderer.isLocked(new Object())).isFalse();
	}

	@Test
	public void getLockedIconUriShouldReturnRelativePathToLockedIcon()
	{
		//given
		final ActionContext<Object> context = mock(ActionContext.class);
		doReturn(true).when(renderer).isLocked(any());

		//when
		final String uri = renderer.getIconUri(context, true);

		//then
		assertThat(uri).isEqualTo(ICON_ACTION_LOCK_ITEM_DEFAULT_FONT_NAME);
	}

	@Test
	public void getLockedIconUriShouldReturnRelativePathToUnlockedIcon()
	{
		//given
		final ActionContext<Object> context = mock(ActionContext.class);
		doReturn(false).when(renderer).isLocked(any());

		//when
		final String uri = renderer.getIconUri(context, true);

		//then
		assertThat(uri).isEqualTo(ICON_ACTION_UNLOCK_ITEM_DEFAULT_FONT_NAME);
	}

	public static class ToggleItemLockActionRendererStub extends ToggleItemLockActionRenderer
	{
		@Override
		protected String adjustUri(final ActionContext<Object> context, final String uri)
		{
			return super.adjustUri(context, uri);
		}
	}

}
