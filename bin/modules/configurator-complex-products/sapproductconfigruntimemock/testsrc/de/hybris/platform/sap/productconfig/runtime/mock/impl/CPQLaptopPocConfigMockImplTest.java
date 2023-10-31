/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.mock.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessage;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessagePromoType;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSeverity;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSource;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSourceSubType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

public class CPQLaptopPocConfigMockImplTest
{
	private final CPQLaptopPocConfigMockImpl classUnderTest = new CPQLaptopPocConfigMockImpl();
	private ConfigModel model;

	@Before
	public void setUp()
	{
		model = classUnderTest.createDefaultConfiguration();
	}

	@Test
	public void testCheckModel4GHzSet()
	{
		removeGameBuilder(model);

		final CsticModel cpuCsticModel = model.getRootInstance().getCstic(CPQLaptopPocConfigMockImpl.CPQ_CPU);
		final CsticValueModel valueModel = cpuCsticModel.getAssignableValues().get(0);
		final List<CsticValueModel> assignedValues = new ArrayList<>();
		assignedValues.add(valueModel);
		cpuCsticModel.setAssignedValues(assignedValues);

		classUnderTest.checkModel(model);

		final CsticModel softwareCsticModel = model.getRootInstance().getCstic(CPQLaptopPocConfigMockImpl.CPQ_SOFTWARE);
		assertNotNull(softwareCsticModel);
		assertEquals(4, softwareCsticModel.getAssignableValues().size());
	}

	@Test
	public void testCheckModelOtherThan4GHzSet()
	{
		removeGameBuilder(model);

		final CsticModel cpuCsticModel = model.getRootInstance().getCstic(CPQLaptopPocConfigMockImpl.CPQ_CPU);
		final CsticValueModel valueModel = cpuCsticModel.getAssignableValues().get(1);
		final List<CsticValueModel> assignedValues = new ArrayList<>();
		assignedValues.add(valueModel);
		cpuCsticModel.setAssignedValues(assignedValues);

		classUnderTest.checkModel(model);

		final CsticModel softwareCsticModel = model.getRootInstance().getCstic(CPQLaptopPocConfigMockImpl.CPQ_SOFTWARE);
		assertNotNull(softwareCsticModel);
		assertEquals(5, softwareCsticModel.getAssignableValues().size());
	}

	@Test
	public void testEnrichConfigurationModel() throws ParseException
	{
		removeGameBuilder(model);

		final CsticModel ramCsticModel = model.getRootInstance().getCstic(CPQLaptopPocConfigMockImpl.CPQ_RAM);
		final CsticValueModel valueModel = ramCsticModel.getAssignableValues().get(1);
		final List<CsticValueModel> assignedValues = new ArrayList<>();
		assignedValues.add(valueModel);
		ramCsticModel.setAssignedValues(assignedValues);

		classUnderTest.enrichConfigurationModel(model);

		assertNotNull(model.getMessages());
		assertEquals(1, model.getMessages().size());
		final ProductConfigMessage modelMessage = model.getMessages().toArray(ProductConfigMessage[]::new)[0];
		verifyMessage(modelMessage, "Message for product", null, "Product message rule", ProductConfigMessageSeverity.INFO,
				ProductConfigMessageSource.ENGINE, ProductConfigMessageSourceSubType.DEFAULT, null, null);

		final Date date = (new SimpleDateFormat("yyyyMMdd")).parse("20221231");
		final CsticModel ramCsticModelAfterCheck = model.getRootInstance().getCstic(CPQLaptopPocConfigMockImpl.CPQ_RAM);
		assertNotNull(ramCsticModelAfterCheck);
		assertNotNull(ramCsticModelAfterCheck.getMessages());
		assertEquals(1, ramCsticModelAfterCheck.getMessages().size());
		final ProductConfigMessage csticMessage = ramCsticModelAfterCheck.getMessages().toArray(ProductConfigMessage[]::new)[0];
		verifyMessage(csticMessage, "Message for characteristic", "Extended message for characteristic",
				"Characteristic message rule", ProductConfigMessageSeverity.INFO, ProductConfigMessageSource.ENGINE,
				ProductConfigMessageSourceSubType.DEFAULT, ProductConfigMessagePromoType.PROMO_APPLIED, date);

		final CsticValueModel value32GB = ramCsticModelAfterCheck.getAssignableValues().get(1);
		assertNotNull(value32GB);
		assertNotNull(value32GB.getMessages());
		assertEquals(1, value32GB.getMessages().size());
		final ProductConfigMessage csticValueMessage = value32GB.getMessages().toArray(ProductConfigMessage[]::new)[0];
		verifyMessage(csticValueMessage, "Message for characteristic value", "Extended message for characteristic value",
				"Characteristic value message rule", ProductConfigMessageSeverity.INFO, ProductConfigMessageSource.ENGINE,
				ProductConfigMessageSourceSubType.DEFAULT, ProductConfigMessagePromoType.PROMO_APPLIED, date);
	}

	protected void verifyMessage(final ProductConfigMessage productConfigmessage, final String message,
			final String extendedMessage,
			final String key, final ProductConfigMessageSeverity severity, final ProductConfigMessageSource source,
			final ProductConfigMessageSourceSubType sourceSubType, final ProductConfigMessagePromoType promoType, final Date date)
	{
		assertEquals(message, productConfigmessage.getMessage());
		assertEquals(extendedMessage, productConfigmessage.getExtendedMessage());
		assertEquals(key, productConfigmessage.getKey());
		assertEquals(severity, productConfigmessage.getSeverity());
		assertEquals(source, productConfigmessage.getSource());
		assertEquals(sourceSubType, productConfigmessage.getSourceSubType());
		assertEquals(promoType, productConfigmessage.getPromoType());
		assertEquals(date, productConfigmessage.getEndDate());
	}

	private void removeGameBuilder(final ConfigModel model)
	{
		final CsticModel softwareCsticModel = model.getRootInstance().getCstic(CPQLaptopPocConfigMockImpl.CPQ_SOFTWARE);
		assertNotNull(softwareCsticModel);

		final List<CsticValueModel> softwareValues = softwareCsticModel.getAssignableValues().stream().filter(value -> !(CPQLaptopPocConfigMockImpl.GAMEBUILDER.equalsIgnoreCase(value.getName()))).collect(Collectors.toList());

		softwareCsticModel.setAssignableValues(softwareValues);
	}
}
