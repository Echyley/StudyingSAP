/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationbackoffice.widgets.modals.data;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.integrationbackoffice.widgets.modals.data.itemtypematchmodal.ItemTypeMatchChangeDetector;
import de.hybris.platform.integrationservices.enums.ItemTypeMatchEnum;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

@UnitTest
public class ItemTypeMatchChangeDetectorUnitTest
{
	private static final String EXPECTED_ITEMTYPEMATCH_CODE = ItemTypeMatchEnum.ALL_SUBTYPES.getCode();

	@Test
	public void testAllIOIItemTypeMatchesAreUpdated()
	{
		final IntegrationObjectItemModel ioi1 = integrationObjectItemModel("IOI1", ItemTypeMatchEnum.RESTRICT_TO_ITEM_TYPE);
		final IntegrationObjectItemModel ioi2 = integrationObjectItemModel("IOI2", ItemTypeMatchEnum.ALL_SUB_AND_SUPER_TYPES);
		final IntegrationObjectItemModel ioi3 = integrationObjectItemModel("IOI2", ItemTypeMatchEnum.ALL_SUBTYPES);
		final IntegrationObjectItemModel ioi4 = integrationObjectItemModel("IOI2", null);

		final List<IntegrationObjectItemModel> ioiList = new ArrayList<>();
		ioiList.add(ioi1);
		ioiList.add(ioi2);
		ioiList.add(ioi3);
		ioiList.add(ioi4);

		final ItemTypeMatchChangeDetector changeDetector = new ItemTypeMatchChangeDetector(ioiList,
				ItemTypeMatchEnum.ALL_SUBTYPES.getCode());

		changeDetector.getIntegrationObjectItemModels().forEach(ioi -> {
			assertEquals(EXPECTED_ITEMTYPEMATCH_CODE, ioi.getItemTypeMatch().getCode());
		});

	}

	private IntegrationObjectItemModel integrationObjectItemModel(final String code, final ItemTypeMatchEnum typeMatchEnum)
	{
		final IntegrationObjectItemModel ioi = new IntegrationObjectItemModel();
		ioi.setCode(code);
		ioi.setItemTypeMatch(typeMatchEnum);

		return ioi;
	}
}
