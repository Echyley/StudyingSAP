/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.pcmbackoffice.widgets.shortcuts;

import static com.hybris.pcmbackoffice.widgets.shortcuts.ShortcutsController.SOCKET_IN_REFRESH;
import static com.hybris.pcmbackoffice.widgets.shortcuts.ShortcutsController.SOCKET_IN_RESET;
import static com.hybris.pcmbackoffice.widgets.shortcuts.ShortcutsController.SOCKET_IN_SEARCH_INIT;
import static com.hybris.pcmbackoffice.widgets.shortcuts.ShortcutsController.SOCKET_IN_UPDATE_CONTEXT;
import static com.hybris.pcmbackoffice.widgets.shortcuts.ShortcutsController.SOCKET_OUT_INIT_SEARCH;
import static com.hybris.pcmbackoffice.widgets.shortcuts.ShortcutsController.SOCKET_OUT_RESET_FULL_TEXT_SEARCH;
import static com.hybris.pcmbackoffice.widgets.shortcuts.ShortcutsController.SOCKET_OUT_UPDATE_ASSORTMENT_FULL_TEXT_SEARCH;
import static com.hybris.pcmbackoffice.widgets.shortcuts.ShortcutsController.SOCKET_OUT_UPDATE_FULL_TEXT_SEARCH;
import static com.hybris.pcmbackoffice.widgets.shortcuts.ShortcutsController.SOCKET_IN_REFRESH_ASSORTMENT_FULL_TEXT_SEARCH;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.pcmbackoffice.services.ShortcutsService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Listbox;

import com.hybris.backoffice.enums.BackofficeSpecialCollectionType;
import com.hybris.backoffice.model.BackofficeObjectSpecialCollectionModel;
import com.hybris.backoffice.widgets.advancedsearch.AdvancedSearchOperatorService;
import com.hybris.backoffice.widgets.advancedsearch.impl.AdvancedSearchInitContext;
import com.hybris.backoffice.widgets.advancedsearch.impl.SearchConditionData;
import com.hybris.cockpitng.core.events.CockpitEvent;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectCRUDHandler;
import com.hybris.cockpitng.dataaccess.facades.type.TypeFacade;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.search.data.ValueComparisonOperator;
import com.hybris.cockpitng.testing.AbstractWidgetUnitTest;
import com.hybris.cockpitng.testing.annotation.DeclaredGlobalCockpitEvent;
import com.hybris.cockpitng.testing.annotation.DeclaredInput;
import com.hybris.cockpitng.testing.util.CockpitTestUtil;


@DeclaredInput(value = SOCKET_IN_SEARCH_INIT, socketType = String.class)
@DeclaredInput(SOCKET_IN_REFRESH)
@DeclaredInput(value = SOCKET_IN_UPDATE_CONTEXT, socketType = AdvancedSearchInitContext.class)
@DeclaredInput(SOCKET_IN_RESET)
@DeclaredInput(SOCKET_IN_REFRESH_ASSORTMENT_FULL_TEXT_SEARCH)
@DeclaredGlobalCockpitEvent(eventName = ObjectCRUDHandler.OBJECTS_DELETED_EVENT, scope = CockpitEvent.SESSION)

public class ShortcutsControllerTest extends AbstractWidgetUnitTest<ShortcutsController>
{
	@Spy
	@InjectMocks
	private ShortcutsController shortcutsController;
	@Mock
	private ShortcutsService shortcutsService;
	@Mock
	private ShortcutsUtilService shortcutsUtilService;
	@Mock
	private AdvancedSearchOperatorService advancedSearchOperatorService;
	@Mock
	private Listbox shortcuts;
	@Mock
	private Component component;
	@Mock
	private WidgetInstanceManager widgetInstanceManager;
	@Mock
	private TypeFacade typeFacade;
	@Mock
	private List<Shortcutsitem> shortcutsList;
	@Mock
	private AdvancedSearchInitContext context;
	@Mock
	private Shortcutsitem shortcutsitem;


