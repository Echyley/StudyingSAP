/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationbackoffice.widgets.modals.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.integrationbackoffice.TypeCreatorTestUtils;
import de.hybris.platform.integrationbackoffice.utility.ItemTypeMatchSelector;
import de.hybris.platform.integrationservices.enums.ItemTypeMatchEnum;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.integrationservices.search.ItemTypeMatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;

import com.hybris.cockpitng.testing.AbstractWidgetUnitTest;
import com.hybris.cockpitng.testing.annotation.DeclaredInput;
import com.hybris.cockpitng.testing.annotation.DeclaredViewEvent;
import com.hybris.cockpitng.testing.annotation.NullSafeWidget;

@DeclaredInput(value = "openItemTypeIOIModal", socketType = IntegrationObjectModel.class)
@DeclaredViewEvent(componentID = "saveButton", eventName = Events.ON_CLICK)
@NullSafeWidget(false)
@UnitTest
public class ItemTypeMatchModalControllerUnitTest extends AbstractWidgetUnitTest<ItemTypeMatchModalController>
{
	@Mock
	protected ItemTypeMatchSelector itemTypeMatchSelector;

	@Spy
	@InjectMocks
	private ItemTypeMatchModalController controller;

	private IntegrationObjectModel io;
	private final Set<IntegrationObjectItemModel> ioiSet = new HashSet<>();

	private final ItemTypeMatchEnum e1 = ItemTypeMatchEnum.ALL_SUB_AND_SUPER_TYPES;
	private final ItemTypeMatchEnum e2 = ItemTypeMatchEnum.RESTRICT_TO_ITEM_TYPE;
	private final ItemTypeMatchEnum e3 = ItemTypeMatchEnum.ALL_SUBTYPES;

	private final List<ItemTypeMatchEnum> enumList1 = new ArrayList<>(Arrays.asList(e1, e2, e3));
	private final List<ItemTypeMatchEnum> enumList2 = new ArrayList<>(Arrays.asList(e1, e2));

	private ComposedTypeModel c1;
	private ComposedTypeModel c2;
	private ComposedTypeModel c3;

	private static final int DROPDOWN_INDEX = 1;


	@Before
	public void setup()
	{
		controller.itemTypeMatcherListBox = new Listbox();
		controller.saveButton = new Button();
		controller.initialize(mock(Component.class));

		io = mock(IntegrationObjectModel.class);

		c1 = TypeCreatorTestUtils.composedTypeModel("Product");
		c2 = TypeCreatorTestUtils.composedTypeModel("Catalog");
		c3 = TypeCreatorTestUtils.composedTypeModel("CatalogVersion");

		final IntegrationObjectItemModel ioi1 = TypeCreatorTestUtils.integrationObjectItemModel(c1);
		final IntegrationObjectItemModel ioi2 = TypeCreatorTestUtils.integrationObjectItemModel(c2);
		final IntegrationObjectItemModel ioi3 = TypeCreatorTestUtils.integrationObjectItemModel(c3);

		when(ioi1.getType()).thenReturn(c1);
		when(ioi2.getType()).thenReturn(c2);
		when(ioi3.getType()).thenReturn(c3);

		setupItemTypeMatch(ioi1, ioi2, ioi3);

		ioiSet.add(ioi1);
		ioiSet.add(ioi2);
		ioiSet.add(ioi3);

		when(io.getItems()).thenReturn(ioiSet);
	}

	@Test
	public void testCorrectNumberOfItemsCreated()
	{
		executeInputSocketEvent("openItemTypeIOIModal", io);
		assertEquals(ioiSet.size(), getListbox().getItemCount());
	}

	@Test
	public void testCorrectNumberOfItemsCreatedIOWithMultipleTypeDefinition()
	{
		final IntegrationObjectItemModel ioi4 = TypeCreatorTestUtils.integrationObjectItemModel(c1);
		ioiSet.add(ioi4);

		executeInputSocketEvent("openItemTypeIOIModal", io);
		assertEquals(ioiSet.size() - 1, getListbox().getItemCount());
	}

	@Test
	public void testMatchDropDownContainsCorrectOptions()
	{
		executeInputSocketEvent("openItemTypeIOIModal", io);

		final int catalogIndex = 0;
		final int catalogVersionIndex = 1;
		final int productIndex = 2;

		final Listitem catalogItem = (Listitem) getListbox().getChildren().get(catalogIndex);
		final Listbox catalogDropdown = (Listbox) catalogItem.getChildren().get(DROPDOWN_INDEX).getFirstChild();

		final Listitem catalogVersionItem = (Listitem) getListbox().getChildren().get(catalogVersionIndex);
		final Listbox catalogVersionDropdown = (Listbox) catalogVersionItem.getChildren().get(DROPDOWN_INDEX).getFirstChild();

		final Listitem productItem = (Listitem) getListbox().getChildren().get(productIndex);
		final Listbox productDropdown = (Listbox) productItem.getChildren().get(DROPDOWN_INDEX).getFirstChild();

		final List<String> catalogLabels = new ArrayList<>();
		final List<String> catalogVersionLabels = new ArrayList<>();
		final List<String> productLabels = new ArrayList<>();

		catalogDropdown.getItems().forEach(i -> catalogLabels.add(i.getLabel()));
		catalogVersionDropdown.getItems().forEach(i -> catalogVersionLabels.add(i.getLabel()));
		productDropdown.getItems().forEach(i -> productLabels.add(i.getLabel()));

		final List<String> enumList1Codes = new ArrayList<>();
		final List<String> enumList2Codes = new ArrayList<>();

		enumList1.forEach(e -> enumList1Codes.add(e.getCode()));
		enumList2.forEach(e -> enumList2Codes.add(e.getCode()));

		// Assert Lengths
		assertEquals(enumList2.size(), catalogDropdown.getItemCount());
		assertEquals(enumList2.size(), catalogVersionDropdown.getItemCount());
		assertEquals(enumList1.size(), productDropdown.getItemCount());

		// Assert values match
		assertTrue(catalogLabels.containsAll(enumList2Codes));
		assertTrue(catalogVersionLabels.containsAll(enumList2Codes));
		assertTrue(productLabels.containsAll(enumList1Codes));

	}

	protected Listbox getListbox()
	{
		return controller.itemTypeMatcherListBox;
	}

	protected Button getSaveButton()
	{
		return controller.saveButton;
	}

	@Override
	protected ItemTypeMatchModalController getWidgetController()
	{
		return controller;
	}

	private void setupItemTypeMatch(final IntegrationObjectItemModel ioi1, final IntegrationObjectItemModel ioi2,
	                                final IntegrationObjectItemModel ioi3)
	{
		when(ioi1.getAllowedItemTypeMatches()).thenReturn(enumList1);
		when(ioi2.getAllowedItemTypeMatches()).thenReturn(enumList2);
		when(ioi3.getAllowedItemTypeMatches()).thenReturn(enumList2);

		final ItemTypeMatch itemTypeMatch = ItemTypeMatch.DEFAULT;
		when(itemTypeMatchSelector.getToSelectItemTypeMatch(any(IntegrationObjectItemModel.class))).thenReturn(itemTypeMatch);
	}
}
