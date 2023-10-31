/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.pcmbackoffice.actions.removelist;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.pcmbackoffice.services.ShortcutsService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import com.hybris.backoffice.enums.BackofficeSpecialCollectionType;
import com.hybris.backoffice.model.BackofficeObjectSpecialCollectionModel;
import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.actions.ActionResult;
import com.hybris.cockpitng.testing.AbstractActionUnitTest;
import com.hybris.cockpitng.util.notifications.NotificationService;
import com.hybris.cockpitng.util.notifications.event.NotificationEvent;
import com.hybris.cockpitng.util.notifications.event.NotificationEventTypes;
import com.hybris.pcmbackoffice.widgets.shortcuts.ShortcutsUtilService;


public class RemoveBlockedListActionTest extends AbstractActionUnitTest<RemoveBlockedListAction>
{
	@InjectMocks
	@Spy
	private RemoveBlockedListAction removeBlockedListAction;

	@Mock
	private NotificationService notificationService;
	@Mock
	private ShortcutsService shortcutsService;
	@Mock
	private ShortcutsUtilService shortcutsUtilService;
	@Mock
	private List<ItemModel> listItems;
	@Mock
	private ActionContext<Object> actionContext;
	@Mock
	private BackofficeObjectSpecialCollectionModel collectionModel;

	private static final String collectionCode = BackofficeSpecialCollectionType.BLOCKEDLIST.getCode();
	private static final String NOTIFICATION_SOURCE_SHORTCUTS_BLOCKED_LIST_REMOVE_SUCCESS = "shortcutsBlockedListProductRemoveSuccess";

	@Override
	public RemoveBlockedListAction getActionInstance()
	{
		return removeBlockedListAction;
	}

	@Before
	public void setup()
	{
		final ItemModel item1 = new ItemModel();
		final ItemModel item2 = new ItemModel();
		final ItemModel item3 = new ItemModel();
		this.listItems = Arrays.asList(item1, item2, item3);
	}

	@Test
	public void shouldNotPerformRemoveWhenGetNullObject()
	{
		// given
		given(actionContext.getData()).willReturn(null);

		// when
		final boolean result = removeBlockedListAction.canPerform(actionContext);

		// then
		assertThat(result).isFalse();
	}

	@Test
	public void shouldNotPerformRemoveWhenCollectionIsEmpty()
	{
		// given
		given(actionContext.getData()).willReturn(new ArrayList());

		// when
		final boolean result = removeBlockedListAction.canPerform(actionContext);

		// then
		assertThat(result).isFalse();
	}

	@Test
	public void shouldPerformRemoveWhenGetNotNullItemList()
	{
		// given
		given(actionContext.getData()).willReturn(listItems);

		// when
		final boolean result = removeBlockedListAction.canPerform(actionContext);

		// then
		assertThat(result).isTrue();
	}

	@Test
	public void shouldNotPerformRemoveWhenItemAlreadyRemoved()
	{
		// given
		given(actionContext.getData()).willReturn(listItems);
		given(shortcutsService.initCollection(collectionCode)).willReturn(collectionModel);
		given(shortcutsUtilService.isItemsAlreadyDeleted(listItems)).willReturn(true);

		// when
		final ActionResult result = removeBlockedListAction.perform(actionContext);

		// then
		assertThat(result.getResultCode()).isSameAs(ActionResult.ERROR);
	}

	@Test
	public void shouldRemoveSuccessWhenItemsAlreadyExist()
	{
		// given
		given(actionContext.getData()).willReturn(listItems);
		given(shortcutsService.initCollection(collectionCode)).willReturn(collectionModel);
		given(shortcutsUtilService.isItemsAlreadyDeleted(listItems)).willReturn(false);

		// when
		final ActionResult result = removeBlockedListAction.perform(actionContext);

		// then
		assertThat(result.getResultCode()).isSameAs(ActionResult.SUCCESS);
		verify(notificationService).notifyUser(NOTIFICATION_SOURCE_SHORTCUTS_BLOCKED_LIST_REMOVE_SUCCESS,
				NotificationEventTypes.EVENT_TYPE_GENERAL, NotificationEvent.Level.SUCCESS);
	}
}