	private final BackofficeObjectSpecialCollectionModel blockedListCollectionModel = new BackofficeObjectSpecialCollectionModel();
	private final BackofficeObjectSpecialCollectionModel quickListCollectionModel = new BackofficeObjectSpecialCollectionModel();
	private static final String typeCode = "testCode";
	private final CockpitEvent event = mock(CockpitEvent.class);

	@Override
	protected ShortcutsController getWidgetController()
	{
		return shortcutsController;
	}

	@Before
	public void setUp()
	{
		//given
		CockpitTestUtil.mockZkEnvironment();
		widgetInstanceManager = CockpitTestUtil.mockWidgetInstanceManager();
		final Component component = mock(Component.class);
		when(shortcutsService.initCollection(any())).thenReturn(mock(BackofficeObjectSpecialCollectionModel.class));
		when(shortcutsUtilService.getDroppables()).thenReturn("");

		//when
		shortcutsController.initialize(component);
		blockedListCollectionModel.setCollectionType(BackofficeSpecialCollectionType.BLOCKEDLIST);
		quickListCollectionModel.setCollectionType(BackofficeSpecialCollectionType.QUICKLIST);

	}

	@Test
	public void shouldSendOutputUpdateAssortmentFullTextSearchWhenRefreshWithBlockedListItem()
	{
		// given

		final List<PK> pkList = new ArrayList<>();
		pkList.add(PK.createCounterPK(1));
		given(shortcutsService.initCollection(ShortcutsFlagEnum.BLOCKED_LIST.getCode())).willReturn(blockedListCollectionModel);
		given(shortcutsService.getAllCollectionList(blockedListCollectionModel)).willReturn(pkList);

		// when

		shortcutsController.refresh(new Object());

		// then
		assertSocketOutput(SOCKET_OUT_UPDATE_FULL_TEXT_SEARCH, new ArrayList());
	}

	@Test
	public void shouldInitSearchWithInputCodeWhenInitSearch()
	{
		// when

		shortcutsController.initializeFullTextSearch(typeCode);

		// then
		assertSocketOutput(SOCKET_OUT_INIT_SEARCH, (Predicate<String>)(final String code) -> code == typeCode);
	}

	@Test
	public void shouldSendOutputUpdateAssortmentFullTextSearchWhenInitSearchWithBlockedListItem()
	{
		// given

		final List<PK> pkList = new ArrayList<>();
		pkList.add(PK.createCounterPK(1));
		given(shortcutsService.initCollection(ShortcutsFlagEnum.BLOCKED_LIST.getCode())).willReturn(blockedListCollectionModel);
		given(shortcutsService.getAllCollectionList(blockedListCollectionModel)).willReturn(pkList);

		// when

		shortcutsController.initializeFullTextSearch(typeCode);

		// then
		assertSocketOutput(SOCKET_OUT_UPDATE_FULL_TEXT_SEARCH, new ArrayList());
	}

	@Test
	public void shouldSendOutResetFullTextSearchWithConditionsWhenUpdateContextWithBlockedListIsNotEmpty()
	{
		// given

		final List<PK> pkList = new ArrayList<>();
		final List<SearchConditionData> conditions = Arrays.asList(mock(SearchConditionData.class));
		pkList.add(PK.createCounterPK(1));
		context = mock(AdvancedSearchInitContext.class);
		final Map<String, Object> finalData = new HashMap<>();
		given(shortcutsService.initCollection(ShortcutsFlagEnum.BLOCKED_LIST.getCode())).willReturn(blockedListCollectionModel);
		given(shortcutsService.getAllCollectionList(blockedListCollectionModel)).willReturn(pkList);
		given(shortcutsUtilService.getConditions(ValueComparisonOperator.NOT_IN, pkList)).willReturn(conditions);
		finalData.put("conditions", conditions);
		finalData.put("context", context);

		// when

		shortcutsController.updateContext(context);

		// then
		assertSocketOutput(SOCKET_OUT_RESET_FULL_TEXT_SEARCH, (Predicate<Map<String, Object>>)(final Map<String, Object> data) -> data.equals(finalData));
	}

