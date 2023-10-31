/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.interf.external.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Test;


@UnitTest
public class CharacteristicValueImplTest
{
	CharacteristicValueImpl classUnderTest = new CharacteristicValueImpl();



	@Test
	public void testCharacteristicValueImplAttributes()
	{
		final String characteristic = "A";
		classUnderTest.setCharacteristic(characteristic);
		assertEquals(characteristic, classUnderTest.getCharacteristic());
		final String author = "8";
		classUnderTest.setAuthor(author);
		assertEquals(author, classUnderTest.getAuthor());
		final String characteristicText = "Text";
		classUnderTest.setCharacteristicText(characteristicText);
		assertEquals(characteristicText, classUnderTest.getCharacteristicText());
		final String instId = "1";
		classUnderTest.setInstId(instId);
		assertEquals(instId, classUnderTest.getInstId());
		classUnderTest.setInvisible(true);
		assertTrue(classUnderTest.isInvisible());
		final String value = "Value";
		classUnderTest.setValue(value);
		assertEquals(value, classUnderTest.getValue());
		final String valueText = "ValueText";
		classUnderTest.setValueText(valueText);
		assertEquals(valueText, classUnderTest.getValueText());


	}

}
