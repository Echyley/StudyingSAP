/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modals.controllers;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;


import de.hybris.platform.integrationservices.model.IntegrationObjectModel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MetadataViewerControllerUnitTest
{

	@Test
	public void testGenerateServicePath()
	{
		final IntegrationObjectModel integrationObject = mock(IntegrationObjectModel.class);
		lenient().when(integrationObject.getCode()).thenReturn("Product1");

		final IntegrationObjectMetadataViewerController controller = new IntegrationObjectMetadataViewerController();

		controller.setSelectedIntegrationObject(integrationObject);

		assertEquals("https://<your-host-name>/odata2webservices/Product1/$metadata", controller.generateServicePath());
	}

}
