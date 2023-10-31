/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.configuration.utility;

import static de.hybris.platform.integrationbackoffice.IntegrationbackofficetestUtils.composedTypeModel;
import static de.hybris.platform.integrationbackoffice.IntegrationbackofficetestUtils.integrationObjectItemModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.integrationbackoffice.IntegrationbackofficetestUtils;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.zkoss.zul.Listitem;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ExportConfigurationUtilsUnitTest
{
	private static final String ROOT_ITEM_CODE = "InboundChannelConfigurationAlias";
	private static final String ROOT_ITEM_TYPE_CODE = "InboundChannelConfiguration";

	@Test
	public void testCreateListItem()
	{
		// setup
		final ComposedTypeModel composedTypeModel = composedTypeModel(ROOT_ITEM_TYPE_CODE);
		final IntegrationObjectItemModel integrationObjectItemModel = integrationObjectItemModel(ROOT_ITEM_CODE,
				composedTypeModel);
		final IntegrationObjectModel integrationObjectModel = IntegrationbackofficetestUtils.integrationObjectModelMock(integrationObjectItemModel);

		// test
		final Listitem listitem = ExportConfigurationUtils.createListitem(integrationObjectModel);
		final String label = listitem.getLabel();
		assertNotEquals("Listitem label is not integration object root item code.", ROOT_ITEM_CODE, label);
		assertEquals("Listitem label is integration object root item type code.", ROOT_ITEM_TYPE_CODE, label);
		assertEquals("Listitem value is integration object.", integrationObjectModel, listitem.getValue());
	}

	@Test
	public void testAppendCountToLabelShouldShowCountNoPreviousCount()
	{
		Listitem item = new Listitem();
		item.setLabel("Test");

		final String countFraction = "1/3";

		item = ExportConfigurationUtils.appendCountToLabel(item, countFraction);

		assertEquals("Test [1/3]", item.getLabel());
	}

	@Test
	public void testAppendCountToLabelShouldShowCountPreviousCount()
	{
		Listitem item = new Listitem();
		item.setLabel("Test [1/3]");

		final String countFraction = "2/3";

		item = ExportConfigurationUtils.appendCountToLabel(item, countFraction);

		assertEquals("Test [2/3]", item.getLabel());

	}

	@Test
	public void testAppendCountToLabelZeroCountNumeratorShouldNotShow()
	{
		Listitem item = new Listitem();
		item.setLabel("Test [1/3]");

		final String countFraction = "0/3";

		item = ExportConfigurationUtils.appendCountToLabel(item, countFraction);

		assertEquals("Test", item.getLabel());
	}

	@Test
	public void testAppendCountToLabelZeroCountDenominatorShouldNotShow()
	{
		Listitem item = new Listitem();
		item.setLabel("Test [1/3]");

		final String countFraction = "1/0";

		item = ExportConfigurationUtils.appendCountToLabel(item, countFraction);

		assertEquals("Test", item.getLabel());

	}

	@Test (expected = IllegalArgumentException.class)
	public void testAppendCountToLabelIllegalArgumentNonFractionString()
	{
		final Listitem item = new Listitem();
		item.setLabel("Test");
		final String invalidFraction = "abc/def";

		ExportConfigurationUtils.appendCountToLabel(item, invalidFraction);
		// Exception thrown here.
	}
}
