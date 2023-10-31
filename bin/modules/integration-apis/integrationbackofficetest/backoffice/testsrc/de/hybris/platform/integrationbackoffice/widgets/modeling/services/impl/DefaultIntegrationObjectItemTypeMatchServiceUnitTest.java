package de.hybris.platform.integrationbackoffice.widgets.modeling.services.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.lenient;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultIntegrationObjectItemTypeMatchServiceUnitTest
{
	private ItemTypeMatchSelector selector;
	private DefaultIntegrationObjectItemTypeMatchService service;
	private Map<ComposedTypeModel, ItemTypeMatchEnum> itemTypeMatchMap;
	IntegrationObjectModel ioModel;
	ItemTypeMatch typeMatch1;
	ItemTypeMatch typeMatch2;
	ItemTypeMatch typeMatch3;
	final ComposedTypeModel c1 = TypeCreatorTestUtils.composedTypeModel("Product");
	final ComposedTypeModel c2 = TypeCreatorTestUtils.composedTypeModel("Media");
	IntegrationObjectItemModel ioi1;
	IntegrationObjectItemModel ioi2;
	IntegrationObjectItemModel ioi3;

	@Before
	public void setup()
	{
		selector = mock(ItemTypeMatchSelector.class);
		service = new DefaultIntegrationObjectItemTypeMatchService(selector);
		itemTypeMatchMap = new HashMap<>();
		ioModel = new IntegrationObjectModel();
		typeMatch1 = ItemTypeMatch.ALL_SUB_AND_SUPER_TYPES;
		typeMatch2 = ItemTypeMatch.ALL_SUBTYPES;
		typeMatch3 = ItemTypeMatch.DEFAULT;
	}

	private void createIOI()
	{
		ioi1 = new IntegrationObjectItemModel();
		ioi2 = new IntegrationObjectItemModel();
		ioi3 = new IntegrationObjectItemModel();

		ioi1.setType(c1);
		ioi2.setType(c2);
		ioi3.setType(c2);

		ioModel.setItems(Set.of(ioi1, ioi2, ioi3));
	}

	private void mockSelector()
	{
		lenient().when(selector.getToSelectItemTypeMatch(ioi1)).thenReturn(typeMatch1);
		lenient().when(selector.getToSelectItemTypeMatch(ioi2)).thenReturn(typeMatch2);
		lenient().when(selector.getToSelectItemTypeMatch(ioi3)).thenReturn(typeMatch2);
	}

	private void checkAssertions()
	{
		assertEquals(typeMatch1.name(), ioi1.getItemTypeMatch().getCode());
		assertEquals(typeMatch2.name(), ioi2.getItemTypeMatch().getCode());
		assertEquals(typeMatch2.name(), ioi3.getItemTypeMatch().getCode());
	}

	@Test
	public void testAssignItemTypeMatchForIntegrationObject()
	{
		this.createIOI();
		ioi1.setItemTypeMatch(ItemTypeMatchEnum.valueOf(typeMatch3.name()));
		ioi2.setItemTypeMatch(ItemTypeMatchEnum.valueOf(typeMatch3.name()));
		ioi3.setItemTypeMatch(ItemTypeMatchEnum.valueOf(typeMatch3.name()));
		this.mockSelector();

		service.assignItemTypeMatchForIntegrationObject(ioModel, itemTypeMatchMap);
		this.checkAssertions();
	}

	@Test
	public void testAssignItemTypeMatchForIntegrationObjectWithNull()
	{
		this.createIOI();
		this.mockSelector();

		service.assignItemTypeMatchForIntegrationObject(ioModel, itemTypeMatchMap);
		
		this.checkAssertions();
	}

	@Test
	public void testGroupItemTypeMatchForIntegrationObject()
	{
		ioi1 = TypeCreatorTestUtils.integrationObjectItemModel(c1, typeMatch1);
		ioi2 = TypeCreatorTestUtils.integrationObjectItemModel(c2, typeMatch2);
		ioi3 = TypeCreatorTestUtils.integrationObjectItemModel(c2, typeMatch2);
		this.mockSelector();
		ioModel.setItems(Set.of(ioi1, ioi2, ioi3));

		itemTypeMatchMap = service.groupItemTypeMatchForIntegrationObject(ioModel);
		assertEquals(2, itemTypeMatchMap.size());
	}
}
