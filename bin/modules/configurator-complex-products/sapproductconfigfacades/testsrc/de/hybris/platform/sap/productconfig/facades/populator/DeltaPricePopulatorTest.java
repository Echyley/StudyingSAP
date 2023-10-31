/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.facades.populator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.sap.productconfig.facades.ConfigPricing;
import de.hybris.platform.sap.productconfig.facades.PriceValueUpdateData;
import de.hybris.platform.sap.productconfig.facades.impl.ConfigPricingImpl;
import de.hybris.platform.sap.productconfig.facades.impl.ConfigPricingImplTest.DummyPriceDataFactory;
import de.hybris.platform.sap.productconfig.facades.impl.UniqueUIKeyGeneratorImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.CsticQualifier;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceValueUpdateModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.PriceModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.UniqueKeyGeneratorImpl;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;


/**
 * Unit test for {@link DeltaPricePopulator}
 */
@UnitTest
public class DeltaPricePopulatorTest
{
	private static final String NULL_KEY = "null-null@null@null";
	private DeltaPricePopulator deltaPricePopulator;
	PriceDataFactory priceDataFactory = new DummyPriceDataFactory();

	private final ConfigPricing configPricing = new ConfigPricingImpl();

	private final UniqueUIKeyGeneratorImpl uiKeyGenerator = new UniqueUIKeyGeneratorImpl();

	@Before
	public void setup()
	{
		uiKeyGenerator.setKeyGenerator(new UniqueKeyGeneratorImpl());
		deltaPricePopulator = new DeltaPricePopulator();
		configPricing.setPriceDataFactory(priceDataFactory);
		deltaPricePopulator.setConfigPricing(configPricing);
		deltaPricePopulator.setUiKeyGenerator(uiKeyGenerator);

	}

	@Test
	public void testPopulatePriceNull()
	{
		final PriceValueUpdateData target = new PriceValueUpdateData();
		final PriceValueUpdateModel source = new PriceValueUpdateModel();
		final CsticQualifier csticQualifier = new CsticQualifier();
		source.setCsticQualifier(csticQualifier);

		deltaPricePopulator.populate(source, target);
		assertEquals(NULL_KEY, target.getCsticUiKey());
		assertTrue(target.getPrices().isEmpty());
	}

	@Test
	public void testPopulateSelectedValuesl()
	{
		final PriceValueUpdateData target = new PriceValueUpdateData();
		final PriceValueUpdateModel source = new PriceValueUpdateModel();
		final CsticQualifier csticQualifier = new CsticQualifier();
		source.setCsticQualifier(csticQualifier);
		source.setSelectedValues(Collections.singletonList("aValue"));

		deltaPricePopulator.populate(source, target);
		assertEquals(1, target.getSelectedValues().size());
		assertEquals("aValue", target.getSelectedValues().get(0));
	}

	@Test
	public void testPopulatePricePriceDefined()
	{
		final PriceValueUpdateData target = new PriceValueUpdateData();
		final PriceValueUpdateModel source = new PriceValueUpdateModel();
		final CsticQualifier csticQualifier = new CsticQualifier();
		csticQualifier.setInstanceId("instanceId");
		csticQualifier.setInstanceName("instanceName");
		csticQualifier.setGroupName("groupName");
		csticQualifier.setCsticName("csticName");
		source.setCsticQualifier(csticQualifier);

		final PriceModel priceModel = createPriceModel("EUR", BigDecimal.valueOf(1.2), null);
		final PriceModel priceModelWithObsoleteValue = createPriceModel("EUR", BigDecimal.valueOf(1.2), BigDecimal.valueOf(5.0));

		final Map<String, PriceModel> valuePrices = new HashMap<String, PriceModel>();
		valuePrices.put("VALUE1", priceModel);
		valuePrices.put("VALUE2", priceModelWithObsoleteValue);
		source.setValuePrices(valuePrices);


		deltaPricePopulator.populate(source, target);
		assertEquals("instanceId-instanceName@groupName@csticName", target.getCsticUiKey());

		assertNotNull(target.getPrices().get("VALUE1").getPriceValue());
		assertNull(target.getPrices().get("VALUE1").getObsoletePriceValue());

		assertNotNull(target.getPrices().get("VALUE2").getPriceValue());
		assertEquals(BigDecimal.valueOf(1.2), target.getPrices().get("VALUE2").getPriceValue().getValue());
		assertNotNull(target.getPrices().get("VALUE2").getObsoletePriceValue());
		assertEquals(BigDecimal.valueOf(5.0), target.getPrices().get("VALUE2").getObsoletePriceValue().getValue());
	}

	protected PriceModel createPriceModel(final String currency, final BigDecimal priceValue, final BigDecimal obsoleteValue)
	{
		final PriceModel model = new PriceModelImpl();
		model.setCurrency(currency);
		model.setPriceValue(priceValue);
		model.setObsoletePriceValue(obsoleteValue);
		return model;
	}

}
