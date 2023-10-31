/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.widgets.savedqueries;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.platformbackoffice.model.BackofficeSavedQueryModel;
import de.hybris.platform.platformbackoffice.services.BackofficeSavedQueriesService;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.zkoss.zul.Listbox;

import com.hybris.backoffice.widgets.advancedsearch.impl.AdvancedSearchInitContext;
import com.hybris.cockpitng.core.events.CockpitEvent;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectCRUDHandler;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectFacade;
import com.hybris.cockpitng.dataaccess.facades.object.exceptions.ObjectNotFoundException;
import com.hybris.cockpitng.testing.AbstractWidgetUnitTest;
import com.hybris.cockpitng.testing.annotation.DeclaredGlobalCockpitEvent;
import com.hybris.cockpitng.testing.annotation.NullSafeWidget;

import java.util.function.Predicate;


@DeclaredGlobalCockpitEvent(eventName = ObjectCRUDHandler.OBJECT_CREATED_EVENT, scope = CockpitEvent.SESSION)
@DeclaredGlobalCockpitEvent(eventName = ObjectCRUDHandler.OBJECTS_UPDATED_EVENT, scope = CockpitEvent.SESSION)
@DeclaredGlobalCockpitEvent(eventName = ObjectCRUDHandler.OBJECTS_DELETED_EVENT, scope = CockpitEvent.SESSION)
@NullSafeWidget
public class SavedQueriesWidgetControllerTest extends AbstractWidgetUnitTest<SavedQueriesWidgetController>
{
	@Spy
	@InjectMocks
	private SavedQueriesWidgetController controller;

	@Mock
	private BackofficeSavedQueriesService backofficeSavedQueriesService;
	@Mock
	private ObjectFacade objectFacade;
	@Mock
	private Listbox listbox;

	@Test
	public void initCtxWithDisabledSimpleSearch() throws ObjectNotFoundException
	{
		// given
		final BackofficeSavedQueryModel savedQuery = mock(BackofficeSavedQueryModel.class);
		final AdvancedSearchInitContext initCtx = new AdvancedSearchInitContext(null);
		when(backofficeSavedQueriesService.getAdvancedSearchInitContext(savedQuery)).thenReturn(initCtx);
		when(objectFacade.reload(savedQuery)).thenReturn(savedQuery);

		// when
		controller.sendInitContext(savedQuery);

		// then
		assertSocketOutput(SavedQueriesWidgetController.SOCKET_OUT_ADV_SEARCH_INIT_CTX,
				(Predicate<AdvancedSearchInitContext>) (final AdvancedSearchInitContext ctx) -> Boolean.TRUE
						.equals(ctx.getAttribute(AdvancedSearchInitContext.DISABLE_SIMPLE_SEARCH)));
	}

	@Override
	protected SavedQueriesWidgetController getWidgetController()
	{
		return controller;
	}
}
