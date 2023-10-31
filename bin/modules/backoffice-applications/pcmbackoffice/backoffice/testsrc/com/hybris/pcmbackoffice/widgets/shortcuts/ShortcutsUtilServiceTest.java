/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.pcmbackoffice.widgets.shortcuts;

import com.hybris.backoffice.model.BackofficeObjectSpecialCollectionModel;
import com.hybris.backoffice.widgets.advancedsearch.AdvancedSearchOperatorService;
import com.hybris.backoffice.widgets.advancedsearch.impl.SearchConditionData;
import com.hybris.cockpitng.dataaccess.facades.type.TypeFacade;
import com.hybris.cockpitng.dnd.DragAndDropStrategy;
import com.hybris.cockpitng.dnd.SelectionSupplier;
import com.hybris.cockpitng.labels.LabelService;
import com.hybris.cockpitng.search.data.ValueComparisonOperator;
import com.hybris.cockpitng.util.notifications.NotificationService;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.pcmbackoffice.services.ShortcutsService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.zkoss.zk.ui.event.DropEvent;
import org.zkoss.zul.Div;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class ShortcutsUtilServiceTest
{

	@Mock
	private ModelService modelService;
	@Mock
	private NotificationService notificationService;
	@Mock
	private AdvancedSearchOperatorService advancedSearchOperatorService;
	@Mock
	private TypeFacade typeFacade;
	@Mock
	private ShortcutsService shortcutsService;
	@Mock
	private LabelService labelService;

	@Spy
	@InjectMocks
	private ShortcutsUtilService shortcutsUtilService;

	@Test
	public void testReturnEmptyConditions()
	{
		//given
		final List<PK> list = new ArrayList<>();

		//then
		assertThat(shortcutsUtilService.getConditions(ValueComparisonOperator.IN, list)).isEqualTo(list);
	}

	@Test
	public void testReturnConditions()
	{
		//given
		final List<PK> list = createPKList();

		//when
		shortcutsUtilService.getConditions(ValueComparisonOperator.IN, list);

		//then
		verify(shortcutsUtilService).addFilterConditionOnSearchData(ValueComparisonOperator.IN, list);
	}

	@Test
	public void testHandleDropMultiSelection()
	{
		//given
		final List<ItemModel> result = createItemModelList();
		final DropEvent event = createDropEvent(result, null);

		//then
		assertThat(shortcutsUtilService.getSelectedData(event)).isEqualTo(result);
	}

	@Test
	public void testHandleDropSingleSelection()
	{
		//given
		final List<ItemModel> result = createItemModelList();
		final DropEvent event = createDropEvent(null, result.get(0));

		//then
		assertThat(shortcutsUtilService.getSelectedData(event)).isEqualTo(result);
	}

	@Test
	public void shouldReturnEmptyWhenListIsEmpty()
	{
		//given
		final List<PK> list = new ArrayList<>();

		//then
		assertThat(shortcutsUtilService.addFilterConditionOnSearchData(ValueComparisonOperator.IN, list))
				.isEqualTo(Lists.newArrayList());
	}


	@Test
	public void shouldReturnConditionsWhenListIsNotEmpty()
	{
		//given
		final List<PK> list = createPKList();

		//then
		assertThat(shortcutsUtilService.addFilterConditionOnSearchData(ValueComparisonOperator.IN, list).size()).isEqualTo(1);
		assertThat(shortcutsUtilService.addFilterConditionOnSearchData(ValueComparisonOperator.IN, list).get(0).getValue())
				.isEqualTo(list);
		assertThat(shortcutsUtilService.addFilterConditionOnSearchData(ValueComparisonOperator.IN, list).get(0).getOperator())
				.isEqualTo(ValueComparisonOperator.IN);
	}

	@Test
	public void shouldReturnPKEmptyCondition()
	{
		//given
		final SearchConditionData condition = shortcutsUtilService.getPKEmptyCondition();
		//then
		assertThat(condition.getValue()).isEqualTo(null);
		assertThat(condition.getOperator()).isEqualTo(ValueComparisonOperator.IS_EMPTY);
		assertThat(condition.getFieldType().getName()).isEqualTo(ItemModel.PK);
	}

	@Test
	public void testIsItemsAlreadyDeletedReturnFalse()
	{
		//given
		final List<ItemModel> list = createItemModelList();

		//then
		when(modelService.isRemoved(list.get(0))).thenReturn(false);
		assertThat(shortcutsUtilService.isItemsAlreadyDeleted(list)).isFalse();
	}

	@Test
	public void testIsItemsAlreadyDeletedWhenListIsEmpty()
	{
		//given
		final List<ItemModel> list = new ArrayList<>();

		//then
		assertThat(shortcutsUtilService.isItemsAlreadyDeleted(list)).isFalse();
	}

	@Test
	public void testIsItemsAlreadyDeletedReturnTrue()
	{
		//given
		final List<ItemModel> list = createItemModelList();

		//then
		when(modelService.isRemoved(list.get(0))).thenReturn(true);
		assertThat(shortcutsUtilService.isItemsAlreadyDeleted(list)).isTrue();
	}

	@Test
	public void testIsLimitationExceededReturnFalse()
	{
		//given
		final List<ItemModel> list = createItemModelList();

		final Shortcutsitem shortcutsitem = mock(Shortcutsitem.class);
		prepareDataForTestIsLimitationExceeded(list, shortcutsitem, 10);

		//then
		assertThat(shortcutsUtilService.isLimitationExceeded(list, shortcutsitem)).isFalse();
	}

	@Test
	public void testIsLimitationExceededReturnTrue()
	{
		//given
		final List<ItemModel> list = createItemModelList();
		final List<ItemModel> list2 = createItemModelList();
		list2.addAll(list);

		final Shortcutsitem shortcutsitem = mock(Shortcutsitem.class);
		prepareDataForTestIsLimitationExceeded(list2, shortcutsitem, 1);

		//then
		assertThat(shortcutsUtilService.isLimitationExceeded(list2, shortcutsitem)).isTrue();
	}

	@Test
	public void shouldReturnAlreadyExistItems()
	{
		//given
		final List<ItemModel> list = createItemModelList();

		when(shortcutsService.collectionContainsItem(any(), any())).thenReturn(true);

		//then
		assertThat(shortcutsUtilService.getAlreadyExistItems(list, mock(BackofficeObjectSpecialCollectionModel.class)).get(0))
				.isEqualTo(list.get(0));
		assertThat(shortcutsUtilService.getAlreadyExistItems(list, mock(BackofficeObjectSpecialCollectionModel.class)).size())
				.isEqualTo(1);
	}

	@Test
	public void shouldReturnEmptyListWhenNoItemsExist()
	{
		//given
		final List<ItemModel> list = createItemModelList();

		when(shortcutsService.collectionContainsItem(any(), any())).thenReturn(false);

		//then
		assertThat(shortcutsUtilService.getAlreadyExistItems(list, mock(BackofficeObjectSpecialCollectionModel.class)).size())
				.isZero();
	}

	@Test
	public void shouldNotExistItems()
	{
		//given
		final List<ItemModel> list = createItemModelList();

		when(shortcutsService.collectionContainsItem(any(), any())).thenReturn(false);

		//then
		assertThat(shortcutsUtilService.getNotExistItems(list, mock(BackofficeObjectSpecialCollectionModel.class)).get(0))
				.isEqualTo(list.get(0));
		assertThat(shortcutsUtilService.getNotExistItems(list, mock(BackofficeObjectSpecialCollectionModel.class)).size())
				.isEqualTo(1);
	}

	@Test
	public void shouldReturnEmptyListWhenAllItemsExist()
	{
		//given
		final List<ItemModel> list = createItemModelList();

		when(shortcutsService.collectionContainsItem(any(), any())).thenReturn(true);

		//then
		assertThat(shortcutsUtilService.getNotExistItems(list, mock(BackofficeObjectSpecialCollectionModel.class)).size())
				.isZero();
	}


	private static DropEvent createDropEvent(final List multiSelection, final ItemModel item)
	{
		final Div draggedDiv = mock(Div.class);
		when(draggedDiv.getAttribute(DragAndDropStrategy.ATTRIBUTE_DND_DATA, true)).thenReturn(item);
		final Div targetDiv = mock(Div.class);

		if (multiSelection != null)
		{
			when(draggedDiv.getAttribute(DragAndDropStrategy.ATTRIBUTE_DND_SELECTION_SUPPLIER, true))
					.thenReturn((SelectionSupplier) () -> multiSelection);
		}

		return new DropEvent("event", targetDiv, draggedDiv, 0, 0, 0, 0, 0);
	}

	private static List<PK> createPKList()
	{
		final List<PK> list = new ArrayList<>();
		final PK pk = PK.createCounterPK(1);
		list.add(pk);
		return list;
	}

	private static List<ItemModel> createItemModelList()
	{
		final List<ItemModel> list = new ArrayList<>();
		final ProductModel item = mock(ProductModel.class);
		list.add(item);
		return list;
	}

	private static List<ProductModel> createProductModelList()
	{
		final List<ProductModel> list = new ArrayList<>();
		final ProductModel item = mock(ProductModel.class);
		list.add(item);
		return list;
	}

	private void prepareDataForTestIsLimitationExceeded(final List<ItemModel> list, final Shortcutsitem shortcutsitem,
			final int maxSize)
	{
		doReturn(maxSize).when(shortcutsUtilService).getMaxSize(any());
	}

}
