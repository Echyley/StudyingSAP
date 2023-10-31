/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.rules.backoffice.editors;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.IntegrationTest;

import org.junit.Test;
import org.zkoss.zul.Comboitem;


// works only as integration test, class cast exception as unit test
@IntegrationTest
public class ProductConfigParameterRendererTest
{
	ProductConfigParameterRenderer classUnderTest = new ProductConfigParameterRenderer();

	@Test
	public void testRender()
	{
		final Comboitem item = new Comboitem();
		final Object data = "label";
		classUnderTest.render(item, data, 1);

		assertEquals(data, item.getValue());
		assertEquals(data.toString(), item.getLabel());
	}
}
