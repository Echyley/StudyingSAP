package com.hybris.backoffice.widgets.actions.personalization;

import static com.hybris.backoffice.personalization.PersonalizationConstants.ACTIVE_MOLD_NAME;
import static com.hybris.backoffice.personalization.PersonalizationConstants.COMPONENT;
import static com.hybris.backoffice.personalization.PersonalizationConstants.DATA_TYPE_CODE;
import static com.hybris.backoffice.personalization.PersonalizationConstants.LIST_VIEW_COLUMN_CONFIG;
import static com.hybris.backoffice.personalization.PersonalizationConstants.LIST_VIEW_MOLD_NAME;
import static com.hybris.backoffice.personalization.PersonalizationConstants.SOCKET_OUT_PERSONALIZE_CTX;
import static com.hybris.backoffice.personalization.PersonalizationConstants.TYPE_CODE;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import de.hybris.platform.core.model.product.ProductModel;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.core.config.impl.jaxb.listview.ListView;
import com.hybris.cockpitng.core.model.WidgetModel;
import com.hybris.cockpitng.testing.util.CockpitTestUtil;


@RunWith(MockitoJUnitRunner.class)
public class ListViewPersonalizationActionTest
{
	@Spy
	private ListViewPersonalizationAction action;

	private ActionContext<String> actionContext;

	private WidgetModel widgetModel;

	@Before
	public void setUp()
	{
		actionContext = new ActionContext<>(ProductModel._TYPECODE, null, null, null);
		widgetModel = CockpitTestUtil.mockWidgetModel();
		actionContext.setParameter(ActionContext.PARENT_WIDGET_MODEL, widgetModel);
	}

	@Test
	public void canPerformIfInListViewMode()
	{
		// given
		widgetModel.setValue(ACTIVE_MOLD_NAME, LIST_VIEW_MOLD_NAME);

		// when
		assertThat(action.canPerform(actionContext)).isTrue();
	}

	@Test
	public void canNotPerformIfNotInListViewMode()
	{
		// given
		widgetModel.setValue(ACTIVE_MOLD_NAME, "tree-view");

		// when
		assertThat(action.canPerform(actionContext)).isFalse();
	}

	@Test
	public void shouldSendOutPutWithCorrectData()
	{
		// given
		final var componentId = "list-view";
		final var columnConfig = mock(ListView.class);
		widgetModel.setValue(DATA_TYPE_CODE, ProductModel._TYPECODE);
		widgetModel.setValue(LIST_VIEW_COLUMN_CONFIG, columnConfig);
		actionContext.setParameter(COMPONENT, componentId);
		doNothing().when(action).sendOutput(any(), any());

		// when
		action.perform(actionContext);

		//then
		verify(action).sendOutput(eq(SOCKET_OUT_PERSONALIZE_CTX), argThat(map -> {
			return map instanceof HashMap && componentId.equals(((HashMap) map).get(COMPONENT))
					&& ProductModel._TYPECODE.equals(((HashMap) map).get(TYPE_CODE))
					&& columnConfig.equals(((HashMap) map).get(LIST_VIEW_COLUMN_CONFIG));
		}));
	}
}
