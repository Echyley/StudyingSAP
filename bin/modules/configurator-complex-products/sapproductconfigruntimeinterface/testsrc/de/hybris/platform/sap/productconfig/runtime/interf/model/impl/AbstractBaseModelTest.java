/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.interf.model.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.BaseModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public abstract class AbstractBaseModelTest
{

	protected void testGenericEqualPart(final BaseModel model1, final BaseModel model2)
	{
		assertNotNull(model1);
		assertNotEquals(new DummyModel(), model1);
		assertEquals(model1, model2);
	}

	protected void equalCheck(final Object model1, final Object model2, final String methodName, final Object value,
			final Object tempValue) throws Exception
	{
		assertEquals(model1, model2);

		Class classType = value.getClass();

		if (classType.equals(Boolean.class))
		{
			classType = boolean.class;
		}
		else if (classType.equals(ArrayList.class))
		{
			classType = List.class;
		}
		else if (classType.equals(InstanceModelImpl.class))
		{
			classType = InstanceModel.class;
		}
		else if (classType.equals(PriceModelImpl.class))
		{
			classType = PriceModel.class;
		}
		else if (classType.equals(KBKeyImpl.class))
		{
			classType = KBKey.class;
		}

		final Method method = model1.getClass().getMethod(methodName, classType);

		method.invoke(model2, value);
		assertNotEquals(model1, model2);

		if (tempValue != null)
		{
			method.invoke(model1, tempValue);
			assertNotEquals(model1, model2);
		}

		method.invoke(model1, value);
		assertEquals(model1, model2);
	}

	private static class DummyModel implements BaseModel
	{
		//empty - used for equals tests
	}
}
