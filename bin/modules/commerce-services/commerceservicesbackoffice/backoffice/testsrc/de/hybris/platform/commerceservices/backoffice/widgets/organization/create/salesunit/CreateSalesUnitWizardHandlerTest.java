/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.commerceservices.backoffice.widgets.organization.create.salesunit;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.model.OrgUnitModel;
import de.hybris.platform.commerceservices.organization.services.OrgUnitParameter;
import de.hybris.platform.commerceservices.organization.services.OrgUnitService;

import java.util.Collections;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.cockpitng.config.jaxb.wizard.CustomType;
import com.hybris.cockpitng.core.impl.NotificationStack;
import com.hybris.cockpitng.core.model.WidgetModel;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.widgets.configurableflow.FlowActionHandlerAdapter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CreateSalesUnitWizardHandlerTest
{
	private static final String NEW_SALES_UNIT_NAME = "newsalesunit";
	private static final String PARENT_UNIT_NAME = "parentUnit";
	@Mock
	private NotificationStack notificationStackMock;
	@Mock
	private OrgUnitService orgUnitServiceMock;

	private CreateSalesUnitWizardHandler handler;

	@Before
	public void setUp()
	{
		handler = new CreateSalesUnitWizardHandler();

		handler.setNotificationStack(notificationStackMock);
		handler.setOrgUnitService(orgUnitServiceMock);
	}

	@Test
	public void testPerformBehavior()
	{
		final CustomType customType = mock(CustomType.class);

		final FlowActionHandlerAdapter adapterMock = mock(FlowActionHandlerAdapter.class);
		final WidgetInstanceManager instanceManagerMock = mock(WidgetInstanceManager.class);
		final WidgetModel widgetModelMock = mock(WidgetModel.class);
		final OrgUnitModel newOrgUnitModelMock = mock(OrgUnitModel.class);

		when(adapterMock.getWidgetInstanceManager()).thenReturn(instanceManagerMock);
		when(instanceManagerMock.getModel()).thenReturn(widgetModelMock);
		when(widgetModelMock.getValue(NEW_SALES_UNIT_NAME, OrgUnitModel.class)).thenReturn(newOrgUnitModelMock);
		when(widgetModelMock.getValue(PARENT_UNIT_NAME, OrgUnitModel.class)).thenReturn(null);

		final OrgUnitModel createdOrgUnit = mock(OrgUnitModel.class);
		when(orgUnitServiceMock.createAndGetUnit(any(OrgUnitParameter.class))).thenReturn(Optional.of(createdOrgUnit));

		handler.perform(customType, adapterMock, Collections.emptyMap());

		verify(widgetModelMock, times(1)).setValue(NEW_SALES_UNIT_NAME, createdOrgUnit);
	}
}
