/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.widgets.savedqueries;

import static de.hybris.platform.platformbackoffice.widgets.savedqueries.SaveAdvancedSearchQueryWizardHandler.BACKOFFICE_SAVED_QUERY_MODEL;
import static de.hybris.platform.platformbackoffice.widgets.savedqueries.SaveAdvancedSearchQueryWizardHandler.SAVE_ADVANCED_SEARCH_QUERY_FORM;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import de.hybris.platform.platformbackoffice.model.BackofficeSavedQueryModel;
import de.hybris.platform.platformbackoffice.services.BackofficeSavedQueriesService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Collections;
import java.util.Map;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.hybris.backoffice.widgets.advancedsearch.impl.AdvancedSearchData;
import com.hybris.backoffice.widgets.notificationarea.NotificationService;
import com.hybris.cockpitng.config.jaxb.wizard.CustomType;
import com.hybris.cockpitng.core.events.CockpitEventQueue;
import com.hybris.cockpitng.testing.AbstractWidgetUnitTest;
import com.hybris.cockpitng.widgets.configurableflow.ConfigurableFlowController;
import com.hybris.cockpitng.widgets.configurableflow.FlowActionHandlerAdapter;


public class SaveAdvancedSearchQueryWizardHandlerTest extends AbstractWidgetUnitTest<SaveAdvancedSearchQueryWizardHandler>
{

	@Mock
	private BackofficeSavedQueriesService backofficeSavedQueriesService;
	@Mock
	private UserService userService;
	@Mock
	private CockpitEventQueue cockpitEventQueue;
	@Mock
	private NotificationService notificationService;

	@InjectMocks
	private SaveAdvancedSearchQueryWizardHandler saveAdvancedSearchQueryWizardHandler;


	@Test
	public void shouldSendMapObjectToTheOutputWizardResultSocket()
	{
		//given
		final CustomType customType = Mockito.mock(CustomType.class);
		final Map<String, String> parameters = Mockito.mock(Map.class);
		final FlowActionHandlerAdapter adapter = Mockito.mock(FlowActionHandlerAdapter.class);
		when(adapter.getWidgetInstanceManager()).thenReturn(widgetInstanceManager);

		final SaveAdvancedSearchQueryForm queryForm = Mockito.mock(SaveAdvancedSearchQueryForm.class);
		when(queryForm.getQueryName()).thenReturn(Mockito.mock(Map.class));
		when(queryForm.getAdvancedSearchData()).thenReturn(Mockito.mock(AdvancedSearchData.class));
		when(widgetInstanceManager.getModel().getValue(SAVE_ADVANCED_SEARCH_QUERY_FORM, SaveAdvancedSearchQueryForm.class))
				.thenReturn(queryForm);

		final BackofficeSavedQueryModel backofficeSavedQueryModel = Mockito.mock(BackofficeSavedQueryModel.class);
		when(backofficeSavedQueriesService.createSavedQuery(any(), any(), any(), any())).thenReturn(backofficeSavedQueryModel);

		//then
		saveAdvancedSearchQueryWizardHandler.perform(customType, adapter, parameters);

		//verify
		assertSocketOutput(ConfigurableFlowController.SOCKET_WIZARD_RESULT,
				Collections.singletonMap(BACKOFFICE_SAVED_QUERY_MODEL, backofficeSavedQueryModel));
	}

	@Override
	protected SaveAdvancedSearchQueryWizardHandler getWidgetController()
	{
		return saveAdvancedSearchQueryWizardHandler;
	}
}
