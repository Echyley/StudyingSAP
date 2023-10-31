/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.hac.controller.console;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.hac.data.dto.SqlSearchResultData;
import de.hybris.platform.hac.data.form.FlexSearchFormData;
import de.hybris.platform.hac.facade.HacFlexibleSearchFacade;
import de.hybris.platform.jalo.flexiblesearch.internal.ReadOnlyConditionsHelper;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.testframework.PropertyConfigSwitcher;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.Model;
import org.springframework.web.server.ResponseStatusException;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class FlexibleSearchControllerTest
{
	private final PropertyConfigSwitcher flexibleSearchReadOnlyDataSource = new PropertyConfigSwitcher(
			ReadOnlyConditionsHelper.PARAM_FS_READ_ONLY_DATASOURCE);

	@Spy
	private HacFlexibleSearchFacade flexibleSearchFacade;

	@Mock
	private I18NService i18nService;

	@Mock
	private UserService userService;

	@InjectMocks
	private FlexibleSearchController flexibleSearchController;

	@Before
	public void setUp() throws Exception
	{
		flexibleSearchReadOnlyDataSource.switchToValue("F");
		when(flexibleSearchFacade.getCompatibleSampleQueries())
				.thenReturn(List.of());
	}

	@Test
	public void testFlexSearchIsReturningReadOnlyDataSource()
	{
		// given
		final FlexSearchFormData form = new FlexSearchFormData();
		given(flexibleSearchFacade.getDefaultDataSource())
				.willReturn("readonly");

		// when
		flexibleSearchController.flexsearch(mock(Model.class),
				form,
				mock(HttpServletRequest.class));

		// then
		assertThat(form.getDataSource()).isEqualTo("readonly");
	}

	@Test
	public void testFlexSearchIsReturningMasterDataSource()
	{
		// given
		final FlexSearchFormData form = new FlexSearchFormData();
		given(flexibleSearchFacade.getDefaultDataSource())
				.willReturn("master");


		// when
		flexibleSearchController.flexsearch(mock(Model.class),
				form,
				mock(HttpServletRequest.class));

		// then
		assertThat(form.getDataSource()).isEqualTo("master");
	}

	@Test
	public void testExecuteFlexSearchWithEmptyDataSource()
	{
		// given
		final FlexSearchFormData data = new FlexSearchFormData();
		data.setDataSource("");
		final SqlSearchResultData sqlSearchResultData = new SqlSearchResultData();
		sqlSearchResultData.setDataSourceId("master");

		given(flexibleSearchFacade.executeFlexibleSearchQuery(
				nullable(String.class),
				nullable(UserModel.class),
				nullable(Locale.class),
				nullable(Integer.class),
				anyBoolean(),
				eq("")))
				.willReturn(sqlSearchResultData);

		// when
		final SqlSearchResultData actual = flexibleSearchController.executeFlexsearch(data);

		// then
		assertThat(actual.getDataSourceId()).isEqualTo("master");
	}

	@Test(expected = ResponseStatusException.class)
	public void testExecuteFlexSearchWithCommitTrueAndReadOnlyDataSource()
	{
		// given
		final FlexSearchFormData data = new FlexSearchFormData();
		data.setFlexibleSearchQuery("nonBlank");
		data.setCommit(true);
		data.setDataSource("F");

		// when
		flexibleSearchController.executeFlexsearch(data);
	}

	@After
	public void tearDown()
	{
		flexibleSearchReadOnlyDataSource.switchBackToDefault();
	}
}