	@Test
	public void shouldSendOutResetFullTextSearchWithoutConditionsWhenUpdateContextWithBlockedListIsEmpty()
	{
		// given

		context = mock(AdvancedSearchInitContext.class);
		final Map<String, Object> finalData = new HashMap<>();
		given(shortcutsService.initCollection(ShortcutsFlagEnum.BLOCKED_LIST.getCode())).willReturn(blockedListCollectionModel);
		given(shortcutsService.getAllCollectionList(blockedListCollectionModel)).willReturn(new ArrayList<>());
		given(shortcutsUtilService.getConditions(ValueComparisonOperator.NOT_IN, new ArrayList<>())).willReturn(null);
		finalData.put("conditions", null);
		finalData.put("context", context);

		// when

		shortcutsController.updateContext(context);

		// then
		assertSocketOutput(SOCKET_OUT_RESET_FULL_TEXT_SEARCH, (Predicate<Map<String, Object>>)(final Map<String, Object> data) -> data.equals(finalData));
	}
	
	@Test
	public void shouldSendOutputUpdateAssortmentFullTextSearchWhenHandleDeleteEventWithBlockedListItem()
	{
		// given

		final List<PK> pkList = new ArrayList<>();
		pkList.add(PK.createCounterPK(1));
		given(shortcutsService.initCollection(ShortcutsFlagEnum.BLOCKED_LIST.getCode())).willReturn(blockedListCollectionModel);
		given(shortcutsService.getAllCollectionList(blockedListCollectionModel)).willReturn(pkList);
		final Collection<Object> deletedObjects = new ArrayList<>();
		deletedObjects.add(mock(ProductModel.class));
		given(event.getDataAsCollection()).willReturn(deletedObjects);


		// when
		shortcutsController.handleObjectDeleteEvent(event);

		// then
		assertSocketOutput(SOCKET_OUT_UPDATE_FULL_TEXT_SEARCH, new ArrayList());
	}

	@Test
	public void shouldUpdateAssortmentViewFullTextSearchWhenRefresh()
	{
		// given
		final List<PK> blockedList = new ArrayList<>();
		final List<SearchConditionData> conditions = Arrays.asList(mock(SearchConditionData.class));
		given(shortcutsService.initCollection(ShortcutsFlagEnum.BLOCKED_LIST.getCode())).willReturn(blockedListCollectionModel);
		given(shortcutsService.getAllCollectionList(blockedListCollectionModel)).willReturn(blockedList);
		given(shortcutsUtilService.getConditions(ValueComparisonOperator.NOT_IN, blockedList)).willReturn(conditions);

		// when
		shortcutsController.refreshAssortmentViewFullTextSearch();

		// then
		assertSocketOutput(SOCKET_OUT_UPDATE_ASSORTMENT_FULL_TEXT_SEARCH, conditions);
	}

	@Test
	public void shouldSendOutputWithPKIsEmptyConditionWhenAllBlockItemsDeleted()
	{
		// given
		final SearchConditionData condition = mock(SearchConditionData.class);

		shortcutsController.setCurrentLabel(ShortcutsFlagEnum.BLOCKED_LIST);
		given(shortcutsService.initCollection(ShortcutsFlagEnum.BLOCKED_LIST.getCode())).willReturn(blockedListCollectionModel);
		given(shortcutsService.getAllCollectionList(blockedListCollectionModel)).willReturn(Collections.emptyList());
		given(shortcutsUtilService.getPKEmptyCondition()).willReturn(condition);
		given(event.getDataAsCollection()).willReturn(Arrays.asList(mock(ProductModel.class)));


		// when
		shortcutsController.handleObjectDeleteEvent(event);

		// then
		assertSocketOutput(SOCKET_OUT_UPDATE_FULL_TEXT_SEARCH, (Predicate<List<SearchConditionData>>)(final List<SearchConditionData> conditions) -> conditions.size() == 1 && conditions.get(0).equals(condition));
	}
}
