/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */
package de.hybris.platform.warehousingbackoffice.actions.printpacklabel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.warehousing.labels.service.PrintMediaService;
import de.hybris.platform.warehousing.taskassignment.services.WarehousingConsignmentWorkflowService;
import de.hybris.platform.warehousingbackoffice.labels.strategy.ConsignmentPrintDocumentStrategy;

import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.actions.ActionResult;
import com.hybris.cockpitng.testing.AbstractActionUnitTest;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PrintPackLabelActionTest extends AbstractActionUnitTest<PrintPackLabelAction>
{
	@InjectMocks
	private PrintPackLabelAction printPackLabelAction;
	@Mock
	private ActionContext actionContext;
	@Mock
	private PrintMediaService printMediaService;
	@Mock
	private ConsignmentModel consignmentModel;
	@Mock
	private MediaModel mediaModel;
	@Mock
	private ConsignmentPrintDocumentStrategy consignmentPrintPackSlipStrategy;
	@Mock
	private WarehousingConsignmentWorkflowService warehousingConsignmentWorkflowService;
	@Mock
	private Object externalFulfillmentConfiguration;
	@Mock
	private ConfigurationService configurationService;
	@Mock
	private Configuration configuration;

	@Override
	public PrintPackLabelAction getActionInstance()
	{
		return printPackLabelAction;
	}

	@Before
	public void setUp()
	{
		Mockito.lenient().when(actionContext.getData()).thenReturn(consignmentModel);
		Mockito.lenient().when(printMediaService.getMediaForTemplate(anyString(), any(ConsignmentProcessModel.class))).thenReturn(mediaModel);
		Mockito.lenient().when(printMediaService.generatePopupScriptForMedia(any(MediaModel.class), anyString(), anyString(), anyString()))
				.thenReturn("TEST");

		Mockito.lenient().when(configurationService.getConfiguration()).thenReturn(configuration);
		Mockito.lenient().when(configuration.getBoolean(any(), anyBoolean())).thenReturn(true);
	}

	@Test
	public void shouldNotRequireConfirmation()
	{
		assertFalse(printPackLabelAction.needsConfirmation(actionContext));
	}

	@Test
	public void shouldReturnNullConfirmationMessage()
	{
		assertNull(printPackLabelAction.getConfirmationMessage(actionContext));
	}

	@Test
	public void cannotPerform_nullData()
	{
		when(actionContext.getData()).thenReturn(null);
		assertFalse(printPackLabelAction.canPerform(actionContext));
	}

	@Test
	public void cannotPerform_notInstanceOfConsignmentModel()
	{
		when(actionContext.getData()).thenReturn("test");
		assertFalse(printPackLabelAction.canPerform(actionContext));
	}

	@Test
	public void cannotPerform_externalFulFillmentConfiguration()
	{
		when(actionContext.getData()).thenReturn(consignmentModel);
		when(consignmentModel.getFulfillmentSystemConfig()).thenReturn(externalFulfillmentConfiguration);
		assertFalse(printPackLabelAction.canPerform(actionContext));
	}

	@Test
	public void canPerform()
	{
		assertTrue(printPackLabelAction.canPerform(actionContext));
	}

	@Test
	public void shouldPerformPrintAction()
	{
		final ActionResult<ConsignmentModel> result = printPackLabelAction.perform(actionContext);

		assertNotNull(result);
		assertEquals(ActionResult.SUCCESS, result.getResultCode());
		verify(consignmentPrintPackSlipStrategy).printDocument(any(ConsignmentModel.class));
	}
}
