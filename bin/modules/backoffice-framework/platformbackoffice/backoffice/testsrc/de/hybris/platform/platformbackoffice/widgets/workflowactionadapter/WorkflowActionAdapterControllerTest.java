/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.widgets.workflowactionadapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Spy;

import com.hybris.backoffice.navigation.NavigationNode;
import com.hybris.backoffice.widgets.advancedsearch.impl.AdvancedSearchData;
import com.hybris.backoffice.widgets.advancedsearch.impl.SearchConditionData;
import com.hybris.cockpitng.core.config.impl.jaxb.hybris.advancedsearch.AdvancedSearch;
import com.hybris.cockpitng.core.config.impl.jaxb.hybris.advancedsearch.FieldListType;
import com.hybris.cockpitng.core.user.CockpitUserService;
import com.hybris.cockpitng.testing.AbstractWidgetUnitTest;
import com.hybris.cockpitng.testing.annotation.DeclaredInput;
import com.hybris.cockpitng.testing.annotation.ExtensibleWidget;
import com.hybris.cockpitng.testing.annotation.NullSafeWidget;


@DeclaredInput(value = WorkflowActionAdapterController.SOCKET_IN_NODE_SELECTED, socketType = NavigationNode.class)
@DeclaredInput(value = WorkflowActionAdapterController.SOCKET_INPUT_STATUSES, socketType = Set.class)
@ExtensibleWidget(level = ExtensibleWidget.ALL)
@NullSafeWidget
public class WorkflowActionAdapterControllerTest extends AbstractWidgetUnitTest<WorkflowActionAdapterController>
{
	@Spy
	@InjectMocks
	private WorkflowActionAdapterController controller;

	@Mock
	protected CockpitUserService cockpitUserService;
	@Mock
	protected UserService userService;

	@Captor
	protected ArgumentCaptor<List<SearchConditionData>> captor;

	@Override
	protected WorkflowActionAdapterController getWidgetController()
	{
		return controller;
	}

	private static final int USER_GROUPS_COUNT = 4;

	@Before
	public void setUp() throws Exception
	{
		final UserModel userModel = mock(UserModel.class);

		when(cockpitUserService.getCurrentUser()).thenReturn("userId");
		when(userService.getUserForUID(Matchers.anyString())).thenReturn(userModel);

		final Set<UserGroupModel> userGroups = new HashSet<>();
		IntStream.range(0, USER_GROUPS_COUNT).forEach(i -> userGroups.add(mock(UserGroupModel.class)));
		when(userService.getAllUserGroupsForUser(userModel)).thenReturn(userGroups);

		final AdvancedSearch advancedSearch = new AdvancedSearch();
		advancedSearch.setFieldList(new FieldListType());
		when(widgetInstanceManager.loadConfiguration(any(), any())).thenReturn(advancedSearch);
	}

	@Test
	public void shouldAddConditionForUserAndUserGroups()
	{
		// given
		final AdvancedSearchData searchData = mock(AdvancedSearchData.class);

		// when
		controller.addSearchDataConditions(searchData, Optional.empty());

		// then
		verify(searchData).addFilterQueryRawConditionsList(any(), captor.capture());

		final List<SearchConditionData> captured = captor.getValue();
		assertThat(captured.size()).isEqualTo(USER_GROUPS_COUNT + 1);
		IntStream.range(0, USER_GROUPS_COUNT + 1)
				.forEach(i -> assertThat(captured.get(i).getValue()).isInstanceOfAny(UserGroupModel.class, UserModel.class));
	}

}
