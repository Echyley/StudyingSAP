/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.integrationservices.populator.AbstractItemToMapPopulator;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.internal.model.impl.DefaultModelService;
import de.hybris.platform.servicelayer.model.ModelService;

@IntegrationTest
public class BaseContextIntegrationTest extends ServicelayerTest
{
	@Rule
	public BaseContext ctx = new BaseContext(){};

	@Test
	public void testGetModelServiceFromContext()
	{
		final ModelService modelService = ctx.modelService();
		assertThat(modelService).isNotNull().isInstanceOf(DefaultModelService.class);
	}

	@Test
	public void testGetServiceForMultipleBeansOfSameType()
	{
		final Class<AbstractItemToMapPopulator> type = AbstractItemToMapPopulator.class;
		assertMultipleBeansOfType(type);

		final AbstractItemToMapPopulator bean = ctx.getService("defaultAtomicType2MapPopulator", type);
		assertThat(bean).isInstanceOf(type);
	}

	private void assertMultipleBeansOfType(final Class<?> aClass)
	{
		final ApplicationContext context = getApplicationContext();
		final String[] beanNames = context.getBeanNamesForType(aClass);

		assertThat(beanNames.length).isGreaterThan(1);
	}
}

