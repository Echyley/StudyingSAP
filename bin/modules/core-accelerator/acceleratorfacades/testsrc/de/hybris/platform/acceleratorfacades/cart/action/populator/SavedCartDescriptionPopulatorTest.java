/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorfacades.cart.action.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.i18n.L10NService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SavedCartDescriptionPopulatorTest
{

	private static final String DESCRIPTION_GENERATED_AUTOMATIC_IN_EN = "This cart was created by CSV import 1662625265496. Successfully imported:3 lines. Imported but with quantity adjustment: 0 lines. Could not import: 0 lines.";
	private static final String DESCRIPTION_GENERATED_AUTOMATIC_IN_DE = "Dieser Warenkorb wurde beim CSV-Import 1662625265496 erstellt. Erfolgreich importiert: 3 Zeilen. Mit Mengenanpassung importiert: 0 Zeilen. Nicht importiert: 0 Zeilen.";
	private static final String DESCRIPTION_WRITTEN_BY_CONSUMER_IN_EN = "This cart was manually added by the consumer instead of csv import";
	private static final String DESCRIPTION_WRITTEN_BY_CONSUMER_IN_EN_WITH_4_ARGS = "This cart was manually added by the consumer, it contains 3 items in total, including 1 short sleeve, 1 glasses and 1 bag.";

	@InjectMocks
	private SavedCartDescriptionPopulator savedCartDescriptionPopulator;

	@Mock
	private L10NService l10nService;

	private CartData cartData;

	@Before
	public void setUp()
	{
		cartData = new CartData();
	}

	@Test
	public void testSaveCartCreatedByCSVImportDescriptionLocalizationSuccess()
	{
		CartModel source = new CartModel();
		source.setDescription(DESCRIPTION_GENERATED_AUTOMATIC_IN_EN);
		Mockito.when(l10nService.getLocalizedString(any(), any())).thenReturn(DESCRIPTION_GENERATED_AUTOMATIC_IN_DE);
		savedCartDescriptionPopulator.populate(source, cartData);
		Assert.assertEquals(DESCRIPTION_GENERATED_AUTOMATIC_IN_DE, cartData.getDescription());
	}

	@Test
	public void testSaveCartCreatedByManualDescriptionLocalizationFailed()
	{
		CartModel source = new CartModel();
		source.setDescription(DESCRIPTION_WRITTEN_BY_CONSUMER_IN_EN);
		savedCartDescriptionPopulator.populate(source, cartData);
		Assert.assertEquals(DESCRIPTION_WRITTEN_BY_CONSUMER_IN_EN, cartData.getDescription());
	}

	@Test
	public void testSaveCartCreatedByManualAndArgSizeIsFourDescriptionLocalizationFailed(){
		CartModel source = new CartModel();
		source.setDescription(DESCRIPTION_WRITTEN_BY_CONSUMER_IN_EN_WITH_4_ARGS);
		savedCartDescriptionPopulator.populate(source, cartData);
		Assert.assertEquals(DESCRIPTION_WRITTEN_BY_CONSUMER_IN_EN_WITH_4_ARGS, cartData.getDescription());
	}

	@Test
	public void shouldNotPopulateIfSaveCartDescriptionIsNull()
	{
		CartModel source = new CartModel();
		savedCartDescriptionPopulator.populate(source, cartData);
		Assert.assertNull(cartData.getDescription());
	}

}
