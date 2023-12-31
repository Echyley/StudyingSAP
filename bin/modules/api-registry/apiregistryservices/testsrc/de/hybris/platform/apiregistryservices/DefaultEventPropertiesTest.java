/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.apiregistryservices;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;

import java.util.List;

import javax.annotation.Resource;

import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.apiregistryservices.dao.EventConfigurationDao;

import org.junit.Before;
import org.junit.Test;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.apiregistryservices.model.events.EventConfigurationModel;
import de.hybris.platform.apiregistryservices.model.events.EventPropertyConfigurationModel;


@IntegrationTest
public class DefaultEventPropertiesTest extends ServicelayerTransactionalTest
{
	@Resource
	private EventConfigurationDao eventConfigurationDao;

	@Resource
	private ModelService modelService;

	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Before
	public void setUp() throws Exception
	{
		importCsv("/test/eventConfigurations.impex", "UTF-8");
	}

	@Test
	public void findProperties()
	{
		final List<EventConfigurationModel> eventConfigsByClass = eventConfigurationDao
				.findActiveEventConfigsByClass("de.hybris.platform.order.events.SubmitOrderEvent");

		assertEquals("total number wrong", 1, eventConfigsByClass.size());

		final EventConfigurationModel eventConfiguration = eventConfigsByClass.get(0);
		assertEquals(1, eventConfiguration.getEventPropertyConfigurations().size());
		assertEquals("orderCode", eventConfiguration.getEventPropertyConfigurations().get(0).getPropertyName());

		modelService.remove(eventConfiguration);

		final EventPropertyConfigurationModel modelByExample = new EventPropertyConfigurationModel();
		modelByExample.setPropertyName("orderCode");

		assertThrows(ModelNotFoundException.class, () -> flexibleSearchService.getModelByExample(modelByExample));
	}
}
